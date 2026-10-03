package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.compose_minecraft.platform.ui.thenIf
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigControlDefaults
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigRowWrapper
import moe.forpleuvoir.ibukigourd.ui.configwrapper.configControlHeight
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IconButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IconButtonDefaults
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import moe.forpleuvoir.ibukigourd.ui.sokitsu.LocalIconScale
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Surface
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip

/**
 * 只读展示框 + **框内**右端的编辑按钮：按钮与展示内容同处一个 [Surface]，
 * 不占用控件区尾部的独立一格（对照 `ConfigControlBlock` 的 action 槽位）。
 *
 * 框高固定为 [configControlHeight]（与文本输入框同高），编辑按钮按 [InlineEditButtonMinSize] 收缩，
 * 能塞进该高度里。
 *
 * @param onEdit 打开编辑浮层
 * @param modifier 作用于展示框
 * @param tooltip 悬停内容；null 时不挂提示
 * @param leadingIcon 框内左端的前置槽位
 * @param contentAlignment 展示内容在剩余宽度里的对齐
 * @param editIconScale 编辑图标倍率，缺省 2 倍（32dp）
 * @param content 展示内容
 */
@Composable
fun InlineEditField(
    onEdit: () -> Unit,
    modifier: Modifier = Modifier,
    tooltip: (@Composable () -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    height: Dp = configControlHeight(),
    contentAlignment: Alignment = Alignment.Center,
    editIconScale: Int = 2,
    content: @Composable () -> Unit,
) {
    DisplayField(
        modifier = modifier,
        tooltip = tooltip,
        leadingIcon = leadingIcon,
        trailingIcon = {
            IconButton(onClick = onEdit) { Icon(Icons.Edit) }
        },
        height = height,
        contentAlignment = contentAlignment,
        editIconScale = editIconScale,
        content = content
    )
}

@Composable
fun DisplayField(
    modifier: Modifier = Modifier,
    tooltip: (@Composable () -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    height: Dp = configControlHeight(),
    contentAlignment: Alignment = Alignment.Center,
    editIconScale: Int = 2,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier
            .height(height)
            .thenIf(tooltip != null) { Modifier.tooltip { tooltip!!() } },
        color = DisplayFieldDefaults.FieldColor,
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(DisplayFieldDefaults.calculatePadding(leadingIcon != null, trailingIcon != null)),
            horizontalArrangement = Arrangement.spacedBy(ConfigRowWrapper.spacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CompositionLocalProvider(
                LocalIconScale provides editIconScale,
                IconButtonDefaults.LocalContentPadding provides DisplayFieldDefaults.ButtonContentPadding,
                IconButtonDefaults.LocalMinSize provides DisplayFieldDefaults.ButtonMinSize,
            ) {
                leadingIcon?.invoke()
                Box(Modifier.weight(1f), contentAlignment = contentAlignment) {
                    content()
                }
                trailingIcon?.invoke()
            }
        }
    }
}

/**
 * [DisplayField] 的缺省规格：框内自带的编辑按钮与调用方塞进框内的动作按钮（试听之类）
 * 取同一组值，才保持同形。
 */
object DisplayFieldDefaults {

    /** 框内动作按钮的最小尺寸：按 2 倍图标（32dp）留一圈，可塞进配置控件的统一高度内。 */
    val ButtonMinSize: DpSize = DpSize(40.dp, 40.dp)

    /** 框内动作按钮的内容内边距。 */
    val ButtonContentPadding: PaddingValues = ConfigControlDefaults.IconButtonPadding

    /** 展示框内边距。 */
    val FieldPadding: PaddingValues = PaddingValues(horizontal = 12.dp)

    /**
     * 展示框背景色：与文本输入框容器同色（文本输入框该色为色板的 surfaceVariant）。
     *
     * 本处自持一份取值，不引用上游的字段 token。
     */
    val FieldColor: Color
        @Composable @ReadOnlyComposable
        get() = SokitsuTheme.colorScheme.surfaceVariant

    @Composable
    fun calculatePadding(hasLeadingIcon: Boolean, hasTrailingIcon: Boolean): PaddingValues {
        val start = if (hasLeadingIcon) 12.dp else 16.dp
        val end = if (hasTrailingIcon) 12.dp else 16.dp
        return PaddingValues(start = start, end = end)
    }
}
