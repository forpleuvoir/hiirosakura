package moe.forpleuvoir.hiirosakura.functional.gameplay

import moe.forpleuvoir.hiirosakura.functional.gameplay.AutoReplant.Config.mapping
import moe.forpleuvoir.hiirosakura.util.id
import moe.forpleuvoir.hiirosakura.util.swapSlotWithHotbar
import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBindBoolean
import moe.forpleuvoir.ibukigourd.task.scheduleStartTick
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.config.item.impl.stringMap
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d

object AutoReplant {

    object Config : ModConfigContainer("auto_replant") {

        val enable by keyBindBoolean("enable", false)

        val mapping by stringMap(
            "mapping", mapOf(
                "minecraft:wheat" to "minecraft:wheat_seeds"
            )
        )

    }

    @JvmStatic
    fun onBreakBlock(blockState: BlockState, blockPos: BlockPos, player: ClientPlayerEntity) {
        if (!Config.enable.value) return
        mapping[blockState.block.id.toString()]?.let { item ->
            val vec3d = Vec3d(blockPos.x.toDouble(), blockPos.y.toDouble() - 1, blockPos.z.toDouble())
            val pos = BlockPos.ofFloored(player.world.worldBorder.clamp(vec3d))
            //如果下面的方块是同类型方块则取消
            val block = player.world.getBlockState(pos).block
            if (block == Blocks.AIR || block == blockState.block) return

            //如果副手有对应物品则使用副手交互
            if (player.offHandStack.item.id.toString() == item) {
                mc.scheduleStartTick { _, _ ->
                    mc.interactionManager!!.interactBlock(player, Hand.OFF_HAND, BlockHitResult(vec3d, Direction.UP, pos, false))
                }
            }

            val index = player.swapSlotWithHotbar {
                it.item.id.toString() == item
            }
            if (index < 0) return
            mc.scheduleStartTick { _, _ ->
                mc.interactionManager!!.interactBlock(player, Hand.MAIN_HAND, BlockHitResult(vec3d, Direction.UP, pos, false))
                player.swapSlotWithHotbar(index)
            }
        }
    }

}