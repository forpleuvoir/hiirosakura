package forpleuvoir.hiirosakura.client.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;
import forpleuvoir.hiirosakura.client.HiiroSakuraClient;
import forpleuvoir.hiirosakura.client.config.base.AbstractHiiroSakuraData;
import forpleuvoir.hiirosakura.client.feature.qcms.QuickChatMessageSend;
import forpleuvoir.hiirosakura.client.feature.qcms.QuickChatMessageSort;
import forpleuvoir.hiirosakura.client.feature.regex.ServerChatMessageRegex;
import forpleuvoir.hiirosakura.client.feature.tooltip.Tooltip;

import java.io.File;
import java.util.List;

/**
 * 数据类
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.config
 * <p>#class_name HiiroSakuraDatas
 * <p>#create_time 2021/6/16 22:18
 */
public class HiiroSakuraDatas implements IConfigHandler {
    private static final IConfigHandler configHandler = new HiiroSakuraDatas();
    public static final File CONFIG_FILE_PATH = new File(FileUtils.getConfigDirectory(), HiiroSakuraClient.MOD_ID);
    private static final String CONFIG_FILE_NAME = HiiroSakuraClient.MOD_ID + "_data.json";

    public static final QuickChatMessageSend QUICK_CHAT_MESSAGE_SEND = new QuickChatMessageSend();
    public static final Tooltip TOOLTIP = new Tooltip();
    public static final ServerChatMessageRegex SERVER_CHAT_MESSAGE_REGEX = new ServerChatMessageRegex();
    public static final QuickChatMessageSort QUICK_CHAT_MESSAGE_SORT = new QuickChatMessageSort();

    public static final List<AbstractHiiroSakuraData> DATAS = ImmutableList.of(
            QUICK_CHAT_MESSAGE_SEND, TOOLTIP, SERVER_CHAT_MESSAGE_REGEX,QUICK_CHAT_MESSAGE_SORT
    );

    public static void initialize() {
        configHandler.load();
    }

    @Override
    public void load() {
        File configFile = new File(CONFIG_FILE_PATH, CONFIG_FILE_NAME);
        if (configFile.isFile() && configFile.canRead() && configFile.exists()) {
            JsonElement element = JsonUtils.parseJsonFile(configFile);
            if (element != null && element.isJsonObject()) {
                JsonObject root = element.getAsJsonObject();
                AbstractHiiroSakuraData.readData(root, "DATAS", DATAS);
            }
        }
    }

    @Override
    public void save() {
        File dir = CONFIG_FILE_PATH;
        if ((dir.exists() && dir.isDirectory()) || dir.mkdirs()) {
            JsonObject root = new JsonObject();
            AbstractHiiroSakuraData.writeData(root, "DATAS", DATAS);
            JsonUtils.writeJsonToFile(root, new File(dir, CONFIG_FILE_NAME));
        }
    }

    public static IConfigHandler getConfigHandler() {
        return HiiroSakuraDatas.configHandler;
    }
}
