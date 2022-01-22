package forpleuvoir.hiirosakura.client.feature.event.events.api

import forpleuvoir.hiirosakura.client.feature.event.events.api.impl.ClientPlayerApiImpl
import net.minecraft.client.network.ClientPlayerEntity

/**
 * 客户端玩家对外接口

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.feature.event.events.api

 * 文件名 ClientPlayerApi

 * 创建时间 2022/1/21 14:52

 * @author forpleuvoir

 */
interface ClientPlayerApi {

	companion object {
		@JvmStatic
		fun getInstance(player: ClientPlayerEntity): ClientPlayerApi {
			return ClientPlayerApiImpl(player)
		}
	}

	/**
	 * 获取玩家 UUID
	 *
	 * @return UUID
	 */
	val uuid: String

	/**
	 * 获取玩家名
	 *
	 * @return 玩家名
	 */
	val name: String

	/**
	 * 获取生命值
	 *
	 * @return 生命值
	 */
	val health: Float

	/**
	 * 获取玩家最大生命值
	 * @return 最大生命值
	 */
	val maxHealth: Float

	/**
	 * 获取当前生命值百分比
	 * @return 生命值百分比
	 */
	val healthProgress: Double

	/**
	 * 获取主手物品注册id
	 * @return 注册id 例 minecraft:melon
	 */
	val mainHandItemRegisterId: String

	/**
	 * 获取主手物品翻译key
	 * @return 翻译key
	 */
	val mainHandItemTranslationKey: String

	/**
	 * 获取助手物品显示名
	 * @return 现实的名字
	 */
	val mainHandItemDisplayName: String

	/**
	 * 获取主手物品的耐久值
	 * @return 耐久值
	 */
	val mainHandItemDurable: Int

	/**
	 * 获取主手物品的最大耐久值
	 * @return 耐久值
	 */
	val mainHandItemMaxDurable: Int

	/**
	 * 获取主手物品剩余耐久百分比
	 * @return 耐久百分比
	 */
	val mainHandItemDurableProgress: Double

	/**
	 * 是否免疫火焰
	 *
	 * @return 免疫火焰状态
	 */
	val isFireImmune: Boolean

	/**
	 * 是否处于着火状态
	 *
	 * @return 着火状态
	 */
	val isOnFire: Boolean

	/**
	 * 是否接触水或在淋雨
	 *
	 * @return 接触水或在淋雨状态
	 */
	val isTouchingWaterOrRain: Boolean

	/**
	 * 是否处于潜行状态
	 *
	 * @return 潜行状态
	 */
	val isSneaking: Boolean

	/**
	 * 是否正在游泳
	 *
	 * @return 游泳状态
	 */
	val isSwimming: Boolean

	/**
	 * 是否隐形
	 *
	 * @return 隐形状态
	 */
	val isInvisible: Boolean

	/**
	 * 获取玩家位置
	 *
	 * @return 数组 0：X坐标 ，1：Y坐标，2：Z坐标
	 */
	val position: DoubleArray

	/**
	 * 获取玩家 X 坐标
	 *
	 * @return X 坐标
	 */
	val posX: Double

	/**
	 * 获取玩家 Y 坐标
	 *
	 * @return Y 坐标
	 */
	val posY: Double

	/**
	 * 获取玩家 Z 坐标
	 *
	 * @return Z 坐标
	 */
	val posZ: Double

	/**
	 * 获取X轴旋转
	 *
	 * @return Pitch
	 */
	val pitch: Float

	/**
	 * 获取Y轴旋转
	 *
	 * @return Yaw
	 */
	val yaw: Float

	/**
	 * 获取玩家速度
	 *
	 * @return 速度
	 */
	val speed: Float

	/**
	 * 获取玩家经验等级
	 *
	 * @return 经验等级
	 */
	val experienceLevel: Int

	/**
	 * 获取玩家经验总量
	 *
	 * @return 经验总量
	 */
	val totalExperience: Int

	/**
	 * 获取玩家经验 升级进度百分比
	 *
	 * @return 升级进度百分比
	 */
	val experienceProgress: Float
}