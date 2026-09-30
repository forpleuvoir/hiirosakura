package moe.forpleuvoir.hiirosakura.functional.task.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.ui.widget.ScrollbarColumn
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigManagerDefaults
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlatButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Surface
import moe.forpleuvoir.ibukigourd.ui.sokitsu.SurfaceDefaults
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.resolve

/**
 * 菜单项下标与页面下标一致：任务列表 / 运行中的任务 / 设置。
 */
private const val PAGE_TASKS = 0
private const val PAGE_RUNNING = 1
private const val PAGE_SETTINGS = 2

/** 三个页面的标题，下标即页面下标。 */
private val taskPages by lazy {
    listOf(HSLang.Task.tasks, HSLang.Task.runningTasks, HSLang.Task.settings)
}

/**
 * 任务管理器：左列菜单（三个页面）+ 右列内容，与配置页同一套骨架。
 *
 * 两列各坐在一张内嵌面板上，宽度与内边距直接取配置页的排版量 [ConfigManagerDefaults]，
 * 因此与配置页看起来是一套；两列之间不画分割线（面板自带边框）。
 */
@Composable
fun TaskManagerUI(modifier: Modifier = Modifier) {
    var selectedPage by remember { mutableIntStateOf(PAGE_TASKS) }
    // 两块内嵌面板共用一档底色，才不会一块深一块浅
    val panelColor = Color.Unspecified.resolve(ConfigManagerDefaults.PanelTone)

    Row(
        modifier = modifier.fillMaxSize().padding(ConfigManagerDefaults.ContentPadding),
        horizontalArrangement = Arrangement.spacedBy(ConfigManagerDefaults.ColumnSpacing),
    ) {
        Surface(
            modifier = Modifier
                .widthIn(
                    min = ConfigManagerDefaults.GroupListMinWidth,
                    max = ConfigManagerDefaults.GroupListMaxWidth,
                )
                .width(IntrinsicSize.Max)
                .fillMaxHeight(),
            color = panelColor,
            sprite = SurfaceDefaults.embeddedPanel,
        ) {
            TaskMenu(
                selected = selectedPage,
                onSelect = { selectedPage = it },
                modifier = Modifier.fillMaxSize().padding(ConfigManagerDefaults.EmbedContentPadding),
            )
        }

        // 右列不再套面板：工具栏要落在面板之外，面板由各页自己出（见 PagePanel）
        Column(Modifier.weight(1f).fillMaxHeight()) {
            when (selectedPage) {
                PAGE_RUNNING  -> RunningTasksPage(Modifier.fillMaxSize())
                PAGE_SETTINGS -> SettingsPage(Modifier.fillMaxSize())
                else          -> TasksPage(Modifier.fillMaxSize())
            }
        }
    }
}

/**
 * 左列菜单：可滚动的项列表 + 并列的滚动条列。
 *
 * @param selected 当前选中的页面下标
 * @param onSelect 选中回调，参数为页面下标
 */
@Composable
private fun TaskMenu(
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Row(modifier) {
        Column(
            modifier = Modifier.weight(1f).fillMaxHeight().verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(ConfigManagerDefaults.GroupSpacing),
        ) {
            taskPages.forEachIndexed { index, title ->
                TaskMenuItem(
                    selected = index == selected,
                    onClick = { onSelect(index) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    // 单行标签：不折行、不省略 —— 固有宽度恒等于文本宽度，面板宽度因此贴合最宽的项
                    Text(
                        component = title,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Clip,
                    )
                }
            }
        }
        ScrollbarColumn(scrollState)
    }
}

/**
 * 菜单项：文字左对齐、贴满整行的 [FlatButton]。
 *
 * 选中时把 `focused` 素材当常态底色，于是选中项常亮、按下仍显示 `pressed`；
 * `normal` 无素材，未选中项因此完全透明。
 */
@Composable
private fun TaskMenuItem(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    val sprites = ConfigManagerDefaults.navItemSprite()
    FlatButton(
        onClick = onClick,
        modifier = modifier,
        sprite = if (selected) sprites.copy(normal = sprites.focused) else sprites,
        minSize = ConfigManagerDefaults.NavItemMinSize,
        contentPadding = ConfigManagerDefaults.NavItemPadding,
        contentAlignment = Alignment.CenterStart,
        content = content,
    )
}
