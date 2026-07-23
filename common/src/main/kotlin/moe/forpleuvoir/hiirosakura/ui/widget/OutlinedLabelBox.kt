package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldLabelPosition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape

@Composable
fun OutlinedLabelBox(
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isError: Boolean = false,
    interactionSource: MutableInteractionSource = remember {
        MutableInteractionSource()
    },
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    contentPadding: PaddingValues =
        OutlinedTextFieldDefaults.contentPadding(),
    content: @Composable () -> Unit,
) {
    val decorator = OutlinedTextFieldDefaults.decorator(
        state = rememberTextFieldState(),
        enabled = enabled,
        lineLimits = TextFieldLineLimits.MultiLine(),
        outputTransformation = null,
        interactionSource = interactionSource,
        labelPosition = TextFieldLabelPosition.Attached(
            alwaysMinimize = true,
        ),
        label = {
            label()
        },
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
        modifier = modifier,
        propagateMinConstraints = true,
    ) {
        decorator.Decoration {
            content()
        }
    }
}