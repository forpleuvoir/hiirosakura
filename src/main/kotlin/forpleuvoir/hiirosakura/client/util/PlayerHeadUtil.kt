package forpleuvoir.hiirosakura.client.util

import com.mojang.authlib.GameProfile
import net.minecraft.block.entity.SkullBlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtString

/**
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.util
 *
 * #class_name PlayerHeadUtil
 *
 * #create_time 2021/6/11 21:20
 */
object PlayerHeadUtil {
	private const val OWNER = "SkullOwner"

	/**
	 * 通过玩家名称获取头颅
	 * @param playerName 玩家名称
	 * @return [ItemStack] [Items.PLAYER_HEAD]
	 */
	@JvmStatic
	fun getPlayerHead(playerName: String): ItemStack {
		val stack = ItemStack(Items.PLAYER_HEAD)
		val tag = NbtCompound()
		tag.put(OWNER, NbtString.of(playerName))
		stack.nbt = tag
		return stack
	}

	fun getPlayerHead(player: PlayerEntity): ItemStack {
		return getPlayerHead(player.entityName)
	}

	fun equals(stack: ItemStack, headStack: ItemStack): Boolean {
		return getSkullOwner(stack) == getSkullOwner(headStack)
	}

	fun getPlayerName(stack: ItemStack): String {
		if (stack.hasNbt()) {
			stack.nbt?.let {
				if (it.contains(OWNER, 8)) {
					return it.getString(OWNER)
				}
			}
		}
		return ""
	}

	fun getSkullOwner(stack: ItemStack): String {
		if (stack.item === Items.PLAYER_HEAD && stack.hasNbt()) {
			var string: String? = null
			val compoundTag = stack.nbt!!
			if (compoundTag.getString(OWNER).isEmpty()) {
				if (compoundTag.contains(OWNER, 8)) {
					string = compoundTag.getString(OWNER)
				} else if (compoundTag.contains(OWNER, 10)) {
					val compoundTag2 = compoundTag.getCompound(OWNER)
					if (compoundTag2.contains("Name", 8)) {
						string = compoundTag2.getString("Name")
					}
				}
				string?.let { return it }
			} else {
				return compoundTag.getString(OWNER)
			}
		}
		return ""
	}

	fun loadProperties(gameProfile: GameProfile?, callback: (GameProfile) -> Unit) {
		SkullBlockEntity.loadProperties(gameProfile, callback)
	}
}