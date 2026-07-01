package moe.forpleuvoir.hiirosakura.mixin.client;

import moe.forpleuvoir.hiirosakura.functional.event.events.DisconnectContext;
import moe.forpleuvoir.hiirosakura.functional.event.events.HSEvents;
import moe.forpleuvoir.hiirosakura.functional.misc.ServerMarker;
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
        HSEvents.Disconnect.invoker().invoke(
            new DisconnectContext(
                ServerMarker.getLastServerName(),
                ServerMarker.getLastServerAddress(),
                title.getString(),
                reason.getString()
            )
        );
    }

}
