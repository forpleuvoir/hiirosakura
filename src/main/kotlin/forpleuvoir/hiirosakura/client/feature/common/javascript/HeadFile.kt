package forpleuvoir.hiirosakura.client.feature.common.javascript

import com.google.common.io.CharStreams
import forpleuvoir.hiirosakura.client.HiiroSakuraClient
import forpleuvoir.hiirosakura.client.util.HSLogger
import forpleuvoir.ibuki_gourd.common.IModInitialize
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.ResourceType
import net.minecraft.util.Identifier
import javax.script.ScriptEngine

/**
 *

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.feature.common.javascript

 * 文件名 HeadFile

 * 创建时间 2022/1/19 14:18

 * @author forpleuvoir

 */
object HeadFile : IModInitialize {
	private val log = HSLogger.getLogger(this.javaClass)

	override fun initialize() {
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(object : SimpleSynchronousResourceReloadListener {
			override fun reload(manager: ResourceManager) {
				headFiles.clear()
				for (id in manager.findResources("headfile") { it.endsWith(".js") }) {
					try {
						val reader = manager.getResource(id).inputStream.reader()
						headFiles.add(CharStreams.toString(reader))
					} catch (e: Exception) {
						log.error("Error occurred while loading resource js $id", e)
					}
				}
			}

			override fun getFabricId(): Identifier {
				return Identifier(HiiroSakuraClient.modId, "headfile")
			}
		})
	}

	private val headFiles = ArrayList<String>()

	fun eval(engine: ScriptEngine) {
		headFiles.forEach(engine::eval)
	}


}