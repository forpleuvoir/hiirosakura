package moe.forpleuvoir.hiirosakura.ui.compat

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.ui.widget.LabeledFieldDefaults
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Button
import moe.forpleuvoir.ibukigourd.ui.sokitsu.ButtonDefaults
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Slider
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Surface
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.LocalColorScheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IconButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import androidx.compose.ui.text.AnnotatedString
import moe.forpleuvoir.hiirosakura.ui.util.rememberClipboardWriter
import androidx.compose.runtime.remember
import net.minecraft.client.gui.screens.Screen
import moe.forpleuvoir.compose_minecraft.platform.screen.ComposeScreen
import moe.forpleuvoir.ibukigourd.render.extension.texture.IGTexture
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.tween

/*
 * 旧版 material3 组件的本项目兼容实现（第二组）。
 *
 * 这些组件在 IG 的 UI 重写里随 material3 一起消失，本项目调用点用 sokitsu 组件等价实现：
 * 外观走主题槽位，m3 特有的形状/高度一类参数保留签名但不参与绘制。
 */

/**
 * 旧版 `OutlinedToggleButton`：选中态用容器色板表达的二态按钮。
 *
 * @param checked 是否选中
 * @param onCheckedChange 切换回调
 */
@Composable
fun OutlinedToggleButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    val scheme = LocalColorScheme.current
    Button(
        onClick = { onCheckedChange(!checked) },
        modifier = modifier,
        enabled = enabled,
        colors = if (checked) {
            ButtonDefaults.colors(color = scheme.primaryContainer, contentColor = scheme.onPrimaryContainer)
        } else {
            ButtonDefaults.colors()
        },
        content = content,
    )
}

/** 旧版 `FloatingActionButton`：sokitsu [Button] 等价实现（无悬浮阴影）。 */
@Composable
fun FloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) = Button(onClick = onClick, modifier = modifier, enabled = enabled, content = content)

/** 旧版 `FilledTonalButton`：sokitsu [Button] 等价实现。 */
@Composable
fun FilledTonalButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) = Button(onClick = onClick, modifier = modifier, enabled = enabled, content = content)

/** 旧版 `ElevatedCard`：sokitsu [Surface] 等价实现（高度由素材决定）。 */
@Composable
fun ElevatedCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) = Surface(modifier = modifier, content = content)

/** 旧版 `OutlinedCard`：sokitsu [Surface] + 描边色。 */
@Composable
fun OutlinedCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) = Surface(modifier = modifier, outlineColor = LocalColorScheme.current.outline, content = content)

/** 旧版 `IntSlider`：转交 sokitsu [Slider]（整数区间换算为浮点区间）。 */
@Composable
fun IntSlider(
    value: Int,
    onValueChange: (Int) -> Unit,
    valueRange: IntRange,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) = Slider(
    value = value.toFloat(),
    onValueChange = { onValueChange(it.toInt()) },
    modifier = modifier,
    enabled = enabled,
    valueRange = valueRange.first.toFloat()..valueRange.last.toFloat(),
)

/**
 * 旧版 material3 的 `OutlinedTextFieldDefaults`：像素风下只剩几何量，转交 [LabeledFieldDefaults]。
 */
object OutlinedTextFieldDefaults {

    /** 直角形状（新版没有圆角槽位）。 */
    val shape = RectangleShape

    /** 框内默认内边距。 */
    val contentPadding: PaddingValues get() = LabeledFieldDefaults.contentPadding

    /** 按边指定的框内边距。 */
    fun contentPadding(top: Dp = 8.dp, bottom: Dp = 8.dp, start: Dp = 12.dp, end: Dp = 12.dp): PaddingValues =
        LabeledFieldDefaults.contentPadding(top, bottom, start, end)
}

/**
 * 旧版 `NavigationRail`：sokitsu [Button] 列的等价实现。
 *
 * 新版上游没有导航栏组件；本项目只用到「一列可选项」，因此用等宽按钮列表达。
 */
@Composable
fun NavigationRail(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) = Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(4.dp),
    content = content,
)

/** 旧版 `NavigationRailItem`：选中态用容器色板表达的一列按钮项。 */
@Composable
fun NavigationRailItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: (@Composable () -> Unit)? = null,
) {
    val scheme = LocalColorScheme.current
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = if (selected) {
            ButtonDefaults.colors(color = scheme.primaryContainer, contentColor = scheme.onPrimaryContainer)
        } else {
            ButtonDefaults.colors()
        },
    ) {
        icon()
        label?.invoke()
    }
}

/** 旧版 `BlitTexture` 的纹理对象重载：取 [IGTexture] 的纹理 id 绘制。 */
@Composable
fun BlitTexture(
    texture: IGTexture,
    modifier: Modifier = Modifier,
) = BlitTexture(texture.textureInfo.textureId, modifier)

/** 旧版 `Card`：sokitsu [Surface] 等价实现。 */
@Composable
fun Card(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) = Surface(modifier = modifier, content = content)

/** 旧版 `RemoveButton`：悬停出现的删除按钮。 */
@Composable
fun RemoveButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    action: () -> Unit,
) = IconButton(onClick = action, modifier = modifier, enabled = enabled) {
    Icon(Icons.Delete)
}

/**
 * 旧版 `LocalAutoExpandConfigGroupLimit`：配置页里分组自动展开的数量上限。
 *
 * 新版上游的配置页不再读取该组合本地量，这里保留取值以兼容旧调用点（不再影响展开行为）。
 */
val LocalAutoExpandConfigGroupLimit = staticCompositionLocalOf { Int.MAX_VALUE }

/**
 * 旧版 `FloatingActionButtonMenu`：按钮 + 展开菜单。
 *
 * 旧实现基于 material3 的悬浮按钮菜单；这里用 [Column] 叠加表达，展开时菜单排在按钮上方。
 *
 * [horizontalAlignment] 同时作用于展开的菜单项与按钮本身：贴在右下角时传 `Alignment.End`，
 * 两者右缘对齐。
 *
 * [menuMaxHeight] 给展开的菜单项区一个有界高度并开启纵向滚动。不限高时，选项多于一屏会被
 * Column 压扁、并溢出到宿主（如对话框页脚）之上；传 [Dp.Unspecified] 保持旧的溢出行为。
 */
@Composable
fun FloatingActionButtonMenu(
    expanded: Boolean,
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.End,
    menuMaxHeight: Dp = Dp.Unspecified,
    button: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) = Column(
    modifier = modifier,
    horizontalAlignment = horizontalAlignment,
    verticalArrangement = Arrangement.spacedBy(8.dp),
) {
    // 展开的菜单项要有过渡:原来直接 if(expanded) 出现/消失,点了就是硬切。
    // 侧边按钮在菜单下方,所以从下方滑入(initialOffsetY 为正 = 起始位置更靠下)。
    AnimatedVisibility(
        visible = expanded,
        enter = fadeIn(tween(150)) +
                slideInVertically(tween(150)) { it / 3 } +
                scaleIn(tween(150), initialScale = 0.9f),
        exit = fadeOut(tween(120)) +
                slideOutVertically(tween(120)) { it / 3 } +
                scaleOut(tween(120), targetScale = 0.9f),
    ) {
        Column(
            modifier = Modifier
                .then(
                    if (menuMaxHeight == Dp.Unspecified) Modifier
                    else Modifier.heightIn(max = menuMaxHeight).verticalScroll(rememberScrollState())
                ),
            horizontalAlignment = horizontalAlignment,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = content,
        )
    }
    button()
}

/**
 * 旧版 `openComposePopupScreen`：旧实现是覆盖在当前界面上的浮层窗口。
 *
 * 新版上游没有浮层屏幕入口，这里退化为独立屏幕（[openComposeScreen]），内容与父屏一致地渲染。
 */
fun openComposePopupScreen(
    parentScreen: Screen? = null,
    content: @Composable () -> Unit,
): ComposeScreen = openComposeScreen(parentScreen = parentScreen, content = content)

/**
 * 旧版 Compose 的剪贴板管理器。
 *
 * 新版 Compose 只提供挂起式 `Clipboard`，本项目调用点用的是「直接写入文本」的旧形态，
 * 这里保留同名接口并转交 [rememberClipboardWriter]。
 */
interface ClipboardManager {

    /** 写入纯文本（富文本信息在旧接口里也只取纯文本）。 */
    fun setText(annotatedString: AnnotatedString)
}

/** 取得当前场景的剪贴板管理器。 */
@Composable
fun rememberClipboardManager(): ClipboardManager {
    val write = rememberClipboardWriter()
    return remember(write) {
        object : ClipboardManager {
            override fun setText(annotatedString: AnnotatedString) = write(annotatedString.text)
        }
    }
}

/** 旧版 `FloatingActionButtonMenuItem`：悬浮菜单里的一项。 */
@Composable
fun FloatingActionButtonMenuItem(
    onClick: () -> Unit,
    text: @Composable () -> Unit,
    icon: @Composable () -> Unit = {},
    modifier: Modifier = Modifier,
) = Button(onClick = onClick, modifier = modifier) {
    icon()
    text()
}
