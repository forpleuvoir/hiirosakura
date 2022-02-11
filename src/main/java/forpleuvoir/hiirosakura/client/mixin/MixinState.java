package forpleuvoir.hiirosakura.client.mixin;

import net.minecraft.state.State;
import net.minecraft.state.property.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.function.Function;

/**
 * 项目名 hiirosakura
 * <p>
 * 包名 forpleuvoir.hiirosakura.client.mixin
 * <p>
 * 文件名 MixinState
 * <p>
 * 创建时间 2022/2/12 4:23
 *
 * @author forpleuvoir
 */
@Mixin(State.class)
public interface MixinState {

    @Accessor("PROPERTY_MAP_PRINTER")
    static Function<Map.Entry<Property<?>, Comparable<?>>, String> PROPERTY_MAP_PRINTER() {
        throw new AssertionError();
    }
}
