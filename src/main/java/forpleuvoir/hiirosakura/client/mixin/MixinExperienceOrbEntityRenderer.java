package forpleuvoir.hiirosakura.client.mixin;

import forpleuvoir.hiirosakura.client.config.Configs;
import forpleuvoir.hiirosakura.client.util.TextRenderUtil;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ExperienceOrbEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.ArrayList;

/**
 * 经验球实体渲染器
 *
 * @author forpleuvoir
 * <p>项目名 hiirosakura
 * <p>包名 forpleuvoir.hiirosakura.client.mixin
 * <p>文件名 MixinExperienceOrbEntityRenderer
 * <p>创建时间 2021/7/30 17:38
 */
@Mixin(ExperienceOrbEntityRenderer.class)
public abstract class MixinExperienceOrbEntityRenderer extends EntityRenderer<ExperienceOrbEntity> {

    protected MixinExperienceOrbEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Inject(method = "render*", at = @At("RETURN"))
    public void render(ExperienceOrbEntity experienceOrbEntity, float f, float g, MatrixStack matrixStack,
                       VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo callbackInfo
    ) {
        var texts = new ArrayList<Text>();

        //渲染经验值
        if (Configs.Toggles.EXPERIENCE_ORB_ENTITY_VALUE_RENDER.getValue()) {

            float age = ((float) experienceOrbEntity.age + g) / 2.0F;
            int experienceAmount = experienceOrbEntity.getExperienceAmount();

            int red = (int) ((MathHelper.sin(age + 0.0F) + 1.0F) * 0.5F * 255.0F);
            int blue = (int) ((MathHelper.sin(age + 4.1887903F) + 1.0F) * 0.1F * 255.0F);
            int rgb = new Color(red, 255, blue).getRGB();

            texts.add(MutableText.of(new LiteralTextContent(String.valueOf(experienceAmount)))
                    .styled(style -> style.withColor(rgb))
            );
        }

        //渲染剩余销毁时间
        if (Configs.Toggles.SHOW_ENTITY_AGE.getValue()) {
            int maxAge = 6000;
            int age = maxAge - ((MixinExperienceOrbEntityInterface) experienceOrbEntity).getAge();
            texts.add(TextRenderUtil.ageColorText(String.valueOf(age / 20), age, maxAge, false)
                    .append("s")
            );

        }
        if (!texts.isEmpty())
            TextRenderUtil
                    .renderEntityMultiText(experienceOrbEntity, texts,
                            this.dispatcher, getTextRenderer(), matrixStack,
                            vertexConsumerProvider, i
                    );
    }

}
