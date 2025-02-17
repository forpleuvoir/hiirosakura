package moe.forpleuvoir.hiirosakura.gui.widget

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.HSLang.blockInfoMatcher
import moe.forpleuvoir.hiirosakura.HSLang.blockInfoMatcherEntryBlock
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
import moe.forpleuvoir.ibukigourd.gui.base.scope.WidgetContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.modifier.bgHoverHighlightBox
import moe.forpleuvoir.ibukigourd.gui.widget.Dialog
import moe.forpleuvoir.ibukigourd.gui.widget.Rect
import moe.forpleuvoir.ibukigourd.gui.widget.Selector
import moe.forpleuvoir.ibukigourd.gui.widget.Vector3iEditor
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.button.RadioButtons
import moe.forpleuvoir.ibukigourd.gui.widget.icon.Icon
import moe.forpleuvoir.ibukigourd.gui.widget.icon.IconTextures
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.layout.list.RowListWrapped
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextAreaWrapped
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextEditor
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextEditorWidget
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextLabel
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.text.maxWidth
import moe.forpleuvoir.ibukigourd.text.translateComment
import moe.forpleuvoir.ibukigourd.text.translateText
import moe.forpleuvoir.ibukigourd.util.forEachWithLimit
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.ibukigourd.util.state.MutableState
import moe.forpleuvoir.ibukigourd.util.state.mutableStateBy
import moe.forpleuvoir.ibukigourd.util.state.mutableStateOf
import moe.forpleuvoir.ibukigourd.util.textRenderer
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.common.color.HSVColor
import net.minecraft.block.Blocks
import net.minecraft.util.Util
import org.joml.Vector3i
import kotlin.jvm.optionals.getOrNull

private val map: Map<Text, (Modifier, Modifier, (BlockInfoMatchEntry) -> Unit) -> IGScreenImpl> = mapOf(
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
    screenModifier: Modifier = Modifier
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
        val selectedMode: MutableState<MultiMatcher.MatchMode?> = mutableStateOf(matcher.mode)
        TextLabel(blockInfoMatcher)
        Row(
            verticalArrangement = Arrangement.spacedBy(2f),
        ) {
            Column(
                Modifier.fill(),
                horizontalArrangement = Arrangement.spacedBy(4f, Alignment.Left)
            ) {
                RadioButtons(
                    MultiMatcher.MatchMode.entries,
                    selectedMode,
                    { matcher.mode = it },
                    {
                        TextLabel(it.translateText)
                    },
                    {
                        Modifier.hoverText(it.translateComment)
                    }
                )
            }
            Column(
                horizontalArrangement = Arrangement.spacedBy(4f)
            ) {
                RowListWrapped(
                    modifier = Modifier.height(180f).weight(1),
                    listModifier = { Modifier.weight(1).fill() },
                ) {
                    if (matcher.entries.isEmpty()) {
                        TextLabel(IGLang.hasNothing)
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
                }.apply {
                    onChanged = { screen()?.execute { this.recompose() } }
                }

                Row(
                    modifier = Modifier.height(180f),
                    verticalArrangement = Arrangement.spacedBy(2f),
                ) {
                    map.forEach { (key, builder) ->
                        Button(Modifier, Arrangement.spacedBy(6f, Alignment.CenterHorizontally)) {
                            TextLabel(key, modifier = Modifier.width(map.keys.maxWidth(textRenderer).toFloat()))
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

private fun WidgetContainerScope.EntryWrapper(
    entry: BlockInfoMatchEntry,
    entryConsumer: (BlockInfoMatchEntry) -> Unit,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier
) = Column(
    Modifier
        .padding(horizontal = 2f)
        .bgHoverHighlightBox()
        .then(modifier),
    horizontalArrangement = Arrangement.spacedBy(4f)
) {
    TextLabel(entry.translateText, modifier = Modifier.weight(1))
    when (entry) {
        is BlockInfoMatchEntry.Block    -> Column(
            horizontalArrangement = Arrangement.spacedBy(2f)
        ) {
            ItemIcon(entry.block.asItem(), .6f)
            TextLabel(entry.asText)
        }

        is BlockInfoMatchEntry.Script   -> TextLabel(entry.asText)
        is BlockInfoMatchEntry.Pos      -> TextLabel(entry.asText)
        is BlockInfoMatchEntry.Tag      -> TextLabel(entry.asText)
        is BlockInfoMatchEntry.Property -> TextLabel(entry.asText)
    }
    TextLabel(entry.mode.translateText)
    EditButton {
        when (entry) {
            is BlockInfoMatchEntry.Block    -> BlockMatchEntryBuilder(entry = entry, entryConsumer = entryConsumer)
            is BlockInfoMatchEntry.Script   -> ScriptMatchEntryBuilder(entry = entry, entryConsumer = entryConsumer)
            is BlockInfoMatchEntry.Pos      -> PosMatchEntryBuilder(entry = entry, entryConsumer = entryConsumer)
            is BlockInfoMatchEntry.Tag      -> TagMatchEntryBuilder(entry = entry, entryConsumer = entryConsumer)
            is BlockInfoMatchEntry.Property -> PropertyMatchEntryBuilder(entry = entry, entryConsumer = entryConsumer)
        }.open()
    }
    RemoveButton { removeAction() }
}

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
            searchBarModifier = { Modifier.width(120f) },
            listModifier = { Modifier.width(120f) },
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
        Column(modifier = Modifier.width(240f), horizontalArrangement = Arrangement.SpaceBetween) {
            TextLabel("MIN: ")
            Vector3iEditor(start)
        }
        Column(modifier = Modifier.width(240f), horizontalArrangement = Arrangement.SpaceBetween) {
            TextLabel("MAX: ")
            Vector3iEditor(end)
        }
    }
}

private fun TagMatchEntryBuilder(
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    entry: BlockInfoMatchEntry.Tag = BlockInfoMatchEntry.Tag(
        mc.targetBlock?.state?.streamTags()?.findFirst()?.getOrNull()?.id?.toString() ?: "",
        MatchEntry.MatchMode.Include
    ),
    entryConsumer: (BlockInfoMatchEntry.Tag) -> Unit
): IGScreenImpl {
    var tag = entry.tag
    val tags = mc.targetBlock?.state
        ?.streamTags()
        ?.toList()
        ?.map { tag -> tag.id.toString() }
    return MatchEntryDialog(
        title = blockInfoMatcherEntryTag,
        modifier = modifier,
        screenModifier = screenModifier,
        entrySupplier = { mode -> BlockInfoMatchEntry.Tag(tag, mode) },
        entryConsumer = entryConsumer
    ) {
        val editor = TextEditor(modifier = Modifier.width(200f)) {
            text = tag
            textConsumer { tag = it }
        }
        tags?.let { tags ->
            if (tags.isEmpty()) return@let
            Selector(
                tags,
                modifier = Modifier.width(200f).hoverText(HSLang.fromTargetBlock),
                onSelected = {
                    editor.text = it
                    tag = it
                },
                selectedWrapper = {
                    TextLabel(it, modifier = Modifier.weight(1))
                },
                optionWrapper = {
                    TextLabel(it, modifier = Modifier.width(tags.maxWidth(textRenderer).toFloat() + 1))
                }
            )
        }
    }
}

private fun PropertyMatchEntryBuilder(
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    entry: BlockInfoMatchEntry.Property = BlockInfoMatchEntry.Property(
        mc.targetBlock?.state?.entries?.entries?.firstOrNull()?.run {
            this.key.name to Util.getValueAsString(this.key, this.value)
        } ?: ("" to ""),
        MatchEntry.MatchMode.Include
    ),
    entryConsumer: (BlockInfoMatchEntry.Property) -> Unit
): IGScreenImpl {

    var key = entry.property.first
    var value = entry.property.second

    val properties = mc.targetBlock?.state?.entries?.map { (key, value) ->
        key.name to Util.getValueAsString(key, value)
    }

    return MatchEntryDialog(
        title = blockInfoMatcherEntryProperty,
        modifier = modifier,
        screenModifier = screenModifier,
        entrySupplier = { mode -> BlockInfoMatchEntry.Property(key to value, mode) },
        entryConsumer = entryConsumer
    ) {
        var k: TextEditorWidget? = null
        var v: TextEditorWidget? = null
        Column {
            k = TextEditor(modifier = Modifier.width(120f)) {
                text = key
                textConsumer { key = it }
            }
            TextLabel(" = ")
            v = TextEditor(modifier = Modifier.width(120f)) {
                text = value
                textConsumer { value = it }
            }

        }
        properties?.let { properties ->
            if (properties.isEmpty()) return@let
            Selector(
                properties,
                modifier = Modifier.matchSibling().hoverText(HSLang.fromTargetBlock),
                onSelected = { (kt, vt) ->
                    k?.text = kt
                    key = kt
                    v?.text = vt
                    value = vt
                },
                selectedWrapper = {
                    TextLabel("${it.first} = ${it.second}", modifier = Modifier.weight(1))
                },
                optionWrapper = {
                    TextLabel(
                        "${it.first} = ${it.second}",
                        modifier = Modifier.width(properties.map { p -> "${p.first} = ${p.second}" }.maxWidth(textRenderer).toFloat() + 1)
                    )
                }
            )
        }
    }
}

fun WidgetContainerScope.BlockInfoMatcherSimpleInfo(
    blockInfoMatcher: MultiMatcher<BlockInfo>,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Center,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) = Column(
    modifier,
    horizontalArrangement,
    verticalAlignment
) {
    val entries = blockInfoMatcher.entries
    when (entries.size) {
        0    -> TextLabel(IGLang.hasNothing)
        1    -> BlockInfoEntryInfo(entries.first())
        else -> TextLabel(IGLang.listConfigWrapperText(entries.size))
    }
}

fun WidgetContainerScope.BlockInfoMatcherInfo(
    blockInfoMatcher: MultiMatcher<BlockInfo>,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(1f),
    horizontalAlignment: Alignment.Horizontal = Alignment.Left,
) = Row(
    modifier,
    verticalArrangement,
    horizontalAlignment
) {
    //mode
    TextLabel(blockInfoMatcher.mode.translateText)
    Rect(Colors.DARK_BLUE_GREY.alpha(0.5f), Modifier.height(1f).matchSibling())
    //entries
    blockInfoMatcher.entries.forEachWithLimit(10) {
        BlockInfoEntryInfo(it)
    }
}

@Suppress("UNCHECKED_CAST")
fun WidgetContainerScope.BlockInfoEntryInfo(entry: MatchEntry<BlockInfo>) {
    when (entry) {
        is BlockInfoMatchEntry.Block    -> BlockInfoEntryBlockInfo(entry)
        is BlockInfoMatchEntry.Script   -> BlockInfoEntryScriptInfo(entry)
        is BlockInfoMatchEntry.Pos      -> BlockInfoEntryPosInfo(entry)
        is BlockInfoMatchEntry.Tag      -> BlockInfoEntryTagInfo(entry)
        is BlockInfoMatchEntry.Property -> BlockInfoEntryPropertyInfo(entry)
    }
}

fun WidgetContainerScope.BlockInfoEntryBlockInfo(
    entry: BlockInfoMatchEntry.Block,
) = Column(
    horizontalArrangement = Arrangement.spacedBy(2f, Alignment.Left)
) {
    Rect(if (entry.mode.toBoolean()) Colors.GREEN.alpha(.5F) else Colors.RED.alpha(0.5f), Modifier.width(1f).matchSibling())
    TextLabel(blockInfoMatcherEntryBlock)
    ItemIcon(entry.block.asItem(), .6f)
    TextLabel(entry.asText)
}

fun WidgetContainerScope.BlockInfoEntryScriptInfo(
    entry: BlockInfoMatchEntry.Script,
) = Column(
    horizontalArrangement = Arrangement.spacedBy(2f, Alignment.Left)
) {
    Rect(if (entry.mode.toBoolean()) Colors.GREEN.alpha(.5F) else Colors.RED.alpha(0.5f), Modifier.width(1f).matchSibling())
    TextLabel(
        mutableStateBy { blockInfoMatcherEntryScript.append(entry.asText) },
        modifier = Modifier.maxWidth(180f)
    )
}

fun WidgetContainerScope.BlockInfoEntryPosInfo(
    entry: BlockInfoMatchEntry.Pos,
) = Column(
    horizontalArrangement = Arrangement.spacedBy(2f, Alignment.Left)
) {
    Rect(if (entry.mode.toBoolean()) Colors.GREEN.alpha(.5F) else Colors.RED.alpha(0.5f), Modifier.width(1f).matchSibling())
    TextLabel(mutableStateBy { blockInfoMatcherEntryPos.append(entry.asText) }, modifier = Modifier.maxWidth(180f))
}

fun WidgetContainerScope.BlockInfoEntryTagInfo(
    entry: BlockInfoMatchEntry.Tag,
) = Column(
    horizontalArrangement = Arrangement.spacedBy(2f, Alignment.Left)
) {
    Rect(if (entry.mode.toBoolean()) Colors.GREEN.alpha(.5F) else Colors.RED.alpha(0.5f), Modifier.width(1f).matchSibling())
    TextLabel(mutableStateBy { blockInfoMatcherEntryTag.append(entry.asText) }, modifier = Modifier.maxWidth(180f))
}

fun WidgetContainerScope.BlockInfoEntryPropertyInfo(
    entry: BlockInfoMatchEntry.Property,
) = Column(
    horizontalArrangement = Arrangement.spacedBy(2f, Alignment.Left)
) {
    Rect(if (entry.mode.toBoolean()) Colors.GREEN.alpha(.5F) else Colors.RED.alpha(0.5f), Modifier.width(1f).matchSibling())
    TextLabel(
        mutableStateBy { blockInfoMatcherEntryProperty.append(entry.asText) },
        modifier = Modifier.maxWidth(180f)
    )
}