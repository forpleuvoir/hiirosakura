package moe.forpleuvoir.hiirosakura.ui.widget.serializereditor.internal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun rememberExpandState(default: Boolean = true): MutableState<Boolean> {
    return remember { mutableStateOf(default) }
}

@Composable
fun rememberExpandState(key: Any, default: Boolean = true): MutableState<Boolean> {
    return remember(key) { mutableStateOf(default) }
}