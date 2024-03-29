package forpleuvoir.hiirosakura.client.mixin;

import forpleuvoir.hiirosakura.client.feature.cameraentity.SwitchCameraEntity;
import forpleuvoir.hiirosakura.client.feature.event.events.AttackEvent;
import forpleuvoir.hiirosakura.client.feature.event.events.GameExitEvent;
import forpleuvoir.hiirosakura.client.feature.event.events.ItemPickEvent;
import forpleuvoir.hiirosakura.client.feature.event.events.ItemUseEvent;
import forpleuvoir.hiirosakura.client.feature.input.AnalogInput;
import forpleuvoir.hiirosakura.client.util.ServerInfoUtil;
import forpleuvoir.ibuki_gourd.mod.config.WhiteListMode;
import kotlin.Unit;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.tutorial.TutorialManager;
import net.minecraft.client.tutorial.TutorialStep;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static forpleuvoir.hiirosakura.client.config.Configs.Toggles.ENABLE_ITEM_USE_PROTECTION;
import static forpleuvoir.hiirosakura.client.config.Configs.Values.ITEM_USE_PROTECTION_LIST;
import static forpleuvoir.hiirosakura.client.config.Configs.Values.ITEM_USE_PROTECTION_MODE;
import static forpleuvoir.hiirosakura.client.feature.input.AnalogInput.Key.*;

/**
 * MinecraftClient注入
 * <p>{@link #init(CallbackInfo)} 关闭游戏再带教程
 *
 * @author forpleuvoir
 * <p>项目名 hiirosakura
 * <p>包名 forpleuvoir.hiirosakura.client.mixin
 * <p>文件名 MixinMinecraftClient
 * <p>创建时间 2021/6/11 21:02
 */
@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {

    @Shadow
    @Nullable
    public Screen currentScreen;
    @Shadow
    @Final
    public GameOptions options;
    @Shadow
    @Nullable
    public ClientPlayerEntity player;
    @Shadow
    @Final
    private TutorialManager tutorialManager;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(CallbackInfo ci) {
        //关闭游戏自带教程
        tutorialManager.setStep(TutorialStep.NONE);
        AnalogInput.setOnReleasedCallBack(ATTACK, key -> {
            KeyBinding.setKeyPressed(InputUtil.fromTranslationKey(
                    this.options.attackKey.getBoundKeyTranslationKey()), false
            );
            return Unit.INSTANCE;
        });
        AnalogInput.setOnReleasedCallBack(USE, key -> {
            KeyBinding.setKeyPressed(InputUtil.fromTranslationKey(
                    this.options.useKey.getBoundKeyTranslationKey()), false);
            return Unit.INSTANCE;
        });

        AnalogInput.setOnReleasedCallBack(PICK_ITEM, key -> {
            KeyBinding.setKeyPressed(InputUtil.fromTranslationKey(
                    this.options.pickItemKey.getBoundKeyTranslationKey()), false);
            return Unit.INSTANCE;
        });

    }


    @Inject(method = "handleInputEvents", at = @At(value = "HEAD"))
    public void handleInputEvents(CallbackInfo callbackInfo) {
        if (this.currentScreen == null) {
            if (this.options.attackKey.isPressed()) {
                AnalogInput.set(ATTACK, 0);
            }
            if (AnalogInput.isPress(ATTACK))
                KeyBinding.setKeyPressed(
                        InputUtil.fromTranslationKey(this.options.attackKey.getBoundKeyTranslationKey()), true);
            if (this.options.useKey.isPressed()) {
                AnalogInput.set(USE, 0);
            }
            if (AnalogInput.isPress(USE))
                KeyBinding.setKeyPressed(InputUtil.fromTranslationKey(this.options.useKey.getBoundKeyTranslationKey()),
                        true
                );
            if (this.options.pickItemKey.isPressed()) {
                AnalogInput.set(PICK_ITEM, 0);
            }
            if (AnalogInput.isPress(PICK_ITEM))
                KeyBinding.setKeyPressed(
                        InputUtil.fromTranslationKey(this.options.pickItemKey.getBoundKeyTranslationKey()), true);
        }
    }

    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    public void doAttack(CallbackInfoReturnable<Boolean> cir) {
        var event = new AttackEvent();
        event.broadcast();
        if (event.isCanceled()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
        if (SwitchCameraEntity.isSwitched()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(method = "doItemPick", at = @At("HEAD"), cancellable = true)
    public void doItemPick(CallbackInfo callbackInfo) {
        var event = new ItemPickEvent();
        event.broadcast();
        if (event.isCanceled()) callbackInfo.cancel();
        if (SwitchCameraEntity.isSwitched()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "doItemUse", at = @At("HEAD"), cancellable = true)
    public void doItemUse(CallbackInfo callbackInfo) {
        var event = new ItemUseEvent(player != null ? player.getMainHandStack().getName().getString() : "null");
        event.broadcast();
        if (event.isCanceled()) callbackInfo.cancel();
        ItemStack stack = player.getMainHandStack();

        if (ENABLE_ITEM_USE_PROTECTION.getValue()) {
            String id = Registries.ITEM.getId(stack.getItem()).toString();
            NbtCompound nbt = stack.getNbt();
            if (nbt != null) {
                id = id + nbt;
            }
            WhiteListMode mode = (WhiteListMode) ITEM_USE_PROTECTION_MODE.getValue();
            List<String> list = ITEM_USE_PROTECTION_LIST.getValue();
            boolean inList = list.contains(Registries.ITEM.getId(stack.getItem()).toString()) || list.contains(id);
            boolean isProtected = switch (mode) {
                case None -> false;
                case WhiteList -> inList;
                case BlackList -> !inList;
            };
            if (isProtected) {
                callbackInfo.cancel();
            }
        }

        if (SwitchCameraEntity.isSwitched()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"))
    public void disconnect(Screen screen, CallbackInfo callbackInfo) {
        new GameExitEvent(ServerInfoUtil.getName(), ServerInfoUtil.getAddress()).broadcast();
        ServerInfoUtil.clear();
    }


}
