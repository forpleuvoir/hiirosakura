package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import moe.forpleuvoir.ibukigourd.ui.util.FabVisibilityState
import moe.forpleuvoir.ibukigourd.ui.util.isQuickAction
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import androidx.compose.ui.graphics.RectangleShape
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Button
import moe.forpleuvoir.hiirosakura.ui.icon.VectorIcon
import moe.forpleuvoir.hiirosakura.ui.compat.FloatingActionButtonMenu
import moe.forpleuvoir.hiirosakura.ui.compat.FloatingActionButtonMenuItem
import moe.forpleuvoir.ibukigourd.ui.util.FabScrollVisibility
import moe.forpleuvoir.ibukigourd.ui.util.fabScrollVisibility

fun interface EntryEditor<T> {
    @Composable
    fun invoke(
        addAction: (T) -> Unit,
        defaultValue: () -> T,
        onDismiss: () -> Unit
    )
}

data class AddMenuOption<T>(
    val text: @Composable () -> Unit,
    val defaultValue: () -> T,
    val editor: EntryEditor<T>?
)

@Composable
fun <T> FloatingAddButton(
    modifier: Modifier = Modifier,
    fabVisibilityState: FabScrollVisibility? = null,
    addMenuOptions: List<AddMenuOption<T>>,
    addAction: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }


    var activeEditor: EntryEditor<T>? by remember { mutableStateOf(null) }

    var currentDefaultValue by remember { mutableStateOf<(() -> T)?>(null) }

    FloatingActionButtonMenu(
        expanded = expanded,
        modifier = modifier
            .then(
                if (fabVisibilityState != null && !expanded)
                    Modifier.fabScrollVisibility(fabVisibilityState)
                else Modifier
            ),
        button = {
            Button(
                onClick = { expanded = !expanded },
                modifier = Modifier.size(50.dp),
            ) {
                val rotation by animateFloatAsState(
                    targetValue = if (expanded) 45f else 0f,
                    animationSpec = tween(150),
                )
                Icon(
                    Icons.Add,
                    modifier = Modifier.rotate(rotation),
                )
            }}
    ) {
        addMenuOptions.forEach { option ->
            FloatingActionButtonMenuItem(
                onClick = {
                    if (isQuickAction || option.editor == null) {
                        addAction(option.defaultValue())
                    } else {
                        activeEditor = option.editor
                        currentDefaultValue = option.defaultValue
                    }
                    expanded = false
                },
                text = option.text,
                icon = {}
            )
        }
    }

    if (activeEditor != null && currentDefaultValue != null) {
        activeEditor!!.invoke(
            addAction,
            currentDefaultValue!!,
            onDismiss = { activeEditor = null }
        )
    }
}