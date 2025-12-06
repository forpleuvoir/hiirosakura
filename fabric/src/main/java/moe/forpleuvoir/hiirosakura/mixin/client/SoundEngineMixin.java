package moe.forpleuvoir.hiirosakura.mixin.client;

import moe.forpleuvoir.hiirosakura.functional.event.events.SoundPlayEvent;
import moe.forpleuvoir.hiirosakura.functional.gameplay.SoundEventFilter;
import moe.forpleuvoir.hiirosakura.functional.script.deobfuscation.HSSoundInstance;
import moe.forpleuvoir.nebula.event.EventBus;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundEngine.class)
public abstract class SoundEngineMixin {

    @Inject(method = "play", at = @At("HEAD"), cancellable = true)
    public void play(SoundInstance sound, CallbackInfoReturnable<SoundEngine.PlayResult> cir) {
        var event = new SoundPlayEvent(new HSSoundInstance(sound));
        EventBus.Companion.broadcast(event);
        if (event.getCanceled() || SoundEventFilter.shouldFilter(sound)) {
            cir.setReturnValue(SoundEngine.PlayResult.NOT_STARTED);
            cir.cancel();
        }
    }


}
