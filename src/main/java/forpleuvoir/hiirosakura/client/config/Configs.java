package forpleuvoir.hiirosakura.client.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.options.*;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;
import forpleuvoir.hiirosakura.client.HiiroSakuraClient;

import java.io.File;

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
    private final static IConfigHandler configHandler = new Configs();
    public static final File CONFIG_FILE_PATH = new File(FileUtils.getConfigDirectory(), HiiroSakuraClient.MOD_ID);
    private static final String CONFIG_FILE_NAME = HiiroSakuraClient.MOD_ID + "_config.json";

    /**
     * 各种开关
     */
    public static class Toggles {
        public static final ConfigBoolean CHAT_SHOW = new ConfigBoolean(
                translationKey("chatShow"), false,
                translationKey("chatShow.comment")
        );
        public static final ConfigBoolean SHOW_ENCHANTMENT = new ConfigBoolean(
                translationKey("showEnchantment"), false,
                translationKey("showEnchantment.comment")
        );
        public static final ConfigBoolean SHOW_TNT_FUSE = new ConfigBoolean(
                translationKey("showTNTFuse"), false,
                translationKey("showTNTFuse.comment")
        );
        public static final ConfigBoolean SHOW_ITEM_ENTITY_NAME = new ConfigBoolean(
                translationKey("showItemEntityName"), false,
                translationKey("showItemEntityName.comment")
        );
        public static final ConfigBoolean SHOW_ITEM_ENTITY_ENCHANTMENT = new ConfigBoolean(
                translationKey("showItemEntityEnchantment"), false,
                translationKey("showItemEntityEnchantment.comment")
        );
        public static final ConfigBoolean SHOW_ITEM_ENTITY_COUNT = new ConfigBoolean(
                translationKey("showItemEntityCount"), false,
                translationKey("showItemEntityCount.comment")
        );
        public static final ConfigBoolean SHOW_ENTITY_AGE = new ConfigBoolean(
                translationKey("showEntityAge"), false,
                translationKey("showEntityAge.comment")
        );
        public static final ConfigBoolean AUTO_REBIRTH = new ConfigBoolean(
                translationKey("autoRebirth"), false,
                translationKey("autoRebirth.comment")
        );
        public static final ConfigBoolean SHOW_TOOLTIP = new ConfigBoolean(
                translationKey("showTooltip"), false,
                translationKey("showTooltip.comment")
        );
        public static final ConfigBoolean SHOW_TOOLTIP_ON_ITEM_TOGGLE = new ConfigBoolean(
                translationKey("showTooltipOnItemToggle"), false,
                translationKey("showTooltipOnItemToggle.comment")
        );
        public static final ConfigBoolean SHOW_TOOLTIP_ON_ITEM_ENTITY = new ConfigBoolean(
                translationKey("showTooltipOnItemEntity"), false,
                translationKey("showTooltipOnItemEntity.comment")
        );
        public static final ConfigBoolean ENABLE_QCMS_GUI = new ConfigBoolean(
                translationKey("enableQcmsGui"), false,
                translationKey("enableQcmsGui.comment")
        );
        public static final ConfigBoolean CHAT_MESSAGE_INJECT = new ConfigBoolean(
                translationKey("chatMessageInject"), false,
                translationKey("chatMessageInject.comment")
        );
        public static final ConfigBoolean ENABLE_CHAT_MESSAGE_INJECT_REGEX = new ConfigBoolean(
                translationKey("enableChatMessageInjectRegex"), false,
                translationKey("enableChatMessageInjectRegex.comment")
        );
        public static final ConfigBoolean REVERSE_CHAT_MESSAGE_INJECT_REGEX = new ConfigBoolean(
                translationKey("reverseChatMessageInjectRegex"), false,
                translationKey("reverseChatMessageInjectRegex.comment")
        );
        public static final ConfigBoolean DISABLE_SCOREBOARD_SIDEBAR_RENDER = new ConfigBoolean(
                translationKey("disableScoreboardSidebarRender"), false,
                translationKey("disableScoreboardSidebarRender.comment")
        );
        public static final ConfigBoolean CHAT_MESSAGE_FILTER = new ConfigBoolean(
                translationKey("chatMessageFilter"), false,
                translationKey("chatMessageFilter.comment")
        );
        public static final ConfigBoolean DISABLE_BLOCK_INTERACTION = new ConfigBoolean(
                translationKey("disableBlockInteraction"), false,
                translationKey("disableBlockInteraction.comment")
        );
        public static final ConfigBoolean EXPERIENCE_ORB_ENTITY_VALUE_RENDER = new ConfigBoolean(
                translationKey("experienceOrbEntityValueRender"), false,
                translationKey("experienceOrbEntityValueRender.comment")
        );


        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                CHAT_SHOW, SHOW_ENCHANTMENT, SHOW_TNT_FUSE, SHOW_ITEM_ENTITY_NAME,
                SHOW_ITEM_ENTITY_ENCHANTMENT, SHOW_ITEM_ENTITY_COUNT, SHOW_ENTITY_AGE, AUTO_REBIRTH, SHOW_TOOLTIP,
                SHOW_TOOLTIP_ON_ITEM_TOGGLE, SHOW_TOOLTIP_ON_ITEM_ENTITY, ENABLE_QCMS_GUI, CHAT_MESSAGE_INJECT,
                ENABLE_CHAT_MESSAGE_INJECT_REGEX, REVERSE_CHAT_MESSAGE_INJECT_REGEX, DISABLE_SCOREBOARD_SIDEBAR_RENDER,
                CHAT_MESSAGE_FILTER, DISABLE_BLOCK_INTERACTION, EXPERIENCE_ORB_ENTITY_VALUE_RENDER
        );


        public static String translationKey(String key) {
            return Configs.translationKey("toggles", key);
        }
    }


    public static class Values {
        public static final ConfigDouble ITEM_ENTITY_TEXT_RENDER_DISTANCE = new ConfigDouble(
                translationKey("itemEntityTextRenderDistance"), 50, 0, 999,
                translationKey("itemEntityTextRenderDistance.comment")
        );
        public static final ConfigDouble CHAT_SHOW_HEIGHT = new ConfigDouble(
                translationKey("chatShowHeight"), 1D, 0, 10D,
                translationKey("chatShowHeight.comment")
        );
        public static final ConfigColor CHAT_SHOW_TEXT_COLOR = new ConfigColor(
                translationKey("chatShowTextColor"), "#AFFFFFFF",
                translationKey("chatShowTextColor.comment")
        );
        public static final ConfigInteger CHAT_SHOW_TEXT_MAX_WIDTH = new ConfigInteger(
                translationKey("chatShowTextMaxWidth"), 96, 9, 480,
                translationKey("chatShowTextMaxWidth.comment")
        );
        public static final ConfigInteger CHAT_SHOW_TIME = new ConfigInteger(
                translationKey("chatShowTime"), 200, 0, 9999,
                translationKey("chatShowTime.comment")
        );
        public static final ConfigDouble CHAT_SHOW_SCALE = new ConfigDouble(
                translationKey("chatShowScale"), 1D, 0.1D, 10D,
                translationKey("chatShowScale.comment")
        );
        public static final ConfigString CHAT_MESSAGE_DEFAULT_REGEX = new ConfigString(
                translationKey("chatMessageDefaultRegex"), "(<(?<name>(.*))>)\\s(?<message>.*)",
                translationKey("chatMessageDefaultRegex.comment")
        );
        public static final ConfigString CHAT_MESSAGE_INJECT_PREFIX = new ConfigString(
                translationKey("chatMessageInjectPrefix"), "",
                translationKey("chatMessageInjectPrefix.comment")
        );
        public static final ConfigString CHAT_MESSAGE_INJECT_SUFFIX = new ConfigString(
                translationKey("chatMessageInjectSuffix"), "",
                translationKey("chatMessageInjectSuffix.comment")
        );
        public static final ConfigStringList CHAT_MESSAGE_INJECT_REGEX = new ConfigStringList(
                translationKey("chatMessageInjectRegex"), ImmutableList.of("^\\/"),
                translationKey("chatMessageInjectRegex.comment")
        );
        public static final ConfigStringList CHAT_MESSAGE_FILTER_REGEX = new ConfigStringList(
                translationKey("chatMessageFilterRegex"), ImmutableList.of(""),
                translationKey("chatMessageFilterRegex.comment")
        );

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                ITEM_ENTITY_TEXT_RENDER_DISTANCE, CHAT_SHOW_HEIGHT, CHAT_SHOW_TEXT_COLOR, CHAT_SHOW_TEXT_MAX_WIDTH,
                CHAT_SHOW_TIME, CHAT_SHOW_SCALE, CHAT_MESSAGE_DEFAULT_REGEX, CHAT_MESSAGE_INJECT_PREFIX,
                CHAT_MESSAGE_INJECT_SUFFIX, CHAT_MESSAGE_INJECT_REGEX, CHAT_MESSAGE_FILTER_REGEX
        );

        public static String translationKey(String key) {
            return Configs.translationKey("values", key);
        }
    }

    public static String translationKey(String type, String key) {
        return String.format("%s.config.%s.%s", HiiroSakuraClient.MOD_ID, type, key);
    }

    @Override
    public void load() {
        File configFile = new File(CONFIG_FILE_PATH, CONFIG_FILE_NAME);
        if (configFile.isFile() && configFile.canRead() && configFile.exists()) {
            JsonElement element = JsonUtils.parseJsonFile(configFile);
            if (element != null && element.isJsonObject()) {
                JsonObject root = element.getAsJsonObject();

                ConfigUtils.readConfigBase(root, "Toggles", Toggles.OPTIONS);
                ConfigUtils.readConfigBase(root, "Values", Values.OPTIONS);
                ConfigUtils.readConfigBase(root, "Hotkeys", HotKeys.HOTKEY_LIST);
                ConfigUtils.readConfigBase(root, "TogglesHotKeys", TogglesHotKeys.HOTKEY_LIST);
            }
        }
    }

    @Override
    public void save() {
        File dir = CONFIG_FILE_PATH;
        if ((dir.exists() && dir.isDirectory()) || dir.mkdirs()) {
            JsonObject root = new JsonObject();

            ConfigUtils.writeConfigBase(root, "Toggles", Toggles.OPTIONS);
            ConfigUtils.writeConfigBase(root, "Values", Values.OPTIONS);
            ConfigUtils.writeConfigBase(root, "Hotkeys", HotKeys.HOTKEY_LIST);
            ConfigUtils.writeConfigBase(root, "TogglesHotKeys", TogglesHotKeys.HOTKEY_LIST);

            JsonUtils.writeJsonToFile(root, new File(dir, CONFIG_FILE_NAME));
        }
    }

    public static IConfigHandler getConfigHandler() {
        return Configs.configHandler;
    }

}
