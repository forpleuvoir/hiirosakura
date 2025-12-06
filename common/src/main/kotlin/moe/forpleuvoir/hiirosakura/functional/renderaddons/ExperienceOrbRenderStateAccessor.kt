package moe.forpleuvoir.hiirosakura.functional.renderaddons

import net.minecraft.world.entity.ExperienceOrb

@Suppress("FunctionName")
interface ExperienceOrbRenderStateAccessor {

    fun `hiirosakura$getExperienceOrb`(): ExperienceOrb?

    fun `hiirosakura$setExperienceOrb`(entity: ExperienceOrb?)
}