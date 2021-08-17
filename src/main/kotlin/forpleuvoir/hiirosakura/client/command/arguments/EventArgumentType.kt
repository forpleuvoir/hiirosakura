package forpleuvoir.hiirosakura.client.command.arguments

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import forpleuvoir.hiirosakura.client.feature.event.base.Event
import forpleuvoir.hiirosakura.client.feature.event.base.HiiroSakuraEvents
import forpleuvoir.hiirosakura.client.util.StringUtil
import java.util.concurrent.CompletableFuture

/**
 * 事件参数类型
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.command.arguments
 *
 * #class_name EventArgumentType
 *
 * #create_time 2021-07-23 15:02
 */
class EventArgumentType : ArgumentType<String> {
	@Throws(CommandSyntaxException::class)
	override fun parse(reader: StringReader): String {
		val type = reader.readString()
		return if (HiiroSakuraEvents.events.containsKey(type)) {
			type
		} else {
			throw ERROR.create()
		}
	}

	override fun <S> listSuggestions(
		context: CommandContext<S>,
		builder: SuggestionsBuilder
	): CompletableFuture<Suggestions> {
		for (key in HiiroSakuraEvents.events.keys) {
			if (key.startsWith(builder.remaining)) builder.suggest(key)
		}
		return builder.buildFuture()
	}

	override fun getExamples(): Collection<String> {
		return HiiroSakuraEvents.events.keys
	}

	companion object {
		val ERROR = SimpleCommandExceptionType(
			StringUtil.translatableText("command.arg.event.error")
		)

		@JvmStatic
		fun eventType(): EventArgumentType = EventArgumentType()


		@JvmStatic
		fun getEventType(context: CommandContext<*>, name: String?): Class<out Event?> {
			return HiiroSakuraEvents.events[context.getArgument(name, String::class.java)]!!
		}
	}
}