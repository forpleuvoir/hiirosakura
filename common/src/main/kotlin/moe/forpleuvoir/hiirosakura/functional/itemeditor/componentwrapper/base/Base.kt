package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.util.asTranslateKey
import moe.forpleuvoir.hiirosakura.util.asTranslateText
import moe.forpleuvoir.hiirosakura.util.identifier
import moe.forpleuvoir.hiirosakura.util.logger
import moe.forpleuvoir.ibukigourd.text.Translatable
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.preset.RemoveConfirmButton
import moe.forpleuvoir.ibukigourd.ui.preset.Text
import moe.forpleuvoir.ibukigourd.ui.preset.modifier.plainTooltip
import moe.forpleuvoir.ibukigourd.ui.util.toComposeColor
import net.minecraft.locale.Language
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier

val unknownComponentType = identifier("unknown_component_type")

private val logger = logger("ComponentWrapper:Base")


object DataComponentEditorDefaults {

    val entrySize: DpSize
        @Composable @ReadOnlyComposable get() = LocalEntrySize.current


    val LocalEntrySize = staticCompositionLocalOf { DpSize(320.dp, 64.dp) }

}

sealed class CommentAppendMode {
    data object Tooltip : CommentAppendMode()
    data class Append(val newLine: Boolean) : CommentAppendMode()
    data object Replace : CommentAppendMode()
    data object None : CommentAppendMode()
}

@Composable
fun Text(
    identifier: Identifier,
    prefix: String? = null,
    suffix: String? = null,
    fallback: String? = null,
    commentAppendMode: CommentAppendMode = CommentAppendMode.Tooltip,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    autoSize: TextAutoSize? = null,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    inlineContent: Map<String, InlineTextContent> = mapOf(),
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
) {
    val key = identifier.asTranslateKey(prefix, suffix)
    val commentKey = "$key.comment"
    val hasComment = Language.getInstance().has(commentKey)

    val displayText = when (commentAppendMode) {
        is CommentAppendMode.Replace -> if (hasComment) Translatable(commentKey)
        else Translatable(key, fallback ?: identifier.toString())

        else                         -> Translatable(key, fallback ?: identifier.toString())
    }

    val actualModifier = if (hasComment && commentAppendMode is CommentAppendMode.Tooltip) {
        modifier.plainTooltip { Text(Translatable(commentKey)) }
    } else {
        modifier
    }

    Text(
        if (hasComment && commentAppendMode is CommentAppendMode.Append) {
            val sep = if (commentAppendMode.newLine) "\n" else " "
            val comment = Language.getInstance().getOrDefault(commentKey)
            val original = Language.getInstance().getOrDefault(key)
            Component.literal("$original$sep$comment")
        } else {
            displayText
        },
        actualModifier,
        color,
        autoSize,
        fontSize,
        fontStyle,
        fontWeight,
        fontFamily,
        letterSpacing,
        textDecoration,
        textAlign,
        lineHeight,
        overflow,
        softWrap,
        maxLines,
        minLines,
        inlineContent,
        onTextLayout,
        style,
    )
}

@Composable
fun DataComponentEntryRow(
    key: Identifier,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp, Alignment.End),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val backgroundColor by animateColorAsState(
        targetValue = if (isHovered) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        animationSpec = tween(200),
        label = "bgHoverHighlight"
    )
    Row(
        modifier = modifier
            .hoverable(interactionSource)
            .background(backgroundColor, MaterialTheme.shapes.medium)
            .fillMaxWidth()
            .padding(20.dp, 8.dp, 12.dp, 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val style = LocalTextStyle.current.copy(
            color = if (DataComponentWrappers.isAdaptedComponent(key))
                moe.forpleuvoir.nebula.common.color.Color.fromHSV(195f / 360f, 1f, 1f).toComposeColor
            else
                moe.forpleuvoir.nebula.common.color.Color.fromHSV(5f / 360f, .6f, 1f).toComposeColor
        )
        ProvideTextStyle(style) {
            Column(modifier = Modifier.weight(1f, false)) {
                Text(key, overflow = TextOverflow.Ellipsis, maxLines = 1)
                val hasTranslation = Language.getInstance().has(key.asTranslateKey())
                if (hasTranslation) {
                    Text(Component.literal(key.toString()), fontSize = MaterialTheme.typography.bodySmall.fontSize)
                }
            }
        }

        Row(
            horizontalArrangement = horizontalArrangement,
            verticalAlignment = verticalAlignment,
        ) {
            content()
            RemoveConfirmButton(
                HSLang.Common.deleteConfirm(key.asTranslateText().plainText).plainText,
                removeAction
            )
        }
    }
}