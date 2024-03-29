package forpleuvoir.hiirosakura.client.mixin;

import forpleuvoir.hiirosakura.client.config.Configs;
import forpleuvoir.hiirosakura.client.util.BlockUtilKt;
import forpleuvoir.ibuki_gourd.mod.config.WhiteListMode;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * 项目名 hiirosakura
 * <p>
 * 包名 forpleuvoir.hiirosakura.client.mixin
 * <p>
 * 文件名 MixinClientPlayerInteractionManager
 * <p>
 * 创建时间 2021/11/19 21:13
 *
 * @author forpleuvoir
 */
@Mixin(ClientPlayerInteractionManager.class)
public abstract class MixinClientPlayerInteractionManager {


    @Shadow
    @Final
    private MinecraftClient client;


    @Inject(method = "updateBlockBreakingProgress", at = @At("HEAD"), cancellable = true)
    public void updateBlockBreakingProgress(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> returnable) {
        handleBlackListBlock(pos, returnable);
    }

    private void handleBlackListBlock(BlockPos pos, CallbackInfoReturnable<Boolean> returnable) {
        if (Configs.Toggles.ENABLE_BLOCK_BRAKE_PROTECTION.getValue())
            if (this.client.world != null) {
                BlockState blockState = this.client.world.getBlockState(pos);
                var block = Registries.BLOCK.getId(blockState.getBlock()).toString();
                WhiteListMode mode = (WhiteListMode) Configs.Values.BLOCK_BRAKE_PROTECTION_MODE.getValue();
                List<String> list = Configs.Values.BLOCK_BRAKE_PROTECTION_LIST.getValue();
                boolean inList = list.contains(block) || BlockUtilKt.isContains(blockState, list);
                boolean isProtectionBlock = switch (mode) {
                    case None -> false;
                    case WhiteList -> inList;
                    case BlackList -> !inList;
                };
                if (isProtectionBlock) {
                    returnable.setReturnValue(false);
                    returnable.cancel();
                }
            }
    }

    @Inject(method = "attackBlock", at = @At("HEAD"), cancellable = true)
    public void attackBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> returnable) {
        handleBlackListBlock(pos, returnable);
    }
}
