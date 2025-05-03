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
import moe.forpleuvoir.ibukigourd.gui.base.scope.GuiScope.Companion.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.base.scope.WidgetContainerScope
import moe.forpleuvoir.ibukigourd.gui.configwrapper.*
import moe.forpleuvoir.ibukigourd.gui.widget.button.DeleteButton
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextLabel
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextSetting
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.nebula.config.container.ConfigContainer
import moe.forpleuvoir.nebula.config.item.impl.ConfigStringKeyMap
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.extensions.checkType
import moe.forpleuvoir.nebula.serialization.extensions.serializeObject

typealias ItemStackBlockInfoPair = Pair<ItemStackMatcher, BlockInfoMatcher>

val ItemStackBlockInfoPair.item get() = first
val ItemStackBlockInfoPair.block get() = second

class ConfigStringItemStackBlockInfoPairMap(
    key: String,
    defaultValue: Map<String, ItemStackBlockInfoPair> = emptyMap()
) : ConfigStringKeyMap<ItemStackBlockInfoPair>(
    key,
    defaultValue,
    { (item, block) ->
        serializeObject {
            "item" to item.serialization()
            "block" to block.serialization()
        }
    }, {
        it.checkType<SerializeObject, ItemStackBlockInfoPair> { obj ->
            ItemStackMatcher.deserialization(obj["item"]!!) to BlockInfoMatcher.deserialization(obj["block"]!!)
        }.getOrThrow()
    }
)

fun ConfigContainer.itemStackBlockInfoMap(
    key: String,
    defaultValue: Map<String, ItemStackBlockInfoPair> = emptyMap()
) = addConfig(ConfigStringItemStackBlockInfoPairMap(key, defaultValue))

//------------ GUI Wrapper ------------\\

fun WidgetContainerScope.ConfigStringItemStackBlockInfoPairMapWrapper(
    config: ConfigStringItemStackBlockInfoPairMap,
    modifier: Modifier = Modifier,
    keyTableName: Text = IGLang.mapKey,
    itemStackTableName: Text = HSLang.itemStackMatcher,
    blockInfoTableName: Text = HSLang.blockInfoMatcher
) = ConfigRowWrapper(config, modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(5f)
    ) {
        TableConfigMapWrappedButton(
            config,
            title = config.translateTextWithParent(1, " -> "),
            newValue = { mapEntry("block to item matcher ${(it.count())}", handItemMatcher to targetBlockMatcher) },
            hoverTableScope = {
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

            },
            dialogModifier = Modifier.padding(20f)
        ) {

            TableConfigMapStringKeyColumn(config, headerText = keyTableName, keyWrapper = { k, v, map ->
                TextLabel(k, modifier = Modifier.width(80f))
            })

            ItemStackMatcherTableColumn(
                { index, entry, matcher ->
                    config[entry.key] = entry.value.copy(first = matcher)
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
                    modifier = Modifier.padding(bottom = 3f)
                )
            }

            BlockInfoMatcherTableColumn(
                { index, entry, matcher ->
                    config[entry.key] = entry.value.copy(second = matcher)
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
                    modifier = Modifier.padding(bottom = 3f)
                )
            }

            Header {
                TextLabel(IGLang.remove)
            }.Column { index, (key, _) ->
                DeleteButton(
                    IGLang.removeConfirm(key),
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
