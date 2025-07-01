package moe.forpleuvoir.hiirosakura.mixin.client;

import moe.forpleuvoir.hiirosakura.functional.event.events.DisconnectEvent;
import moe.forpleuvoir.hiirosakura.functional.misc.ServerMarker;
import moe.forpleuvoir.nebula.event.EventBus;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DisconnectedScreen.class)
public abstract class DisconnectedScreenMixin {

    @Inject(method = "<init>(Lnet/minecraft/client/gui/screen/Screen;Lnet/minecraft/text/Text;Lnet/minecraft/text/Text;)V", at = @At("RETURN"))
    public void hiirosakura$init(Screen parent, Text title, Text reason, CallbackInfo ci) {
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
