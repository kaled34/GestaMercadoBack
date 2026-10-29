package mx.gob.chiapas.gestamercado.api.common.exception;

import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessRuleException.class)
    public ProblemDetail handleBusinessRule(BusinessRuleException ex) {
        log.warn("Regla de negocio no satisfecha: {} (Código: {})", ex.getMessage(), ex.getRuleCode());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problem.setTitle("Violación de Regla de Negocio");
        problem.setType(URI.create("urn:gestamercado:error:business-rule"));
        problem.setProperty("timestamp", Instant.now());
        if (ex.getRuleCode() != null) {
            problem.setProperty("ruleCode", ex.getRuleCode());
        }
        return problem;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleNotFound(ResourceNotFoundException ex) {
        log.warn("Recurso no encontrado: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Recurso No Encontrado");
        problem.setType(URI.create("urn:gestamercado:error:not-found"));
        problem.setProperty("timestamp", Instant.now());
        if (ex.getResourceName() != null) {
            problem.setProperty("resourceName", ex.getResourceName());
            problem.setProperty("identifier", ex.getIdentifier());
        }
        return problem;
    }

    @ExceptionHandler(ConflictException.class)
    public ProblemDetail handleConflict(ConflictException ex) {
        log.warn("Conflicto de recursos: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Conflicto de Datos");
        problem.setType(URI.create("urn:gestamercado:error:conflict"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ProblemDetail handleUnauthorized(UnauthorizedException ex) {
        log.warn("Acceso no autorizado: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        problem.setTitle("No Autorizado");
        problem.setType(URI.create("urn:gestamercado:error:unauthorized"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentials(BadCredentialsException ex) {
        log.warn("Credenciales inválidas: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Credenciales de acceso incorrectas o cuenta no autorizada");
        problem.setTitle("Credenciales Inválidas");
        problem.setType(URI.create("urn:gestamercado:error:bad-credentials"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthenticationException(AuthenticationException ex) {
        log.warn("Fallo de autenticación: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        problem.setTitle("Error de Autenticación");
        problem.setType(URI.create("urn:gestamercado:error:unauthenticated"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler({ForbiddenException.class, AccessDeniedException.class})
    public ProblemDetail handleAccessDenied(Exception ex) {
        log.warn("Acceso denegado: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "No cuenta con los privilegios o el alcance necesario para realizar esta operación");
        problem.setTitle("Acceso Prohibido");
        problem.setType(URI.create("urn:gestamercado:error:forbidden"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        log.warn("Validación de argumentos fallida: {} errores", ex.getBindingResult().getErrorCount());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Los datos enviados contienen errores de validación");
        problem.setTitle("Error de Validación");
        problem.setType(URI.create("urn:gestamercado:error:validation"));
        problem.setProperty("timestamp", Instant.now());

        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        problem.setProperty("fieldErrors", fieldErrors);
        return problem;
    }

    @ExceptionHandler(DataAccessException.class)
    public ProblemDetail handleDataAccessException(DataAccessException ex) {
        Throwable cause = ex.getCause();
        while (cause != null && !(cause instanceof PSQLException)) {
            cause = cause.getCause();
        }

        if (cause instanceof PSQLException psqlEx) {
            return handlePSQLException(psqlEx);
        }

        log.error("Error de acceso a datos no catalogado", ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Error inesperado al ejecutar la operación en base de datos");
        problem.setTitle("Error de Base de Datos");
        problem.setType(URI.create("urn:gestamercado:error:database"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(PSQLException.class)
    public ProblemDetail handlePSQLException(PSQLException ex) {
        String sqlState = ex.getSQLState();
        ServerErrorMessage serverError = ex.getServerErrorMessage();
        String message = (serverError != null && serverError.getMessage() != null)
                ? serverError.getMessage()
                : cleanPostgresMessage(ex.getMessage());

        log.warn("Excepción PostgreSQL capturada [SQLState={}]: {}", sqlState, message);

        if ("P0001".equals(sqlState)) {
            // RAISE EXCEPTION emitido desde funciones PL/pgSQL o triggers del esquema gesta
            ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, message);
            problem.setTitle("Regla de Negocio de Base de Datos");
            problem.setType(URI.create("urn:gestamercado:error:pg-rule-violation"));
            problem.setProperty("sqlState", sqlState);
            problem.setProperty("timestamp", Instant.now());
            if (serverError != null && serverError.getDetail() != null) {
                problem.setProperty("detailMessage", serverError.getDetail());
            }
            return problem;
        }

        if ("23505".equals(sqlState)) {
            // Violación de unicidad (unique_violation)
            ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "Ya existe un registro con los mismos datos únicos: " + message);
            problem.setTitle("Violación de Unicidad");
            problem.setType(URI.create("urn:gestamercado:error:unique-violation"));
            problem.setProperty("sqlState", sqlState);
            problem.setProperty("timestamp", Instant.now());
            return problem;
        }

        if ("23503".equals(sqlState)) {
            // Violación de llave foránea (foreign_key_violation)
            ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Referencia foránea inválida o registro padre inexistente");
            problem.setTitle("Error de Integridad Referencial");
            problem.setType(URI.create("urn:gestamercado:error:fk-violation"));
            problem.setProperty("sqlState", sqlState);
            problem.setProperty("timestamp", Instant.now());
            return problem;
        }

        if ("23514".equals(sqlState)) {
            // Violación de restricción CHECK (check_violation)
            ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, "Los datos infringen una restricción de integridad del esquema: " + message);
            problem.setTitle("Violación de Restricción CHECK");
            problem.setType(URI.create("urn:gestamercado:error:check-violation"));
            problem.setProperty("sqlState", sqlState);
            problem.setProperty("timestamp", Instant.now());
            return problem;
        }

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Error de persistencia: " + message);
        problem.setTitle("Error de Base de Datos");
        problem.setType(URI.create("urn:gestamercado:error:database-internal"));
        problem.setProperty("sqlState", sqlState);
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneralException(Exception ex) {
        log.error("Excepción no controlada en el controlador REST", ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error interno no previsto en el servidor");
        problem.setTitle("Error Interno del Servidor");
        problem.setType(URI.create("urn:gestamercado:error:internal"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    private String cleanPostgresMessage(String rawMessage) {
        if (rawMessage == null) return "Error desconocido en PostgreSQL";
        String cleaned = rawMessage;
        if (cleaned.startsWith("ERROR: ")) {
            cleaned = cleaned.substring(7);
        }
        int whereIndex = cleaned.indexOf("\n  Where:");
        if (whereIndex != -1) {
            cleaned = cleaned.substring(0, whereIndex);
        }
        return cleaned.trim();
    }
}
