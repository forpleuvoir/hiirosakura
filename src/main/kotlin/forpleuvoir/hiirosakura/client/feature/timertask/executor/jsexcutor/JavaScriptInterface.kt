package forpleuvoir.hiirosakura.client.feature.timertask.executor.jsexcutor

/**
 *

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.feature.timertask.executor.jsexcutor

 * 文件名 JavaScriptInterface

 * 创建时间 2022/1/19 14:13

 * @author forpleuvoir

 */
interface JavaScriptInterface {

	companion object {
		@JvmStatic
		fun getInstance(): JavaScriptInterface {
			return JavaScriptInterfaceImpl()
		}
	}


	/**
	 * 模拟按下方向前进按键
	 * @param tick 持续时间
	 */
	fun forward(tick: Int)

	/**
	 * 模拟按下方向后退按键
	 * @param tick 持续时间
	 */
	fun back(tick: Int)

	/**
	 * 模拟按下方向左按键
	 * @param tick 持续时间
	 */
	fun left(tick: Int)

	/**
	 * 模拟按下方向右按键
	 * @param tick 持续时间
	 */
	fun right(tick: Int)

	/**
	 * 模拟按下跳跃按键
	 * @param tick 持续时间
	 */
	fun jump(tick: Int)

	/**
	 * 模拟下潜行键
	 * @param tick 持续时间
	 */
	fun sneak(tick: Int)

	/**
	 * 模拟按住攻击键
	 * @param tick 持续时间
	 */
	fun attack(tick: Int)

	/**
	 * 模拟按住使用键
	 * @param tick 持续时间
	 */
	fun use(tick: Int)

	/**
	 * 模拟器按住鼠标中键
	 * @param tick 持续时间
	 */
	fun pickItem(tick: Int)

	/**
	 * 客户端玩家攻击一次
	 */
	fun doAttack()

	/**
	 * 客户端玩家使用一次物品
	 */
	fun doItemUse()

	/**
	 * 模拟按下鼠标中键
	 */
	fun doItemPick()

	/**
	 * 加入服务器
	 * @param address 服务器ip地址
	 */
	fun joinServer(address: String)

	/**
	 * 加入服务器
	 * @param address 服务器ip地址
	 * @param maxConnect 最大连接次数
	 */
	fun joinServer(address: String, maxConnect: Int)

	/**
	 * 发送聊天消息
	 * @param message 消息文本
	 */
	fun sendChatMessage(message: String)

    /**
     * 发送指令
     * @param command String
     */
    fun sendCommand(command: String)

	/**
	 * 丢弃物品
	 * @param slot Int 丢弃的物品编号
	 * @param all Boolean 是否全部丢弃
	 */
	fun dropItem(slot: Int, all: Boolean)

	/**
	 * 丢弃物品
	 * @param name String 物品的注册ID
	 * @param all Boolean 是否全部丢弃
	 */
	fun dropItem(name: String, all: Boolean)
}