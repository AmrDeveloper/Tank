package modules.parsers;

import modules.Library;
import modules.LibraryFunction;
import org.json.JSONArray;
import org.json.JSONObject;

@Library
public class json {

    @LibraryFunction
    public JSONObject jsonObject(String source) {
        return new JSONObject(source);
    }

    @LibraryFunction
    public JSONArray jsonArray(String source) {
        return new JSONArray(source);
    }

    @LibraryFunction
    public JSONObject jsonArrayObject(JSONArray jsonArray, Double index) {
        return jsonArray.getJSONObject(index.intValue());
    }

    @LibraryFunction
    public JSONArray jsonObjectArray(JSONObject jsonObject, String key) {
        return jsonObject.getJSONArray(key);
    }

    @LibraryFunction
    public Integer jsonArrayLength(JSONArray jsonArray) {
        return jsonArray.length();
    }

    @LibraryFunction
    public Object jsonObjectValue(JSONObject jsonObject, String key) {
        return jsonObject.get(key);
    }
}
