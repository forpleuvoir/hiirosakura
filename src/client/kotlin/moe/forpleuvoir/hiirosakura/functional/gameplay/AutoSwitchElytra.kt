package moe.forpleuvoir.hiirosakura.functional.gameplay

import moe.forpleuvoir.hiirosakura.config.items.itemStackMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatchEntry
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.MultiMatcher
import moe.forpleuvoir.hiirosakura.util.id
import moe.forpleuvoir.hiirosakura.util.swapSlotWithHotbar
import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBindBoolean
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.config.item.impl.enum
import moe.forpleuvoir.nebula.config.item.impl.stringList
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.component.DataComponentTypes
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.Items
import net.minecraft.util.Hand
import net.minecraft.util.Unit

object AutoSwitchElytra {

    object Config : ModConfigContainer("auto_switch_elytra") {

        val enable by keyBindBoolean("enable", false)

        val slot: EquipmentSlot by enum("slot", EquipmentSlot.CHEST)

        val switchableEquip by itemStackMatcher(
            "switchable_equip",
            ItemStackMatcher(
                MultiMatcher.MatchMode.AnyMatch,
                ItemStackMatchEntry.Item(Items.NETHERITE_CHESTPLATE),
                ItemStackMatchEntry.Item(Items.DIAMOND_CHESTPLATE),
                ItemStackMatchEntry.Item(Items.CHAINMAIL_CHESTPLATE),
                ItemStackMatchEntry.Item(Items.IRON_CHESTPLATE),
                ItemStackMatchEntry.Item(Items.LEATHER_CHESTPLATE),
                ItemStackMatchEntry.Item(Items.GOLDEN_CHESTPLATE),
            )
        )

        val switchableGlider by itemStackMatcher(
            "switchable_glider",
            ItemStackMatcher(
                MultiMatcher.MatchMode.AnyMatch,
                ItemStackMatchEntry.Item(Items.ELYTRA)
            )
        )

    }

    @JvmStatic
    fun trySwitchElytra(player: ClientPlayerEntity) {
        if (!Config.enable.value) return
        val interaction = mc.interactionManager!!

        val offHand = player.offHandStack
        if (
             Config.switchableGlider.match(offHand)
            && offHand.get(DataComponentTypes.GLIDER) == Unit.INSTANCE
            && offHand.get(DataComponentTypes.EQUIPPABLE)?.slot == Config.slot
        ) {
            interaction.interactItem(player, Hand.OFF_HAND)
        }

        val index = player.swapSlotWithHotbar {
            Config.switchableGlider.match(it)
            && it.get(DataComponentTypes.GLIDER) == Unit.INSTANCE
            && it.get(DataComponentTypes.EQUIPPABLE)?.slot == Config.slot
        }
        if (index < 0) return
        interaction.interactItem(player, Hand.MAIN_HAND)
        player.swapSlotWithHotbar(index)
    }

    @JvmStatic
    fun trySwitchChestplate(player: ClientPlayerEntity) {
        if (!Config.enable.value) return

        player.inventory.armor.find {
            it.get(DataComponentTypes.GLIDER) == Unit.INSTANCE
                    && it.get(DataComponentTypes.EQUIPPABLE)?.slot == Config.slot
        }.let {
            if (it == null) return
        }

        val interaction = mc.interactionManager!!

        val offHand = player.offHandStack
        if (
            Config.switchableEquip.match(offHand)
            && offHand.get(DataComponentTypes.EQUIPPABLE)?.slot == Config.slot
        ) {
            interaction.interactItem(player, Hand.OFF_HAND)
        }

        val index = player.swapSlotWithHotbar {
            Config.switchableEquip.match(it)
            && it.get(DataComponentTypes.EQUIPPABLE)?.slot == Config.slot
        }
        if (index < 0) return
        interaction.interactItem(player, Hand.MAIN_HAND)
        player.swapSlotWithHotbar(index)
    }


}