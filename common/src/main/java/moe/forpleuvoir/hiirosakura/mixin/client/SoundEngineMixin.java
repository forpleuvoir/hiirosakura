package moe.forpleuvoir.hiirosakura.mixin.client;

import moe.forpleuvoir.hiirosakura.functional.event.events.HSEvents;
import moe.forpleuvoir.hiirosakura.functional.event.events.SoundPlayContext;
import moe.forpleuvoir.hiirosakura.functional.gameplay.SoundEventFilter;
import moe.forpleuvoir.hiirosakura.functional.script.deobfuscation.HSSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundEngine.class)
public abstract class SoundEngineMixin {

    @Inject(method = "play", at = @At("HEAD"), cancellable = true)
    public void play(SoundInstance instance, CallbackInfoReturnable<SoundEngine.PlayResult> cir) {
        var context = new SoundPlayContext(new HSSoundInstance(instance));
        HSEvents.SoundPlay.invoker().invoke(context);
        if (context.isCancelled() || SoundEventFilter.shouldFilter(instance)) {
            cir.setReturnValue(SoundEngine.PlayResult.NOT_STARTED);
            cir.cancel();
        }
    }


}
