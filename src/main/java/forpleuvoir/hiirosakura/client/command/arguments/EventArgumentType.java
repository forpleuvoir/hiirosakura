package forpleuvoir.hiirosakura.client.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import forpleuvoir.hiirosakura.client.feature.event.base.Event;
import forpleuvoir.hiirosakura.client.feature.event.base.HiiroSakuraEvents;
import forpleuvoir.hiirosakura.client.util.StringUtil;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * 事件参数类型
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.command.arguments
 * <p>#class_name EventArgumentType
 * <p>#create_time 2021-07-23 15:02
 */
public class EventArgumentType implements ArgumentType<String> {
    public static final SimpleCommandExceptionType ERROR = new SimpleCommandExceptionType(
            StringUtil.translatableText("command.arg.event.error"));

    public static EventArgumentType event() {
        return new EventArgumentType();
    }

    public static Class<? extends Event> getEventType(final CommandContext<?> context, final String name) {
        return HiiroSakuraEvents.events.get(context.getArgument(name, String.class));
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        var type = reader.readString();
        if (HiiroSakuraEvents.events.containsKey(type)) {
            return type;
        } else {
            throw ERROR.create();
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for (String key : HiiroSakuraEvents.events.keySet()) {
            if (key.startsWith(builder.getRemaining()))
                builder.suggest(key);
        }
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return HiiroSakuraEvents.events.keySet();
    }
}
