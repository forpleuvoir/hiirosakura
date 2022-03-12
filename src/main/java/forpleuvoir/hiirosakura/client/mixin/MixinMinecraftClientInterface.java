package forpleuvoir.hiirosakura.client.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * 客户端注入接口
 *
 * @author forpleuvoir
 * <p>项目名 hiirosakura
 * <p>包名 forpleuvoir.hiirosakura.client.mixin
 * <p>文件名 MixinMinecraftClientInterface
 * <p>创建时间 2021-07-26 16:21
 */
@Mixin(MinecraftClient.class)
public interface MixinMinecraftClientInterface {

    @Invoker("doAttack")
    boolean callDoAttack();

    @Invoker("doItemUse")
    void callDoItemUse();

    @Invoker("doItemPick")
    void callDoItemPick();
}
