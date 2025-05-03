package moe.forpleuvoir.hiirosakura.gui.widget

import moe.forpleuvoir.ibukigourd.gui.base.element.parentChain
import moe.forpleuvoir.ibukigourd.gui.base.extensions.drawcontext.useMatrixStack
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.attachLeft
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.render
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.size
import moe.forpleuvoir.ibukigourd.gui.base.scope.WidgetContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreen
import moe.forpleuvoir.ibukigourd.gui.base.widget.IGWidget
import moe.forpleuvoir.ibukigourd.gui.widget.Widget
import moe.forpleuvoir.ibukigourd.util.state.State
import moe.forpleuvoir.ibukigourd.util.state.mutableStateBy
import moe.forpleuvoir.ibukigourd.util.state.stateOf
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.DiffuseLighting
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.OverlayTexture
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemConvertible
import net.minecraft.item.ItemStack
import net.minecraft.item.ModelTransformationMode
import net.minecraft.world.World

@JvmName("ItemStackIcon")
fun WidgetContainerScope.ItemIcon(
    item: State<ItemStack>,
    scale: Float = 1f,
    modifier: Modifier = Modifier
) = Widget(modifier.attachLeft {
    size(16f * scale, 16f * scale)
        .render { context, x, y, delta ->
            context.useMatrixStack {
                it.scale(scale, scale, scale)
                it.translate(transform.worldX * (1f / scale), transform.worldY * (1f / scale), 0f)
                renderItem(item.getValue(), 0f, 0f)
            }
        }
})

val IGWidget.layers: Int
    get() = generateSequence(this.screen()) {
        it.parentScreen as IGScreen?
    }.count() + this.parentChain.size

fun WidgetContainerScope.ItemIcon(
    item: ItemStack,
    scale: Float = 1f,
    modifier: Modifier = Modifier
) = ItemIcon(stateOf(item), scale, modifier)


fun WidgetContainerScope.ItemIcon(
    item: ItemConvertible,
    scale: Float = 1f,
    modifier: Modifier = Modifier
) = ItemIcon(stateOf(ItemStack(item)), scale, modifier)

fun WidgetContainerScope.ItemIcon(
    item: State<out ItemConvertible>,
    scale: Float = 1f,
    modifier: Modifier = Modifier
) = ItemIcon(mutableStateBy { ItemStack(item.getValue()) }, scale, modifier)

fun DrawContext.renderItem(
    stack: ItemStack,
    x: Float,
    y: Float,
    z: Float = 0f,
    seed: Int = 0,
    entity: LivingEntity? = this.client.player,
    world: World? = this.client.world
) {
    if (stack.isEmpty) return
    this.client.itemModelManager.update(this.itemRenderState, stack, ModelTransformationMode.GUI, false, world, entity, seed)
    this.useMatrixStack { matrices ->
        matrices.translate(x + 8, y + 8, z + 8f)
        matrices.scale(16.0f, -16.0f, 16f)
        val isSideLit: Boolean = !this.itemRenderState.isSideLit
        if (isSideLit) {
            this.draw()
            DiffuseLighting.disableGuiDepthLighting()
        }
        this.itemRenderState.render(this.matrices, this.vertexConsumers, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV)
        this.draw()
        if (isSideLit) {
            DiffuseLighting.enableGuiDepthLighting()
        }
    }
}