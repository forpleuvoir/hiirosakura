package moe.forpleuvoir.hiirosakura.config.items.matcher

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfoMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfoMatcher.Companion.targetBlockMatcher
import moe.forpleuvoir.hiirosakura.gui.widget.BlockInfoMatcherSimpleInfo
import moe.forpleuvoir.hiirosakura.gui.widget.BlockInfoMatcherTableColumn
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.config.translateTextWithParent
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.height
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.padding
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.width
import moe.forpleuvoir.ibukigourd.gui.base.scope.GuiScope.Companion.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.base.scope.TableLayoutColumnScope
import moe.forpleuvoir.ibukigourd.gui.base.scope.WidgetContainerScope
import moe.forpleuvoir.ibukigourd.gui.configwrapper.*
import moe.forpleuvoir.ibukigourd.gui.widget.button.DeleteButton
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextLabel
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextSetting
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.nebula.config.container.ConfigContainer
import moe.forpleuvoir.nebula.config.item.impl.ConfigStringKeyMap

class ConfigBlockInfoMatcherMap(
    key: String,
    defaultValue: Map<String, BlockInfoMatcher> = emptyMap()
) : ConfigStringKeyMap<BlockInfoMatcher>(
    key,
    defaultValue,
    {
        it.serialization()
    },
    {
        BlockInfoMatcher.deserialization(it)
    }
)

fun ConfigContainer.blockInfoMatcherMap(
    key: String,
    defaultValue: Map<String, BlockInfoMatcher> = emptyMap()
) = addConfig(ConfigBlockInfoMatcherMap(key, defaultValue))

//------------ GUI Wrapper ------------\\

fun WidgetContainerScope.BlockInfoMatcherMapWrapper(
    config: ConfigBlockInfoMatcherMap,
    modifier: Modifier = Modifier,
    buttonModifier: TableLayoutColumnScope.() -> Modifier = { Modifier.width(140f) },
    keyTableName: Text = HSLang.name,
    matcherTableName: Text = HSLang.itemStackMatcher
) = ConfigRowWrapper(config, modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(5f)
    ) {
        TableConfigMapWrappedButton(
            config,
            title = config.translateTextWithParent(1, " -> "),
            newValue = {
                mapEntry("${HSLang.blockInfoMatcher.plainText} ${(it.count())}", targetBlockMatcher)
            },
            hoverTableScope = {
                Header {
                    TextLabel(keyTableName)
                }.Column {
                    TextLabel(it.key, modifier = Modifier.align(Alignment.CenterLeft))
                }
                Header {
                    TextLabel(HSLang.autoReplantMapEntryReplantItem)
                }.Column {
                    BlockInfoMatcherSimpleInfo(it.value, Modifier.height(10f).align(Alignment.CenterLeft))
                }
            },
            dialogModifier = Modifier.padding(20f)
        ) {

            TableConfigMapStringKeyColumn(config, headerText = keyTableName, keyWrapper = { k, v, map ->
                TextLabel(k, modifier = Modifier.width(80f))
            })

            BlockInfoMatcherTableColumn(
                { index, entry, matcher ->
                    config[entry.key] = matcher
                    this@TableConfigMapWrappedButton.executeRecompose()
                },
                { it.value },
                buttonModifier
            ) {
                TextLabel(matcherTableName, setting = TextSetting().copy(horizontalAlignment = Alignment.CenterHorizontally))
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