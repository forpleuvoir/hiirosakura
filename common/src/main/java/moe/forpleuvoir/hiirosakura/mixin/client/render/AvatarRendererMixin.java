package moe.forpleuvoir.hiirosakura.mixin.client.render;

import moe.forpleuvoir.hiirosakura.functional.chataddons.chatbubble.AvatarRenderStateAccessor;
import moe.forpleuvoir.hiirosakura.functional.chataddons.chatbubble.ChatBubbleLayer;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public abstract class AvatarRendererMixin<T extends Avatar & ClientAvatarEntity> extends LivingEntityRenderer<T, AvatarRenderState, PlayerModel> {

    public AvatarRendererMixin(EntityRendererProvider.Context context, PlayerModel model, float shadowRadius) {
        super(context, model, shadowRadius);
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V", at = @At("HEAD"))
    public void extractRenderState(T entity, AvatarRenderState reusedState, float partialTick, CallbackInfo ci) {
        ((AvatarRenderStateAccessor) reusedState).hiirosakura$setName(entity.getName().getString());
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(EntityRendererProvider.Context context, boolean slim, CallbackInfo ci) {
        addLayer(new ChatBubbleLayer(this));
    }

}
