package forpleuvoir.hiirosakura.client.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * 客户端注入接口
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.mixin
 * <p>#class_name MixinMinecraftClientInterface
 * <p>#create_time 2021-07-26 16:21
 */
@Mixin(MinecraftClient.class)
public interface MixinMinecraftClientInterface {

    @Invoker("doAttack")
    void callDoAttack();

    @Invoker("doItemUse")
    void callDoItemUse();

    @Invoker("doItemPick")
    void callDoItemPick();
}
