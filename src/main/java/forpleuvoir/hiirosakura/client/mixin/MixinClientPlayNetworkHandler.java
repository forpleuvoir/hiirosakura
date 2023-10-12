package forpleuvoir.hiirosakura.client.mixin;

import forpleuvoir.hiirosakura.client.feature.chatmessage.ChatMessageInject;
import forpleuvoir.hiirosakura.client.feature.event.events.*;
import forpleuvoir.hiirosakura.client.util.ServerInfoUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author forpleuvoir
 * <p>项目名 hiirosakura
 * <p>包名 forpleuvoir.hiirosakura.client.mixin
 * <p>文件名 MixinClientPlayNetworkHandler
 * <p>创建时间 2021-07-23 13:39
 */
@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler {


	@Shadow
	@Final
	private MinecraftClient client;

	@Shadow
	private ClientWorld world;

	@Inject(method = "onGameJoin", at = @At("RETURN"))
	public void onGameJoin(GameJoinS2CPacket packet, CallbackInfo callbackInfo) {
		ServerInfo serverInfo = MinecraftClient.getInstance().getCurrentServerEntry();
		var name = serverInfo != null ? serverInfo.name : null;
		var address = serverInfo != null ? serverInfo.address : null;
		if (name != null)
			if (!name.equals(ServerInfoUtil.getName())) {
				ServerInfoUtil.setValue(name, address);
				new ServerJoinEvent(name, address).broadcast();
			}
		ServerInfoUtil.setValue(name, address);
		new GameJoinEvent(name, address).broadcast();
	}

	/**
	 * 客户端玩家发送聊天消息时
	 *
	 * @param message 客户端准备发送的消息 {@link String}
	 */
	@ModifyVariable(method = "sendChatMessage", at = @At(value = "HEAD"), argsOnly = true)
	public String sendChatMessage(String message) {
		var messageEvent = new MessageSendEvent(message);
		messageEvent.broadcast();
		if (messageEvent.isCanceled()) return "";
		return ChatMessageInject.handlerMessage(messageEvent.message);
	}

	@Inject(method = "onPlayerRespawn", at = @At("RETURN"))
	public void onPlayerRespawn(PlayerRespawnS2CPacket packet, CallbackInfo callbackInfo) {
		if (PlayerDeathEventKt.isDeath()) {
			PlayerDeathEventKt.setDeath(false);
			new PlayerRespawnEvent().broadcast();
		}
	}

	@Inject(method = "onDeathMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/DeathMessageS2CPacket;getEntityId()I", shift = At.Shift.AFTER))
	public void onDeathMessage(DeathMessageS2CPacket packet, CallbackInfo callbackInfo) {
		Entity entity = this.world.getEntityById(packet.getEntityId());
		if (entity == this.client.player) {
            Entity killer = this.world.getEntityById(packet.getEntityId());
			String message = packet.getMessage().getString().replaceAll("(§).", "");
			String name = "";
			if (killer != null) {
				name = killer.getDisplayName().getString();
			}
			PlayerDeathEventKt.setDeath(true);
			new PlayerDeathEvent(name, message).broadcast();
		}
	}


}
