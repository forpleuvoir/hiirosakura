package moe.forpleuvoir.hiirosakura.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import moe.forpleuvoir.hiirosakura.functional.chataddons.ChatFilterHandler;
import moe.forpleuvoir.hiirosakura.functional.chataddons.ChatInjectHandler;
import moe.forpleuvoir.hiirosakura.functional.chataddons.chatbubble.ChatBubbleHandler;
import moe.forpleuvoir.hiirosakura.functional.event.events.*;
import moe.forpleuvoir.hiirosakura.functional.misc.ServerMarker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatKillPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {

    @Inject(method = "handlePlayerChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ClientboundPlayerChatPacket;chatType()Lnet/minecraft/network/chat/ChatType$Bound;", ordinal = 0))
    public void handlePlayerChat(ClientboundPlayerChatPacket packet, CallbackInfo ci, @Local(name = "senderId") UUID senderId) {
        var parameters = packet.chatType();
        var text = parameters.decorate(ChatListener.CHAT_VALIDATION_ERROR);
        if (!ChatFilterHandler.shouldFilter(text)) {
            ChatBubbleHandler.addChatBubble(text, senderId, null);
        }
    }

    @Inject(method = "handlePlayerChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/SignedMessageValidator;updateAndValidate(Lnet/minecraft/network/chat/PlayerChatMessage;)Lnet/minecraft/network/chat/PlayerChatMessage;", shift = At.Shift.AFTER))
    public void handlePlayerChat(
            ClientboundPlayerChatPacket packet,
            CallbackInfo ci,
            @Local(name = "senderId") UUID senderId,
            @Local(name = "sender") PlayerInfo sender,
            @Local(name = "message") PlayerChatMessage message
    ) {
        var parameters = packet.chatType();
        var profile = sender.getProfile();
        Component text;
        if (message != null) {
            boolean bl = Minecraft.getInstance().options.onlyShowSecureChat().get();
            var signedMessage = bl ? message.removeUnsignedContent() : message;
            text = parameters.decorate(signedMessage.decoratedContent());
        } else {
            text = parameters.decorate(ChatListener.CHAT_VALIDATION_ERROR);
        }

        if (!ChatFilterHandler.shouldFilter(text)) {
            ChatBubbleHandler.addChatBubble(text, senderId, profile);
        }
    }

    @Inject(method = "handleLogin", at = @At("RETURN"))
    public void handleLogin(ClientboundLoginPacket packet, CallbackInfo ci) {
        ServerData serverInfo = Minecraft.getInstance().getCurrentServer();
        var name = serverInfo != null ? serverInfo.name : "";
        var address = serverInfo != null ? serverInfo.ip : "";
        if (!name.equals(ServerMarker.getName())) {
            ServerMarker.setValue(name, address);
            HSEvents.ServerJoin.invoker().invoke(new ServerJoinContext(name, address));
        }
        ServerMarker.setValue(name, address);
        HSEvents.GameJoin.invoker().invoke(new GameJoinContext(name, address));
    }

    @Inject(method = "sendChat", at = @At("HEAD"), cancellable = true)
    public void sendChat(String content, CallbackInfo ci, @Local(ordinal = 0, argsOnly = true) LocalRef<String> msg) {
        var context = new MessageSendContext(content);
        HSEvents.MessageSend.invoker().invoke(context);
        if (context.isCancelled()) {
            ci.cancel();
            return;
        }
        msg.set(ChatInjectHandler.handle(context.message));
    }

    @Inject(method = "sendCommand", at = @At("HEAD"), cancellable = true)
    public void sendCommand(String command, CallbackInfo ci, @Local(ordinal = 0, argsOnly = true) LocalRef<String> cmd) {
        var context = new CommandSendContext(command);
        HSEvents.CommandSend.invoker().invoke(context);
        if (context.isCancelled()) {
            ci.cancel();
            return;
        }
        cmd.set(context.command);
    }

    @Inject(method = "sendUnattendedCommand", at = @At("HEAD"), cancellable = true)
    public void sendUnattendedCommand(String command, Screen previousScreen, CallbackInfo ci, @Local(ordinal = 0, argsOnly = true) LocalRef<String> cmd) {
        var context = new CommandSendContext(command);
        HSEvents.CommandSend.invoker().invoke(context);
        if (context.isCancelled()) {
            ci.cancel();
            return;
        }
        cmd.set(context.command);
    }

    @Inject(method = "openCommandSendConfirmationWindow", at = @At("HEAD"), cancellable = true)
    public void openCommandSendConfirmationWindow(String command, String titleKey, Screen previousScreen, CallbackInfo ci, @Local(ordinal = 0, argsOnly = true) LocalRef<String> cmd) {
        var context = new CommandSendContext(command);
        HSEvents.CommandSend.invoker().invoke(context);
        if (context.isCancelled()) {
            ci.cancel();
            return;
        }
        cmd.set(context.command);
    }

    @Inject(method = "handleRespawn", at = @At("RETURN"))
    public void handleRespawn(ClientboundRespawnPacket packet, CallbackInfo ci) {
        if (PlayerDeathContext.isDead()) {
            PlayerDeathContext.setDead(false);
            HSEvents.PlayerRespawn.invoker().invoke(PlayerRespawnContext.INSTANCE);
        }
    }

    @Inject(method = "handlePlayerCombatKill", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;shouldShowDeathScreen()Z", shift = At.Shift.AFTER))
    public void handlePlayerCombatKill(ClientboundPlayerCombatKillPacket packet, CallbackInfo ci, @Local(name = "player") Entity player) {
        if (player == Minecraft.getInstance().player) {
            String message = packet.message().getString().replaceAll("(§).", "");
            PlayerDeathContext.setDead(true);
            HSEvents.PlayerDeath.invoker().invoke(new PlayerDeathContext(message));
        }
    }

}
