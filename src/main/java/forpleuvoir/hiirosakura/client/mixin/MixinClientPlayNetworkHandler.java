package forpleuvoir.hiirosakura.client.mixin;

import forpleuvoir.hiirosakura.client.feature.event.OnGameJoinEvent;
import forpleuvoir.hiirosakura.client.feature.event.OnServerJoinEvent;
import forpleuvoir.hiirosakura.client.feature.event.base.EventBus;
import forpleuvoir.hiirosakura.client.util.ServerInfoUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.mixin
 * <p>#class_name MixinClientPlayNetworkHandler
 * <p>#create_time 2021-07-23 13:39
 */
@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler {


    @Inject(method = "onGameJoin", at = @At("RETURN"))
    public void onGameJoin(CallbackInfo callbackInfo) {
        ServerInfo serverInfo = MinecraftClient.getInstance().getCurrentServerEntry();
        var name = serverInfo != null ? serverInfo.name : null;
        var address = serverInfo != null ? serverInfo.address : null;
        if (name != null)
            if (!name.equals(ServerInfoUtil.getName())) {
                ServerInfoUtil.setValue(name, address);
                EventBus.broadcast(new OnServerJoinEvent(name, address));
            }
        ServerInfoUtil.setValue(name, address);
        EventBus.broadcast(new OnGameJoinEvent(name, address));
    }



}
