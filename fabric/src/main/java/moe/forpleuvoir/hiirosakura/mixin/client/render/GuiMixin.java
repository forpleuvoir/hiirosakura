package moe.forpleuvoir.hiirosakura.mixin.client.render;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import moe.forpleuvoir.hiirosakura.functional.renderaddons.HeldItemRenderAddon;
import moe.forpleuvoir.hiirosakura.functional.renderaddons.RenderInfoAddon;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.scores.Objective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 选中物品名（tool highlight）与侧边栏的显示控制。
 *
 * Minecraft 26.2 把这一整套从 {@code Gui} 搬到了 {@link Hud}：
 * 字段 {@code lastToolHighlight} / {@code toolHighlightTimer}、方法 {@code extractSelectedItemName}、
 * {@code displayScoreboardSidebar} 现在都在 {@code Hud} 上（{@code Gui} 只剩 {@code hud} 字段与
 * {@code extractRenderState} 这层壳）。因此注入目标改为 {@code Hud}。
 *
 * 另一个 26.2 的变化：Minecraft 的类文件不再携带局部变量表，MixinExtras 的 {@code @Local}
 * 无法再按名字取局部变量，只能按类型匹配；原先用 {@code @Local(name = "alpha")} 取的透明度
 * 改为按原版公式从 {@code toolHighlightTimer} 复算。
 */
@Mixin(Hud.class)
public abstract class GuiMixin {

    @Shadow
    private ItemStack lastToolHighlight;

    @Shadow
    private int toolHighlightTimer;

    @Redirect(
            method = "extractSelectedItemName",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;textWithBackdrop(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIII)V"
            )
    )
    public void renderSelectedItemName(GuiGraphicsExtractor guiGraphics, Font font, Component str, int textX, int textY, int textWidth, int textColor) {
        if (!HeldItemRenderAddon.INSTANCE.getEnable().getEnabled()) {
            guiGraphics.textWithBackdrop(font, str, textX, textY, textWidth, textColor);
        } else {
            // 原版计算：alpha = (int) (toolHighlightTimer * 256.0f / 10.0f)，上限 255
            int alpha = (int) (toolHighlightTimer * 256.0F / 10.0F);
            if (alpha > 255) alpha = 255;
            HeldItemRenderAddon.render(guiGraphics, font, textY, alpha, lastToolHighlight);
        }
    }

    @ModifyExpressionValue(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z", ordinal = 1))
    public boolean tick(boolean original, @Local ItemStack selected) {
        return original || HeldItemRenderAddon.shouldRender(selected, lastToolHighlight);
    }

    @Inject(method = "displayScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    public void displayScoreboardSidebar(GuiGraphicsExtractor graphics, Objective objective, CallbackInfo ci) {
        if (RenderInfoAddon.INSTANCE.getDisableScoreboardSidebarRender().getEnabled()) ci.cancel();
    }

}
