package moe.forpleuvoir.hiirosakura.gui.widget

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.gameplay.AutoReplant
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.hoverTip
import moe.forpleuvoir.ibukigourd.gui.base.scope.WidgetContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextLabel

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
        modifier = Modifier.weight(1).hoverTip {
            BlockInfoMatcherInfo(autoReplantMapEntry.targetBlock)
        },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextLabel(HSLang.autoReplantMapEntryTargetBlock.appendLiteral(" -> "))
        BlockInfoMatcherSimpleInfo(autoReplantMapEntry.targetBlock)
        click {
            BlockInfoMatcherBuilder(autoReplantMapEntry.targetBlock, {
                consumer(autoReplantMapEntry.copy(targetBlock = it))
            }).open()
        }
    }

    Button(
        modifier = Modifier.weight(1).hoverTip {
            ItemStackMatcherInfo(autoReplantMapEntry.replantItem)
        },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextLabel(HSLang.autoReplantMapEntryReplantItem.appendLiteral(" -> "))
        ItemStackMatcherSimpleInfo(autoReplantMapEntry.replantItem)
        click {
            ItemStackMatcherBuilder(autoReplantMapEntry.replantItem, {
                consumer(autoReplantMapEntry.copy(replantItem = it))
            }).open()
        }
    }

    Button(
        modifier = Modifier.weight(1).hoverTip {
            BlockInfoMatcherInfo(autoReplantMapEntry.groundBlock)
        },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextLabel(HSLang.autoReplantMapEntryGroundBlock.appendLiteral(" -> "))
        BlockInfoMatcherSimpleInfo(autoReplantMapEntry.groundBlock)
        click {
            BlockInfoMatcherBuilder(autoReplantMapEntry.groundBlock, {
                consumer(autoReplantMapEntry.copy(groundBlock = it))
            }).open()
        }
    }
}


fun WidgetContainerScope.AutoReplantMapEntryInfo(
    autoReplantMapEntry: AutoReplant.MapEntry,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Center,
) = Column(
    modifier,
    horizontalArrangement
) {
    Column(
        modifier = Modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextLabel(HSLang.autoReplantMapEntryTargetBlock.appendLiteral(" -> "))
        BlockInfoMatcherSimpleInfo(autoReplantMapEntry.targetBlock)
    }

    Column(
        modifier = Modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextLabel(HSLang.autoReplantMapEntryReplantItem.appendLiteral(" -> "))
        ItemStackMatcherSimpleInfo(autoReplantMapEntry.replantItem)
    }

    Column(
        modifier = Modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextLabel(HSLang.autoReplantMapEntryGroundBlock.appendLiteral(" -> "))
        BlockInfoMatcherSimpleInfo(autoReplantMapEntry.groundBlock)
    }
}