@file:Suppress("FunctionName")

package moe.forpleuvoir.hiirosakura.gui.widget

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.HSLang.blockInfoMatcher
import moe.forpleuvoir.hiirosakura.HSLang.blockInfoMatcherEntryBlock
import moe.forpleuvoir.hiirosakura.HSLang.blockInfoMatcherEntryMatcher
import moe.forpleuvoir.hiirosakura.HSLang.blockInfoMatcherEntryPos
import moe.forpleuvoir.hiirosakura.HSLang.blockInfoMatcherEntryProperty
import moe.forpleuvoir.hiirosakura.HSLang.blockInfoMatcherEntryScript
import moe.forpleuvoir.hiirosakura.HSLang.blockInfoMatcherEntryTag
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.*
import moe.forpleuvoir.hiirosakura.util.targetBlock
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.*
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.scope.TableLayoutColumnScope
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.base.widget.GuiWidget
import moe.forpleuvoir.ibukigourd.gui.base.widget.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.modifier.bgHoverHighlightBox
import moe.forpleuvoir.ibukigourd.gui.widget.*
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.button.FlatButton
import moe.forpleuvoir.ibukigourd.gui.widget.button.RadioButtons
import moe.forpleuvoir.ibukigourd.gui.widget.icon.Icon
import moe.forpleuvoir.ibukigourd.gui.widget.icon.IconTextures
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.layout.TableScope
import moe.forpleuvoir.ibukigourd.gui.widget.layout.list.ColumnListWrapped
import moe.forpleuvoir.ibukigourd.gui.widget.text.*
import moe.forpleuvoir.ibukigourd.text.*
import moe.forpleuvoir.ibukigourd.util.forEachWithLimit
import moe.forpleuvoir.ibukigourd.util.lateInitValueOf
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.ibukigourd.util.state.MutableState
import moe.forpleuvoir.ibukigourd.util.state.mutableStateBy
import moe.forpleuvoir.ibukigourd.util.state.mutableStateOf
import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.common.color.HSVColor
import net.minecraft.util.Util
import net.minecraft.world.level.block.Blocks
import org.joml.Vector3i
import kotlin.jvm.optionals.getOrNull

private val map: Map<Text, (Modifier, Modifier, (BlockInfoMatchEntry) -> Unit) -> IGScreenImpl> = mapOf(
    blockInfoMatcherEntryMatcher to { modifier: Modifier, screenModifier: Modifier, entryConsumer: (BlockInfoMatchEntry.Matcher) -> Unit ->
        MatcherMatchEntryBuilder(modifier, screenModifier, entryConsumer = entryConsumer)
    },
    blockInfoMatcherEntryBlock to { modifier: Modifier, screenModifier: Modifier, entryConsumer: (BlockInfoMatchEntry.Block) -> Unit ->
        BlockMatchEntryBuilder(modifier, screenModifier, entryConsumer = entryConsumer)
    },
    blockInfoMatcherEntryScript to { modifier: Modifier, screenModifier: Modifier, entryConsumer: (BlockInfoMatchEntry.Script) -> Unit ->
        ScriptMatchEntryBuilder(modifier, screenModifier, entryConsumer = entryConsumer)
    },
    blockInfoMatcherEntryPos to { modifier: Modifier, screenModifier: Modifier, entryConsumer: (BlockInfoMatchEntry.Pos) -> Unit ->
        PosMatchEntryBuilder(modifier, screenModifier, entryConsumer = entryConsumer)
    },
    blockInfoMatcherEntryTag to { modifier: Modifier, screenModifier: Modifier, entryConsumer: (BlockInfoMatchEntry.Tag) -> Unit ->
        TagMatchEntryBuilder(modifier, screenModifier, entryConsumer = entryConsumer)
    },
    blockInfoMatcherEntryProperty to { modifier: Modifier, screenModifier: Modifier, entryConsumer: (BlockInfoMatchEntry.Property) -> Unit ->
        PropertyMatchEntryBuilder(modifier, screenModifier, entryConsumer = entryConsumer)
    }
)

fun BlockInfoMatcherBuilder(
    matcher: BlockInfoMatcher,
    matcherConsumer: (BlockInfoMatcher) -> Unit,
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    title: Text = blockInfoMatcher
): IGScreenImpl {
    val matcher = matcher.clone()
    return Dialog(
        screenModifier = Modifier
            .onClose {
                matcherConsumer(matcher)
            }.then(screenModifier),
        modifier = Modifier.width(420f).then(modifier),
    ) {
        var onChanged = {}
        val selectedMode: MutableState<CompositeMatcher.MatchMode?> = mutableStateOf(matcher.mode)
        Text(title)
        Column(
            verticalArrangement = Arrangement.spacedBy(2f),
        ) {
            Row(
                Modifier.fill(),
                horizontalArrangement = Arrangement.spacedBy(4f, Alignment.Left)
            ) {
                RadioButtons(
                    CompositeMatcher.MatchMode.entries,
                    selectedMode,
                    { matcher.mode = it },
                    {
                        Text(it.translateText)
                    },
                    {
                        Modifier.hoverText(it.translateComment).hoverable { widget -> widget.visible }
                    }
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(4f)
            ) {
                ColumnListWrapped(
                    modifier = Modifier.height(180f).weight(1),
                    listModifier = { Modifier.weight(1).fill() },
                    onCreate = { onChanged = { this.executeRecompose() } }
                ) {
                    if (matcher.entries.isEmpty()) {
                        Text(IGLang.hasNothing)
                    }
                    matcher.entries.forEachIndexed { index, entry ->
                        EntryWrapper(
                            entry,
                            {
                                matcher.setEntry(index, it)
                                onChanged()
                            },
                            {
                                matcher.removeEntry(index)
                                onChanged()
                            },
                            Modifier.fill()
                        )
                    }
                }
                Column(
                    modifier = Modifier.height(180f),
                    verticalArrangement = Arrangement.spacedBy(2f),
                ) {
                    map.forEach { (key, builder) ->
                        Button(Modifier, Arrangement.spacedBy(6f, Alignment.CenterHorizontally)) {
                            Text(key, modifier = Modifier.width(map.keys.maxWidth))
                            Icon(IconTextures.PLUS, HSVColor(120f, 1f, .65f), modifier = Modifier.size(8f, 8f))
                            click {
                                builder.invoke(Modifier, Modifier) {
                                    matcher.addEntry(it)
                                    onChanged()
                                }.open()
                            }
                        }
                    }
                }

            }

        }
    }
}

private fun ContainerScope.EntryWrapper(
    entry: BlockInfoMatchEntry,
    entryConsumer: (BlockInfoMatchEntry) -> Unit,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier
) = Row(
    Modifier
        .padding(horizontal = 2f)
        .bgHoverHighlightBox()
        .then(modifier),
    horizontalArrangement = Arrangement.spacedBy(4f)
) {
    Text(entry.translateText, modifier = Modifier.weight(1))
    when (entry) {
        is BlockInfoMatchEntry.Matcher  -> BlockInfoMatcherSimpleInfo(entry.matcher, Modifier.hoverTip { BlockInfoMatcherInfo(entry.matcher) })
        is BlockInfoMatchEntry.Block    -> Row(
            horizontalArrangement = Arrangement.spacedBy(2f)
        ) {
            ItemIcon(entry.block, .6f)
            Text(entry.asText)
        }

        is BlockInfoMatchEntry.Script   -> Text(entry.asText, Modifier.hoverText(entry.script))
        is BlockInfoMatchEntry.Pos      -> Text(entry.asText)
        is BlockInfoMatchEntry.Tag      -> Text(entry.asText)
        is BlockInfoMatchEntry.Property -> Text(entry.asText)
    }
    FlatButton(
        idleColor = Color.ofARGB(0), round = 0
    ) {
        ModeRect(entry)
        Text(
            entry.mode.translateText,
            setting = TextSetting().copy(backgroundColor = if (entry.mode.toBoolean()) Colors.LIME.alpha(.25f) else Colors.RED.alpha(0.25f))
        )
        click {
            val mode = MatchEntry.MatchMode.fromBoolean(!entry.mode.toBoolean())
            val newValue = when (entry) {
                is BlockInfoMatchEntry.Matcher  -> BlockInfoMatchEntry.Matcher(entry.matcher, mode)
                is BlockInfoMatchEntry.Block    -> BlockInfoMatchEntry.Block(entry.block, mode)
                is BlockInfoMatchEntry.Script   -> BlockInfoMatchEntry.Script(entry.script, mode)
                is BlockInfoMatchEntry.Pos      -> BlockInfoMatchEntry.Pos(entry.min, entry.max, mode)
                is BlockInfoMatchEntry.Tag      -> BlockInfoMatchEntry.Tag(entry.tag, mode)
                is BlockInfoMatchEntry.Property -> BlockInfoMatchEntry.Property(entry.property, mode)
            }
            entryConsumer(newValue)
        }
    }
    EditButton {
        //应该是对应Entry的编辑屏幕
        when (entry) {
            is BlockInfoMatchEntry.Matcher  -> MatcherMatchEntryBuilder(entry = entry, entryConsumer = entryConsumer)
            is BlockInfoMatchEntry.Block    -> BlockMatchEntryBuilder(entry = entry, entryConsumer = entryConsumer)
            is BlockInfoMatchEntry.Script   -> ScriptMatchEntryBuilder(entry = entry, entryConsumer = entryConsumer)
            is BlockInfoMatchEntry.Pos      -> PosMatchEntryBuilder(entry = entry, entryConsumer = entryConsumer)
            is BlockInfoMatchEntry.Tag      -> TagMatchEntryBuilder(entry = entry, entryConsumer = entryConsumer)
            is BlockInfoMatchEntry.Property -> PropertyMatchEntryBuilder(entry = entry, entryConsumer = entryConsumer)
        }.open()
    }
    RemoveButton { removeAction() }
}

private fun MatcherMatchEntryBuilder(
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    entry: BlockInfoMatchEntry.Matcher = BlockInfoMatchEntry.Matcher(BlockInfoMatcher.targetBlockMatcher, MatchEntry.MatchMode.Include),
    entryConsumer: (BlockInfoMatchEntry.Matcher) -> Unit
): IGScreenImpl = BlockInfoMatcherBuilder(
    entry.matcher,
    {
        entryConsumer(BlockInfoMatchEntry.Matcher(it, entry.mode))
    },
    modifier,
    screenModifier,
    blockInfoMatcherEntryMatcher
)


private fun BlockMatchEntryBuilder(
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    entry: BlockInfoMatchEntry.Block = BlockInfoMatchEntry.Block(mc.targetBlock?.state?.block ?: Blocks.MELON, MatchEntry.MatchMode.Include),
    entryConsumer: (BlockInfoMatchEntry.Block) -> Unit
): IGScreenImpl {
    val block = mutableStateOf(entry.block)
    return MatchEntryDialog(
        title = blockInfoMatcherEntryBlock,
        modifier = modifier,
        screenModifier = screenModifier,
        entrySupplier = { mode -> BlockInfoMatchEntry.Block(block.getValue(), mode) },
        entryConsumer = entryConsumer
    ) {
        BlockSelector(
            block,
            modifier = Modifier.width(120f),
        )
    }
}

private fun ScriptMatchEntryBuilder(
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    entry: BlockInfoMatchEntry.Script = BlockInfoMatchEntry.Script(mode = MatchEntry.MatchMode.Include),
    entryConsumer: (BlockInfoMatchEntry.Script) -> Unit
): IGScreenImpl {
    var script = entry.script
    return MatchEntryDialog(
        title = blockInfoMatcherEntryScript,
        modifier = modifier,
        screenModifier = screenModifier,
        entrySupplier = { mode -> BlockInfoMatchEntry.Script(script, mode) },
        entryConsumer = entryConsumer
    ) {
        TextAreaWrapped(
            modifier = Modifier.size(440f, 200f)
        ) {
            text = script
            textConsumer { script = it }
        }
    }
}

private fun PosMatchEntryBuilder(
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    entry: BlockInfoMatchEntry.Pos = BlockInfoMatchEntry.Pos(
        mc.targetBlock?.pos ?: Vector3i(0, 0, 0),
        mc.targetBlock?.pos ?: Vector3i(0, 0, 0),
        MatchEntry.MatchMode.Include
    ),
    entryConsumer: (BlockInfoMatchEntry.Pos) -> Unit
): IGScreenImpl {
    val start = mutableStateOf(entry.min)
    val end = mutableStateOf(entry.max)
    return MatchEntryDialog(
        title = blockInfoMatcherEntryPos,
        modifier = modifier,
        screenModifier = screenModifier,
        entrySupplier = { mode -> BlockInfoMatchEntry.Pos(start.getValue(), end.getValue(), mode) },
        entryConsumer = entryConsumer
    ) {
        Row(modifier = Modifier.width(240f), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("MIN: ")
            Vector3iEditor(start)
        }
        Row(modifier = Modifier.width(240f), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("MAX: ")
            Vector3iEditor(end)
        }
    }
}

private fun TagMatchEntryBuilder(
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    entry: BlockInfoMatchEntry.Tag = BlockInfoMatchEntry.Tag(
        mc.targetBlock?.state?.tags?.findFirst()?.getOrNull()?.location?.toString() ?: "",
        MatchEntry.MatchMode.Include
    ),
    entryConsumer: (BlockInfoMatchEntry.Tag) -> Unit
): IGScreenImpl {
    var tag = entry.tag
    val tags = mc.targetBlock?.state
        ?.tags
        ?.toList()
        ?.map { tag -> tag.location.toString() }
    return MatchEntryDialog(
        title = blockInfoMatcherEntryTag,
        modifier = modifier,
        screenModifier = screenModifier,
        entrySupplier = { mode -> BlockInfoMatchEntry.Tag(tag, mode) },
        entryConsumer = entryConsumer
    ) {
        var editor by lateInitValueOf<TextEditorWidget>()
        Row(horizontalArrangement = Arrangement.spacedBy(5f)) {
            Column(horizontalAlignment = Alignment.Left, verticalArrangement = Arrangement.spacedBy(5f)) {
                if (!tags.isNullOrEmpty()) {
                    Text(HSLang.getFromTargetBlock, modifier = Modifier.height(20f))
                }
                Text(HSLang.tag, modifier = Modifier.height(20f))
            }
            Column(horizontalAlignment = Alignment.Right, verticalArrangement = Arrangement.spacedBy(5f)) {
                if (!tags.isNullOrEmpty()) {
                    Selector(
                        tags,
                        modifier = Modifier.width(200f),
                        onSelected = {
                            editor.text = it
                            tag = it
                        },
                        selectedWrapper = {
                            Text(it, modifier = Modifier.weight(1))
                        },
                        optionWrapper = {
                            Text(it, modifier = Modifier.width(tags.maxWidth + 1))
                        }
                    )
                }
                TextEditor(modifier = Modifier.width(200f)) {
                    text = tag
                    textConsumer { tag = it }
                    editor = owner()
                }
            }
        }
    }
}

private fun PropertyMatchEntryBuilder(
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    entry: BlockInfoMatchEntry.Property = BlockInfoMatchEntry.Property(
        mc.targetBlock?.state?.values?.entries?.firstOrNull()?.run {
            this.key.name to Util.getPropertyName(this.key, this.value)
        } ?: ("" to ""),
        MatchEntry.MatchMode.Include
    ),
    entryConsumer: (BlockInfoMatchEntry.Property) -> Unit
): IGScreenImpl {

    var key = entry.property.first
    var value = entry.property.second

    val properties = mc.targetBlock?.state?.values?.map { (key, value) ->
        key.name to Util.getPropertyName(key, value)
    }

    return MatchEntryDialog(
        title = blockInfoMatcherEntryProperty,
        modifier = modifier,
        screenModifier = screenModifier,
        entrySupplier = { mode -> BlockInfoMatchEntry.Property(key to value, mode) },
        entryConsumer = entryConsumer
    ) {
        var k by lateInitValueOf<TextEditorWidget>()
        var v by lateInitValueOf<TextEditorWidget>()
        Row(horizontalArrangement = Arrangement.spacedBy(5f)) {
            Column(horizontalAlignment = Alignment.Left, verticalArrangement = Arrangement.spacedBy(5f)) {
                if (!properties.isNullOrEmpty()) {
                    Text(HSLang.getFromTargetBlock, modifier = Modifier.height(20f))
                }
                Text(HSLang.blockProperty, modifier = Modifier.height(20f))
            }

            Column(horizontalAlignment = Alignment.Right, verticalArrangement = Arrangement.spacedBy(5f)) {
                if (!properties.isNullOrEmpty()) {
                    Selector(
                        properties,
                        modifier = Modifier.matchSibling(),
                        onSelected = { (kt, vt) ->
                            k.text = kt
                            key = kt
                            v.text = vt
                            value = vt
                        },
                        selectedWrapper = {
                            Text("${it.first} = ${it.second}", modifier = Modifier.weight(1))
                        },
                        optionWrapper = {
                            Text(
                                "${it.first} = ${it.second}",
                                modifier = Modifier.width(properties.map { p -> "${p.first} = ${p.second}" }.maxWidth + 1)
                            )
                        }
                    )
                }
                Row {
                    k = TextEditor(modifier = Modifier.width(100f)) {
                        text = key
                        textConsumer { key = it }
                    }
                    Text(" = ")
                    v = TextEditor(modifier = Modifier.width(60f)) {
                        text = value
                        textConsumer { value = it }
                    }
                }
            }
        }
    }
}


//------------ BlockInfoMatcherTableColumn ------------\\

fun <T> TableScope<T>.BlockInfoMatcherTableColumn(
    consumer: (Int, T, BlockInfoMatcher) -> Unit,
    matcherExtractor: (T) -> BlockInfoMatcher,
    buttonModifier: TableLayoutColumnScope.() -> Modifier = { Modifier },
    weight: Int = 0,
    header: TableLayoutColumnScope.() -> GuiWidget,
) = Header(weight, header).Column { index, entry ->
    val matcher = matcherExtractor(entry)
    Button(
        modifier = Modifier.hoverTip {
            BlockInfoMatcherInfo(matcher)
        }.then(buttonModifier()),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BlockInfoMatcherSimpleInfo(matcher)
        click {
            BlockInfoMatcherBuilder(matcher, {
                consumer(index, entry, it)
            }).open()
        }
    }
}

//------------ BlockInfoMatcher ------------\\

fun ContainerScope.BlockInfoMatcherSimpleInfo(
    blockInfoMatcher: CompositeMatcher<BlockInfo>,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Center,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) = Row(
    modifier,
    horizontalArrangement,
    verticalAlignment
) {
    val entries = blockInfoMatcher.entries
    when (entries.size) {
        0    -> Text(IGLang.hasNothing)
        1    -> BlockInfoEntryInfo(entries.first())
        else -> {
            if (BlockInfoMatcher.isAnyMatcher(blockInfoMatcher)) {
                Text(CompositeMatcher.MatchMode.AnyMatch.translateText)
            } else Text(blockInfoMatcher.mode.translateText.appendLiteral(":").append(IGLang.listConfigWrapperText(entries.size)))
        }
    }
}

fun ContainerScope.BlockInfoMatcherInfo(
    blockInfoMatcher: CompositeMatcher<BlockInfo>,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(1f),
    horizontalAlignment: Alignment.Horizontal = Alignment.Left,
) = Column(
    modifier,
    verticalArrangement,
    horizontalAlignment
) {
    //mode
    Text(
        blockInfoMatcher.mode.translateText,
        Modifier.matchSibling(),
        setting = TextWidget.Setting().copy(horizontalAlignment = Alignment.CenterHorizontally)
    )
    Rect(Colors.DARK_BLUE_GREY.alpha(0.5f), Modifier.height(1f).matchSibling())
    //entries
    blockInfoMatcher.entries.forEachWithLimit(10) {
        BlockInfoEntryInfo(it)
    }
}

//------------ BlockInfoEntryInfo ------------\\

@Suppress("UNCHECKED_CAST")
fun ContainerScope.BlockInfoEntryInfo(entry: MatchEntry<BlockInfo>) {
    when (entry) {
        is BlockInfoMatchEntry.Matcher  -> BlockInfoEntryMatcherInfo(entry)
        is BlockInfoMatchEntry.Block    -> BlockInfoEntryBlockInfo(entry)
        is BlockInfoMatchEntry.Script   -> BlockInfoEntryScriptInfo(entry)
        is BlockInfoMatchEntry.Pos      -> BlockInfoEntryPosInfo(entry)
        is BlockInfoMatchEntry.Tag      -> BlockInfoEntryTagInfo(entry)
        is BlockInfoMatchEntry.Property -> BlockInfoEntryPropertyInfo(entry)
    }
}


fun ContainerScope.BlockInfoEntryMatcherInfo(
    entry: BlockInfoMatchEntry.Matcher,
) = Row(
    horizontalArrangement = Arrangement.spacedBy(2f, Alignment.Left)
) {
    ModeRect(entry)
    Text(blockInfoMatcherEntryMatcher)
    BlockInfoMatcherSimpleInfo(entry.matcher)
}

fun ContainerScope.BlockInfoEntryBlockInfo(
    entry: BlockInfoMatchEntry.Block,
) = Row(
    horizontalArrangement = Arrangement.spacedBy(2f, Alignment.Left)
) {
    ModeRect(entry)
    Text(blockInfoMatcherEntryBlock)
    ItemIcon(entry.block, .6f)
    Text(entry.asText)
}

fun ContainerScope.BlockInfoEntryScriptInfo(
    entry: BlockInfoMatchEntry.Script,
) = Row(
    horizontalArrangement = Arrangement.spacedBy(2f, Alignment.Left)
) {
    ModeRect(entry)
    Text(blockInfoMatcherEntryPos)
    Text(mutableStateBy { entry.asText }, modifier = Modifier.maxWidth(180f))
}

fun ContainerScope.BlockInfoEntryPosInfo(
    entry: BlockInfoMatchEntry.Pos,
) = Row(
    horizontalArrangement = Arrangement.spacedBy(2f, Alignment.Left)
) {
    ModeRect(entry)
    Text(blockInfoMatcherEntryPos)
    Text(mutableStateBy { entry.asText }, modifier = Modifier.maxWidth(180f))
}

fun ContainerScope.BlockInfoEntryTagInfo(
    entry: BlockInfoMatchEntry.Tag,
) = Row(
    horizontalArrangement = Arrangement.spacedBy(2f, Alignment.Left)
) {
    ModeRect(entry)
    Text(blockInfoMatcherEntryTag)
    Text(mutableStateBy { entry.asText }, modifier = Modifier.maxWidth(180f))
}

fun ContainerScope.BlockInfoEntryPropertyInfo(
    entry: BlockInfoMatchEntry.Property,
) = Row(
    horizontalArrangement = Arrangement.spacedBy(2f, Alignment.Left)
) {
    ModeRect(entry)
    Text(blockInfoMatcherEntryProperty)
    Text(mutableStateBy { entry.asText }, modifier = Modifier.maxWidth(180f))
}