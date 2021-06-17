package forpleuvoir.hiirosakura.client.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.gui.GuiBase;
import forpleuvoir.hiirosakura.client.gui.GuiConfig;
import net.minecraft.client.MinecraftClient;

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

    public static final List<ConfigHotkey> HOTKEY_LIST = ImmutableList.of(
            OPEN_CONFIG_GUI
    );

    public static String translationKey(String key) {
        return Configs.translationKey("hotkeys", key);
    }

    public static void callback(MinecraftClient client) {
        OPEN_CONFIG_GUI.getKeybind().setCallback((action, key) -> {
            GuiBase.openGui(new GuiConfig());
            return true;
        });
    }
}
