package cc.ejyf.jfly;


import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

public class Validator {

    public static boolean ensureAllNotNull(Object... objects) {
        for (Object obj : objects) {
            if (obj == null) {
                return false;
            }
        }
        return true;
    }

    public static boolean ensureNotEmpty(Object obj) {
        if (obj != null) {
            if (obj instanceof String) {
                return !((String) obj).isBlank() && !((String) obj).isEmpty();
            }
            if (obj instanceof Collection) {
                return ((Collection) obj).size() != 0;
            }
            if (obj instanceof Map) {
                return ((Map) obj).size() != 0;
            }
            if (obj.getClass().isArray()) {
                return Array.getLength(obj) > 0;
            }
            throw new IllegalArgumentException("不支持的参数");
        } else {
            return false;
        }
    }

    public static boolean ensureAllNotEmpty(Object... objects) {
        for (Object obj : objects) {
            if (!ensureNotEmpty(obj)) {
                return false;
            }
        }
        return true;
    }
}
