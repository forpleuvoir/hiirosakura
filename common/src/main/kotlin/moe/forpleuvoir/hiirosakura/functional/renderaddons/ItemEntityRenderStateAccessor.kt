package moe.forpleuvoir.hiirosakura.functional.renderaddons

import net.minecraft.world.entity.item.ItemEntity

@Suppress("FunctionName")
interface ItemEntityRenderStateAccessor {

    fun `hiirosakura$getItemEntity`(): ItemEntity?

    fun `hiirosakura$setItemEntity`(entity: ItemEntity?)

}