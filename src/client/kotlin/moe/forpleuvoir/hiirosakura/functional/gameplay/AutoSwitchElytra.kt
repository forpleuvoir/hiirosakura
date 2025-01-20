package moe.forpleuvoir.hiirosakura.functional.gameplay

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
import net.minecraft.util.Hand
import net.minecraft.util.Unit

object AutoSwitchElytra {

    object Config : ModConfigContainer("auto_switch_elytra") {

        val enable by keyBindBoolean("enable", false)

        val slot: EquipmentSlot by enum("slot", EquipmentSlot.CHEST)

        val switchableEquip by stringList(
            "switchable_equip",
            listOf(
                "minecraft:netherite_chestplate",
                "minecraft:diamond_chestplate",
                "minecraft:chainmail_chestplate",
                "minecraft:iron_chestplate",
                "minecraft:leather_chestplate",
                "minecraft:golden_chestplate",
            )
        )

    }

    @JvmStatic
    fun trySwitchElytra(player: ClientPlayerEntity) {
        if (!Config.enable.value) return
        val interaction = mc.interactionManager!!

        val offHand = player.offHandStack
        if (
            offHand.get(DataComponentTypes.GLIDER) == Unit.INSTANCE
            && offHand.get(DataComponentTypes.EQUIPPABLE)?.slot == Config.slot
        ) {
            interaction.interactItem(player, Hand.OFF_HAND)
        }

        val index = player.swapSlotWithHotbar {
            it.get(DataComponentTypes.GLIDER) == Unit.INSTANCE
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
            offHand.item.id.toString() in Config.switchableEquip
            && offHand.get(DataComponentTypes.EQUIPPABLE)?.slot == Config.slot
        ) {
            interaction.interactItem(player, Hand.OFF_HAND)
        }

        val index = player.swapSlotWithHotbar {
            it.item.id.toString() in Config.switchableEquip
                    && it.get(DataComponentTypes.EQUIPPABLE)?.slot == Config.slot
        }
        if (index < 0) return
        interaction.interactItem(player, Hand.MAIN_HAND)
        player.swapSlotWithHotbar(index)
    }


}