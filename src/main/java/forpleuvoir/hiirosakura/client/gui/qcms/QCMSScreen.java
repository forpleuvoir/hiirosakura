package forpleuvoir.hiirosakura.client.gui.qcms;

import fi.dy.masa.malilib.gui.*;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.interfaces.IConfirmationListener;
import fi.dy.masa.malilib.util.StringUtils;
import forpleuvoir.hiirosakura.client.HiiroSakuraClient;
import forpleuvoir.hiirosakura.client.config.HiiroSakuraDatas;
import forpleuvoir.hiirosakura.client.feature.qcms.QuickChatMessage;
import forpleuvoir.hiirosakura.client.feature.qcms.QuickChatMessageSend;
import forpleuvoir.hiirosakura.client.feature.task.TimeTask;
import forpleuvoir.hiirosakura.client.feature.task.TimeTaskHandler;
import forpleuvoir.hiirosakura.client.util.Colors;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;

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
        var datas = HiiroSakuraDatas.QUICK_CHAT_MESSAGE_SORT.getSortedData();
        HiiroSakuraDatas.QUICK_CHAT_MESSAGE_SORT.getUnSortedData().forEach(datas::addLast);
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
        datas.forEach(qcm -> {
            int width = Math.max(QuickChatMessageSend.getKeyLength(qcm.remark()) * 12, 20);
            int x = indexX.get();
            indexX.addAndGet(width);
            int y = indexY.get();
            if (this.width - indexX.get() <= 40 || indexX.get() + width > (this.width - 20)) {
                indexY.addAndGet(20);
                indexX.set(40);
            }
            MutableText message = new LiteralText(qcm.message()).styled(style -> style.withColor(
                    Colors.DHWUIA.getColor()));
            MutableText tooltip = new TranslatableText(String.format("%s.gui.qcms.hover",
                    HiiroSakuraClient.MOD_ID
            )).styled(style -> style.withColor(Colors.FORPLEUVOIR.getColor()));
            var qcmsButton = new QCMSButton(x + padding, y + padding, width, 20,
                    qcm.remark().replace("&", "§"),
                    message, tooltip
            );
            this.addButton(qcmsButton, (button, mouseButton) ->
                    this.buttonClick(mouseButton, qcm)
            );

        });
    }

    private void sendChatMessage(String message) {
        assert Objects.requireNonNull(client).player != null;
        if (client.player != null) {
            client.player.sendChatMessage(message);
            TimeTaskHandler.getInstance()
                    .addTask(TimeTask.once(hiiroSakuraClient -> this.onClose(), 5, "Close_QCMS_Screen"));
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

    private void buttonClick(int mouseButton, QuickChatMessage quickChatMessage) {
        switch (mouseButton) {
            case 0 -> sendChatMessage(quickChatMessage.message());
            case 1 -> openEditScreen(quickChatMessage);
            case 2 -> openDeleteScreen(quickChatMessage.remark());
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
                        HiiroSakuraDatas.QUICK_CHAT_MESSAGE_SORT.remove(key);
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

    private void openEditScreen(QuickChatMessage quickChatMessage) {
        GuiBase.openGui(new EditScreen(quickChatMessage, this));
    }

    private void openAddScreen() {
        GuiBase.openGui(new EditScreen(this));
    }


}
