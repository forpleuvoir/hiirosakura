package moe.forpleuvoir.hiirosakura.config.items

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.chataddons.chatbubble.ChatBubbleServerConfig
import moe.forpleuvoir.hiirosakura.functional.misc.ServerMarker
import moe.forpleuvoir.hiirosakura.gui.widget.EditButton
import moe.forpleuvoir.hiirosakura.util.closeScreen
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.config.translateTextWithParent
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.attachLeft
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.*
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.scope.GuiScope.Companion.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.base.scope.TableLayoutColumnScope
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.base.widget.IGWidget
import moe.forpleuvoir.ibukigourd.gui.configwrapper.*
import moe.forpleuvoir.ibukigourd.gui.widget.ConfirmDialog
import moe.forpleuvoir.ibukigourd.gui.widget.Dialog
import moe.forpleuvoir.ibukigourd.gui.widget.DialogContent
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.button.DeleteButton
import moe.forpleuvoir.ibukigourd.gui.widget.button.SwitchButton
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Table
import moe.forpleuvoir.ibukigourd.gui.widget.layout.TableScope
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextArea
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextEditor
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextLabel
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextSetting
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.util.state.asMutableState
import moe.forpleuvoir.ibukigourd.util.state.asState
import moe.forpleuvoir.nebula.config.container.ConfigContainer
import moe.forpleuvoir.nebula.config.item.impl.ConfigStringKeyMap

class ConfigStringChatBubbleServerConfigMap(
    key: String,
    defaultValue: Map<String, ChatBubbleServerConfig> = emptyMap()
) : ConfigStringKeyMap<ChatBubbleServerConfig>(
    key,
    defaultValue,
    ChatBubbleServerConfig::serialization,
    ChatBubbleServerConfig::deserialization
)

fun ConfigContainer.stringChatBubbleServerConfigMap(
    key: String,
    defaultValue: Map<String, ChatBubbleServerConfig> = emptyMap()
) = addConfig(ConfigStringChatBubbleServerConfigMap(key, defaultValue))


//------------ GUI Wrapper ------------\\

fun ContainerScope.ConfigStringChatBubbleServerConfigMapWrapper(
    config: ConfigStringChatBubbleServerConfigMap,
    modifier: Modifier = Modifier,
    keyTableName: Text = HSLang.chatBubbleServerName,
    configTableName: Text = HSLang.chatBubbleServerConfig
) = ConfigRowWrapper(config, modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(5f)
    ) {
        TableConfigMapWrappedButton(
            config,
            title = config.translateTextWithParent(1, " -> "),
            newValue = {
                var name = ServerMarker.name
                if (config.getValue().containsKey(name)) {
                    name = "${name}_${config.getValue().size}"
                }
                mapEntry(name, ChatBubbleServerConfig.DEFAULT_CONFIG)
            },
            hoverTableScope = {},
            hoverContent = {
                Table(
                    config.getValue().entries.toList().subList(0, config.getValue().size.coerceAtMost(9)),
                    rowGap = 10f
                ) {
                    Header {
                        TextLabel(keyTableName, modifier = Modifier.align(Alignment.CenterLeft))
                    }.Column {
                        TextLabel(Literal(it.key), modifier = Modifier.align(Alignment.CenterLeft).minWidth(20f).maxWidth(100f))
                    }

                    Header {
                        TextLabel(configTableName, modifier = Modifier.align(Alignment.CenterLeft))
                    }.Column {
                        TextLabel(
                            Literal(it.value.regex),
                            modifier = Modifier.align(Alignment.CenterLeft).minWidth(20f).maxWidth(220f)
                        )
                    }
                }
            },
            dialogModifier = Modifier.padding(20F)
        ) {
            TableConfigMapStringKeyColumn(
                config,
                header = {
                    TextLabel(
                        keyTableName,
                        setting = TextSetting(horizontalAlignment = Alignment.CenterHorizontally),
                        modifier = Modifier.padding(bottom = 3f).minWidth(35f)
                    )
                },
                keyWrapper = { k, v, map ->
                    TextLabel(Literal(k), modifier = Modifier.maxWidth(35f))
                },
                modifier = { Modifier.minWidth(80f).hoverText(IGLang.edit.appendLiteral(" ").append(keyTableName)) }
            )

            ChatBubbleServerConfigTableColumn(
                { index, entry, cc ->
                    config[entry.key] = cc
                    this@TableConfigMapWrappedButton.executeRecompose()
                },
                { it.value },
                buttonModifier = {
                    Modifier.width(240f)
                },
            ) {
                TextLabel(
                    HSLang.chatBubbleServerConfig,
                    setting = TextSetting(horizontalAlignment = Alignment.CenterHorizontally),
                    modifier = Modifier.minWidth(240f)
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

fun <T> TableScope<T>.ChatBubbleServerConfigTableColumn(
    consumer: (Int, T, ChatBubbleServerConfig) -> Unit,
    extractor: (T) -> ChatBubbleServerConfig,
    buttonModifier: TableLayoutColumnScope.() -> Modifier = { Modifier },
    weight: Int = 0,
    header: TableLayoutColumnScope.() -> IGWidget,
) = Header(weight, header).Column { index, entry ->
    val config = extractor(entry)
    ChatBubbleServerConfigWrapper(config, buttonModifier()) {
        consumer(index, entry, it)
    }
}


fun ContainerScope.ChatBubbleServerConfigWrapper(
    config: ChatBubbleServerConfig,
    modifier: Modifier = Modifier,
    consumer: (ChatBubbleServerConfig) -> Unit
) = Button(
    modifier.attachLeft {
        hoverTip {
            Row(horizontalArrangement = Arrangement.spacedBy(5f)) {
                Column(Modifier, verticalArrangement = Arrangement.spacedBy(2f), horizontalAlignment = Alignment.Left) {
                    TextLabel(HSLang.chatBubbleServerConfigRegex)
                    TextLabel(HSLang.chatBubbleServerConfigEnableUUID)
                    TextLabel(HSLang.chatBubbleServerConfigEnableProfile)
                }
                Column(Modifier, verticalArrangement = Arrangement.spacedBy(2f), horizontalAlignment = Alignment.Left) {
                    TextLabel(config.regex)
                    TextLabel(IGLang.coloredSwitch(config.enableUUID))
                    TextLabel(IGLang.coloredSwitch(config.enableProfile))
                }
            }
        }
    }
) {
    TextLabel(
        config.regex,
        modifier = modifier.maxWidth(80f)
    )
    click {
        val regex = config.regex.asMutableState
        val uuid = config.enableUUID.asMutableState
        val profile = config.enableProfile.asMutableState
        ConfirmDialog(
            HSLang.chatBubbleServerConfig.asState,
            onConfirm = {
                consumer(ChatBubbleServerConfig(regex.getValue(), uuid.getValue(), profile.getValue()))
                closeScreen()
            }
        ) {
            DialogContent {
                Column(Modifier.width(260f), verticalArrangement = Arrangement.spacedBy(5f)) {
                    Row(Modifier.fill(), horizontalArrangement = Arrangement.SpaceBetween) {
                        TextLabel(HSLang.chatBubbleServerConfigRegex)
                        Row {
                            TextEditor { bindState(regex) }
                            EditButton {
                                Dialog(Modifier.maxWidth(330f).maxHeight(185f)) {
                                    TextLabel(HSLang.chatBubbleServerConfigRegex)
                                    TextArea(Modifier.fill().weight(1)) {
                                        bindState(regex)
                                    }
                                }.open()
                            }
                        }
                    }

                    Row(Modifier.fill(), horizontalArrangement = Arrangement.SpaceBetween) {
                        TextLabel(HSLang.chatBubbleServerConfigEnableUUID)
                        SwitchButton(uuid)
                    }

                    Row(Modifier.fill(), horizontalArrangement = Arrangement.SpaceBetween) {
                        TextLabel(HSLang.chatBubbleServerConfigEnableProfile)
                        SwitchButton(profile)
                    }
                }
            }
        }.open()
    }
}