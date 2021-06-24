package forpleuvoir.hiirosakura.client.feature.cameraentity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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
    private ClientPlayerEntity player;
    private Entity targetEntity;
    private ClientWorld world;
    private final MinecraftClient client;


    public SwitchCameraEntity(MinecraftClient client) {
        this.client = client;
    }

    public List<AbstractClientPlayerEntity> getPlayers() {
        return world.getPlayers();
    }

    public List<String> getPlayersSuggest() {
        List<String> playerNames = new LinkedList<>();
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
            if (client.getCameraEntity().equals(player)) {
                setTargetEntity();
                if (targetEntity != null)
                    client.setCameraEntity(targetEntity);
            } else {
                resetCamera();
                targetEntity = null;
            }
        } else {
            if (player != null) {
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
        client.setCameraEntity(player);
    }

    public void setPlayer(ClientPlayerEntity player) {
        this.player = player;
    }

    public void setWorld(ClientWorld world) {
        this.world = world;
    }
}
