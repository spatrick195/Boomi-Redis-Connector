package nz.co.fmg.redis.Utils;

public class ErrorUtils {
    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void throwException(Throwable exception, Object dummy) throws T {
        throw (T) exception;
    }

    public static void throwException(Throwable exception) {
        ErrorUtils.throwException(exception, null);
    }
}
