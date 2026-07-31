package moe.forpleuvoir.hiirosakura.ui.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.ui.icon.default.Cancel
import moe.forpleuvoir.hiirosakura.ui.icon.default.CheckCircle
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.toast.ToastHandler

fun showErrorToast(message: String?) {
    ToastHandler.showContent {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Cancel, null)
            Text(message ?: "unknown error")
        }
    }
}

fun showSuccessToast(message: String?) {
    ToastHandler.showContent {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.CheckCircle, null)
            Text(message ?: HSLang.Common.success.plainText)
        }
    }
}