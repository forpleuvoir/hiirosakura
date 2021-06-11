package forpleuvoir.hiirosakura.client.mixin;

import forpleuvoir.hiirosakura.client.util.PlayerHeadUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.tutorial.TutorialManager;
import net.minecraft.client.tutorial.TutorialStep;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * MinecraftClient注入
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.mixin
 * <p>#class_name MixinMinecraftClient
 * <p>#create_time 2021/6/11 21:02
 */
@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {

    @Shadow
    @Final
    private TutorialManager tutorialManager;

    @Shadow
    @Nullable
    public HitResult crosshairTarget;

    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Shadow
    @Nullable
    public ClientWorld world;

    @Shadow
    @Final
    private static Logger LOGGER;

    @Shadow
    protected abstract ItemStack addBlockEntityNbt(ItemStack stack, BlockEntity blockEntity);

    @Shadow
    @Nullable
    public ClientPlayerInteractionManager interactionManager;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(CallbackInfo ci) {
        //关闭游戏自带教程
        tutorialManager.setStep(TutorialStep.NONE);
    }

    //物品选择注入(鼠标中键)
    @Inject(method = "doItemPick", at = @At("HEAD"), cancellable = true)
    public void doItemPick(CallbackInfo ci) {
        if (this.crosshairTarget != null && this.crosshairTarget.getType() != HitResult.Type.MISS) {
            boolean bl = this.player.getAbilities().creativeMode;
            BlockEntity blockEntity = null;
            HitResult.Type type = this.crosshairTarget.getType();
            ItemStack itemStack3;
            if (type == HitResult.Type.BLOCK) {
                BlockPos blockPos = ((BlockHitResult) this.crosshairTarget).getBlockPos();
                BlockState blockState = this.world.getBlockState(blockPos);
                if (blockState.isAir()) {
                    return;
                }

                Block block = blockState.getBlock();
                itemStack3 = block.getPickStack(this.world, blockPos, blockState);
                if (itemStack3.isEmpty()) {
                    return;
                }

                if (bl && Screen.hasControlDown() && blockState.hasBlockEntity()) {
                    blockEntity = this.world.getBlockEntity(blockPos);
                }
            } else {
                if (type != HitResult.Type.ENTITY || !bl) {
                    return;
                }

                //创造模式下鼠标中键获取玩家头颅
                Entity entity = ((EntityHitResult) this.crosshairTarget).getEntity();
                if (entity.getType().equals(EntityType.PLAYER)) {
                    itemStack3 = PlayerHeadUtil.getPlayerHead(entity.getEntityName());
                }else {
                    itemStack3 = entity.getPickBlockStack();
                }
                if (itemStack3 == null) {
                    return;
                }
            }

            if (itemStack3.isEmpty()) {
                String string = "";
                if (type == HitResult.Type.BLOCK) {
                    string = Registry.BLOCK
                            .getId(this.world.getBlockState(((BlockHitResult) this.crosshairTarget).getBlockPos())
                                             .getBlock()).toString();
                } else if (type == HitResult.Type.ENTITY) {
                    string = Registry.ENTITY_TYPE.getId(((EntityHitResult) this.crosshairTarget).getEntity().getType())
                                                 .toString();
                }

                LOGGER.warn((String) "Picking on: [{}] {} gave null item", (Object) type, (Object) string);
            } else {
                PlayerInventory playerInventory = this.player.getInventory();
                if (blockEntity != null) {
                    this.addBlockEntityNbt(itemStack3, blockEntity);
                }

                int i = playerInventory.getSlotWithStack(itemStack3);
                if (bl) {
                    playerInventory.addPickBlock(itemStack3);
                    this.interactionManager.clickCreativeStack(this.player.getStackInHand(Hand.MAIN_HAND),
                                                               36 + playerInventory.selectedSlot
                    );
                } else if (i != -1) {
                    if (PlayerInventory.isValidHotbarIndex(i)) {
                        playerInventory.selectedSlot = i;
                    } else {
                        this.interactionManager.pickFromInventory(i);
                    }
                }

            }
        }
        //代替原版方法
        ci.cancel();
    }
}
