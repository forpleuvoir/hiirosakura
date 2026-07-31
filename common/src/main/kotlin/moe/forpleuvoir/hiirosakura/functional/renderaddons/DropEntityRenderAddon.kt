package moe.forpleuvoir.hiirosakura.functional.renderaddons

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import moe.forpleuvoir.hiirosakura.render.pushText
import moe.forpleuvoir.ibukigourd.config.item.configToggleKeybind
import moe.forpleuvoir.ibukigourd.config.item.configVector3f
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.text.width
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.common.util.primitive.either
import moe.forpleuvoir.nebula.config.ConfigGroup
import moe.forpleuvoir.nebula.config.item.*
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.state.EntityRenderState
import net.minecraft.client.renderer.entity.state.ExperienceOrbRenderState
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState
import net.minecraft.client.renderer.state.level.CameraRenderState
import net.minecraft.network.chat.Component
import net.minecraft.util.LightCoordsUtil
import net.minecraft.world.entity.ExperienceOrb
import net.minecraft.world.entity.item.ItemEntity
import org.joml.Vector3f
import kotlin.math.atan2

object DropEntityRenderAddon : ConfigGroup("drop_entity") {

    val enable by configToggleKeybind("enable", false)

    val distance by configDouble("distance", 233.0, 0.0, 2333.0)

    val useMaxLight by configBoolean("use_max_light", false)

    val offset by configVector3f("offset", Vector3f(0f, 0f, 0f), Vector3f(-200f, -200f, -200f), Vector3f(200f, 200f, 200f))

    val spacing by configFloat("spacing", 0f, 0f, 10f)

    val textDefaultColor by configColor("default_color", Colors.WHITE)

    val textBackgroundColor by configColor("background_color", Color.fromARGB(0x66000000))

    val invertOutlineColor by configBoolean("invert_outline_color", false)

    val textOutlineColor by configColor("text_outline_color", Colors.TRANSPARENT)

    val displayMode: Font.DisplayMode by configEnum("display_mode", Font.DisplayMode.NORMAL)

    val onlyYRotation by configBoolean("only_y_rotation", false)

    val experienceOrbValue by configToggleKeybind("experience_orb_value", false)

    val itemStackInfo = addConfig(ItemStackInfo(enableScript = true))

    @JvmStatic
    fun renderItemEntityInfo(
        entity: ItemEntity,
        renderState: ItemEntityRenderState,
        cameraRenderState: CameraRenderState,
        poseStack: PoseStack,
        nodeCollector: SubmitNodeCollector
    ) {
        if (!enable.enabled || distance <= 0) return
        if (renderState.distanceToCameraSq > distance) return
        val texts = itemStackInfo.getItemStackInfo(entity.item, mc.player)
        if (texts.isNotEmpty()) {
            renderEntityMultiText(
                renderState.boundingBoxHeight,
                texts,
                renderState.lightCoords,
                renderState,
                cameraRenderState,
                poseStack,
                nodeCollector
            )
        }
    }

    @JvmStatic
    fun renderExperienceOrbValue(
        color: Int,
        entity: ExperienceOrb,
        renderState: ExperienceOrbRenderState,
        cameraRenderState: CameraRenderState,
        poseStack: PoseStack,
        nodeCollector: SubmitNodeCollector
    ) {
        if (!enable.enabled || !experienceOrbValue.enabled || distance <= 0) return
        if (renderState.distanceToCameraSq > distance) return
        val text = Literal(entity.value.toString()).withColor(color)
        renderEntityText(
            renderState.boundingBoxHeight,
            -0.5,
            text,
            renderState.lightCoords,
            renderState,
            cameraRenderState,
            poseStack,
            nodeCollector
        )
    }

    private fun renderEntityText(
        height: Float,
        textHeight: Double,
        text: Component,
        packedLight: Int,
        entityRenderState: EntityRenderState,
        cameraRenderState: CameraRenderState,
        poseStack: PoseStack,
        nodeCollector: SubmitNodeCollector
    ) {
        val light = useMaxLight.either(LightCoordsUtil.FULL_BRIGHT, packedLight)

        poseStack.pushPose()

        poseStack.translate(offset.x() * 0.01f, offset.y() * 0.01f + (height + 0.7f + textHeight).toFloat(), offset.z() * 0.01f)

        if (onlyYRotation) {
            val dx = cameraRenderState.pos.x - entityRenderState.x
            val dz = cameraRenderState.pos.z - entityRenderState.z
            val yaw = (Math.toDegrees(atan2(-dx, dz))).toFloat()
            // 获取相机的完整旋转信息
            val vector3f = Vector3f()
            mc.gameRenderer.mainCamera.rotation().getEulerAnglesXYZ(vector3f)

            // 组合Y轴朝向和Z轴旋转
            poseStack.mulPose(Axis.YP.rotationDegrees(-yaw))
        } else
            poseStack.mulPose(cameraRenderState.orientation)

        poseStack.scale(0.025f, -0.025f, 0.025f)

        val outlineColor = text.style.color?.let {
            Color.fromRGB(it.value).reverse()
        } ?: textDefaultColor.reverse()
        nodeCollector.pushText(
            text,
            (-text.width / 2),
            0f,
            false,
            displayMode,
            light,
            textDefaultColor,
            if (text.string.isEmpty()) Colors.TRANSPARENT else textBackgroundColor,
            if (invertOutlineColor) outlineColor else textOutlineColor,
            poseStack
        )
        poseStack.popPose()
    }

    fun renderEntityMultiText(
        height: Float,
        text: List<Component>,
        packedLight: Int,
        entityRenderState: EntityRenderState,
        cameraRenderState: CameraRenderState,
        poseStack: PoseStack,
        nodeCollector: SubmitNodeCollector
    ) {
        val textRows = text.size
        var textHeight = (textRows * (0.25f + (spacing * 0.05f))).toDouble()
        for (item in text) {
            textHeight -= 0.25 + spacing * 0.05f
            renderEntityText(height, textHeight, item, packedLight, entityRenderState, cameraRenderState, poseStack, nodeCollector)
        }
    }

}






