package moe.forpleuvoir.hiirosakura.mixin.client;

import moe.forpleuvoir.hiirosakura.functional.event.events.BreakBlockContext;
import moe.forpleuvoir.hiirosakura.functional.event.events.HSEvents;
import moe.forpleuvoir.hiirosakura.functional.gameplay.BlockBreakProtection;
import moe.forpleuvoir.hiirosakura.functional.gameplay.CameraSwitcher;
import moe.forpleuvoir.hiirosakura.functional.gameplay.ItemDropIntercept;
import moe.forpleuvoir.hiirosakura.functional.gameplay.chaindoors.ChainDoors;
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfo;
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher;
import moe.forpleuvoir.hiirosakura.functional.script.deobfuscation.HSBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "startDestroyBlock", at = @At(value = "HEAD"), cancellable = true)
    public void startDestroyBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        hiirosakura$breakBlockEvent(pos, direction, cir);
    }

    @Inject(method = "continueDestroyBlock", at = @At(value = "HEAD"), cancellable = true)
    public void continueDestroyBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        hiirosakura$breakBlockEvent(pos, direction, cir);
    }

    @Unique
    private void hiirosakura$breakBlockEvent(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (minecraft.level != null) {
            if (!BlockBreakProtection.canBreak(ItemStackMatcher.getHandItemStackOrEmpty(), BlockInfo.getTargetBlockInfoOrEmpty())) {
                cir.setReturnValue(false);
                return;
            }
            var context = new BreakBlockContext(new HSBlockState(minecraft.level.getBlockState(pos)), direction.name());
            HSEvents.BreakBlock.invoker().invoke(context);
            if (context.isCancelled() || CameraSwitcher.getShouldBlockActions()) {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "handleContainerInput", at = @At(value = "HEAD"), cancellable = true)
    public void handleContainerInput(int containerId, int slotNum, int buttonNum, ContainerInput containerInput, Player player, CallbackInfo ci) {
        if (containerInput == ContainerInput.PICKUP && slotNum == -999 && !ItemDropIntercept.canDrop(player.containerMenu.getCarried())) {
            ci.cancel();
        }
    }

    @Inject(method = "useItemOn", at = @At(value = "RETURN", ordinal = 1))
    public void useItemOn(LocalPlayer player, InteractionHand hand, BlockHitResult blockHit, CallbackInfoReturnable<InteractionResult> cir) {
        if (minecraft.level != null || cir.getReturnValue() == InteractionResult.SUCCESS) {
            ChainDoors.onClickedDoor(player, minecraft.level, hand, blockHit, (MultiPlayerGameMode) (Object) this);
        }
    }
}
