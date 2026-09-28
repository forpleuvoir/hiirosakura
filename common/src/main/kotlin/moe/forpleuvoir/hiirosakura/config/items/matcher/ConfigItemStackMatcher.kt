package moe.forpleuvoir.hiirosakura.config.items.matcher

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.itemstack.ItemStackMatcherDisplayerEditor
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.itemstack.ItemStackMatcherEditorDialog
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.itemstack.ItemStackMatcherInfo
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.itemstack.ItemStackMatcherSimpleInfo
import moe.forpleuvoir.ibukigourd.ui.configwrapper.*
import moe.forpleuvoir.ibukigourd.ui.sokitsu.*
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import moe.forpleuvoir.nebula.config.Config
import moe.forpleuvoir.nebula.config.ConfigGroup
import moe.forpleuvoir.nebula.config.config

context(group: ConfigGroup)
fun configItemStackMatcher(name: String, defaultValue: ItemStackMatcher) = config(name, defaultValue, ItemStackMatcher)

//------------ UI Wrapper ------------\\

@Composable
fun ItemStackMatcherConfigWrapper(
    config: Config<ItemStackMatcher>,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceBetween,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically
) = ConfigRowWrapper(config, modifier, horizontalArrangement, verticalAlignment) {
    val value by config.asState()

    ConfigControlBlock{
        ItemStackMatcherDisplayerEditor(
            value = value,
            onValueChange = {config.setValue(it)},
        )
    }
//    ConfigControlBlock {
//        Surface(
//            modifier = modifier.weight(1f)
//                .height(configControlHeight())
//                .tooltip { ItemStackMatcherInfo(value) },
//            contentAlignment = Alignment.Center,
//        ) {
//            Box(Modifier.padding(TextFieldDefaults.contentPadding())) {
//                ItemStackMatcherSimpleInfo(value)
//            }
//        }
//    }
//
//    if (showDialog) {
//        ItemStackMatcherEditorDialog(
//            { showDialog = false },
//            value = value,
//            onValueChange = { config.setValue(it) }
//        )
//    }
}
