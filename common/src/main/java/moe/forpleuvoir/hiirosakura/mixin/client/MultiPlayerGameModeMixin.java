package moe.forpleuvoir.hiirosakura.mixin.client;

import moe.forpleuvoir.hiirosakura.functional.event.events.BreakBlockEvent;
import moe.forpleuvoir.hiirosakura.functional.gameplay.BlockBreakProtection;
import moe.forpleuvoir.hiirosakura.functional.gameplay.CameraSwitcher;
import moe.forpleuvoir.hiirosakura.functional.gameplay.ItemDropIntercept;
import moe.forpleuvoir.hiirosakura.functional.gameplay.chaindoors.ChainDoors;
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfo;
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher;
import moe.forpleuvoir.hiirosakura.functional.script.deobfuscation.HSBlockState;
import moe.forpleuvoir.nebula.event.EventBus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
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
    public void startDestroyBlock(BlockPos loc, Direction face, CallbackInfoReturnable<Boolean> cir) {
        hiirosakura$breakBlockEvent(loc, face, cir);
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
            BreakBlockEvent event = new BreakBlockEvent(new HSBlockState(minecraft.level.getBlockState(pos)), direction.name());
            EventBus.Companion.broadcast(event);
            if (event.getCanceled() || CameraSwitcher.getShouldBlockActions()) {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "handleInventoryMouseClick", at = @At(value = "HEAD"), cancellable = true)
    public void handleInventoryMouseClick(int containerId, int slotId, int mouseButton, ClickType clickType, Player player, CallbackInfo ci) {
        if (clickType == ClickType.PICKUP && slotId == -999 && !ItemDropIntercept.canDrop(player.containerMenu.getCarried())) {
            ci.cancel();
        }
    }

    @Inject(method = "useItemOn", at = @At(value = "RETURN", ordinal = 1))
    public void useItemOn(LocalPlayer player, InteractionHand hand, BlockHitResult result, CallbackInfoReturnable<InteractionResult> cir) {
        if (minecraft.level != null || cir.getReturnValue() == InteractionResult.SUCCESS) {
            ChainDoors.onClickedDoor(player, minecraft.level, hand, result, (MultiPlayerGameMode) (Object) this);
        }
    }
}
