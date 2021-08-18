package forpleuvoir.hiirosakura.client.mixin;

import forpleuvoir.hiirosakura.client.config.Configs;
import forpleuvoir.hiirosakura.client.feature.chatmessage.ChatMessageFilter;
import forpleuvoir.hiirosakura.client.feature.chatshow.HiiroSakuraChatShow;
import forpleuvoir.hiirosakura.client.feature.event.OnMessageEvent;
import forpleuvoir.hiirosakura.client.feature.event.base.EventBus;
import net.minecraft.client.gui.hud.ChatHudListener;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

/**
 * 聊天栏监听注入
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.mixin
 * <p>#class_name MixinChatHudListener
 * <p>#create_time 2021/6/13 0:11
 */
@Mixin(ChatHudListener.class)
public abstract class MixinChatHudListener {

    @Inject(
            method = "onChatMessage",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onChatMessage(MessageType type, Text text, UUID senderUuid, CallbackInfo ci) {
        EventBus.broadcast(new OnMessageEvent(text.getString(), type.getId()));

        if (Configs.Toggles.CHAT_MESSAGE_FILTER.getBooleanValue()) {
            if (ChatMessageFilter.needToFilter(text)) {
                ci.cancel();
            }
        }
        if (Configs.Toggles.CHAT_SHOW.getBooleanValue())
            HiiroSakuraChatShow.addChatShow(text);
    }

}
