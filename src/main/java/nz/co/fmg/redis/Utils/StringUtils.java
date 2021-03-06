package nz.co.fmg.redis.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jacob Hallgarth
 * @since 15/02/2020
 */
public class StringUtils {

    /**
     * @param value The object to be evaluated
     * @return return true if value is null or empty
     */
    public static boolean isEmpty(Object value) {
        return value == null || value.equals("");
    }

    /**
     * @param value The object to be evaluated
     * @return true if the object isn't null or empty and vice versa
     */
    public static boolean isNotEmpty(Object value) {
        return !isEmpty(value);
    }

    /**
     * @param values  Specifies the values to be serialized
     * @param isArray Specifies a JSON array
     * @return Returns the {@param values} into prettified JSON format
     */
    public static String reduceMap(Map<String, String> values, boolean isArray) {
        if (isArray) {
            HashMap<String, Collection<JSONObject>> jsonMap = new HashMap<>();
            ArrayList<JSONObject> jsonValues = new ArrayList<>();
            for (Map.Entry<String, String> entry : values.entrySet()) {
                JSONObject json = new JSONObject(entry.getValue());
                jsonValues.add(json);
            }
            jsonMap.put("Entries", jsonValues);
            return new JSONObject(jsonMap).toString();
        } else {
            HashMap<String, JSONObject> jsonMap = new HashMap<>();
            for (Map.Entry<String, String> entry : values.entrySet()) {
                jsonMap.put(entry.getKey(), new JSONObject(entry.getValue()));
            }
            return new JSONObject(jsonMap).toString();
        }
    }
}
