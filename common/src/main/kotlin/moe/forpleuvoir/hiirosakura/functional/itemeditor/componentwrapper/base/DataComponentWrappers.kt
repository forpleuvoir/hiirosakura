package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base

import it.unimi.dsi.fastutil.ints.IntList
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.*
import moe.forpleuvoir.hiirosakura.util.key
import moe.forpleuvoir.hiirosakura.util.keyOrUnknown
import moe.forpleuvoir.hiirosakura.util.logger
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.nebula.common.api.Initializable
import net.minecraft.core.Holder
import net.minecraft.core.HolderSet
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.component.DataComponents.*
import net.minecraft.resources.Identifier
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.tags.DamageTypeTags
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.food.Foods
import net.minecraft.world.item.*
import net.minecraft.world.item.component.*
import net.minecraft.world.item.consume_effects.ConsumeEffect
import net.minecraft.world.item.enchantment.Enchantable
import net.minecraft.world.item.enchantment.ItemEnchantments
import net.minecraft.world.item.enchantment.Repairable
import net.minecraft.world.item.equipment.Equippable
import net.minecraft.world.level.block.entity.BannerPatternLayers
import java.util.*

fun interface DataComponentWrapper<C> {
    fun ContainerScope.wrapper(
        key: Identifier,
        component: C,
        modifier: Modifier,
        removeAction: () -> Unit,
        onValueChange: (C, Boolean) -> Unit
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

    fun <C : Any> ContainerScope.DataComponentWrapper(
        componentType: DataComponentType<C>,
        component: Any?,
        modifier: Modifier = Modifier,
        removeAction: () -> Unit,
        onValueChange: (C, Boolean) -> Unit
    ) {
        componentWrappers[componentType]?.apply {
            val defaultComponent = defaultValue(componentType)!!
            this@DataComponentWrapper.wrapper(
                componentIds[componentType]!!,
                component ?: defaultComponent,
                modifier,
                removeAction,
                onValueChange as (Any, Boolean) -> Unit
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
        //------------ Int ------------\\
        register(MAX_STACK_SIZE, { 64 }) { key, c, m, rm, consumer ->
            IntComponentWrapper(key, c, 1..Item.ABSOLUTE_MAX_STACK_SIZE, modifier = m, removeAction = rm, onValueChange = consumer)
        }
        register(MAX_DAMAGE, { 233 }) { key, c, m, rm, consumer ->
            IntComponentWrapper(key, c, 1..Int.MAX_VALUE, modifier = m, removeAction = rm, onValueChange = consumer)
        }
        register(DAMAGE, { 0 }) { key, c, m, rm, consumer ->
            IntComponentWrapper(key, c, 0..Int.MAX_VALUE, modifier = m, removeAction = rm, onValueChange = consumer)
        }
        register(REPAIR_COST, { 0 }) { key, c, m, rm, consumer ->
            IntComponentWrapper(key, c, 0..Int.MAX_VALUE, modifier = m, removeAction = rm, onValueChange = consumer)
        }
        register(OMINOUS_BOTTLE_AMPLIFIER, { OminousBottleAmplifier(0) }) { key, c, m, rm, consumer ->
            IntComponentWrapper(key, c.value(), 0..4, modifier = m, removeAction = rm, onValueChange = { value, changed ->
                consumer(OminousBottleAmplifier(value), changed)
            })
        }
        //------------ Float ------------\\
        register(POTION_DURATION_SCALE, { 1f }) { key, c, m, rm, consumer ->
            FloatComponentWrapper(key, c, 0f..Float.MAX_VALUE, modifier = m, removeAction = rm, onValueChange = consumer)
        }
        register(MINIMUM_ATTACK_CHARGE, { 1f }) { key, c, m, rm, consumer ->
            FloatComponentWrapper(key, c, 0f..1f, modifier = m, removeAction = rm, onValueChange = consumer)
        }
        //------------ Boolean ------------\\
        register(ENCHANTMENT_GLINT_OVERRIDE, { true }) { key, c, m, rm, consumer ->
            BooleanComponentWrapper(key, c, modifier = m, removeAction = rm, onValueChange = consumer)
        }
        //------------ Text ------------\\
        register(CUSTOM_NAME, { Literal("custom_name") }) { key, c, m, rm, consumer ->
            TextComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
        }
        register(ITEM_NAME, { Literal("item_name") }) { key, c, m, rm, consumer ->
            TextComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
        }
        //------------ Identifier ------------\\
        register(ITEM_MODEL, { Identifier.parse("minecraft:item_model") }) { key, c, m, rm, consumer ->
            IdentifierComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
        }
        register(TOOLTIP_STYLE, { Identifier.parse("minecraft:tooltip_style") }) { key, c, m, rm, consumer ->
            IdentifierComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
        }
        register(NOTE_BLOCK_SOUND, { Identifier.parse("minecraft:note_block_sound") }) { key, c, m, rm, consumer ->
            IdentifierComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
        }
        //------------ Lore ------------\\
        register(LORE, { ItemLore.EMPTY }) { key, c, m, rm, consumer ->
            LoreComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
        }
        //------------ Enum ------------\\
        register(RARITY, { Rarity.COMMON }) { key, c, m, rm, consumer ->
            EnumComponentWrapper(key, c, modifier = m, removeAction = rm, onValueChange = consumer)
        }
        //------------ ItemEnchantments ------------\\
        register(ENCHANTMENTS, { ItemEnchantments.EMPTY }) { key, c, m, rm, consumer ->
            ItemEnchantmentsComponentWrapper(key, c, modifier = m, removeAction = rm, onValueChange = consumer)
        }
        register(STORED_ENCHANTMENTS, { ItemEnchantments.EMPTY }) { key, c, m, rm, consumer ->
            ItemEnchantmentsComponentWrapper(key, c, modifier = m, removeAction = rm, onValueChange = consumer)
        }
        //------------ EnchantableComponent ------------\\
        register(ENCHANTABLE, { Enchantable(15) }) { key, c, m, rm, consumer ->
            EnchantableComponentWrapper(key, c, modifier = m, removeAction = rm, onValueChange = consumer)
        }
        //------------  Unit ------------\\
        register(UNBREAKABLE, { net.minecraft.util.Unit.INSTANCE }) { key, _, m, rm, _ ->
            UnitComponentWrapper(key, modifier = m, removeAction = rm)
        }
        register(CREATIVE_SLOT_LOCK, { net.minecraft.util.Unit.INSTANCE }) { key, _, m, rm, _ ->
            UnitComponentWrapper(key, modifier = m, removeAction = rm)
        }
        register(INTANGIBLE_PROJECTILE, { net.minecraft.util.Unit.INSTANCE }) { key, _, m, rm, _ ->
            UnitComponentWrapper(key, modifier = m, removeAction = rm)
        }
        register(GLIDER, { net.minecraft.util.Unit.INSTANCE }) { key, _, m, rm, _ ->
            UnitComponentWrapper(key, modifier = m, removeAction = rm)
        }
        //------------ Food ------------\\
        register(FOOD, { Foods.MELON_SLICE }) { key, c, m, rm, consumer ->
            FoodComponentWrapper(key, c, modifier = m, removeAction = rm, onValueChange = consumer)
        }
        //------------ Attribute Modifiers ------------\\
        register(ATTRIBUTE_MODIFIERS, { ItemAttributeModifiers.EMPTY }) { key, c, m, rm, consumer ->
            AttributeModifiersComponentWrapper(key, c, modifier = m, removeAction = rm, onValueChange = consumer)
        }
        //------------ Break Sound ------------\\
        register(BREAK_SOUND, { SoundEvents.ITEM_BREAK }) { key, c, m, rm, consumer ->
            BreakSoundComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
        }
        //------------ Tooltip Display ------------\\
        register(TOOLTIP_DISPLAY, { TooltipDisplay.DEFAULT }) { key, c, m, rm, consumer ->
            TooltipDisplayComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
        }
        //------------ Banner Pattern ------------\\
        register(BANNER_PATTERNS, { BannerPatternLayers.EMPTY }) { key, c, m, rm, consumer ->
            BannerPatternComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
        }
        //------------ DamageResistant ------------\\
        register(DAMAGE_RESISTANT, { DamageResistant(DamageTypeTags.IS_FIRE) }) { key, c, m, rm, consumer ->
            DamageResistantComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
        }
        //------------ Equippable ------------\\
        register(EQUIPPABLE, { Items.SADDLE.components().get(EQUIPPABLE) ?: Equippable.harness(DyeColor.PINK) }) { key, c, m, rm, consumer ->
            EquippableComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
        }
        //------------ Repairable ------------\\
        register(REPAIRABLE, { Repairable(HolderSet.empty()) }) { key, c, m, rm, consumer ->
            RepairableComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
        }
        //------------ Tool ------------\\
        register(TOOL, { Tool(emptyList(), 1f, 1, true) }) { key, c, m, rm, consumer ->
            ToolComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
        }
        //------------ Weapon ------------\\
        register(WEAPON, { Weapon(1) }) { key, c, m, rm, consumer ->
            WeaponComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
        }
        //------------ SwingAnimation ------------\\
        register(SWING_ANIMATION, { SwingAnimation.DEFAULT }) { key, c, m, rm, consumer ->
            SwingAnimationComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
        }
        //------------ UseEffects ------------\\
        register(USE_EFFECTS, { UseEffects.DEFAULT }) { key, c, m, rm, consumer ->
            UseEffectsComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
        }
        //------------ AttackRange ------------\\
        register(ATTACK_RANGE, { AttackRange(0f, 3f, 0f, 5f, 0.3f, 1f) }) { key, c, m, rm, consumer ->
            AttackRangeComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
        }
        //------------ DamageType ------------\\
        register(DAMAGE_TYPE, { EitherHolder(DamageTypes.SPEAR) }) { key, c, m, rm, consumer ->
            DamageTypeComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
        }
        //------------ Consumable ------------\\
        register(
            CONSUMABLE,
            { Consumable(1.6f, ItemUseAnimation.EAT, SoundEvents.GENERIC_EAT, true, ArrayList<ConsumeEffect>()) }
        ) { key, c, m, rm, consumer ->
            ConsumableComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
        }
        //------------ KineticWeapon ------------\\
        register(
            KINETIC_WEAPON, {
                KineticWeapon(
                    10,
                    0,
                    Optional<KineticWeapon.Condition>.ofNullable(null),
                    Optional<KineticWeapon.Condition>.ofNullable(null),
                    Optional<KineticWeapon.Condition>.ofNullable(null),
                    0f,
                    1f,
                    Optional<Holder<SoundEvent>>.ofNullable(SoundEvents.SPEAR_USE),
                    Optional<Holder<SoundEvent>>.ofNullable(SoundEvents.SPEAR_HIT)
                )
            }) { key, c, m, rm, consumer ->
            KineticWeaponComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
        }
        //------------ PiercingWeapon ------------\\
        register(
            PIERCING_WEAPON,
            {
                PiercingWeapon(
                    true,
                    false,
                    Optional<Holder<SoundEvent>>.ofNullable(SoundEvents.SPEAR_USE),
                    Optional<Holder<SoundEvent>>.ofNullable(SoundEvents.SPEAR_HIT)
                )
            }
        ) { key, c, m, rm, consumer ->
            PiercingWeaponComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
        }
        //------------ Fireworks ------------\\
        register(
            FIREWORKS,
            {
                Fireworks(
                    0,
                    mutableListOf()
                )
            }
        ) { key, c, m, rm, consumer ->
            FireworksComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
        }
        //------------ FireworkExplosion ------------\\
        register(
            FIREWORK_EXPLOSION,
            {
                FireworkExplosion(
                    FireworkExplosion.Shape.SMALL_BALL,
                    IntList.of(),
                    IntList.of(),
                    false,
                    false
                )
            }
        ) { key, c, m, rm, consumer ->
            FireworkExplosionComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
        }
        //------------ DyeColor ------------\\
        register(BASE_COLOR, { DyeColor.CYAN }) { key, c, m, rm, consumer ->
            EnumComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
        }
        register(WOLF_COLLAR, { DyeColor.CYAN }) { key, c, m, rm, consumer ->
            EnumComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
        }
        register(TROPICAL_FISH_BASE_COLOR, { DyeColor.CYAN }) { key, c, m, rm, consumer ->
            EnumComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
        }
        register(TROPICAL_FISH_PATTERN_COLOR, { DyeColor.CYAN }) { key, c, m, rm, consumer ->
            EnumComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
        }
        register(CAT_COLLAR, { DyeColor.CYAN }) { key, c, m, rm, consumer ->
            EnumComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
        }
        register(SHEEP_COLOR, { DyeColor.CYAN }) { key, c, m, rm, consumer ->
            EnumComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
        }
        register(SHULKER_COLOR, { DyeColor.CYAN }) { key, c, m, rm, consumer ->
            EnumComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
        }
        //------------  ------------\\
        register(LODESTONE_TRACKER, { LodestoneTracker(Optional.ofNullable(null), true) }) { key, c, m, rm, consumer ->
            LodestoneTrackerComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
        }
//      CONTAINER
    }

}
