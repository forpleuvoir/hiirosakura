package forpleuvoir.hiirosakura.client.util;


import org.jetbrains.annotations.Nullable;

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
    @Nullable
    private static String name;
    @Nullable
    private static String address;
    @Nullable
    private static String lastServerName;
    @Nullable
    private static String lastServerAddress;
    private static int disConnectCounter = 0;

    public static void clear() {
        ServerInfoUtil.name = null;
        ServerInfoUtil.address = null;
    }

    public static void setValue(String name, String address) {
        ServerInfoUtil.name = name;
        ServerInfoUtil.address = address;
        ServerInfoUtil.lastServerName = name;
        ServerInfoUtil.lastServerAddress = address;
        ServerInfoUtil.disConnectCounter = 0;
    }

    public static void disconnect() {
        disConnectCounter++;
    }

    public static int getDisConnectCounter() {
        return disConnectCounter;
    }

    @Nullable
    public static String getName() {
        return name;
    }

    @Nullable
    public static String getAddress() {
        return address;
    }

    @Nullable
    public static String getLastServerName() {
        return lastServerName;
    }

    @Nullable
    public static String getLastServerAddress() {
        return lastServerAddress;
    }
}
