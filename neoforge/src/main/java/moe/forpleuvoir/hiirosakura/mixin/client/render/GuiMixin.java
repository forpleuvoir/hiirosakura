package moe.forpleuvoir.hiirosakura.mixin.client.render;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import moe.forpleuvoir.hiirosakura.functional.renderaddons.HeldItemRenderAddon;
import moe.forpleuvoir.hiirosakura.functional.renderaddons.RenderInfoAddon;
import moe.forpleuvoir.ibukigourd.gui.base.render.IGGuiGraphics;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.scores.Objective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Shadow
    private ItemStack lastToolHighlight;

    @Redirect(
            method = "renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;drawStringWithBackdrop(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIII)V"
            )
    )
    public void renderSelectedItemName(GuiGraphics guiGraphics, Font font, Component text, int x, int y, int width, int color, @Local(ordinal = 3) int alpha) {
        if (!HeldItemRenderAddon.INSTANCE.getEnable().getValue()) {
            guiGraphics.drawStringWithBackdrop(font, text, x, y, width, color);
        } else {
            HeldItemRenderAddon.render(IGGuiGraphics.Companion.toIGGUIGraphics(guiGraphics), font, y, alpha, lastToolHighlight);
        }
    }

    @ModifyExpressionValue(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z", ordinal = 1))
    public boolean tick(boolean original, @Local(ordinal = 0) ItemStack stack) {
        return original || HeldItemRenderAddon.shouldRender(stack, lastToolHighlight);
    }

    @Inject(method = "displayScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    public void displayScoreboardSidebar(GuiGraphics guiGraphics, Objective objective, CallbackInfo ci) {
        if (RenderInfoAddon.INSTANCE.getDisableScoreboardSidebarRender().getValue()) ci.cancel();
    }

}
