package moe.forpleuvoir.hiirosakura.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import moe.forpleuvoir.hiirosakura.functional.chataddons.ChatFilterHandler;
import moe.forpleuvoir.hiirosakura.functional.chataddons.ChatInjectHandler;
import moe.forpleuvoir.hiirosakura.functional.chataddons.chatbubble.ChatBubbleHandler;
import moe.forpleuvoir.hiirosakura.functional.event.events.*;
import moe.forpleuvoir.hiirosakura.functional.misc.ServerMarker;
import moe.forpleuvoir.nebula.event.EventBus;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {

    @Inject(method = "onChatMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/ChatMessageS2CPacket;serializedParameters()Lnet/minecraft/network/message/MessageType$Parameters;", ordinal = 0))
    public void hiirosakura$onChatMessageBranch1(ChatMessageS2CPacket packet, CallbackInfo ci, @Local UUID uuid) {
        var parameters = packet.serializedParameters();
        var text = parameters.applyChatDecoration(MessageHandler.VALIDATION_ERROR_TEXT);
        if (!ChatFilterHandler.shouldFilter(text)) {
            ChatBubbleHandler.addChatBubble(text, uuid, null);
        }
    }

    @Inject(method = "onChatMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/message/MessageVerifier;ensureVerified(Lnet/minecraft/network/message/SignedMessage;)Lnet/minecraft/network/message/SignedMessage;", shift = At.Shift.AFTER))
    public void hiirosakura$onChatMessageBranch2(ChatMessageS2CPacket packet, CallbackInfo ci, @Local UUID uuid, @Local PlayerListEntry playerListEntry, @Local SignedMessage message) {
        var parameters = packet.serializedParameters();
        var profile = playerListEntry.getProfile();
        Text text;
        if (message != null) {
            boolean bl = MinecraftClient.getInstance().options.getOnlyShowSecureChat().getValue();
            var signedMessage = bl ? message.withoutUnsigned() : message;
            text = parameters.applyChatDecoration(signedMessage.getContent());
        } else {
            text = parameters.applyChatDecoration(MessageHandler.VALIDATION_ERROR_TEXT);
        }

        if (!ChatFilterHandler.shouldFilter(text)) {
            ChatBubbleHandler.addChatBubble(text, uuid, profile);
        }
    }

    @Inject(method = "onGameJoin", at = @At("RETURN"))
    public void hiirosakura$onGameJoin(GameJoinS2CPacket packet, CallbackInfo callbackInfo) {
        ServerInfo serverInfo = MinecraftClient.getInstance().getCurrentServerEntry();
        var name = serverInfo != null ? serverInfo.name : "";
        var address = serverInfo != null ? serverInfo.address : "";
        if (!name.equals(ServerMarker.getName())) {
            ServerMarker.setValue(name, address);
            EventBus.Companion.broadcast(new ServerJoinEvent(name, address));
        }
        ServerMarker.setValue(name, address);
        EventBus.Companion.broadcast(new GameJoinEvent(name, address));
    }

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    public void hiirosakura$sendChatMessageEvent(String content, CallbackInfo ci, @Local(ordinal = 0, argsOnly = true) LocalRef<String> msg) {
        var event = new MessageSendEvent(content);
        EventBus.Companion.broadcast(event);
        if (event.getCanceled()) {
            ci.cancel();
            return;
        }
        msg.set(ChatInjectHandler.handle(event.message));
    }

    @Inject(method = "sendCommand", at = @At("HEAD"), cancellable = true)
    public void hiirosakura$sendCommand(String command, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 0, argsOnly = true) LocalRef<String> cmd) {
        var event = new CommandSendEvent(command);
        EventBus.Companion.broadcast(event);
        if (event.getCanceled()) {
            cir.setReturnValue(false);
            return;
        }
        cmd.set(event.command);
    }

    @Inject(method = "sendChatCommand", at = @At("HEAD"), cancellable = true)
    public void hiirosakura$sendChatCommand(String command, CallbackInfo ci, @Local(ordinal = 0, argsOnly = true) LocalRef<String> cmd) {
        var event = new CommandSendEvent(command);
        EventBus.Companion.broadcast(event);
        if (event.getCanceled()) {
            ci.cancel();
            return;
        }
        cmd.set(event.command);
    }

    @Inject(method = "onPlayerRespawn", at = @At("RETURN"))
    public void hiirosakura$onPlayerRespawn(PlayerRespawnS2CPacket packet, CallbackInfo callbackInfo) {
        if (PlayerDeathEvent.isDead()) {
            PlayerDeathEvent.setDead(false);
            EventBus.Companion.broadcast(PlayerRespawnEvent.INSTANCE);
        }
    }

    @Inject(method = "onDeathMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;showsDeathScreen()Z", shift = At.Shift.AFTER))
    public void hiirosakura$onDeathMessage(DeathMessageS2CPacket packet, CallbackInfo ci, @Local Entity player) {
        if (player == MinecraftClient.getInstance().player) {
            String message = packet.message().getString().replaceAll("(§).", "");
            PlayerDeathEvent.setDead(true);
            EventBus.Companion.broadcast(new PlayerDeathEvent(message));
        }
    }

}
