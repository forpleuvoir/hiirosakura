package moe.forpleuvoir.hiirosakura.mixin.client.render;

import moe.forpleuvoir.hiirosakura.functional.chataddons.chatbubble.AvatarRenderStateAccessor;
import moe.forpleuvoir.hiirosakura.functional.chataddons.chatbubble.ChatBubbleLayer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerRenderState, PlayerModel> {

    public PlayerRendererMixin(EntityRendererProvider.Context context, PlayerModel model, float shadowRadius) {
        super(context, model, shadowRadius);
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;F)V", at = @At("HEAD"))
    public void extractRenderState(AbstractClientPlayer entity, PlayerRenderState reusedState, float partialTick, CallbackInfo ci) {
        ((AvatarRenderStateAccessor) reusedState).hiirosakura$setName(entity.getName().getString());
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(EntityRendererProvider.Context context, boolean slim, CallbackInfo ci) {
        addLayer(new ChatBubbleLayer(this));
    }

}
