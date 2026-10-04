package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base

import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.compose_minecraft.platform.ui.text.resolveDefaultFontSize
import moe.forpleuvoir.compose_minecraft.platform.ui.thenIf
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.ui.widget.DisplayField
import moe.forpleuvoir.hiirosakura.ui.widget.InlineEditField
import moe.forpleuvoir.hiirosakura.ui.widget.DisplayFieldDefaults
import moe.forpleuvoir.hiirosakura.ui.widget.LabelBox
import moe.forpleuvoir.hiirosakura.ui.widget.LabeledFieldDefaults
import moe.forpleuvoir.hiirosakura.util.asTranslateKey
import moe.forpleuvoir.hiirosakura.util.asTranslateText
import moe.forpleuvoir.hiirosakura.util.identifier
import moe.forpleuvoir.hiirosakura.util.logger
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.Translatable
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.configwrapper.*
import moe.forpleuvoir.ibukigourd.ui.editdialog.RemoveConfirmButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.*
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import moe.forpleuvoir.nebula.common.util.primitive.toTitleCase
import net.minecraft.locale.Language
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.resources.Identifier
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.Unit
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text as SokitsuText

val unknownComponentType = identifier("unknown_component_type")

private val logger = logger("ComponentWrapper:Base")


/**
 * 展示框：内容装在一个 [Surface] 里，尾部可选编辑按钮，整体填满所在槽位。
 *
 * [onEdit] 非空时编辑按钮画在框内尾部（浮层里的属性行用它）；组件列表行要把编辑按钮放到动作格，
 * 用 [DataComponentDisplayRow]，不要用这里的框内按钮。
 *
 * @param modifier 作用于整个框
 * @param tooltip 悬停气泡内容，null 时不挂
 * @param leading 框内首元素，null 时不占位
 * @param onEdit 框内编辑回调，null 时不画按钮
 * @param content 框内内容
 */
@Composable
fun DataComponentDisplay(
    modifier: Modifier = Modifier,
    tooltip: (@Composable () -> Unit)? = null,
    leading: (@Composable () -> Unit)? = null,
    onEdit: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    if (onEdit != null) {
        InlineEditField(
            onEdit = onEdit,
            modifier = modifier,
            tooltip = tooltip,
            leadingIcon = leading,
            contentAlignment = Alignment.CenterStart,
        ) { content() }
    } else {
        DisplayField(
            modifier = modifier,
            tooltip = tooltip,
            leadingIcon = leading,
            contentAlignment = Alignment.CenterStart,
            content = content,
        )
    }
}

/** 组件行内三格之间的间距。 */
private val entrySlotSpacing = 8.dp

val DataComponentCardContentPadding = PaddingValues(bottom = 12.dp, start = 12.dp, end = 12.dp)

/** 组件列表行的编辑动作：铅笔按钮，占动作格。 */
@Composable
fun DataComponentEditButton(onClick: () -> Unit) = IconButton(
    onClick = onClick,
    modifier = Modifier.tooltip { Text(component = IGLang.Misc.edit) },
    contentPadding = IconButtonDefaults.contentPadding,
    minSize = IconButtonDefaults.minSize,
) {
    Icon(Icons.Edit, scale = LocalIconScale.current)
}

/**
 * 展示框内的单行文本：超出宽度省略，**仅在真被截断时**悬停弹出全文气泡。
 *
 * 判定与配置页注释行同一套（`TextLayoutResult.hasVisualOverflow`），未截断不弹。
 *
 * 文本自身追加 `fillMaxWidth`：定宽后段落按所在槽位宽度排版，省略号铺满槽位剩余宽度。
 *
 * @param text 文本
 * @param modifier 作用于文本
 */
@Composable
fun DataComponentDisplayText(text: Component, modifier: Modifier = Modifier) {
    var truncated by remember(text) { mutableStateOf(false) }
    val interactionSource = remember(text) { MutableInteractionSource() }

    Text(
        component = text,
        modifier = modifier
            .fillMaxWidth()
            .thenIf(truncated) {
                Modifier.tooltip(
                    interactionSource = interactionSource,
                    delay = ConfigRowDefaults.TooltipDelay,
                ) {
                    Text(component = text)
                }
            }
            .hoverable(interactionSource),
        maxLines = 1,
        softWrap = false,
        overflow = TextOverflow.Ellipsis,
        onTextLayout = { truncated = it.hasVisualOverflow || it.lineCount > 1 },
    )
}

/**
 * 展示型组件行：主控件槽是 [DataComponentDisplay] 的展示框，动作格是编辑按钮，尾部是删除按钮。
 *
 * 三格与 [DataComponentEntryRow] 同宽同序，因此展示行的主控件左右边缘与数值行一致。
 *
 * @param modifier 作用于整行
 * @param frameModifier 作用于展示框
 * @param tooltip 悬停气泡内容，null 时不挂
 * @param leading 展示框内首元素，null 时不占位
 * @param onEdit 编辑回调，null 时动作格留空
 * @param content 展示框内内容
 */
@Composable
fun DataComponentDisplayRow(
    key: Identifier,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(entrySlotSpacing),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    frameModifier: Modifier = Modifier,
    tooltip: (@Composable () -> Unit)? = null,
    leading: (@Composable () -> Unit)? = null,
    onEdit: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    DataComponentEntryRow(
        key = key,
        removeAction = removeAction,
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment,
        action = { if (onEdit != null) DataComponentEditButton(onEdit) },
    ) {
        DataComponentDisplay(
            modifier = Modifier.fillMaxWidth().then(frameModifier),
            tooltip = tooltip,
            leading = leading,
            content = content,
        )
    }
}

/**
 * 带标题的字段块：标题在上、内容在下，两者之间留 2dp；内容占满整块宽度，两侧不留内边距。
 *
 * 块与块之间的间距由调用方的 [Column] 给出（`verticalArrangement` 或显式 `Spacer`），本组件不提供。
 *
 * @param component 标题文案（组件文本形态）
 * @param modifier 作用于整块
 * @param content 内容
 */
@Composable
fun DataComponentField(
    component: Component,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) = DataComponentField(
    modifier = modifier,
    title = { Text(component = component, fontSize = SokitsuTheme.typography.body.fontSize) },
) { content() }

/**
 * 以组件文本为标题的 [DataComponentSection]。
 *
 * @param component 标题文案
 * @param modifier 作用于整块
 * @param content 内容
 */
@Composable
fun DataComponentSection(
    component: Component,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) = DataComponentSection(
    modifier = modifier,
    title = { Text(component = component, fontSize = SokitsuTheme.typography.body.fontSize) },
) { content() }

/**
 * 属性行：名称列在左（`weight(1f)`），控件区在右且定宽（IG 的 [ConfigControlBlock]）。
 *
 * 行与行之间的间距由调用方的 [Column] 给出（`verticalArrangement` 或显式 `Spacer`）。
 *
 * @param modifier 作用于整行
 * @param title 名称（左列）
 * @param content 主控件（右列，占满控件区）
 */
@Composable
fun DataComponentField(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    content: @Composable () -> Unit,
) = Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
) {
    Box(modifier = Modifier.weight(1f)) { title() }
    ConfigControlBlock {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart,
        ) { content() }
    }
}

/**
 * 区块标题：转发给 [LabelBox]（标题在上、内容在下，label 字号由它统一注入）。
 *
 * @param modifier 作用于整块
 * @param title 标题
 * @param labelIndent 标题内容相对标题起始边的额外缩进
 * @param content 内容
 */
@Composable
fun DataComponentSection(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    labelIndent: Dp = LabeledFieldDefaults.LabelIndent,
    content: @Composable () -> Unit,
) = LabelBox(
    label = title,
    modifier = modifier,
    contentPadding = PaddingValues(0.dp),
    labelIndent = labelIndent,
) { content() }

/**
 * 以语言键为标题的 [DataComponentField]：标题字号取像素字体原生网格折算的最小锐利字号
 * （sokitsu 的 `Text(component)` 缺省取字体自身字号，不经 `LocalTextStyle`）。
 *
 * @param key 语言键来源
 * @param suffix 语言键后缀
 * @param fallback 无翻译时的回落文案
 * @param modifier 作用于整块
 * @param content 内容
 */
@Composable
fun DataComponentField(
    key: Identifier,
    suffix: String? = null,
    fallback: String? = suffix?.toTitleCase() ?: key.toString(),
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) = DataComponentField(
    modifier = modifier,
    title = { Text(key, suffix = suffix, fallback = fallback, fontSize = SokitsuTheme.typography.body.fontSize) },
) { content() }

/**
 * 以语言键为标题的 [DataComponentSection]，参数同上一重载。
 *
 * @param key 语言键来源
 * @param suffix 语言键后缀
 * @param fallback 无翻译时的回落文案
 * @param modifier 作用于整块
 * @param content 内容
 */
@Composable
fun DataComponentSection(
    key: Identifier,
    suffix: String? = null,
    fallback: String? = suffix?.toTitleCase() ?: key.toString(),
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) = DataComponentSection(
    modifier = modifier,
    title = { Text(key, suffix = suffix, fallback = fallback, fontSize = SokitsuTheme.typography.body.fontSize) },
    content = content
)

/**
 * 编辑浮层标题：显式给 subtitle 字号。
 *
 * sokitsu 的 `Text` 缺省字号取自字体自身、不经 `LocalTextStyle`，标题否则会退化成正文字号。
 */
@Composable
fun DataComponentDialogTitle(key: Identifier) {
    Text(key, fontSize = SokitsuTheme.typography.subtitle.fontSize)
}

/** 语言键带后缀与回落文案的编辑浮层标题，字号同 [DataComponentDialogTitle]。 */
@Composable
fun DataComponentDialogTitle(
    key: Identifier,
    suffix: String? = null,
    fallback: String? = suffix?.toTitleCase() ?: key.toString(),
) {
    Text(key, suffix = suffix, fallback = fallback, fontSize = SokitsuTheme.typography.subtitle.fontSize)
}

/** 直接渲染组件文本的编辑浮层标题，字号同 [DataComponentDialogTitle]。 */
@Composable
fun DataComponentDialogTitle(component: Component) {
    SokitsuText(component = component, fontSize = SokitsuTheme.typography.subtitle.fontSize)
}


sealed class CommentAppendMode {
    data object Tooltip : CommentAppendMode()
    data class Append(val newLine: Boolean) : CommentAppendMode()
    data object Replace : CommentAppendMode()
    data object None : CommentAppendMode()
}

/**
 * 便捷重载：直接渲染纯文本。
 *
 * 本包原有的 [Text] 以语言键为入口（`Identifier` + 前后缀）；这里补上直接给文本/组件文本的形态，
 * 便于调用点写 `Text("...")` 或 `Text(component = ...)` 而不必切到 sokitsu 的导入。
 */
@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
) = SokitsuText(
    text,
    modifier,
    color,
    fontSize = if (fontSize.isSp) fontSize else resolveDefaultFontSize(),
    overflow = overflow,
    softWrap = softWrap,
    maxLines = maxLines,
)

/**
 * 便捷重载：直接渲染组件文本，见上一条。
 *
 * `fontSize` 缺省取值与 sokitsu 的同名重载一致：组件文本走平台默认字号（`LocalDefaultFontSize`），
 * 不经 `LocalTextStyle`。
 */
@Composable
fun Text(
    component: Component,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = resolveDefaultFontSize(),
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
) = SokitsuText(
    component = component,
    modifier = modifier,
    color = color,
    fontSize = fontSize,
    overflow = overflow,
    softWrap = softWrap,
    maxLines = maxLines,
)

@Composable
fun Text(
    identifier: Identifier,
    prefix: String? = null,
    suffix: String? = null,
    fallback: String? = suffix?.toTitleCase() ?: identifier.toString(),
    commentAppendMode: CommentAppendMode = CommentAppendMode.Tooltip,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    style: Style = Style.EMPTY,
    fontSize: TextUnit = TextUnit.Unspecified,
    textAlign: TextAlign = TextAlign.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
) {
    val key = identifier.asTranslateKey(prefix, suffix)
    val commentKey = "$key.comment"
    val hasComment = Language.getInstance().has(commentKey)

    val displayText = when (commentAppendMode) {
        is CommentAppendMode.Replace -> if (hasComment) Translatable(commentKey)
        else Translatable(key, fallback)

        else                         -> Translatable(key, fallback)
    }

    val actualModifier = if (hasComment && commentAppendMode is CommentAppendMode.Tooltip) {
        modifier.tooltip {
            Text(Translatable(commentKey), fontSize = SokitsuTheme.typography.body.fontSize)
        }
    } else {
        modifier
    }

    val resolvedFontSize = if (fontSize.isSp) fontSize else resolveDefaultFontSize()

    Text(
        component = if (hasComment && commentAppendMode is CommentAppendMode.Append) {
            val sep = if (commentAppendMode.newLine) "\n" else " "
            val comment = Language.getInstance().getOrDefault(commentKey)
            val original = Language.getInstance().getOrDefault(key)
            Component.literal("$original$sep$comment")
        } else {
            displayText
        },
        modifier = actualModifier,
        color = color,
        style = style,
        fontSize = resolvedFontSize,
        textAlign = textAlign,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        onTextLayout = onTextLayout,
    )
}

/**
 * 组件列表行：左侧是组件名与 id，右侧是定宽的控件区。
 *
 * 控件区固定三格，从左到右依次是主控件槽（[ConfigControlDefaults.ControlWidth]）、动作格与删除格
 * （各 [IconButtonDefaults.minSize] 宽），格间留 [entrySlotSpacing]。三格定宽时，所有行的主控件
 * 左右边缘一致，动作按钮与删除按钮各在同一列；`action` 为 null 时动作格留空但仍占位。
 *
 * @param key 组件语言键来源
 * @param removeAction 删除回调
 * @param modifier 作用于整行
 * @param horizontalArrangement 主控件槽内多个控件之间的排布
 * @param verticalAlignment 行内控件区的垂直对齐
 * @param action 动作格内容，null 时留空
 * @param content 主控件槽内容
 */
@Composable
fun DataComponentEntryRow(
    key: Identifier,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(entrySlotSpacing),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    action: (@Composable () -> Unit)? = null,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .hoverable(interactionSource)
            .hoverHighlight(interactionSource),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 8.dp, 12.dp, 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val style = LocalTextStyle.current.copy(
                color = if (DataComponentWrappers.isAdaptedComponent(key))
                    Color.hsv(195f, 1f, 1f)
                else
                    Color.hsv(5f, 0.6f, 1f)
            )
            ProvideTextStyle(style) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(key, overflow = TextOverflow.Ellipsis, maxLines = 1)
                    val hasTranslation = Language.getInstance().has(key.asTranslateKey())
                    if (hasTranslation) {
                        Text(
                            Component.literal(key.toString()),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            fontSize = SokitsuTheme.typography.body.fontSize,
                        )
                    }
                }
            }

            val slotWidth = IconButtonDefaults.minSize.width
            // 无动作格时主控件槽补上动作格与格间距，避免行内留空
            val controlWidth = if (action == null) {
                ConfigControlDefaults.ControlWidth + entrySlotSpacing + slotWidth
            } else {
                ConfigControlDefaults.ControlWidth
            }
            Row(
                modifier = Modifier.width(ConfigControlDefaults.ControlWidth + (entrySlotSpacing + slotWidth) * 2),
                verticalAlignment = verticalAlignment,
            ) {
                Row(
                    modifier = Modifier.width(controlWidth),
                    horizontalArrangement = horizontalArrangement,
                    verticalAlignment = verticalAlignment,
                ) {
                    content()
                }

                Spacer(Modifier.width(entrySlotSpacing))

                if (action != null) {
                    Box(
                        modifier = Modifier.width(slotWidth),
                        contentAlignment = Alignment.Center,
                    ) { action() }

                    Spacer(Modifier.width(entrySlotSpacing))
                }

                Box(
                    modifier = Modifier.width(slotWidth),
                    contentAlignment = Alignment.Center,
                ) {
                    RemoveConfirmButton(
                        HSLang.Common.deleteConfirm(key.asTranslateText().plainText).plainText,
                        removeAction
                    )
                }
            }
        }
    }
}