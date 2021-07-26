package forpleuvoir.hiirosakura.client.util;


/**
 * 服务器信息工具类
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.util
 * <p>#class_name ServerInfoUtil
 * <p>#create_time 2021-07-26 15:41
 */
public class ServerInfoUtil {
    private static String name;
    private static String address;

    public static void clear() {
        ServerInfoUtil.name = null;
        ServerInfoUtil.address = null;
    }

    public static void setValue(String name, String address) {
        ServerInfoUtil.name = name;
        ServerInfoUtil.address = address;
    }

    public static String getName() {
        return name;
    }

    public static String getAddress() {
        return address;
    }
}
