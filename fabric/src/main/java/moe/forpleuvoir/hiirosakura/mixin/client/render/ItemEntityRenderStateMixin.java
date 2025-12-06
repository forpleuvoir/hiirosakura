package moe.forpleuvoir.hiirosakura.mixin.client.render;

import moe.forpleuvoir.hiirosakura.functional.renderaddons.ItemEntityRenderStateAccessor;
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
import net.minecraft.world.entity.item.ItemEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemEntityRenderState.class)
public class ItemEntityRenderStateMixin implements ItemEntityRenderStateAccessor {

    @Unique
    private ItemEntity hiirosakura$itemEntity;

    @Unique
    public @Nullable ItemEntity hiirosakura$getItemEntity() {
        return hiirosakura$itemEntity;
    }

    @Unique
    public void hiirosakura$setItemEntity(@Nullable ItemEntity entity) {
        hiirosakura$itemEntity = entity;
    }
}
