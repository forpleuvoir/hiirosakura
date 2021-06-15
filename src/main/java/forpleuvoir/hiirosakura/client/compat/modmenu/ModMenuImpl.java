package forpleuvoir.hiirosakura.client.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import forpleuvoir.hiirosakura.client.gui.GuiConfig;

/**
 * ModMenu 接口
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.compat.modmenu
 * <p>#class_name ModMenuImpl
 * <p>#create_time 2021/6/10 21:50
 */
public class ModMenuImpl implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (screen) -> {
            var gui = new GuiConfig();
            gui.setParent(screen);
            return gui;
        };
    }
}
