package moe.forpleuvoir.hiirosakura.functional.gameplay

import moe.forpleuvoir.hiirosakura.config.items.matcher.configItemStackMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.CompositeMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatchEntry
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.hiirosakura.util.swapSlotWithHotbar
import moe.forpleuvoir.ibukigourd.config.item.configKeybind
import moe.forpleuvoir.ibukigourd.config.item.configToggleKeybind
import moe.forpleuvoir.ibukigourd.input.KeyTriggerTiming
import moe.forpleuvoir.ibukigourd.input.Keybind
import moe.forpleuvoir.ibukigourd.input.KeybindSetting
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.config.ConfigGroup
import net.minecraft.client.player.LocalPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

object Gliding : ConfigGroup("gliding") {

    val disableFireworkRocketInteractionWhenGliding by configToggleKeybind("disable_firework_rocket_interaction_when_gliding", false)

    val quickUseFireworkRocketWhenGliding by configKeybind(
        "quick_use_firework_rocket__when_gliding", Keybind(
            defaultSetting = KeybindSetting(
                trigger = KeyTriggerTiming.Press,
                passthrough = true,
                strict = false
            )
        ) {
            mc.player?.let { player ->
                val interaction = mc.gameMode!!
                if (player.isFallFlying) {
                    if (fireworkMatcher.match(player.mainHandItem)) {
                        interaction.useItem(player, InteractionHand.MAIN_HAND)
                        return@Keybind
                    }
                    if (fireworkMatcher.match(player.offhandItem)) {
                        interaction.useItem(player, InteractionHand.OFF_HAND)
                        return@Keybind
                    }

                    val index = player.swapSlotWithHotbar { fireworkMatcher.match(it) }
                    if (index < 0) return@Keybind
                    interaction.useItem(player, InteractionHand.MAIN_HAND)
                    player.swapSlotWithHotbar(index)
                }
            }
        })

    val fireworkMatcher by configItemStackMatcher(
        "firework_matcher", ItemStackMatcher(
            CompositeMatcher.MatchMode.AnyMatch,
            ItemStackMatchEntry.Item(Items.FIREWORK_ROCKET)
        )
    )

    @JvmStatic
    fun fireworkRocketInteractionWhenGliding(player: LocalPlayer, hand: InteractionHand, itemStack: ItemStack): Boolean {
        if (!disableFireworkRocketInteractionWhenGliding.enabled) return false

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