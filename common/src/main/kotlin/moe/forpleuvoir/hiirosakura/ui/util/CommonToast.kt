package moe.forpleuvoir.hiirosakura.ui.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.sokitsu.toast.ToastHandler

fun showErrorToast(message: String?) {
    ToastHandler.showContent {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("!")
            Text(message ?: "unknown error")
        }
    }
}

fun showSuccessToast(message: String?) {
    ToastHandler.showContent {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("OK")
            Text(message ?: HSLang.Common.success.plainText)
        }
    }
}