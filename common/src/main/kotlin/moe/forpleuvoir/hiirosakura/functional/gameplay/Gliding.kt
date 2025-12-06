package moe.forpleuvoir.hiirosakura.functional.gameplay

import moe.forpleuvoir.hiirosakura.config.items.matcher.itemStackMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.CompositeMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatchEntry
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.hiirosakura.util.swapSlotWithHotbar
import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBind
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBindBoolean
import moe.forpleuvoir.ibukigourd.input.KeyBind
import moe.forpleuvoir.ibukigourd.input.KeyBindSetting
import moe.forpleuvoir.ibukigourd.input.KeyTriggerMode
import moe.forpleuvoir.ibukigourd.util.mc
import net.minecraft.client.player.LocalPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

object Gliding : ModConfigContainer("gliding") {

    val disableFireworkRocketInteractionWhenGliding by keyBindBoolean("disable_firework_rocket_interaction_when_gliding", false)

    val quickUseFireworkRocketWhenGliding by keyBind("quick_use_firework_rocket__when_gliding", KeyBind(defaultSetting = KeyBindSetting {
        triggerMode = KeyTriggerMode.OnPress
        exactMatch = false
    }) {
        mc.player?.let { player ->
            val interaction = mc.gameMode!!
            if (player.isFallFlying) {
                if (fireworkMatcher.match(player.mainHandItem)) {
                    interaction.useItem(player, InteractionHand.MAIN_HAND)
                    return@KeyBind
                }
                if (fireworkMatcher.match(player.offhandItem)) {
                    interaction.useItem(player, InteractionHand.OFF_HAND)
                    return@KeyBind
                }

                val index = player.swapSlotWithHotbar { fireworkMatcher.match(it) }
                if (index < 0) return@KeyBind
                interaction.useItem(player, InteractionHand.MAIN_HAND)
                player.swapSlotWithHotbar(index)
            }
        }
    })

    val fireworkMatcher by itemStackMatcher(
        "firework_matcher", ItemStackMatcher(
            CompositeMatcher.MatchMode.AnyMatch,
            ItemStackMatchEntry.Item(Items.FIREWORK_ROCKET)
        )
    )

    @JvmStatic
    fun fireworkRocketInteractionWhenGliding(player: LocalPlayer, hand: InteractionHand, itemStack: ItemStack): Boolean {
        if (!disableFireworkRocketInteractionWhenGliding.value) return false

        if (player.isFallFlying && itemStack.item == Items.FIREWORK_ROCKET) {
            val result = mc.gameMode!!.useItem(player, hand)
            if (result is InteractionResult.Success) {
                if (result.swingSource() == InteractionResult.SwingSource.CLIENT) {
                    player.swing(hand);
                }

                mc.gameRenderer.itemInHandRenderer.itemUsed(hand)
                return true
            }
        }

        return false
    }
}