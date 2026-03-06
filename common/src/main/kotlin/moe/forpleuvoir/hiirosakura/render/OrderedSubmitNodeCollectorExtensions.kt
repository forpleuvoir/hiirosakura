package moe.forpleuvoir.hiirosakura.render

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.render.shape.box.Box
import moe.forpleuvoir.ibukigourd.gui.base.render.texture.Corner
import moe.forpleuvoir.ibukigourd.gui.base.render.texture.WidgetTexture
import moe.forpleuvoir.ibukigourd.gui.util.Direction
import moe.forpleuvoir.ibukigourd.render.color
import moe.forpleuvoir.ibukigourd.render.uv
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.text.width
import moe.forpleuvoir.ibukigourd.text.wrapToLines
import moe.forpleuvoir.ibukigourd.util.math.Vector2f
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.common.color.ARGBColor
import moe.forpleuvoir.nebula.common.color.Colors
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.OverlayTexture
import kotlin.math.absoluteValue

//------------ Texture ------------\\

fun MultiBufferSource.pushTexture(
    box: Box,
    widgetTexture: WidgetTexture,
    packedLight: Int,
    color: ARGBColor = Colors.WHITE,
    poseStack: PoseStack,
    renderType: RenderType
) = pushTexture(box.x, box.y, box.width, box.height, widgetTexture, packedLight, color, poseStack, renderType)

fun MultiBufferSource.pushTexture(
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
    pushNineSlicedTexture(x, y, width, height, color, widgetTexture, packedLight, poseStack.last(), this.getBuffer(renderType))
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
    widgetTexture: WidgetTexture,
    packedLight: Int,
    color: ARGBColor = Colors.WHITE,
    pose: PoseStack.Pose,
    vertexConsumer: VertexConsumer
) {
    val x0 = x
    val y0 = y
    val x1 = x + width
    val y1 = y + height
    val u0 = widgetTexture.u0
    val v0 = widgetTexture.v0
    val u1 = widgetTexture.u1
    val v1 = widgetTexture.v1
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
    color: ARGBColor = Colors.WHITE,
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
    color: ARGBColor,
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
    color: ARGBColor = Colors.WHITE,
    widgetTexture: WidgetTexture,
    packedLight: Int,
    pose: PoseStack.Pose,
    vertexConsumer: VertexConsumer
) {

    val corner = widgetTexture.corner

    if (widgetTexture.corner == Corner.Unspecified) {
        pushTexture(x, y, width, height, widgetTexture, packedLight, color, pose, vertexConsumer)
        return
    }

    val tw = widgetTexture.textureInfo.width
    val th = widgetTexture.textureInfo.height
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

fun MultiBufferSource.pushSpeechBubbleTexture(
    bubbleBox: Box,
    bubble: WidgetTexture,
    arrowBox: Box,
    arrow: WidgetTexture,
    /**
     * 箭头所在的方向
     */
    arrowDirection: Direction,
    packedLight: Int,
    color: ARGBColor = Colors.WHITE,
    poseStack: PoseStack,
    renderType: RenderType
) {
    pushSpeechBubbleTexture(bubbleBox, bubble, arrowBox, arrow, arrowDirection, color, packedLight, poseStack.last(), this.getBuffer(renderType))
}

fun pushSpeechBubbleTexture(
    bubbleBox: Box,
    bubble: WidgetTexture,
    arrowBox: Box,
    arrow: WidgetTexture,
    /**
     * 箭头所在的方向
     */
    arrowDirection: Direction,
    color: ARGBColor = Colors.WHITE,
    packedLight: Int,
    pose: PoseStack.Pose,
    vertexConsumer: VertexConsumer
) {
    pushNineSlicedTexture(arrowBox.x, arrowBox.y, arrowBox.width, arrowBox.height, color, arrow, packedLight, pose, vertexConsumer)
    val corner = bubble.corner
    if (!bubble.corner.isSpecified) {
        pushTexture(bubbleBox.x, bubbleBox.y, bubbleBox.width, bubbleBox.height, bubble, packedLight, color, pose, vertexConsumer)
        return
    }

    val aw = arrowBox.width
    val ah = arrowBox.height
    val ax = arrowBox.x
    val ax2 = arrowBox.right
    val ay = arrowBox.y
    val ay2 = arrowBox.bottom

    val texture = bubble.textureSetup
    val x = bubbleBox.x
    val y = bubbleBox.y
    val width = bubbleBox.width
    val height = bubbleBox.height
    val u0 = bubble.uStart
    val v0 = bubble.vStart
    val u1 = bubble.uSize
    val v1 = bubble.vSize
    val tw = bubble.textureInfo.width
    val th = bubble.textureInfo.height

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
    if (arrowDirection == Direction.Top) {
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
    if (arrowDirection == Direction.Left) {
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
    if (arrowDirection == Direction.Right) {
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
    if (arrowDirection == Direction.Bottom) {
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

//------------ Box ------------\\


//region Text
fun Font.pushText(
    text: Text,
    x: Float,
    y: Float,
    dropShadow: Boolean,
    displayMode: Font.DisplayMode,
    packedLight: Int,
    color: ARGBColor,
    backgroundColor: ARGBColor,
    poseStack: PoseStack,
    bufferSource: MultiBufferSource
) = drawInBatch(
    text, x, y, color.argb, dropShadow, poseStack.last().pose(), bufferSource, displayMode, backgroundColor.argb, packedLight
)


fun Font.pushText(
    string: String,
    x: Float,
    y: Float,
    dropShadow: Boolean,
    displayMode: Font.DisplayMode,
    packedLight: Int,
    color: ARGBColor,
    backgroundColor: ARGBColor,
    poseStack: PoseStack,
    bufferSource: MultiBufferSource
) = drawInBatch(
    string, x, y, color.argb, dropShadow, poseStack.last().pose(), bufferSource, displayMode, backgroundColor.argb, packedLight
)


fun Font.pushStringLines(
    lines: List<String>,
    box: Box,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    dropShadow: Boolean = false,
    displayMode: Font.DisplayMode = Font.DisplayMode.NORMAL,
    defaultColor: ARGBColor = Colors.BLACK,
    backgroundColor: ARGBColor = Colors.BLACK.alpha(0),
    packedLight: Int = LightTexture.FULL_BRIGHT,
    poseStack: PoseStack,
    bufferSource: MultiBufferSource
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
        poseStack,
        bufferSource
    )
}

fun Font.pushStringLines(
    lines: String,
    box: Box,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    dropShadow: Boolean = false,
    displayMode: Font.DisplayMode = Font.DisplayMode.NORMAL,
    defaultColor: ARGBColor = Colors.BLACK,
    backgroundColor: ARGBColor = Colors.BLACK.alpha(0),
    packedLight: Int = LightTexture.FULL_BRIGHT,
    poseStack: PoseStack,
    bufferSource: MultiBufferSource
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
        poseStack,
        bufferSource
    )
}

@JvmName("textStringLines")
private fun Font.textLines(
    verticalArrangement: Arrangement.Vertical,
    box: Box,
    texts: List<String>,
    horizontalAlignment: Alignment.Horizontal,
    dropShadow: Boolean,
    displayMode: Font.DisplayMode,
    packedLight: Int,
    defaultColor: ARGBColor,
    backgroundColor: ARGBColor,
    poseStack: PoseStack,
    bufferSource: MultiBufferSource
) {
    val verticalOffsets = verticalArrangement.arrange(box.height, List(texts.size) { mc.font.lineHeight.toFloat() })
    val horizontalOffsets = texts.map { horizontalAlignment.align(box.width, it.width) }
    horizontalOffsets.zip(verticalOffsets) { x, y ->
        Vector2f(box.x + x, box.y + y)
    }.forEachIndexed { index, offset ->
        pushText(texts[index], offset.x, offset.y, dropShadow, displayMode, packedLight, defaultColor, backgroundColor, poseStack, bufferSource)
    }
}

private fun Font.textLines(
    verticalArrangement: Arrangement.Vertical,
    box: Box,
    texts: List<Text>,
    horizontalAlignment: Alignment.Horizontal,
    dropShadow: Boolean,
    displayMode: Font.DisplayMode,
    packedLight: Int,
    defaultColor: ARGBColor,
    backgroundColor: ARGBColor,
    poseStack: PoseStack,
    bufferSource: MultiBufferSource
) {
    val verticalOffsets = verticalArrangement.arrange(box.height, List(texts.size) { mc.font.lineHeight.toFloat() })
    val horizontalOffsets = texts.map { horizontalAlignment.align(box.width, it.width) }
    horizontalOffsets.zip(verticalOffsets) { x, y ->
        Vector2f(box.x + x, box.y + y)
    }.forEachIndexed { index, offset ->
        pushText(texts[index], offset.x, offset.y, dropShadow, displayMode, packedLight, defaultColor, backgroundColor, poseStack, bufferSource)
    }
}
//endregion