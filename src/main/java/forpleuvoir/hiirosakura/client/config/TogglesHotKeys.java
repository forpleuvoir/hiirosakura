package forpleuvoir.hiirosakura.client.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import forpleuvoir.hiirosakura.client.HiiroSakuraClient;
import forpleuvoir.hiirosakura.client.config.base.ToggleHotkey;
import net.minecraft.client.MinecraftClient;

import java.util.List;

import static forpleuvoir.hiirosakura.client.config.Configs.Toggles.*;

/**
 * 开关热键
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.config
 * <p>#class_name TogglesHotKeys
 * <p>#create_time 2021-07-27 16:17
 */
public class TogglesHotKeys {

    public static final ToggleHotkey CHAT_SHOW_HOTKEY = new ToggleHotkey(
            translationKey("chatShow"), "",
            translationKey("chatShow.comment"), CHAT_SHOW
    );
    public static final ToggleHotkey SHOW_ENCHANTMENT_HOTKEY = new ToggleHotkey(
            translationKey("showEnchantment"), "",
            translationKey("showEnchantment.comment"), SHOW_ENCHANTMENT
    );
    public static final ToggleHotkey SHOW_TNT_FUSE_HOTKEY = new ToggleHotkey(
            translationKey("showTNTFuse"), "",
            translationKey("showTNTFuse.comment"), SHOW_TNT_FUSE
    );
    public static final ToggleHotkey SHOW_ITEM_ENTITY_NAME_HOTKEY = new ToggleHotkey(
            translationKey("showItemEntityName"), "",
            translationKey("showItemEntityName.comment"), SHOW_ITEM_ENTITY_NAME
    );
    public static final ToggleHotkey SHOW_ITEM_ENTITY_ENCHANTMENT_HOTKEY = new ToggleHotkey(
            translationKey("showItemEntityEnchantment"), "",
            translationKey("showItemEntityEnchantment.comment"), SHOW_ITEM_ENTITY_ENCHANTMENT
    );
    public static final ToggleHotkey SHOW_ITEM_ENTITY_COUNT_HOTKEY = new ToggleHotkey(
            translationKey("showItemEntityCount"), "",
            translationKey("showItemEntityCount.comment"), SHOW_ITEM_ENTITY_COUNT
    );
    public static final ToggleHotkey AUTO_REBIRTH_HOTKEY = new ToggleHotkey(
            translationKey("autoRebirth"), "",
            translationKey("autoRebirth.comment"), AUTO_REBIRTH
    );
    public static final ToggleHotkey SHOW_TOOLTIP_HOTKEY = new ToggleHotkey(
            translationKey("showTooltip"), "",
            translationKey("showTooltip.comment"), SHOW_TOOLTIP
    );
    public static final ToggleHotkey SHOW_TOOLTIP_ON_ITEM_TOGGLE_HOTKEY = new ToggleHotkey(
            translationKey("showTooltipOnItemToggle"), "",
            translationKey("showTooltipOnItemToggle.comment"), SHOW_TOOLTIP_ON_ITEM_TOGGLE
    );
    public static final ToggleHotkey SHOW_TOOLTIP_ON_ITEM_ENTITY_HOTKEY = new ToggleHotkey(
            translationKey("showTooltipOnItemEntity"), "",
            translationKey("showTooltipOnItemEntity.comment"), SHOW_TOOLTIP_ON_ITEM_ENTITY
    );
    public static final ToggleHotkey ENABLE_QCMS_GUI_HOTKEY = new ToggleHotkey(
            translationKey("enableQcmsGui"), "",
            translationKey("enableQcmsGui.comment"), ENABLE_QCMS_GUI
    );
    public static final ToggleHotkey CHAT_MESSAGE_INJECT_HOTKEY = new ToggleHotkey(
            translationKey("chatMessageInject"), "",
            translationKey("chatMessageInject.comment"), CHAT_MESSAGE_INJECT
    );
    public static final ToggleHotkey ENABLE_CHAT_MESSAGE_INJECT_REGEX_HOTKEY = new ToggleHotkey(
            translationKey("enableChatMessageInjectRegex"), "",
            translationKey("enableChatMessageInjectRegex.comment"), ENABLE_CHAT_MESSAGE_INJECT_REGEX
    );
    public static final ToggleHotkey REVERSE_CHAT_MESSAGE_INJECT_REGEX_HOTKEY = new ToggleHotkey(
            translationKey("reverseChatMessageInjectRegex"), "",
            translationKey("reverseChatMessageInjectRegex.comment"), REVERSE_CHAT_MESSAGE_INJECT_REGEX
    );
    public static final ToggleHotkey DISABLE_SCOREBOARD_SIDEBAR_RENDER_HOTKEY = new ToggleHotkey(
            translationKey("disableScoreboardSidebarRender"), "",
            translationKey("disableScoreboardSidebarRender.comment"), DISABLE_SCOREBOARD_SIDEBAR_RENDER
    );
    public static final ToggleHotkey CHAT_MESSAGE_FILTER_HOTKEY = new ToggleHotkey(
            translationKey("chatMessageFilter"), "",
            translationKey("chatMessageFilter.comment"), CHAT_MESSAGE_FILTER
    );
    public static final ToggleHotkey DISABLE_BLOCK_INTERACTION_HOTKEY = new ToggleHotkey(
            translationKey("disableBlockInteraction"), "",
            translationKey("disableBlockInteraction.comment"), DISABLE_BLOCK_INTERACTION
    );

    public static final List<ToggleHotkey> HOTKEY_LIST = ImmutableList.of(
            CHAT_SHOW_HOTKEY, SHOW_ENCHANTMENT_HOTKEY, SHOW_TNT_FUSE_HOTKEY, SHOW_ITEM_ENTITY_NAME_HOTKEY,
            SHOW_ITEM_ENTITY_ENCHANTMENT_HOTKEY, SHOW_ITEM_ENTITY_COUNT_HOTKEY, AUTO_REBIRTH_HOTKEY,
            SHOW_TOOLTIP_HOTKEY, SHOW_TOOLTIP_ON_ITEM_TOGGLE_HOTKEY, SHOW_TOOLTIP_ON_ITEM_ENTITY_HOTKEY,
            ENABLE_QCMS_GUI_HOTKEY, CHAT_MESSAGE_INJECT_HOTKEY, ENABLE_CHAT_MESSAGE_INJECT_REGEX_HOTKEY,
            REVERSE_CHAT_MESSAGE_INJECT_REGEX_HOTKEY, DISABLE_SCOREBOARD_SIDEBAR_RENDER_HOTKEY,
            CHAT_MESSAGE_FILTER_HOTKEY, DISABLE_BLOCK_INTERACTION_HOTKEY
    );


    public static void initCallback(HiiroSakuraClient hs) {
        for (ToggleHotkey hotkey : HOTKEY_LIST) {
            hotkey.initCallback(hs);
        }
    }

    public static String translationKey(String key) {
        return Configs.translationKey("toggles.hotkeys", key);
    }
}
