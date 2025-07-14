package moe.forpleuvoir.hiirosakura.functional.itemeditor

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.delay
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.hiirosakura.gui.widget.EditButton
import moe.forpleuvoir.hiirosakura.gui.widget.RemoveButton
import moe.forpleuvoir.hiirosakura.util.closeScreen
import moe.forpleuvoir.hiirosakura.util.lateInitValueOf
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.active
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.hoverText
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.padding
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.size
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.scope.GuiScope.Companion.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.base.tip.Tip
import moe.forpleuvoir.ibukigourd.gui.base.toast.Toast
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
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.layout.RowScope
import moe.forpleuvoir.ibukigourd.gui.widget.layout.list.ColumnListScope
import moe.forpleuvoir.ibukigourd.gui.widget.layout.list.ColumnListWrapped
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextLabel
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.text.copyToText
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.ibukigourd.util.state.MutableState
import moe.forpleuvoir.ibukigourd.util.state.asMutableState
import moe.forpleuvoir.ibukigourd.util.state.asState
import moe.forpleuvoir.ibukigourd.util.state.mutableStateOf
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.common.color.HSVColor
import moe.forpleuvoir.nebula.common.util.ioLaunch
import moe.forpleuvoir.nebula.common.util.primitive.pick
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.registry.DynamicRegistryManager
import kotlin.time.Duration.Companion.milliseconds

fun ContainerScope.ItemStackManagerGui(
    registryManager: DynamicRegistryManager,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
) = Column(modifier, verticalArrangement, horizontalAlignment) {
    //TODO i18n
    var recompose by lateInitValueOf<() -> Unit>()
    val deferred = ItemStackManager.loadDataAsync(registryManager)

    val regex = Regex("").asMutableState
    println("重组了")

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
            TextLabel("以手中的物品为基础添加")
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
    scrollerModifier: RowScope.() -> Modifier = { Modifier },
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
        TextLabel("Loading...")
    } else {
        val list = ItemStackManager.asSequence().filter {
            regex.getValue().containsMatchIn(it.name.string)
                    || regex.getValue().containsMatchIn(it.count.toString())
        }
        val filtered = list.count() != ItemStackManager.asSequence().count()

        if (list.count() == 0) {
            TextLabel(IGLang.hasNothing)
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
            delay(50.milliseconds)
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
    Row(horizontalArrangement = Arrangement.spacedBy(4f)) {
        if (!filtered) {
            MoveButton(recompose, index)
        }
        ItemIcon(itemStack)
        TextLabel(itemStack.name.copyToText(), Modifier)
    }
    Row(horizontalArrangement = Arrangement.spacedBy(4f)) {

        if (mc.player?.isCreative == true) {
            Button {
                TextLabel("获取物品到背包")
                click {
                    mc.player?.inventory?.insertStack(itemStack.copy())
                    Toast.showToast(Literal("已将物品[").append(itemStack.name).appendLiteral("]添加到背包l"))
                }
            }
        }

        EditButton {
            ItemStackEditor(itemStack) { newItem ->
                ItemStackManager[index] = newItem
            }.open()
        }

        RemoveButton {
            ConfirmDialog(Literal("Are you sure you want to delete this item?").asState) {
                Row {
                    ItemIcon(itemStack)
                    TextLabel(itemStack.name.copyToText())
                }
                confirm {
                    ItemStackManager.removeAt(index)
                    recompose()
                    closeScreen()
                }
            }
        }
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