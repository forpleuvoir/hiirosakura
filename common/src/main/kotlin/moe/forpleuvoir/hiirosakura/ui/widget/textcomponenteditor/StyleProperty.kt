package moe.forpleuvoir.hiirosakura.ui.widget.textcomponenteditor

import androidx.compose.runtime.Immutable

@Immutable
sealed interface StyleProperty<out T> {

    data object Unset : StyleProperty<Nothing>

    data class Set<out T>(val value: T) : StyleProperty<T>

    data object None : StyleProperty<Nothing>
}

fun <T> StyleProperty<T>.merge(new: StyleProperty<@UnsafeVariance T>): StyleProperty<T> =
    when (new) {
        StyleProperty.Unset -> this
        is StyleProperty.Set -> new
        StyleProperty.None -> StyleProperty.None
    }

fun StyleProperty<Boolean>.normalized(): StyleProperty<Boolean> =
    when (this) {
        is StyleProperty.Set -> if (value) this else StyleProperty.None
        else -> this
    }
