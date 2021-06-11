package forpleuvoir.hiirosakura.client.mixin;

import forpleuvoir.hiirosakura.client.util.PlayerHeadUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 实体注入
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.mixin
 * <p>#class_name MixinOtherClientPlayerEntity
 * <p>#create_time 2021/6/11 22:16
 */
@Mixin(Entity.class)
public abstract class MixinEntity {


    @Shadow
    @Final
    private EntityType<?> type;

    @Shadow
    public abstract String getEntityName();

    //修改玩家实体的getPickBlockStack返回值，使鼠标中键可以获取到玩家头颅
    @Inject(method = "getPickBlockStack", at = @At("RETURN"), cancellable = true)
    public void getPickBlockStack(CallbackInfoReturnable<ItemStack> returnable) {
        if (this.type.equals(EntityType.PLAYER)) {
            returnable.setReturnValue(PlayerHeadUtil.getPlayerHead(getEntityName()));
        }
    }

}
