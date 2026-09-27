package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogContentDefaults
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import moe.forpleuvoir.ibukigourd.ui.util.FabScrollVisibility
import moe.forpleuvoir.ibukigourd.ui.util.FabVisibilityDefaults
import moe.forpleuvoir.ibukigourd.ui.util.FabVisibilityState
import moe.forpleuvoir.ibukigourd.ui.util.isQuickAction
import moe.forpleuvoir.ibukigourd.ui.util.rememberHideActionState
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Button
import moe.forpleuvoir.hiirosakura.ui.icon.VectorIcon
import moe.forpleuvoir.hiirosakura.ui.compat.FloatingActionButtonMenu
import moe.forpleuvoir.hiirosakura.ui.compat.FloatingActionButtonMenuItem
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.LocalSokitsuPixelScale

fun interface EntryEditor<T> {
    @Composable
    fun invoke(
        addAction: (T) -> Unit,
        defaultValue: () -> T,
        onDismiss: () -> Unit
    )
}

data class AddMenuOption<T>(
    val text: @Composable () -> Unit,
    val defaultValue: () -> T,
    val editor: EntryEditor<T>?
)

/** 浮动按钮本体尺寸；计算菜单可用高度时要减掉它。 */
private val FabMenuButtonSize: Dp = 50.dp

/** 菜单项之间、菜单项与按钮之间的间距（与 [moe.forpleuvoir.hiirosakura.ui.compat.FloatingActionButtonMenu] 内一致）。 */
private val FabMenuSpacing: Dp = 8.dp

/**
 * 列表右下角的浮动添加按钮：按钮本体与展开后的菜单项都靠右对齐，并跟随滚动与隐藏动作键收起。
 *
 * 显隐的两个来源（与 IG `EditDialogContent` 的浮动按钮一致）：
 * - **隐藏动作键**：`IGConfig.Gui.hideActionKeyCode`，按住期间强制收起；
 * - **滚动隐藏**：由 [fabVisibilityState] 表达，方向判定发生在
 *   `Modifier.fabScrollVisibility` 的嵌套滚动回调里 —— 该 Modifier 必须挂在**滚动容器的祖先**
 *   上，本按钮只是滚动容器的兄弟节点，挂在这里收不到位移（历史实现即因如此从未生效）。
 *
 * 收起时菜单一并折叠，避免出现「看不见但仍处于展开态」。
 *
 * @param modifier 应用在按钮外层盒子上的 Modifier，调用方用它做对齐（如 `Modifier.align(Alignment.BottomEnd)`）
 * @param fabVisibilityState 滚动显隐状态，null 表示不参与滚动隐藏
 * @param fabMargin 与宿主右下角的间距，默认取 IG 对话框浮动按钮的同款留边
 * @param addMenuOptions 展开后的菜单项
 * @param addAction 添加回调
 */
@Composable
fun <T> FloatingAddButton(
    modifier: Modifier = Modifier,
    fabVisibilityState: FabScrollVisibility? = null,
    fabMargin: PaddingValues = EditDialogContentDefaults.addButtonPadding,
    addMenuOptions: List<AddMenuOption<T>>,
    addAction: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }


    var activeEditor: EntryEditor<T>? by remember { mutableStateOf(null) }

    var currentDefaultValue by remember { mutableStateOf<(() -> T)?>(null) }

    val hiddenByKey = rememberHideActionState()
    val hiddenByScroll = fabVisibilityState?.state == FabVisibilityState.Hidden
    val visible = !hiddenByKey && !hiddenByScroll

    // 收起时同步折叠菜单:重新出现时不会带着旧的展开态
    LaunchedEffect(visible) {
        if (!visible) expanded = false
    }

    val duration = FabVisibilityDefaults.hideDuration.inWholeMilliseconds.toInt()
    val offsetPx = with(LocalDensity.current) { FabVisibilityDefaults.translationY.roundToPx() }

    BoxWithConstraints(modifier = modifier.padding(fabMargin)) {
        // 菜单最多用到「容器高度 − 按钮 − 间距」:选项多于一屏时在菜单内滚动。
        // 不限高的话 Column 会把每一项压扁,并且整块溢出到宿主(如对话框页脚)上面。
        val menuMaxHeight =
            if (maxHeight.value.isFinite())
                (maxHeight - FabMenuButtonSize - FabMenuSpacing).coerceAtLeast(FabMenuButtonSize)
            else Dp.Unspecified

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(duration)) +
                    scaleIn(tween(duration), initialScale = 0.8f) +
                    slideInVertically(tween(duration)) { offsetPx },
            exit = fadeOut(tween(duration)) +
                    scaleOut(tween(duration), targetScale = 0.8f) +
                    slideOutVertically(tween(duration)) { offsetPx },
        ) {
            FloatingActionButtonMenu(
                expanded = expanded,
                horizontalAlignment = Alignment.End,
                menuMaxHeight = menuMaxHeight,
                button = {
                    Button(
                        onClick = { expanded = !expanded },
                        modifier = Modifier.size(FabMenuButtonSize),
                        // Icon 尺寸走 fittedIconSize(缩到可用空间),故去掉 contentPadding 以放大图标
                        contentPadding = PaddingValues(0.dp),
                    ) {
                        val rotation by animateFloatAsState(
                            targetValue = if (expanded) 45f else 0f,
                            animationSpec = tween(150),
                        )
                        Icon(
                            Icons.Add,
                            // 默认 scale = 2 在 50dp 按钮里偏小:放大到 3 倍像素
                            scale = 2,
                            modifier = Modifier.rotate(rotation),
                        )
                    }
                }
            ) {
                addMenuOptions.forEach { option ->
                    FloatingActionButtonMenuItem(
                        onClick = {
                            if (isQuickAction || option.editor == null) {
                                addAction(option.defaultValue())
                            } else {
                                activeEditor = option.editor
                                currentDefaultValue = option.defaultValue
                            }
                            expanded = false
                        },
                        text = option.text,
                        icon = {}
                    )
                }
            }
        }
    }

    if (activeEditor != null && currentDefaultValue != null) {
        activeEditor!!.invoke(
            addAction,
            currentDefaultValue!!,
            onDismiss = { activeEditor = null }
        )
    }
}
