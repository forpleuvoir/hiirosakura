package forpleuvoir.hiirosakura.client.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import forpleuvoir.hiirosakura.client.config.Configs;
import forpleuvoir.hiirosakura.client.config.HiiroSakuraDatas;
import forpleuvoir.hiirosakura.client.util.ItemStackUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedList;

/**
 * GameHud注入
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.mixin
 * <p>#class_name MixinInGameHud
 * <p>#create_time 2021/6/13 15:10
 */
@Mixin(InGameHud.class)
public abstract class MixinInGameHud {
    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private int heldItemTooltipFade;

    @Shadow
    private ItemStack currentStack;

    @Shadow
    public abstract TextRenderer getFontRenderer();

    @Shadow
    private int scaledWidth;

    @Shadow
    private int scaledHeight;

    @Inject(method = "renderHeldItemTooltip", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/option/GameOptions;getTextBackgroundColor(I)I",
            ordinal = 0, shift = At.Shift.AFTER),
            cancellable = true
    )
    public void renderHeldItemTooltip(MatrixStack matrices, CallbackInfo ci) {
        int k = this.scaledHeight - 59;
        assert this.client.interactionManager != null;
        if (!this.client.interactionManager.hasStatusBars()) {
            k += 14;
        }
        int l = (int) ((float) this.heldItemTooltipFade * 256.0F / 10.0F);
        if (l > 255) {
            l = 255;
        }
        LinkedList<Text> mutableTexts = new LinkedList<>();
        if (Configs.Toggles.SHOW_ENCHANTMENT.getBooleanValue()) {
            //添加附魔文本
            mutableTexts.addAll(ItemStackUtil.getEnchantmentsWithLvl(currentStack, Formatting.DARK_AQUA));
        }
        if (Configs.Toggles.SHOW_TOOLTIP_ON_ITEM_TOGGLE.getBooleanValue()) {
            mutableTexts.addAll(HiiroSakuraDatas.TOOLTIP.getTooltip(currentStack));
        }
        //这一段我自己都看不明白了 总之这么写就对了
        int count = 0;
        int padding = 10;
        int size = mutableTexts.size();
        int newK = k - ((size - 1) * padding) - padding;
        for (Text e : mutableTexts) {
            int a = this.getFontRenderer().getWidth(e);
            int b = (this.scaledWidth - a) / 2;
            this.getFontRenderer()
                .drawWithShadow(matrices, e, (float) b, (float) newK + (count * padding),
                                16777215 + (l << 24)
                );
            count++;
        }
        RenderSystem.disableBlend();
    }
}
