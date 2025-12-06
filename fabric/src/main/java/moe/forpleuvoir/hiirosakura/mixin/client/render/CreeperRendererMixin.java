package moe.forpleuvoir.hiirosakura.mixin.client.render;

import moe.forpleuvoir.hiirosakura.functional.renderaddons.fuse.CreeperFuseLayer;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreeperRenderer.class)
public abstract class CreeperRendererMixin extends MobRenderer<Creeper, CreeperRenderState, CreeperModel> {

    public CreeperRendererMixin(EntityRendererProvider.Context context, CreeperModel model, float shadowRadius) {
        super(context, model, shadowRadius);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(EntityRendererProvider.Context context, CallbackInfo ci) {
        this.addLayer(new CreeperFuseLayer(this));
    }

}
