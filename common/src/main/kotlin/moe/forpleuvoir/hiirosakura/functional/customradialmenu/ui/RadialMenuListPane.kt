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
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IconButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Surface
import androidx.compose.ui.graphics.RectangleShape
import moe.forpleuvoir.ibukigourd.ui.sokitsu.VerticalScroller
import moe.forpleuvoir.ibukigourd.ui.sokitsu.rememberScrollerAdapter
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Button
import moe.forpleuvoir.hiirosakura.ui.compat.FilledTonalButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.menu.DropdownMenu
import moe.forpleuvoir.ibukigourd.ui.sokitsu.menu.DropdownMenuItem
import androidx.compose.ui.geometry.Rect
import androidx.compose.foundation.clickable
import moe.forpleuvoir.ibukigourd.ui.sokitsu.menu.dropdownMenuAnchor

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
            Icon(Icons.Add)
            Spacer(Modifier.width(4.dp))
            Text(component = HSLang.CustomRadialMenu.add)
        }

        Spacer(Modifier.height(12.dp))

        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (CustomRadialMenuManager.customRadialMenus.isEmpty()) {
                Text(component = IGLang.Misc.hasNothing, color = SokitsuTheme.colorScheme.onSurfaceVariant)
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

                VerticalScroller(
                    adapter = rememberScrollerAdapter(lazyListState),
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }
    }
}

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
                SokitsuTheme.colorScheme.secondary
            else
                SokitsuTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        } else if (isSelected) SokitsuTheme.colorScheme.secondary.copy(alpha = 0.85f)
        else SokitsuTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        animationSpec = tween(200),
        label = "RadialMenuListItem"
    )
    Row(
        modifier = Modifier
            .hoverable(interactionSource)
            .clip(RectangleShape)
            .background(backgroundColor)
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
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
            var menuAnchorBounds by remember { mutableStateOf(Rect.Zero) }
            Box(modifier = Modifier.dropdownMenuAnchor { menuAnchorBounds = it }) {
            IconButton(onClick = { showMenu = true }, Modifier.tooltip { Text(component = HSLang.CustomRadialMenu.setting) }) {
                Icon(Icons.Menu)
            }


            val menus = remember {
                listOf(
                    MenuOption(
                        text = { Text(component = HSLang.CustomRadialMenu.setting) },
                        icon = { Icon(Icons.Setting) },
                        onClick = { showMenu = false; onEditSetting() },
                    ),
                    MenuOption(
                        text = { Text(component = HSLang.CustomRadialMenu.editName) },
                        icon = { Icon(Icons.Edit) },
                        onClick = { showMenu = false; onRename() },
                    ),
                    MenuOption(
                        text = { Text(component = HSLang.CustomRadialMenu.delete) },
                        icon = { Icon(Icons.Delete) },
                        onClick = { showMenu = false; onDelete() },
                    ),
                )
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                anchorBounds = menuAnchorBounds,
            ) {
                menus.forEach { option ->
                    DropdownMenuItem(
                        onClick = option.onClick,
                        leadingIcon = option.icon,
                    ) {
                        option.text()
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