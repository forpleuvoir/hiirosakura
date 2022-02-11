package forpleuvoir.hiirosakura.client.config

import com.google.common.collect.ImmutableList
import com.google.gson.JsonObject
import forpleuvoir.hiirosakura.client.HiiroSakuraClient
import forpleuvoir.hiirosakura.client.config.base.ConfigMultilineText
import forpleuvoir.ibuki_gourd.config.ConfigUtil
import forpleuvoir.ibuki_gourd.config.IConfigBase
import forpleuvoir.ibuki_gourd.config.IConfigHandler
import forpleuvoir.ibuki_gourd.config.options.*
import forpleuvoir.ibuki_gourd.mod.config.WhiteListMode
import forpleuvoir.ibuki_gourd.utils.color.Color4i

/**
 * Mod配置
 *
 *
 * 项目名 hiirosakura
 *
 * 包名 forpleuvoir.hiirosakura.client.config
 *
 * 文件名 Configs
 *
 * 创建时间 2021/6/15 20:04
 *
 * @author forpleuvoir
 *
 */
object Configs : IConfigHandler {
    /**
     * 各种开关
     */
    object Toggles {
        @JvmField
        val CHAT_BUBBLE = ConfigBooleanWithKeyBind(name = translationKey("chatBubble"), defaultValue = false)

        @JvmField
        val SHOW_ENCHANTMENT = ConfigBooleanWithKeyBind(name = translationKey("showEnchantment"), defaultValue = false)

        @JvmField
        val SHOW_TNT_FUSE = ConfigBooleanWithKeyBind(name = translationKey("showTNTFuse"), defaultValue = false)

        @JvmField
        val SHOW_ITEM_ENTITY_NAME =
            ConfigBooleanWithKeyBind(name = translationKey("showItemEntityName"), defaultValue = false)

        @JvmField
        val SHOW_ITEM_ENTITY_ENCHANTMENT =
            ConfigBooleanWithKeyBind(name = translationKey("showItemEntityEnchantment"), defaultValue = false)

        @JvmField
        val SHOW_ITEM_ENTITY_COUNT =
            ConfigBooleanWithKeyBind(name = translationKey("showItemEntityCount"), defaultValue = false)

        @JvmField
        val SHOW_ENTITY_AGE = ConfigBooleanWithKeyBind(name = translationKey("showEntityAge"), defaultValue = false)

        @JvmField
        val AUTO_REBIRTH = ConfigBooleanWithKeyBind(name = translationKey("autoRebirth"), defaultValue = false)

        @JvmField
        val SHOW_TOOLTIP = ConfigBooleanWithKeyBind(name = translationKey("showTooltip"), defaultValue = false)

        @JvmField
        val SHOW_TOOLTIP_ON_ITEM_TOGGLE =
            ConfigBooleanWithKeyBind(name = translationKey("showTooltipOnItemToggle"), defaultValue = false)

        @JvmField
        val SHOW_TOOLTIP_ON_ITEM_ENTITY =
            ConfigBooleanWithKeyBind(name = translationKey("showTooltipOnItemEntity"), defaultValue = false)

        @JvmField
        val CHAT_MESSAGE_INJECT =
            ConfigBooleanWithKeyBind(name = translationKey("chatMessageInject"), defaultValue = false)

        @JvmField
        val ENABLE_CHAT_MESSAGE_INJECT_REGEX =
            ConfigBooleanWithKeyBind(name = translationKey("enableChatMessageInjectRegex"), defaultValue = false)

        @JvmField
        val REVERSE_CHAT_MESSAGE_INJECT_REGEX =
            ConfigBooleanWithKeyBind(name = translationKey("reverseChatMessageInjectRegex"), defaultValue = false)

        @JvmField
        val DISABLE_SCOREBOARD_SIDEBAR_RENDER =
            ConfigBooleanWithKeyBind(name = translationKey("disableScoreboardSidebarRender"), defaultValue = false)

        @JvmField
        val CHAT_MESSAGE_FILTER =
            ConfigBooleanWithKeyBind(name = translationKey("chatMessageFilter"), defaultValue = false)

        @JvmField
        val EXPERIENCE_ORB_ENTITY_VALUE_RENDER =
            ConfigBooleanWithKeyBind(name = translationKey("experienceOrbEntityValueRender"), defaultValue = false)

        @JvmField
        val ENABLE_BLOCK_BRAKE_PROTECTION =
            ConfigBooleanWithKeyBind(name = translationKey("enableBlockBrakeProtection"), defaultValue = false)

        @JvmField
        val ENABLE_ITEM_USE_PROTECTION =
            ConfigBooleanWithKeyBind(name = translationKey("enableItemUseProtection"), defaultValue = false)

        @JvmField
        val ALWAYS_SHOW_BLOCK_MARKER =
            ConfigBooleanWithKeyBind(name = translationKey("alwaysShowBlockMarker"), defaultValue = false)

        @JvmField
        val CONFIGS: ImmutableList<ConfigBase> = ImmutableList.of(
            CHAT_BUBBLE,
            SHOW_ENCHANTMENT,
            SHOW_TNT_FUSE,
            SHOW_ITEM_ENTITY_NAME,
            SHOW_ITEM_ENTITY_ENCHANTMENT,
            SHOW_ITEM_ENTITY_COUNT,
            SHOW_ENTITY_AGE,
            AUTO_REBIRTH,
            SHOW_TOOLTIP,
            SHOW_TOOLTIP_ON_ITEM_TOGGLE,
            SHOW_TOOLTIP_ON_ITEM_ENTITY,
            CHAT_MESSAGE_INJECT,
            ENABLE_CHAT_MESSAGE_INJECT_REGEX,
            REVERSE_CHAT_MESSAGE_INJECT_REGEX,
            DISABLE_SCOREBOARD_SIDEBAR_RENDER,
            CHAT_MESSAGE_FILTER,
            EXPERIENCE_ORB_ENTITY_VALUE_RENDER,
            ENABLE_BLOCK_BRAKE_PROTECTION,
            ENABLE_ITEM_USE_PROTECTION,
            ALWAYS_SHOW_BLOCK_MARKER
        )

        private fun translationKey(key: String): String {
            return translationKey("toggles", key)
        }
    }

    object Values {
        @JvmField
        val ITEM_ENTITY_TEXT_RENDER_DISTANCE =
            ConfigDouble(
                name = translationKey("itemEntityTextRenderDistance"),
                defaultValue = 50.0,
                minValue = 0.0,
                maxValue = 999.0
            )

        @JvmField
        val CHAT_BUBBLE_HEIGHT =
            ConfigDouble(
                name = translationKey("chatBubbleHeight"),
                defaultValue = 0.75,
                minValue = 0.0,
                maxValue = 10.0
            )

        @JvmField
        val CHAT_BUBBLE_TEXTURE_COLOR =
            ConfigColor(name = translationKey("chatBubbleTextureColor"), defaultValue = Color4i(255, 70, 70))

        @JvmField
        val CHAT_BUBBLE_TEXT_COLOR =
            ConfigColor(name = translationKey("chatBubbleTextColor"), defaultValue = Color4i.BLACK)

        @JvmField
        val CHAT_BUBBLE_TEXT_MAX_WIDTH =
            ConfigInt(name = translationKey("chatBubbleTextMaxWidth"), defaultValue = 96, minValue = 9, maxValue = 480)

        @JvmField
        val CHAT_BUBBLE_TIME =
            ConfigInt(name = translationKey("chatBubbleTime"), defaultValue = 200, minValue = 0, maxValue = 9999)

        @JvmField
        val CHAT_BUBBLE_SCALE =
            ConfigDouble(name = translationKey("chatBubbleScale"), defaultValue = 1.0, minValue = 0.1, maxValue = 10.0)

        @JvmField
        val CHAT_BUBBLE_BACKGROUND_Z_OFFSET =
            ConfigDouble(
                name = translationKey("chatBubbleBackgroundZOffset"),
                defaultValue = 0.0,
                minValue = -10.0,
                maxValue = 10.0
            )

        @JvmField
        val CHAT_BUBBLE_VANILLA_SHADER =
            ConfigBoolean(name = translationKey("chatBubbleVanillaShader"), defaultValue = true)

        @JvmField
        val CHAT_BUBBLE_ONLY_Y_ROTATION =
            ConfigBoolean(name = translationKey("chatBubbleOnlyYRotation"), defaultValue = true)

        @JvmField
        val CHAT_BUBBLE_REGEX = ConfigMap(
            name = translationKey("chatBubbleRegex"), defaultValue = mapOf(
                ".*\\.bakamc\\.cn" to "(?<name>(?<=]\\[).*(?=])).*:\\s(?<message>.*)",
            )
        )

        @JvmField
        val CHAT_BUBBLE_CONFIG_GROUP = ConfigGroup(
            name = translationKey("chatBubbleConfigGroup"), defaultValue = listOf(
                CHAT_BUBBLE_HEIGHT,
                CHAT_BUBBLE_TEXTURE_COLOR,
                CHAT_BUBBLE_TEXT_COLOR,
                CHAT_BUBBLE_TEXT_MAX_WIDTH,
                CHAT_BUBBLE_TIME,
                CHAT_BUBBLE_SCALE,
                CHAT_BUBBLE_BACKGROUND_Z_OFFSET,
                CHAT_BUBBLE_VANILLA_SHADER,
                CHAT_BUBBLE_ONLY_Y_ROTATION,
                CHAT_BUBBLE_REGEX
            )
        )

        @JvmField
        val CHAT_MESSAGE_INJECT_EXP =
            ConfigString(name = translationKey("chatMessageInjectExp"), defaultValue = "#{message}")

        @JvmField
        val CHAT_MESSAGE_INJECT_FILTER =
            ConfigStringList(name = translationKey("chatMessageInjectFilter"), defaultValue = ImmutableList.of("^\\/"))

        @JvmField
        val CHAT_MESSAGE_INJECT_GROUP = ConfigGroup(
            name = translationKey("chatMessageInjectGroup"), defaultValue = listOf(
                CHAT_MESSAGE_INJECT_EXP,
                CHAT_MESSAGE_INJECT_FILTER
            )
        )

        @JvmField
        val CHAT_MESSAGE_FILTER_REGEX =
            ConfigStringList(name = translationKey("chatMessageFilterRegex"), defaultValue = ImmutableList.of())

        @JvmField
        val BLOCK_BRAKE_PROTECTION_MODE =
            ConfigOptions(name = translationKey("blockBrakeProtectionMode"), defaultValue = WhiteListMode.None)

        @JvmField
        val BLOCK_BRAKE_PROTECTION_LIST =
            ConfigStringList(name = translationKey("blockBrakeProtectionList"), defaultValue = ImmutableList.of())

        @JvmField
        val ITEM_USE_PROTECTION_MODE =
            ConfigOptions(name = translationKey("itemUseProtectionMode"), defaultValue = WhiteListMode.None)

        @JvmField
        val ITEM_USE_PROTECTION_LIST =
            ConfigStringList(name = translationKey("itemUseProtectionList"), defaultValue = ImmutableList.of())

        @JvmField
        val JS_COMMON_LIB = ConfigMultilineText(name = translationKey("jsCommonLib"), defaultValue = "")


        @JvmField
        val CONFIGS: ImmutableList<ConfigBase> = ImmutableList.of(
            ITEM_ENTITY_TEXT_RENDER_DISTANCE,
            CHAT_BUBBLE_CONFIG_GROUP,
            CHAT_MESSAGE_INJECT_GROUP,
            CHAT_MESSAGE_FILTER_REGEX,
            BLOCK_BRAKE_PROTECTION_MODE,
            BLOCK_BRAKE_PROTECTION_LIST,
            ITEM_USE_PROTECTION_MODE,
            ITEM_USE_PROTECTION_LIST,
            JS_COMMON_LIB
        )

        private fun translationKey(key: String): String {
            return translationKey("values", key)
        }
    }

    override fun allConfig(): List<IConfigBase> {
        return ArrayList<ConfigBase>().apply {
            addAll(Toggles.CONFIGS)
            addAll(Values.CONFIGS)
            addAll(HotKeys.HOTKEY_LIST)
        }
    }

    override fun load() {
        val configFile = ConfigUtil.configFile(HiiroSakuraClient)
        ConfigUtil.paresJsonFile(configFile)?.let {
            if (it is JsonObject) {
                ConfigUtil.readConfigBase(it, "Toggles", Toggles.CONFIGS)
                ConfigUtil.readConfigBase(it, "Values", Values.CONFIGS)
                ConfigUtil.readConfigBase(it, "Hotkeys", HotKeys.HOTKEY_LIST)
            }
        }
    }

    override fun save() {
        val configFile = ConfigUtil.configFile(HiiroSakuraClient)
        val json = JsonObject()
        ConfigUtil.writeConfigBase(json, "Toggles", Toggles.CONFIGS)
        ConfigUtil.writeConfigBase(json, "Values", Values.CONFIGS)
        ConfigUtil.writeConfigBase(json, "Hotkeys", HotKeys.HOTKEY_LIST)
        ConfigUtil.writeJsonToFile(json, configFile)
    }

    @JvmStatic
    fun translationKey(type: String, key: String): String {
        return String.format("%s.config.%s.%s", HiiroSakuraClient.modId, type, key)
    }
}
