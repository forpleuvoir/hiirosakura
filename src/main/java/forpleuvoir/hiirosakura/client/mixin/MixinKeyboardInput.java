package forpleuvoir.hiirosakura.client.mixin;


import forpleuvoir.hiirosakura.client.feature.input.AnalogInput;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static forpleuvoir.hiirosakura.client.feature.input.AnalogInput.Key.*;

/**
 * 键盘输入
 *
 * @author forpleuvoir
 * <p>项目名 hiirosakura
 * <p>包名 forpleuvoir.hiirosakura.client.mixin
 * <p>文件名 MixinKeyboardInput
 * <p>创建时间 2021-07-28 11:46
 */
@Mixin(KeyboardInput.class)
public abstract class MixinKeyboardInput {

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z", ordinal = 0))
    public boolean analogForward(KeyBinding keyBinding) {
        return AnalogInput.isPress(FORWARD) ? AnalogInput.isPress(FORWARD) : keyBinding.isPressed();
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z", ordinal = 1))
    public boolean analogBack(KeyBinding keyBinding) {
        return AnalogInput.isPress(BACK) ? AnalogInput.isPress(BACK) : keyBinding.isPressed();
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z", ordinal = 2))
    public boolean analogLeft(KeyBinding keyBinding) {
        return AnalogInput.isPress(LEFT) ? AnalogInput.isPress(LEFT) : keyBinding.isPressed();
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z", ordinal = 3))
    public boolean analogRight(KeyBinding keyBinding) {
        return AnalogInput.isPress(RIGHT) ? AnalogInput.isPress(RIGHT) : keyBinding.isPressed();
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z", ordinal = 4))
    public boolean analogJump(KeyBinding keyBinding) {
        return AnalogInput.isPress(JUMP) ? AnalogInput.isPress(JUMP) : keyBinding.isPressed();
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z", ordinal = 5))
    public boolean analogSneak(KeyBinding keyBinding) {
        return AnalogInput.isPress(SNEAK) ? AnalogInput.isPress(SNEAK) : keyBinding.isPressed();
    }
}
