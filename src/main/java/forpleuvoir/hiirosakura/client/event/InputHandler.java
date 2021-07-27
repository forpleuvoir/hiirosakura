package forpleuvoir.hiirosakura.client.event;

import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.IKeybindManager;
import fi.dy.masa.malilib.hotkeys.IKeybindProvider;
import forpleuvoir.hiirosakura.client.HiiroSakuraClient;
import forpleuvoir.hiirosakura.client.config.HotKeys;
import forpleuvoir.hiirosakura.client.config.TogglesHotKeys;

/**
 * 输入处理程序
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.event
 * <p>#class_name InputHandler
 * <p>#create_time 2021/6/15 23:52
 */
public class InputHandler implements IKeybindProvider {
    private static final InputHandler INSTANCE = new InputHandler();

    public static InputHandler getInstance() {
        return INSTANCE;
    }

    @Override
    public void addKeysToMap(IKeybindManager manager) {
        for (IHotkey hotkey : HotKeys.HOTKEY_LIST) {
            manager.addKeybindToMap(hotkey.getKeybind());
        }
        for (IHotkey togglesHotkey : TogglesHotKeys.HOTKEY_LIST) {
            manager.addKeybindToMap(togglesHotkey.getKeybind());
        }
    }

    @Override
    public void addHotkeys(IKeybindManager manager) {
        manager.addHotkeysForCategory(HiiroSakuraClient.MOD_NAME, String.format("%s.hotkeys", HiiroSakuraClient.MOD_ID), HotKeys.HOTKEY_LIST);
        manager.addHotkeysForCategory(HiiroSakuraClient.MOD_NAME, String.format("%s.hotkeys.toggles", HiiroSakuraClient.MOD_ID), TogglesHotKeys.HOTKEY_LIST);
    }

}
