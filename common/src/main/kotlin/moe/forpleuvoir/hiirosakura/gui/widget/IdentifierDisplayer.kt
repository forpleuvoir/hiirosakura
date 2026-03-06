package moe.forpleuvoir.hiirosakura.gui.widget

import moe.forpleuvoir.hiirosakura.util.asTranslateText
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.attachLeft
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.minHeight
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.padding
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.renderBackground
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.width
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.widget.WidgetTextures
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Box
import moe.forpleuvoir.ibukigourd.gui.widget.layout.BoxScope
import moe.forpleuvoir.ibukigourd.gui.widget.text.Text
import net.minecraft.resources.ResourceLocation

fun ContainerScope.IdentifierDisplayer(
    resource: ResourceLocation,
    modifier: Modifier = Modifier.width(115f)
) = WrappedBox(modifier) {
    Text(resource.asTranslateText())
}


fun ContainerScope.WrappedBox(
    modifier: Modifier = Modifier,
    content: BoxScope.() -> Unit
) = Box(
    modifier.attachLeft {
        padding(4f).minHeight(14f).renderBackground { guiGraphics, _, _, _ ->
            guiGraphics.pushWidgetTexture(transform, WidgetTextures.DROP_DOWN_MENU_BACKGROUND)
        }
    }, content
)