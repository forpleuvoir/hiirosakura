package forpleuvoir.hiirosakura.client.feature.event.eventinterface.impl;

import forpleuvoir.hiirosakura.client.feature.event.eventinterface.IClientPlayerInterface;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.registry.Registry;

/**
 * 客户端玩家接口实现
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.feature.event.eventinterface.impl
 * <p>#class_name ClientPlayerInterface
 * <p>#create_time 2021/8/1 14:47
 */
public class ClientPlayerInterface implements IClientPlayerInterface {

    public ClientPlayerInterface(ClientPlayerEntity clientPlayerEntity) {
        this.player = clientPlayerEntity;
    }

    private final ClientPlayerEntity player;

    @Override
    public String getUUID() {
        return player.getUuidAsString();
    }

    @Override
    public String getName() {
        return player.getEntityName();
    }

    @Override
    public float getHealth() {
        return player.getHealth();
    }

    @Override
    public float getMaxHealth() {
        return player.getMaxHealth();
    }

    @Override
    public double getHealthProgress() {
        return getHealth() / getMaxHealth();
    }

    @Override
    public String getMainHandItemRegisterId() {
        return Registry.ITEM.getId(player.getMainHandStack().getItem()).toString();
    }

    @Override
    public String getMainHandItemTranslationKey() {
        return player.getMainHandStack().getItem().getTranslationKey();
    }

    @Override
    public String getMainHandItemDisplayName() {
        return player.getMainHandStack().getName().getString();
    }

    @Override
    public int getMainHandItemDurable() {
        return getMainHandItemMaxDurable() - player.getMainHandStack().getDamage();
    }

    @Override
    public int getMainHandItemMaxDurable() {
        return player.getMainHandStack().getMaxDamage();
    }

    @Override
    public double getMainHandItemDurableProgress() {
        return ((double) getMainHandItemDurable()) / player.getMainHandStack().getMaxDamage();
    }

    @Override
    public boolean isFireImmune() {
        return player.isFireImmune();
    }

    @Override
    public boolean isOnFire() {
        return player.isOnFire();
    }

    @Override
    public boolean isTouchingWaterOrRain() {
        return player.isTouchingWaterOrRain();
    }

    @Override
    public boolean isSneaking() {
        return player.isSneaking();
    }

    @Override
    public boolean isSwimming() {
        return player.isSwimming();
    }

    @Override
    public boolean isInvisible() {
        return player.isInvisible();
    }

    @Override
    public double[] getPosition() {
        return new double[]{player.getPos().getX(), player.getPos().getY(), player.getPos().getX()};
    }

    @Override
    public double getPosX() {
        return player.getPos().getX();
    }

    @Override
    public double getPosY() {
        return player.getPos().getY();
    }

    @Override
    public double getPosZ() {
        return player.getPos().getZ();
    }

    @Override
    public float getPitch() {
        return player.getPitch();
    }

    @Override
    public float getYaw() {
        return player.getYaw();
    }

    @Override
    public float getSpeed() {
        return player.getSpeed();
    }

    @Override
    public int getExperienceLevel() {
        return player.experienceLevel;
    }

    @Override
    public int getTotalExperience() {
        return player.totalExperience;
    }

    @Override
    public float getExperienceProgress() {
        return player.experienceProgress;
    }
}
