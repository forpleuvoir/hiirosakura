package moe.forpleuvoir.hiirosakura.functional.gameplay.chaindoors

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import moe.forpleuvoir.hiirosakura.config.items.configChainDoorsRuleList
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfo
import moe.forpleuvoir.hiirosakura.util.logger
import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBindBoolean
import moe.forpleuvoir.ibukigourd.config.item.impl.keyCode
import moe.forpleuvoir.ibukigourd.input.InputHandler
import moe.forpleuvoir.ibukigourd.input.Keyboard
import moe.forpleuvoir.nebula.common.util.primitive.onFalse
import net.minecraft.client.multiplayer.MultiPlayerGameMode
import net.minecraft.client.player.LocalPlayer
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.DoorBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf
import net.minecraft.world.phys.BlockHitResult
import kotlin.jvm.optionals.getOrNull

object ChainDoors : ModConfigContainer("chain_doors") {

    private val logger = logger()

    val enabled by keyBindBoolean("enable", false)

    val activationKey by keyCode("activation_key", Keyboard.LEFT_SHIFT)

    val rules by configChainDoorsRuleList(
        "rules", listOf(
            ChainDoorsRule.MOB_INTERACTABLE_DOORS,
            ChainDoorsRule.TRAPDOORS
        )
    )

    private var clicking: Boolean = false

    fun isEnabled(keyToggleMode: Boolean): Boolean = keyToggleMode == InputHandler.wasKeyPressed(activationKey)


    @JvmStatic
    fun onClickedDoor(
        player: LocalPlayer,
        level: Level,
        hand: InteractionHand,
        blockResult: BlockHitResult,
        gameMode: MultiPlayerGameMode
    ) {
        // 基础状态检查
        if (!enabled.value || clicking) return

        try {
            clicking = true

            val originPos = blockResult.blockPos
            val originState = level.getBlockState(originPos)

            val rule = rules.find {
                it.originDoor.match(BlockInfo(originState, originPos, blockResult.direction))
            } ?: return

            isEnabled(rule.keyToggleMode).onFalse { return }

            val predicate: (BlockPos) -> Boolean = { pos ->
                rule.chainDoor.match(BlockInfo(level.getBlockState(pos), pos, blockResult.direction))
            }

            val reachDist = player.blockInteractionRange() + 0.25
            val reachDistSq = reachDist * reachDist

            val alreadyActed = ObjectOpenHashSet<BlockPos>()

            alreadyActed.add(originPos)
            getOtherPart(originPos, originState)?.let { alreadyActed.add(it) }

            rule.strategy.collect(originPos, level, predicate).forEach { targetPos ->
                if (alreadyActed.add(targetPos)) {
                    val state = level.getBlockState(targetPos)

                    if (player.eyePosition.distanceToSqr(targetPos.center) > reachDistSq) {
                        return@forEach
                    }

                    getOtherPart(targetPos, state)?.let { alreadyActed.add(it) }

                    gameMode.useItemOn(
                        player,
                        hand,
                        BlockHitResult(targetPos.center, blockResult.direction, targetPos, false)
                    )
                }
            }

        } catch (e: Exception) {
            logger.error("Failed to execute chain doors interaction", e)
        } finally {
            clicking = false
        }
    }

    fun getOtherPart(pos: BlockPos, state: BlockState): BlockPos? {
        val half = state.getOptionalValue(DoorBlock.HALF).getOrNull() ?: return null
        return if (half == DoubleBlockHalf.LOWER) pos.above() else pos.below()
    }

}