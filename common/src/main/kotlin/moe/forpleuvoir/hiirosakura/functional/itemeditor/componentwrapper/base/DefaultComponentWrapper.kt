package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base

import moe.forpleuvoir.hiirosakura.gui.widget.serializereditor.SerializeElementEditor
import moe.forpleuvoir.hiirosakura.gui.widget.serializereditor.SerializeElementType
import moe.forpleuvoir.hiirosakura.util.asTranslateText
import moe.forpleuvoir.hiirosakura.util.registryAccess
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.height
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.hoverText
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.maxWidth
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.width
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.base.toast.Toast
import moe.forpleuvoir.ibukigourd.gui.base.widget.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.widget.EnumSelector
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.icon.Icon
import moe.forpleuvoir.ibukigourd.gui.widget.icon.IconTextures
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Box
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.text.withColor
import moe.forpleuvoir.ibukigourd.util.NebulaOps
import moe.forpleuvoir.ibukigourd.util.lateInitValueOf
import moe.forpleuvoir.ibukigourd.util.state.mutableStateOf
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.base.SerializePrimitive
import moe.forpleuvoir.nebula.serialization.extensions.toSerializeElement
import net.minecraft.core.component.DataComponentType
import net.minecraft.resources.Identifier

@Suppress("UNCHECKED_CAST")
fun <C : Any> ContainerScope.DefaultComponentWrapper(
    key: Identifier,
    componentType: DataComponentType<C>,
    component: Any?,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f, Alignment.Right),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    onValueChange: (C, Boolean) -> Unit
) = DataComponentWrapperRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    Button(Modifier.hoverText(IGLang.edit)) {
        Icon(IconTextures.EDIT)
        click {
            runCatching {
                componentType.codecOrThrow()
                    .encodeStart(registryAccess!!.createSerializationContext(NebulaOps), component as C)
                    .orThrow
            }.onFailure {
                Toast.showToast(Literal(it.message ?: "unknown error").withColor(Colors.RED))
                DataComponentWrappers.log.error(it)
            }.onSuccess {
                var data = it
                var result = component
                //Editor
                DataComponentEditor(
                    key.asTranslateText(),
                    { result!! as C to true },
                    onValueChange,
                    modifier = Modifier.width(340f),
                    onConfirm = {
                        var r = true
                        runCatching {
                            result = componentType.codecOrThrow()
                                .parse(registryAccess!!.createSerializationContext(NebulaOps), data)
                                .orThrow
                        }.onFailure {
                            Toast.showToast(Literal(it.message ?: "unknown error").withColor(Colors.RED))
                            DataComponentWrappers.log.error(it)
                            r = false
                        }
                        r
                    }
                ) {
                    SerializeElementEditor(data, modifier = Modifier.width(330f).height(190f)) {
                        data = it
                    }
                }.open()
            }

        }
    }
}


fun <C : Any> DefaultComponentBuilder(
    key: Identifier,
    componentType: DataComponentType<C>,
    onValueChange: (C, Boolean) -> Unit,
): IGScreenImpl {
    var data: SerializeElement = SerializeObject()
    var editorRecompose by lateInitValueOf {}
    val type = mutableStateOf(SerializeElementType.Object)
    type.subscribe {
        data = it.defaultValue
        editorRecompose()
    }
    var result: C by lateInitValueOf()
    return DataComponentEditor(
        key.asTranslateText(),
        { result to true },
        onValueChange,
        modifier = Modifier.maxWidth(340f),
        onConfirm = {
            var r = true
            runCatching {
                result = componentType.codecOrThrow()
                    .parse(registryAccess!!.createSerializationContext(NebulaOps), data)
                    .orThrow
            }.onFailure {
                Toast.showToast(Literal(it.message ?: "unknown error").withColor(Colors.RED))
                DataComponentWrappers.log.error(it)
                r = false
            }
            r
        }
    ) {
        EnumSelector(type, SerializeElementType.entries, modifier = Modifier.width(70f))
        Box {
//            val modifier = when (data) {
//                is Map<*, *>, is Iterable<*> -> Modifier.width(330f).height(190f)
//                is Boolean                   -> Modifier.width(40f)
//                else                         -> Modifier.width(160f)
//            }
            val modifier = when {
                data.isObject || data.isArray -> Modifier.width(330f).height(190f)
                data.isPrimitive && (data as SerializePrimitive).isBoolean -> Modifier.width(40f)
                else -> Modifier.width(160f)
            }
            SerializeElementEditor(data.toSerializeElement(), modifier = modifier) {
                data = it
            }
        }.apply {
            editorRecompose = { this.executeRecompose() }
        }
    }
}