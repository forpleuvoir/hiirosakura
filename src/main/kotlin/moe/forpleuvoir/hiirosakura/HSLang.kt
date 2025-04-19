package moe.forpleuvoir.hiirosakura

import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.text.Translatable

object HSLang {

    fun lang(key: String, vararg args: Any): Text = Translatable("${HiiroSakura.MOD_ID}.$key", args = args)

    fun cantBeEmpty(text: Text): Text = lang("cant_be_empty", text)

    val loadFromDisk = lang("load_from_disk")

    fun clickToCopy(text: Text): Text = lang("click_to_copy", text)

    val enable = lang("enable")

    val copySuccess = lang("copy_success")

    val success = lang("success")

    fun blockBreakProtection(supplier: String) = lang("gameplay.block_break_protection.toast", supplier)

    fun itemUseIntercepted(supplier: String) = lang("gameplay.item_use_intercept.toast", supplier)

    fun itemDropIntercepted(supplier: String) = lang("gameplay.item_drop_intercept.toast", supplier)

    fun setConfigSuccess(key: String, value: String) = lang("script.set_config.success", key, value)

    fun setConfigFail(key: String, value: String, message: String?) = lang("script.set_config.fail", key, value, message ?: "unknown")

    fun setConfigFailNotFound(key: String) = lang("script.set_config.not_found", key)

    fun enableEvent(name: String, enabled: Boolean) = lang("event.enable.success", name).append(IGLang.coloredSwitch(enabled))

    fun enableEventNotFound(name: String) = lang("event.enable.not_found", name)

    //------------ CustomData ------------\\

    val customData get() = lang("custom_data")

    val customDataType get() = lang("custom_data.type")

    val customDataKey get() = lang("custom_data.key")

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

    //------------ AutoReplan ------------\\

    val autoReplantMapEntryTargetBlock get() = lang("auto_replant.map_entry.target_block")

    val autoReplantMapEntryReplantItem get() = lang("auto_replant.map_entry.replant_item")

    val autoReplantMapEntryGroundBlock get() = lang("auto_replant.map_entry.ground_block")

    //------------ Matcher.ItemStack ------------\\

    val fromHandItem get() = lang("from_hand_item")
    val itemStackMatcher get() = lang("item_stack_matcher")
    val handledItem get() = lang("item_stack_matcher.handled_item")
    val itemStackMatcherEntryItem get() = lang("item_stack_matcher_entry.item")
    val itemStackMatcherEntryName get() = lang("item_stack_matcher_entry.name")
    val itemStackMatcherEntryScript get() = lang("item_stack_matcher_entry.script")
    val itemStackMatcherEntryCount get() = lang("item_stack_matcher_entry.count")
    val itemStackMatcherEntryRarity get() = lang("item_stack_matcher_entry.rarity")
    val itemStackMatcherEntryEnchantment get() = lang("item_stack_matcher_entry.enchantment")
    val itemStackMatcherEntryTag get() = lang("item_stack_matcher_entry.tag")
    val itemStackMatcherEntryDataComponentType get() = lang("item_stack_matcher_entry.data_component_type")

    //------------ Matcher.BlockInfo ------------\\

    val fromTargetBlock get() = lang("from_target_block")
    val blockInfoMatcher get() = lang("block_info_matcher")
    val targetBlock get() = lang("block_info_matcher.target_block")
    val blockInfoMatcherEntryBlock get() = lang("block_info_matcher_entry.block")
    val blockInfoMatcherEntryScript get() = lang("block_info_matcher_entry.script")
    val blockInfoMatcherEntryPos get() = lang("block_info_matcher_entry.pos")
    val blockInfoMatcherEntryTag get() = lang("block_info_matcher_entry.tag")
    val blockInfoMatcherEntryProperty get() = lang("block_info_matcher_entry.property")
}