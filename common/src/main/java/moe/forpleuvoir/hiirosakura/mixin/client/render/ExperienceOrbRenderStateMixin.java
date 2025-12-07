package moe.forpleuvoir.hiirosakura.mixin.client.render;

import moe.forpleuvoir.hiirosakura.functional.renderaddons.ExperienceOrbRenderStateAccessor;
import net.minecraft.client.renderer.entity.state.ExperienceOrbRenderState;
import net.minecraft.world.entity.ExperienceOrb;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ExperienceOrbRenderState.class)
public class ExperienceOrbRenderStateMixin implements ExperienceOrbRenderStateAccessor {

    @Unique
    ExperienceOrb hiirosakura$experienceOrb;

    @Override
    public @Nullable ExperienceOrb hiirosakura$getExperienceOrb() {
        return hiirosakura$experienceOrb;
    }

    @Override
    public void hiirosakura$setExperienceOrb(@Nullable ExperienceOrb entity) {
        hiirosakura$experienceOrb = entity;
    }
}
