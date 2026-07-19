package moe.forpleuvoir.hiirosakura.ui.widget.radialmenu

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 默认的选中进度动画规格。
 *
 * 提取为共享实例以保证 [RadialMenuAnimation] 默认参数的相等性。
 */
private val DefaultAnimationSpec: AnimationSpec<Float> = tween(
    durationMillis = 150,
    easing = FastOutSlowInEasing,
)

/**
 * 径向菜单的扇区动画参数。
 *
 * 每个扇区独立维护一个"选中进度"（0f 未选中 -> 1f 选中），
 * 以下属性均按该进度插值，由 [spec] 驱动，因此缩放与位移同步进行。
 *
 * 整体旋转不需要单独的动画参数：直接在外部对
 * `startAngleDegree` 做动画即可（命中测试基于角度计算，会随之一致旋转）。
 *
 * @param sectorScale 选中时扇区背景围绕内容锚点（扇区中心角方向、option 半径处）的缩放，不产生额外位移
 * @param sectorRadialOffset 选中时扇区背景沿扇区中心角方向（远离菜单中心）的位移（选项内容随扇区同步移动）
 * @param contentScale 选中时选项内容的缩放
 * @param contentRadialOffset 选中时选项内容在扇区位移基础上额外叠加的径向位移
 * @param spec 选中进度的动画规格
 */
@Immutable
data class RadialMenuAnimation(
    val sectorScale: Float = 1.06f,
    val sectorRadialOffset: Dp = 3.dp,
    val contentScale: Float = 1.12f,
    val contentRadialOffset: Dp = 0.dp,
    val spec: AnimationSpec<Float> = DefaultAnimationSpec,
) {
    companion object {
        /**
         * 关闭动画（所有属性均为恒等值，不产生任何视觉效果）
         */
        val Disabled: RadialMenuAnimation = RadialMenuAnimation(
            sectorScale = 1f,
            sectorRadialOffset = 0.dp,
            contentScale = 1f,
            contentRadialOffset = 0.dp,
        )
    }
}
