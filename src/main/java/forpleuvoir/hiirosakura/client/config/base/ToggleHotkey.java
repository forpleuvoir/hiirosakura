package forpleuvoir.hiirosakura.client.config.base;

import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import forpleuvoir.hiirosakura.client.HiiroSakuraClient;
import net.minecraft.text.LiteralText;

/**
 * 开关热键
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.config.base
 * <p>#class_name ToggleHotkey
 * <p>#create_time 2021-07-27 16:30
 */
public class ToggleHotkey extends ConfigHotkey {
    private final ConfigBoolean toggle;

    public ToggleHotkey(String name, String defaultStorageString, String comment, ConfigBoolean toggle) {
        super(name, defaultStorageString, comment);
        this.toggle = toggle;
    }

    public void initCallback(HiiroSakuraClient hs) {
        this.getKeybind().setCallback((action, key) -> {
            toggle.setBooleanValue(!toggle.getBooleanValue());
            hs.showGameInfo(
                    new LiteralText("§b" + toggle.getPrettyName())
                            .append(new LiteralText(toggle.getBooleanValue() ? " :§a true" : " :§4 false"))
            );
            return true;
        });
    }


}
