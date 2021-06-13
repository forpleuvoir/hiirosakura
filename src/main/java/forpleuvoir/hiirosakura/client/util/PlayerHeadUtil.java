package forpleuvoir.hiirosakura.client.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import java.util.function.Consumer;

/**
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.util
 * <p>#class_name PlayerHeadUtil
 * <p>#create_time 2021/6/11 21:20
 */
public class PlayerHeadUtil {
    public static final String OWNER="SkullOwner";

    /**
     * 通过玩家名称获取头颅
     * @param playerName 玩家名称
     * @return {@link ItemStack} {@link Items#PLAYER_HEAD}
     */
    public static ItemStack getPlayerHead(String playerName) {
        ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
        NbtCompound tag = new NbtCompound();
        tag.put("SkullOwner", NbtString.of(playerName));
        stack.setTag(tag);
        return stack;
    }

    public static ItemStack getPlayerHead(PlayerEntity player) {
        return getPlayerHead(player.getEntityName());
    }

    public static boolean equals(ItemStack stack, ItemStack headStack) {
        return getSkullOwner(stack).equals(getSkullOwner(headStack));
    }

    public static String getPlayerName(ItemStack stack) {
        if (stack.hasTag()) {
            assert stack.getTag() != null;
            if (stack.getTag().contains("SkullOwner", 8)) {
                return stack.getTag().getString("SkullOwner");
            }
        }
        return "";
    }

    public static ItemEntity getItemEntity(ServerWorld world, PlayerEntity player) {
        return new ItemEntity(world, player.getX(), player.getY(), player.getZ(), PlayerHeadUtil.getPlayerHead(player.getEntityName()));
    }

    public static ItemEntity getItemEntity(ServerWorld world, Vec3d pos, String name) {
        return new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), PlayerHeadUtil.getPlayerHead(name));
    }

    public static String getSkullOwner(ItemStack stack) {
        if (stack.getItem() == Items.PLAYER_HEAD && stack.hasTag()) {
            String string = null;
            NbtCompound compoundTag = stack.getTag();
            assert compoundTag != null;
            if (compoundTag.getString(OWNER).isEmpty()) {
                if (compoundTag.contains(OWNER, 8)) {
                    string = compoundTag.getString(OWNER);
                } else if (compoundTag.contains(OWNER, 10)) {
                    NbtCompound compoundTag2 = compoundTag.getCompound(OWNER);
                    if (compoundTag2.contains("Name", 8)) {
                        string = compoundTag2.getString("Name");
                    }
                }
                if (string != null) {
                    return string;
                }
            } else {
                return compoundTag.getString(OWNER);
            }
        }
        return "";
    }

    public static void loadProperties(GameProfile gameProfile, Consumer<GameProfile> callback) {
        SkullBlockEntity.loadProperties(gameProfile, callback);
    }

}
