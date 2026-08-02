package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEditorDefaults
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEntryRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.hiirosakura.ui.widget.EnchatmentHelper
import moe.forpleuvoir.hiirosakura.ui.widget.EnchatmentHelper.enchantmentDescription
import moe.forpleuvoir.hiirosakura.ui.widget.EnchatmentSelector
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.text.appendTranslate
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.defaults.Add
import moe.forpleuvoir.ibukigourd.ui.icon.defaults.EditNote
import moe.forpleuvoir.ibukigourd.ui.platformcontext.IGCompositionLocalProvider
import moe.forpleuvoir.ibukigourd.ui.preset.*
import moe.forpleuvoir.ibukigourd.ui.preset.modifier.fabVisibilityAnimation
import moe.forpleuvoir.ibukigourd.ui.preset.modifier.plainTooltip
import moe.forpleuvoir.ibukigourd.ui.preset.state.rememberFabVisibilityByScroll
import moe.forpleuvoir.ibukigourd.ui.preset.state.rememberHideActionState
import net.minecraft.core.Holder
import net.minecraft.resources.Identifier
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.ItemEnchantments

@Composable
fun ItemEnchantmentsComponentWrapper(
    key: Identifier,
    value: ItemEnchantments,
    onValueChange: (ItemEnchantments) -> Unit,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp, Alignment.End),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) = DataComponentEntryRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    Box(modifier = Modifier.height(DataComponentEditorDefaults.entrySize.height).padding(vertical = 4.dp)) {
        var showDialog by remember { mutableStateOf(false) }

        val tip = if (value.size() > 0) {
            Modifier.plainTooltip {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    value.entrySet().take(20).forEach { (holder, i) ->
                        Text(Literal("").append(enchantmentDescription(holder)).append(" ").appendTranslate("enchantment.level.$i", i.toString()))
                    }
                    if (value.size() > 20) {
                        Text("...")
                    }
                }
            }
        } else Modifier

        AssistChip(
            {},
            modifier = Modifier
                .fillMaxHeight()
                .then(tip)
                .width(DataComponentEditorDefaults.entrySize.width),
            label = {
                Text(IGLang.ConfigWrapper.listConfigWrapperText(value.enchantments.size), overflow = TextOverflow.Ellipsis, maxLines = 1)
            },
            trailingIcon = {
                IconButton(onClick = {
                    showDialog = true
                }) {
                    Icon(Icons.EditNote, null)
                }
            }
        )

        if (showDialog) {
            ItemEnchantmentsComponentEditDialog(
                value,
                onValueChange,
                key,
                { Text(key) },
                { showDialog = false }
            )
        }

    }
}

@Composable
fun ItemEnchantmentsComponentEditDialog(
    value: ItemEnchantments,
    onValueChange: (ItemEnchantments) -> Unit,
    key: Identifier,
    title: @Composable () -> Unit,
    onDismissRequest: () -> Unit,
) {
    val editingEnchantments = remember { value.enchantments.map { it.key to it.value }.toMutableStateList() }

    fun otherEnchantments(
        enchantment: Holder<Enchantment>?,
    ): List<Holder<Enchantment>> {
        val editing = editingEnchantments
            .mapTo(mutableSetOf()) { it.first }
        return EnchatmentHelper.REGISTERED_ENCHANTMENT
            .filter { it == enchantment || it !in editing }
    }

    FlexibleDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        modifier = Modifier
            .padding(24.dp)
            .size(800.dp, 720.dp),
        onConfirmRequest = {
            onValueChange(ItemEnchantments(Object2IntOpenHashMap<Holder<Enchantment>>().apply {
                editingEnchantments.forEach { (key, value) ->
                    set(key, value)
                }
            }))
            true
        },
        content = {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

                val lazyListState = rememberLazyListState()
                if (editingEnchantments.isEmpty()) {
                    Text(IGLang.Misc.hasNothing, color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    val canScroll = lazyListState.canScrollBackward || lazyListState.canScrollForward
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(end = if (canScroll) 12.dp else 0.dp).fillMaxSize(),
                        state = lazyListState
                    ) {
                        itemsIndexed(
                            editingEnchantments,
                            key = { _, entry -> entry.first }
                        ) { index, (enchantment, level) ->
                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    Modifier.padding(12.dp).fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {

                                    EnchatmentSelector(
                                        enchantment,
                                        onSelect = {
                                            editingEnchantments[index] = editingEnchantments[index].copy(first = it)
                                        },
                                        items = otherEnchantments(enchantment),
                                        modifier = Modifier.weight(1f),
                                        searchFilter = { str, entry ->
                                            entry.registeredName.contains(str) || enchantmentDescription(entry).plainText.contains(str)
                                        },
                                        label = {
                                            Text(key, suffix = "enchantment", fallback = "enchantment")
                                        }
                                    )
                                    CompositionLocalProvider(LocalNumberFieldStyle provides NumberFieldStyle.Outlined) {
                                        IntField(
                                            level,
                                            {
                                                editingEnchantments[index] = editingEnchantments[index].copy(second = it)
                                            },
                                            range = 1..255,
                                            labelPosition = TextFieldLabelPosition.Attached(true),
                                            label = {
                                                Text(key, suffix = "level", fallback = "level")
                                            },
                                            modifier = Modifier.height(68.dp).width(120.dp),
                                        )
                                    }


                                    RemoveConfirmButton(
                                        enchantment.value().description.plainText,
                                        { editingEnchantments.removeAt(index) }
                                    )
                                }
                            }
                        }
                    }

                    VerticalScrollbar(
                        adapter = rememberScrollbarAdapter(lazyListState),
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )

                }
                var showAddDialog by remember { mutableStateOf(false) }

                FloatingActionButton(
                    onClick = {
                        otherEnchantments(null).firstOrNull()?.let {
                            showAddDialog = true
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .size(40.dp)
                        .fabVisibilityAnimation(
                            rememberFabVisibilityByScroll(lazyListState),
                            shouldHide = rememberHideActionState() && editingEnchantments.size == EnchatmentHelper.REGISTERED_ENCHANTMENT.size
                        )
                ) {
                    Icon(Icons.Add, IGLang.Misc.add.plainText)
                }

                if (showAddDialog) {
                    var level by remember { mutableStateOf(1) }
                    var enchantment by remember { mutableStateOf(otherEnchantments(null).first()) }
                    SimpleAlertDialog(
                        onDismissRequest = { showAddDialog = false },
                        onConfirmRequest = {
                            editingEnchantments.addLast(enchantment to level)
                            true
                        },
                        title = { Text(IGLang.Misc.add) },
                        content = {
                            IGCompositionLocalProvider(LocalNumberFieldStyle provides NumberFieldStyle.Outlined) {
                                Row {
                                    EnchatmentSelector(
                                        enchantment,
                                        onSelect = { enchantment = it },
                                        items = otherEnchantments(enchantment),
                                        modifier = Modifier.weight(1f),
                                        searchFilter = { str, entry ->
                                            entry.registeredName.contains(str) || entry.value().description.plainText.contains(str)
                                        },
                                        label = {
                                            Text(key, suffix = "enchantment", fallback = "enchantment")
                                        }
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    IntField(
                                        level,
                                        { level = it },
                                        range = 1..255,
                                        labelPosition = TextFieldLabelPosition.Attached(true),
                                        label = {
                                            Text(key, suffix = "level", fallback = "level")
                                        },
                                        modifier = Modifier.height(68.dp).width(120.dp)
                                    )
                                }
                            }
                        }
                    )

                }

            }
        }
    )

}