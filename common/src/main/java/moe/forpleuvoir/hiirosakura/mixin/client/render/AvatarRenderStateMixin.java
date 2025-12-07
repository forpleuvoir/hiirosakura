package moe.forpleuvoir.hiirosakura.mixin.client.render;

import moe.forpleuvoir.hiirosakura.functional.chataddons.chatbubble.AvatarRenderStateAccessor;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AvatarRenderState.class)
public class AvatarRenderStateMixin implements AvatarRenderStateAccessor {

    @Unique
    private String hiirosakura$name;

    @Unique
    public @Nullable String hiirosakura$getName() {
        return hiirosakura$name;
    }

    @Unique
    public void hiirosakura$setName(@NotNull String name) {
        hiirosakura$name = name;
    }
}
