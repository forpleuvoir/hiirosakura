package moe.forpleuvoir.hiirosakura.functional.itemeditor

import kotlinx.coroutines.Deferred
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.hiirosakura.gui.widget.CopyButton
import moe.forpleuvoir.hiirosakura.gui.widget.EditButton
import moe.forpleuvoir.hiirosakura.gui.widget.RemoveButton
import moe.forpleuvoir.hiirosakura.util.key
import moe.forpleuvoir.hiirosakura.util.registryAccess
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.*
import moe.forpleuvoir.ibukigourd.gui.base.render.IGGuiGraphics
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.scope.GuiScope.Companion.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.base.screen.closeScreen
import moe.forpleuvoir.ibukigourd.gui.base.tip.Tip
import moe.forpleuvoir.ibukigourd.gui.base.toast.Toast
import moe.forpleuvoir.ibukigourd.gui.base.widget.WidgetTextures
import moe.forpleuvoir.ibukigourd.gui.base.widget.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.modifier.bgHoverHighlightBox
import moe.forpleuvoir.ibukigourd.gui.util.Direction
import moe.forpleuvoir.ibukigourd.gui.util.ScrollState
import moe.forpleuvoir.ibukigourd.gui.widget.ConfirmDialog
import moe.forpleuvoir.ibukigourd.gui.widget.ItemIcon
import moe.forpleuvoir.ibukigourd.gui.widget.SearchBar
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.button.FlatButton
import moe.forpleuvoir.ibukigourd.gui.widget.icon.Icon
import moe.forpleuvoir.ibukigourd.gui.widget.icon.IconTextures
import moe.forpleuvoir.ibukigourd.gui.widget.layout.BoxScope
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.layout.RowScope
import moe.forpleuvoir.ibukigourd.gui.widget.layout.list.ColumnListScope
import moe.forpleuvoir.ibukigourd.gui.widget.layout.list.ColumnListWrapped
import moe.forpleuvoir.ibukigourd.gui.widget.text.Text
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextSetting
import moe.forpleuvoir.ibukigourd.util.lateInitValueOf
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.ibukigourd.util.state.MutableState
import moe.forpleuvoir.ibukigourd.util.state.asMutableState
import moe.forpleuvoir.ibukigourd.util.state.asState
import moe.forpleuvoir.ibukigourd.util.state.mutableStateOf
import moe.forpleuvoir.ibukigourd.util.textRenderer
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.common.color.HSVColor
import moe.forpleuvoir.nebula.common.util.ioLaunch
import moe.forpleuvoir.nebula.common.util.primitive.pick
import net.minecraft.Util
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner
import net.minecraft.core.RegistryAccess
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.*
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import java.util.Map
import java.util.regex.Pattern
import kotlin.jvm.optionals.getOrNull

fun ContainerScope.ItemStackManagerGui(
    registryAccess: RegistryAccess,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(5f),
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
) = Column(modifier, verticalArrangement, horizontalAlignment) {
    var recompose by lateInitValueOf<() -> Unit>()
    val deferred = ItemStackManager.loadDataAsync(registryAccess)

    val regex = Regex("").asMutableState

    ToolBar(regexConsumer = {
        regex.setValue(it)
        recompose()
    }) {
        ItemStackManager.add(it)
        recompose()
    }

    ItemStackList(
        deferred,
        regex,
        mutableStateOf(false),
        modifier = Modifier.fill()
    ).apply {
        recompose = {
            this.executeRecompose()
        }
    }
}


private fun ContainerScope.ToolBar(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    regexConsumer: (Regex) -> Unit,
    consumer: (ItemStack) -> Unit
) = Row(modifier, horizontalArrangement, verticalAlignment) {
    SearchBar(
        textConsumer = {
            regexConsumer(it.toRegex())
        },
        modifier = Modifier.weight(1),
        textEditorModifier = { Modifier.weight(1) }
    )
    ItemStackMatcher.handheldItemStack?.let { handheldItem ->
        Button {
            Text(HSLang.itemEditorAddFromHandheldItem)
            click {
                ItemStackEditor(handheldItem, consumer).open()
            }
        }
    }
    Button {
        Icon(IconTextures.PLUS, HSVColor(120f, 1f, .65f), modifier = Modifier.size(9f, 9f))
        click {
            ItemStackEditor(ItemStack(Items.MELON), consumer).open()
        }
    }
}


private fun ContainerScope.ItemStackList(
    loadDataAsync: Deferred<Result<Unit>>,
    regex: MutableState<Regex>,
    loaded: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = ScrollState(),
    spacing: Float = 2f,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    barThickness: Float = 9f,
    listModifier: RowScope.() -> Modifier = { Modifier.fill().weight(1) },
    scrollerModifier: BoxScope.() -> Modifier = { Modifier },
) = ColumnListWrapped(
    modifier,
    scrollState,
    spacing,
    horizontalAlignment,
    barThickness,
    listModifier,
    scrollerModifier
) {
    if (!loaded.getValue()) {
        //TODO i18n
        Text("Loading...")
    } else {
        val list = ItemStackManager.asSequence().filter {
            regex.getValue().containsMatchIn(it.hoverName.string)
                    || regex.getValue().containsMatchIn(it.count.toString())
        }
        val filtered = list.count() != ItemStackManager.asSequence().count()

        if (list.count() == 0) {
            Text(IGLang.hasNothing)
            return@ColumnListWrapped
        }
        val recompose = { this@ColumnListWrapped.executeRecompose() }
        list.forEachIndexed { index, itemStack ->
            EntryRow(index, itemStack, filtered, recompose)
        }
    }
    if (!loaded.getValue()) {
        ioLaunch {
            loadDataAsync.await()
            loaded.setValue(true)
//            delay(50.milliseconds)
            this@ColumnListWrapped.executeRecompose()
        }
    }
}

private fun ColumnListScope.EntryRow(
    index: Int,
    itemStack: ItemStack,
    filtered: Boolean,
    recompose: () -> Unit
) = Row(Modifier.fill().bgHoverHighlightBox(), horizontalArrangement = Arrangement.SpaceBetween) {
    Row(
        modifier = Modifier.renderOverlay { guiGraphics, mx, my, d ->
            if (wasMouseOver) guiGraphics.postEndRender {
                pushItemTooltip(itemStack, mx, my)
            }
        },
        horizontalArrangement = Arrangement.spacedBy(4f)
    ) {
        if (!filtered) {
            MoveButton(recompose, index)
        }
        ItemIcon(itemStack)
        Text(itemStack.styledHoverName, Modifier)
    }
    Row(horizontalArrangement = Arrangement.spacedBy(4f)) {

        if (mc.player?.isCreative == true) {
            Button {
                Text(HSLang.itemEditorGetToBackpack)
                click {
                    mc.player?.inventory?.add(itemStack.copy())
                    Toast.showToast(HSLang.itemEditorGetToBackpackSuccess(itemStack.hoverName))
                }
            }
        }

        CopyButton(Modifier.hoverText(HSLang.itemEditorCopyToCommand)) {
            val command = genCommand(itemStack)
            mc.keyboardHandler.clipboard = command
            Toast.showToast {
                Text(command, modifier = Modifier.maxWidth(360f).maxHeight(400f), setting = TextSetting(autoNewLine = true))
            }
        }

        EditButton {
            ItemStackEditor(itemStack) { newItem ->
                ItemStackManager[index] = newItem
                recompose()
            }.open()
        }

        RemoveButton {
            ConfirmDialog(HSLang.itemEditorRemoveConfirm.asState) {
                Row(
                    Modifier
                        .width(120f)
                        .padding(horizontal = 5f, vertical = 4f)
                        .render { ctx, _, _, _ ->
                            ctx.pushWidgetTexture(transform, WidgetTextures.DROP_DOWN_MENU_BACKGROUND)
                        }
                        .renderOverlay { guiGraphics, mx, my, d ->
                            if (wasMouseOver) guiGraphics.postEndRender {
                                pushItemTooltip(itemStack, mx, my)
                            }
                        },
                    Arrangement.spacedBy(5f)
                ) {
                    ItemIcon(itemStack, 0.6f)
                    Text(itemStack.hoverName)
                }
                confirm {
                    ItemStackManager.removeAt(index)
                    recompose()
                    closeScreen()
                }
            }.open()
        }
    }
}

fun IGGuiGraphics.pushItemTooltip(
    itemStack: ItemStack,
    mx: Float,
    my: Float
) {
    val lines = Screen.getTooltipFromItem(mc, itemStack)
    if(lines.isEmpty()) return
    val list = lines.stream()
        .map { it.visualOrderText }
        .map { ClientTooltipComponent.create(it) }
        .collect(Util.toMutableList())
    renderTooltip(
        textRenderer,
        list,
        mx.toInt(),
        my.toInt(),
        DefaultTooltipPositioner.INSTANCE,
        itemStack.get(DataComponents.TOOLTIP_STYLE)
    )
}

private fun genCommand(itemStack: ItemStack): String {
    val tag = DataComponentPatch.CODEC.encodeStart(registryAccess!!.createSerializationContext(NbtOps.INSTANCE), itemStack.componentsPatch).result().getOrNull()
    val tagString = tag?.getAsString() ?: ""
    val type = itemStack.item.key.toString()
    val count = itemStack.count
    return "/give @p $type$tagString $count"
}

private fun <T : Tag> T.getAsString(): String {
    return if (this is CompoundTag) {
//        TextComponentTagVisitor("").visit(this).string
        CommandNbtWriter().apply {
            visitCompound(this@getAsString as CompoundTag)
        }.build()
    } else {
        this.toString()
    }
}

class CommandNbtWriter() : StringTagVisitor() {
    override fun visitCompound(tag: CompoundTag) {
        this.builder.append('[')
        val list = ArrayList<MutableMap.MutableEntry<String, Tag>>(tag.entrySet())
        list.sortWith(Map.Entry.comparingByKey<String, Tag>())

        for (i in list.indices) {
            val entry = list[i]
            if (i != 0) {
                this.builder.append(',')
            }

            val key = entry.key
            if (!key.equals("true", ignoreCase = true) && !key.equals("false", ignoreCase = true) && Pattern.compile("[A-Za-z._]+[A-Za-z0-9._+-]*")
                    .matcher(key)
                    .matches()
            ) {
                this.builder.append(key)
            } else {
                buildString {
                    StringTag.quoteAndEscape(key, this)
                }.apply {
                    builder.append(this.trim('"'))
                }
            }
            this.builder.append('=')
            val sub = StringTagVisitor()
            entry.value.accept(sub)
            this.builder.append(sub.builder)
        }

        this.builder.append(']')
    }
}

private fun ContainerScope.MoveButton(
    recompose: () -> Unit,
    index: Int,
) = Column {
    FlatButton(
        hoveredColor = Colors.BLACK.alpha(.15f),
        round = 0,
        modifier = Modifier.hoverText(IGLang.moveUp).padding(1).active(index > 0)
    ) {
        Icon(IconTextures.UP, modifier = Modifier, color = Colors.GRAY.alpha((index > 0).pick(1f, .25f)))

        click {
            ItemStackManager.moveElement(index, (index - 1).coerceAtLeast(0))
            recompose()
        }
    }
    FlatButton(
        hoveredColor = Colors.BLACK.alpha(.15f),
        round = 0,
        modifier = Modifier.hoverText(IGLang.moveDown, Tip.DefaultSetting.copy(optionalDirection = Direction.clockwiseFromBottom)).padding(1)
            .active(index != ItemStackManager.lastIndex)
    ) {
        Icon(
            IconTextures.DOWN,
            modifier = Modifier,
            color = Colors.GRAY.alpha((index != ItemStackManager.lastIndex).pick(1f, .25f))
        )

        click {
            ItemStackManager.moveElement(index, (index + 1).coerceAtMost(ItemStackManager.lastIndex))
            recompose()
        }
    }
}