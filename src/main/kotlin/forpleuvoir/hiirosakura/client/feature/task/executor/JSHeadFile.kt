package forpleuvoir.hiirosakura.client.feature.task.executor

import forpleuvoir.hiirosakura.client.HiiroSakuraClient
import forpleuvoir.hiirosakura.client.common.IInitialized
import forpleuvoir.hiirosakura.client.config.Configs
import forpleuvoir.hiirosakura.client.util.FileUtil
import forpleuvoir.hiirosakura.client.util.HSLogger.Companion.getLogger
import forpleuvoir.hiirosakura.client.util.StringUtil.isEmptyString
import java.io.File


/**
 * JavaScript头文件

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.feature.task.executor

 * 文件名 JSHeadFile

 * 创建时间 2021-08-18 12:29

 * @author forpleuvoir

 */
object JSHeadFile : IInitialized {
	@Transient
	private val log = getLogger(JSHeadFile::class.java)
	private val DEFAULT_SCRIPT: String = """
		var ${'$'}TimeTask = Java.type('forpleuvoir.hiirosakura.client.feature.task.TimeTask');
		var ${'$'}TimeTaskData = Java.type('forpleuvoir.hiirosakura.client.feature.task.TimeTaskData');
		var ${'$'}TimeTaskHandler = Java.type('forpleuvoir.hiirosakura.client.feature.task.TimeTaskHandler').INSTANCE;
		function ${'$'}sendMessage(message){${'$'}hs.sendChatMessage(message);}
		function ${'$'}attack(){
		    if(arguments.length == 1){
		        ${'$'}hs.attack(arguments[0]);
		    }else{
		        ${'$'}hs.doAttack();
		    }
		}
		function ${'$'}msg(msg){
			${'$'}sendMessage(msg);
		}
		function ${'$'}cmd(cmd){
			if(cmd.startsWith("/")){
				${'$'}msg(cmd);
			}else{
				${'$'}msg("/" + cmd);
			}
		}
		function ${'$'}use(){
		    if(arguments.length == 1){
		        ${'$'}hs.use(arguments[0]);
		    }else{
		        ${'$'}hs.doItemUse();
		    }
		}
		function ${'$'}pick	(){
		    if(arguments.length == 1){
		        ${'$'}hs.pickItem(arguments[0]);
		    }else{
		        ${'$'}hs.doItemPick();
		    }
		}
		function ${'$'}sneak(tick){${'$'}hs.sneak(tick);}
		function ${'$'}jump(tick){${'$'}hs.jump(tick);}
		function ${'$'}move(dir,tick){
		    switch(dir){
		        case 'forward':${'$'}hs.forward(tick);break;
		        case 'back':${'$'}hs.back(tick);break;
		        case 'left':${'$'}hs.left(tick);break;
		        case 'right':${'$'}hs.right(tick);break;
		    }
		}
		function ${'$'}joinServer(){
		    if(arguments.length == 1){
		        ${'$'}hs.joinServer(arguments[0]);
		    }else if(arguments.length == 2){
		        ${'$'}hs.joinServer(arguments[0],arguments[1]);
		    }
		}
		function ${'$'}newTimeTask(executor,data){
			return new ${'$'}TimeTask(data,executor)
		}
		function ${'$'}addTask(executor,data){
		    ${'$'}TimeTaskHandler.addTask(${'$'}newTimeTask(executor,data));
		}
		function ${'$'}getTaskData(name,startTime,cycles,cyclesTime){
		    return new ${'$'}TimeTaskData(name,startTime,cycles,cyclesTime);
		}
		function ${'$'}onceTask(executor,name,startTime){
			${'$'}TimeTaskHandler.addTask(${'$'}newTimeTask(executor,${'$'}getTaskData(name,startTime,1,0)));
		}
		
	""".trimIndent()
	private val PATH = Configs.CONFIG_FILE_PATH
	private val HEAD_FILE = File(PATH, HiiroSakuraClient.MOD_ID + "_head.js")
	private var content: String? = null

	override fun initialize() {
		if (!read()) {
			createFile()
		}
	}

	private fun createFile() {
		try {
			FileUtil.createFile(HEAD_FILE)
			FileUtil.writeFile(HEAD_FILE, DEFAULT_SCRIPT)
		} catch (e: Exception) {
			log.error(e.message!!, e)
		}
	}

	fun openFile() {
		try {
			FileUtil.openFile(HEAD_FILE)
		} catch (e: Exception) {
			log.error(e.message!!, e)
		}
	}


	fun read(): Boolean {
		return if (HEAD_FILE.isFile && HEAD_FILE.canRead() && HEAD_FILE.exists()) {
			try {
				content = FileUtil.readFile(HEAD_FILE)
				return true
			} catch (e: Exception) {
				log.error(e.message!!, e)
				false
			}
		} else
			false
	}

	fun getDefault():String{
		return DEFAULT_SCRIPT
	}

	fun getContent(): String {
		return if (content.isEmptyString()) DEFAULT_SCRIPT else content!!
	}

}