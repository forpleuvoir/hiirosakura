package forpleuvoir.hiirosakura.client.mixin;

/**
 * 键盘输入
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.mixin
 * <p>#class_name MixinKeyboardInput
 * <p>#create_time 2021-07-28 11:46
 */

import forpleuvoir.hiirosakura.client.feature.input.AnalogInput;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public abstract class MixinKeyboardInput {
    private static final AnalogInput analogInput = AnalogInput.getInstance();

    @Inject(method = "tick", at = @At("RETURN"))
    public void tick(CallbackInfo callbackInfo) {
        analogInput.tick();
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE",target ="Lnet/minecraft/client/option/KeyBinding;isPressed()Z",ordinal = 0))
    public boolean analogForward(KeyBinding keyBinding) {
        return analogInput.getPressingForward() ? analogInput.getPressingForward() : keyBinding.isPressed();
    }
    @Redirect(method = "tick", at = @At(value = "INVOKE",target ="Lnet/minecraft/client/option/KeyBinding;isPressed()Z",ordinal = 1))
    public boolean analogBack(KeyBinding keyBinding) {
        return analogInput.getPressingBack() ? analogInput.getPressingBack() : keyBinding.isPressed();
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE",target ="Lnet/minecraft/client/option/KeyBinding;isPressed()Z",ordinal = 2))
    public boolean analogLeft(KeyBinding keyBinding) {
        return analogInput.getPressingLeft() ? analogInput.getPressingLeft() : keyBinding.isPressed();
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE",target ="Lnet/minecraft/client/option/KeyBinding;isPressed()Z",ordinal = 3))
    public boolean analogRight(KeyBinding keyBinding) {
        return analogInput.getPressingRight() ? analogInput.getPressingRight() : keyBinding.isPressed();
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE",target ="Lnet/minecraft/client/option/KeyBinding;isPressed()Z",ordinal = 4))
    public boolean analogJump(KeyBinding keyBinding) {
        return analogInput.getJumping() ? analogInput.getJumping() : keyBinding.isPressed();
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE",target ="Lnet/minecraft/client/option/KeyBinding;isPressed()Z",ordinal = 5))
    public boolean analogSneak(KeyBinding keyBinding) {
        return analogInput.getSneaking() ? analogInput.getSneaking() : keyBinding.isPressed();
    }
}
