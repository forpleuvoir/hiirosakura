package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import moe.forpleuvoir.hiirosakura.util.key
import moe.forpleuvoir.hiirosakura.util.logger
import moe.forpleuvoir.hiirosakura.util.resourceLocation
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.text.Literal
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.component.DataComponents.*
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.food.Foods
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.component.ItemAttributeModifiers
import net.minecraft.world.item.component.ItemLore
import net.minecraft.world.item.enchantment.Enchantable
import net.minecraft.world.item.enchantment.ItemEnchantments

fun interface DataComponentWrapper<C> {
    fun ContainerScope.wrapper(
        key: ResourceLocation,
        component: C,
        modifier: Modifier,
        removeAction: () -> Unit,
        onValueChange: (C, Boolean) -> Unit
    )
}

@Suppress("UNCHECKED_CAST")
object DataComponentWrappers {

    val log = logger()

    private val componentWrappers = mutableMapOf<DataComponentType<*>, DataComponentWrapper<Any>>()
    private val componentDefaultValues = mutableMapOf<DataComponentType<*>, Any>()
    private val componentIds = mutableMapOf<DataComponentType<*>, ResourceLocation>()
    private val adaptedComponent = mutableListOf<DataComponentType<*>>()

    fun isAdaptedComponent(type: DataComponentType<*>): Boolean {
        return adaptedComponent.contains(type)
    }

    fun isAdaptedComponent(key: ResourceLocation): Boolean {
        return adaptedComponent.any { it.key == key }
    }

    fun <C> defaultValue(type: DataComponentType<C>): C? {
        return componentDefaultValues[type] as? C
    }

    fun <C : Any> register(
        type: DataComponentType<C>,
        defaultValue: C,
        key: ResourceLocation = type.key ?: resourceLocation("unknown_component_type"),
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
            val defaultComponent = componentDefaultValues[componentType]!! as C
            this@DataComponentWrapper.wrapper(
                componentIds[componentType]!!,
                component ?: defaultComponent,
                modifier,
                removeAction,
                onValueChange as (Any, Boolean) -> Unit
            )
        } ?: run {
            DefaultComponentWrapper(
                componentType.key ?: resourceLocation("unknown_component_type"),
                componentType,
                component,
                removeAction,
                modifier,
                onValueChange = onValueChange
            )
        }
    }

    init {
        //------------ Int ------------\\
        register(MAX_STACK_SIZE, 64) { key, c, m, rm, consumer ->
            IntComponentWrapper(key, c, 1..99, modifier = m, removeAction = rm, onValueChange = consumer)
        }
        register(MAX_DAMAGE, 233) { key, c, m, rm, consumer ->
            IntComponentWrapper(key, c, 1..Int.MAX_VALUE, modifier = m, removeAction = rm, onValueChange = consumer)
        }
        register(DAMAGE, 0) { key, c, m, rm, consumer ->
            IntComponentWrapper(key, c, 0..Int.MAX_VALUE, modifier = m, removeAction = rm, onValueChange = consumer)
        }
        register(REPAIR_COST, 0) { key, c, m, rm, consumer ->
            IntComponentWrapper(key, c, 0..Int.MAX_VALUE, modifier = m, removeAction = rm, onValueChange = consumer)
        }
        //------------ Boolean ------------\\
        register(ENCHANTMENT_GLINT_OVERRIDE, true) { key, c, m, rm, consumer ->
            BooleanComponentWrapper(key, c, modifier = m, removeAction = rm, onValueChange = consumer)
        }
        //------------ Text ------------\\
        register(CUSTOM_NAME, Literal("custom_name")) { key, c, m, rm, consumer ->
            TextComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
        }
        register(ITEM_NAME, Literal("item_name")) { key, c, m, rm, consumer ->
            TextComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
        }
        //------------ Identifier ------------\\
        register(ITEM_MODEL, ResourceLocation.parse("minecraft:item_model")) { key, c, m, rm, consumer ->
            IdentifierComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
        }
        register(TOOLTIP_STYLE, ResourceLocation.parse("minecraft:tooltip_style")) { key, c, m, rm, consumer ->
            IdentifierComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
        }
        register(NOTE_BLOCK_SOUND, ResourceLocation.parse("minecraft:note_block_sound")) { key, c, m, rm, consumer ->
            IdentifierComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
        }
        //------------ Lore ------------\\
        register(LORE, ItemLore.EMPTY) { key, c, m, rm, consumer ->
            LoreComponentWrapper(key, c, rm, modifier = m, onValueChange = consumer)
        }
        //------------ Enum ------------\\
        register(RARITY, Rarity.COMMON) { key, c, m, rm, consumer ->
            EnumComponentWrapper(key, c, modifier = m, removeAction = rm, onValueChange = consumer)
        }
        //------------ ItemEnchantments ------------\\
        register(ENCHANTMENTS, ItemEnchantments.EMPTY) { key, c, m, rm, consumer ->
            ItemEnchantmentsComponentWrapper(key, c, modifier = m, removeAction = rm, onValueChange = consumer)
        }
        register(STORED_ENCHANTMENTS, ItemEnchantments.EMPTY) { key, c, m, rm, consumer ->
            ItemEnchantmentsComponentWrapper(key, c, modifier = m, removeAction = rm, onValueChange = consumer)
        }
        //------------ EnchantableComponent ------------\\
        register(ENCHANTABLE, Enchantable(15)) { key, c, m, rm, consumer ->
            EnchantableComponentWrapper(key, c, modifier = m, removeAction = rm, onValueChange = consumer)
        }
        //------------  Unit ------------\\
        register(UNBREAKABLE,net.minecraft.util.Unit.INSTANCE) {  key, _, m, rm, _ ->
            UnitComponentWrapper(key, modifier = m, removeAction = rm)
        }
        register(CREATIVE_SLOT_LOCK, net.minecraft.util.Unit.INSTANCE) { key, _, m, rm, _ ->
            UnitComponentWrapper(key, modifier = m, removeAction = rm)
        }
        register(INTANGIBLE_PROJECTILE, net.minecraft.util.Unit.INSTANCE) { key, _, m, rm, _ ->
            UnitComponentWrapper(key, modifier = m, removeAction = rm)
        }
        register(GLIDER, net.minecraft.util.Unit.INSTANCE) { key, _, m, rm, _ ->
            UnitComponentWrapper(key, modifier = m, removeAction = rm)
        }
        //------------ Food ------------\\
        register(FOOD, Foods.MELON_SLICE) { key, c, m, rm, consumer ->
            FoodComponentWrapper(key, c, modifier = m, removeAction = rm, onValueChange = consumer)
        }
        //------------ Attribute Modifiers ------------\\
        register(ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY) { key, c, m, rm, consumer ->
            AttributeModifiersComponentWrapper(key, c, modifier = m, removeAction = rm, onValueChange = consumer)
        }
    }

}
