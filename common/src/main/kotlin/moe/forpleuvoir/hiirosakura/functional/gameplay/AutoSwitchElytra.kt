package moe.forpleuvoir.hiirosakura.functional.gameplay

import moe.forpleuvoir.hiirosakura.config.items.matcher.itemStackMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.CompositeMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatchEntry
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.hiirosakura.util.swapSlotWithHotbar
import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBindBoolean
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.config.item.impl.enum
import net.minecraft.client.player.LocalPlayer
import net.minecraft.core.component.DataComponents
import net.minecraft.util.Unit
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.Items

object AutoSwitchElytra : ModConfigContainer("auto_switch_elytra") {

    val enable by keyBindBoolean("enable", false)

    val slot: EquipmentSlot by enum("slot", EquipmentSlot.CHEST)

    val switchableEquip by itemStackMatcher(
        "switchable_equip",
        ItemStackMatcher(
            CompositeMatcher.MatchMode.AnyMatch,
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
            CompositeMatcher.MatchMode.AnyMatch,
            ItemStackMatchEntry.Item(Items.ELYTRA)
        )
    )


    @JvmStatic
    fun trySwitchElytra(player: LocalPlayer) {
        if (!enable.value) return

        val interaction = mc.gameMode!!

        val offHand = player.offhandItem
        if (
            switchableGlider.match(offHand)
            && offHand.get(DataComponents.GLIDER) == Unit.INSTANCE
            && offHand.get(DataComponents.EQUIPPABLE)?.slot == slot
        ) {
            interaction.useItem(player, InteractionHand.OFF_HAND)
        }

        val index = player.swapSlotWithHotbar {
            switchableGlider.match(it)
                    && it.get(DataComponents.GLIDER) == Unit.INSTANCE
                    && it.get(DataComponents.EQUIPPABLE)?.slot == slot
        }
        if (index < 0) return
        interaction.useItem(player, InteractionHand.MAIN_HAND)
        player.swapSlotWithHotbar(index)
    }

    @JvmStatic
    fun trySwitchChestplate(player: LocalPlayer) {
        if (!enable.value) return

        player.inventory.equipment.items.values.find {
            it.get(DataComponents.GLIDER) == Unit.INSTANCE
                    && it.get(DataComponents.EQUIPPABLE)?.slot == slot
        }.let {
            if (it == null) return
        }

        val interaction = mc.gameMode!!

        val offHand = player.offhandItem
        if (
            switchableEquip.match(offHand)
            && offHand.get(DataComponents.EQUIPPABLE)?.slot == slot
        ) {
            interaction.useItem(player, InteractionHand.OFF_HAND)
        }

        val index = player.swapSlotWithHotbar {
            switchableEquip.match(it)
                    && it.get(DataComponents.EQUIPPABLE)?.slot == slot
        }
        if (index < 0) return
        interaction.useItem(player, InteractionHand.MAIN_HAND)
        player.swapSlotWithHotbar(index)
    }


}