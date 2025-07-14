package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import moe.forpleuvoir.hiirosakura.util.id
import moe.forpleuvoir.hiirosakura.util.identifier
import moe.forpleuvoir.hiirosakura.util.logger
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.text.Literal
import net.minecraft.component.ComponentType
import net.minecraft.component.DataComponentTypes.*
import net.minecraft.component.type.ItemEnchantmentsComponent
import net.minecraft.component.type.LoreComponent
import net.minecraft.component.type.UnbreakableComponent
import net.minecraft.util.Identifier
import net.minecraft.util.Rarity

fun interface DataComponentWrapper<C> {
    fun ContainerScope.wrapper(
        id: Identifier,
        component: C,
        modifier: Modifier,
        removeAction: () -> Unit,
        onValueChange: (C, Boolean) -> Unit
    )
}

@Suppress("UNCHECKED_CAST")
object DataComponentWrappers {

    val log = logger()

    private val componentWrappers = mutableMapOf<ComponentType<*>, DataComponentWrapper<Any>>()
    private val componentDefaultValues = mutableMapOf<ComponentType<*>, Any>()
    private val componentIds = mutableMapOf<ComponentType<*>, Identifier>()
    private val supportedComponent = mutableListOf<ComponentType<*>>()

    fun <C> defaultValue(type: ComponentType<C>): C? {
        return componentDefaultValues[type] as? C
    }

    fun <C : Any> register(
        type: ComponentType<C>,
        defaultValue: C,
        id: Identifier = type.id ?: identifier("unknown"),
        wrapper: DataComponentWrapper<C>
    ) {
        componentWrappers[type] = wrapper as DataComponentWrapper<Any>
        componentDefaultValues[type] = defaultValue
        componentIds[type] = id
        supportedComponent.add(type)
    }

    fun <C : Any> ContainerScope.DataComponentWrapper(
        componentType: ComponentType<C>,
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
                componentType.id ?: identifier("unknown_component_type"),
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
        register(MAX_STACK_SIZE, 64) { id, c, m, rm, consumer ->
            IntComponentWrapper(id, c, 1..99, modifier = m, removeAction = rm, onValueChange = consumer)
        }
        register(MAX_DAMAGE, 233) { id, c, m, rm, consumer ->
            IntComponentWrapper(id, c, 1..Int.MAX_VALUE, modifier = m, removeAction = rm, onValueChange = consumer)
        }
        register(DAMAGE, 0) { id, c, m, rm, consumer ->
            IntComponentWrapper(id, c, 0..Int.MAX_VALUE, modifier = m, removeAction = rm, onValueChange = consumer)
        }
        register(REPAIR_COST, 0) { id, c, m, rm, consumer ->
            IntComponentWrapper(id, c, 0..Int.MAX_VALUE, modifier = m, removeAction = rm, onValueChange = consumer)
        }
        //------------ Boolean ------------\\
        register(ENCHANTMENT_GLINT_OVERRIDE, true) { id, c, m, rm, consumer ->
            BooleanComponentWrapper(id, c, modifier = m, removeAction = rm, onValueChange = consumer)
        }
        //------------ Unbreakable ------------\\
        register(UNBREAKABLE, UnbreakableComponent(true)) { id, c, m, rm, consumer ->
            UnbreakableComponentWrapper(id, c, rm, modifier = m, onValueChange = consumer)
        }
        //------------ Text ------------\\
        register(CUSTOM_NAME, Literal("custom_name")) { id, c, m, rm, consumer ->
            TextComponentWrapper(id, c, rm, modifier = m, onValueChange = consumer)
        }
        register(ITEM_NAME, Literal("item_name")) { id, c, m, rm, consumer ->
            TextComponentWrapper(id, c, rm, modifier = m, onValueChange = consumer)
        }
        //------------ Identifier ------------\\
        register(ITEM_MODEL, Identifier.of("minecraft:item_model")) { id, c, m, rm, consumer ->
            IdentifierComponentWrapper(id, c, rm, modifier = m, onValueChange = consumer)
        }
        register(TOOLTIP_STYLE, Identifier.of("minecraft:tooltip_style")) { id, c, m, rm, consumer ->
            IdentifierComponentWrapper(id, c, rm, modifier = m, onValueChange = consumer)
        }
        register(NOTE_BLOCK_SOUND, Identifier.of("minecraft:note_block_sound")) { id, c, m, rm, consumer ->
            IdentifierComponentWrapper(id, c, rm, modifier = m, onValueChange = consumer)
        }
        //------------ Lore ------------\\
        register(LORE, LoreComponent.DEFAULT) { id, c, m, rm, consumer ->
            LoreComponentWrapper(id, c, rm, modifier = m, onValueChange = consumer)
        }
        //------------ Enum ------------\\
        register(RARITY, Rarity.COMMON) { id, c, m, rm, consumer ->
            EnumComponentWrapper(id, c, modifier = m, removeAction = rm, onValueChange = consumer)
        }
        //------------ ItemEnchantments ------------\\
        register(ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT) { id, c, m, rm, consumer ->
            ItemEnchantmentsComponentWrapper(id, c, modifier = m, removeAction = rm, onValueChange = consumer)
        }
        register(STORED_ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT) { id, c, m, rm, consumer ->
            ItemEnchantmentsComponentWrapper(id, c, modifier = m, removeAction = rm, onValueChange = consumer)
        }
        //------------  Unit ------------\\
        register(HIDE_ADDITIONAL_TOOLTIP, net.minecraft.util.Unit.INSTANCE) { id, c, m, rm, consumer ->
            UnitComponentWrapper(id, modifier = m, removeAction = rm)
        }
        register(HIDE_TOOLTIP, net.minecraft.util.Unit.INSTANCE) { id, c, m, rm, consumer ->
            UnitComponentWrapper(id, modifier = m, removeAction = rm)
        }
        register(CREATIVE_SLOT_LOCK, net.minecraft.util.Unit.INSTANCE) { id, c, m, rm, consumer ->
            UnitComponentWrapper(id, modifier = m, removeAction = rm)
        }
        register(INTANGIBLE_PROJECTILE, net.minecraft.util.Unit.INSTANCE) { id, c, m, rm, consumer ->
            UnitComponentWrapper(id, modifier = m, removeAction = rm)
        }
        register(GLIDER, net.minecraft.util.Unit.INSTANCE) { id, c, m, rm, consumer ->
            UnitComponentWrapper(id, modifier = m, removeAction = rm)
        }
    }

}
