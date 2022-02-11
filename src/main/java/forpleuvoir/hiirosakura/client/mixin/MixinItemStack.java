package forpleuvoir.hiirosakura.client.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * 项目名 hiirosakura
 * <p>
 * 包名 forpleuvoir.hiirosakura.client.mixin
 * <p>
 * 文件名 MixinItemStack
 * <p>
 * 创建时间 2022/2/12 2:06
 *
 * @author forpleuvoir
 */
@Mixin(ItemStack.class)
public abstract class MixinItemStack {

    @Shadow
    public abstract Item getItem();

    @Shadow
    @Nullable
    public abstract NbtCompound getNbt();


}
