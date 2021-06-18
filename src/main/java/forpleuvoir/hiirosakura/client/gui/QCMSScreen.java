package forpleuvoir.hiirosakura.client.gui;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiConfirmAction;
import fi.dy.masa.malilib.gui.GuiDialogBase;
import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.ITextFieldListener;
import fi.dy.masa.malilib.interfaces.ICompletionListener;
import fi.dy.masa.malilib.interfaces.IConfirmationListener;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;
import forpleuvoir.hiirosakura.client.HiiroSakuraClient;
import forpleuvoir.hiirosakura.client.config.HiiroSakuraDatas;
import forpleuvoir.hiirosakura.client.feature.qcms.QuickChatMessageSend;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.tutorial.TutorialManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
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
public class QCMSScreen extends Screen {
    private boolean editMode;

    public QCMSScreen(boolean editMode) {
        super(new TranslatableText(String.format("%s.gui.title.qcms", HiiroSakuraClient.MOD_ID)));
        this.editMode = editMode;
    }

    @Override
    protected void init() {
        var datas = HiiroSakuraDatas.QUICK_CHAT_MESSAGE_SEND.getDatas();
        if (datas.isEmpty()) return;
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
            this.addDrawableChild(
                    new ButtonWidget(x + padding, y + padding, width, 20,
                                     new LiteralText(k.replace("&", "§")),
                                     (button -> this.buttonClick(k, v)),
                                     (button, matrices, mouseX, mouseY) -> {
                                         assert client != null;
                                         if (client.cameraEntity != null) {
                                             List<Text> list = new ArrayList<>();
                                             list.add(new LiteralText(v).styled(style -> style.withColor(
                                                     Formatting.RED)));
                                             list.add(getModeText().styled(style -> style.withColor(
                                                     Formatting.RED)));
                                             renderTooltip(matrices, list, mouseX, mouseY);
                                         }
                                     }
                    ));
        });
        this.addDrawableChild(
                new ButtonWidget(this.width / 2 - 40, this.height - 50, 80, 20,
                                 new TranslatableText(String.format("%s.gui.qcms.toggleMode", HiiroSakuraClient.MOD_ID))
                                         .append(getModeText()),
                                 (button -> {
                                     toggleMode();
                                     button.setMessage(new TranslatableText(
                                             String.format("%s.gui.qcms.toggleMode", HiiroSakuraClient.MOD_ID))
                                                               .append(getModeText()));
                                 })
                )
        );

    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private MutableText getModeText() {
        return !editMode ?
                new TranslatableText(String.format("%s.gui.qcms.send",
                                                   HiiroSakuraClient.MOD_ID
                )) :
                new TranslatableText(String.format("%s.gui.qcms.edit",
                                                   HiiroSakuraClient.MOD_ID
                ));
    }

    private void buttonClick(String key, String value) {
        if (!editMode) {
            assert client != null;
            ((ClientPlayerEntity) Objects.requireNonNull(client.getCameraEntity()))
                    .networkHandler.sendPacket(new ChatMessageC2SPacket(value));
            this.onClose();
        } else {
            openEditScreen(key, value);
        }
    }

    private void openEditScreen(String key, String value) {
        GuiBase.openGui(new EditScreen(key, value, this));
    }

    public void toggleMode() {
        this.editMode = !this.editMode;
    }


    public static class EditScreen extends GuiDialogBase {
        private final String remark;
        private final String value;
        private GuiTextFieldGeneric remarkText;
        private GuiTextFieldGeneric valueText;

        public EditScreen(String remark, String value, Screen parent) {
            this.setParent(parent);
            this.remark = remark;
            this.value = value;
            this.setWidthAndHeight(200, 112);
            this.setTitle("QCMS Edit");
            this.centerOnScreen();
        }


        @Override
        public void initGui() {
            super.initGui();
            int x = this.dialogLeft + 10;
            int y = this.dialogTop + this.dialogHeight - 24;
            int buttonWidth = this.dialogWidth / 2 - 20;
            createButton(x, y, buttonWidth, ButtonType.APPLY);
            x += buttonWidth + 20;
            createButton(x, y, buttonWidth, ButtonType.CANCEL);
            int tY = this.dialogTop + 24;
            createRemarkTextField(tY);
            tY += 20;
            createValueTextField(tY);
        }

        public void createRemarkTextField(int y) {
            int x = this.dialogLeft + 10;
            int width = this.dialogWidth - 20;
            remarkText = new GuiTextFieldGeneric(x, y, width, 20, this.textRenderer);
            remarkText.setText(remark);
            this.addTextField(remarkText, null);
        }

        public void createValueTextField(int y) {
            int x = this.dialogLeft + 10;
            int width = this.dialogWidth - 20;
            valueText = new GuiTextFieldGeneric(x, y, width, 20, this.textRenderer);
            valueText.setText(value);
            this.addTextField(valueText, null);
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
            this.drawTextFields(mouseX, mouseY, matrixStack);
            this.drawButtons(mouseX, mouseY, partialTicks, matrixStack);
            matrixStack.pop();
        }

        protected void createButton(int x, int y, int buttonWidth, ButtonType type) {
            ButtonGeneric button = new ButtonGeneric(x, y, buttonWidth, 20, type.getDisplayName());
            this.addButton(button, type == ButtonType.APPLY ? this::apply : this::cancel);
        }

        private void apply(ButtonBase button, int mouseButton) {
            if (mouseButton == 0) {
                HiiroSakuraDatas.QUICK_CHAT_MESSAGE_SEND.reset(remark, remarkText.getText(), valueText.getText());
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
