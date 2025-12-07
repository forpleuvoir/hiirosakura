package moe.forpleuvoir.hiirosakura.mixin.client;

import moe.forpleuvoir.hiirosakura.functional.event.events.DisconnectEvent;
import moe.forpleuvoir.hiirosakura.functional.misc.ServerMarker;
import moe.forpleuvoir.nebula.event.EventBus;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DisconnectedScreen.class)
public abstract class DisconnectedScreenMixin {

    @Inject(method = "<init>(Lnet/minecraft/client/gui/screens/Screen;Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/Component;)V", at = @At("RETURN"))
    public void init(Screen parent, Component title, Component reason, CallbackInfo ci) {
        ServerMarker.clear();
        EventBus.Companion.broadcast(
            new DisconnectEvent(
                ServerMarker.getLastServerName(),
                ServerMarker.getLastServerAddress(),
                title.getString(),
                reason.getString()
            )
        );
    }

}
