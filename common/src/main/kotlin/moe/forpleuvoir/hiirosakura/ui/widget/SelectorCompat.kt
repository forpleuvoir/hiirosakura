package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import moe.forpleuvoir.ibukigourd.text.translateComment
import moe.forpleuvoir.ibukigourd.text.translateCommentKey
import moe.forpleuvoir.ibukigourd.text.translateText
import moe.forpleuvoir.ibukigourd.ui.selector.Selector
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import net.minecraft.locale.Language

/*
 * 旧版 IbukiGourd 的 `ui.preset.{Selector,StringSelector,EnumSelector}` 在本项目的兼容层。
 *
 * 新版上游只保留 `ui.selector.Selector(selected, onSelect, items, content, modifier, enabled,
 * itemEquals, itemContent, itemLeadingIcon, itemTrailingIcon, searchFilter, expandStyle,
 * onExpandedChange)`；本项目十多个专用选择器（活动类型、稀有度、音效、附魔、粒子……）都建立在这两个
 * 包装之上，因此按旧签名复刻一层，实现转交新版 `Selector`：
 *
 * - 标签画在触发件上方（像素风没有浮动标签），由 [OutlinedLabelBox] 承担；
 * - `textStyle` / `interactionSource` / `shape` 保留签名但不参与绘制 —— 外观由主题与组件 meta 决定；
 * - 旧版 `searchFilter` 是 `(查询, 选项) -> Boolean`，新版是 `(选项, 查询) -> Boolean`，此处适配。
 */

/**
 * 字符串单项选择器。
 *
 * @param label 标签；为 null 时只有触发件
 * @param searchFilter 搜索过滤，参数顺序为「查询, 选项」
 */
@Composable
fun StringSelector(
    selected: String,
    onSelect: (String) -> Unit,
    items: List<String>,
    itemEquals: (String, String) -> Boolean = { a, b -> a == b },
    content: @Composable (String) -> Unit = { Text(it) },
    label: @Composable (() -> Unit)? = null,
    itemContent: @Composable (String, Boolean) -> Unit = { item, _ -> Text(item) },
    enabled: Boolean = true,
    searchFilter: ((String, String) -> Boolean)? = null,
    modifier: Modifier = Modifier,
    itemLeadingIcon: ((Boolean) -> (@Composable (String) -> Unit)?)? = null,
    itemTrailingIcon: ((Boolean) -> (@Composable (String) -> Unit)?)? = null,
    textStyle: TextStyle = TextStyle.Default,
    interactionSource: MutableInteractionSource? = null,
    shape: Shape? = null,
    contentPadding: PaddingValues = LabeledFieldDefaults.contentPadding,
) {
    OutlinedLabelBox(label = label, modifier = modifier, contentPadding = contentPadding) {
            Selector(
            selected = selected,
            onSelect = onSelect,
            items = items,
            itemEquals = itemEquals,
            content = content,
            itemContent = itemContent,
            enabled = enabled,
            searchFilter = searchFilter?.let { filter -> { item, query -> filter(query, item) } },
            modifier = Modifier.fillMaxWidth(),
            itemLeadingIcon = itemLeadingIcon,
            itemTrailingIcon = itemTrailingIcon,
        )
    }
}

/**
 * 枚举单项选择器；选项文案与注释沿用 `Enum.translateText` / `translateComment` 的键约定。
 *
 * @param items 候选值，缺省为该枚举的全部常量
 * @param searchFilter 搜索过滤，参数顺序为「查询, 选项」
 */
@Composable
fun <E : Enum<E>> EnumSelector(
    selected: E,
    onSelect: (E) -> Unit,
    items: List<E> = selected.declaringJavaClass.enumConstants?.toList() ?: listOf(selected),
    itemEquals: (E, E) -> Boolean = { a, b -> a == b },
    content: @Composable (E) -> Unit = { Text(it.translateText) },
    label: @Composable (() -> Unit)? = null,
    itemContent: @Composable (E, Boolean) -> Unit = { item, _ ->
        val tip = if (Language.getInstance().has(item.translateCommentKey)) {
            Modifier.tooltip { Text(item.translateComment) }
        } else {
            Modifier
        }

        Text(item.translateText, tip)
    },
    enabled: Boolean = true,
    searchFilter: ((String, E) -> Boolean)? = null,
    modifier: Modifier = Modifier,
    itemLeadingIcon: ((Boolean) -> (@Composable (E) -> Unit)?)? = null,
    itemTrailingIcon: ((Boolean) -> (@Composable (E) -> Unit)?)? = null,
    textStyle: TextStyle = TextStyle.Default,
    interactionSource: MutableInteractionSource? = null,
    shape: Shape? = null,
    contentPadding: PaddingValues = LabeledFieldDefaults.contentPadding,
) {
    OutlinedLabelBox(label = label, modifier = modifier, contentPadding = contentPadding) {
            Selector(
            selected = selected,
            onSelect = onSelect,
            items = items,
            itemEquals = itemEquals,
            content = content,
            itemContent = itemContent,
            enabled = enabled,
            searchFilter = searchFilter?.let { filter -> { item, query -> filter(query, item) } },
            modifier = Modifier.fillMaxWidth(),
            itemLeadingIcon = itemLeadingIcon,
            itemTrailingIcon = itemTrailingIcon,
        )
    }
}
