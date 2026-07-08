package moe.forpleuvoir.hiirosakura.config.items.matcher

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfoMatcher
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.blockinfo.BlockInfoMatcherDisplayerEditor
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigRowWrapper
import moe.forpleuvoir.ibukigourd.ui.configwrapper.asState
import moe.forpleuvoir.nebula.config.Config
import moe.forpleuvoir.nebula.config.ConfigGroup
import moe.forpleuvoir.nebula.config.config

context(group: ConfigGroup)
fun configBlockInfoMatcher(name: String, defaultValue: BlockInfoMatcher) =
    config(name, defaultValue, BlockInfoMatcher)

//------------ UI Wrapper ------------\\

@Composable
fun BlockInfoMatcherConfigWrapper(
    config: Config<BlockInfoMatcher>,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceBetween,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically
) = ConfigRowWrapper(config, modifier, horizontalArrangement, verticalAlignment) {
    val value by config.asState()
    BlockInfoMatcherDisplayerEditor(
        value = value,
        onValueChange = { config.setValue(it) },
        modifier = Modifier.size(ConfigRowWrapper.entrySize),
        displayModifier = { Modifier.weight(1f).fillMaxHeight() }
    )
}
