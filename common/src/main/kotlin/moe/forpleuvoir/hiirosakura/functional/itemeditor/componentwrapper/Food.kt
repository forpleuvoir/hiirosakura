package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEditor
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentWrapperRow
import moe.forpleuvoir.hiirosakura.util.asTranslateText
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.*
import moe.forpleuvoir.ibukigourd.gui.base.render.texture.Corner
import moe.forpleuvoir.ibukigourd.gui.base.render.texture.TextureInfo
import moe.forpleuvoir.ibukigourd.gui.base.render.texture.WidgetTexture
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.base.widget.WidgetTextures
import moe.forpleuvoir.ibukigourd.gui.widget.DialogContent
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.button.SwitchButton
import moe.forpleuvoir.ibukigourd.gui.widget.icon.Icon
import moe.forpleuvoir.ibukigourd.gui.widget.icon.IconTextures
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.text.FloatEditor
import moe.forpleuvoir.ibukigourd.gui.widget.text.IntEditor
import moe.forpleuvoir.ibukigourd.gui.widget.text.Text
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.util.state.asMutableState
import net.minecraft.resources.Identifier
import net.minecraft.world.food.FoodProperties
import kotlin.math.absoluteValue

private val FOOD_EMPTY_HUNGER_TEXTURE = Identifier.withDefaultNamespace("textures/gui/sprites/hud/food_empty_hunger.png")
private val IG_FOOD_EMPTY_HUNGER_TEXTURE = WidgetTexture(Corner.Unspecified, 0, 0, 9, 9, TextureInfo(9, 9, FOOD_EMPTY_HUNGER_TEXTURE))
private val FOOD_EMPTY_TEXTURE = Identifier.withDefaultNamespace("textures/gui/sprites/hud/food_empty.png")
private val IG_FOOD_EMPTY_TEXTURE = WidgetTexture(Corner.Unspecified, 0, 0, 9, 9, TextureInfo(9, 9, FOOD_EMPTY_TEXTURE))
private val FOOD_HALF_TEXTURE = Identifier.withDefaultNamespace("textures/gui/sprites/hud/food_half.png")
private val IG_FOOD_HALF_TEXTURE = WidgetTexture(Corner.Unspecified, 0, 0, 9, 9, TextureInfo(9, 9, FOOD_HALF_TEXTURE))
private val FOOD_FULL_TEXTURE = Identifier.withDefaultNamespace("textures/gui/sprites/hud/food_full.png")
private val IG_FOOD_FULL_TEXTURE = WidgetTexture(Corner.Unspecified, 0, 0, 9, 9, TextureInfo(9, 9, FOOD_FULL_TEXTURE))

private fun Modifier.foodIconModifier(icon: WidgetTexture) = render { guiGraphics, _, _, _ ->
    val box = contentBox(true)
    guiGraphics {
        pushWidgetTexture(box, IG_FOOD_EMPTY_TEXTURE)
        pushWidgetTexture(box, icon)
    }
}

fun ContainerScope.FoodComponentWrapper(
    key: Identifier,
    component: FoodProperties,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f, Alignment.Right),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    onValueChange: (FoodProperties, Boolean) -> Unit,
) = DataComponentWrapperRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    Row(Modifier.width(115f).padding(vertical = 4f, horizontal = 6f).renderBackground { guiGraphics, x, y, d ->
        guiGraphics.pushWidgetTexture(transform, WidgetTextures.DROP_DOWN_MENU_BACKGROUND)
    }) {
        Row(Modifier.weight(1), horizontalArrangement = Arrangement.aligned(Alignment.Left)) {
            val nutrition = (component.nutrition / 2)
            val remainder = component.nutrition % 2
            if (nutrition > 5) {
                Icon(IG_FOOD_FULL_TEXTURE, modifier = Modifier.foodIconModifier(IG_FOOD_FULL_TEXTURE))
                Text("x$nutrition", modifier = Modifier.margin(left = 3f))
            } else {
                repeat(nutrition) {
                    Icon(IG_FOOD_FULL_TEXTURE, modifier = Modifier.foodIconModifier(IG_FOOD_FULL_TEXTURE))
                }
            }
            if (remainder != 0) {
                Icon(IG_FOOD_HALF_TEXTURE, modifier = Modifier.foodIconModifier(IG_FOOD_HALF_TEXTURE))
            }
        }
        Row(Modifier.weight(1), horizontalArrangement = Arrangement.aligned(Alignment.Left)) {
            val saturation = (component.saturation / 2).toInt()
            if (saturation.absoluteValue > 5) {
                Icon(IG_FOOD_EMPTY_HUNGER_TEXTURE)
                Text("x${saturation}", modifier = Modifier.margin(left = 3f))
            } else {
                repeat(saturation.absoluteValue) {
                    Icon(IG_FOOD_EMPTY_HUNGER_TEXTURE)
                }
            }
        }
    }
    Button(
        Modifier.hoverText(IGLang.edit)
    ) {
        Icon(IconTextures.EDIT)
        click {
            FoodEditor(key.asTranslateText(), component, onValueChange = onValueChange).open()
        }
    }
}

fun FoodEditor(
    title: Text,
    component: FoodProperties,
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    onValueChange: (FoodProperties, Boolean) -> Unit,
): IGScreenImpl {
    val nutrition = component.nutrition.asMutableState
    val saturation = component.saturation.asMutableState
    val canAlwaysEat = component.canAlwaysEat.asMutableState
    return DataComponentEditor(
        title,
        { FoodProperties(nutrition.getValue(), saturation.getValue(), canAlwaysEat.getValue()) to true },
        onValueChange,
        modifier = modifier,
        screenModifier = screenModifier
    ) {
        DialogContent {
            Column(verticalArrangement = Arrangement.spacedBy(5f)) {
                //nutrition
                Row(Modifier.width(240f), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("nutrition")
                    IntEditor(nutrition, 0..Int.MAX_VALUE, modifier = Modifier.width(120f), editorModifier = { Modifier.weight(1) })
                }
                //saturation
                Row(Modifier.width(240f), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("saturation")
                    FloatEditor(saturation, modifier = Modifier.width(120f), editorModifier = { Modifier.weight(1) })
                }
                //can_always_eat
                Row(Modifier.width(240f), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("can_always_eat")
                    SwitchButton(canAlwaysEat)
                }
            }
        }
    }
}
