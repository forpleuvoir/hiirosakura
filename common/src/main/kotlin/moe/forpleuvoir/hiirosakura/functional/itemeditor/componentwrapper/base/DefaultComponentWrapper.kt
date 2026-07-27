package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.default.EditNote
import net.minecraft.core.component.DataComponentType
import net.minecraft.resources.Identifier

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
                //TODO 实现通用的编辑器
            }
        }
    }
}

//
//@Suppress("UNCHECKED_CAST")
//fun <C : Any> ContainerScope.DefaultComponentWrapper(
//    key: Identifier,
//    componentType: DataComponentType<C>,
//    component: Any?,
//    removeAction: () -> Unit,
//    modifier: Modifier = Modifier,
//    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f, Alignment.Right),
//    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
//    onValueChange: (C, Boolean) -> Unit
//) = DataComponentWrapperRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
//    Button(Modifier.hoverText(IGLang.edit)) {
//        Icon(IconTextures.EDIT)
//        click {
//            runCatching {
//                componentType.codecOrThrow()
//                    .encodeStart(registryAccess!!.createSerializationContext(NebulaOps), component as C)
//                    .orThrow
//            }.onFailure {
//                Toast.showToast(Literal(it.message ?: "unknown error").withColor(Colors.RED))
//                DataComponentWrappers.log.error(it)
//            }.onSuccess {
//                var data = it
//                var result = component
//                //Editor
//                DataComponentEditor(
//                    key.asTranslateText(),
//                    { result!! as C to true },
//                    onValueChange,
//                    modifier = Modifier.width(340f),
//                    onConfirm = {
//                        var r = true
//                        runCatching {
//                            result = componentType.codecOrThrow()
//                                .parse(registryAccess!!.createSerializationContext(NebulaOps), data)
//                                .orThrow
//                        }.onFailure {
//                            Toast.showToast(Literal(it.message ?: "unknown error").withColor(Colors.RED))
//                            DataComponentWrappers.log.error(it)
//                            r = false
//                        }
//                        r
//                    }
//                ) {
//                    SerializeElementEditor(data, modifier = Modifier.width(330f).height(190f)) {
//                        data = it
//                    }
//                }.open()
//            }
//
//        }
//    }
//}
//
//
//fun <C : Any> DefaultComponentBuilder(
//    key: Identifier,
//    componentType: DataComponentType<C>,
//    onValueChange: (C, Boolean) -> Unit,
//): IGScreenImpl {
//    var data: SerializeElement = SerializeObject()
//    var editorRecompose by lateInitValueOf {}
//    val type = mutableStateOf(SerializeElementType.Object)
//    type.subscribe {
//        data = it.defaultValue
//        editorRecompose()
//    }
//    var result: C by lateInitValueOf()
//    return DataComponentEditor(
//        key.asTranslateText(),
//        { result to true },
//        onValueChange,
//        modifier = Modifier.maxWidth(340f),
//        onConfirm = {
//            var r = true
//            runCatching {
//                result = componentType.codecOrThrow()
//                    .parse(registryAccess!!.createSerializationContext(NebulaOps), data)
//                    .orThrow
//            }.onFailure {
//                Toast.showToast(Literal(it.message ?: "unknown error").withColor(Colors.RED))
//                DataComponentWrappers.log.error(it)
//                r = false
//            }
//            r
//        }
//    ) {
//        EnumSelector(type, SerializeElementType.entries, modifier = Modifier.width(70f))
//        Box {
////            val modifier = when (data) {
////                is Map<*, *>, is Iterable<*> -> Modifier.width(330f).height(190f)
////                is Boolean                   -> Modifier.width(40f)
////                else                         -> Modifier.width(160f)
////            }
//            val modifier = when {
//                data.isObject || data.isArray -> Modifier.width(330f).height(190f)
//                data.isPrimitive && (data as SerializePrimitive).isBoolean -> Modifier.width(40f)
//                else -> Modifier.width(160f)
//            }
//            SerializeElementEditor(data.toSerializeElement(), modifier = modifier) {
//                data = it
//            }
//        }.apply {
//            editorRecompose = { this.executeRecompose() }
//        }
//    }
//}