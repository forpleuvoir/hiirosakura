package moe.forpleuvoir.hiirosakura.functional.customradialmenu.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.customradialmenu.CustomRadialMenuManager
import moe.forpleuvoir.hiirosakura.ui.widget.ScrollbarColumn
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.InlineStyleText
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigManagerDefaults
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Button
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlatButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IconButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.ibukigourd.ui.sokitsu.menu.DropdownMenu
import moe.forpleuvoir.ibukigourd.ui.sokitsu.menu.DropdownMenuItem
import moe.forpleuvoir.ibukigourd.ui.sokitsu.menu.dropdownMenuAnchor
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip

/** 菜单列表的排版常量。 */
private object MenuPaneDefaults {

    /** 「添加菜单项」按钮与列表之间的间距。 */
    val ButtonGap: Dp = 12.dp
}

/** 菜单项里图标的倍率。 */
private const val MenuIconScale = 2

/**
 * 左列：菜单列表 + 新建入口。
 *
 * 菜单项与配置页的分组导航同款（[FlatButton] + 导航项四态素材，选中项把 `focused` 当常态底色）；
 * 列表与滚动条并列，滚动条不叠在列表上。
 */
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
    val menus = CustomRadialMenuManager.customRadialMenus.entries.toList()

    Column(modifier) {
        Button(onClick = onNewMenu, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Add)
            Spacer(Modifier.width(8.dp))
            Text(component = HSLang.CustomRadialMenu.add)
        }

        Spacer(Modifier.height(MenuPaneDefaults.ButtonGap))

        if (menus.isEmpty()) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(component = IGLang.Misc.hasNothing, color = SokitsuTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            val scrollState = rememberScrollState()
            Row(Modifier.weight(1f).fillMaxWidth()) {
                Column(
                    modifier = Modifier.weight(1f).fillMaxHeight().verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(ConfigManagerDefaults.GroupSpacing),
                ) {
                    menus.forEach { (key, _) ->
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
                ScrollbarColumn(scrollState)
            }
        }
    }
}

/**
 * 单个菜单项：名称一行 + 尾部一个打开操作菜单的按钮。
 *
 * @param name 菜单名（含内联样式）
 * @param isSelected 是否当前选中
 */
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
    var menuAnchorBounds by remember { mutableStateOf(Rect.Zero) }
    val sprites = ConfigManagerDefaults.navItemSprite()

    FlatButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().tooltip { Text(component = InlineStyleText(name)) },
        sprite = if (isSelected) sprites.copy(normal = sprites.focused) else sprites,
        minSize = ConfigManagerDefaults.NavItemMinSize,
        contentPadding = ConfigManagerDefaults.NavItemPadding,
        contentAlignment = Alignment.CenterStart,
    ) {
        // 单行标签：不折行、不省略 —— 固有宽度恒等于文本宽度，面板宽度因此贴合最宽的菜单名
        Text(
            component = InlineStyleText(name),
            modifier = Modifier.weight(1f),
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Clip,
        )

        // 操作入口排在名称之后的同一行内：选中底色因此铺满整行，而不是"名称一块、按钮另一块"
        Box(Modifier.dropdownMenuAnchor { menuAnchorBounds = it }) {
            IconButton(
                onClick = { showMenu = true },
                modifier = Modifier.tooltip { Text(component = HSLang.CustomRadialMenu.setting) },
            ) {
                Icon(Icons.Menu, scale = MenuIconScale)
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                anchorBounds = menuAnchorBounds,
            ) {
                DropdownMenuItem(
                    onClick = { showMenu = false; onEditSetting() },
                    leadingIcon = { Icon(Icons.Setting, scale = MenuIconScale) },
                ) {
                    Text(component = HSLang.CustomRadialMenu.setting)
                }

                DropdownMenuItem(
                    onClick = { showMenu = false; onRename() },
                    leadingIcon = { Icon(Icons.Edit, scale = MenuIconScale) },
                ) {
                    Text(component = HSLang.CustomRadialMenu.editName)
                }

                DropdownMenuItem(
                    onClick = { showMenu = false; onDelete() },
                    leadingIcon = { Icon(Icons.Delete, scale = MenuIconScale) },
                ) {
                    Text(component = HSLang.CustomRadialMenu.delete)
                }
            }
        }
    }
}
