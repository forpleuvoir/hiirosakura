package forpleuvoir.hiirosakura.client.mixin;

import forpleuvoir.hiirosakura.client.command.common.HiiroSakuraClientCommand;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
public abstract class MixinClientPlayerEntity {

    /**
     * 客户端玩家发送聊天消息时
     * @param message 客户端准备发送的消息 {@link String}
     * @param callbackInfo 回调信息
     */
    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    public void sendChatMessage(String message, CallbackInfo callbackInfo){

    }
}
