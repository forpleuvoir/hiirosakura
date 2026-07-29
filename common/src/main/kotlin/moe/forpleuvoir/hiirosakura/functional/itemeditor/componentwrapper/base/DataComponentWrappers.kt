package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.*
import moe.forpleuvoir.hiirosakura.util.key
import moe.forpleuvoir.hiirosakura.util.keyOrUnknown
import moe.forpleuvoir.hiirosakura.util.logger
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.nebula.common.api.Initializable
import net.minecraft.core.HolderSet
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.component.DataComponents.*
import net.minecraft.resources.Identifier
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.food.Foods
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.component.*
import net.minecraft.world.item.enchantment.Enchantable
import net.minecraft.world.item.enchantment.ItemEnchantments
import net.minecraft.world.item.enchantment.Repairable
import net.minecraft.world.item.equipment.Equippable
import net.minecraft.world.level.block.entity.BannerPatternLayers

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
            IntComponentWrapper(key, c.value(), { onValueChange(OminousBottleAmplifier(it)) }, 1..Int.MAX_VALUE, modifier = m, removeAction = rm)
        }
        register(ENCHANTABLE, { Enchantable(15) }) { key, c, m, rm, onValueChange ->
            IntComponentWrapper(key, c.value, { onValueChange(Enchantable(it)) }, 1..Int.MAX_VALUE, modifier = m, removeAction = rm)
        }
        //endregion
        //region Float
        register(POTION_DURATION_SCALE, { 1f }) { key, c, m, rm, onValueChange ->
            FloatComponentWrapper(key, c, onValueChange, 0f..Float.MAX_VALUE, modifier = m, removeAction = rm)
        }
        register(MINIMUM_ATTACK_CHARGE, { 1f }) { key, c, m, rm, onValueChange ->
            FloatComponentWrapper(key, c, onValueChange, 0f..1f, modifier = m, removeAction = rm)
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
//        //------------ Tool ------------\\
//        register(TOOL, { Tool(emptyList(), 1f, 1, true) }) { key, c, m, rm, consumer ->
//            ToolComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
//        }
//        //------------ Weapon ------------\\
//        register(WEAPON, { Weapon(1) }) { key, c, m, rm, consumer ->
//            WeaponComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
//        }
//        //------------ SwingAnimation ------------\\
//        register(SWING_ANIMATION, { SwingAnimation.DEFAULT }) { key, c, m, rm, consumer ->
//            SwingAnimationComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
//        }
//        //------------ UseEffects ------------\\
//        register(USE_EFFECTS, { UseEffects.DEFAULT }) { key, c, m, rm, consumer ->
//            UseEffectsComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
//        }
//        //------------ AttackRange ------------\\
//        register(ATTACK_RANGE, { AttackRange(0f, 3f, 0f, 5f, 0.3f, 1f) }) { key, c, m, rm, consumer ->
//            AttackRangeComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
//        }
//        //------------ DamageType ------------\\
//        register(DAMAGE_TYPE, { EitherHolder(DamageTypes.SPEAR) }) { key, c, m, rm, consumer ->
//            DamageTypeComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
//        }
//        //------------ Consumable ------------\\
//        register(
//            CONSUMABLE,
//            { Consumable(1.6f, ItemUseAnimation.EAT, SoundEvents.GENERIC_EAT, true, ArrayList<ConsumeEffect>()) }
//        ) { key, c, m, rm, consumer ->
//            ConsumableComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
//        }
//        //------------ KineticWeapon ------------\\
//        register(
//            KINETIC_WEAPON, {
//                KineticWeapon(
//                    10,
//                    0,
//                    Optional<KineticWeapon.Condition>.ofNullable(null),
//                    Optional<KineticWeapon.Condition>.ofNullable(null),
//                    Optional<KineticWeapon.Condition>.ofNullable(null),
//                    0f,
//                    1f,
//                    Optional<Holder<SoundEvent>>.ofNullable(SoundEvents.SPEAR_USE),
//                    Optional<Holder<SoundEvent>>.ofNullable(SoundEvents.SPEAR_HIT)
//                )
//            }) { key, c, m, rm, consumer ->
//            KineticWeaponComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
//        }
//        //------------ PiercingWeapon ------------\\
//        register(
//            PIERCING_WEAPON,
//            {
//                PiercingWeapon(
//                    true,
//                    false,
//                    Optional<Holder<SoundEvent>>.ofNullable(SoundEvents.SPEAR_USE),
//                    Optional<Holder<SoundEvent>>.ofNullable(SoundEvents.SPEAR_HIT)
//                )
//            }
//        ) { key, c, m, rm, consumer ->
//            PiercingWeaponComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
//        }
//        //------------ Fireworks ------------\\
//        register(
//            FIREWORKS,
//            {
//                Fireworks(
//                    0,
//                    mutableListOf()
//                )
//            }
//        ) { key, c, m, rm, consumer ->
//            FireworksComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
//        }
//        //------------ FireworkExplosion ------------\\
//        register(
//            FIREWORK_EXPLOSION,
//            {
//                FireworkExplosion(
//                    FireworkExplosion.Shape.SMALL_BALL,
//                    IntList.of(),
//                    IntList.of(),
//                    false,
//                    false
//                )
//            }
//        ) { key, c, m, rm, consumer ->
//            FireworkExplosionComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
//        }
//        //------------ DyeColor ------------\\
//        register(BASE_COLOR, { DyeColor.CYAN }) { key, c, m, rm, consumer ->
//            EnumComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
//        }
//        register(WOLF_COLLAR, { DyeColor.CYAN }) { key, c, m, rm, consumer ->
//            EnumComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
//        }
//        register(TROPICAL_FISH_BASE_COLOR, { DyeColor.CYAN }) { key, c, m, rm, consumer ->
//            EnumComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
//        }
//        register(TROPICAL_FISH_PATTERN_COLOR, { DyeColor.CYAN }) { key, c, m, rm, consumer ->
//            EnumComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
//        }
//        register(CAT_COLLAR, { DyeColor.CYAN }) { key, c, m, rm, consumer ->
//            EnumComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
//        }
//        register(SHEEP_COLOR, { DyeColor.CYAN }) { key, c, m, rm, consumer ->
//            EnumComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
//        }
//        register(SHULKER_COLOR, { DyeColor.CYAN }) { key, c, m, rm, consumer ->
//            EnumComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
//        }
//        //------------  ------------\\
//        register(LODESTONE_TRACKER, { LodestoneTracker(Optional.ofNullable(null), true) }) { key, c, m, rm, consumer ->
//            LodestoneTrackerComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
//        }
////      CONTAINER
    }

}
