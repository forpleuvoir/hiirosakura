package forpleuvoir.hiirosakura.client.mixin;

import forpleuvoir.hiirosakura.client.feature.cameraentity.SwitchCameraEntity;
import forpleuvoir.hiirosakura.client.feature.event.events.AttackEvent;
import forpleuvoir.hiirosakura.client.feature.event.events.GameExitEvent;
import forpleuvoir.hiirosakura.client.feature.event.events.ItemPickEvent;
import forpleuvoir.hiirosakura.client.feature.event.events.ItemUseEvent;
import forpleuvoir.hiirosakura.client.feature.input.AnalogInput;
import forpleuvoir.hiirosakura.client.util.ServerInfoUtil;
import kotlin.Unit;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.tutorial.TutorialManager;
import net.minecraft.client.tutorial.TutorialStep;
import net.minecraft.client.util.InputUtil;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
	@Final
	private TutorialManager tutorialManager;

	@Shadow
	@Nullable
	public Screen currentScreen;


	@Shadow
	@Final
	public GameOptions options;

	@Shadow
	@Nullable
	public ClientPlayerEntity player;

	@Inject(method = "<init>", at = @At("RETURN"))
	public void init(CallbackInfo ci) {
		//关闭游戏自带教程
		tutorialManager.setStep(TutorialStep.NONE);
		AnalogInput.setOnReleasedCallBack(ATTACK, key -> {
			KeyBinding.setKeyPressed(InputUtil.fromTranslationKey(
					this.options.keyAttack.getBoundKeyTranslationKey()), false
			);
			return Unit.INSTANCE;
		});
		AnalogInput.setOnReleasedCallBack(USE, key -> {
			KeyBinding.setKeyPressed(InputUtil.fromTranslationKey(
					this.options.keyUse.getBoundKeyTranslationKey()), false);
			return Unit.INSTANCE;
		});

		AnalogInput.setOnReleasedCallBack(PICK_ITEM, key -> {
			KeyBinding.setKeyPressed(InputUtil.fromTranslationKey(
					this.options.keyPickItem.getBoundKeyTranslationKey()), false);
			return Unit.INSTANCE;
		});

	}


	@Inject(method = "handleInputEvents", at = @At(value = "HEAD"))
	public void handleInputEvents(CallbackInfo callbackInfo) {
		if (this.currentScreen == null) {
			if (this.options.keyAttack.isPressed()) {
				AnalogInput.set(ATTACK, 0);
			}
			if (AnalogInput.isPress(ATTACK))
				KeyBinding.setKeyPressed(
						InputUtil.fromTranslationKey(this.options.keyAttack.getBoundKeyTranslationKey()), true);
			if (this.options.keyUse.isPressed()) {
				AnalogInput.set(USE, 0);
			}
			if (AnalogInput.isPress(USE))
				KeyBinding.setKeyPressed(InputUtil.fromTranslationKey(this.options.keyUse.getBoundKeyTranslationKey()),
						true
				);
			if (this.options.keyPickItem.isPressed()) {
				AnalogInput.set(PICK_ITEM, 0);
			}
			if (AnalogInput.isPress(PICK_ITEM))
				KeyBinding.setKeyPressed(
						InputUtil.fromTranslationKey(this.options.keyPickItem.getBoundKeyTranslationKey()), true);
		}
	}

	@Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
	public void doAttack(CallbackInfo callbackInfo) {
		new AttackEvent().broadcast();
		if (SwitchCameraEntity.isSwitched()) {
			callbackInfo.cancel();
		}
	}

	@Inject(method = "doItemPick", at = @At("HEAD"), cancellable = true)
	public void doItemPick(CallbackInfo callbackInfo) {
		new ItemPickEvent().broadcast();
		if (SwitchCameraEntity.isSwitched()) {
			callbackInfo.cancel();
		}
	}

	@Inject(method = "doItemUse", at = @At("HEAD"), cancellable = true)
	public void doItemUse(CallbackInfo callbackInfo) {
		new ItemUseEvent(player != null ? player.getMainHandStack().getName().getString() : "null").broadcast();
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
