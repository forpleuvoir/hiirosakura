package forpleuvoir.hiirosakura.client.util


/**
 * 服务器信息工具类
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.util
 *
 * #class_name ServerInfoUtil
 *
 * #create_time 2021-07-26 15:41
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