package forpleuvoir.hiirosakura.client.util;

/**
 * 字符串工具
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.util
 * <p>#class_name StringUtil
 * <p>#create_time 2021/6/13 3:03
 */
public class StringUtil {

    /**
     * 将字符串分割为等长的字符串
     *
     * @param str    原字符串
     * @param length 每一份长度
     * @return 字符串数组
     */
    public static String[] strSplit(String str, int length) {
        int len = str.length();
        String[] arr = new String[(len + length - 1) / length];
        for (int i = 0; i < len; i += length) {
            int n = len - i;
            if (n > length)
                n = length;
            arr[i / length] = str.substring(i, i + n);
        }
        return arr;
    }

    public static boolean isEmpty(String regex) {
        return regex == null || regex.isEmpty();
    }
}
