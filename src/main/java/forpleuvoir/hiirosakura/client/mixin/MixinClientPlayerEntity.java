package forpleuvoir.hiirosakura.client.mixin;

import forpleuvoir.hiirosakura.client.config.Configs;
import forpleuvoir.hiirosakura.client.feature.chatmessage.ChatMessageInject;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.*;
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
public abstract class MixinClientPlayerEntity {

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
        cir.setReturnValue(!Configs.Toggles.AUTO_REBIRTH.getBooleanValue());
    }
}
