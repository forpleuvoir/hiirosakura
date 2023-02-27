package forpleuvoir.hiirosakura.client.mixin;

import com.mojang.authlib.GameProfile;
import forpleuvoir.hiirosakura.client.config.Configs;
import forpleuvoir.hiirosakura.client.feature.chatbubble.HiiroSakuraChatBubble;
import forpleuvoir.hiirosakura.client.feature.chatmessage.ChatMessageFilter;
import forpleuvoir.hiirosakura.client.feature.event.events.MessageEvent;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
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
@Mixin(MessageHandler.class)
public abstract class MixinChatHud {

    @Inject(method = "onChatMessage", at = @At("HEAD"), cancellable = true)
    public void onChatMessage(SignedMessage message, GameProfile sender, MessageType.Parameters params, CallbackInfo ci) {
        var text = params.applyChatDecoration(message.getContent());
        var event = new MessageEvent(text.getString());
        event.broadcast();
        if (event.isCanceled()) ci.cancel();
        if (Configs.Toggles.CHAT_MESSAGE_FILTER.getValue()) {
            if (ChatMessageFilter.needToFilter(text)) {
                ci.cancel();
            }
        }

        if (Configs.Toggles.CHAT_BUBBLE.getValue())
            HiiroSakuraChatBubble.addChatBubble(text);
    }

}
