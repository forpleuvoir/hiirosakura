package forpleuvoir.hiirosakura.client.gui.event;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.gui.*;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetDropDownList;
import fi.dy.masa.malilib.util.StringUtils;
import forpleuvoir.hiirosakura.client.HiiroSakuraClient;
import forpleuvoir.hiirosakura.client.config.HiiroSakuraDatas;
import forpleuvoir.hiirosakura.client.feature.event.OnGameJoinEvent;
import forpleuvoir.hiirosakura.client.feature.event.base.EventSubscriberBase;
import forpleuvoir.hiirosakura.client.feature.event.base.HiiroSakuraEvents;
import forpleuvoir.hiirosakura.client.feature.task.TimeTask;
import forpleuvoir.hiirosakura.client.util.JsonUtil;
import forpleuvoir.hiirosakura.client.util.StringUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 事件编辑界面
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.gui.event
 * <p>#class_name EventEditScreen
 * <p>#create_time 2021/7/29 1:18
 */
public class EventEditScreen extends GuiBase {

    @Nullable
    private EventSubscriberBase subscriber;
    private final boolean editMode;

    private final TranslatableText nullValeText = StringUtil.translatableText("gui.event.null_value");
    private final TranslatableText saveButtonText = StringUtil.translatableText("gui.button.apply");
    private final TranslatableText eventTypeText = StringUtil.translatableText("gui.event.type");
    private final TranslatableText nameText = StringUtil.translatableText("gui.event.name");
    private final TranslatableText startTimeText = StringUtil.translatableText("gui.event.start_time");
    private final TranslatableText cyclesText = StringUtil.translatableText("gui.event.cycles");
    private final TranslatableText cycleTimeText = StringUtil.translatableText("gui.event.cycles_time");
    private final TranslatableText scriptText = StringUtil.translatableText("gui.event.script");

    private WidgetDropDownList<String> eventListDropDown;

    private GuiTextFieldGeneric nameInput;
    private GuiTextFieldInteger startTimeInput;
    private GuiTextFieldInteger cyclesInput;
    private GuiTextFieldInteger cycleTimeInput;
    private JsTextField scriptInput;

    public EventEditScreen(@Nullable EventSubscriberBase subscriber, Screen parentScreen) {
        this.subscriber = subscriber;
        this.editMode = true;
        this.setParent(parentScreen);
        this.title = StringUtils.translate(String.format("%s.gui.title.event.edit", HiiroSakuraClient.MOD_ID));
    }

    public EventEditScreen(Screen parentScreen) {
        this.editMode = false;
        this.setParent(new EventScreen());
        this.setParent(parentScreen);
        this.title = StringUtils.translate(String.format("%s.gui.title.event.edit", HiiroSakuraClient.MOD_ID));
    }

    @Override
    public void initGui() {
        super.initGui();
        int x = 10;
        int y = 24;
        initSaveButton(x);
        x = initEventListDropDown(x, y);
        initScriptEditor(x, y + 28);
        x = initEditor(x, y);
    }


    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        GuiBase.openGui(this.getParent());
    }

    public boolean checkSave() {
        var arg = "";
        if (StringUtil.isEmpty(nameInput.getText())) {
            arg = nameText.getKey();
        } else if (StringUtil.isEmpty(startTimeInput.getText())) {
            arg = startTimeText.getKey();
        } else if (StringUtil.isEmpty(cyclesInput.getText())) {
            arg = cyclesText.getKey();
        } else if (StringUtil.isEmpty(cycleTimeInput.getText())) {
            arg = cycleTimeText.getKey();
        }
        if (!StringUtil.isEmpty(arg)) {
            this.addGuiMessage(Message.MessageType.WARNING, 2000, nullValeText.getKey(), arg);
            return false;
        } else {
            return true;
        }
    }

    public void save() {
        if (!checkSave()) return;

        String eventType = eventListDropDown.getSelectedEntry();

        JsonObject timeTask = new JsonObject();

        String name = nameInput.getText();
        boolean enabled = subscriber == null || subscriber.enabled;

        timeTask.addProperty("name", name);
        timeTask.addProperty("enabled", enabled);

        Map<String, Object> data = new HashMap<>();

        int startTime = Integer.parseInt(startTimeInput.getText());
        int cycles = Integer.parseInt(cyclesInput.getText());
        int cyclesTime = Integer.parseInt(cycleTimeInput.getText());
        String script = scriptInput.getText();

        data.put("startTime", startTime);
        data.put("cycles", cycles);
        data.put("cyclesTime", cyclesTime);
        data.put("script", script);
        data.put("name", name);

        timeTask.addProperty("timeTask", JsonUtil.toJsonStr(data));

        var subscriber = new EventSubscriberBase(eventType, timeTask);

        if (editMode)
            HiiroSakuraDatas.HIIRO_SAKURA_EVENTS.update(this.subscriber, subscriber);
        else {
            HiiroSakuraDatas.HIIRO_SAKURA_EVENTS.subscribe(subscriber);
        }
        this.closeGui(true);
    }

    public void initSaveButton(int x) {
        ButtonGeneric saveButton = new ButtonGeneric(x + 12, this.height - 24, 128, false, saveButtonText.getKey());
        saveButton.setPosition(saveButton.getX(), this.height - 22 - saveButton.getHeight());
        this.addButton(saveButton, (button, mouseButton) -> save());
    }


    public int initEventListDropDown(int x, int y) {
        this.addLabel(x + 12, y, -1, 12, 0xFFFFFFFF, "§b" + StringUtils.translate(eventTypeText.getKey()));
        y += 4;
        eventListDropDown = new WidgetDropDownList<>(x, y, 128, 15, 200, 10,
                ImmutableList.copyOf(
                        HiiroSakuraEvents.events.keySet()),
                (type) -> "§6§l§n" + type);
        eventListDropDown.setPosition(x + 12, y + (eventListDropDown.getHeight() / 2));
        this.addWidget(eventListDropDown);
        if (subscriber != null) {
            eventListDropDown.setSelectedEntry(subscriber.eventType);
        } else {
            eventListDropDown.setSelectedEntry(HiiroSakuraEvents.getEventType(OnGameJoinEvent.class));
        }
        return x + eventListDropDown.getWidth() + 4;
    }

    public int initEditor(int x, int y) {
        TimeTask timeTask = null;
        if (subscriber != null) {
            timeTask = subscriber.getTimeTask();
        }
        x += this.createStringEditorFiled(x, y, nameText.getKey(), consumer -> nameInput = consumer, timeTask != null ? timeTask.getName() : null);
        x += this.createIntEditorFiled(x, y, startTimeText.getKey(), consumer -> startTimeInput = consumer, timeTask != null ? timeTask.data.startTime() : 0, 0, Integer.MAX_VALUE);
        x += this.createIntEditorFiled(x, y, cyclesText.getKey(), consumer -> cyclesInput = consumer, timeTask != null ? timeTask.data.cycles() : 1, 1, Integer.MAX_VALUE);
        x += this.createIntEditorFiled(x, y, cycleTimeText.getKey(), consumer -> cycleTimeInput = consumer, timeTask != null ? timeTask.data.cyclesTime() : 0, 0, Integer.MAX_VALUE);
        return x;
    }

    public void initScriptEditor(int x, int y) {
        this.addLabel(x + 12, y, -1, 12, 0xFFFFFFFF, StringUtils.translate(scriptText.getKey()));
        y += 11;
        scriptInput = new JsTextField(textRenderer, x + 12, y, 261, this.height - y - 24, 10, true);
        scriptInput.setText(subscriber != null ? subscriber.getScript() : "");
        this.addTextField(scriptInput, null);
    }

    @Override
    public boolean onMouseScrolled(int mouseX, int mouseY, double mouseWheelDelta) {
        if (scriptInput.mouseScrolled(mouseX, mouseY, mouseWheelDelta)) {
            return true;
        }
        return super.onMouseScrolled(mouseX, mouseY, mouseWheelDelta);
    }

    public int createStringEditorFiled(int x, int y, String translationKey, Consumer<GuiTextFieldGeneric> inputConsumer, @Nullable String value) {
        this.addLabel(x + 12, y, -1, 12, 0xFFFFFFFF, StringUtils.translate(translationKey));
        y += 11;
        GuiTextFieldGeneric inputFiled = new GuiTextFieldGeneric(x + 12, y, 80, 14, this.textRenderer);
        inputFiled.setText(value != null ? value : "");
        this.addTextField(inputFiled, null);
        inputConsumer.accept(inputFiled);
        return inputFiled.getWidth() + 4;
    }

    public int createIntEditorFiled(int x, int y, String translationKey, Consumer<GuiTextFieldInteger> inputConsumer, @Nullable Integer value, int min, int max) {
        this.addLabel(x + 12, y, -1, 12, 0xFFFFFFFF, StringUtils.translate(translationKey));
        y += 11;
        GuiTextFieldInteger inputFiled = new GuiTextFieldInteger(x + 12, y, 40, 14, this.textRenderer);
        inputFiled.setText(String.valueOf(value != null ? value : 0));
        inputFiled.setEditable(false);
        this.addTextField(inputFiled, null);
        String hover = StringUtils.translate("malilib.gui.button.hover.plus_minus_tip");
        ButtonGeneric button = new ButtonGeneric(x + 54, y - 1, MaLiLibIcons.BTN_PLUSMINUS_16, hover);
        this.addButton(button, new ButtonListenerIntegerModifier(inputFiled, min, max));
        inputConsumer.accept(inputFiled);
        return button.getWidth() + inputFiled.getWidth() + 4;
    }


    public static class ButtonListenerIntegerModifier implements IButtonActionListener {

        protected final GuiTextFieldInteger consumer;
        protected final int modifierShift = 10;
        protected final int modifierAlt = 5;
        private final int minValue;
        private final int maxValue;

        public ButtonListenerIntegerModifier(GuiTextFieldInteger consumer, int minValue, int maxValue) {
            this.consumer = consumer;
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            int amount = mouseButton == 1 ? -1 : 1;

            if (GuiBase.isShiftDown()) {
                amount *= this.modifierShift;
            }
            if (GuiBase.isAltDown()) {
                amount *= this.modifierAlt;
            }

            int value = Integer.parseInt(this.consumer.getText()) + amount;
            value = Math.max(value, minValue);
            value = Math.min(value, maxValue);

            this.consumer.setText(String.valueOf(value));
        }
    }


}
