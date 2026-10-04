@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")

package moe.forpleuvoir.hiirosakura.functional.itemeditor

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.hiirosakura.ui.HSUiDefaults
import moe.forpleuvoir.hiirosakura.ui.util.LocalRegistryAccess
import moe.forpleuvoir.hiirosakura.ui.widget.PagePanel
import moe.forpleuvoir.hiirosakura.ui.widget.ScrollbarColumn
import moe.forpleuvoir.hiirosakura.ui.widget.hsItemAnimation
import moe.forpleuvoir.hiirosakura.util.key
import moe.forpleuvoir.hiirosakura.util.logger
import moe.forpleuvoir.hiirosakura.util.registryAccess
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.Texts
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import moe.forpleuvoir.ibukigourd.ui.editdialog.DragHandle
import moe.forpleuvoir.ibukigourd.ui.item.ItemIcon
import moe.forpleuvoir.ibukigourd.ui.editdialog.RemoveConfirmButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.ibukigourd.ui.sokitsu.toast.ToastHandler
import moe.forpleuvoir.ibukigourd.ui.util.copyValue
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.common.color.Colors
import net.minecraft.client.Minecraft
import net.minecraft.client.player.LocalPlayer
import net.minecraft.core.RegistryAccess
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.nbt.*
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.util.Map
import java.util.regex.Pattern
import moe.forpleuvoir.ibukigourd.ui.sokitsu.hoverHighlight
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigManagerDefaults
import moe.forpleuvoir.ibukigourd.ui.configwrapper.configIconScale
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogContentDefaults
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IconButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Button
import moe.forpleuvoir.hiirosakura.ui.util.rememberClipboardWriter

private val logger = logger("ItemStackManagerGui")

/** 列表行内边距。 */
private val itemRowPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)

/** 相邻两行之间的间距。 */
private val itemRowSpacing = 2.dp

/**
 * 物品编辑器页：工具栏（新增入口）与物品列表各坐一张内嵌面板，与其它页同款骨架。
 */
@Composable
fun ItemEditorManagerUI(
    registryAccess: RegistryAccess,
    modifier: Modifier = Modifier,
) {
    CompositionLocalProvider(LocalRegistryAccess provides registryAccess) {
        val deferred = ItemStackManager.loadDataAsync(registryAccess)
        var loaded by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            deferred.await()
            loaded = true
        }
        DisposableEffect(Unit) {
            onDispose {
                @Suppress("DeferredResultUnused")
                ItemStackManager.saveDataAsync(registryAccess)
            }
        }

        PagePanel(
            modifier = modifier.fillMaxSize().padding(ConfigManagerDefaults.ContentPadding),
            toolbar = { ToolBar() },
        ) {
            ItemStackList(loaded)
        }
    }
}

@Composable
private fun ToolBar(
    modifier: Modifier = Modifier.fillMaxWidth(),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp, Alignment.End),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment
    ) {
        var editingItem by remember { mutableStateOf<ItemStack?>(null) }
        ItemStackMatcher.handheldItemStack?.let { stack ->
            Button({
                editingItem = stack
            }) {
                Icon(Icons.Add, scale = HSUiDefaults.ICON_SCALE)
                Spacer(Modifier.width(8.dp))
                Text(component = HSLang.ItemEditor.addFromHandheldItem)
            }
        }
        Button({
            editingItem = ItemStack(Items.MELON)
        }) {
            Icon(Icons.Add, scale = HSUiDefaults.ICON_SCALE)
            Spacer(Modifier.width(8.dp))
            Text(component = IGLang.Misc.add)
        }

        editingItem?.let { item ->
            ItemStackEditor(
                item,
                { editingItem = null },
                onValueChange = {
                    ItemStackManager.add(it)
                }
            )
        }
    }
}

@Composable
private fun ItemStackList(
    loaded: Boolean,
    filter: (ItemStack) -> Boolean = { true },
) {
    if (!loaded) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(component = HSLang.ItemEditor.loading, color = SokitsuTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    if (ItemStackManager.items.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(component = IGLang.Misc.hasNothing, color = SokitsuTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    val registryAccess = LocalRegistryAccess.current
    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        ItemStackManager.moveElement(from.index, to.index)
    }
    var editingItemIndex by remember { mutableStateOf<Int?>(null) }

    Row(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxSize(),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(itemRowSpacing),
        ) {
            itemsIndexed(
                ItemStackManager.items,
                key = { _, keyed -> keyed.key }
            ) { index, (key, item) ->
                if (filter(item)) {
                    ReorderableItem(
                        reorderableLazyListState, key,
                        animateItemModifier = hsItemAnimation(),
                    ) { _ ->
                        // 拖拽手势只能在 ReorderableItem 的作用域里取，因此在调用点算好再传进去
                        ItemStackRow(
                            item = item,
                            registryAccess = registryAccess,
                            dragHandleModifier = Modifier.draggableHandle(),
                            onEdit = { editingItemIndex = index },
                            onRemove = { ItemStackManager.removeAt(index) },
                        )
                    }
                }
            }
        }

        ScrollbarColumn(lazyListState)
    }
    editingItemIndex?.let { index ->
        ItemStackEditor(
            ItemStackManager.items[index].value,
            { editingItemIndex = null },
            onValueChange = {
                ItemStackManager.items[index].copyValue(it)
            }
        )
    }
}

/**
 * 单条物品：拖拽手柄、图标与名称在左，动作按钮在右。
 *
 * 行不画容器，悬停时由 [hoverHighlight] 铺一层高亮（与配置行、事件行同款反馈）。
 *
 * @param dragHandleModifier 拖拽手势修饰符，由调用方在 [ReorderableItem] 作用域内取好后传入
 */
@Composable
private fun ItemStackRow(
    item: ItemStack,
    registryAccess: RegistryAccess,
    dragHandleModifier: Modifier,
    onEdit: () -> Unit,
    onRemove: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val copyToClipboard = rememberClipboardWriter()
    val iconScale = configIconScale()

    Box(Modifier.fillMaxWidth().hoverHighlight(interactionSource)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .hoverable(interactionSource)
                .padding(itemRowPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DragHandle(
                modifier = dragHandleModifier,
                iconScale = iconScale,
                contentPadding = EditDialogContentDefaults.iconPadding,
            )

            ItemIcon(item, showCount = true, scaleOnHover = 1f)

            Text(
                item.styledHoverName,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            // 获取到背包
            if (mc.player?.isCreative == true) {
                IconButton(
                    onClick = {
                        mc.player?.addCreativeItem(item).let {
                            if (!it.isNullOrEmpty()) {
                                ToastHandler.showContent {
                                    Text(component = HSLang.ItemEditor.getToBackpackSuccess(item.hoverName))
                                }
                            }
                        }
                    },
                    modifier = Modifier.tooltip { Text(component = HSLang.ItemEditor.getToBackpack) },
                ) {
                    Icon(Icons.Import, scale = iconScale)
                }
            }

            // 复制为指令
            IconButton(
                onClick = {
                    runCatching {
                        val command = item.asCommand(registryAccess)
                        copyToClipboard(command)
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
                modifier = Modifier.tooltip { Text(component = HSLang.ItemEditor.copyToCommand) },
            ) {
                Icon(Icons.Copy, scale = iconScale)
            }

            IconButton(onEdit, modifier = Modifier.tooltip { Text(component = IGLang.Misc.edit) }) {
                Icon(Icons.Edit, scale = iconScale)
            }

            RemoveConfirmButton(
                HSLang.ItemEditor.removeConfirm.plainText,
                onRemove,
                modifier = Modifier.tooltip { Text(component = IGLang.Misc.remove) },
                iconScale = iconScale,
                contentPadding = EditDialogContentDefaults.iconPadding,
            ) {
                ItemIcon(item, showCount = true, scaleOnHover = 1f)
                Text(item.hoverName)
            }
        }
    }
}


fun ItemStack.asCommand(registryAccess: RegistryAccess): String {
    val tag = DataComponentPatch.CODEC
        .encodeStart(registryAccess.createSerializationContext(NbtOps.INSTANCE), componentsPatch)
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

/**
 * 以创造模式向玩家背包添加物品，并将发生变化的槽位同步到服务端。
 *
 * @return 发生变化的玩家背包槽位。快捷栏为 0..8，主背包为 9..35。
 */
fun LocalPlayer.addCreativeItem(stack: ItemStack): List<Int> {
    val gameMode = Minecraft.getInstance().gameMode ?: return emptyList()

    val before = (0 until 36).map { slot ->
        inventory.getItem(slot).copy()
    }

    // add 可能同时修改多个槽位，所以不能依赖其返回值判断是否有槽位变化
    inventory.add(stack.copy())

    return (0 until 36)
        .filter { slot ->
            !ItemStack.matches(before[slot], inventory.getItem(slot))
        }
        .onEach { inventorySlot ->
            val menuSlot = when (inventorySlot) {
                in 0..8 -> 36 + inventorySlot
                in 9..35 -> inventorySlot
                else -> return@onEach
            }

            gameMode.handleCreativeModeItemAdd(
                inventory.getItem(inventorySlot).copy(),
                menuSlot,
            )
        }
}
