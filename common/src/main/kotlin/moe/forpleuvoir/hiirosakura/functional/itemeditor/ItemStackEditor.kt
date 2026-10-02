package moe.forpleuvoir.hiirosakura.functional.itemeditor

import moe.forpleuvoir.hiirosakura.ui.modifier.vanillaTooltip
import moe.forpleuvoir.ibukigourd.util.mc
import net.minecraft.world.item.TooltipFlag

import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import moe.forpleuvoir.ibukigourd.ui.sokitsu.hoverHighlight
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDialogTitle
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentField
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentSection
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentWrappers
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentWrappers.DataComponentWrapper
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.hiirosakura.ui.widget.ItemBrowser
import moe.forpleuvoir.hiirosakura.ui.widget.ItemIconButton
import moe.forpleuvoir.hiirosakura.ui.widget.LabeledFieldDefaults
import moe.forpleuvoir.hiirosakura.ui.widget.truncateLines
import moe.forpleuvoir.hiirosakura.util.*
import moe.forpleuvoir.hiirosakura.ui.widget.serializereditor.SerializeElementEditor
import moe.forpleuvoir.hiirosakura.ui.widget.serializereditor.SerializeElementType
import moe.forpleuvoir.hiirosakura.ui.widget.serializereditor.matchesType
import moe.forpleuvoir.ibukigourd.util.NebulaOps
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.sokitsu.toast.ToastHandler
import moe.forpleuvoir.ibukigourd.util.toComposeColor
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
import moe.forpleuvoir.ibukigourd.ui.configwrapper.EnumSelector
import moe.forpleuvoir.ibukigourd.ui.sokitsu.VerticalFlatScroller
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Surface
import moe.forpleuvoir.ibukigourd.ui.sokitsu.SurfaceDefaults
import moe.forpleuvoir.ibukigourd.ui.sokitsu.rememberScrollerAdapter

private val logger = logger("ItemStackEditor")

/** 物品类型格里图标与它悬停底色之间的内边距。 */
private val itemTypeIconPadding = 4.dp

@Composable
fun ItemStackEditor(
    value: ItemStack = ItemStackMatcher.handheldItemStack ?: ItemStack(Items.MELON),
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    onValueChange: (ItemStack) -> Unit,
) {
    var editingItem by remember { mutableStateOf(value) }
    FlexibleDialog(
        modifier = modifier.padding(24.dp).size(1300.dp, 1050.dp),
        onDismissRequest = onDismissRequest,
        onConfirmRequest = {
            onValueChange(editingItem)
            true
        },
        title = {
            DataComponentDialogTitle(component = HSLang.ItemEditor.title)
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
                ItemPreview(editingItem, modifier = Modifier.weight(0.75f))
                ItemType(item, { item = it }, modifier = Modifier.weight(0.75f))
                ItemCount(count, { count = it }, dataComponents.maxCount, modifier = Modifier.weight(0.5f))
                ComponentAdder(dataComponents, modifier = Modifier.weight(1.75f))
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
) = DataComponentSection(
    modifier = modifier,
    title = { Text(component = HSLang.ItemEditor.itemType, fontSize = LabeledFieldDefaults.labelFontSize) },
    labelIndent = 0.dp,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        var showDialog by remember { mutableStateOf(false) }

        val typeInteraction = remember { MutableInteractionSource() }
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .hoverHighlight(typeInteraction)
                    .clickable(typeInteraction, indication = null) { showDialog = true }
                    .padding(itemTypeIconPadding),
            ) {
                ItemIcon(ItemStack(value), modifier = Modifier, showTooltip = false, scaleOnHover = 1f)
            }
            Spacer(Modifier.width(8.dp))
            Text(value.value().name, modifier = Modifier.weight(1f), overflow = TextOverflow.Ellipsis, maxLines = 1)
        }

        if (showDialog) {
            FlexibleDialog(
                onDismissRequest = { showDialog = false },
                onConfirmRequest = { true },
                content = {
                    ItemBrowser(
                        itemDisplay = {
                            ItemIconButton(it) { selected ->
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
    DataComponentSection(
        modifier = modifier,
        title = {
            Row {
                Text(component = HSLang.ItemEditor.itemCount, fontSize = LabeledFieldDefaults.labelFontSize)
                Spacer(Modifier.width(8.dp))
                Text("1..$maxValue", fontSize = LabeledFieldDefaults.labelFontSize)
            }
        },
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

    DataComponentSection(
        modifier = modifier,
        title = { Text(component = HSLang.ItemEditor.itemPreview, fontSize = LabeledFieldDefaults.labelFontSize) },
        labelIndent = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .hoverable(interactionSource)
                .vanillaTooltip(
                    value.getTooltipLines(
                        Item.TooltipContext.of(mc.level),
                        mc.player,
                        if (mc.options.advancedItemTooltips) TooltipFlag.ADVANCED else TooltipFlag.NORMAL,
                    ),
                    style = value.get(DataComponents.TOOLTIP_STYLE),
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ItemIcon(
                value,
                modifier = Modifier,
                showCount = true,
                showTooltip = false,
                scaleOnHover = 1f,
                countStyle = TextStyle(
                    color = Colors.WHITE.toComposeColor(),
                    fontSize = 14.sp,
                    shadow = Shadow(Colors.BLACK.alpha(0.5f).toComposeColor(), Offset(3f, 3f), blurRadius = 1f)
                )
            )
            Spacer(Modifier.width(8.dp))
            Text(value.styledHoverName, overflow = TextOverflow.Ellipsis, maxLines = 1)
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

    DataComponentSection(
        modifier = modifier,
        title = { Text(component = HSLang.ItemEditor.addItemComponent, fontSize = LabeledFieldDefaults.labelFontSize) },
    ) {
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
                        Text(Component.literal(identifier.toString()), color = color, fontSize = LabeledFieldDefaults.labelFontSize)
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
        title = { DataComponentDialogTitle(Component.literal(type.keyOrUnknown(registryAccess!!).toString())) },
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
) {
    val lazyListState = rememberLazyListState()
    Surface(
        modifier = Modifier.fillMaxSize(),
        sprite = SurfaceDefaults.embeddedPanel,
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(12.dp),
        ) {
            Box(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                contentAlignment = Alignment.TopStart,
            ) {
                if (components.isEmpty()) {
                    Text(component = IGLang.Misc.hasNothing, color = SokitsuTheme.colorScheme.onSurfaceVariant)
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize(),
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
                }
            }

            Spacer(Modifier.width(8.dp))

            VerticalFlatScroller(
                adapter = rememberScrollerAdapter(lazyListState),
                modifier = Modifier.fillMaxHeight(),
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
