package moe.forpleuvoir.hiirosakura.test

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfoMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.hiirosakura.ui.compat.closeScreen
import moe.forpleuvoir.hiirosakura.ui.compat.openComposeScreen
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.blockinfo.BlockInfoMatcherEditorDialog
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.itemstack.ItemStackMatcherEditorDialog

fun openBlockMacherEditor() = openComposeScreen {
    var matcher by remember { mutableStateOf(BlockInfoMatcher.targetBlockMatcher) }
    BlockInfoMatcherEditorDialog({ closeScreen() }, matcher, { matcher = it })
}


fun openItemMacherEditor() = openComposeScreen {

    var matcher by remember { mutableStateOf(ItemStackMatcher.handheldItemMatcher) }
    ItemStackMatcherEditorDialog({ closeScreen() }, matcher, { matcher = it })
}
