package forpleuvoir.hiirosakura.client.gui;

import fi.dy.masa.malilib.gui.*;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.interfaces.IConfirmationListener;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;
import forpleuvoir.hiirosakura.client.HiiroSakuraClient;
import forpleuvoir.hiirosakura.client.config.HiiroSakuraDatas;
import forpleuvoir.hiirosakura.client.feature.qcms.QuickChatMessageSend;
import forpleuvoir.hiirosakura.client.util.Colors;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 快速聊天消息发送屏幕
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.gui
 * <p>#class_name QCMSScreen
 * <p>#create_time 2021/6/18 21:12
 */
public class QCMSScreen extends GuiBase {
    private final TranslatableText title = new TranslatableText(
            String.format("%s.gui.title.qcms", HiiroSakuraClient.MOD_ID));
    private final TranslatableText empty = new TranslatableText(
            String.format("%s.feature.qcms.data.empty", HiiroSakuraClient.MOD_ID));
    private final List<QCMSButton> buttons = new LinkedList<>();

    public QCMSScreen() {
        super();
        this.setTitle(StringUtils.translate(title.getKey()));
    }

    @Override
    public void initGui() {
        super.initGui();
        var datas = HiiroSakuraDatas.QUICK_CHAT_MESSAGE_SEND.getDatas();
        this.addButton(
                new QCMSButton(this.width / 2 - 40, this.height - 55, 80, 20,
                               StringUtils.translate(String.format("%s.gui.button.add", HiiroSakuraClient.MOD_ID))
                ),
                (button, mouseButton) -> openAddScreen()
        );
        if (datas.isEmpty()) {
            this.addGuiMessage(Message.MessageType.WARNING, 2000, empty.getKey());
            return;
        }
        int padding = 10;
        AtomicInteger indexX = new AtomicInteger(40);
        AtomicInteger indexY = new AtomicInteger(40);
        datas.forEach((k, v) -> {
            int width = Math.max(QuickChatMessageSend.getKeyLength(k) * 12, 20);
            int x = indexX.get();
            indexX.addAndGet(width);
            int y = indexY.get();
            if (this.width - indexX.get() <= 40 || indexX.get() + width > (this.width - 20)) {
                indexY.addAndGet(20);
                indexX.set(40);
            }
            MutableText message = new LiteralText(v).styled(style -> style.withColor(
                    Colors.DHWUIA.getColor()));
            MutableText tooltip = new TranslatableText(String.format("%s.gui.qcms.hover",
                                                                     HiiroSakuraClient.MOD_ID
            )).styled(style -> style.withColor(Colors.FORPLEUVOIR.getColor()));
            var qcmsButton = new QCMSButton(x + padding, y + padding, width, 20,
                                            k.replace("&", "§"),
                                            message, tooltip
            );
            this.addButton(qcmsButton, (button, mouseButton) ->
                    this.buttonClick(mouseButton, k, v)
            );
        });
    }

    private void sendChatMessage(String message) {
        assert Objects.requireNonNull(client).player != null;
        if (client.player != null) {
            client.player.sendChatMessage(message);
            this.onClose();
        }
    }

    protected void drawButtonHoverTexts(int mouseX, int mouseY, float partialTicks, MatrixStack matrixStack) {
        for (QCMSButton button : buttons) {
            if (button.hasHoverText() && button.isMouseOver()) {
                renderTooltip(matrixStack, button.getHoverText(), mouseX + 10, mouseY);
            }
        }
    }

    @Override
    public <T extends ButtonBase> T addButton(T button, IButtonActionListener listener) {
        if (button instanceof QCMSButton qcmsButton) {
            this.buttons.add(qcmsButton);
        }
        return super.addButton(button, listener);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void buttonClick(int mouseButton, String key, String value) {
        switch (mouseButton) {
            case 0 -> sendChatMessage(value);
            case 1 -> openEditScreen(key, value);
            case 2 -> openDeleteScreen(key);
        }
    }

    private void openDeleteScreen(String key) {
        String titleKey = String.format("%s.gui.title.qcms.delete", HiiroSakuraClient.MOD_ID);
        String messageKey = String.format("%s.gui.qcms.confirmDelete", HiiroSakuraClient.MOD_ID);
        int width = Math.max(textRenderer
                                     .getWidth(StringUtils.translate(titleKey)),
                             textRenderer
                                     .getWidth(StringUtils.translate(messageKey))
        );
        var dialog = new GuiConfirmAction(width,
                                          titleKey,
                                          new IConfirmationListener() {
                                              @Override
                                              public boolean onActionConfirmed() {
                                                  HiiroSakuraDatas.QUICK_CHAT_MESSAGE_SEND.remove(key);
                                                  return true;
                                              }

                                              @Override
                                              public boolean onActionCancelled() {
                                                  return false;
                                              }
                                          },
                                          this,
                                          messageKey,
                                          key
        );
        openGui(dialog);
    }

    private void openEditScreen(String key, String value) {
        GuiBase.openGui(new EditScreen(key, value, this));
    }

    private void openAddScreen() {
        GuiBase.openGui(new EditScreen(this));
    }

    public static class QCMSButton extends ButtonGeneric {
        private final List<Text> hoverText = new LinkedList<>();

        public QCMSButton(int x, int y, int width, int height, String text, Text... hoverText) {
            super(x, y, width, height, text, "");
            if (hoverText != null && hoverText.length > 0)
                this.hoverText.addAll(Arrays.asList(hoverText));
        }

        public List<Text> getHoverText() {
            return hoverText;
        }

        @Override
        public boolean hasHoverText() {
            return !hoverText.isEmpty();
        }
    }

    public static class EditScreen extends GuiDialogBase {
        private final String remark;
        private final String value;
        private GuiTextFieldGeneric remarkTextField;
        private GuiTextFieldGeneric valueTextField;
        private final TranslatableText remarkText = new TranslatableText(
                String.format("%s.gui.qcms.key", HiiroSakuraClient.MOD_ID));
        private final TranslatableText valueText = new TranslatableText(
                String.format("%s.gui.qcms.value", HiiroSakuraClient.MOD_ID));
        private final boolean editModel;

        public EditScreen(String remark, String value, Screen parent) {
            this.setParent(parent);
            this.remark = remark;
            this.value = value;
            this.editModel = true;
        }

        public EditScreen(Screen parent) {
            this.setParent(parent);
            this.remark = null;
            this.value = null;
            this.editModel = false;
        }


        @Override
        public void initGui() {
            super.initGui();
            this.setWidthAndHeight(200, 112);
            this.setTitle(StringUtils.translate(String.format("%s.gui.qcms.edit", HiiroSakuraClient.MOD_ID)));
            this.centerOnScreen();
            int x = this.dialogLeft + 10;
            int y = this.dialogTop + this.dialogHeight - 24;
            int buttonWidth = this.dialogWidth / 2 - 20;
            createButton(x, y, buttonWidth, ButtonType.APPLY);
            x += buttonWidth + 20;
            createButton(x, y, buttonWidth, ButtonType.CANCEL);
            int tY = this.dialogTop + 24;
            createRemarkTextField(tY);
            tY += 35;
            createValueTextField(tY);
        }

        public void createRemarkTextField(int y) {
            int x = this.dialogLeft + 10;
            int width = this.dialogWidth - 20;
            int textWidth = this.textRenderer.getWidth(remarkText);
            remarkTextField = new GuiTextFieldGeneric(x + textWidth + 5, y, width - textWidth - 6, 20,
                                                      this.textRenderer
            );
            if (editModel) remarkTextField.setText(remark);
            this.addTextField(remarkTextField, null);
        }

        public void createValueTextField(int y) {
            int x = this.dialogLeft + 10;
            int width = this.dialogWidth - 20;
            int textWidth = this.textRenderer.getWidth(valueText);
            valueTextField = new GuiTextFieldGeneric(x + textWidth + 5, y, width - textWidth - 6, 20,
                                                     this.textRenderer
            );
            if (editModel) valueTextField.setText(value);
            this.addTextField(valueTextField, null);
        }


        @Override
        protected void drawContents(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks
        ) {
            if (this.getParent() != null) {
                this.getParent().render(matrixStack, mouseX, mouseY, partialTicks);
            }
            matrixStack.push();
            matrixStack.translate(0, 0, this.getZOffset());
            RenderUtils
                    .drawOutlinedBox(this.dialogLeft, this.dialogTop, this.dialogWidth, this.dialogHeight, 0xF0000000,
                                     COLOR_HORIZONTAL_BAR
                    );
            // Draw the title
            this.drawStringWithShadow(matrixStack, this.getTitleString(), this.dialogLeft + 10, this.dialogTop + 4,
                                      COLOR_WHITE
            );
            this.drawStringWithShadow(matrixStack, StringUtils.translate(remarkText.getKey()), this.dialogLeft + 10,
                                      this.remarkTextField.getY() + 5,
                                      COLOR_WHITE
            );
            this.drawStringWithShadow(matrixStack, StringUtils.translate(valueText.getKey()), this.dialogLeft + 10,
                                      this.valueTextField.getY() + 5,
                                      COLOR_WHITE
            );
            this.drawTextFields(mouseX, mouseY, matrixStack);
            this.drawButtons(mouseX, mouseY, partialTicks, matrixStack);
            matrixStack.pop();
        }

        @Override
        protected void drawTitle(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {

        }

        protected void createButton(int x, int y, int buttonWidth, ButtonType type) {
            ButtonGeneric button = new ButtonGeneric(x, y, buttonWidth, 20, type.getDisplayName());
            this.addButton(button, type == ButtonType.APPLY ? this::apply : this::cancel);
        }

        private void apply(ButtonBase button, int mouseButton) {
            if (mouseButton == 0) {
                if (editModel)
                    HiiroSakuraDatas.QUICK_CHAT_MESSAGE_SEND
                            .reset(remark, remarkTextField.getText(), valueTextField.getText());
                else
                    HiiroSakuraDatas.QUICK_CHAT_MESSAGE_SEND.add(remarkTextField.getText(), valueTextField.getText());
            }
            this.closeGui(true);
        }

        private void cancel(ButtonBase button, int mouseButton) {
            this.closeGui(true);
        }

        protected enum ButtonType {
            APPLY(String.format("%s.gui.button.apply", HiiroSakuraClient.MOD_ID)),
            CANCEL(String.format("%s.gui.button.cancel", HiiroSakuraClient.MOD_ID));

            private final String labelKey;

            ButtonType(String labelKey) {
                this.labelKey = labelKey;
            }

            public String getDisplayName() {
                return (this == ButtonType.APPLY ? GuiBase.TXT_GREEN : GuiBase.TXT_RED) + StringUtils
                        .translate(this.labelKey) + GuiBase.TXT_RST;
            }
        }
    }
}
