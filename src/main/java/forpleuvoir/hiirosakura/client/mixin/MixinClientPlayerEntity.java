package forpleuvoir.hiirosakura.client.mixin;

import com.mojang.authlib.GameProfile;
import forpleuvoir.hiirosakura.client.config.Configs;
import forpleuvoir.hiirosakura.client.feature.chatmessage.ChatMessageInject;
import forpleuvoir.hiirosakura.client.feature.event.OnDeathEvent;
import forpleuvoir.hiirosakura.client.feature.event.OnPlayerTickEvent;
import forpleuvoir.hiirosakura.client.feature.event.base.EventBus;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * ClientPlayerEntity注入
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.mixin
 * <p>#class_name MixinClientPlayerEntity
 * <p>#create_time 2021/6/10 22:19
 */
@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity extends PlayerEntity {

    public MixinClientPlayerEntity(World world, BlockPos pos, float yaw,
                                   GameProfile profile
    ) {
        super(world, pos, yaw, profile);
    }

    /**
     * 客户端玩家发送聊天消息时
     *
     * @param message 客户端准备发送的消息 {@link String}
     */
    @ModifyVariable(method = "sendChatMessage", at = @At(value = "HEAD"), argsOnly = true)
    public String sendChatMessage(String message) {
        return ChatMessageInject.INSTANCE.handlerMessage(message);
    }


    /**
     * 是否显示死亡画面
     *
     * @param cir {@link CallbackInfoReturnable}
     */
    @Inject(method = "showsDeathScreen", at = @At("HEAD"), cancellable = true)
    public void showsDeathScreen(CallbackInfoReturnable<Boolean> cir) {
        boolean showsDeathScreen = !Configs.Toggles.AUTO_REBIRTH.getBooleanValue();
        DamageSource source = getRecentDamageSource();
        Entity attacker = source != null ? source.getAttacker() : null;
        String attackerName = attacker != null ? attacker.getDisplayName().toString() : null;
        EventBus.broadcast(
                new OnDeathEvent(showsDeathScreen,
                                 source != null ? source.name : null,
                                 attackerName
                ));
        cir.setReturnValue(showsDeathScreen);
    }


    @Inject(method = "tick", at = @At("RETURN"))
    public void tick(CallbackInfo callbackInfo) {
        if (Configs.Toggles.ENABLE_PLAYER_TICK_EVENT.getBooleanValue())
            EventBus.broadcast(new OnPlayerTickEvent(((ClientPlayerEntity) (Object) this)));
    }
}
