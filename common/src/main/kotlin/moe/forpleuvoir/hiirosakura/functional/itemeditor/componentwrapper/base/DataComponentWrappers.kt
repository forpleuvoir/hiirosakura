package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.*
import moe.forpleuvoir.hiirosakura.util.key
import moe.forpleuvoir.hiirosakura.util.keyOrUnknown
import moe.forpleuvoir.hiirosakura.util.logger
import moe.forpleuvoir.hiirosakura.util.registryAccess
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.nebula.common.api.Initializable
import net.minecraft.core.HolderSet
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.component.DataComponents.*
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.food.Foods
import net.minecraft.world.item.*
import net.minecraft.world.item.component.*
import net.minecraft.world.item.enchantment.Enchantable
import net.minecraft.world.item.enchantment.ItemEnchantments
import net.minecraft.world.item.enchantment.Repairable
import net.minecraft.world.item.equipment.Equippable
import net.minecraft.world.level.block.entity.BannerPatternLayers
import java.util.*

fun interface DataComponentWrapper<C> {
    @Composable
    fun wrapper(
        key: Identifier,
        component: C,
        modifier: Modifier,
        removeAction: () -> Unit,
        onValueChange: (C) -> Unit
    )
}

@Suppress("UNCHECKED_CAST")
object DataComponentWrappers : Initializable {

    val log = logger()

    private val componentWrappers = mutableMapOf<DataComponentType<*>, DataComponentWrapper<Any>>()
    private val componentDefaultValues = mutableMapOf<DataComponentType<*>, () -> Any>()
    private val componentIds = mutableMapOf<DataComponentType<*>, Identifier>()
    private val adaptedComponent = mutableListOf<DataComponentType<*>>()

    fun isAdaptedComponent(type: DataComponentType<*>): Boolean {
        return adaptedComponent.contains(type)
    }

    fun isAdaptedComponent(key: Identifier): Boolean {
        return adaptedComponent.any { it.key == key }
    }

    fun <C : Any> defaultValue(type: DataComponentType<C>): C? {
        return componentDefaultValues[type]?.invoke() as? C
    }

    fun <C : Any> register(
        type: DataComponentType<C>,
        defaultValue: () -> C,
        key: Identifier = type.keyOrUnknown,
        wrapper: DataComponentWrapper<C>
    ) {
        componentWrappers[type] = wrapper as DataComponentWrapper<Any>
        componentDefaultValues[type] = defaultValue
        componentIds[type] = key
        adaptedComponent.add(type)
    }

    @Composable
    fun <C : Any> DataComponentWrapper(
        componentType: DataComponentType<C>,
        component: Any,
        modifier: Modifier = Modifier,
        removeAction: () -> Unit,
        onValueChange: (C) -> Unit
    ) {
        componentWrappers[componentType]?.apply {
            wrapper(
                componentIds[componentType]!!,
                component,
                modifier,
                removeAction,
                onValueChange as (Any) -> Unit
            )
        } ?: run {
            DefaultComponentWrapper(
                componentType.keyOrUnknown,
                componentType,
                component,
                removeAction,
                modifier,
                onValueChange = onValueChange
            )
        }
    }

    override fun init() {
        //region Int
        register(MAX_STACK_SIZE, { Item.DEFAULT_MAX_STACK_SIZE }) { key, c, m, rm, onValueChange ->
            IntComponentWrapper(key, c, onValueChange, 1..Item.ABSOLUTE_MAX_STACK_SIZE, modifier = m, removeAction = rm)
        }
        register(MAX_DAMAGE, { 233 }) { key, c, m, rm, onValueChange ->
            IntComponentWrapper(key, c, onValueChange, 1..Int.MAX_VALUE, modifier = m, removeAction = rm)
        }
        register(DAMAGE, { 0 }) { key, c, m, rm, onValueChange ->
            IntComponentWrapper(key, c, onValueChange, 1..Int.MAX_VALUE, modifier = m, removeAction = rm)
        }
        register(REPAIR_COST, { 0 }) { key, c, m, rm, onValueChange ->
            IntComponentWrapper(key, c, onValueChange, 1..Int.MAX_VALUE, modifier = m, removeAction = rm)
        }
        register(OMINOUS_BOTTLE_AMPLIFIER, { OminousBottleAmplifier(0) }) { key, c, m, rm, onValueChange ->
            IntComponentWrapper(key, c.value(), { onValueChange(OminousBottleAmplifier(it)) }, 0..4, modifier = m, removeAction = rm)
        }
        register(ENCHANTABLE, { Enchantable(15) }) { key, c, m, rm, onValueChange ->
            IntComponentWrapper(key, c.value, { onValueChange(Enchantable(it)) }, 1..Int.MAX_VALUE, modifier = m, removeAction = rm)
        }
        register(DYED_COLOR, { DyedItemColor(DyedItemColor.LEATHER_COLOR) }) { key, c, m, rm, onValueChange ->
            IntComponentWrapper(key, c.rgb(), { onValueChange(DyedItemColor(it)) }, 0..0xFFFFFF, modifier = m, removeAction = rm)
        }
        register(MAP_COLOR, { MapItemColor.DEFAULT }) { key, c, m, rm, onValueChange ->
            IntComponentWrapper(key, c.rgb(), { onValueChange(MapItemColor(it)) }, 0..0xFFFFFF, modifier = m, removeAction = rm)
        }
        //endregion
        //region Float
        register(POTION_DURATION_SCALE, { 1f }) { key, c, m, rm, onValueChange ->
            FloatComponentWrapper(key, c, onValueChange, 0f..Float.MAX_VALUE, modifier = m, removeAction = rm)
        }
        register(MINIMUM_ATTACK_CHARGE, { 1f }) { key, c, m, rm, onValueChange ->
            FloatComponentWrapper(key, c, onValueChange, 0f..1f, modifier = m, removeAction = rm)
        }
        register(USE_COOLDOWN, { UseCooldown(1.0f) }) { key, c, m, rm, onValueChange ->
            FloatComponentWrapper(key, c.seconds(), { onValueChange(UseCooldown(it)) }, 0f..Float.MAX_VALUE, modifier = m, removeAction = rm)
        }
        //endregion
        //region Boolean
        register(ENCHANTMENT_GLINT_OVERRIDE, { true }) { key, c, m, rm, onValueChange ->
            BooleanComponentWrapper(key, c, onValueChange, modifier = m, removeAction = rm)
        }
        //endregion
        //region Text
        register(CUSTOM_NAME, { Literal("custom_name") }) { key, c, m, rm, onValueChange ->
            TextComponentWrapper(key, c, onValueChange, modifier = m, removeAction = rm)
        }
        register(ITEM_NAME, { Literal("item_name") }) { key, c, m, rm, onValueChange ->
            TextComponentWrapper(key, c, onValueChange, modifier = m, removeAction = rm)
        }
        //endregion
        //region Lore
        register(LORE, { ItemLore.EMPTY }) { key, c, m, rm, onValueChange ->
            ItemLoreComponentWrapper(key, c, onValueChange, modifier = m, removeAction = rm)
        }
        //endregion
        //region Identifier
        register(ITEM_MODEL, { Identifier.parse("minecraft:item_model") }) { key, c, m, rm, onValueChange ->
            IdentifierComponentWrapper(key, c, onValueChange, modifier = m, removeAction = rm)
        }
        register(TOOLTIP_STYLE, { Identifier.parse("minecraft:tooltip_style") }) { key, c, m, rm, onValueChange ->
            IdentifierComponentWrapper(key, c, onValueChange, modifier = m, removeAction = rm)
        }
        register(NOTE_BLOCK_SOUND, { Identifier.parse("minecraft:note_block_sound") }) { key, c, m, rm, onValueChange ->
            IdentifierComponentWrapper(key, c, onValueChange, modifier = m, removeAction = rm)
        }
        //endregion
        //region Enum
        register(RARITY, { Rarity.COMMON }) { key, c, m, rm, onValueChange ->
            EnumComponentWrapper(key, c, onValueChange, modifier = m, removeAction = rm)
        }
        //endregion
        //region ItemEnchantments
        register(ENCHANTMENTS, { ItemEnchantments.EMPTY }) { key, c, m, rm, onValueChange ->
            ItemEnchantmentsComponentWrapper(key, c, onValueChange, modifier = m, removeAction = rm)
        }
        register(STORED_ENCHANTMENTS, { ItemEnchantments.EMPTY }) { key, c, m, rm, onValueChange ->
            ItemEnchantmentsComponentWrapper(key, c, onValueChange, modifier = m, removeAction = rm)
        }
        //endregion
        //region Unit
        register(UNBREAKABLE, { net.minecraft.util.Unit.INSTANCE }) { key, _, m, rm, _ ->
            UnitComponentWrapper(key, rm, modifier = m)
        }
        register(CREATIVE_SLOT_LOCK, { net.minecraft.util.Unit.INSTANCE }) { key, _, m, rm, _ ->
            UnitComponentWrapper(key, rm, modifier = m)
        }
        register(INTANGIBLE_PROJECTILE, { net.minecraft.util.Unit.INSTANCE }) { key, _, m, rm, _ ->
            UnitComponentWrapper(key, rm, modifier = m)
        }
        register(GLIDER, { net.minecraft.util.Unit.INSTANCE }) { key, _, m, rm, _ ->
            UnitComponentWrapper(key, rm, modifier = m)
        }
        //endregion
        //region Food
        register(FOOD, { Foods.MELON_SLICE }) { key, c, m, rm, onValueChange ->
            FoodComponentWrapper(key, c, onValueChange, modifier = m, removeAction = rm)
        }
        //endregion
        //region Attribute Modifiers
        register(ATTRIBUTE_MODIFIERS, { ItemAttributeModifiers.EMPTY }) { key, c, m, rm, onValueChange ->
            AttributeModifiersComponentWrapper(key, c, onValueChange, modifier = m, removeAction = rm)
        }
        //endregion
        //region Break Sound
        register(BREAK_SOUND, { SoundEvents.ITEM_BREAK }) { key, c, m, rm, onValueChange ->
            BreakSoundComponentWrapper(key, c, onValueChange, modifier = m, removeAction = rm)
        }
        //endregion
        //region Tooltip Display
        register(TOOLTIP_DISPLAY, { TooltipDisplay.DEFAULT }) { key, c, m, rm, onValueChange ->
            TooltipDisplayComponentWrapper(key, c, onValueChange, modifier = m, removeAction = rm)
        }
        //endregion
        //region Banner Pattern
        register(BANNER_PATTERNS, { BannerPatternLayers.EMPTY }) { key, c, m, rm, onValueChange ->
            BannerPatternComponentWrapper(key, c, onValueChange, modifier = m, removeAction = rm)
        }
        //endregion
        //region Damage Resistant
        register(DAMAGE_RESISTANT, { DamageResistant(HolderSet.empty()) }) { key, c, m, rm, onValueChange ->
            DamageResistantComponentWrapper(key, c, onValueChange, modifier = m, removeAction = rm)
        }
        //endregion
        //region Equippable
        register(EQUIPPABLE, { Items.SADDLE.components().get(EQUIPPABLE) ?: Equippable.harness(DyeColor.PINK) }) { key, c, m, rm, onValueChange ->
            EquippableComponentWrapper(key, c, onValueChange, modifier = m, removeAction = rm)
        }
        //endregion
        //region Repairable
        register(REPAIRABLE, { Repairable(HolderSet.empty()) }) { key, c, m, rm, onValueChange ->
            RepairableComponentWrapper(key, c, onValueChange, modifier = m, removeAction = rm)
        }
        //endregion
        //region Tool
        register(TOOL, { Tool(emptyList(), 1f, 1, true) }) { key, c, m, rm, onValueChange ->
            ToolComponentWrapper(key, c, onValueChange, modifier = m, removeAction = rm)
        }
        //endregion
        //region Weapon
        register(WEAPON, { Weapon(1) }) { key, c, m, rm, onValueChange ->
            WeaponComponentWrapper(key, c, onValueChange, modifier = m, removeAction = rm)
        }
        //endregion
        //region Swing Animation
        register(SWING_ANIMATION, { SwingAnimation.DEFAULT }) { key, c, m, rm, onValueChange ->
            SwingAnimationComponentWrapper(key, c, onValueChange, modifier = m, removeAction = rm)
        }
        //endregion
        //region Use Effects
        register(USE_EFFECTS, { UseEffects.DEFAULT }) { key, c, m, rm, onValueChange ->
            UseEffectsComponentWrapper(key, c, onValueChange, modifier = m, removeAction = rm)
        }
        //endregion
        //region Attack Range
        register(ATTACK_RANGE, { AttackRange(0f, 3f, 0f, 5f, 0.3f, 1f) }) { key, c, m, rm, onValueChange ->
            AttackRangeComponentWrapper(key, c, onValueChange, modifier = m, removeAction = rm)
        }
        //endregion
        //region Damage Type
        register(DAMAGE_TYPE, { registryAccess!!.lookupOrThrow(Registries.DAMAGE_TYPE).get(DamageTypes.SPEAR).get() }) { key, c, m, rm, onValueChange ->
            DamageTypeComponentWrapper(key, c, onValueChange, modifier = m, removeAction = rm)
        }
        //endregion
        //region Consumable
        register(CONSUMABLE, {
            Consumable(1.6f, ItemUseAnimation.EAT, SoundEvents.GENERIC_EAT, true, ArrayList())
        }) { key, c, m, rm, onValueChange ->
            ConsumableComponentWrapper(key, c, onValueChange, modifier = m, removeAction = rm)
        }
        //endregion
        //region Kinetic Weapon
        register(KINETIC_WEAPON, {
            KineticWeapon(
                10,
                0,
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                0f,
                1f,
                Optional.of(SoundEvents.SPEAR_USE),
                Optional.of(SoundEvents.SPEAR_HIT)
            )
        }) { key, c, m, rm, onValueChange ->
            KineticWeaponComponentWrapper(key, c, onValueChange, modifier = m, removeAction = rm)
        }
        //endregion
        //region Piercing Weapon
        register(PIERCING_WEAPON, {
            PiercingWeapon(
                true,
                false,
                Optional.of(SoundEvents.SPEAR_USE),
                Optional.of(SoundEvents.SPEAR_HIT)
            )
        }) { key, c, m, rm, onValueChange ->
            PiercingWeaponComponentWrapper(key, c, onValueChange, modifier = m, removeAction = rm)
        }
        //endregion
        //region Fireworks
        register(FIREWORKS, { Fireworks(0, mutableListOf()) }) { key, c, m, rm, onValueChange ->
            FireworksComponentWrapper(key, c, onValueChange, modifier = m, removeAction = rm)
        }
        //endregion
        //region FireworkExplosion
        register(FIREWORK_EXPLOSION, { FireworkExplosion.DEFAULT }) { key, c, m, rm, onValueChange ->
            FireworkExplosionComponentWrapper(key, c, onValueChange, modifier = m, removeAction = rm)
        }
        //endregion
        //region DyeColor
        register(DYE, { DyeColor.WHITE }) { key, c, m, rm, onValueChange ->
            EnumComponentWrapper(key, c, onValueChange, modifier = m, removeAction = rm)
        }
        register(BASE_COLOR, { DyeColor.CYAN }) { key, c, m, rm, onValueChange ->
            EnumComponentWrapper(key, c, onValueChange, modifier = m, removeAction = rm)
        }
        register(WOLF_COLLAR, { DyeColor.CYAN }) { key, c, m, rm, onValueChange ->
            EnumComponentWrapper(key, c, onValueChange, modifier = m, removeAction = rm)
        }
        register(TROPICAL_FISH_BASE_COLOR, { DyeColor.CYAN }) { key, c, m, rm, onValueChange ->
            EnumComponentWrapper(key, c, onValueChange, modifier = m, removeAction = rm)
        }
        register(TROPICAL_FISH_PATTERN_COLOR, { DyeColor.CYAN }) { key, c, m, rm, onValueChange ->
            EnumComponentWrapper(key, c, onValueChange, modifier = m, removeAction = rm)
        }
        register(CAT_COLLAR, { DyeColor.CYAN }) { key, c, m, rm, onValueChange ->
            EnumComponentWrapper(key, c, onValueChange, modifier = m, removeAction = rm)
        }
        register(SHEEP_COLOR, { DyeColor.CYAN }) { key, c, m, rm, onValueChange ->
            EnumComponentWrapper(key, c, onValueChange, modifier = m, removeAction = rm)
        }
        register(SHULKER_COLOR, { DyeColor.CYAN }) { key, c, m, rm, onValueChange ->
            EnumComponentWrapper(key, c, onValueChange, modifier = m, removeAction = rm)
        }
        //endregion
        //region Lodestone Tracker
        register(LODESTONE_TRACKER, { LodestoneTracker(Optional.empty(), true) }) { key, c, m, rm, onValueChange ->
            LodestoneTrackerComponentWrapper(key, c, onValueChange, modifier = m, removeAction = rm)
        }
        //endregion
////      CONTAINER
    }

}
