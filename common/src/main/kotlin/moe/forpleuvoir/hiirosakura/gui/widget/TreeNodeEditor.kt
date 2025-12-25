package moe.forpleuvoir.hiirosakura.gui.widget

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.gui.widget.DataType.Array
import moe.forpleuvoir.hiirosakura.gui.widget.DataType.Object
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.gui.base.Transform
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.attachLeft
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.*
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.scope.GuiScope.Companion.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.base.screen.closeScreen
import moe.forpleuvoir.ibukigourd.gui.base.tip.Tip
import moe.forpleuvoir.ibukigourd.gui.base.tip.TipHandler
import moe.forpleuvoir.ibukigourd.gui.base.toast.Toast
import moe.forpleuvoir.ibukigourd.gui.base.widget.GuiWidgetImpl
import moe.forpleuvoir.ibukigourd.gui.base.widget.WidgetTextures
import moe.forpleuvoir.ibukigourd.gui.base.widget.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.modifier.bgHoverHighlightBox
import moe.forpleuvoir.ibukigourd.gui.modifier.disableRender
import moe.forpleuvoir.ibukigourd.gui.modifier.disableRenderBackground
import moe.forpleuvoir.ibukigourd.gui.widget.*
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.button.SwitchButton
import moe.forpleuvoir.ibukigourd.gui.widget.icon.Icon
import moe.forpleuvoir.ibukigourd.gui.widget.icon.IconTextures
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.layout.RowScope
import moe.forpleuvoir.ibukigourd.gui.widget.layout.list.ColumnListWrapped
import moe.forpleuvoir.ibukigourd.gui.widget.text.*
import moe.forpleuvoir.ibukigourd.input.Mouse
import moe.forpleuvoir.ibukigourd.mod.config.GuiConfig.configContainerWrapperGuidelinesColor
import moe.forpleuvoir.ibukigourd.text.Translatable
import moe.forpleuvoir.ibukigourd.text.withColor
import moe.forpleuvoir.ibukigourd.util.lateInitValueOf
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
fun ContainerScope.TreeNodeEditor(
    data: MutableMap<String, Any?>,
    modifier: Modifier = Modifier,
    listModifier: RowScope.() -> Modifier = { Modifier },
) = Column(
    verticalArrangement = Arrangement.spacedBy(4f),
) {
    var recompose by lateInitValueOf<() -> Unit>()
    Row(
        modifier = Modifier.fill(),
        horizontalArrangement = Arrangement.spacedBy(4f, Alignment.Left)
    ) {
        Button {
            Text(IGLang.add)
            click {
                EntryAdder(
                    key = "key${data.size}",
                    isObject = true,
                    keyPredicate = { !data.containsKey(it) },
                ) { key, value, type ->
                    data[key] = value
                    recompose()
                }.open()
            }
        }
    }
    ColumnListWrapped(
        modifier,
        listModifier = listModifier
    ) {
        if (data.isEmpty()) Text(IGLang.hasNothing)
        data.forEach { (key, _) ->
            Entry(
                key,
                { data[key] },
                {
                    if (data[key] != it) {
                        data[key] = it
                    }
                },
                {
                    data.remove(key)
                    recompose()
                },
                Modifier.unlockConstraint()
            )
        }
        recompose = { executeRecompose() }
    }
}

fun ContainerScope.ListNodeEditor(
    list: MutableList<Any?>,
    modifier: Modifier = Modifier,
    listModifier: RowScope.() -> Modifier = { Modifier },
) = Column(
    verticalArrangement = Arrangement.spacedBy(4f),
) {
    var recompose by lateInitValueOf<() -> Unit>()
    Row(
        modifier = Modifier.fill(),
        horizontalArrangement = Arrangement.spacedBy(4f, Alignment.Left)
    ) {
        Button {
            Text(IGLang.add)
            click {
                EntryAdder(
                    key = list.size.toString(),
                    isObject = false,
                    keyPredicate = { it.toInt() !in list.indices },
                ) { key, value, type ->
                    list.add(value)
                    recompose()
                }.open()
            }
        }
    }
    ColumnListWrapped(
        modifier,
        listModifier = listModifier
    ) {
        if (list.isEmpty()) Text(IGLang.hasNothing)
        list.forEachIndexed { index, value ->
            Entry(
                index.toString(),
                { list[index] },
                {
                    if (list[index] != it) {
                        list[index] = it
                    }
                }, {
                    list.removeAt(index)
                    recompose()
                },
                Modifier.unlockConstraint()
            )
        }
        recompose = { executeRecompose() }
    }
}

@Suppress("UNCHECKED_CAST")
private fun ContainerScope.Entry(key: String, valueSupplier: () -> Any?, valueSetter: (Any) -> Unit, valueRemover: () -> Unit, modifier: Modifier = Modifier) {
    when (val value = valueSupplier()) {
        is MutableMap<*, *>              -> {
            ObjectEntry(key, value as MutableMap<String, Any>, modifier, valueRemover)
        }

        is MutableList<*>                -> {
            ArrayEntry(key, value as MutableList<Any>, modifier, valueRemover)
        }

        is Number, is String, is Boolean -> {
            PrimitiveEntry(key, valueSupplier, valueSetter, valueRemover, modifier)
        }
    }
}

private fun ContainerScope.EntryRow(
    key: String,
    modifier: Modifier = Modifier,
    content: RowScope.() -> Unit
) = Row(
    modifier = modifier.attachLeft {
        bgHoverHighlightBox()
            .padding(2f)
            .name("entry:$key")
    },
    horizontalArrangement = Arrangement.spacedBy(4f)
) {
    Row(
        modifier = modifier.attachLeft { weight(1) },
        horizontalArrangement = Arrangement.Left
    ) {
        Text(
            key,
            modifier = modifier
                .hoverText(HSLang.pressToCopy(Translatable(Mouse.RIGHT.translationKey)))
                .mousePress {
                    it.tryUse(it.button == Mouse.RIGHT && wasMouseOver)
                        .onSuccess {
                            mc.keyboardHandler.clipboard = key
                            Toast.showToast(text = HSLang.copySuccess)
                        }
                    this.onMousePress(it)
                }
        )
    }
    content()
}

private fun ContainerScope.ObjectEntry(
    key: String,
    obj: MutableMap<String, Any>,
    modifier: Modifier,
    valueRemover: () -> Unit
) = Column(modifier) {
    val expanded = mutableStateOf(false)
    var recompose: (() -> Unit)? = null
    Button(
        modifier = Modifier.disableRender().padding(0)
    ) {
        click { expanded.switch() }
        EntryRow(key) {
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
            Row {
                Widget(
                    Modifier.width(1.5f)
                        .matchSibling()
                        .padding(0, 0, 2, 2)
                        .margin(4, 0.5, 0, 0)
                        .render { guiGraphics, _, _, _ ->
                            guiGraphics.pushBox(
                                transform.asWorldCoordinateBox.trimEdges(padding.top, padding.bottom, padding.left, padding.right),
                                configContainerWrapperGuidelinesColor
                            )
                        }
                )
                Column(
                    modifier = Modifier.padding(2, 8, 2, 2),
                    verticalArrangement = Arrangement.spacedBy(4f)
                ) {
                    if (obj.isEmpty()) Text(IGLang.hasNothing, Modifier.fill())
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
                        this.executeRecompose()
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

private fun ContainerScope.ArrayEntry(
    key: String,
    array: MutableList<Any>,
    modifier: Modifier,
    valueRemover: () -> Unit
) = Column(modifier) {
    val expanded = mutableStateOf(false)
    var recompose: (() -> Unit)? = null
    Button(
        modifier = Modifier.disableRender().padding(0)
    ) {
        click { expanded.switch() }
        EntryRow(key) {
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
            Row {
                Widget(
                    Modifier.width(1.5f)
                        .matchSibling()
                        .padding(0, 0, 2, 2)
                        .margin(4, 0.5, 0, 0)
                        .render { guiGraphics, _, _, _ ->
                            guiGraphics.pushBox(
                                transform.asWorldCoordinateBox.trimEdges(padding.top, padding.bottom, padding.left, padding.right),
                                configContainerWrapperGuidelinesColor
                            )
                        }
                )
                Column(
                    modifier = Modifier.padding(2, 8, 2, 2),
                    verticalArrangement = Arrangement.spacedBy(4f)
                ) {
                    if (array.isEmpty()) Text(IGLang.hasNothing, Modifier.fill())
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
                        executeRecompose()
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

private fun ContainerScope.PrimitiveEntry(
    key: String,
    valueSupplier: () -> Any?,
    valueSetter: (Any) -> Unit,
    valueRemover: () -> Unit,
    modifier: Modifier,
) = EntryRow(key, modifier = modifier) {
    when (val value = valueSupplier()) {
        is Boolean -> BooleanEntry(value, valueSetter)
        is String  -> StringEntry(value, valueSetter)
        is Number  -> NumberEntry(value, valueSetter)
        null       -> NullEntry()
        else       -> UnsupportedEntry()
    }
    RemoveButton { valueRemover() }
}

private fun ContainerScope.UnsupportedEntry() = Text(IGLang.unsupported)

private fun ContainerScope.NullEntry() = Text("null")

private fun ContainerScope.BooleanEntry(
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

private fun ContainerScope.StringEntry(
    value: String,
    valueSetter: (String) -> Unit
) = Row(
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
private fun ContainerScope.NumberEntry(
    value: Number,
    valueSetter: (Number) -> Unit
): GuiWidgetImpl {
    val modifier = Modifier.width(EDITOR_WIDTH)
    val editorModifier: RowScope.() -> Modifier = { Modifier.weight(1) }
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

private var TIP: Tip? = null

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
        screenModifier.attachLeft {
            onClose { TipHandler.popTip(TIP) }
        },
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
                closeScreen()
            } else {
                TipHandler.popTip(TIP)
                TIP = TipHandler.pushTip(transform!!, Tip {
                    Text(IGLang.keyExists(key).withColor(Colors.RED))
                })
            }
        },
    ) {
        DialogContent {
            Column(
                verticalArrangement = Arrangement.spacedBy(4f)
            ) {
                Row(
                    modifier.width(240f),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(HSLang.customDataKey)
                    TextEditor(Modifier.width(120f).active(isObject)) {
                        text = _key
                        textConsumer { _key = it }
                        transform = { this.owner().transform }
                    }
                }
                Row(
                    modifier.width(240f),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(HSLang.customDataType)
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