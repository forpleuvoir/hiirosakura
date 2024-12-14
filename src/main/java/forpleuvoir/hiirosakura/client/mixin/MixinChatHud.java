package forpleuvoir.hiirosakura.client.mixin;

import forpleuvoir.hiirosakura.client.config.Configs;
import forpleuvoir.hiirosakura.client.feature.chatbubble.HiiroSakuraChatBubble;
import forpleuvoir.hiirosakura.client.feature.chatmessage.ChatMessageFilter;
import forpleuvoir.hiirosakura.client.feature.event.events.MessageEvent;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


/**
 * 聊天栏监听注入
 *
 * @author forpleuvoir
 * <p>项目名 hiirosakura
 * <p>包名 forpleuvoir.hiirosakura.client.mixin
 * <p>文件名 MixinChatHudListener
 * <p>创建时间 2021/6/13 0:11
 */
@Mixin(ChatHud.class)
public abstract class MixinChatHud {

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V", at = @At("HEAD"), cancellable = true)
    private void onChatMessage(Text message, @Nullable MessageSignatureData signature, int ticks, @Nullable MessageIndicator indicator, boolean refresh, CallbackInfo ci) {
        var event = new MessageEvent(message.getString());
        event.broadcast();
        if (event.isCanceled()) ci.cancel();
        if (Configs.Toggles.CHAT_MESSAGE_FILTER.getValue()) {
            if (ChatMessageFilter.needToFilter(message)) {
                ci.cancel();
            }
        }

        if (Configs.Toggles.CHAT_BUBBLE.getValue())
            HiiroSakuraChatBubble.addChatBubble(message);
    }

}
