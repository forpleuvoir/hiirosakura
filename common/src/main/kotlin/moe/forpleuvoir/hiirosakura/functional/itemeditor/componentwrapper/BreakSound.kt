package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentWrapperRow
import moe.forpleuvoir.hiirosakura.gui.widget.HolderSoundEventSelector
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.width
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.util.state.asMutableState
import net.minecraft.core.Holder
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent

fun ContainerScope.BreakSoundComponentWrapper(
    key: ResourceLocation,
    component: Holder<SoundEvent>,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f, Alignment.Right),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    onValueChange: (Holder<SoundEvent>, Boolean) -> Unit,
) = DataComponentWrapperRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    val valueState = component.asMutableState
    valueState.subscribe { onValueChange(it, false) }
    HolderSoundEventSelector(valueState, modifier = Modifier.width(140f))
}
