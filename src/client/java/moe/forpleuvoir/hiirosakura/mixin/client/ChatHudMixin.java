package moe.forpleuvoir.hiirosakura.mixin.client;

import moe.forpleuvoir.hiirosakura.functional.chataddons.chatbubble.ChatBubbleHandler;
import moe.forpleuvoir.hiirosakura.functional.chataddons.ChatFilterHandler;
import moe.forpleuvoir.hiirosakura.functional.event.events.MessageReceiveEvent;
import moe.forpleuvoir.nebula.event.EventBus;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class ChatHudMixin {

    @Inject(
        method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
        at = @At("HEAD"),
        cancellable = true)
    public void onChatMessage(Text message, MessageSignatureData signatureData, MessageIndicator indicator, CallbackInfo ci) {
        var event = new MessageReceiveEvent(message.getString());
        EventBus.Companion.broadcast(event);
        if (event.getCanceled()) {
            ci.cancel();
            return;
        }
        if (ChatFilterHandler.shouldFilter(message)) {
            ci.cancel();
            return;
        }
        ChatBubbleHandler.addChatBubble(message);
    }

}
