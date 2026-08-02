package moe.forpleuvoir.hiirosakura.ui.widget.matcher.itemstack

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatchEntry
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.hiirosakura.ui.widget.EnchatmentHelper
import moe.forpleuvoir.hiirosakura.ui.widget.EnchatmentSelector
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.BasicMatchEntryEditor
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.LocalMatchEntryInfoHeight
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.MatchEntryModeDisplayer
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.rememberTextFieldStateBinding
import moe.forpleuvoir.hiirosakura.util.allEnchantments
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.defaults.EditNote
import moe.forpleuvoir.ibukigourd.ui.preset.*
import net.minecraft.core.Holder
import net.minecraft.world.item.enchantment.Enchantment

@Composable
fun ItemStackMatchEntryEnchantmentInfo(entry: ItemStackMatchEntry.Enchantment) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(LocalMatchEntryInfoHeight.current)) {
        MatchEntryModeDisplayer(entry.mode)
        Spacer(Modifier.width(8.dp))
        Text(ItemStackMatchEntry.Enchantment.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.width(8.dp))
        Text(entry.asText, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

/**
 * Entry 在 Row 中简单信息展示
 */
@Composable
internal fun ItemStackMatchEntryEnchantmentRow(
    entry: ItemStackMatchEntry.Enchantment,
    onChange: (ItemStackMatchEntry) -> Unit,
    modifier: Modifier = Modifier
) = Row(modifier, verticalAlignment = Alignment.CenterVertically) {
    Text(entry.asText, Modifier.weight(1f, false), overflow = TextOverflow.Ellipsis)
    Spacer(Modifier.width(6.dp))
    var showEditor by remember { mutableStateOf(false) }
    IconButton({ showEditor = true }) {
        Icon(Icons.EditNote, "${IGLang.Misc.edit} ${entry.translateText.plainText}")
    }
    if (showEditor) {
        ItemStackMatchEntryEnchantmentEditorDialog(
            { showEditor = false },
            entry,
            onChange,
        )
    }
}

@Composable
internal fun ItemStackMatchEntryEnchantmentEditorDialog(
    onDismissRequest: () -> Unit,
    value: ItemStackMatchEntry.Enchantment,
    onValueChange: (ItemStackMatchEntry) -> Unit,
) {
    var editingEntry by remember(value) { mutableStateOf(value) }
    FlexibleDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(editingEntry.translateText) },
        content = {
            BasicItemStackMatchEntryEnchantmentEditor(
                value = editingEntry,
                onValueChange = { editingEntry = it },
                modifier = Modifier.width(600.dp)
            )
        },
        onConfirmRequest = {
            onValueChange(editingEntry)
            true
        }
    )
}

@Composable
internal fun BasicItemStackMatchEntryEnchantmentEditor(
    value: ItemStackMatchEntry.Enchantment,
    modifier: Modifier = Modifier,
    onValueChange: (ItemStackMatchEntry.Enchantment) -> Unit,
) {
    BasicMatchEntryEditor(
        mode = value.mode,
        onModeChange = { onValueChange(value.copy(mode = it)) },
        modifier = modifier,
    ) {
        val state = rememberTextFieldStateBinding(value.enchantment) { onValueChange(value.copy(enchantment = it)) }
        var levelRange by remember(value.level) { mutableStateOf(value.level) }
        LaunchedEffect(levelRange) {
            onValueChange(value.copy(level = levelRange))
        }

        //从手中物品获取
        val width = remember { 360.dp }
        val enchantments = remember { ItemStackMatcher.handheldItemStack?.allEnchantments }
        if (!enchantments.isNullOrEmpty()) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(HSLang.Common.getFromHandItem)
                var selected by remember { mutableStateOf(enchantments.keys.first()) }
                EnchatmentSelector(
                    selected,
                    { s ->
                        selected = s
                        state.edit { replace(0, length, s.registeredName) }
                        enchantments[s]?.let { levelRange = it..it }
                    },
                    items = enchantments.keys.toList(),
                    modifier = Modifier.width(width)
                )
            }
        }

        Spacer(Modifier.height(12.dp))
        //从注册表获取
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(HSLang.Common.getFromRegistry)
            var selected: Holder<Enchantment> by remember { mutableStateOf(EnchatmentHelper.REGISTERED_ENCHANTMENT.first()) }
            EnchatmentSelector(
                selected,
                { s ->
                    selected = s
                    state.edit { replace(0, length, s.registeredName) }
                },
                items = EnchatmentHelper.REGISTERED_ENCHANTMENT,
                modifier = Modifier.width(width),
                searchFilter = { s, e ->
                    s in e.registeredName || s in EnchatmentHelper.enchantmentDescription(e).plainText
                }
            )
        }
        Spacer(Modifier.height(12.dp))
        //附魔ID
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(HSLang.ItemStackMatcher.Entry.enchantmentID)
            TipBox({
                Text(EnchatmentHelper.enchantmentDescription(state.text.toString()))
            }) {
                OutlinedTextField(state, modifier = Modifier.width(width))
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(HSLang.ItemStackMatcher.Entry.enchantmentLevelRange)
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.width(width)) {
                CompositionLocalProvider(LocalNumberFieldStyle provides NumberFieldStyle.Outlined) {
                    IntField(
                        levelRange.first,
                        { levelRange = it..levelRange.last },
                        range = 1..Int.MAX_VALUE,
                        modifier = Modifier.weight(1f)
                    )
                    Text("≤..≤", modifier = Modifier.padding(horizontal = 12.dp))
                    IntField(
                        levelRange.last,
                        { levelRange = levelRange.first..it },
                        range = levelRange.first..Int.MAX_VALUE,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

    }
}
