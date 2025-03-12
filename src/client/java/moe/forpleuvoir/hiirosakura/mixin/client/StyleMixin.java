package moe.forpleuvoir.hiirosakura.mixin.client;

import moe.forpleuvoir.hiirosakura.functional.renderaddons.RenderInfoAddon;
import net.minecraft.text.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(Style.class)
public class StyleMixin {

    @Inject(method = "isObfuscated", at = @At("HEAD"), cancellable = true)
    public void hiirosakura$isObfuscated(CallbackInfoReturnable<Boolean> cir) {
        if (RenderInfoAddon.INSTANCE.getDisableTextObfuscationRender()) {
            cir.setReturnValue(false);
        }
    }

}
