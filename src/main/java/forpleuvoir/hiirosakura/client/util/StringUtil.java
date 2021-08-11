package forpleuvoir.hiirosakura.client.util;

import forpleuvoir.hiirosakura.client.HiiroSakuraClient;
import forpleuvoir.hiirosakura.client.gui.event.JsTextField;
import forpleuvoir.hiirosakura.client.gui.event.JsTextField.WrappedString;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

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
    public static final char[] FILTER_CHARS = new char[]{'\r', '\f'};
    private static final MinecraftClient minecraft = MinecraftClient.getInstance();
    private static final TextRenderer fontRenderer;

    static {
        fontRenderer = minecraft.textRenderer;
    }

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

    public static String stripInvalidChars(String s) {
        StringBuilder stringBuilder = new StringBuilder();
        char[] var2 = s.toCharArray();
        for (char c : var2) {
            if (isValidChar(c)) {
                stringBuilder.append(c);
            }
        }

        return stringBuilder.toString();
    }

    public static boolean isValidChar(char chr) {
        return chr == '\n' || chr != 167 && chr >= ' ' && chr != 127;
    }

    public static List<StringVisitable> wrapToWidth(String str, int wrapWidth) {
        List<StringVisitable> strings = new ArrayList<>();
        StringBuilder temp = new StringBuilder();

        for (int i = 0; i < str.length(); ++i) {
            char c;
            label19:
            {
                c = str.charAt(i);
                if (c != '\n') {
                    String var10001 = temp.toString();
                    if (fontRenderer.getWidth(var10001 + c) < wrapWidth) {
                        break label19;
                    }
                }

                strings.add(new LiteralText(temp.toString()));
                temp = new StringBuilder();
            }

            if (c != '\n') {
                temp.append(c);
            }
        }

        strings.add(new LiteralText(temp.toString()));
        return strings;
    }

    public static List<WrappedString> wrapToWidthWithIndication(String str, int wrapWidth) {
        List<WrappedString> strings = new ArrayList<>();
        StringBuilder temp = new StringBuilder();
        boolean wrapped = false;

        for (int i = 0; i < str.length(); ++i) {
            char c = str.charAt(i);
            if (c == '\n') {
                strings.add(new WrappedString(temp.toString(), wrapped));
                temp = new StringBuilder();
                wrapped = false;
            } else {
                String var10001 = temp.toString();
                if (fontRenderer.getWidth(var10001 + c) >= wrapWidth) {
                    strings.add(new WrappedString(temp.toString(), wrapped));
                    temp = new StringBuilder();
                    wrapped = true;
                }
            }

            if (c != '\n') {
                temp.append(c);
            }
        }

        strings.add(new WrappedString(temp.toString(), wrapped));
        return strings;
    }

    public static String insertStringAt(String insert, String insertTo, int pos) {
        return insertTo.substring(0, pos) + insert + insertTo.substring(pos);
    }

    public static String filter(String s) {
        String filtered = s.replace(String.valueOf('\t'), "    ");
        for (char c : FILTER_CHARS) {
            filtered = filtered.replace(String.valueOf(c), "");
        }

        return filtered;
    }

}
