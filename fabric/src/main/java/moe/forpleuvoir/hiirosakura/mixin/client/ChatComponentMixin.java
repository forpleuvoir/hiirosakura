package moe.forpleuvoir.hiirosakura.mixin.client;

import moe.forpleuvoir.hiirosakura.functional.chataddons.ChatFilterHandler;
import moe.forpleuvoir.hiirosakura.functional.event.events.MessageReceiveEvent;
import moe.forpleuvoir.nebula.event.EventBus;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatComponent.class)
public abstract class ChatComponentMixin {

    @Inject(
            method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V",
            at = @At("HEAD"),
            cancellable = true)
    public void onChatMessage(Component message, MessageSignature headerSignature, GuiMessageTag tag, CallbackInfo ci) {
        var event = new MessageReceiveEvent(message.getString(), null);
        EventBus.Companion.broadcast(event);
        if (event.getCanceled()) {
            ci.cancel();
            return;
        }
        if (ChatFilterHandler.shouldFilter(message)) {
            ci.cancel();
        }
    }

}
