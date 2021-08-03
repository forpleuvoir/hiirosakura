package forpleuvoir.hiirosakura.client.feature.task.executor;

import forpleuvoir.hiirosakura.client.HiiroSakuraClient;
import forpleuvoir.hiirosakura.client.config.Configs;
import forpleuvoir.hiirosakura.client.util.FileUtil;
import forpleuvoir.hiirosakura.client.util.HSLogger;
import forpleuvoir.hiirosakura.client.util.StringUtil;
import org.apache.commons.io.FileUtils;

import java.io.*;

/**
 * javascript头文件
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.feature.task.executor
 * <p>#class_name JSHeadFile
 * <p>#create_time 2021/8/3 22:12
 */
public class JSHeadFile {
    private transient static final HSLogger log = HSLogger.getLogger(JSHeadFile.class);

    private static final String DEFAULT_SCRIPT = """
            var $TimeTask = Java.type('forpleuvoir.hiirosakura.client.feature.task.TimeTask');
            var $TimeTaskData = Java.type('forpleuvoir.hiirosakura.client.feature.task.TimeTaskData');
            var $TimeTaskHandler = Java.type('forpleuvoir.hiirosakura.client.feature.task.TimeTaskHandler').getInstance();
            function $sendMessage(message){$hs.sendChatMessage(message);}
            function $attack(){$hs.doAttack();}
            function $use(){$hs.doItemUse();}
            function $pick(){$hs.doItemPick();}
            function $sneak(tick){$hs.sneak(tick)}
            function $jump(tick){$hs.jump(tick)}
            function $move(dir,tick){
                switch(dir){
                    case 'forward':$hs.forward(tick);break;
                    case 'back':$hs.back(tick);break;
                    case 'left':$hs.left(tick);break;
                    case 'right':$hs.right(tick);break;
                }
            }
            function $joinServer(address,maxConnect){$hs.joinServer(address,maxConnect);}
            function $addTask(executor,data){
                $TimeTaskHandler.addTask(new $TimeTask(executor,data));
            }
            function $getTaskData(name,startTime,cycles,cyclesTime){
                return new $TimeTaskData(name,startTime,cycles,cyclesTime);
            }
            """;

    private static final File PATH = Configs.CONFIG_FILE_PATH;
    private static final File HEAD_FILE = new File(PATH, HiiroSakuraClient.MOD_ID + "_head.js");
    private static String content;

    public static void initialize() {
        if (!HEAD_FILE.exists()) {
            createFile();
        }
        read();
    }

    public static void openFile() {
        try {
            FileUtil.openFile(HEAD_FILE);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    public static void createFile() {
        try {
            FileUtil.createFile(HEAD_FILE);
            FileUtil.writeFile(HEAD_FILE, DEFAULT_SCRIPT);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public static boolean read() {
        if (HEAD_FILE.isFile() && HEAD_FILE.canRead() && HEAD_FILE.exists()) {
            try {
                content = FileUtil.readFile(HEAD_FILE);
                return true;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return false;
    }

    public static String getContent() {
        return StringUtil.isEmpty(content) ? DEFAULT_SCRIPT : content;
    }
}
