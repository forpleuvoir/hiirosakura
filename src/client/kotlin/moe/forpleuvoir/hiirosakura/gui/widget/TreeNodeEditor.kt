package moe.forpleuvoir.hiirosakura.gui.widget

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.gui.widget.DataType.Array
import moe.forpleuvoir.hiirosakura.gui.widget.DataType.Object
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.gui.base.Transform
import moe.forpleuvoir.ibukigourd.gui.base.extensions.drawcontext.renderBox
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.attachLeft
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.*
import moe.forpleuvoir.ibukigourd.gui.base.scope.WidgetContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.base.screen.execute
import moe.forpleuvoir.ibukigourd.gui.base.tip.Tip
import moe.forpleuvoir.ibukigourd.gui.base.tip.TipHandler
import moe.forpleuvoir.ibukigourd.gui.base.toast.Toast
import moe.forpleuvoir.ibukigourd.gui.base.widget.IGWidgetImpl
import moe.forpleuvoir.ibukigourd.gui.base.widget.WidgetTextures
import moe.forpleuvoir.ibukigourd.gui.modifier.bgHoverHighlightBox
import moe.forpleuvoir.ibukigourd.gui.modifier.disableRender
import moe.forpleuvoir.ibukigourd.gui.modifier.disableRenderBackground
import moe.forpleuvoir.ibukigourd.gui.widget.*
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.button.SwitchButton
import moe.forpleuvoir.ibukigourd.gui.widget.icon.Icon
import moe.forpleuvoir.ibukigourd.gui.widget.icon.IconTextures
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.layout.ColumnScope
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.layout.list.RowListWrapped
import moe.forpleuvoir.ibukigourd.gui.widget.text.*
import moe.forpleuvoir.ibukigourd.input.Mouse
import moe.forpleuvoir.ibukigourd.mod.config.GuiConfig.configContainerWrapperGuidelinesColor
import moe.forpleuvoir.ibukigourd.text.Translatable
import moe.forpleuvoir.ibukigourd.text.translateText
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.ibukigourd.util.state.MutableState
import moe.forpleuvoir.ibukigourd.util.state.mutableStateOf
import moe.forpleuvoir.ibukigourd.util.state.stateOf
import moe.forpleuvoir.ibukigourd.util.state.switch
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.common.util.primitive.pick

/**
 * - Object
 * - Array
 *
 * **Primitive**
 * - Boolean
 * - String
 * - Number
 * - null
 *
 */
fun WidgetContainerScope.TreeNodeEditor(
    data: MutableMap<String, Any?>,
    modifier: Modifier = Modifier,
    listModifier: ColumnScope.() -> Modifier = { Modifier },
) = Row(
    verticalArrangement = Arrangement.spacedBy(4f),
) {
    var recompose: (() -> Unit)? = null
    Column(
        modifier = Modifier.fill(),
        horizontalArrangement = Arrangement.spacedBy(4f, Alignment.Left)
    ) {
        Button {
            TextLabel(IGLang.add)
            click {
                EntryAdder(
                    key = "key${data.size}",
                    isObject = true,
                    keyPredicate = { !data.containsKey(it) },
                ) { key, value, type ->
                    data[key] = value
                    recompose?.invoke()
                }.open()
            }
        }
    }
    RowListWrapped(
        modifier,
        listModifier = listModifier
    ) {
        if (data.isEmpty()) TextLabel(IGLang.hasNothing)
        data.forEach { (key, _) ->
            Entry(
                key,
                { data[key] },
                {
                    if (data[key] != it) {
                        data[key] = it
                    }
                }, {
                    data.remove(key)
                    recompose?.invoke()
                }
            )
        }
    }.apply {
        recompose = {
            execute {
                this.recompose()
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
private fun WidgetContainerScope.Entry(key: String, valueSupplier: () -> Any?, valueSetter: (Any) -> Unit, valueRemover: () -> Unit) {
    when (val value = valueSupplier()) {
        is MutableMap<*, *>              -> {
            ObjectEntry(key, value as MutableMap<String, Any>, valueRemover)
        }

        is MutableList<*>                -> {
            ArrayEntry(key, value as MutableList<Any>, valueRemover)
        }

        is Number, is String, is Boolean -> {
            PrimitiveEntry(key, valueSupplier, valueSetter, valueRemover)
        }
    }
}

private fun WidgetContainerScope.EntryColumn(
    key: String,
    modifier: Modifier = Modifier,
    content: ColumnScope.() -> Unit
) = Column(
    modifier = modifier.attachLeft {
        bgHoverHighlightBox()
            .padding(2f)
            .name("entry:$key")
    },
    horizontalArrangement = Arrangement.spacedBy(4f)
) {
    Column(
        modifier = modifier.attachLeft { weight(1) },
        horizontalArrangement = Arrangement.Left
    ) {
        TextLabel(
            key,
            modifier = modifier
                .hoverText(HSLang.clickToCopy(Translatable(Mouse.RIGHT.translationKey)))
                .mousePress {
                    it.tryUse(it.button == Mouse.RIGHT && wasMouseOver)
                        .onSuccess {
                            mc.keyboard.clipboard = key
                            Toast.showToast(text = HSLang.copySuccess)
                        }
                    this.onMousePress(it)
                }
        )
    }
    content()
}

private fun WidgetContainerScope.ObjectEntry(
    key: String,
    obj: MutableMap<String, Any>,
    valueRemover: () -> Unit
) = Row {
    val expanded = mutableStateOf(false)
    var recompose: (() -> Unit)? = null
    Button(
        modifier = Modifier.disableRender().padding(0)
    ) {
        click { expanded.switch() }
        EntryColumn(key) {
            AddButton {
                EntryAdder(
                    key = "key${obj.size}",
                    isObject = true,
                    keyPredicate = { !obj.containsKey(it) },
                ) { key, value, type ->
                    obj[key] = value
                    recompose?.invoke()
                }.open()
            }
            RemoveButton { valueRemover() }
            Icon(
                mutableStateOf(expanded) { it.pick(WidgetTextures.DROP_DOWN_MENU_ARROW_UP, WidgetTextures.DROP_DOWN_MENU_ARROW_DOWN) },
                modifier = Modifier.margin(right = 10f)
            )
        }
    }
    SwitchableProxy(
        widgetA = {
            Column {
                Widget(
                    Modifier.width(1.5f)
                        .matchSibling()
                        .padding(0, 0, 2, 2)
                        .margin(4, 0.5, 0, 0)
                        .render { context, _, _, _ ->
                            context.renderBox(
                                transform.asWorldCoordinateBox.trimEdges(padding.top, padding.bottom, padding.left, padding.right),
                                configContainerWrapperGuidelinesColor
                            )
                        }
                )
                Row(
                    modifier = Modifier.padding(2, 8, 2, 2),
                    verticalArrangement = Arrangement.spacedBy(4f)
                ) {
                    if (obj.isEmpty()) TextLabel(IGLang.hasNothing, Modifier.fill())
                    obj.forEach { (key, _) ->
                        Entry(
                            key,
                            { obj[key]!! },
                            {
                                if (obj[key] != it) {
                                    obj[key] = it
                                }
                            },
                            {
                                obj.remove(key)
                                recompose?.invoke()
                            }
                        )
                    }
                }.apply {
                    recompose = {
                        execute {
                            this@apply.recompose()
                        }
                    }
                }
            }

        },
        widgetB = {
            Widget(Modifier)
        },
        expanded
    )
}

private fun WidgetContainerScope.ArrayEntry(
    key: String,
    array: MutableList<Any>,
    valueRemover: () -> Unit
) = Row {
    val expanded = mutableStateOf(false)
    var recompose: (() -> Unit)? = null
    Button(
        modifier = Modifier.disableRender().padding(0)
    ) {
        click { expanded.switch() }
        EntryColumn(key) {
            AddButton {
                EntryAdder(
                    key = array.size.toString(),
                    isObject = false,
                    keyPredicate = { it.toInt() !in array.indices },
                ) { key, value, type ->
                    array.add(value)
                    recompose?.invoke()
                }.open()
            }
            RemoveButton { valueRemover() }
            Icon(
                mutableStateOf(expanded) { it.pick(WidgetTextures.DROP_DOWN_MENU_ARROW_UP, WidgetTextures.DROP_DOWN_MENU_ARROW_DOWN) },
                modifier = Modifier.margin(right = 10f)
            )
        }
    }
    SwitchableProxy(
        widgetA = {
            Column {
                Widget(
                    Modifier.width(1.5f)
                        .matchSibling()
                        .padding(0, 0, 2, 2)
                        .margin(4, 0.5, 0, 0)
                        .render { context, _, _, _ ->
                            context.renderBox(
                                transform.asWorldCoordinateBox.trimEdges(padding.top, padding.bottom, padding.left, padding.right),
                                configContainerWrapperGuidelinesColor
                            )
                        }
                )
                Row(
                    modifier = Modifier.padding(2, 8, 2, 2),
                    verticalArrangement = Arrangement.spacedBy(4f)
                ) {
                    if (array.isEmpty()) TextLabel(IGLang.hasNothing, Modifier.fill())
                    array.forEachIndexed { index, _ ->
                        Entry(
                            index.toString(),
                            { array[index] },
                            {
                                if (array[index] != it) {
                                    array[index] = it
                                }
                            }, {
                                array.removeAt(index)
                                recompose?.invoke()
                            }
                        )
                    }
                }.apply {
                    recompose = {
                        execute {
                            this.recompose()
                        }
                    }
                }
            }

        },
        widgetB = {
            Widget(Modifier)
        },
        expanded
    )
}

private fun WidgetContainerScope.PrimitiveEntry(
    key: String,
    valueSupplier: () -> Any?,
    valueSetter: (Any) -> Unit,
    valueRemover: () -> Unit
) = EntryColumn(key) {
    when (val value = valueSupplier()) {
        is Boolean -> BooleanEntry(value, valueSetter)
        is String  -> StringEntry(value, valueSetter)
        is Number  -> NumberEntry(value, valueSetter)
        null       -> NullEntry()
        else       -> UnsupportedEntry()
    }
    RemoveButton { valueRemover() }
}

private fun WidgetContainerScope.UnsupportedEntry() = TextLabel(IGLang.unsupported)

private fun WidgetContainerScope.NullEntry() = TextLabel("null")

private fun WidgetContainerScope.BooleanEntry(
    value: Boolean,
    valueSetter: (Boolean) -> Unit
) = SwitchButton(
    mutableStateOf(value).apply {
        this.onSetValue = {
            valueSetter(it)
            it
        }
    }
)

private fun WidgetContainerScope.StringEntry(
    value: String,
    valueSetter: (String) -> Unit
) = Column(
    horizontalArrangement = Arrangement.spacedBy(5f)
) {
    val str = mutableStateOf(value).apply {
        onSetValue = {
            valueSetter(it)
            it
        }
    }
    TextEditor(
        modifier = Modifier.width(120f)
    ) {
        text = str.getValue()
        textConsumer {
            str.setValue(it)
        }
    }.apply {
        str.subscribe {
            this.text = it
        }
    }
    Button {
        Icon(IconTextures.EDIT)
        click {
            SimpleDialog(stateOf(IGLang.edit)) {
                TextAreaWrapped(
                    modifier = Modifier
                        .maxSize(320f, 240f).minSize(180f, 150f)
                        .disableRenderBackground()
                        .padding(0f)
                ) {
                    text = str.getValue()
                    textConsumer {
                        str.setValue(it)
                    }
                }
            }.open()
        }
    }
}

private const val EDITOR_WIDTH = 120f

@Suppress("UNCHECKED_CAST")
private fun WidgetContainerScope.NumberEntry(
    value: Number,
    valueSetter: (Number) -> Unit
): IGWidgetImpl {
    val modifier = Modifier.width(EDITOR_WIDTH)
    val editorModifier: ColumnScope.() -> Modifier = { Modifier.weight(1) }
    val state = mutableStateOf(value)
    state.subscribe {
        valueSetter(it)
    }
    return when (value) {
        is Int    -> IntEditor(state as MutableState<Int>, modifier = modifier, editorModifier = editorModifier)
        is Long   -> LongEditor(state as MutableState<Long>, modifier = modifier, editorModifier = editorModifier)
        is Float  -> FloatEditor(state as MutableState<Float>, modifier = modifier, editorModifier = editorModifier)
        is Double -> DoubleEditor(state as MutableState<Double>, modifier = modifier, editorModifier = editorModifier)
        else      -> UnsupportedEntry()
    }
}

private fun EntryAdder(
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    key: String,
    isObject: Boolean,
    keyPredicate: (String) -> Boolean,
    newData: (String, Any, DataType) -> Unit
): IGScreenImpl {
    val type = mutableStateOf(DataType.String)
    var transform: (() -> Transform)? = null
    var _key = key

    return ConfirmDialog(
        stateOf(IGLang.edit),
        modifier,
        screenModifier,
        onConfirm = {
            if (keyPredicate(_key)) {
                newData(
                    _key,
                    when (type.getValue()) {
                        DataType.String  -> ""
                        DataType.Boolean -> true
                        DataType.Int     -> 0
                        DataType.Long    -> 0L
                        DataType.Float   -> 0f
                        DataType.Double  -> 0.0
                        Array            -> mutableListOf<Any>()
                        Object           -> LinkedHashMap<String, Any>()
                    },
                    type.getValue()
                )
                mc.currentScreen?.close()
            } else {
                TipHandler.pushTip("#ENTRY_ADDER", transform!!, Tip {
                    TextLabel(IGLang.keyExists(key).withColor(Colors.RED))
                })
            }
        },
    ) {
        DialogContent {
            Row(
                verticalArrangement = Arrangement.spacedBy(4f)
            ) {
                Column(
                    modifier.width(240f),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextLabel(HSLang.customDataKey)
                    TextEditor(Modifier.width(120f).active(isObject)) {
                        text = _key
                        textConsumer { _key = it }
                        transform = { this.owner().transform }
                    }
                }
                Column(
                    modifier.width(240f),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextLabel(HSLang.customDataType)
                    EnumSelector(type, modifier = Modifier.width(120f))
                }
            }
        }
    }
}

enum class DataType {
    String,
    Boolean,
    Int,
    Long,
    Float,
    Double,
    Array,
    Object,
}