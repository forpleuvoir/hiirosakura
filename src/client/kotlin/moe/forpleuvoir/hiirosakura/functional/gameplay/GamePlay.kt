package moe.forpleuvoir.hiirosakura.functional.gameplay

import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBindBoolean
import moe.forpleuvoir.ibukigourd.util.mc
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.ActionResult.Success
import net.minecraft.util.ActionResult.SwingSource
import net.minecraft.util.Hand

object GamePlay : ModConfigContainer("gameplay") {

    val autoRebirth by keyBindBoolean("auto_rebirth", false)

    val disableFireworkRocketInteractionWhenGliding by keyBindBoolean("disable_firework_rocket_interaction_when_gliding", false)

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

    init {
        addConfig(ItemUseIntercept)
        addConfig(BlockBreakProtection)
        addConfig(AutoSwitchElytra)
        addConfig(AutoReplant)
        addConfig(CameraSwitcher)
        addConfig(SoundEventFilter)
    }

}