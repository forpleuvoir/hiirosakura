package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.ui.widget.serializereditor.SerializeElementEditor
import moe.forpleuvoir.hiirosakura.ui.widget.serializereditor.SerializeElementType
import moe.forpleuvoir.hiirosakura.ui.widget.serializereditor.matchesType
import moe.forpleuvoir.hiirosakura.ui.widget.truncateLines
import moe.forpleuvoir.hiirosakura.util.logger
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.util.registryAccess
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.default.EditNote
import moe.forpleuvoir.ibukigourd.ui.preset.EnumSelector
import moe.forpleuvoir.ibukigourd.ui.preset.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.preset.Text
import moe.forpleuvoir.ibukigourd.ui.toast.ToastContainer
import moe.forpleuvoir.ibukigourd.ui.toast.ToastHandler
import moe.forpleuvoir.ibukigourd.util.NebulaOps
import moe.forpleuvoir.nebula.serialization.base.SerializeArray
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeNull
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.base.SerializePrimitive
import net.minecraft.core.component.DataComponentType
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.level.storage.loot.functions.SetAttributesFunction.modifier

private val logger = logger("DefaultComponentWrapper")

private fun rootTypeOf(element: SerializeElement): SerializeElementType = when (element) {
    is SerializeObject -> SerializeElementType.Object
    is SerializeArray -> SerializeElementType.Array
    is SerializePrimitive -> when {
        element.isString -> SerializeElementType.String
        element.isBoolean -> SerializeElementType.Boolean
        element.isNumber -> when (element.asNumber) {
            is Int -> SerializeElementType.Int
            is Long -> SerializeElementType.Long
            is Float -> SerializeElementType.Float
            is Double -> SerializeElementType.Double
            is Byte -> SerializeElementType.Byte
            is Short -> SerializeElementType.Short
            else -> SerializeElementType.Object
        }
        else -> SerializeElementType.Object
    }
    is SerializeNull -> SerializeElementType.Null
}

@Suppress("UNCHECKED_CAST")
@Composable
fun <C : Any> DefaultComponentWrapper(
    key: Identifier,
    componentType: DataComponentType<C>,
    component: Any?,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp, Alignment.End),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    onValueChange: (C) -> Unit
) {
    DataComponentEntryRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
        var showEditDialog by remember { mutableStateOf(false) }
        Box(Modifier.height(DataComponentEditorDefaults.entrySize.height), contentAlignment = Alignment.CenterEnd) {
            IconButton({ showEditDialog = true }) {
                Icon(Icons.EditNote, null)
            }
            if (showEditDialog) {
                var data by remember { mutableStateOf<SerializeElement?>(null) }
                LaunchedEffect(showEditDialog) {
                    runCatching {
                        componentType.codecOrThrow()
                            .encodeStart(
                                registryAccess!!.createSerializationContext(NebulaOps),
                                component as C
                            )
                            .orThrow
                    }.onSuccess {
                        data = it
                    }.onFailure {
                        logger.error(it)
                        ToastHandler.showContent {
                            Text(it.stackTraceToString().truncateLines(8))
                        }
                    }
                }
                data?.let { element ->
                    DefaultComponentEditorDialog(
                        key = key,
                        componentType = componentType,
                        initialData = element,
                        onDismiss = { showEditDialog = false },
                        onValueChange = { newComponent ->
                            onValueChange(newComponent)
                            showEditDialog = false
                        },
                        modifier = Modifier.padding(24.dp),
                    )
                }
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
@Composable
private fun <C : Any> DefaultComponentEditorDialog(
    key: Identifier,
    componentType: DataComponentType<C>,
    initialData: SerializeElement,
    onDismiss: () -> Unit,
    onValueChange: (C) -> Unit,
    modifier: Modifier = Modifier,
) {
    var rootType by remember { mutableStateOf(rootTypeOf(initialData)) }
    var data by remember { mutableStateOf(initialData) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    FlexibleDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        title = { Text(Component.literal(key.toString())) },
        onConfirmRequest = {
            runCatching {
                componentType.codecOrThrow()
                    .parse(registryAccess!!.createSerializationContext(NebulaOps), data)
                    .orThrow
            }.fold(
                onSuccess = { result ->
                    onValueChange(result)
                    true
                },
                onFailure = { e ->
                    logger.error(e)
                    errorMessage = e.message
                    false
                }
            )
        },
        content = {
            Column {
                EnumSelector(
                    selected = rootType,
                    onSelect = { newType ->
                        rootType = newType
                        if (!data.matchesType(newType)) {
                            data = newType.defaultValue.deepCopy()
                        }
                    },
                    items = SerializeElementType.entries,
                    label = { Text(HSLang.ItemEditor.rootType) },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))
                SerializeElementEditor(
                    data = data,
                    onDataChange = { data = it },
                    modifier = Modifier.fillMaxSize(),
                )
                errorMessage?.let {
                    Text(
                        Component.literal(it),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    )
}