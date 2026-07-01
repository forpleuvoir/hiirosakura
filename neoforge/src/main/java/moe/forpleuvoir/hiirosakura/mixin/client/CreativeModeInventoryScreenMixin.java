package moe.forpleuvoir.hiirosakura.mixin.client;

import moe.forpleuvoir.hiirosakura.functional.gameplay.ItemDropIntercept;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryScreenMixin extends AbstractContainerScreen<CreativeModeInventoryScreen.ItemPickerMenu> {

    public CreativeModeInventoryScreenMixin(CreativeModeInventoryScreen.ItemPickerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Inject(method = "slotClicked", at = @At("HEAD"), cancellable = true)
    public void slotClicked(Slot slot, int slotId, int buttonNum, ContainerInput containerInput, CallbackInfo ci) {
        if (containerInput == ContainerInput.PICKUP && (slot == null || slotId == -999)) {
            if (!ItemDropIntercept.canDrop(this.menu.getCarried())) {
                ci.cancel();
            }
        }

        if (containerInput == ContainerInput.THROW) {
            ItemStack itemStack = slot == null ? ItemStack.EMPTY : this.menu.getSlot(slot.index).getItem();
            if (itemStack.isEmpty()) {
                itemStack = this.menu.getCarried();
            }
            if (itemStack.isEmpty()) {
                if (slot != null) {
                    itemStack = slot.getItem();
                }
            }
            if (!ItemDropIntercept.canDrop(itemStack)) {
                ci.cancel();
            }
        }
    }


}
