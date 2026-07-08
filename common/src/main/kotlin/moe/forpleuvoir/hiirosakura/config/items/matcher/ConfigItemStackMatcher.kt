package moe.forpleuvoir.hiirosakura.config.items.matcher

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.itemstack.ItemStackMatcherDisplayerEditor
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigRowWrapper
import moe.forpleuvoir.ibukigourd.ui.configwrapper.asState
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

    ItemStackMatcherDisplayerEditor(
        value = value,
        onValueChange = { config.setValue(it) },
        modifier = Modifier.size(ConfigRowWrapper.entrySize),
        displayModifier = { Modifier.weight(1f).fillMaxHeight() }
    )
}
