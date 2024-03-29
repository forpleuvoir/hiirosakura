package forpleuvoir.hiirosakura.client.mixin;

import net.minecraft.entity.ExperienceOrbEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * 经验球实体访问器
 *
 * @author forpleuvoir
 * <p>项目名 hiirosakura
 * <p>包名 forpleuvoir.hiirosakura.client.mixin
 * <p>文件名 MixinExperienceOrbEntityInterface
 * <p>创建时间 2021/7/30 18:37
 */
@Mixin(ExperienceOrbEntity.class)
public interface MixinExperienceOrbEntityInterface {

    @Accessor("orbAge")
    int getAge();
}
