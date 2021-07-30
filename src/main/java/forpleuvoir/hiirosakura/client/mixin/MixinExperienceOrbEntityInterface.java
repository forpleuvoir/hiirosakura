package forpleuvoir.hiirosakura.client.mixin;

import net.minecraft.entity.ExperienceOrbEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * 经验球实体访问器
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.mixin
 * <p>#class_name MixinExperienceOrbEntityInterface
 * <p>#create_time 2021/7/30 18:37
 */
@Mixin(ExperienceOrbEntity.class)
public interface MixinExperienceOrbEntityInterface {

    @Accessor("orbAge")
    int getAge();
}
