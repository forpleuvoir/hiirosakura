package moe.forpleuvoir.hiirosakura.mixin.client;

import moe.forpleuvoir.hiirosakura.functional.gameplay.ItemDropIntercept;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin {

    @Shadow
    @Final
    public NonNullList<net.minecraft.world.inventory.Slot> slots;

    @Inject(
        method = "doClick",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;safeTake(IILnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/item/ItemStack;", ordinal = 0),
        cancellable = true
    )
    public void doClick(int slotIndex, int buttonNum, ContainerInput containerInput, Player player, CallbackInfo ci) {
        var slot = this.slots.get(slotIndex);
        if (!ItemDropIntercept.canDrop(slot.getItem())) {
            ci.cancel();
        }
    }
}
