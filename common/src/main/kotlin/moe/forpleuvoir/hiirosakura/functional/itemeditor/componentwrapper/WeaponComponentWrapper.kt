package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import moe.forpleuvoir.hiirosakura.util.asTranslateText
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.hoverTip
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.margin
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.width
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.scope.GuiScope.Companion.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.widget.DialogContent
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.text.FloatEditor
import moe.forpleuvoir.ibukigourd.gui.widget.text.IntEditor
import moe.forpleuvoir.ibukigourd.gui.widget.text.Text
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.util.state.asMutableState
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.component.Weapon

fun ContainerScope.WeaponComponentWrapper(
    key: ResourceLocation,
    component: Weapon,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f, Alignment.Right),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    onValueChange: (Weapon, Boolean) -> Unit,
) = DataComponentWrapperRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    var weapon = component
    Button(
        Modifier.width(140f).hoverTip {
            Row {
                Column(Modifier.margin(right = 10f), horizontalAlignment = Alignment.Left) {
                    Text("item_damage_per_attack")
                    Text("disable_blocking_for_seconds")
                }
                Column(horizontalAlignment = Alignment.Left) {
                    Text(weapon.itemDamagePerAttack.toString())
                    Text(weapon.disableBlockingForSeconds.toString())
                }
            }
        }
    ) {
        Text(IGLang.edit)
        click {
            WeaponEditor(key.asTranslateText(), weapon, modifier, onValueChange = { it, recompose ->
                if (it != weapon) {
                    weapon = it
                    onValueChange(weapon, recompose)
                    if (recompose) this@DataComponentWrapperRow.executeRecompose()
                }
            }).open()
        }
    }
}

fun WeaponEditor(
    title: Text,
    weapon: Weapon,
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    onValueChange: (Weapon, Boolean) -> Unit,
): IGScreenImpl {
    val itemDamagePerAttack = weapon.itemDamagePerAttack.asMutableState
    val disableBlockingForSeconds = weapon.disableBlockingForSeconds.asMutableState

    return DataComponentEditor(
        title,
        { Weapon(itemDamagePerAttack.getValue(), disableBlockingForSeconds.getValue()) to true },
        onValueChange,
        modifier,
        screenModifier
    ) {
        DialogContent {
            Column(Modifier.width(260f), verticalArrangement = Arrangement.spacedBy(5f)) {
                Row(Modifier.fill(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("item_damage_per_attack")
                    IntEditor(itemDamagePerAttack, 0..Int.MAX_VALUE, modifier = Modifier.width(80f), editorModifier = { Modifier.weight(1) })
                }
                Row(Modifier.fill(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("disable_blocking_for_seconds")
                    FloatEditor(disableBlockingForSeconds, 0f..Float.MAX_VALUE, modifier = Modifier.width(80f), editorModifier = { Modifier.weight(1) })
                }
            }
        }
    }
}
