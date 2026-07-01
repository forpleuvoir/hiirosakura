package moe.forpleuvoir.hiirosakura.render

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.toIntSize
import androidx.compose.ui.util.fastForEachIndexed
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import moe.forpleuvoir.ibukigourd.render.color
import moe.forpleuvoir.ibukigourd.render.extension.AnchorPosition
import moe.forpleuvoir.ibukigourd.render.extension.texture.Corner
import moe.forpleuvoir.ibukigourd.render.extension.texture.IGTexture
import moe.forpleuvoir.ibukigourd.render.uv
import moe.forpleuvoir.ibukigourd.text.*
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.common.color.Colors
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.OrderedSubmitNodeCollector
import net.minecraft.client.renderer.rendertype.RenderType
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.locale.Language
import net.minecraft.network.chat.FormattedText
import net.minecraft.util.LightCoordsUtil
import kotlin.math.absoluteValue

//------------ Texture ------------\\

fun OrderedSubmitNodeCollector.pushTexture(
    area: Rect,
    texture: IGTexture,
    packedLight: Int,
    color: Color = Colors.WHITE,
    poseStack: PoseStack,
    renderType: RenderType
) = pushTexture(area.left, area.top, area.width, area.height, texture, packedLight, color, poseStack, renderType)

fun OrderedSubmitNodeCollector.pushTexture(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    texture: IGTexture,
    packedLight: Int,
    color: Color = Colors.WHITE,
    poseStack: PoseStack,
    renderType: RenderType
) {
    submitCustomGeometry(poseStack, renderType) { pose, consumer ->
        pushNineSlicedTexture(x, y, width, height, color, texture, packedLight, pose, consumer)
    }
}

private fun VertexConsumer.vertex(pose: PoseStack.Pose, x: Float, y: Float): VertexConsumer =
    addVertex(pose, x, y, 0f)

private fun VertexConsumer.setOLN(pose: PoseStack.Pose, packedLight: Int): VertexConsumer =
    setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, 0f, 1f, 0f)

private fun pushTexture(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    texture: IGTexture,
    packedLight: Int,
    color: Color = Colors.WHITE,
    pose: PoseStack.Pose,
    vertexConsumer: VertexConsumer
) {
    val x0 = x
    val y0 = y
    val x1 = x + width
    val y1 = y + height
    val u0 = texture.u0
    val v0 = texture.v0
    val u1 = texture.u1
    val v1 = texture.v1
    setVertex(vertexConsumer, pose, x0, y0, x1, y1, u0, v0, u1, v1, color, packedLight)
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
    color: Color = Colors.WHITE,
    textureWidth: Int = 256,
    textureHeight: Int = 256,
    pose: PoseStack.Pose,
    vertexConsumer: VertexConsumer
) {
    val x0 = x
    val y0 = y
    val x1 = x + width
    val y1 = y + height
    val u0 = u.toFloat() / textureWidth.toFloat()
    val v0 = v.toFloat() / textureHeight.toFloat()
    val u1 = (u + uSize).toFloat() / textureWidth.toFloat()
    val v1 = (v + vSize).toFloat() / textureHeight.toFloat()
    setVertex(vertexConsumer, pose, x0, y0, x1, y1, u0, v0, u1, v1, color, packedLight)
}

private fun setVertex(
    vertexConsumer: VertexConsumer,
    pose: PoseStack.Pose,
    x0: Float,
    y0: Float,
    x1: Float,
    y1: Float,
    u0: Float,
    v0: Float,
    u1: Float,
    v1: Float,
    color: Color,
    packedLight: Int
) {
    if (x1 - x0 <= 0f || y1 - y0 <= 0f || color.alpha == 0) return
    vertexConsumer.apply {
        vertex(pose, x0, y0).color(color).uv(u0, v0).setOLN(pose, packedLight)
        vertex(pose, x0, y1).color(color).uv(u0, v1).setOLN(pose, packedLight)
        vertex(pose, x1, y1).color(color).uv(u1, v1).setOLN(pose, packedLight)
        vertex(pose, x1, y0).color(color).uv(u1, v0).setOLN(pose, packedLight)
    }
}

private fun pushNineSlicedTexture(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    color: Color = Colors.WHITE,
    texture: IGTexture,
    packedLight: Int,
    pose: PoseStack.Pose,
    vertexConsumer: VertexConsumer
) {

    val corner = texture.corner

    if (texture.corner == Corner.Unspecified) {
        pushTexture(x, y, width, height, texture, packedLight, color, pose, vertexConsumer)
        return
    }

    val tw = texture.textureInfo.width
    val th = texture.textureInfo.height
    val u = texture.uStart
    val v = texture.vStart
    val uSize = texture.uSize
    val vSize = texture.vSize

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
    pushTexture(leftX, topY, cl, ct, leftU, topV, leftUS, topVS, packedLight, color, tw, th, pose, vertexConsumer)
    //top center
    pushTexture(centerX, topY, cw, ct, centerU, topV, centerUS, topVS, packedLight, color, tw, th, pose, vertexConsumer)
    //top right
    pushTexture(rightX, topY, cr, ct, rightU, topV, rightUS, topVS, packedLight, color, tw, th, pose, vertexConsumer)

    //center left
    pushTexture(leftX, centerY, cl, ch, leftU, centerV, leftUS, centerVS, packedLight, color, tw, th, pose, vertexConsumer)
    //center
    pushTexture(centerX, centerY, cw, ch, centerU, centerV, centerUS, centerVS, packedLight, color, tw, th, pose, vertexConsumer)
    //center right
    pushTexture(rightX, centerY, cr, ch, rightU, centerV, rightUS, centerVS, packedLight, color, tw, th, pose, vertexConsumer)

    //bottom left
    pushTexture(leftX, bottomY, cl, cb, leftU, bottomV, leftUS, bottomVS, packedLight, color, tw, th, pose, vertexConsumer)
    //bottom center
    pushTexture(centerX, bottomY, cw, cb, centerU, bottomV, centerUS, bottomVS, packedLight, color, tw, th, pose, vertexConsumer)
    //bottom right
    pushTexture(rightX, bottomY, cr, cb, rightU, bottomV, rightUS, bottomVS, packedLight, color, tw, th, pose, vertexConsumer)
}

fun OrderedSubmitNodeCollector.pushSpeechBubbleTexture(
    bubbleArea: Rect,
    bubbleTexture: IGTexture,
    arrowArea: Rect,
    arrowTexture: IGTexture,
    /**
     * 箭头所在的方向
     */
    arrowAnchorPosition: AnchorPosition,
    packedLight: Int,
    color: Color = Colors.WHITE,
    poseStack: PoseStack,
    renderType: RenderType
) {
    submitCustomGeometry(poseStack, renderType) { pose, consumer ->
        pushSpeechBubbleTexture(bubbleArea, bubbleTexture, arrowArea, arrowTexture, arrowAnchorPosition, color, packedLight, pose, consumer)
    }
}

fun pushSpeechBubbleTexture(
    bubbleArea: Rect,
    bubbleTexture: IGTexture,
    arrowArea: Rect,
    arrowTexture: IGTexture,
    /**
     * 箭头所在的方向
     */
    arrowAnchorPosition: AnchorPosition,
    color: Color = Colors.WHITE,
    packedLight: Int,
    pose: PoseStack.Pose,
    vertexConsumer: VertexConsumer
) {
    pushNineSlicedTexture(arrowArea.left, arrowArea.top, arrowArea.width, arrowArea.height, color, arrowTexture, packedLight, pose, vertexConsumer)
    val corner = bubbleTexture.corner
    if (!bubbleTexture.corner.isSpecified) {
        pushTexture(bubbleArea.left, bubbleArea.top, bubbleArea.width, bubbleArea.height, bubbleTexture, packedLight, color, pose, vertexConsumer)
        return
    }

    val aw = arrowArea.width
    val ah = arrowArea.height
    val ax = arrowArea.left
    val ax2 = arrowArea.right
    val ay = arrowArea.top
    val ay2 = arrowArea.bottom

    val x = bubbleArea.left
    val y = bubbleArea.top
    val width = bubbleArea.width
    val height = bubbleArea.height
    val u0 = bubbleTexture.uStart
    val v0 = bubbleTexture.vStart
    val u1 = bubbleTexture.uSize
    val v1 = bubbleTexture.vSize
    val tw = bubbleTexture.textureInfo.width
    val th = bubbleTexture.textureInfo.height

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

    val leftU = if (corner.left >= 0) u0 else u0 - cl.toInt()
    val centerU = if (corner.left >= 0) u0 + cl.toInt() else u0
    val rightU = if (corner.right >= 0) u0 + (u1 - cr.toInt()) else u0 + u1

    val topV = if (corner.top >= 0) v0 else v0 - ct.toInt()
    val centerV = if (corner.top >= 0) v0 + ct.toInt() else v0
    val bottomV = if (corner.bottom >= 0) v0 + (v1 - cb.toInt()) else v0 + v1

    val leftUS = cl.toInt()
    val centerUS = u1 - (corner.left.coerceAtLeast(0) + corner.right.coerceAtLeast(0))
    val rightUS = cr.toInt()

    val topVS = ct.toInt()
    val centerVS = v1 - (corner.top.coerceAtLeast(0) + corner.bottom.coerceAtLeast(0))
    val bottomVS = cb.toInt()

    //top left
    pushTexture(leftX, topY, cl, ct, leftU, topV, leftUS, topVS, packedLight, color, tw, th, pose, vertexConsumer)
    //top center
    if (arrowAnchorPosition == AnchorPosition.Above) {
        if (cw - aw > 0) {
            pushTexture(centerX, topY, ax - centerX, ct, centerU, topV, centerUS, topVS, packedLight, color, tw, th, pose, vertexConsumer)
            pushTexture(ax2, topY, rightX - ax2, ct, centerU, topV, centerUS, topVS, packedLight, color, tw, th, pose, vertexConsumer)
        }
    } else {
        pushTexture(centerX, topY, cw, ct, centerU, topV, centerUS, topVS, packedLight, color, tw, th, pose, vertexConsumer)
    }
    //top right
    pushTexture(rightX, topY, cr, ct, rightU, topV, rightUS, topVS, packedLight, color, tw, th, pose, vertexConsumer)

    //center left
    if (arrowAnchorPosition == AnchorPosition.Left) {
        if (ch - ah > 0) {
            pushTexture(leftX, centerY, cl, ay - centerY, leftU, centerV, leftUS, centerVS, packedLight, color, tw, th, pose, vertexConsumer)
            pushTexture(leftX, ay2, cl, bottomY - ay2, leftU, centerV, leftUS, centerVS, packedLight, color, tw, th, pose, vertexConsumer)
        }
    } else {
        pushTexture(leftX, centerY, cl, ch, leftU, centerV, leftUS, centerVS, packedLight, color, tw, th, pose, vertexConsumer)
    }
    //center
    pushTexture(centerX, centerY, cw, ch, centerU, centerV, centerUS, centerVS, packedLight, color, tw, th, pose, vertexConsumer)
    //center right
    if (arrowAnchorPosition == AnchorPosition.Right) {
        if (ch - ah > 0) {
            pushTexture(rightX, centerY, cr, ay - centerY, rightU, centerV, rightUS, centerVS, packedLight, color, tw, th, pose, vertexConsumer)
            pushTexture(rightX, ay2, cr, bottomY - ay2, rightU, centerV, rightUS, centerVS, packedLight, color, tw, th, pose, vertexConsumer)
        }
    } else {
        pushTexture(rightX, centerY, cr, ch, rightU, centerV, rightUS, centerVS, packedLight, color, tw, th, pose, vertexConsumer)
    }

    //bottom left
    pushTexture(leftX, bottomY, cl, cb, leftU, bottomV, leftUS, bottomVS, packedLight, color, tw, th, pose, vertexConsumer)
    //bottom center
    if (arrowAnchorPosition == AnchorPosition.Below) {
        if (cw - aw > 0) {
            pushTexture(centerX, bottomY, ax - centerX, cb, centerU, bottomV, centerUS, bottomVS, packedLight, color, tw, th, pose, vertexConsumer)
            pushTexture(ax2, bottomY, rightX - ax2, cb, centerU, bottomV, centerUS, bottomVS, packedLight, color, tw, th, pose, vertexConsumer)
        }
    } else {
        pushTexture(centerX, bottomY, cw, cb, centerU, bottomV, centerUS, bottomVS, packedLight, color, tw, th, pose, vertexConsumer)
    }
    //bottom right
    pushTexture(rightX, bottomY, cr, cb, rightU, bottomV, rightUS, bottomVS, packedLight, color, tw, th, pose, vertexConsumer)
}

//------------ Rect ------------\\


//------------ Text ------------\\

fun OrderedSubmitNodeCollector.pushText(
    string: Text,
    x: Float,
    y: Float,
    dropShadow: Boolean,
    displayMode: Font.DisplayMode,
    packedLight: Int,
    color: Color,
    backgroundColor: Color,
    outlineColor: Color,
    poseStack: PoseStack
) = submitText(poseStack, x, y, string.visualOrderText, dropShadow, displayMode, packedLight, color.argb, backgroundColor.argb, outlineColor.argb)

fun OrderedSubmitNodeCollector.pushText(
    string: String,
    x: Float,
    y: Float,
    dropShadow: Boolean,
    displayMode: Font.DisplayMode,
    packedLight: Int,
    color: Color,
    backgroundColor: Color,
    outlineColor: Color,
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
    string: Text,
    area: IntRect,
    alignment: Alignment = Alignment.Center,
    dropShadow: Boolean,
    displayMode: Font.DisplayMode,
    packedLight: Int,
    color: Color,
    backgroundColor: Color,
    outlineColor: Color,
    poseStack: PoseStack
) {
    alignment.align(string.size.toIntSize(), area.size, LayoutDirection.Ltr).apply {
        pushText(
            string,
            (area.left + x).toFloat(),
            (area.top + y).toFloat(),
            dropShadow,
            displayMode,
            packedLight,
            color,
            backgroundColor,
            outlineColor,
            poseStack
        )
    }
}

fun OrderedSubmitNodeCollector.pusAlignmentText(
    string: String,
    area: IntRect,
    alignment: Alignment = Alignment.Center,
    dropShadow: Boolean,
    displayMode: Font.DisplayMode = Font.DisplayMode.NORMAL,
    packedLight: Int = LightCoordsUtil.FULL_BRIGHT,
    color: Color,
    backgroundColor: Color,
    outlineColor: Color,
    poseStack: PoseStack
) {
    alignment.align(string.size.toIntSize(), area.size, LayoutDirection.Ltr).apply {
        pushText(
            string,
            (area.left + x).toFloat(),
            (area.top + y).toFloat(),
            dropShadow,
            displayMode,
            packedLight,
            color,
            backgroundColor,
            outlineColor,
            poseStack
        )
    }
}

fun OrderedSubmitNodeCollector.pushTextLines(
    lines: List<Text>,
    area: IntRect,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    dropShadow: Boolean = false,
    displayMode: Font.DisplayMode = Font.DisplayMode.NORMAL,
    defaultColor: Color = Colors.BLACK,
    backgroundColor: Color = Colors.BLACK.alpha(0),
    outlineColor: Color,
    packedLight: Int = LightCoordsUtil.FULL_BRIGHT,
    poseStack: PoseStack
) {
    textLines(
        verticalArrangement,
        area,
        lines.wrapToTextLines(area.width.toFloat()),
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
    lines: Text,
    area: IntRect,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    dropShadow: Boolean = false,
    displayMode: Font.DisplayMode = Font.DisplayMode.NORMAL,
    defaultColor: Color = Colors.BLACK,
    backgroundColor: Color = Colors.BLACK.alpha(0),
    outlineColor: Color,
    packedLight: Int = LightCoordsUtil.FULL_BRIGHT,
    poseStack: PoseStack
) {
    textLines(
        verticalArrangement,
        area,
        lines.wrapToTextLines(area.width.toFloat()),
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
    area: IntRect,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    dropShadow: Boolean = false,
    displayMode: Font.DisplayMode = Font.DisplayMode.NORMAL,
    defaultColor: Color = Colors.BLACK,
    backgroundColor: Color = Colors.BLACK.alpha(0),
    outlineColor: Color = Colors.BLACK.alpha(0),
    packedLight: Int = LightCoordsUtil.FULL_BRIGHT,
    poseStack: PoseStack
) {
    textLines(
        verticalArrangement,
        area,
        lines.wrapToLines(area.width.toFloat()),
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
    area: IntRect,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    dropShadow: Boolean = false,
    displayMode: Font.DisplayMode = Font.DisplayMode.NORMAL,
    defaultColor: Color = Colors.BLACK,
    backgroundColor: Color = Colors.BLACK.alpha(0),
    outlineColor: Color,
    packedLight: Int = LightCoordsUtil.FULL_BRIGHT,
    poseStack: PoseStack
) {
    textLines(
        verticalArrangement,
        area,
        lines.wrapToLines(area.width.toFloat()),
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

private val density = Density(1f)

@JvmName("textStringLines")
private fun OrderedSubmitNodeCollector.textLines(
    verticalArrangement: Arrangement.Vertical,
    area: IntRect,
    texts: List<String>,
    horizontalAlignment: Alignment.Horizontal,
    dropShadow: Boolean,
    displayMode: Font.DisplayMode,
    packedLight: Int,
    defaultColor: Color,
    backgroundColor: Color,
    outlineColor: Color,
    poseStack: PoseStack
) {
    val verticalOffsets = IntArray(texts.size)
    verticalArrangement.run {
        density.arrange(area.height, IntArray(texts.size) { mc.font.lineHeight }, verticalOffsets)
    }

    val horizontalOffsets = texts.map { horizontalAlignment.align(it.width.toInt(), area.width, LayoutDirection.Ltr) }

    verticalOffsets.zip(horizontalOffsets).fastForEachIndexed { idx, (y, x) ->
        pushText(texts[idx], area.left + x.toFloat(), area.top + y.toFloat(), dropShadow, displayMode, packedLight, defaultColor, backgroundColor, outlineColor, poseStack)
    }
}

private fun OrderedSubmitNodeCollector.textLines(
    verticalArrangement: Arrangement.Vertical,
    area: IntRect,
    texts: List<Text>,
    horizontalAlignment: Alignment.Horizontal,
    dropShadow: Boolean,
    displayMode: Font.DisplayMode,
    packedLight: Int,
    defaultColor: Color,
    backgroundColor: Color,
    outlineColor: Color,
    poseStack: PoseStack
) {
    val verticalOffsets = IntArray(texts.size)
    verticalArrangement.run {
        density.arrange(area.height, IntArray(texts.size) { mc.font.lineHeight }, verticalOffsets)
    }

    val horizontalOffsets = texts.map { horizontalAlignment.align(it.width.toInt(), area.width, LayoutDirection.Ltr) }

    verticalOffsets.zip(horizontalOffsets).fastForEachIndexed { idx, (y, x) ->
        pushText(texts[idx], area.left + x.toFloat(), area.top + y.toFloat(), dropShadow, displayMode, packedLight, defaultColor, backgroundColor, outlineColor, poseStack)
    }
}