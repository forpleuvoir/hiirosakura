package moe.forpleuvoir.hiirosakura

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

    //------------ Matcher.ItemStack ------------\\

    val itemStackMatcherEntryItem get() = lang("item_stack_matcher_entry.item")
    val itemStackMatcherEntryScript get()= lang("item_stack_matcher_entry.script")
    val itemStackMatcherEntryCount get()= lang("item_stack_matcher_entry.count")
    val itemStackMatcherEntryRarity get()= lang("item_stack_matcher_entry.rarity")
    val itemStackMatcherEntryTag get()= lang("item_stack_matcher_entry.tag")
    val itemStackMatcherEntryDataComponentType get()= lang("item_stack_matcher_entry.data_component_type")
}