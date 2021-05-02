package nz.co.fmg.redis.Utils;

import com.boomi.connector.api.OperationContext;
import com.boomi.connector.api.PropertyMap;
import com.boomi.connector.api.TrackedData;

import java.util.Map;
import java.util.Objects;

public class BoomiUtils {
    /**
     * @param data         The data being passed
     * @param propertyName Specifies the name of the dynamic property
     * @return Returns the value of the {@param propertyName} or null if it is not found
     */
    public static String GetDynamicProperty(TrackedData data, String propertyName) {
        Map<String, String> dynamicProperties = data.getDynamicProperties();
        return dynamicProperties.get(propertyName);
    }

    /**
     * @param data         Specifies the operation context
     * @param propertyName Specifies the name of the property to retrieve
     * @return Returns the boolean value of {@param propertyName} if found
     */
    public static boolean GetOperationBoolProperty(OperationContext data, String propertyName) {
        PropertyMap propertyMap = data.getOperationProperties();
        return Boolean.parseBoolean((propertyMap.getProperty(propertyName)));
    }

    /**
     * @param data The request input
     * @param key  Specifies the name of the dynamic property
     * @return Returns either the prefixed cache key or the key by itself
     */
    public static String GetPrefixedKey(TrackedData data, String key) {
        String cacheKey = GetDynamicProperty(data, key);
        String cacheAtom = GetDynamicProperty(data, "cacheAtom");
        String cachePrefix = GetDynamicProperty(data, "cachePrefix");
        String prefixedKey = String.join(":", cacheAtom, cacheKey);

        boolean isPrefix = StringUtils.isNotEmpty(cachePrefix) && "true".equals(cachePrefix) ? Boolean.TRUE :
                Boolean.FALSE;
        boolean isProductionEnv = Objects.equals(cacheAtom, Constants.PROD_ATOM) || Objects.equals(cacheAtom,
                Constants.PRE_PROD_ATOM);

        if (!isPrefix) {
            return cacheKey;
        } else if (isProductionEnv) {
            return cacheKey;
        } else {
            return prefixedKey;
        }
    }
}
