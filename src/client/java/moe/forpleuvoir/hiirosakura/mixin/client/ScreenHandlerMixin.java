package moe.forpleuvoir.hiirosakura.mixin.client;

import moe.forpleuvoir.hiirosakura.functional.gameplay.ItemDropIntercept;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandler.class)
public class ScreenHandlerMixin {

    @Shadow
    @Final
    public DefaultedList<Slot> slots;

    @Inject(
        method = "internalOnSlotClick",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/slot/Slot;takeStackRange(IILnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/item/ItemStack;", ordinal = 0),
        cancellable = true
    )
    public void hiirosakura$internalOnSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        var slot = this.slots.get(slotIndex);
        if (!ItemDropIntercept.canDrop(slot.getStack())) {
            ci.cancel();
        }
    }
}
