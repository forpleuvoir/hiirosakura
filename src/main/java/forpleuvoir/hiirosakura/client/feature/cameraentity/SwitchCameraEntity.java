package forpleuvoir.hiirosakura.client.feature.cameraentity;

import forpleuvoir.hiirosakura.client.HiiroSakuraClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;

import java.util.LinkedList;
import java.util.List;

import static net.minecraft.util.hit.HitResult.Type.ENTITY;

/**
 * 切换相机实体
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.feature.cameraentity
 * <p>#class_name SwitchCameraEntity
 * <p>#create_time 2021/6/22 21:12
 */
public class SwitchCameraEntity {
    public static final SwitchCameraEntity INSTANCE = new SwitchCameraEntity(MinecraftClient.getInstance());
    private Entity targetEntity;
    private final MinecraftClient client;

    static {
        HiiroSakuraClient.addTickHandler(mc -> {
            if (mc.options.keySneak.wasPressed()) {
                if (mc.getCameraEntity() == SwitchCameraEntity.INSTANCE.targetEntity)
                    mc.setCameraEntity(mc.player);
            }
        });
    }

    public boolean isSwitched() {
        return client.getCameraEntity() == targetEntity;
    }

    public SwitchCameraEntity(MinecraftClient client) {
        this.client = client;
    }

    public List<AbstractClientPlayerEntity> getPlayers() {
        if (client.world != null)
            return client.world.getPlayers();
        return null;
    }

    public List<String> getPlayersSuggest() {
        List<String> playerNames = new LinkedList<>();
        if (getPlayers() != null)
            getPlayers().forEach(player -> playerNames.add(player.getEntityName()));
        return playerNames;
    }

    public void switchOtherPlayer(String playerName) {
        getPlayers().stream().filter(
                player -> player.getEntityName().equals(playerName)
        ).findFirst().ifPresent(client::setCameraEntity);
    }

    public boolean switchToTarget() {
        if (targetEntity != null) {
            if (targetEntity.isLiving()) {
                if (targetEntity.isAlive()) {
                    client.setCameraEntity(targetEntity);
                } else {
                    return false;
                }
            } else {
                client.setCameraEntity(targetEntity);
            }
            return true;
        }
        return false;
    }

    public void switchEntity() {
        if (client.getCameraEntity() != null) {
            setTargetEntity();
            if (targetEntity != null)
                client.setCameraEntity(targetEntity);
        } else {
            if (client.player != null) {
                resetCamera();
            }
        }
    }

    public boolean setTargetEntity() {
        if (client.crosshairTarget == null) return false;
        if (client.crosshairTarget.getType().equals(ENTITY)) {
            targetEntity = ((EntityHitResult) client.crosshairTarget).getEntity();
            return true;
        }
        return false;
    }

    public void resetCamera() {
        client.setCameraEntity(client.player);
    }

}
