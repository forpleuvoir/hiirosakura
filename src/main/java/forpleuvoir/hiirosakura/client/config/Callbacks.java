package forpleuvoir.hiirosakura.client.config;

import fi.dy.masa.malilib.gui.GuiBase;
import forpleuvoir.hiirosakura.client.gui.GuiConfig;
import net.minecraft.client.MinecraftClient;

import static forpleuvoir.hiirosakura.client.config.HotKeys.*;

/**
 * 回调
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.config
 * <p>#class_name Callbacks
 * <p>#create_time 2021/6/15 23:40
 */
public class Callbacks {

    public static void init(MinecraftClient client) {
        OPEN_CONFIG_GUI.getKeybind().setCallback((action, key) -> {
            GuiBase.openGui(new GuiConfig());
            return true;
        });


    }

}
