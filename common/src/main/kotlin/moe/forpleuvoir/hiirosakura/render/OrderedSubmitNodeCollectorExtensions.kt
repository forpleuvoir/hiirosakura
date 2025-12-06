package moe.forpleuvoir.hiirosakura.render

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.render.shape.box.Box
import moe.forpleuvoir.ibukigourd.gui.base.render.texture.Corner
import moe.forpleuvoir.ibukigourd.gui.base.render.texture.WidgetTexture
import moe.forpleuvoir.ibukigourd.render.color
import moe.forpleuvoir.ibukigourd.render.uv
import moe.forpleuvoir.ibukigourd.render.vertex
import moe.forpleuvoir.ibukigourd.text.*
import moe.forpleuvoir.ibukigourd.util.math.Vector2f
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.common.color.ARGBColor
import moe.forpleuvoir.nebula.common.color.Colors
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.OrderedSubmitNodeCollector
import net.minecraft.client.renderer.RenderType
import net.minecraft.locale.Language
import net.minecraft.network.chat.FormattedText
import org.joml.Matrix4f
import kotlin.math.absoluteValue

//------------ Texture ------------\\

fun OrderedSubmitNodeCollector.pushTexture(
    box: Box,
    widgetTexture: WidgetTexture,
    packedLight: Int,
    color: ARGBColor = Colors.WHITE,
    poseStack: PoseStack,
    renderType: RenderType
) = pushTexture(box.x, box.y, box.width, box.height, widgetTexture, packedLight, color, poseStack, renderType)

fun OrderedSubmitNodeCollector.pushTexture(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    widgetTexture: WidgetTexture,
    packedLight: Int,
    color: ARGBColor = Colors.WHITE,
    poseStack: PoseStack,
    renderType: RenderType
) {
    submitCustomGeometry(poseStack, renderType) { pose, consumer ->
        pushNineSlicedTexture(x, y, width, height, color, widgetTexture, packedLight, pose.pose(), consumer)
    }
}

private fun pushTexture(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    widgetTexture: WidgetTexture,
    packedLight: Int,
    color: ARGBColor = Colors.WHITE,
    matrix4f: Matrix4f,
    vertexConsumer: VertexConsumer
) {
    vertexConsumer.vertex(matrix4f, x, y, 0f).uv(widgetTexture.u0, widgetTexture.v0).setLight(packedLight).color(color)
    vertexConsumer.vertex(matrix4f, x, y + height, 0f).uv(widgetTexture.u0, widgetTexture.v1).setLight(packedLight).color(color)
    vertexConsumer.vertex(matrix4f, x + width, y + height, 0f).uv(widgetTexture.u1, widgetTexture.v1).setLight(packedLight).color(color)
    vertexConsumer.vertex(matrix4f, x + width, y, 0f).uv(widgetTexture.u1, widgetTexture.v0).setLight(packedLight).color(color)
}

private fun pushTexture(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    u: Int,
    v: Int,
    uSize: Int,
    vSize: Int,
    packedLight: Int,
    color: ARGBColor = Colors.WHITE,
    textureWidth: Int = 256,
    textureHeight: Int = 256,
    matrix4f: Matrix4f,
    vertexConsumer: VertexConsumer
) {
    val textureU = u.toFloat() / textureWidth.toFloat()
    val textureV = v.toFloat() / textureHeight.toFloat()
    val textureUEnd = (u + uSize).toFloat() / textureWidth.toFloat()
    val textureVEnd = (v + vSize).toFloat() / textureHeight.toFloat()
    vertexConsumer.apply {
        //top left
        vertex(matrix4f, x, y, 0f).uv(textureU, textureV).setLight(packedLight).color(color)
        //bottom left
        vertex(matrix4f, x, y + height, 0f).uv(textureU, textureVEnd).setLight(packedLight).color(color)
        //bottom right
        vertex(matrix4f, x + width, y + height, 0f).uv(textureUEnd, textureVEnd).setLight(packedLight).color(color)
        //top right
        vertex(matrix4f, x + width, y, 0f).uv(textureUEnd, textureV).setLight(packedLight).color(color)
    }
}

private fun pushNineSlicedTexture(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    color: ARGBColor = Colors.WHITE,
    widgetTexture: WidgetTexture,
    packedLight: Int,
    matrix4f: Matrix4f,
    vertexConsumer: VertexConsumer
) {

    val corner = widgetTexture.corner

    if (widgetTexture.corner == Corner.Unspecified) {
        pushTexture(x, y, width, height, widgetTexture, packedLight, color, matrix4f, vertexConsumer)
        return
    }

    val textureWidth = widgetTexture.textureInfo.width
    val textureHeight = widgetTexture.textureInfo.height
    val u = widgetTexture.uStart
    val v = widgetTexture.vStart
    val uSize = widgetTexture.uSize
    val vSize = widgetTexture.vSize

    //corner.left
    val cl = corner.left.absoluteValue.toFloat()
    //corner.right
    val cr = corner.right.absoluteValue.toFloat()
    //corner.top
    val ct = corner.top.absoluteValue.toFloat()
    //corner.bottom
    val cb = corner.bottom.absoluteValue.toFloat()

    /**
     * centerWidth
     */
    val cw = width - (corner.left.coerceAtLeast(0) + corner.right.coerceAtLeast(0))

    /**
     * centerHeight
     */
    val ch = height - (corner.top.coerceAtLeast(0) + corner.bottom.coerceAtLeast(0))

    val leftX = if (corner.left >= 0) x else x - cl
    val centerX = if (corner.left >= 0) x + cl else x
    val rightX = if (corner.right >= 0) x + (width - corner.right) else x + width

    val topY = if (corner.top >= 0) y else y - ct
    val centerY = if (corner.top >= 0) y + ct else y
    val bottomY = if (corner.bottom >= 0) y + (height - corner.bottom) else y + height

    val leftU = if (corner.left >= 0) u else u - cl.toInt()
    val centerU = if (corner.left >= 0) u + cl.toInt() else u
    val rightU = if (corner.right >= 0) u + (uSize - cr.toInt()) else u + uSize

    val topV = if (corner.top >= 0) v else v - ct.toInt()
    val centerV = if (corner.top >= 0) v + ct.toInt() else v
    val bottomV = if (corner.bottom >= 0) v + (vSize - cb.toInt()) else v + vSize

    val leftUS = cl.toInt()
    val centerUS = uSize - (corner.left.coerceAtLeast(0) + corner.right.coerceAtLeast(0))
    val rightUS = cr.toInt()

    val topVS = ct.toInt()
    val centerVS = vSize - (corner.top.coerceAtLeast(0) + corner.bottom.coerceAtLeast(0))
    val bottomVS = cb.toInt()

    //top left
    pushTexture(leftX, topY, cl, ct, leftU, topV, leftUS, topVS, packedLight, color, textureWidth, textureHeight, matrix4f, vertexConsumer)
    //top center
    pushTexture(centerX, topY, cw, ct, centerU, topV, centerUS, topVS, packedLight, color, textureWidth, textureHeight, matrix4f, vertexConsumer)
    //top right
    pushTexture(rightX, topY, cr, ct, rightU, topV, rightUS, topVS, packedLight, color, textureWidth, textureHeight, matrix4f, vertexConsumer)

    //center left
    pushTexture(leftX, centerY, cl, ch, leftU, centerV, leftUS, centerVS, packedLight, color, textureWidth, textureHeight, matrix4f, vertexConsumer)
    //center
    pushTexture(centerX, centerY, cw, ch, centerU, centerV, centerUS, centerVS, packedLight, color, textureWidth, textureHeight, matrix4f, vertexConsumer)
    //center right
    pushTexture(rightX, centerY, cr, ch, rightU, centerV, rightUS, centerVS, packedLight, color, textureWidth, textureHeight, matrix4f, vertexConsumer)

    //bottom left
    pushTexture(leftX, bottomY, cl, cb, leftU, bottomV, leftUS, bottomVS, packedLight, color, textureWidth, textureHeight, matrix4f, vertexConsumer)
    //bottom center
    pushTexture(centerX, bottomY, cw, cb, centerU, bottomV, centerUS, bottomVS, packedLight, color, textureWidth, textureHeight, matrix4f, vertexConsumer)
    //bottom right
    pushTexture(rightX, bottomY, cr, cb, rightU, bottomV, rightUS, bottomVS, packedLight, color, textureWidth, textureHeight, matrix4f, vertexConsumer)
}

//------------ Box ------------\\


//------------ Text ------------\\

fun OrderedSubmitNodeCollector.pushText(
    string: McText,
    x: Float,
    y: Float,
    dropShadow: Boolean,
    displayMode: Font.DisplayMode,
    packedLight: Int,
    color: ARGBColor,
    backgroundColor: ARGBColor,
    outlineColor: ARGBColor,
    poseStack: PoseStack
) = submitText(poseStack, x, y, string.visualOrderText, dropShadow, displayMode, packedLight, color.argb, backgroundColor.argb, outlineColor.argb)

fun OrderedSubmitNodeCollector.pushText(
    string: String,
    x: Float,
    y: Float,
    dropShadow: Boolean,
    displayMode: Font.DisplayMode,
    packedLight: Int,
    color: ARGBColor,
    backgroundColor: ARGBColor,
    outlineColor: ARGBColor,
    poseStack: PoseStack
) = submitText(
    poseStack,
    x,
    y,
    Language.getInstance().getVisualOrder(FormattedText.of(string)),
    dropShadow,
    displayMode,
    packedLight,
    color.argb,
    backgroundColor.argb,
    outlineColor.argb
)

fun OrderedSubmitNodeCollector.pusAlignmentText(
    string: McText,
    box: Box,
    alignment: Alignment = Alignment.Center,
    dropShadow: Boolean,
    displayMode: Font.DisplayMode,
    packedLight: Int,
    color: ARGBColor,
    backgroundColor: ARGBColor,
    outlineColor: ARGBColor,
    poseStack: PoseStack
) {
    alignment.align(box, string.size).apply {
        pushText(string, x(), y(), dropShadow, displayMode, packedLight, color, backgroundColor, outlineColor, poseStack)
    }
}

fun OrderedSubmitNodeCollector.pusAlignmentText(
    string: String,
    box: Box,
    alignment: Alignment = Alignment.Center,
    dropShadow: Boolean,
    displayMode: Font.DisplayMode = Font.DisplayMode.NORMAL,
    packedLight: Int = LightTexture.FULL_BRIGHT,
    color: ARGBColor,
    backgroundColor: ARGBColor,
    outlineColor: ARGBColor,
    poseStack: PoseStack
) {
    alignment.align(box, string.size).apply {
        pushText(string, x(), y(), dropShadow, displayMode, packedLight, color, backgroundColor, outlineColor, poseStack)
    }
}

fun OrderedSubmitNodeCollector.pushTextLines(
    lines: List<McText>,
    box: Box,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    dropShadow: Boolean = false,
    displayMode: Font.DisplayMode = Font.DisplayMode.NORMAL,
    defaultColor: ARGBColor = Colors.BLACK,
    backgroundColor: ARGBColor = Colors.BLACK.alpha(0),
    outlineColor: ARGBColor,
    packedLight: Int = LightTexture.FULL_BRIGHT,
    poseStack: PoseStack
) {
    textLines(
        verticalArrangement,
        box,
        lines.wrapToTextLines(box.width),
        horizontalAlignment,
        dropShadow,
        displayMode,
        packedLight,
        defaultColor,
        backgroundColor,
        outlineColor,
        poseStack
    )
}

fun OrderedSubmitNodeCollector.pushTextLines(
    lines: McText,
    box: Box,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    dropShadow: Boolean = false,
    displayMode: Font.DisplayMode = Font.DisplayMode.NORMAL,
    defaultColor: ARGBColor = Colors.BLACK,
    backgroundColor: ARGBColor = Colors.BLACK.alpha(0),
    outlineColor: ARGBColor,
    packedLight: Int = LightTexture.FULL_BRIGHT,
    poseStack: PoseStack
) {
    textLines(
        verticalArrangement,
        box,
        lines.wrapToTextLines(box.width),
        horizontalAlignment,
        dropShadow,
        displayMode,
        packedLight,
        defaultColor,
        backgroundColor,
        outlineColor,
        poseStack
    )
}

fun OrderedSubmitNodeCollector.pushStringLines(
    lines: List<String>,
    box: Box,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    dropShadow: Boolean = false,
    displayMode: Font.DisplayMode = Font.DisplayMode.NORMAL,
    defaultColor: ARGBColor = Colors.BLACK,
    backgroundColor: ARGBColor = Colors.BLACK.alpha(0),
    outlineColor: ARGBColor = Colors.BLACK.alpha(0),
    packedLight: Int = LightTexture.FULL_BRIGHT,
    poseStack: PoseStack
) {
    textLines(
        verticalArrangement,
        box,
        lines.wrapToLines(box.width),
        horizontalAlignment,
        dropShadow,
        displayMode,
        packedLight,
        defaultColor,
        backgroundColor,
        outlineColor,
        poseStack
    )
}

fun OrderedSubmitNodeCollector.pushStringLines(
    lines: String,
    box: Box,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    dropShadow: Boolean = false,
    displayMode: Font.DisplayMode = Font.DisplayMode.NORMAL,
    defaultColor: ARGBColor = Colors.BLACK,
    backgroundColor: ARGBColor = Colors.BLACK.alpha(0),
    outlineColor: ARGBColor,
    packedLight: Int = LightTexture.FULL_BRIGHT,
    poseStack: PoseStack
) {
    textLines(
        verticalArrangement,
        box,
        lines.wrapToLines(box.width),
        horizontalAlignment,
        dropShadow,
        displayMode,
        packedLight,
        defaultColor,
        backgroundColor,
        outlineColor,
        poseStack
    )
}

@JvmName("textStringLines")
private fun OrderedSubmitNodeCollector.textLines(
    verticalArrangement: Arrangement.Vertical,
    box: Box,
    texts: List<String>,
    horizontalAlignment: Alignment.Horizontal,
    dropShadow: Boolean,
    displayMode: Font.DisplayMode,
    packedLight: Int,
    defaultColor: ARGBColor,
    backgroundColor: ARGBColor,
    outlineColor: ARGBColor,
    poseStack: PoseStack
) {
    val verticalOffsets = verticalArrangement.arrange(box.height, List(texts.size) { mc.font.lineHeight.toFloat() })
    val horizontalOffsets = texts.map { horizontalAlignment.align(box.width, it.width) }
    horizontalOffsets.zip(verticalOffsets) { x, y ->
        Vector2f(box.x + x, box.y + y)
    }.forEachIndexed { index, offset ->
        pushText(texts[index], offset.x, offset.y, dropShadow, displayMode, packedLight, defaultColor, backgroundColor, outlineColor, poseStack)
    }
}

private fun OrderedSubmitNodeCollector.textLines(
    verticalArrangement: Arrangement.Vertical,
    box: Box,
    texts: List<McText>,
    horizontalAlignment: Alignment.Horizontal,
    dropShadow: Boolean,
    displayMode: Font.DisplayMode,
    packedLight: Int,
    defaultColor: ARGBColor,
    backgroundColor: ARGBColor,
    outlineColor: ARGBColor,
    poseStack: PoseStack
) {
    val verticalOffsets = verticalArrangement.arrange(box.height, List(texts.size) { mc.font.lineHeight.toFloat() })
    val horizontalOffsets = texts.map { horizontalAlignment.align(box.width, it.width) }
    horizontalOffsets.zip(verticalOffsets) { x, y ->
        Vector2f(box.x + x, box.y + y)
    }.forEachIndexed { index, offset ->
        pushText(texts[index], offset.x, offset.y, dropShadow, displayMode, packedLight, defaultColor, backgroundColor, outlineColor, poseStack)
    }
}