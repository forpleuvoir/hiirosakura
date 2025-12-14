package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mojang.serialization.JsonOps
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
import moe.forpleuvoir.ibukigourd.util.lateInitValueOf
import moe.forpleuvoir.ibukigourd.util.state.mutableStateOf
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.serialization.base.SerializePrimitive
import moe.forpleuvoir.nebula.serialization.gson.toJsonElement
import moe.forpleuvoir.nebula.serialization.gson.toSerializeElement
import net.minecraft.core.component.DataComponentType
import net.minecraft.resources.ResourceLocation

@Suppress("UNCHECKED_CAST")
fun <C : Any> ContainerScope.DefaultComponentWrapper(
    key: ResourceLocation,
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
                var data = componentType.codecOrThrow()
                    .encodeStart(registryAccess!!.createSerializationContext(JsonOps.COMPRESSED), component as C)
                    .resultOrPartial {
                        Toast.showToast(Literal(it).withColor(Colors.RED))
                    }
                    .get()

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
                                .parse(registryAccess!!.createSerializationContext(JsonOps.COMPRESSED), data)
                                .resultOrPartial {
                                    Toast.showToast(Literal(it).withColor(Colors.RED))
                                    DataComponentWrappers.log.error(it)
                                    r = false
                                }.get()
                        }.onFailure {
                            Toast.showToast(Literal(it.message ?: "unknown error").withColor(Colors.RED))
                            DataComponentWrappers.log.error(it)
                        }
                        r
                    }
                ) {
                    SerializeElementEditor(data.toSerializeElement(), modifier = Modifier.width(330f).height(190f)) {
                        data = it.toJsonElement()
                    }
                }.open()
            }.onFailure {
                Toast.showToast(Literal(it.message ?: "unknown error").withColor(Colors.RED))
                DataComponentWrappers.log.error(it)
            }
        }
    }
}


fun <C : Any> DefaultComponentBuilder(
    key: ResourceLocation,
    componentType: DataComponentType<C>,
    onValueChange: (C, Boolean) -> Unit,
): IGScreenImpl {
    var data: JsonElement = JsonObject()
    var editorRecompose by lateInitValueOf {}
    val type = mutableStateOf(SerializeElementType.Object)
    type.subscribe {
        data = it.defaultValue.toJsonElement()
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
                    .parse(registryAccess!!.createSerializationContext(JsonOps.COMPRESSED), data)
                    .resultOrPartial {
                        Toast.showToast(Literal(it).withColor(Colors.RED))
                        DataComponentWrappers.log.error(it)
                        r = false
                    }.get()
            }.onFailure { DataComponentWrappers.log.error(it) }
            r
        }
    ) {
        EnumSelector(type, SerializeElementType.entries, modifier = Modifier.width(70f))
        Box {
            val modifier = when {
                data.isJsonObject || data.isJsonArray -> Modifier.width(330f).height(190f)
                data is SerializePrimitive && (data as SerializePrimitive).isBoolean -> Modifier.width(40f)
                else -> Modifier.width(160f)
            }
            SerializeElementEditor(data.toSerializeElement(), modifier = modifier) {
                data = it.toJsonElement()
            }
        }.apply {
            editorRecompose = { this.executeRecompose() }
        }
    }
}