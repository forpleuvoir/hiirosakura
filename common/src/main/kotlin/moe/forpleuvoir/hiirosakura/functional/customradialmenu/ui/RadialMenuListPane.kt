package moe.forpleuvoir.hiirosakura.functional.customradialmenu.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.customradialmenu.CustomRadialMenuManager
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.InlineStyleText
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.defaults.*
import moe.forpleuvoir.ibukigourd.ui.preset.Text
import moe.forpleuvoir.ibukigourd.ui.preset.modifier.plainTooltip

@Composable
fun RadialMenuListPane(
    selectedMenuKey: String?,
    onSelectMenu: (String) -> Unit,
    onNewMenu: () -> Unit,
    onEditSetting: (String) -> Unit,
    onRename: (String) -> Unit,
    onDeleteMenu: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.widthIn(min = 200.dp, max = 300.dp).fillMaxHeight().padding(16.dp)
    ) {
        FilledTonalButton(
            onClick = onNewMenu,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(Icons.Add, null)
            Spacer(Modifier.width(4.dp))
            Text(HSLang.CustomRadialMenu.add)
        }

        Spacer(Modifier.height(12.dp))

        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (CustomRadialMenuManager.customRadialMenus.isEmpty()) {
                Text(IGLang.Misc.hasNothing, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                val lazyListState = rememberLazyListState()
                val canScroll = lazyListState.canScrollBackward || lazyListState.canScrollForward

                LazyColumn(
                    modifier = Modifier.padding(end = if (canScroll) 12.dp else 0.dp).fillMaxSize(),
                    state = lazyListState,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(CustomRadialMenuManager.customRadialMenus.entries.toList(), key = { it.key }) { (key, _) ->
                        RadialMenuListItem(
                            name = key,
                            isSelected = key == selectedMenuKey,
                            onClick = { onSelectMenu(key) },
                            onEditSetting = { onEditSetting(key) },
                            onRename = { onRename(key) },
                            onDelete = { onDeleteMenu(key) },
                        )
                    }
                }

                VerticalScrollbar(
                    adapter = rememberScrollbarAdapter(lazyListState),
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun RadialMenuListItem(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    onEditSetting: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
) {
    var showMenu by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val backgroundColor by animateColorAsState(
        targetValue = if (isHovered) {
            if (isSelected)
                MaterialTheme.colorScheme.secondaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        } else if (isSelected) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.85f)
        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        animationSpec = tween(200),
        label = "RadialMenuListItem"
    )
    Row(
        modifier = Modifier
            .hoverable(interactionSource)
            .clip(MaterialTheme.shapes.medium)
            .background(backgroundColor)
            .onClick { onClick.invoke() }
            .fillMaxWidth()
            .padding(12.dp, 6.dp, 6.dp, 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            InlineStyleText(name),
            modifier = Modifier.weight(1f),
            overflow = TextOverflow.Ellipsis
        )
        Box {
            IconButton(onClick = { showMenu = true }, Modifier.plainTooltip { Text(HSLang.CustomRadialMenu.setting) }) {
                Icon(Icons.MoreVert, null)
            }

            val menus = remember {
                listOf(
                    MenuOption(
                        text = { Text(HSLang.CustomRadialMenu.setting) },
                        icon = { Icon(Icons.Settings, null) },
                        onClick = { showMenu = false; onEditSetting() },
                    ),
                    MenuOption(
                        text = { Text(HSLang.CustomRadialMenu.editName) },
                        icon = { Icon(Icons.EditNote, null) },
                        onClick = { showMenu = false; onRename() },
                    ),
                    MenuOption(
                        text = { Text(HSLang.CustomRadialMenu.delete) },
                        icon = { Icon(Icons.Delete, null) },
                        onClick = { showMenu = false; onDelete() },
                    ),
                )
            }

            DropdownMenuPopup(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
            ) {
                Surface(
                    modifier = Modifier.hoverable(interactionSource = interactionSource),
                    shape = MenuDefaults.groupShape(0, 1).shape,
                    color = MenuDefaults.groupStandardContainerColor,
                    tonalElevation = MenuDefaults.TonalElevation,
                    shadowElevation = MenuDefaults.ShadowElevation,
                ) {
                    Column(modifier = Modifier.padding(0.dp, 4.dp)) {
                        menus.forEachIndexed { index, option ->
                            val shapes = MenuDefaults.itemShape(index, menus.size)
                            DropdownMenuItem(
                                text = option.text,
                                onClick = option.onClick,
                                leadingIcon = option.icon,
                                shape = shapes.shape,
                            )
                        }
                    }
                }
            }
        }
    }
}

private class MenuOption(
    val text: @Composable () -> Unit,
    val icon: @Composable () -> Unit,
    val onClick: () -> Unit,
)