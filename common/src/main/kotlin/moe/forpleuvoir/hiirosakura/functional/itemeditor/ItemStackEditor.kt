package moe.forpleuvoir.hiirosakura.functional.itemeditor

import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.isActive
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentWrappers
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentWrappers.DataComponentWrapper
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.hiirosakura.ui.widget.ItemBrowser
import moe.forpleuvoir.hiirosakura.ui.widget.ItemBrowserDefaults
import moe.forpleuvoir.hiirosakura.ui.widget.OutlinedLabelBox
import moe.forpleuvoir.hiirosakura.ui.widget.truncateLines
import moe.forpleuvoir.hiirosakura.util.*
import moe.forpleuvoir.hiirosakura.ui.widget.serializereditor.SerializeElementEditor
import moe.forpleuvoir.hiirosakura.ui.widget.serializereditor.SerializeElementType
import moe.forpleuvoir.hiirosakura.ui.widget.serializereditor.matchesType
import moe.forpleuvoir.ibukigourd.util.NebulaOps
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import moe.forpleuvoir.ibukigourd.ui.sokitsu.toast.ToastHandler
import moe.forpleuvoir.ibukigourd.util.toComposeColor
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.common.color.Colors
import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.component.DataComponents
import net.minecraft.core.component.PatchedDataComponentMap
import net.minecraft.core.registries.Registries
import net.minecraft.locale.Language
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.selector.Selector
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IntField
import moe.forpleuvoir.ibukigourd.ui.item.ItemIcon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IconButton
import moe.forpleuvoir.hiirosakura.ui.widget.EnumSelector
import moe.forpleuvoir.ibukigourd.ui.sokitsu.VerticalScroller
import moe.forpleuvoir.ibukigourd.ui.sokitsu.rememberScrollerAdapter
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Button

private val logger = logger("ItemStackEditor")

@Composable
fun ItemStackEditor(
    value: ItemStack = ItemStackMatcher.handheldItemStack ?: ItemStack(Items.MELON),
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    onValueChange: (ItemStack) -> Unit,
) {
    var editingItem by remember { mutableStateOf(value) }
    FlexibleDialog(
        modifier = modifier.padding(24.dp).size(1300.dp, 900.dp),
        onDismissRequest = onDismissRequest,
        onConfirmRequest = {
            onValueChange(editingItem)
            true
        },
        title = {
            Text(component = HSLang.ItemEditor.title)
        },
        content = {
            var item by remember { mutableStateOf(value.typeHolder()) }

            var count by remember { mutableStateOf(value.count) }

            val dataComponents = remember {
                ObservableDataComponentMap(value.copy().components.let {
                    it as? PatchedDataComponentMap ?: PatchedDataComponentMap(it)
                })
            }
            LaunchedEffect(item, count, dataComponents.revision) {
                editingItem = ItemStack(item, count, dataComponents.delegate.copy())
            }


            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ItemPreview(editingItem, modifier = Modifier.height(66.5.dp).weight(1f))
                ItemType(item, { item = it }, modifier = Modifier.height(66.5.dp).weight(1f))
                ItemCount(count, { count = it }, dataComponents.maxCount, modifier = Modifier.height(66.5.dp).weight(0.5f))
                ComponentAdder(dataComponents, modifier = Modifier.height(66.5.dp).weight(1.75f))
            }
            Spacer(Modifier.height(12.dp))
            Components(dataComponents)
        }
    )
}

@Composable
private fun ItemType(
    value: Holder<Item>,
    onValueChange: (Holder<Item>) -> Unit,
    modifier: Modifier = Modifier,
) = OutlinedLabelBox(
    modifier = modifier,
    label = { Text(component = HSLang.ItemEditor.itemType) },
    contentPadding = PaddingValues(16.dp, 8.dp, 8.dp, 8.dp),
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        var showDialog by remember { mutableStateOf(false) }

        ItemIcon(ItemStack(value), modifier = Modifier, showTooltip = false, scaleOnHover = 1f)
        Spacer(Modifier.width(8.dp))
        Text(value.value().name, modifier = Modifier.weight(1f), overflow = TextOverflow.Ellipsis, maxLines = 1)
        Spacer(Modifier.width(8.dp))
        IconButton(onClick = { showDialog = true }) {
            Icon(Icons.Edit)
        }


        if (showDialog) {
            FlexibleDialog(
                onDismissRequest = { showDialog = false },
                onConfirmRequest = { true },
                content = {
                    ItemBrowser(
                        itemDisplay = {
                            ItemBrowserDefaults.ItemWrapper(it) { selected ->
                                @Suppress("DEPRECATION")
                                onValueChange(selected.asItem().builtInRegistryHolder())
                                showDialog = false
                            }
                        },
                        modifier = Modifier.size(680.dp, 520.dp)
                    )
                },
                confirmButton = {},
                dismissButton = {}
            )
        }
    }
}

@Composable
fun ItemCount(
    value: Int,
    onValueChange: (Int) -> Unit,
    maxValue: Int,
    modifier: Modifier = Modifier
    ) {
        OutlinedLabelBox(
            label = {
                Row {
                    Text(component = HSLang.ItemEditor.itemCount)
                    Spacer(Modifier.width(8.dp))
                    Text("1..${maxValue}")
                }
            },
            modifier = modifier,
        ) {
            IntField(
                value,
                onValueChange,
                valueRange = 1..maxValue,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }

@Composable
fun ItemPreview(
    value: ItemStack,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    val hovered by interactionSource.collectIsHoveredAsState()
    OutlinedLabelBox(
        modifier = modifier.hoverable(interactionSource),
        label = { Text(component = HSLang.ItemEditor.itemPreview) },
        contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 8.dp),
    ) {

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxHeight()) {
            ItemIcon(
                value,
                modifier = Modifier,
                showCount = true,
                showTooltip = true,
                scaleOnHover = 1f,
                countStyle = TextStyle(
                    color = Colors.WHITE.toComposeColor(),
                    fontSize = 14.sp,
                    shadow = Shadow(Colors.BLACK.alpha(0.5f).toComposeColor(), Offset(3f, 3f), blurRadius = 1f)
                )
            )
            Spacer(Modifier.width(8.dp))
            Text(value.hoverName, overflow = TextOverflow.Ellipsis, maxLines = 1)
        }
    }
}


@Suppress("UNCHECKED_CAST")
@Composable
private fun ComponentAdder(
    dataComponents: ObservableDataComponentMap,
    modifier: Modifier = Modifier,
) {
    val registryManager = registryAccess!!

    val components =
        registryManager.lookupOrThrow(Registries.DATA_COMPONENT_TYPE).sortedBy { it.key(registryManager) } - dataComponents.keySet()

    var selected by remember { mutableStateOf(components.first()) }

    var buildingType by remember { mutableStateOf<DataComponentType<*>?>(null) }

        OutlinedLabelBox(label = {
            Text(component = HSLang.ItemEditor.addItemComponent)
        }) {
        Selector(
                selected = selected,
                onSelect = { type ->
                    selected = type
                    runCatching {
                        DataComponentWrappers.defaultValue(type)?.let {
                            if (!dataComponents.delegate.has(type)) {
                                dataComponents[type as DataComponentType<Any>] = it
                            } else {
                                ToastHandler.showContent { Text(component = HSLang.ItemEditor.itemComponentExist(type.keyOrUnknown(registryManager))) }
                            }
                        } ?: run {
                            buildingType = type
                        }
                    }.onFailure {
                        ToastHandler.showContent {
                            Text(it.stackTraceToString(), maxLines = 16, overflow = TextOverflow.Ellipsis, color = Colors.RED.toComposeColor())
                        }
                        DataComponentWrappers.log.error(it)
                    }
                },
                items = components,
                
                modifier = modifier,
                searchFilter = { type, str ->
                    val id = type.keyOrUnknown(registryManager)
                    id.toString().contains(str) || id.asTranslateText().plainText.contains(str)
                },
                content = {
                    Text(it.keyOrUnknown(registryManager).toString())
                },
                itemContent = { type, _ ->
                    val isAdapted = DataComponentWrappers.isAdaptedComponent(type)
                    val color = if (isAdapted)
                        Color.fromHSV(195f / 360f, 1f, 1f).toComposeColor()
                    else
                        Color.fromHSV(5f / 360f, .6f, 1f).toComposeColor()
                    Column {
                        val identifier = type.keyOrUnknown(registryManager)
                        Text(
                            identifier = identifier,
                            color = color
                        )
                        val hasTranslation = Language.getInstance().has(identifier.asTranslateKey())
                        if (hasTranslation) {
                            Text(Component.literal(identifier.toString()), color = color, fontSize = SokitsuTheme.typography.body.fontSize)
                        }
                    }
                }
            )
    }

    buildingType?.let { type ->
        ComponentBuilderDialog(
            type = type as DataComponentType<Any>,
            onDismiss = { buildingType = null },
            onValueChange = { component ->
                if (!dataComponents.delegate.has(type)) {
                    dataComponents[type] = component
                } else {
                    ToastHandler.showContent {
                        Text(component = HSLang.ItemEditor.itemComponentExist(type.keyOrUnknown(registryManager)))
                    }
                }
                buildingType = null
            },
        )
    }
}

@Suppress("UNCHECKED_CAST")
@Composable
private fun <C : Any> ComponentBuilderDialog(
    type: DataComponentType<C>,
    onDismiss: () -> Unit,
    onValueChange: (C) -> Unit,
) {
    var rootType by remember { mutableStateOf(SerializeElementType.Object) }

    var data by remember { mutableStateOf(SerializeElementType.Object.defaultValue) }

    FlexibleDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.padding(24.dp),
        title = { Text(Component.literal(type.keyOrUnknown(registryAccess!!).toString())) },
        onConfirmRequest = {
            runCatching {
                type.codecOrThrow()
                    .parse(registryAccess!!.createSerializationContext(NebulaOps), data)
                    .orThrow
            }.fold(
                onSuccess = { result ->
                    onValueChange(result)
                    true
                },
                onFailure = { e ->
                    logger.error(e)
                    ToastHandler.showContent {
                        Text(e.stackTraceToString().truncateLines(8))
                    }
                    false
                }
            )
        },
        content = {
            Column {
                EnumSelector(
                    selected = rootType,
                    onSelect = { newType ->
                        rootType = newType
                        if (!data.matchesType(newType)) {
                            data = newType.defaultValue.deepCopy()
                        }
                    },
                    items = SerializeElementType.entries,
                    label = { Text(component = HSLang.ItemEditor.rootType) },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))
                SerializeElementEditor(
                    data = data,
                    onDataChange = { data = it },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        },
    )
}

@Suppress("UNCHECKED_CAST")
@Composable
private fun Components(
    components: ObservableDataComponentMap
) = OutlinedLabelBox(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(16.dp, 16.dp, 8.dp, 16.dp),
    label = {
        Text(component = HSLang.ItemEditor.dataComponents)
    }
) {
    val lazyListState = rememberLazyListState()
    if (components.isEmpty()) {
        Text(component = IGLang.Misc.hasNothing, color = SokitsuTheme.colorScheme.onSurfaceVariant)
    } else {
        val canScroll = lazyListState.canScrollBackward || lazyListState.canScrollForward
        Box {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(end = if (canScroll) 12.dp else 8.dp).fillMaxSize(),
                state = lazyListState
            ) {
                items(
                    components.keySet().sortedBy { it.keyOrUnknown(registryAccess!!) },
                    key = { type -> type.keyOrUnknown(registryAccess!!) }
                ) { type ->
                    components[type]?.let { component ->
                        DataComponentWrapper(
                            type,
                            component,
                            removeAction = {
                                components.remove(type)
                            }
                        ) {
                            components[type as DataComponentType<Any>] = it
                        }
                    }
                }
            }

            VerticalScroller(
                adapter = rememberScrollerAdapter(lazyListState),
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight()
            )
        }
    }
}


@Stable
private class ObservableDataComponentMap(
    val delegate: PatchedDataComponentMap
) {

    var revision by mutableLongStateOf(0L)
        private set

    var maxCount by mutableIntStateOf(readMaxCount())
        private set

    fun isEmpty(): Boolean {
        revision
        return delegate.isEmpty
    }

    operator fun <T : Any> get(type: DataComponentType<T>): T? {
        revision
        return delegate[type]
    }

    operator fun <T : Any> set(
        type: DataComponentType<T>,
        component: T,
    ) {
        delegate[type] = component
        notifyChanged()
    }

    fun <T : Any> remove(type: DataComponentType<T>): T? {
        val removed = delegate.remove(type)
        notifyChanged()
        return removed
    }

    fun keySet(): Set<DataComponentType<*>> {
        revision
        return delegate.keySet()
    }

    private fun notifyChanged() {
        maxCount = readMaxCount()
        revision++
    }

    private fun readMaxCount(): Int {
        return delegate[DataComponents.MAX_STACK_SIZE] ?: 64
    }

}