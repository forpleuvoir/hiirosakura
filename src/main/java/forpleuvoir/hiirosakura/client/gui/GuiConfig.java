package forpleuvoir.hiirosakura.client.gui;

import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.GuiTextFieldInteger;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.util.StringUtils;
import forpleuvoir.hiirosakura.client.HiiroSakuraClient;
import forpleuvoir.hiirosakura.client.config.Configs;
import forpleuvoir.hiirosakura.client.config.HotKeys;
import forpleuvoir.hiirosakura.client.config.TogglesHotKeys;
import forpleuvoir.hiirosakura.client.gui.event.EventScreen;
import net.minecraft.client.gui.screen.Screen;

import java.util.Collections;
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
    public static ConfigGuiTab tab = ConfigGuiTab.TOGGLES;

    public GuiConfig(Screen parent){
    	this();
    	this.setParent(parent);
    }

    public GuiConfig() {
        super(10, 50, HiiroSakuraClient.MOD_ID, null, "hiirosakura.gui.title.configs");
    }

    @Override
    public void initGui() {
        if (tab == ConfigGuiTab.EVENT) {
            GuiBase.openGui(new EventScreen());
            return;
        }
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
        return GuiConfig.tab == ConfigGuiTab.HOTKEYS || GuiConfig.tab == ConfigGuiTab.TOGGLES_HOTKEYS;
    }

    @Override
    protected int getConfigWidth() {
        ConfigGuiTab tab = GuiConfig.tab;
        return switch (tab) {
            case TOGGLES -> 80;
            case VALUES -> 120;
            case HOTKEYS, TOGGLES_HOTKEYS -> 200;
            default -> super.getConfigWidth();
        };
    }

    @Override
    public List<ConfigOptionWrapper> getConfigs() {
        List<? extends IConfigBase> configs;
        ConfigGuiTab tab = GuiConfig.tab;
        configs = switch (tab) {
            case TOGGLES -> Configs.Toggles.OPTIONS;
            case TOGGLES_HOTKEYS -> TogglesHotKeys.getHOTKEY_LIST();
            case VALUES -> Configs.Values.OPTIONS;
            case HOTKEYS -> HotKeys.HOTKEY_LIST;
            default -> Collections.emptyList();
        };
        return ConfigOptionWrapper.createFor(configs);
    }

    private record ButtonListener(ConfigGuiTab tab, GuiConfig parent) implements IButtonActionListener {

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            GuiConfig.tab = this.tab;
            if (tab == ConfigGuiTab.EVENT) {
                GuiBase.openGui(new EventScreen());
            } else {
                this.parent.reCreateListWidget();
                Objects.requireNonNull(this.parent.getListWidget()).resetScrollbarPosition();
                this.parent.initGui();
            }
        }
    }


    public enum ConfigGuiTab {
        TOGGLES("button.config_gui.toggles"),
        TOGGLES_HOTKEYS("button.config_gui.toggles.hotkeys"),
        VALUES("button.config_gui.values"),
        HOTKEYS("button.config_gui.hotkeys"),
        EVENT("button.config_gui.event");


        private final String translationKey;

        ConfigGuiTab(String translationKey) {
            this.translationKey = String.format("%s.gui.%s", HiiroSakuraClient.MOD_ID, translationKey);
        }

        public String getDisplayName() {
            return StringUtils.translate(this.translationKey);
        }
    }
}
