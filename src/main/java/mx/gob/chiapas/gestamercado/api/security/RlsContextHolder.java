package mx.gob.chiapas.gestamercado.api.security;

import java.util.UUID;

public final class RlsContextHolder {

    private static final ThreadLocal<UUID> EXPLICIT_USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> SYSTEM_CONTEXT = new ThreadLocal<>();

    private RlsContextHolder() {
    }

    public static void setExplicitUserId(UUID userId) {
        EXPLICIT_USER_ID.set(userId);
    }

    public static UUID getExplicitUserId() {
        return EXPLICIT_USER_ID.get();
    }

    public static void setSystemContext(boolean isSystem) {
        SYSTEM_CONTEXT.set(isSystem);
    }

    public static boolean isSystemContext() {
        return Boolean.TRUE.equals(SYSTEM_CONTEXT.get());
    }

    public static void clear() {
        EXPLICIT_USER_ID.remove();
        SYSTEM_CONTEXT.remove();
    }

    public static void runInSystemContext(Runnable runnable, UUID systemUserId) {
        try {
            setSystemContext(true);
            setExplicitUserId(systemUserId);
            runnable.run();
        } finally {
            clear();
        }
    }
}
