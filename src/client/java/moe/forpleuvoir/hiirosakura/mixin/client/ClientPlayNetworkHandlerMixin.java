package moe.forpleuvoir.hiirosakura.mixin.client;

import moe.forpleuvoir.hiirosakura.functional.chataddons.ChatInjectHandler;
import moe.forpleuvoir.hiirosakura.functional.misc.ServerMarker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Inject(method = "onGameJoin", at = @At("RETURN"))
    public void hiirosakura$onGameJoin(GameJoinS2CPacket packet, CallbackInfo callbackInfo) {
        ServerInfo serverInfo = MinecraftClient.getInstance().getCurrentServerEntry();
        var name = serverInfo != null ? serverInfo.name : null;
        var address = serverInfo != null ? serverInfo.address : null;
        if (name != null)
            if (!name.equals(ServerMarker.getName())) {
                ServerMarker.setValue(name, address);
            }
        ServerMarker.setValue(name, address);
    }

    @ModifyVariable(method = "sendChatMessage", at = @At("HEAD"), argsOnly = true)
    public String hiirosakura$sendCatMessage(String value) {
        return ChatInjectHandler.handle(value);
    }

}
