package forpleuvoir.hiirosakura.client.compat.ibuki_gourd

import forpleuvoir.hiirosakura.client.config.Configs
import forpleuvoir.ibuki_gourd.api.config.IbukiGourdConfigApi
import forpleuvoir.ibuki_gourd.config.IConfigHandler

/**
 *

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.compat.ibuki_gourd

 * 文件名 ConfigImpl

 * 创建时间 2022/2/11 23:18

 * @author forpleuvoir

 */
class ConfigImpl : IbukiGourdConfigApi {
    override fun getConfigHandlerFactory(): () -> IConfigHandler {
        return { Configs }
    }
}