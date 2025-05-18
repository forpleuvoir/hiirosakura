package moe.forpleuvoir.hiirosakura.config.items.matcher

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfoMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfoMatcher.Companion.targetBlockMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher.Companion.handItemMatcher
import moe.forpleuvoir.hiirosakura.gui.widget.BlockInfoMatcherSimpleInfo
import moe.forpleuvoir.hiirosakura.gui.widget.BlockInfoMatcherTableColumn
import moe.forpleuvoir.hiirosakura.gui.widget.ItemStackMatcherSimpleInfo
import moe.forpleuvoir.hiirosakura.gui.widget.ItemStackMatcherTableColumn
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.config.translateTextWithParent
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.*
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.scope.GuiScope.Companion.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.configwrapper.*
import moe.forpleuvoir.ibukigourd.gui.widget.button.DeleteButton
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Table
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextLabel
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextSetting
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.nebula.config.container.ConfigContainer
import moe.forpleuvoir.nebula.config.item.impl.ConfigStringKeyMap
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.extensions.checkType
import moe.forpleuvoir.nebula.serialization.extensions.serializeObject

typealias BlockInfoItemStackPair = Pair<BlockInfoMatcher, ItemStackMatcher>

val BlockInfoItemStackPair.block get() = first
val BlockInfoItemStackPair.item get() = second

class ConfigStringBlockInfoItemStackPairMap(
    key: String,
    defaultValue: Map<String, BlockInfoItemStackPair> = emptyMap()
) : ConfigStringKeyMap<BlockInfoItemStackPair>(
    key,
    defaultValue,
    { (block, item) ->
        serializeObject {
            "block" to block.serialization()
            "item" to item.serialization()
        }
    }, {
        it.checkType<SerializeObject, BlockInfoItemStackPair> { obj ->
            BlockInfoMatcher.deserialization(obj["block"]!!) to ItemStackMatcher.deserialization(obj["item"]!!)
        }.getOrThrow()
    }
)

fun ConfigContainer.blockInfoItemStackMap(
    key: String,
    defaultValue: Map<String, BlockInfoItemStackPair> = emptyMap()
) = addConfig(ConfigStringBlockInfoItemStackPairMap(key, defaultValue))

fun ContainerScope.ConfigStringBlockInfoItemStackPairMapWrapper(
    config: ConfigStringBlockInfoItemStackPairMap,
    modifier: Modifier = Modifier,
    keyTableName: Text = IGLang.mapKey,
    blockInfoTableName: Text = HSLang.blockInfoMatcher,
    itemStackTableName: Text = HSLang.itemStackMatcher,
) = ConfigRowWrapper(config, modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(5f)
    ) {
        TableConfigMapWrappedButton(
            config,
            title = config.translateTextWithParent(1, " -> "),
            newValue = { mapEntry("block to item matcher ${(it.count())}", targetBlockMatcher to handItemMatcher) },
            hoverTableScope = {},
            hoverContent = {
                Table(
                    config.getValue().entries.toList().subList(0, config.getValue().size.coerceAtMost(9)),
                    rowGap = 10f
                ) {
                    Header {
                        TextLabel(keyTableName, modifier = Modifier.align(Alignment.CenterLeft))
                    }.Column {
                        TextLabel(it.key.take(60), modifier = Modifier.align(Alignment.CenterLeft).minWidth(20f).maxWidth(100f))
                    }

                    Header {
                        TextLabel(HSLang.targetBlock)
                    }.Column {
                        BlockInfoMatcherSimpleInfo(it.value.block, Modifier.height(10f).align(Alignment.CenterLeft))
                    }

                    Header {
                        TextLabel(HSLang.handledItem)
                    }.Column {
                        ItemStackMatcherSimpleInfo(it.value.item, Modifier.height(10f).align(Alignment.CenterLeft))
                    }
                }
            },
            dialogModifier = Modifier.padding(20f)
        ) {

            TableConfigMapStringKeyColumn(
                config,
                header = {
                    TextLabel(
                        keyTableName,
                        setting = TextSetting().copy(horizontalAlignment = Alignment.CenterHorizontally),
                        modifier = Modifier.padding(bottom = 3f).minWidth(80f)
                    )
                }, keyWrapper = { k, v, map ->
                    TextLabel(k, modifier = Modifier.width(120f))
                }
            )

            BlockInfoMatcherTableColumn(
                { index, entry, matcher ->
                    config.getValue()[entry.key] = entry.value.copy(first = matcher)
                    this@TableConfigMapWrappedButton.executeRecompose()
                },
                { it.value.block },
                buttonModifier = {
                    Modifier.width(120f)
                }
            ) {
                TextLabel(
                    blockInfoTableName,
                    setting = TextSetting().copy(horizontalAlignment = Alignment.CenterHorizontally),
                    modifier = Modifier.minWidth(120f)
                )
            }

            ItemStackMatcherTableColumn(
                { index, entry, matcher ->
                    config.getValue()[entry.key] = entry.value.copy(second = matcher)
                    this@TableConfigMapWrappedButton.executeRecompose()
                },
                { it.value.item },
                buttonModifier = {
                    Modifier.width(120f)
                }
            ) {
                TextLabel(
                    itemStackTableName,
                    setting = TextSetting().copy(horizontalAlignment = Alignment.CenterHorizontally),
                    modifier = Modifier.minWidth(120f)
                )
            }

            Header {
                TextLabel(IGLang.remove)
            }.Column { index, (key, _) ->
                DeleteButton(
                    { IGLang.removeConfirm(key) },
                    { this@TableConfigMapWrappedButton.executeRecompose() }
                ) {
                    config.remove(key)
                }
            }

        }

        ConfigResetButton(config) {
            this@Row.executeRecompose()
        }
    }
}
