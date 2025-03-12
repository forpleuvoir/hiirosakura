package moe.forpleuvoir.hiirosakura.mixin.client;

import moe.forpleuvoir.hiirosakura.functional.gameplay.ItemDropIntercept;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryScreenMixin extends HandledScreen<CreativeInventoryScreen.CreativeScreenHandler> {

    public CreativeInventoryScreenMixin(CreativeInventoryScreen.CreativeScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Inject(method = "onMouseClick", at = @At("HEAD"), cancellable = true)
    public void hiirosakura$onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci) {
        if (actionType == SlotActionType.PICKUP && (slot == null || slotId == -999)) {
            if (!ItemDropIntercept.canDrop(this.handler.getCursorStack())) {
                ci.cancel();
            }
        }

        if (actionType == SlotActionType.THROW) {
            ItemStack itemStack = slot == null ? ItemStack.EMPTY : this.handler.getSlot(slot.id).getStack();
            if (itemStack.isEmpty()) {
                itemStack = this.handler.getCursorStack();
            }
            if (itemStack.isEmpty()) {
                if (slot != null) {
                    itemStack = slot.getStack();
                }
            }
            if (!ItemDropIntercept.canDrop(itemStack)) {
                ci.cancel();
            }
        }
    }


}
