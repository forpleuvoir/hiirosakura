package moe.forpleuvoir.hiirosakura.mixin.client;

import moe.forpleuvoir.hiirosakura.functional.chataddons.ChatFilterHandler;
import moe.forpleuvoir.hiirosakura.functional.chataddons.chatbubble.ChatBubbleHandler;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatListener.class)
public abstract class ChatListenerMixin {

    @Inject(method = "handleSystemMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;addClientSystemMessage(Lnet/minecraft/network/chat/Component;)V"))
    public void handleSystemMessage(Component message, boolean remote, CallbackInfo ci) {
        if (!ChatFilterHandler.shouldFilter(message)) {
            ChatBubbleHandler.addChatBubble(message, null, null);
        }
    }

}
