package forpleuvoir.hiirosakura.client.mixin;

import forpleuvoir.hiirosakura.client.HiiroSakuraClient;
import forpleuvoir.hiirosakura.client.chatshow.ChatShow;
import forpleuvoir.hiirosakura.client.chatshow.HiiroSakuraChatShow;
import net.minecraft.client.gui.hud.ChatHudListener;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

/**
 * 聊天栏监听注入
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.mixin
 * <p>#class_name MixinChatHudListener
 * <p>#create_time 2021/6/13 0:11
 */
@Mixin(ChatHudListener.class)
public class MixinChatHudListener {
    private transient static final Logger log = LoggerFactory.getLogger(MixinChatHudListener.class);

    @Inject(
            method = "onChatMessage",
            at = @At("HEAD"),
            cancellable = true
    )
    public void postSay(MessageType type, Text text, UUID senderUuid, CallbackInfo ci) {
        if (type == MessageType.CHAT)
            HiiroSakuraChatShow.INSTANCE.addChatShow(text, senderUuid);
    }

}
