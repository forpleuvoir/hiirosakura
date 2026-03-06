package moe.forpleuvoir.hiirosakura.functional.renderaddons

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import moe.forpleuvoir.hiirosakura.render.pushText
import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBindBoolean
import moe.forpleuvoir.ibukigourd.config.item.vector3f
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.text.width
import moe.forpleuvoir.ibukigourd.text.withColor
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.common.color.ARGBColor
import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.common.util.primitive.either
import moe.forpleuvoir.nebula.config.item.impl.*
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRenderDispatcher
import net.minecraft.client.renderer.entity.state.EntityRenderState
import net.minecraft.client.renderer.entity.state.ExperienceOrbRenderState
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.ExperienceOrb
import net.minecraft.world.entity.item.ItemEntity
import org.joml.Vector3f

object DropEntityRenderAddon : ModConfigContainer("drop_entity") {

    val enable by keyBindBoolean("enable", value = false)

    val distance by double("distance", 233.0, 0.0, 2333.0)

    val useMaxLight by boolean("use_max_light", false)

    val offset by vector3f("offset", Vector3f(0f, 0f, 0f), Vector3f(-200f, -200f, -200f), Vector3f(200f, 200f, 200f))

    val spacing by float("spacing", 0f, 0f, 10f)

    val textDefaultColor by color("default_color", Colors.WHITE)

    val textBackgroundColor by color("background_color", Color.ofARGB(0x66000000))

//    val invertOutlineColor by boolean("invert_outline_color", false)

//    val textOutlineColor by color("text_outline_color", Color.ofARGB(0))

    val displayMode: Font.DisplayMode by enum("display_mode", Font.DisplayMode.NORMAL)

    val onlyYRotation by boolean("only_y_rotation", false)

    val experienceOrbValue by keyBindBoolean("experience_orb_value", value = false)

    val itemStackInfo = addConfig(ItemStackInfo(enableScript = true))

    @JvmStatic
    fun renderItemEntityInfo(
        entity: ItemEntity,
        renderState: ItemEntityRenderState,
        entityRenderDispatcher: EntityRenderDispatcher,
        packedLight: Int,
        poseStack: PoseStack,
        multiBufferSource: MultiBufferSource
    ) {
        if (!enable.value || distance <= 0) return
        if (renderState.distanceToCameraSq > distance) return
        val texts = itemStackInfo.getItemStackInfo(entity.item, mc.player)
        if (texts.isNotEmpty()) {
            renderEntityMultiText(
                renderState.boundingBoxHeight,
                texts,
                packedLight,
                renderState,
                entityRenderDispatcher,
                poseStack,
                multiBufferSource
            )
        }
    }

    @JvmStatic
    fun renderExperienceOrbValue(
        color: ARGBColor,
        entity: ExperienceOrb,
        renderState: ExperienceOrbRenderState,
        entityRenderDispatcher: EntityRenderDispatcher,
        packedLight: Int,
        poseStack: PoseStack,
        multiBufferSource: MultiBufferSource
    ) {
        if (!enable.value || !experienceOrbValue.value || distance <= 0) return
        if (renderState.distanceToCameraSq > distance) return
        val text = Literal(entity.value.toString()).withColor(color)
        renderEntityText(
            renderState.boundingBoxHeight,
            -0.5,
            text,
            packedLight,
            renderState,
            entityRenderDispatcher,
            poseStack,
            multiBufferSource
        )
    }

    private fun renderEntityText(
        height: Float,
        textHeight: Double,
        text: Component,
        packedLight: Int,
        entityRenderState: EntityRenderState,
        entityRenderDispatcher: EntityRenderDispatcher,
        poseStack: PoseStack,
        multiBufferSource: MultiBufferSource
    ) {
        val light = useMaxLight.either(LightTexture.FULL_BRIGHT, packedLight)

        poseStack.pushPose()

        poseStack.translate(offset.x() * 0.01f, offset.y() * 0.01f + (height + 0.7f + textHeight).toFloat(), offset.z() * 0.01f)

        if (onlyYRotation) {
            poseStack.mulPose(Axis.YP.rotation(entityRenderDispatcher.cameraOrientation().getEulerAnglesYXZ(Vector3f()).y()))
        } else
            poseStack.mulPose(entityRenderDispatcher.cameraOrientation())

        poseStack.scale(0.025f, -0.025f, 0.025f)

//        val outlineColor = text.style.color?.let {
//            Color.ofRGB(it.value).reverse()
//        } ?: textDefaultColor.reverse()
        mc.font.pushText(
            text,
            (-text.width / 2),
            0f,
            false,
            displayMode,
            light,
            textDefaultColor,
            if (text.string.isEmpty()) Color.ofARGB(0) else textBackgroundColor,
//            if (invertOutlineColor) outlineColor else textOutlineColor,
            poseStack,
            multiBufferSource
        )
        poseStack.popPose()
    }

    fun renderEntityMultiText(
        height: Float,
        text: List<Component>,
        packedLight: Int,
        entityRenderState: EntityRenderState,
        entityRenderDispatcher: EntityRenderDispatcher,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource
    ) {
        val textRows = text.size
        var textHeight = (textRows * (0.25f + (spacing * 0.05f))).toDouble()
        for (item in text) {
            textHeight -= 0.25 + spacing * 0.05f
            renderEntityText(height, textHeight, item, packedLight, entityRenderState, entityRenderDispatcher, poseStack, bufferSource)
        }
    }

}






