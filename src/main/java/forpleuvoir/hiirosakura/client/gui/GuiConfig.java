package forpleuvoir.hiirosakura.client.gui;

import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.util.StringUtils;
import forpleuvoir.hiirosakura.client.HiiroSakuraClient;
import forpleuvoir.hiirosakura.client.config.Configs;
import forpleuvoir.hiirosakura.client.config.HotKeys;

import java.util.List;
import java.util.Objects;

/**
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.gui
 * <p>#class_name GuiConfig
 * <p>#create_time 2021/6/15 20:28
 */
public class GuiConfig extends GuiConfigsBase {
    private static ConfigGuiTab tab = ConfigGuiTab.TOGGLES;

    public GuiConfig() {
        super(10, 50, HiiroSakuraClient.MOD_ID, null, "hiirosakura.gui.title.configs");
    }

    @Override
    public void initGui() {
        super.initGui();
        this.clearOptions();
        int x = 10;
        for (ConfigGuiTab tab : ConfigGuiTab.values()) {
            x += this.createButton(x, tab);
        }
    }

    private int createButton(int x, ConfigGuiTab tab) {
        ButtonGeneric button = new ButtonGeneric(x, 26, -1, 20, tab.getDisplayName());
        button.setEnabled(GuiConfig.tab != tab);
        this.addButton(button, new ButtonListener(tab, this));
        return button.getWidth() + 2;
    }

    @Override
    protected boolean useKeybindSearch() {
        return GuiConfig.tab == ConfigGuiTab.HOTKEYS;
    }

    @Override
    protected int getConfigWidth() {
        ConfigGuiTab tab = GuiConfig.tab;
        return switch (tab) {
            case TOGGLES -> 80;
            case VALUES -> 120;
            case HOTKEYS -> 200;
        };
    }

    @Override
    public List<ConfigOptionWrapper> getConfigs() {
        List<? extends IConfigBase> configs;
        ConfigGuiTab tab = GuiConfig.tab;
        configs = switch (tab) {
            case TOGGLES -> Configs.Toggles.OPTIONS;
            case VALUES -> Configs.Values.OPTIONS;
            case HOTKEYS -> HotKeys.HOTKEY_LIST;
        };
        return ConfigOptionWrapper.createFor(configs);
    }

    private record ButtonListener(ConfigGuiTab tab, GuiConfig parent) implements IButtonActionListener {

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            GuiConfig.tab = this.tab;
            this.parent.reCreateListWidget(); // apply the new config width
            Objects.requireNonNull(this.parent.getListWidget()).resetScrollbarPosition();
            this.parent.initGui();
        }
    }


    public enum ConfigGuiTab {
        TOGGLES("button.config_gui.toggles"),
        VALUES("button.config_gui.values"),
        HOTKEYS("button.config_gui.hotkeys");

        private final String translationKey;

        ConfigGuiTab(String translationKey) {
            this.translationKey = String.format("%s.gui.%s", HiiroSakuraClient.MOD_ID, translationKey);
        }

        public String getDisplayName() {
            return StringUtils.translate(this.translationKey);
        }
    }
}
