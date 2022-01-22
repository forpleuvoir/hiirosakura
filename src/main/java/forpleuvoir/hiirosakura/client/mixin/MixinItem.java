package forpleuvoir.hiirosakura.client.mixin;

import forpleuvoir.hiirosakura.client.config.HiiroSakuraData;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * 物品注入
 *
 * @author forpleuvoir
 * <p>项目名 hiirosakura
 * <p>包名 forpleuvoir.hiirosakura.client.mixin
 * <p>文件名 MixinItem
 * <p>创建时间 2021/6/17 21:50
 */
@Mixin(Item.class)
public abstract class MixinItem {

	@Inject(method = "appendTooltip", at = @At("HEAD"))
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context, CallbackInfo callbackInfo) {
		tooltip.addAll(HiiroSakuraData.TOOLTIP.getTooltip(stack));
	}
}
