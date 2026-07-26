@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")

package moe.forpleuvoir.hiirosakura.functional.itemeditor

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.hiirosakura.util.key
import moe.forpleuvoir.hiirosakura.util.logger
import moe.forpleuvoir.hiirosakura.util.registryAccess
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.Texts
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.default.Add
import moe.forpleuvoir.ibukigourd.ui.icon.default.ContentCopy
import moe.forpleuvoir.ibukigourd.ui.icon.default.EditNote
import moe.forpleuvoir.ibukigourd.ui.platformcontext.MinecraftClipboard
import moe.forpleuvoir.ibukigourd.ui.preset.DragHandle
import moe.forpleuvoir.ibukigourd.ui.preset.ItemIcon
import moe.forpleuvoir.ibukigourd.ui.preset.RemoveConfirmButton
import moe.forpleuvoir.ibukigourd.ui.preset.Text
import moe.forpleuvoir.ibukigourd.ui.preset.modifier.plainTooltip
import moe.forpleuvoir.ibukigourd.ui.toast.ToastHandler
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.common.color.Colors
import net.minecraft.core.RegistryAccess
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.StringTag
import net.minecraft.nbt.StringTagVisitor
import net.minecraft.nbt.Tag
import net.minecraft.world.item.ItemStack
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.util.Map
import java.util.regex.Pattern

private val logger = logger("ItemStackManagerGui")

@Composable
fun ItemEditorManagerUI(
    registryAccess: RegistryAccess,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp),
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally
) {
    Column(
        modifier = modifier,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment
    ) {
        val deferred = ItemStackManager.loadDataAsync(registryAccess)
        var loaded by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            deferred.await()
            loaded = true
        }


        ToolBar()

        ItemStackList(loaded)
    }
}

@Composable
private fun ToolBar(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp, Alignment.End),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment
    ) {
        ItemStackMatcher.handheldItemStack?.let { stack ->
            Button({
                //TODO 打开物品编辑器
            }) {
                Text(HSLang.ItemEditor.addFromHandheldItem)
            }
        }
        IconButton({
            //TODO 打开物品编辑器
        }) {
            Icon(Icons.Add, null)
        }
    }
}

@Composable
private fun ItemStackList(
    loaded: Boolean,
    filter: (ItemStack) -> Boolean = { true },
) {
    if (!loaded) {
        Text(HSLang.ItemEditor.loading)
    } else {
        if (ItemStackManager.items.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(IGLang.Misc.hasNothing, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            val lazyListState = rememberLazyListState()
            val hapticFeedback = LocalHapticFeedback.current
            val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
                ItemStackManager.moveElement(from.index, to.index)
                hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
            }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize(), state = lazyListState) {
                itemsIndexed(
                    ItemStackManager.items,
                    key = { _, keyed -> keyed.key }
                ) { index, (key, item) ->
                    if (filter(item)) {
                        ReorderableItem(reorderableLazyListState, key) { isDragging ->
                            val scale by animateFloatAsState(if (isDragging) 1.015f else 1.0f)
                            val handleInteraction = remember { MutableInteractionSource() }
                            val handleHovered by handleInteraction.collectIsHoveredAsState()
                            Card(
                                modifier = Modifier.fillMaxWidth().scale(scale)
                            ) {
                                Row(
                                    Modifier.padding(12.dp).fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        DragHandle(
                                            hapticFeedback,
                                            handleInteraction,
                                            handleHovered,
                                            isDragging
                                        )
                                        ItemIcon(item, showCount = true, scaleOnHover = 1f)
                                        Text(item.styledHoverName)
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        //获取到背包
                                        if (mc.player?.isCreative == true) {
                                            Button({
                                                mc.player?.inventory?.add(item.copy())
                                                ToastHandler.showContent {
                                                    Text(HSLang.ItemEditor.getToBackpackSuccess(item.hoverName))
                                                }
                                            }) {
                                                Text(HSLang.ItemEditor.getToBackpack)
                                            }
                                        }
                                        //复制微指令
                                        IconButton(
                                            onClick = {
                                                runCatching {
                                                    val command = item.asCommand()
                                                    MinecraftClipboard.setClipboardText(command)
                                                    ToastHandler.showContent {
                                                        Text(command, modifier = Modifier.widthIn(max = 720.dp).heightIn(max = 400.dp))
                                                    }
                                                }.onFailure { throwable ->
                                                    ToastHandler.showContent {
                                                        Text(Texts.literal("Error : ${throwable.message}").withColor(Colors.RED.argb))
                                                    }
                                                    logger.error(throwable)
                                                }
                                            },
                                            modifier = Modifier
                                                .plainTooltip { Text(HSLang.ItemEditor.copyToCommand) }
                                        ) {
                                            Icon(Icons.ContentCopy, null)
                                        }
                                        //编辑
                                        IconButton({
                                            //TODO编辑 物品
                                        }) {
                                            Icon(Icons.EditNote, null)
                                        }

                                        RemoveConfirmButton(
                                            HSLang.ItemEditor.removeConfirm.plainText,
                                            { ItemStackManager.removeAt(index) }
                                        ) {
                                            ItemIcon(item, showCount = true, scaleOnHover = 1f)
                                            Text(item.hoverName)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }
}


fun ItemStack.asCommand(): String {
    val tag = DataComponentPatch.CODEC
        .encodeStart(registryAccess!!.createSerializationContext(NbtOps.INSTANCE), componentsPatch)
        .orThrow
        .getAsString()
    return "/give @p ${item.key}$tag $count"
}

private fun <T : Tag> T.getAsString(): String {
    return if (this is CompoundTag) {
        CommandNbtWriter().apply {
            visitCompound(this@getAsString as CompoundTag)
        }.build()
    } else {
        this.toString()
    }
}

private class CommandNbtWriter : StringTagVisitor() {
    override fun visitCompound(tag: CompoundTag) {
        this.builder.append('[')
        val list = ArrayList(tag.entrySet())
        list.sortWith(Map.Entry.comparingByKey())

        for (i in list.indices) {
            val entry = list[i]
            if (i != 0) {
                this.builder.append(',')
            }

            val key = entry.key
            if (!key.equals("true", ignoreCase = true) && !key.equals("false", ignoreCase = true) && Pattern.compile("[A-Za-z._]+[A-Za-z0-9._+-]*")
                    .matcher(key)
                    .matches()
            ) {
                this.builder.append(key)
            } else {
                buildString {
                    StringTag.quoteAndEscape(key, this)
                }.apply {
                    builder.append(this.trim('"'))
                }
            }
            this.builder.append('=')
            val sub = StringTagVisitor()
            entry.value.accept(sub)
            this.builder.append(sub.builder)
        }

        this.builder.append(']')
    }
}