package moe.forpleuvoir.hiirosakura.mixin.client.compat;

import net.irisshaders.iris.layer.BufferSourceWrapper;
import net.minecraft.client.render.VertexConsumerProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BufferSourceWrapper.class)
public interface BufferSourceWrapperAccessor {

    @Accessor("bufferSource")
    VertexConsumerProvider hiirosakura$getBufferSource();

}
