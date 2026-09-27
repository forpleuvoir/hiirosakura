package moe.forpleuvoir.hiirosakura.ui.widget.serializereditor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.nebula.serialization.base.SerializeArray
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeNull
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.base.SerializePrimitive
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Surface

@Composable
internal fun keyLabelStyle(): TextStyle =
    SokitsuTheme.typography.body.copy(fontFamily = FontFamily.Monospace)

@Composable
fun SerializeElementEntryEditor(
    key: String,
    data: SerializeElement,
    keyWrapper: @Composable RowScope.() -> Unit = { KeyLabel(text = key) },
    onValueChange: (SerializeElement) -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
) {
    ElementEntryRow(
        keyWrapper = keyWrapper,
        actions = actions,
        verticalAlignment = when (data) {
            is SerializeObject, is SerializeArray -> Alignment.Top
            else -> Alignment.CenterVertically
        },
    ) {
        when (data) {
            is SerializeObject -> SerializeObjectEntryEditor(
                serializeObject = data,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
            )
            is SerializeArray -> SerializeArrayEntryEditor(
                serializeArray = data,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
            )
            is SerializePrimitive -> SerializePrimitiveEditor(
                serializePrimitive = data,
                onValueChange = onValueChange,
            )
            is SerializeNull -> NullEditor()
        }
    }
}

@Composable
fun ElementEntryRow(
    keyWrapper: @Composable RowScope.() -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        verticalAlignment = verticalAlignment,
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        keyWrapper()
        content()
        actions()
    }
}

@Composable
fun KeyLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = SokitsuTheme.colorScheme.onSurfaceVariant,
    onClick: (() -> Unit)? = null,
) {
    Text(
        text = "$text :",
        style = keyLabelStyle(),
        color = color,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier.then(
            if (onClick != null) {
                Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable(onClick = onClick)
            } else {
                Modifier
            }
        ),
    )
}

@Composable
private fun NullEditor() {
    Surface(
        
        color = SokitsuTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
    ) {
        Text(
            text = "null",
            style = SokitsuTheme.typography.button.copy(fontFamily = FontFamily.Monospace),
            color = SokitsuTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
        )
    }
}