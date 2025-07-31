package moe.forpleuvoir.hiirosakura.gui.widget.serializereditor

import moe.forpleuvoir.hiirosakura.util.closeScreen
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.gui.base.Transform
import moe.forpleuvoir.ibukigourd.gui.base.extensions.drawcontext.batchRenderTextureColored
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.attachLeft
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.padding
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.renderBackground
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.width
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.tip.Tip
import moe.forpleuvoir.ibukigourd.gui.base.tip.TipHandler
import moe.forpleuvoir.ibukigourd.gui.base.widget.IGWidgetImpl
import moe.forpleuvoir.ibukigourd.gui.base.widget.WidgetTextures
import moe.forpleuvoir.ibukigourd.gui.widget.ConfirmDialog
import moe.forpleuvoir.ibukigourd.gui.widget.DialogContent
import moe.forpleuvoir.ibukigourd.gui.widget.EnumSelector
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Box
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.layout.RowScope
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextEditor
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextLabel
import moe.forpleuvoir.ibukigourd.util.lateInitValueOf
import moe.forpleuvoir.ibukigourd.util.state.MutableState
import moe.forpleuvoir.ibukigourd.util.state.State
import moe.forpleuvoir.ibukigourd.util.state.asState
import moe.forpleuvoir.ibukigourd.util.state.mutableStateOf
import moe.forpleuvoir.nebula.serialization.base.*
import kotlin.time.Duration.Companion.seconds

fun ContainerScope.SerializeElementEditor(
    serializeElement: SerializeElement,
    modifier: Modifier = Modifier,
    onValueChange: (SerializeElement) -> Unit
): IGWidgetImpl = when (serializeElement) {
    is SerializeArray     -> SerializeArrayEditor(serializeElement, modifier, onValueChange = onValueChange)
    is SerializeObject    -> SerializeObjectEditor(serializeElement, modifier, onValueChange = onValueChange)
    is SerializeNull      -> TextLabel("null", modifier = modifier)
    is SerializePrimitive -> SerializePrimitiveEditor(serializeElement, modifier, onValueChange)
}


fun ContainerScope.SerializeElementEntryEditor(
    key: String,
    serializeElement: SerializeElement,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceBetween,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    keyWrapper: RowScope.(String) -> Unit = { TextLabel(it) },
    onValueChange: (SerializeElement) -> Unit,
    content: RowScope.() -> Unit
) {
    when (serializeElement) {
        is SerializeArray     -> SerializeArrayEntryEditor(
            key,
            serializeElement,
            modifier,
            horizontalArrangement,
            verticalAlignment,
            keyWrapper,
            onValueChange
        ) {
            content()
        }

        is SerializeObject    -> SerializeObjectEntryEditor(
            key,
            serializeElement,
            modifier,
            horizontalArrangement,
            verticalAlignment,
            keyWrapper,
            onValueChange
        ) {
            content()
        }

        is SerializeNull      -> ElementEntry(key, modifier, horizontalArrangement, verticalAlignment, keyWrapper) {
            Row(horizontalArrangement = Arrangement.spacedBy(5f)) {
                TextLabel("null")
                content()
            }
        }

        is SerializePrimitive -> SerializePrimitiveEntryEditor(
            key,
            serializeElement,
            modifier,
            { Modifier },
            horizontalArrangement,
            verticalAlignment,
            keyWrapper,
            onValueChange,
            content
        )
    }
}


fun ContainerScope.ElementEntry(
    key: String,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceBetween,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    keyWrapper: RowScope.(String) -> Unit = { TextLabel(it) },
    content: RowScope.() -> Unit
) = Row(modifier.attachLeft { padding(left = 5, right = 2) }, horizontalArrangement, verticalAlignment) {
    keyWrapper(key)
    content()
}


//------------ Adder ------------\\

enum class SerializeElementType(val defaultValue: SerializeElement) {
    Array(SerializeArray()),
    Object(SerializeObject()),
    String(SerializePrimitive("")),
    Boolean(SerializePrimitive(false)),
    Int(SerializePrimitive(0)),
    Long(SerializePrimitive(0L)),
    Float(SerializePrimitive(0f)),
    Double(SerializePrimitive(0.0)),
    Byte(SerializePrimitive(0.toByte())),
    Null(SerializeNull)
}


fun SerializeElementAdder(
    key: State<String>,
    keyVerify: (String) -> Boolean,
    consumer: (String, SerializeElement) -> Unit
) = ConfirmDialog(IGLang.add.asState) {
    val selected = mutableStateOf(SerializeElementType.String)
    var editor by lateInitValueOf<() -> Transform>()
    DialogContent {
        Column {
            Row(Modifier.width(160f), horizontalArrangement = Arrangement.SpaceBetween) {
                TextLabel(IGLang.mapKey)
                if (key is MutableState) {
                    TextEditor(Modifier.width(120f)) {
                        bindState(key)
                        editor = { owner().transform }
                    }
                } else {
                    Box(
                        Modifier.width(120f).padding(4f).renderBackground { ctx, x, y, d ->
                            ctx.batchRenderTextureColored {
                                pushWidgetTexture(transform, WidgetTextures.DROP_DOWN_MENU_BACKGROUND)
                            }
                        }
                    ) {
                        TextLabel(key.getValue(), modifier = Modifier.align(Alignment.CenterLeft))
                        editor = { owner().transform }
                    }
                }
            }
            Row(Modifier.width(160f), horizontalArrangement = Arrangement.SpaceBetween) {
                TextLabel("type")
                EnumSelector(selected, SerializeElementType.entries, modifier = Modifier.width(120f))
            }
        }
    }
    confirm {
        val key = key.getValue()
        if (keyVerify(key)) {
            consumer(key, selected.getValue().defaultValue)
            closeScreen()
        } else {
            TipHandler.pushTip("#serialize_element_adder", 2.seconds, editor, Tip {
                TextLabel(IGLang.keyExists(key))
            })
        }
    }
}