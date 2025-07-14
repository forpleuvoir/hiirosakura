package moe.forpleuvoir.hiirosakura

import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.text.Translatable

object HSLang {

    fun lang(key: String, vararg args: Any): Text = Translatable("${HiiroSakura.MOD_ID}.$key", args = args)

    fun cantBeEmpty(text: Text): Text = lang("cant_be_empty", text)

    val loadFromDisk get() = lang("load_from_disk")

    fun clickToCopy(text: Text): Text = lang("click_to_copy", text)

    val name get() = lang("name")

    val enable get() = lang("enable")

    val copySuccess get() = lang("copy_success")

    val success get() = lang("success")

    val message get() = lang("message")

    val send get() = lang("send")

    fun blockBreakProtection(supplier: Any) = lang("gameplay.block_break_protection.toast", supplier)

    fun itemUseIntercepted(supplier: Any) = lang("gameplay.item_use_intercept.toast", supplier)

    fun itemDropIntercepted(supplier: Any) = lang("gameplay.item_drop_intercept.toast", supplier)

    fun setConfigSuccess(key: String, value: String) = lang("script.set_config.success", key, value)

    fun setConfigFail(key: String, value: String, message: String?) = lang("script.set_config.fail", key, value, message ?: "unknown")

    fun setConfigFailNotFound(key: String) = lang("script.set_config.not_found", key)

    fun enableEvent(name: String, enabled: Boolean) = lang("event.enable.success", name).append(IGLang.coloredSwitch(enabled))

    fun enableEventNotFound(name: String) = lang("event.enable.not_found", name)

    //------------ GUI Wrapper ------------\\

    val chatBubblePreviewLock get() = lang("chat_bubble.preview.lock")

    //------------ Sound ------------\\

    val soundEffect get() = lang("sound_effect")

    //------------ CustomData ------------\\

    val customData get() = lang("custom_data")

    val customDataType get() = lang("custom_data.type")

    val customDataKey get() = lang("custom_data.key")

    //------------ Chat ------------\\

    val chatInjectRegex
        get() = lang("chat.inject.regex").style {
            hover(lang("chat.inject.regex.comment"))
        }

    val chatInjectExp
        get() = lang("chat.inject.exp").style {
            hover(lang("chat.inject.exp.comment"))
        }

    val chatFilterExp
        get() = lang("chat.filter.regex").style {
            hover(lang("chat.filter.regex.comment"))
        }

    val chatBubbleServerName
        get() = lang("chat.bubble.server_name").style {
            hover(lang("chat.bubble.server_name.comment"))
        }

    val chatBubbleServerConfig
        get() = lang("chat.bubble.server_config").style {
            hover(lang("chat.bubble.server_config.comment"))
        }

    val chatBubbleServerConfigRegex
        get() = lang("chat.bubble.server_config.regex").style {
            hover(lang("chat.bubble.server_config.regex.comment"))
        }

    val chatBubbleServerConfigEnableUUID
        get() = lang("chat.bubble.server_config.enable_uuid").style {
            hover(lang("chat.bubble.server_config.enable_uuid.comment"))
        }

    val chatBubbleServerConfigEnableProfile
        get() = lang("chat.bubble.server_config.enable_profile").style {
            hover(lang("chat.bubble.server_config.enable_profile.comment"))
        }


    //------------ TaskManager ------------\\

    val taskUnSelected get() = lang("task.un_selected")

    val taskReBindKey get() = lang("task.re_bind_key")

    val taskReBindKeyComment get() = lang("task.re_bind_key.comment")

    val taskExecute get() = lang("task.execute")

    val taskManager get() = lang("task.manager")

    val taskRunning get() = lang("task.running")

    fun taskRunningPeriod(period: Int) = lang("task.running.period", period)

    fun taskRunningRemainingTimes(times: Int) = lang("task.running.remaining_times", times)

    val taskEditor get() = lang("task.editor")

    val taskName get() = lang("task.name")

    val taskDelay get() = lang("task.delay")

    val taskPeriod get() = lang("task.period")

    val taskTimes get() = lang("task.times")

    val taskExecuteOn get() = lang("task.execute_on")

    val taskExecutorType get() = lang("task.executor_type")

    val taskIcon get() = lang("task.icon")

    val taskExecutor get() = lang("task.executor")

    //------------ HSEventManager ------------\\

    val eventSubscriberManager get() = lang("event.subscriber.manager")

    val eventSubscriberEditor get() = lang("event.subscriber.editor")

    val eventSubscriberName get() = lang("event.subscriber.name")

    //------------ AutoReplant ------------\\

    val autoReplantMapEntryTargetBlock get() = lang("auto_replant.map_entry.target_block")
    val autoReplantMapEntryTargetBlockComment get() = lang("auto_replant.map_entry.target_block.comment")

    val autoReplantMapEntryReplantItem get() = lang("auto_replant.map_entry.replant_item")
    val autoReplantMapEntryReplantItemComment get() = lang("auto_replant.map_entry.replant_item.comment")

    val autoReplantMapEntryGroundBlock get() = lang("auto_replant.map_entry.ground_block")
    val autoReplantMapEntryGroundBlockComment get() = lang("auto_replant.map_entry.ground_block.comment")

    //------------ ItemEditor ------------\\

    val itemEditor get() = lang("item_editor")

    //------------ Matcher.ItemStack ------------\\

    val tag get() = lang("tag")
    val itemComponent get() = lang("item_component")
    val getFromHandItem get() = lang("get_from_handheld_item")
    val getFromRegistry get() = lang("get_from_registry")
    val itemStackMatcher get() = lang("item_stack_matcher")
    val handheldItem get() = lang("item_stack_matcher.handheld_item")
    val itemStackMatcherEntryMatcher get() = lang("item_stack_matcher_entry.matcher")
    val itemStackMatcherEntryItem get() = lang("item_stack_matcher_entry.item")
    val itemStackMatcherEntryName get() = lang("item_stack_matcher_entry.name")
    val itemStackMatcherEntryScript get() = lang("item_stack_matcher_entry.script")
    val itemStackMatcherEntryCount get() = lang("item_stack_matcher_entry.count")
    val itemStackMatcherEntryRarity get() = lang("item_stack_matcher_entry.rarity")
    val itemStackMatcherEntryEnchantment get() = lang("item_stack_matcher_entry.enchantment")
    val itemStackMatcherEntryEnchantmentID get() = lang("item_stack_matcher_entry.enchantment.id")
    val itemStackMatcherEntryEnchantmentLevelRange get() = lang("item_stack_matcher_entry.enchantment.level_range")
    val itemStackMatcherEntryTag get() = lang("item_stack_matcher_entry.tag")
    val itemStackMatcherEntryDataComponentType get() = lang("item_stack_matcher_entry.data_component_type")

    //------------ Matcher.BlockInfo ------------\\

    val getFromTargetBlock get() = lang("get_from_target_block")
    val blockProperty get() = lang("block_property")
    val blockInfoMatcher get() = lang("block_info_matcher")
    val targetBlock get() = lang("block_info_matcher.target_block")
    val blockInfoMatcherEntryMatcher get() = lang("block_info_matcher_entry.matcher")
    val blockInfoMatcherEntryBlock get() = lang("block_info_matcher_entry.block")
    val blockInfoMatcherEntryScript get() = lang("block_info_matcher_entry.script")
    val blockInfoMatcherEntryPos get() = lang("block_info_matcher_entry.pos")
    val blockInfoMatcherEntryTag get() = lang("block_info_matcher_entry.tag")
    val blockInfoMatcherEntryProperty get() = lang("block_info_matcher_entry.property")
}