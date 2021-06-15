package forpleuvoir.hiirosakura.client.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Json工具
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.util
 * <p>#class_name JsonUtil
 * <p>#create_time 2021/6/14 23:52
 */
public class JsonUtil {
    public static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static StringBuffer toStringBuffer(Object obj) {
        return new StringBuffer(gson.toJson(obj));
    }

    /**
     * 将对象转换成json字符串
     *
     * @param json 需要转换的对象
     * @return json字符串
     */
    public static String toJsonStr(Object json) {
        return gson.toJson(json);
    }


    /**
     * 将json字符串转换成JsonObject
     *
     * @param json 需要转换的字符串对象
     * @return JsonObject对象
     * @throws Exception
     */
    public static JsonObject strToJsonObject(String json) {
        return new JsonParser().parse(json).getAsJsonObject();
    }

}
