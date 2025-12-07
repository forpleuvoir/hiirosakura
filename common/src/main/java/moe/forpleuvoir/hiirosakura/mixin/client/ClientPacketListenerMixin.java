package moe.forpleuvoir.hiirosakura.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import moe.forpleuvoir.hiirosakura.functional.chataddons.ChatFilterHandler;
import moe.forpleuvoir.hiirosakura.functional.chataddons.ChatInjectHandler;
import moe.forpleuvoir.hiirosakura.functional.chataddons.chatbubble.ChatBubbleHandler;
import moe.forpleuvoir.hiirosakura.functional.event.events.*;
import moe.forpleuvoir.hiirosakura.functional.misc.ServerMarker;
import moe.forpleuvoir.nebula.event.EventBus;
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
    public void handlePlayerChat(ClientboundPlayerChatPacket packet, CallbackInfo ci, @Local UUID uuid) {
        var parameters = packet.chatType();
        var text = parameters.decorate(ChatListener.CHAT_VALIDATION_ERROR);
        if (!ChatFilterHandler.shouldFilter(text)) {
            ChatBubbleHandler.addChatBubble(text, uuid, null);
        }
    }

    @Inject(method = "handlePlayerChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/SignedMessageValidator;updateAndValidate(Lnet/minecraft/network/chat/PlayerChatMessage;)Lnet/minecraft/network/chat/PlayerChatMessage;", shift = At.Shift.AFTER))
    public void handlePlayerChat(ClientboundPlayerChatPacket packet, CallbackInfo ci, @Local UUID uuid, @Local PlayerInfo playerInfo, @Local PlayerChatMessage message) {
        var parameters = packet.chatType();
        var profile = playerInfo.getProfile();
        Component text;
        if (message != null) {
            boolean bl = Minecraft.getInstance().options.onlyShowSecureChat().get();
            var signedMessage = bl ? message.removeUnsignedContent() : message;
            text = parameters.decorate(signedMessage.decoratedContent());
        } else {
            text = parameters.decorate(ChatListener.CHAT_VALIDATION_ERROR);
        }

        if (!ChatFilterHandler.shouldFilter(text)) {
            ChatBubbleHandler.addChatBubble(text, uuid, profile);
        }
    }

    @Inject(method = "handleLogin", at = @At("RETURN"))
    public void handleLogin(ClientboundLoginPacket packet, CallbackInfo ci) {
        ServerData serverInfo = Minecraft.getInstance().getCurrentServer();
        var name = serverInfo != null ? serverInfo.name : "";
        var address = serverInfo != null ? serverInfo.ip : "";
        if (!name.equals(ServerMarker.getName())) {
            ServerMarker.setValue(name, address);
            EventBus.Companion.broadcast(new ServerJoinEvent(name, address));
        }
        ServerMarker.setValue(name, address);
        EventBus.Companion.broadcast(new GameJoinEvent(name, address));
    }

    @Inject(method = "sendChat", at = @At("HEAD"), cancellable = true)
    public void sendChat(String content, CallbackInfo ci, @Local(ordinal = 0, argsOnly = true) LocalRef<String> msg) {
        var event = new MessageSendEvent(content);
        EventBus.Companion.broadcast(event);
        if (event.getCanceled()) {
            ci.cancel();
            return;
        }
        msg.set(ChatInjectHandler.handle(event.message));
    }

    @Inject(method = "sendCommand", at = @At("HEAD"), cancellable = true)
    public void sendCommand(String command, CallbackInfo ci, @Local(ordinal = 0, argsOnly = true) LocalRef<String> cmd) {
        var event = new CommandSendEvent(command);
        EventBus.Companion.broadcast(event);
        if (event.getCanceled()) {
            ci.cancel();
            return;
        }
        cmd.set(event.command);
    }

    @Inject(method = "sendUnattendedCommand", at = @At("HEAD"), cancellable = true)
    public void sendUnattendedCommand(String command, Screen previousScreen, CallbackInfo ci, @Local(ordinal = 0, argsOnly = true) LocalRef<String> cmd) {
        var event = new CommandSendEvent(command);
        EventBus.Companion.broadcast(event);
        if (event.getCanceled()) {
            ci.cancel();
            return;
        }
        cmd.set(event.command);
    }

    @Inject(method = "openCommandSendConfirmationWindow", at = @At("HEAD"), cancellable = true)
    public void openCommandSendConfirmationWindow(String command, String titleKey, Screen previousScreen, CallbackInfo ci, @Local(ordinal = 0, argsOnly = true) LocalRef<String> cmd) {
        var event = new CommandSendEvent(command);
        EventBus.Companion.broadcast(event);
        if (event.getCanceled()) {
            ci.cancel();
            return;
        }
        cmd.set(event.command);
    }

    @Inject(method = "handleRespawn", at = @At("RETURN"))
    public void handleRespawn(ClientboundRespawnPacket packet, CallbackInfo ci) {
        if (PlayerDeathEvent.isDead()) {
            PlayerDeathEvent.setDead(false);
            EventBus.Companion.broadcast(PlayerRespawnEvent.INSTANCE);
        }
    }

    @Inject(method = "handlePlayerCombatKill", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;shouldShowDeathScreen()Z", shift = At.Shift.AFTER))
    public void handlePlayerCombatKill(ClientboundPlayerCombatKillPacket packet, CallbackInfo ci, @Local Entity player) {
        if (player == Minecraft.getInstance().player) {
            String message = packet.message().getString().replaceAll("(§).", "");
            PlayerDeathEvent.setDead(true);
            EventBus.Companion.broadcast(new PlayerDeathEvent(message));
        }
    }

}
