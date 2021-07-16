package forpleuvoir.hiirosakura.client.gui.qcms;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiDialogBase;
import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.GuiTextFieldInteger;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;
import forpleuvoir.hiirosakura.client.HiiroSakuraClient;
import forpleuvoir.hiirosakura.client.config.HiiroSakuraDatas;
import forpleuvoir.hiirosakura.client.feature.qcms.QuickChatMessage;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

/**
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.gui.qcms
 * <p>#class_name EditScreen
 * <p>#create_time 2021/7/17 0:59
 */
public class EditScreen extends GuiDialogBase {
    private final String remark;
    private final String message;
    private final Integer level;
    private GuiTextFieldGeneric remarkTextField;
    private GuiTextFieldGeneric messageTextField;
    private GuiTextFieldInteger levelTextField;
    private final TranslatableText remarkText = new TranslatableText(
            String.format("%s.gui.qcms.key", HiiroSakuraClient.MOD_ID));
    private final TranslatableText messageText = new TranslatableText(
            String.format("%s.gui.qcms.value", HiiroSakuraClient.MOD_ID));
    private final TranslatableText levelText = new TranslatableText(
            String.format("%s.gui.qcms.level", HiiroSakuraClient.MOD_ID));
    private final boolean editModel;

    public EditScreen(QuickChatMessage quickChatMessage, Screen parent) {
        this.setParent(parent);
        this.remark = quickChatMessage.remark();
        this.message = quickChatMessage.message();
        this.level = quickChatMessage.level();
        this.editModel = true;
    }


    public EditScreen(Screen parent) {
        this.setParent(parent);
        this.remark = null;
        this.message = null;
        this.level = null;
        this.editModel = false;
    }


    @Override
    public void initGui() {
        super.initGui();
        this.setWidthAndHeight(200, 155);
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
        createMessageTextField(tY);
        tY += 35;
        createLevelTextField(tY);
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

    public void createMessageTextField(int y) {
        int x = this.dialogLeft + 10;
        int width = this.dialogWidth - 20;
        int textWidth = this.textRenderer.getWidth(messageText);
        messageTextField = new GuiTextFieldGeneric(x + textWidth + 5, y, width - textWidth - 6, 20,
                                                   this.textRenderer
        );
        if (editModel) messageTextField.setText(message);
        this.addTextField(messageTextField, null);
    }

    public void createLevelTextField(int y) {
        int x = this.dialogLeft + 10;
        int width = this.dialogWidth - 20;
        int textWidth = this.textRenderer.getWidth(messageText);
        levelTextField = new GuiTextFieldInteger(x + textWidth + 5, y, width - textWidth - 6, 20,
                                                 this.textRenderer
        );
        if (editModel) levelTextField.setText(String.valueOf(level));
        this.addTextField(levelTextField, null);
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
        this.drawStringWithShadow(matrixStack, this.getTitleString(), this.dialogLeft + 10, this.dialogTop + 4,
                                  COLOR_WHITE
        );
        this.drawStringWithShadow(matrixStack, StringUtils.translate(remarkText.getKey()), this.dialogLeft + 10,
                                  this.remarkTextField.getY() + 5,
                                  COLOR_WHITE
        );
        this.drawStringWithShadow(matrixStack, StringUtils.translate(messageText.getKey()), this.dialogLeft + 10,
                                  this.messageTextField.getY() + 5,
                                  COLOR_WHITE
        );
        this.drawStringWithShadow(matrixStack, StringUtils.translate(levelText.getKey()), this.dialogLeft + 10,
                                  this.levelTextField.getY() + 5,
                                  COLOR_WHITE
        );
        this.drawTextFields(mouseX, mouseY, matrixStack);
        this.drawButtons(mouseX, mouseY, partialTicks, matrixStack);
        matrixStack.pop();
    }

    @Override
    protected void drawTitle(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {

    }

    protected void createButton(int x, int y, int buttonWidth, EditScreen.ButtonType type) {
        ButtonGeneric button = new ButtonGeneric(x, y, buttonWidth, 20, type.getDisplayName());
        this.addButton(button, type == ButtonType.APPLY ? this::apply : this::cancel);
    }

    private void apply(ButtonBase button, int mouseButton) {
        if (mouseButton == 0) {
            if (editModel) {
                HiiroSakuraDatas.QUICK_CHAT_MESSAGE_SEND
                        .reset(remark, remarkTextField.getText(), messageTextField.getText());
                if (levelTextField.getText() != null) {
                    Integer level = null;
                    try {
                        level = Integer.parseInt(levelTextField.getText());
                    } catch (Exception ignored) {
                    }
                    HiiroSakuraDatas.QUICK_CHAT_MESSAGE_SORT.resetLevel(remark, remarkTextField.getText(), level);
                }
            } else {
                HiiroSakuraDatas.QUICK_CHAT_MESSAGE_SEND.add(remarkTextField.getText(), messageTextField.getText());
                if (levelTextField.getText() != null) {
                    try {
                        int level = Integer.parseInt(levelTextField.getText());
                        HiiroSakuraDatas.QUICK_CHAT_MESSAGE_SORT.setSort(remarkTextField.getText(), level);
                    } catch (Exception ignored) {
                    }
                }
            }
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