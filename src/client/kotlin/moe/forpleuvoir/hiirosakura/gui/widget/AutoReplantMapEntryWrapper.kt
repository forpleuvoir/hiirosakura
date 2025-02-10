package moe.forpleuvoir.hiirosakura.gui.widget

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.gameplay.AutoReplant
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.hoverTtp
import moe.forpleuvoir.ibukigourd.gui.base.scope.WidgetContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextLabel
import moe.forpleuvoir.ibukigourd.util.state.stateOf

fun WidgetContainerScope.AutoReplantMapEntryWrapper(
    autoReplantMapEntry: AutoReplant.MapEntry,
    consumer: (AutoReplant.MapEntry) -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Center,
) = Column(
    modifier,
    horizontalArrangement
) {
    Button(
        modifier = Modifier.weight(1).hoverTtp {
            BlockInfoMatcherInfo(stateOf(autoReplantMapEntry.targetBlock))
        },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextLabel(HSLang.autoReplantMapEntryTargetBlock.appendLiteral(" -> "))
        BlockInfoMathcerSimpleInfo(stateOf(autoReplantMapEntry.targetBlock))
        click {
            BlockInfoMatcherBuilder(autoReplantMapEntry.targetBlock, {
                consumer(autoReplantMapEntry.copy(targetBlock = it))
            }).open()
        }
    }

    Button(
        modifier = Modifier.weight(1).hoverTtp {
            ItemStackMathcerInfo(stateOf(autoReplantMapEntry.replantItem))
        },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextLabel(HSLang.autoReplantMapEntryReplantItem.appendLiteral(" -> "))
        ItemStackMathcerSimpleInfo(stateOf(autoReplantMapEntry.replantItem))
        click {
            ItemStackMatcherBuilder(autoReplantMapEntry.replantItem, {
                consumer(autoReplantMapEntry.copy(replantItem = it))
            }).open()
        }
    }

    Button(
        modifier = Modifier.weight(1).hoverTtp {
            BlockInfoMatcherInfo(stateOf(autoReplantMapEntry.groundBlock))
        },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextLabel(HSLang.autoReplantMapEntryGroundBlock.appendLiteral(" -> "))
        BlockInfoMathcerSimpleInfo(stateOf(autoReplantMapEntry.groundBlock))
        click {
            BlockInfoMatcherBuilder(autoReplantMapEntry.groundBlock, {
                consumer(autoReplantMapEntry.copy(groundBlock = it))
            }).open()
        }
    }
}
