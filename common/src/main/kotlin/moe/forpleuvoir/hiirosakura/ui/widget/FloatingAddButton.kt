package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.defaults.Add
import moe.forpleuvoir.ibukigourd.ui.preset.modifier.fabVisibilityAnimation
import moe.forpleuvoir.ibukigourd.ui.preset.state.FabVisibilityState
import moe.forpleuvoir.ibukigourd.ui.preset.state.isQuickAction

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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun <T> FloatingAddButton(
    modifier: Modifier = Modifier,
    fabVisibilityState: FabVisibilityState? = null,
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
                    Modifier.fabVisibilityAnimation(fabVisibilityState)
                else Modifier
            ),
        button = {
            FloatingActionButton(
                modifier = Modifier.size(50.dp),
                shape = MaterialTheme.shapes.extraLarge,
                onClick = { expanded = !expanded },
            ) {
                val rotation by animateFloatAsState(
                    targetValue = if (expanded) 45f else 0f,
                    animationSpec = tween(150)
                )
                Icon(
                    imageVector = Icons.Add,
                    contentDescription = "Add",
                    modifier = Modifier.rotate(rotation)
                )
            }
        }
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