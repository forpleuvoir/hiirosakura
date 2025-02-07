package moe.forpleuvoir.hiirosakura.mixin.client;

import moe.forpleuvoir.hiirosakura.functional.event.events.BreakBlockEvent;
import moe.forpleuvoir.hiirosakura.functional.gameplay.BlockBreakProtection;
import moe.forpleuvoir.hiirosakura.functional.gameplay.CameraSwitcher;
import moe.forpleuvoir.hiirosakura.functional.script.deobfuscation.HSBlockState;
import moe.forpleuvoir.nebula.event.EventBus;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {


    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "attackBlock", at = @At(value = "HEAD"), cancellable = true)
    public void hiirosakura$attackBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        hiirosakura$breakBlockEvent(pos, direction, cir);
    }

    @Inject(method = "updateBlockBreakingProgress", at = @At(value = "HEAD"), cancellable = true)
    public void hiirosakura$updateBlockBreakingProgress(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        hiirosakura$breakBlockEvent(pos, direction, cir);
    }

    @Unique
    private void hiirosakura$breakBlockEvent(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (client.world != null) {
            if (!BlockBreakProtection.canBreak(client.world.getBlockState(pos), pos)) {
                cir.setReturnValue(false);
                cir.cancel();
                return;
            }
            BreakBlockEvent event = new BreakBlockEvent(new HSBlockState(client.world.getBlockState(pos)), direction.name());
            EventBus.Companion.broadcast(event);
            if (event.getCanceled() || CameraSwitcher.getShouldBlockActions()) {
                cir.setReturnValue(false);
                cir.cancel();
            }
        }
    }


}
