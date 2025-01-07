package moe.forpleuvoir.hiirosakura.mixin.client;

import moe.forpleuvoir.hiirosakura.functional.event.events.SoundPlayEvent;
import moe.forpleuvoir.hiirosakura.functional.gameplay.SoundEffectFilter;
import moe.forpleuvoir.hiirosakura.functional.script.deobfuscation.HSSoundInstance;
import moe.forpleuvoir.nebula.event.EventBus;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundSystem.class)
public class SoundSystemMixin {

    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At("HEAD"), cancellable = true)
    public void play(SoundInstance sound, CallbackInfo ci) {
        var event = new SoundPlayEvent(new HSSoundInstance(sound));
        EventBus.Companion.broadcast(event);
        if (event.getCanceled() || SoundEffectFilter.shouldFilter(sound)) {
            ci.cancel();
        }
    }


}
