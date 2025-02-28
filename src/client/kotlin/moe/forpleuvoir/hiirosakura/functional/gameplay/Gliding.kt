package moe.forpleuvoir.hiirosakura.functional.gameplay

import moe.forpleuvoir.hiirosakura.config.items.itemStackMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatchEntry
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.MultiMatcher
import moe.forpleuvoir.hiirosakura.util.swapSlotWithHotbar
import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBind
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBindBoolean
import moe.forpleuvoir.ibukigourd.input.KeyBind
import moe.forpleuvoir.ibukigourd.input.KeyBindSetting
import moe.forpleuvoir.ibukigourd.input.KeyTriggerMode
import moe.forpleuvoir.ibukigourd.util.mc
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.ActionResult.Success
import net.minecraft.util.ActionResult.SwingSource
import net.minecraft.util.Hand

object Gliding : ModConfigContainer("gliding") {

    val disableFireworkRocketInteractionWhenGliding by keyBindBoolean("disable_firework_rocket_interaction_when_gliding", false)

    val quickUseFireworkRocketWhenGliding by keyBind("quick_use_firework_rocket__when_gliding", KeyBind(defaultSetting = KeyBindSetting {
        triggerMode = KeyTriggerMode.OnPress
        exactMatch = false
    }) {
        mc.player?.let { player ->
            val interaction = mc.interactionManager!!
            if (player.isGliding) {
                if (fireworkMatcher.match(player.mainHandStack)) {
                    interaction.interactItem(player, Hand.MAIN_HAND)
                    return@KeyBind
                }
                if (fireworkMatcher.match(player.offHandStack)) {
                    interaction.interactItem(player, Hand.OFF_HAND)
                    return@KeyBind
                }

                val index = player.swapSlotWithHotbar { fireworkMatcher.match(it) }
                if (index < 0) return@KeyBind
                interaction.interactItem(player, Hand.MAIN_HAND)
                player.swapSlotWithHotbar(index)
            }
        }
    })

    val fireworkMatcher by itemStackMatcher(
        "firework_matcher", ItemStackMatcher(
            MultiMatcher.MatchMode.AnyMatch,
            ItemStackMatchEntry.Item(Items.FIREWORK_ROCKET)
        )
    )

    @JvmStatic
    fun fireworkRocketInteractionWhenGliding(player: ClientPlayerEntity, hand: Hand, itemStack: ItemStack): Boolean {
        if (!disableFireworkRocketInteractionWhenGliding.value) return false

        if (player.isGliding && itemStack.item == Items.FIREWORK_ROCKET) {
            val result = mc.interactionManager!!.interactItem(player, hand)
            if (result is Success) {
                if (result.swingSource() == SwingSource.CLIENT) {
                    player.swingHand(hand);
                }

                mc.gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
                return true
            }
        }

        return false
    }
}