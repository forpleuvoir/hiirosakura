package moe.forpleuvoir.hiirosakura.config.items.matcher

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher.Companion.handheldItemMatcher
import moe.forpleuvoir.hiirosakura.gui.widget.ItemStackMatcherSimpleInfo
import moe.forpleuvoir.hiirosakura.gui.widget.ItemStackMatcherTableColumn
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.config.translateTextWithParent
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.height
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.minWidth
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.padding
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.width
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.scope.GuiScope.Companion.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.base.scope.TableLayoutColumnScope
import moe.forpleuvoir.ibukigourd.gui.configwrapper.*
import moe.forpleuvoir.ibukigourd.gui.widget.button.DeleteButton
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextLabel
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextSetting
import moe.forpleuvoir.ibukigourd.text.InlineStyleText
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.nebula.config.container.ConfigContainer
import moe.forpleuvoir.nebula.config.item.impl.ConfigStringKeyMap

class ConfigItemStackMatcherMap(
    key: String,
    defaultValue: Map<String, ItemStackMatcher> = emptyMap()
) : ConfigStringKeyMap<ItemStackMatcher>(
    key,
    defaultValue,
    {
        it.serialization()
    },
    {
        ItemStackMatcher.deserialization(it)
    }
)

fun ConfigContainer.itemStackMatcherMap(
    key: String,
    defaultValue: Map<String, ItemStackMatcher> = emptyMap()
) = addConfig(ConfigItemStackMatcherMap(key, defaultValue))


fun ContainerScope.ItemStackMatcherMapWrapper(
    config: ConfigItemStackMatcherMap,
    modifier: Modifier = Modifier,
    buttonModifier: TableLayoutColumnScope.() -> Modifier = { Modifier.width(140f) },
    keyTableName: Text = IGLang.mapKey,
    matcherTableName: Text = HSLang.itemStackMatcher
) = ConfigRowWrapper(config, modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(5f)
    ) {
        TableConfigMapWrappedButton(
            config,
            title = config.translateTextWithParent(1, " -> "),
            newValue = {
                mapEntry("${HSLang.itemStackMatcher.plainText} ${(it.count())}", handheldItemMatcher)
            },
            hoverTableScope = {
                Header {
                    TextLabel(keyTableName)
                }.Column {
                    TextLabel(InlineStyleText(it.key), modifier = Modifier.align(Alignment.CenterLeft))
                }
                Header {
                    TextLabel(matcherTableName)
                }.Column {
                    ItemStackMatcherSimpleInfo(it.value, Modifier.height(10f).align(Alignment.CenterLeft))
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
                    TextLabel(InlineStyleText(k), modifier = Modifier.width(120f))
                }
            )

            ItemStackMatcherTableColumn(
                { index, entry, matcher ->
                    config[entry.key] = matcher
                    this@TableConfigMapWrappedButton.executeRecompose()
                },
                { it.value },
                buttonModifier
            ) {
                TextLabel(matcherTableName, setting = TextSetting().copy(horizontalAlignment = Alignment.CenterHorizontally), modifier = Modifier.minWidth(120f))
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