package moe.forpleuvoir.hiirosakura.mixin.client.render;

import moe.forpleuvoir.hiirosakura.functional.renderaddons.CreeperFeatureRenderer;
import net.minecraft.client.render.entity.CreeperEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.client.render.entity.state.CreeperEntityRenderState;
import net.minecraft.entity.mob.CreeperEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreeperEntityRenderer.class)
public abstract class CreeperEntityRendererMixin extends MobEntityRenderer<CreeperEntity, CreeperEntityRenderState, CreeperEntityModel> {

    public CreeperEntityRendererMixin(EntityRendererFactory.Context context, CreeperEntityModel entityModel, float f) {
        super(context, entityModel, f);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void hiirosakura$init(EntityRendererFactory.Context context, CallbackInfo ci) {
        this.addFeature(new CreeperFeatureRenderer(this));
    }

}
