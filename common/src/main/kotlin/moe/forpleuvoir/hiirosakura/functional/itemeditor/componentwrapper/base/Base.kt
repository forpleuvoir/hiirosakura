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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.util.asTranslateText
import moe.forpleuvoir.hiirosakura.util.identifier
import moe.forpleuvoir.hiirosakura.util.logger
import moe.forpleuvoir.ibukigourd.text.Translatable
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.preset.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.preset.RemoveConfirmButton
import moe.forpleuvoir.ibukigourd.ui.preset.Text
import moe.forpleuvoir.ibukigourd.ui.preset.modifier.plainTooltip
import moe.forpleuvoir.nebula.common.util.primitive.either
import net.minecraft.locale.Language
import net.minecraft.network.chat.Style
import net.minecraft.resources.Identifier

val unknownComponentType = identifier("unknown_component_type")

private val logger = logger("ComponentWrapper:Base")


object DataComponentEditorDefaults {


}

@Composable
fun Text(
    identifier: Identifier,
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
    val text = identifier.asTranslateText()
    val commentKey = "${identifier.toLanguageKey()}.comment"
    if (Language.getInstance().has(commentKey)) {
        modifier.plainTooltip {
            Text(Translatable(commentKey))
        }
    }
    Text(
        text,
        modifier,
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
fun IdentifierText(
    key: Identifier,
    style: Style? = Style.EMPTY,
    modifier: Modifier = Modifier,
) {
    val text = key.asTranslateText().apply {
        if (style != null) setStyle(style)
    }
    val commentKey = "${key.toLanguageKey()}.comment"
    if (Language.getInstance().has(commentKey)) {
        modifier.plainTooltip {
            Text(Translatable(commentKey))
        }
    }
    return Text(text, modifier)
}

@Composable
fun <C : Any> DataComponentEditor(
    newComponent: () -> C,
    onValueChange: (C) -> Unit,
    title: @Composable () -> Unit,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Boolean = { true },
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) = FlexibleDialog(
    modifier = modifier,
    onDismissRequest = onDismissRequest,
    title = title,
    onConfirmRequest = {
        onValueChange(newComponent())
        true
    },
    content = content
)

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
            color = DataComponentWrappers.isAdaptedComponent(key).either(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.error,
            )
        )
        ProvideTextStyle(style) {
            Text(key, modifier = Modifier.weight(1f, false).widthIn(max = 420.dp), overflow = TextOverflow.Ellipsis, maxLines = 1)
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