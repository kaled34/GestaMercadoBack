package mx.gob.chiapas.gestamercado.api.common.exception;

public class ResourceNotFoundException extends RuntimeException {

    private final String resourceName;
    private final Object identifier;

    public ResourceNotFoundException(String message) {
        super(message);
        this.resourceName = null;
        this.identifier = null;
    }

    public ResourceNotFoundException(String resourceName, Object identifier) {
        super(String.format("El recurso %s con identificador '%s' no fue encontrado", resourceName, identifier));
        this.resourceName = resourceName;
        this.identifier = identifier;
    }

    public String getResourceName() {
        return resourceName;
    }

    public Object getIdentifier() {
        return identifier;
    }
}
