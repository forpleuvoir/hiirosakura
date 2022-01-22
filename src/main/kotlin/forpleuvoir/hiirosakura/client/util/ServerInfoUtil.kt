package forpleuvoir.hiirosakura.client.util


/**
 * 服务器信息工具类
 *
 * @author forpleuvoir
 *
 * 项目名 hiirosakura
 *
 * 包名 forpleuvoir.hiirosakura.client.util
 *
 * 文件名 ServerInfoUtil
 *
 * 创建时间 2021-07-26 15:41
 */
object ServerInfoUtil {
	@JvmStatic
	var name: String? = null
		private set
	@JvmStatic
	var address: String? = null
		private set
	@JvmStatic
	var lastServerName: String? = null
		private set
	@JvmStatic
	var lastServerAddress: String? = null
		private set
	var disConnectCounter = 0
		private set

	@JvmStatic
	fun clear() {
		name = null
		address = null
	}

	@JvmStatic
	fun setValue(name: String?, address: String?) {
		ServerInfoUtil.name = name
		ServerInfoUtil.address = address
		lastServerName = name
		lastServerAddress = address
		disConnectCounter = 0
	}

	fun disconnect() {
		disConnectCounter++
	}
}