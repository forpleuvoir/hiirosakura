package forpleuvoir.hiirosakura.client.util;

import net.minecraft.util.Util;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * 文件工具类
 *
 * @author forpleuvoir
 * #BelongsProject suikamod
 * #BelongsPackage com.forpleuvoir.suika.util
 * #ClassName FileUtil
 * #CreateTime 2020/10/21 9:47
 * #Description 文件工具类
 */
public class FileUtil {
    /**
     * 加载文件中的内容
     *
     * @param path 文件路径
     * @param name 文件名
     * @return 内容
     * @throws Exception {@link FileNotFoundException} 文件未找到
     */
    public static String loadFile(String path, String name) throws Exception {
        File file = new File(path + "/" + name);
        if (!file.exists()) {
            throw new FileNotFoundException("文件未找到:" + path + name);
        }
        StringBuilder result = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        String s;
        while ((s = bufferedReader.readLine()) != null) {
            result.append(System.lineSeparator()).append(s);
        }
        bufferedReader.close();
        return result.toString();
    }

    /**
     * 加载文件中的内容
     *
     * @param file 文件
     * @return 内容
     * @throws IOException {@link IOException} 文件未找到
     */
    public static String readFile(File file) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException("文件未找到:" + file.getName());
        }
        StringBuilder result = new StringBuilder();
        BufferedReader bf = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        String s;
        while ((s = bf.readLine()) != null) {
            result.append(System.lineSeparator()).append(s);
        }
        bf.close();
        return result.toString();
    }

    /**
     * 创建文件
     *
     * @param path 文件路径
     * @param name 文件名
     * @return 成功创建的文件对象
     * @throws IOException 文件创建失败
     */
    public static File createFile(String path, String name) throws IOException {
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        File file = new File(path + "/" + name);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    /**
     * 创建文件
     *
     * @param file 文件路径
     * @return 成功创建的文件对象
     * @throws IOException 文件创建失败
     */
    public static File createFile(File file) throws IOException {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
            if (!file.exists()) {
                file.createNewFile();
            }
        }
        return file;
    }

    /**
     * 将字符串写入文件
     *
     * @param file    文件对象
     * @param content 写入的内容
     * @return 文件对象
     * @throws Exception 文件写入失败
     */
    public static File writeFile(File file, String content) throws Exception {
        try {
            OutputStreamWriter fileWriter = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            fileWriter.write(content);
            fileWriter.close();
            return file;
        } catch (Exception e) {
            return createFile(file);
        }
    }

    /**
     * 打开文件
     * @param file 需要打开的文件
     */
    public static void openFile(File file) {
       if (file.isFile() && file.exists() && file.canRead()) {
            URI uri = file.toURI();
            Util.getOperatingSystem().open(uri);
        }
    }
}
