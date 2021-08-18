package forpleuvoir.hiirosakura.client.util

import net.minecraft.util.Util
import java.io.*
import java.lang.Exception
import kotlin.Throws
import java.lang.StringBuilder
import java.nio.charset.StandardCharsets

/**
 * 文件工具类
 *
 * @author forpleuvoir
 *
 * #BelongsProject hiirosakura
 *
 * #BelongsPackage forpleuvoir.hiirosakura.client.util
 *
 * #ClassName FileUtil
 *
 * #CreateTime 2020/10/21 9:47
 *
 * #Description 文件工具类
 */
object FileUtil {
	/**
	 * 加载文件中的内容
	 *
	 * @param path 文件路径
	 * @param name 文件名
	 * @return 内容
	 * @throws Exception [FileNotFoundException] 文件未找到
	 */
	@Throws(Exception::class)
	fun loadFile(path: String, name: String): String {
		val file = File("$path/$name")
		if (!file.exists()) {
			throw FileNotFoundException("文件未找到:$path$name")
		}
		val result = StringBuilder()
		val bufferedReader = BufferedReader(
			InputStreamReader(FileInputStream(file), StandardCharsets.UTF_8)
		)
		var s: String?
		while (bufferedReader.readLine().also { s = it } != null) {
			result.append(System.lineSeparator()).append(s)
		}
		bufferedReader.close()
		return result.toString()
	}

	/**
	 * 加载文件中的内容
	 *
	 * @param file 文件
	 * @return 内容
	 * @throws IOException [IOException] 文件未找到
	 */
	@Throws(IOException::class)
	fun readFile(file: File): String {
		if (!file.exists()) {
			throw FileNotFoundException("文件未找到:" + file.name)
		}
		val result = StringBuilder()
		val bf = BufferedReader(
			InputStreamReader(FileInputStream(file), StandardCharsets.UTF_8)
		)
		var s: String?
		while (bf.readLine().also { s = it } != null) {
			result.append(System.lineSeparator()).append(s)
		}
		bf.close()
		return result.toString()
	}

	/**
	 * 创建文件
	 *
	 * @param path 文件路径
	 * @param name 文件名
	 * @return 成功创建的文件对象
	 * @throws IOException 文件创建失败
	 */
	@Throws(IOException::class)
	fun createFile(path: String, name: String): File {
		val fileDir = File(path)
		if (!fileDir.exists()) {
			fileDir.mkdirs()
		}
		val file = File("$path/$name")
		if (!file.exists()) {
			file.createNewFile()
		}
		return file
	}

	/**
	 * 创建文件
	 *
	 * @param file 文件路径
	 * @return 成功创建的文件对象
	 * @throws IOException 文件创建失败
	 */
	@Throws(IOException::class)
	fun createFile(file: File): File {
		if (!file.parentFile.exists()) {
			file.parentFile.mkdirs()
			if (!file.exists()) {
				file.createNewFile()
			}
		}
		return file
	}

	/**
	 * 将字符串写入文件
	 *
	 * @param file    文件对象
	 * @param content 写入的内容
	 * @return 文件对象
	 * @throws Exception 文件写入失败
	 */
	@Throws(Exception::class)
	fun writeFile(file: File, content: String): File {
		return try {
			val fileWriter = OutputStreamWriter(FileOutputStream(file), StandardCharsets.UTF_8)
			fileWriter.write(content)
			fileWriter.close()
			file
		} catch (e: Exception) {
			createFile(file)
		}
	}

	/**
	 * 打开文件
	 * @param file 需要打开的文件
	 */
	fun openFile(file: File) {
		if (file.isFile && file.exists() && file.canRead()) {
			val uri = file.toURI()
			Util.getOperatingSystem().open(uri)
		}
	}
}