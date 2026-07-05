package moe.forpleuvoir.hiirosakura.test

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfoMatcher
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.blockinfo.BlockInfoMatcherEditorDialog
import moe.forpleuvoir.ibukigourd.ui.openComposePopupScreen
import moe.forpleuvoir.ibukigourd.ui.platformcontext.IbukiGourdTheme

fun openBlockMacherEditor() = openComposePopupScreen {
    IbukiGourdTheme {
        var expanded by remember { mutableStateOf(true) }
        Box(contentAlignment = Alignment.Center) {
            Button(onClick = {
                expanded = true
            }) {
                Text("点我")
            }
        }
        var matcher by remember { mutableStateOf(BlockInfoMatcher.targetBlockMatcher) }
        if (expanded)
            BlockInfoMatcherEditorDialog({ expanded = false }, matcher, { matcher = it })
    }
}

