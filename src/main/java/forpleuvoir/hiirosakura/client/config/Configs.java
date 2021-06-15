package forpleuvoir.hiirosakura.client.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigDouble;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;
import forpleuvoir.hiirosakura.client.HiiroSakuraClient;

import java.io.File;
import java.sql.Connection;

/**
 * Mod配置
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.config
 * <p>#class_name Configs
 * <p>#create_time 2021/6/15 20:04
 */
public class Configs implements IConfigHandler {

    private static final String CONFIG_FILE_NAME = HiiroSakuraClient.MOD_ID + ".json";

    /**
     * 各种开关
     */
    public static class Toggles {
        public static final ConfigBoolean CHAT_SHOW = new ConfigBoolean(
                "chatShow", false,
                "开启或关闭发送聊天消息时玩家头上文本的渲染"
        );
        public static final ConfigBoolean SHOW_ENCHANTMENT = new ConfigBoolean(
                "showEnchantment", false,
                "开启或关闭切换物品时附魔的显示"
        );
        public static final ConfigBoolean SHOW_TNT_FUSE = new ConfigBoolean(
                "showTNTFuse", false,
                "开启或关闭TNT剩余爆炸时间显示"
        );
        public static final ConfigBoolean SHOW_ITEM_ENTITY_NAME = new ConfigBoolean(
                "showItemEntityName", false,
                "开启或关闭掉落物品的名称渲染"
        );
        public static final ConfigBoolean SHOW_ITEM_ENTITY_ENCHANTMENT = new ConfigBoolean(
                "showItemEntityEnchantment", false,
                "开启或关闭掉落物品渲染时附魔的显示"
        );
        public static final ConfigBoolean SHOW_ITEM_ENTITY_COUNT = new ConfigBoolean(
                "showItemEntityCount", false,
                "开启或关闭掉落物品渲染时数量的显示"
        );
        public static final ConfigBoolean AUTO_REBIRTH = new ConfigBoolean(
                "autoRebirth", false,
                "开启或关闭自动复活"
        );

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                CHAT_SHOW, SHOW_ENCHANTMENT, SHOW_TNT_FUSE, SHOW_ITEM_ENTITY_NAME, SHOW_ITEM_ENTITY_ENCHANTMENT,
                SHOW_ITEM_ENTITY_COUNT,AUTO_REBIRTH
        );

    }

    public static class Values {
        public static final ConfigDouble ITEM_ENTITY_TEXT_RENDER_DISTANCE = new ConfigDouble(
                "itemEntityTextRenderDistance", 50, 0, 999,
                "掉落物在一定距离之内时才会显示"
        );

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                ITEM_ENTITY_TEXT_RENDER_DISTANCE
        );

    }


    @Override
    public void load() {
        File configFile = new File(FileUtils.getConfigDirectory(), CONFIG_FILE_NAME);
        if (configFile.isFile() && configFile.canRead() && configFile.exists()) {
            JsonElement element = JsonUtils.parseJsonFile(configFile);
            if (element != null && element.isJsonObject()) {
                JsonObject root = element.getAsJsonObject();

                ConfigUtils.readConfigBase(root, "Toggles", Toggles.OPTIONS);
                ConfigUtils.readConfigBase(root, "Values", Values.OPTIONS);
            }
        }
    }

    @Override
    public void save() {
        File dir = FileUtils.getConfigDirectory();
        if ((dir.exists() && dir.isDirectory()) || dir.mkdirs()) {
            JsonObject root = new JsonObject();

            ConfigUtils.writeConfigBase(root, "Toggles", Toggles.OPTIONS);
            ConfigUtils.writeConfigBase(root, "Values", Values.OPTIONS);

            JsonUtils.writeJsonToFile(root, new File(dir, CONFIG_FILE_NAME));
        }
    }
}
