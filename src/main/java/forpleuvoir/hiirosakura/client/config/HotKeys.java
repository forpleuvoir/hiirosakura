package forpleuvoir.hiirosakura.client.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.util.InfoUtils;
import forpleuvoir.hiirosakura.client.feature.cameraentity.SwitchCameraEntity;
import forpleuvoir.hiirosakura.client.gui.GuiConfig;
import forpleuvoir.hiirosakura.client.gui.QCMSScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.MessageType;
import net.minecraft.util.Util;

import java.util.List;

/**
 * 热键配置
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.config
 * <p>#class_name HotKeys
 * <p>#create_time 2021/6/15 23:28
 */
public class HotKeys {
    public static final ConfigHotkey OPEN_CONFIG_GUI = new ConfigHotkey(
            translationKey("openConfig"), "H,S",
            translationKey("openConfig.comment")
    );
    public static final ConfigHotkey OPEN_QCMS = new ConfigHotkey(
            translationKey("openQcms"), "B",
            translationKey("openQcms.comment")
    );
    public static final ConfigHotkey SWITCH_CAMERA_ENTITY = new ConfigHotkey(
            translationKey("switchCameraEntity"), "V",
            translationKey("switchCameraEntity.comment")
    );

    public static final List<ConfigHotkey> HOTKEY_LIST = ImmutableList.of(
            OPEN_CONFIG_GUI, OPEN_QCMS,SWITCH_CAMERA_ENTITY
    );

    public static String translationKey(String key) {
        return Configs.translationKey("hotkeys", key);
    }

    public static void callback(MinecraftClient client) {
        OPEN_CONFIG_GUI.getKeybind().setCallback((action, key) -> {
            GuiBase.openGui(new GuiConfig());
            return true;
        });
        OPEN_QCMS.getKeybind().setCallback((action, key) -> {
            if (Configs.Toggles.ENABLE_QCMS_GUI.getBooleanValue()) {
                GuiBase.openGui(new QCMSScreen(false));
            } else {
                client.inGameHud.addChatMessage(MessageType.SYSTEM, HiiroSakuraDatas.QUICK_CHAT_MESSAGE_SEND.getAsText(), Util.NIL_UUID);
            }
            return true;
        });
        SWITCH_CAMERA_ENTITY.getKeybind().setCallback((action, key) -> {
            SwitchCameraEntity.INSTANCE.switchEntity();
            return true;
        });
    }
}
