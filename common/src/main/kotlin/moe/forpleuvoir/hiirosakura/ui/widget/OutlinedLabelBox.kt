package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldLabelPosition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Composable
fun OutlinedLabelBox(
    label: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isError: Boolean = false,
    interactionSource: MutableInteractionSource = remember {
        MutableInteractionSource()
    },
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    contentPadding: PaddingValues = OutlinedTextFieldDefaults.contentPadding(),
    /**
     * Label 距离逻辑起始侧的距离。
     *
     * null 时跟随 contentPadding 的 start padding。
     */
    labelStartPadding: Dp? = 16.dp,
    content: @Composable BoxScope.() -> Unit,
) {
    val layoutDirection = LocalLayoutDirection.current
    val density = LocalDensity.current

    val contentStartPadding =
        contentPadding.calculateStartPadding(layoutDirection)

    val resolvedLabelStartPadding =
        labelStartPadding ?: contentStartPadding

    /*
     * decorator 本身已经按照 contentPadding 放置了 Label，
     * 因此这里只需要计算额外偏移量。
     */
    val labelOffsetPx = with(density) {
        (resolvedLabelStartPadding - contentStartPadding).roundToPx()
    }

    val labelAlignment = remember(labelOffsetPx) {
        LabelStartOffsetAlignment(labelOffsetPx)
    }

    val decorator = OutlinedTextFieldDefaults.decorator(
        state = rememberTextFieldState(),
        enabled = enabled,
        lineLimits = TextFieldLineLimits.MultiLine(),
        outputTransformation = null,
        interactionSource = interactionSource,
        labelPosition = TextFieldLabelPosition.Attached(
            alwaysMinimize = true,
            minimizedAlignment = labelAlignment,
        ),
        label = label?.let { { it.invoke() } },
        isError = isError,
        colors = colors,
        contentPadding = contentPadding,
        container = {
            OutlinedTextFieldDefaults.Container(
                enabled = enabled,
                isError = isError,
                interactionSource = interactionSource,
                colors = colors,
                shape = shape,
            )
        },
    )

    Box(
        modifier = modifier.then(
            if (label != null) {
                Modifier.padding(top = 8.dp)
            } else {
                Modifier
            }
        ),
        propagateMinConstraints = true,
    ) {
        decorator.Decoration {
            content()
        }
    }
}

private class LabelStartOffsetAlignment(
    private val offsetPx: Int,
) : Alignment.Horizontal {

    override fun align(
        size: Int,
        space: Int,
        layoutDirection: LayoutDirection,
    ): Int {
        val start = Alignment.Start.align(
            size = size,
            space = space,
            layoutDirection = layoutDirection,
        )

        val position = when (layoutDirection) {
            LayoutDirection.Ltr -> start + offsetPx
            LayoutDirection.Rtl -> start - offsetPx
        }

        return position.coerceIn(
            minimumValue = 0,
            maximumValue = (space - size).coerceAtLeast(0),
        )
    }
}