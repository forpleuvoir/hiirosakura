package moe.forpleuvoir.hiirosakura.mixin.client;

import moe.forpleuvoir.hiirosakura.functional.chataddons.ChatFilterHandler;
import moe.forpleuvoir.hiirosakura.functional.event.events.HSEvents;
import moe.forpleuvoir.hiirosakura.functional.event.events.MessageReceiveContext;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatComponent.class)
public abstract class ChatComponentMixin {

    @Inject(
            method = "addPlayerMessage",
            at = @At("HEAD"),
            cancellable = true)
    public void addPlayerMessage(Component message, MessageSignature signature, GuiMessageTag tag, CallbackInfo ci) {
        var context = new MessageReceiveContext(message.getString(), null);
        HSEvents.MessageReceive.invoker().invoke(context);
        if (context.isCancelled()) {
            ci.cancel();
            return;
        }
        if (ChatFilterHandler.shouldFilter(message)) {
            ci.cancel();
        }
    }

}
