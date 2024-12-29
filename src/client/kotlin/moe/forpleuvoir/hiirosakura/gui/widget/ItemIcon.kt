package moe.forpleuvoir.hiirosakura.gui.widget

import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.attachLeft
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.render
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.size
import moe.forpleuvoir.ibukigourd.gui.base.scope.WidgetContainerScope
import moe.forpleuvoir.ibukigourd.gui.widget.Widget
import moe.forpleuvoir.ibukigourd.util.state.State
import moe.forpleuvoir.ibukigourd.util.state.mutableStateBy
import moe.forpleuvoir.ibukigourd.util.state.stateOf
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

@JvmName("ItemStackIcon")
fun WidgetContainerScope.ItemIcon(
    item: State<ItemStack>,
    scale: Float = 1f,
    modifier: Modifier = Modifier
) = Widget(modifier.attachLeft {
    size(16f * scale, 16f * scale)
        .render { context, x, y, delta ->
            context.useMatrixStack {
                it.scale(scale, scale, 1f)
                it.translate(transform.worldX * (1f / scale), transform.worldY * (1f / scale), 0f)
                drawItem(item.getValue(), 0, 0)
            }
        }
})

fun WidgetContainerScope.ItemIcon(
    item: ItemStack,
    scale: Float = 1f,
    modifier: Modifier = Modifier
) = ItemIcon(stateOf(item), scale, modifier)


fun WidgetContainerScope.ItemIcon(
    item: Item,
    scale: Float = 1f,
    modifier: Modifier = Modifier
) = ItemIcon(stateOf(ItemStack(item)), scale, modifier)

fun WidgetContainerScope.ItemIcon(
    item: State<Item>,
    scale: Float = 1f,
    modifier: Modifier = Modifier
) = ItemIcon(mutableStateBy { ItemStack(item.getValue()) }, scale, modifier)