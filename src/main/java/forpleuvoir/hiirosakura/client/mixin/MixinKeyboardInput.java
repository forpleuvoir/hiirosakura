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
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.mixin
 * <p>#class_name MixinKeyboardInput
 * <p>#create_time 2021-07-28 11:46
 */
@Mixin(KeyboardInput.class)
public abstract class MixinKeyboardInput {
    private static final AnalogInput analogInput = AnalogInput.getInstance();

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z", ordinal = 0))
    public boolean analogForward(KeyBinding keyBinding) {
        return analogInput.isPress(FORWARD) ? analogInput.isPress(FORWARD) : keyBinding.isPressed();
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z", ordinal = 1))
    public boolean analogBack(KeyBinding keyBinding) {
        return analogInput.isPress(BACK) ? analogInput.isPress(BACK) : keyBinding.isPressed();
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z", ordinal = 2))
    public boolean analogLeft(KeyBinding keyBinding) {
        return analogInput.isPress(LEFT) ? analogInput.isPress(LEFT) : keyBinding.isPressed();
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z", ordinal = 3))
    public boolean analogRight(KeyBinding keyBinding) {
        return analogInput.isPress(RIGHT) ? analogInput.isPress(RIGHT) : keyBinding.isPressed();
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z", ordinal = 4))
    public boolean analogJump(KeyBinding keyBinding) {
        return analogInput.isPress(JUMP) ? analogInput.isPress(JUMP) : keyBinding.isPressed();
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z", ordinal = 5))
    public boolean analogSneak(KeyBinding keyBinding) {
        return analogInput.isPress(SNEAK) ? analogInput.isPress(SNEAK) : keyBinding.isPressed();
    }
}
