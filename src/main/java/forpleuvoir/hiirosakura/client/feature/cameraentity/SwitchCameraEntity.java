package forpleuvoir.hiirosakura.client.feature.cameraentity;

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

    public void switchEntity() {
        if (client.getCameraEntity() != null) {
            if (client.getCameraEntity().equals(client.player)) {
                setTargetEntity();
                if (targetEntity != null)
                    client.setCameraEntity(targetEntity);
            } else {
                resetCamera();
                targetEntity = null;
            }
        } else {
            if (client.player != null) {
                resetCamera();
            }
        }
    }

    public void setTargetEntity() {
        if (client.crosshairTarget == null) return;
        if (client.crosshairTarget.getType().equals(ENTITY)) {
            targetEntity = ((EntityHitResult) client.crosshairTarget).getEntity();
        }
    }

    public void resetCamera() {
        client.setCameraEntity(client.player);
    }

}
