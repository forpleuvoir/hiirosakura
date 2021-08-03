package forpleuvoir.hiirosakura.client.util;

import forpleuvoir.hiirosakura.client.HiiroSakuraClient;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

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

    /**
     * 过长的字符串将被省略超过长度的部分
     *
     * @param originalStr 源字符串
     * @param length      最大长度
     * @param suffix      后缀 如...
     * @return 处理之后的字符串
     */
    public static String tooLongOmitted(String originalStr, int length, @Nullable String suffix, boolean keepLastChar) {
        suffix = !isEmpty(suffix) ? suffix : "...";
        if (keepLastChar) {
            suffix = suffix + originalStr.substring(originalStr.length() - 1);
        }
        if (originalStr.length() > length) {
            originalStr = originalStr.substring(0, length) + suffix;
        }
        return originalStr;
    }

    /**
     * 检查字符串是否为空
     *
     * @param str 需要检查的字符串
     * @return 是否为空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static TranslatableText translatableText(String key, Object... params) {
        return new TranslatableText(String.format("%s.%s", HiiroSakuraClient.MOD_ID, key), params);
    }
}
