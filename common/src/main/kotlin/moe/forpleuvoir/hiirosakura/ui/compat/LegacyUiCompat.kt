package moe.forpleuvoir.hiirosakura.ui.compat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import com.mojang.blaze3d.textures.FilterMode
import moe.forpleuvoir.compose_minecraft.platform.screen.ComposeScreen
import moe.forpleuvoir.compose_minecraft.platform.ui.draw.minecraftTexture
import moe.forpleuvoir.ibukigourd.ui.sokitsu.SokitsuScreen
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.resources.Identifier
import moe.forpleuvoir.hiirosakura.ui.compat.NumberFieldStyle
import moe.forpleuvoir.hiirosakura.ui.compat.LocalNumberFieldStyle
import moe.forpleuvoir.hiirosakura.ui.compat.TipBox
import moe.forpleuvoir.hiirosakura.ui.compat.BlitTexture
import moe.forpleuvoir.hiirosakura.ui.compat.closeScreen
import moe.forpleuvoir.hiirosakura.ui.compat.openComposeScreen

/*
 * 旧版 material3 / 旧版 IbukiGourd 的 UI 入口在本项目的兼容层。
 *
 * 这些符号在 IG 1.0.0-alpha 的 UI 重写里被删除或被新体系取代；本项目调用点较多，逐个改写成本高，
 * 因此在这里按旧签名提供等价实现，内部转交 sokitsu / compose-minecraft。语义有损失的地方在各自
 * KDoc 里标注。
 */

/** 旧版字段样式分档。像素主题下外观由组件 meta 决定，这里只保留取值以兼容旧调用点。 */
enum class NumberFieldStyle { Default, Outlined }

/** 旧版 `LocalNumberFieldStyle`；新版无需表达字段样式，读取方忽略其值。 */
val LocalNumberFieldStyle = staticCompositionLocalOf { NumberFieldStyle.Outlined }

/**
 * 旧版 `TipBox`：给内容挂一个悬浮提示。
 *
 * 旧实现基于 material3 的 `TooltipBox`，这里改用 sokitsu 的 [tooltip] 修饰符；`positionProvider` /
 * `state` 一类参数不再支持。
 */
@Composable
fun TipBox(
    tooltip: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier.tooltip { tooltip() }) { content() }
}

/**
 * 旧版 `BlitTexture`：直接按原版纹理绘制。
 *
 * 旧实现走 Skia 离屏渲染（已随 UI 栈移除），这里改为 compose-minecraft 的
 * [minecraftTexture] 修饰符，按最近邻采样（像素风素材）。`contentScale` / `alpha` /
 * `colorFilter` 参数保留但不再参与绘制（缩放交由外层 Modifier 决定）。
 */
@Composable
fun BlitTexture(
    texture: Identifier,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = 1f,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = FilterQuality.None,
) {
    Spacer(modifier.minecraftTexture(texture, filterMode = FilterMode.NEAREST))
}

/**
 * 旧版 `closeScreen()`：在主线程关闭当前屏幕（回到父屏）。
 */
fun closeScreen() {
    Minecraft.getInstance().execute {
        Minecraft.getInstance().gui.screen()?.onClose()
    }
}

/**
 * 旧版 `openComposeScreen`：转交 [SokitsuScreen.open]。
 *
 * 旧签名里的 `shouldRenderLevel` 是 `(ComposeScreen) -> Boolean`，这里放宽为无参 lambda；
 * `entryAnimation` 由新版屏幕动画取代。
 */
fun openComposeScreen(
    pauseGame: Boolean = false,
    renderParent: Boolean = false,
    parentScreen: Screen? = null,
    shouldRenderLevel: () -> Boolean = { true },
    content: @Composable () -> Unit,
): ComposeScreen = SokitsuScreen.open(
    parent = parentScreen,
    renderParentScreen = renderParent,
    disableWorldRender = !shouldRenderLevel(),
    pauseGame = pauseGame,
    content = content,
)
