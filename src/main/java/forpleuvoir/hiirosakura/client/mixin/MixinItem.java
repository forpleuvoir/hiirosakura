package forpleuvoir.hiirosakura.client.mixin;

import forpleuvoir.hiirosakura.client.config.HiiroSakuraDatas;
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
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.mixin
 * <p>#class_name MixinItem
 * <p>#create_time 2021/6/17 21:50
 */
@Mixin(Item.class)
public abstract class MixinItem {

    @Inject(method = "appendTooltip", at = @At("HEAD"))
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context,
                              CallbackInfo callbackInfo
    ) {
        tooltip.addAll(HiiroSakuraDatas.TOOLTIP.getTooltip(stack));
    }
}
