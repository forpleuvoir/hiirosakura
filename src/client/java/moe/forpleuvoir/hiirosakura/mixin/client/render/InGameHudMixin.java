package moe.forpleuvoir.hiirosakura.mixin.client.render;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import moe.forpleuvoir.hiirosakura.functional.renderaddons.RenderInfoAddon;
import moe.forpleuvoir.hiirosakura.functional.renderaddons.ShowEnchantmentWhenSwitch;
import moe.forpleuvoir.ibukigourd.gui.base.render.IGDrawContext;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static moe.forpleuvoir.hiirosakura.functional.renderaddons.RenderEnchantmentWhenSwitchKt.renderEnchantmentWhenSwitch;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow
    private ItemStack currentStack;

    @Shadow
    public abstract TextRenderer getTextRenderer();

    @Inject(
        method = "renderHeldItemTooltip",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithBackground(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIII)I",
            ordinal = 0,
            shift = At.Shift.AFTER
        )
    )
    public void hiirosakura$renderHeldItemTooltip(DrawContext context, CallbackInfo ci, @Local(ordinal = 3) int alpha, @Local(ordinal = 2) int y) {
        renderEnchantmentWhenSwitch(y, currentStack, getTextRenderer(), IGDrawContext.Companion.toIGDrawContext(context), alpha);
    }

    @ModifyExpressionValue(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", ordinal = 1))
    public boolean hiirosakura$tick(boolean original, @Local(ordinal = 0) ItemStack stack) {
        if (ShowEnchantmentWhenSwitch.INSTANCE.getEnable().getValue()) {
            return original || stack != currentStack;
        }
        return original;
    }

    @Inject(method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V", at = @At("HEAD"), cancellable = true)
    public void hiirosakura$renderScoreboardSidebar(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (RenderInfoAddon.INSTANCE.getDisableScoreboardSidebarRender().getValue()) ci.cancel();
    }

}
