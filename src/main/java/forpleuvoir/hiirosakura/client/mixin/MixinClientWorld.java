package forpleuvoir.hiirosakura.client.mixin;

import forpleuvoir.hiirosakura.client.HiiroSakuraClient;
import net.minecraft.block.Block;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static forpleuvoir.hiirosakura.client.config.Configs.Toggles.ALWAYS_SHOW_BLOCK_MARKER;

/**
 * 项目名 hiirosakura
 * <p>
 * 包名 forpleuvoir.hiirosakura.client.mixin
 * <p>
 * 文件名 MixinClientWorld
 * <p>
 * 创建时间 2022/2/12 1:30
 *
 * @author forpleuvoir
 */
@Mixin(ClientWorld.class)
public abstract class MixinClientWorld {

    @Inject(method = "getBlockParticle", at = @At("HEAD"), cancellable = true)
    public void getBlockParticle(CallbackInfoReturnable<Block> cir) {
        if (ALWAYS_SHOW_BLOCK_MARKER.getValue()) {
            if (HiiroSakuraClient.INSTANCE.getTickCounter() % 2 == 1) {
                cir.setReturnValue(Block.getBlockFromItem(Items.BARRIER));
            } else {
                cir.setReturnValue(Block.getBlockFromItem(Items.LIGHT));
            }
        }
    }
}
