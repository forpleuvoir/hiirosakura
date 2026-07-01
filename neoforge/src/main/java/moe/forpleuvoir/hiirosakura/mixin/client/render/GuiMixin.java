package moe.forpleuvoir.hiirosakura.mixin.client.render;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import moe.forpleuvoir.hiirosakura.functional.renderaddons.HeldItemRenderAddon;
import moe.forpleuvoir.hiirosakura.functional.renderaddons.RenderInfoAddon;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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
            method = "extractSelectedItemName(Lnet/minecraft/client/gui/GuiGraphicsExtractor;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;textWithBackdrop(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIII)V"
            )
    )
    public void renderSelectedItemName(GuiGraphicsExtractor guiGraphics, Font font, Component str, int textX, int textY, int textWidth, int textColor, @Local(name = "alpha") int alpha) {
        if (!HeldItemRenderAddon.INSTANCE.getEnable().getEnabled()) {
            guiGraphics.textWithBackdrop(font, str, textX, textY, textWidth, textColor);
        } else {
            HeldItemRenderAddon.render(guiGraphics, font, textY, alpha, lastToolHighlight);
        }
    }

    @ModifyExpressionValue(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z", ordinal = 1))
    public boolean tick(boolean original, @Local(name = "selected") ItemStack selected) {
        return original || HeldItemRenderAddon.shouldRender(selected, lastToolHighlight);
    }

    @Inject(method = "displayScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    public void displayScoreboardSidebar(GuiGraphicsExtractor graphics, Objective objective, CallbackInfo ci) {
        if (RenderInfoAddon.INSTANCE.getDisableScoreboardSidebarRender().getEnabled()) ci.cancel();
    }

}
