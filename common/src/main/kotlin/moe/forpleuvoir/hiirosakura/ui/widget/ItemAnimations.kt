package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.ui.Modifier

/**
 * 列表新条目动画:220ms 线性淡入 + 库默认的位移/删除动画。
 *
 * 为什么显式给 spec:库默认 [LazyItemScope.animateItem] 用
 * `spring(stiffness = 400f, dampingRatio = 1f)`,到 95% 只有约 0.24s,观感是一闪而过。
 * 时长在这里集中定义,所有带添加/删除的列表统一引用,以后要调只改这一行。
 *
 * 注意:改这个 spec 只影响"新条目的淡入";本渲染栈的图层是"命令烘焙"模型,图层属性
 * 动画要靠 Compose-Minecraft 侧的失效链才能上屏,所以不要退回到"什么都不传"(见
 * `GraphicsLayer.onDynamicPropertyChanged` 与 `NodeCoordinator.invalidateLayer` 的平台适配)。
 */
fun LazyItemScope.hsItemAnimation(): Modifier =
    Modifier.animateItem(fadeInSpec = tween(durationMillis = 220))

/** `LazyVerticalGrid` 的行版本;网格用的是 [LazyGridItemScope],与列表的 [LazyItemScope] 不是一个接口。 */
fun LazyGridItemScope.hsItemAnimation(): Modifier =
    Modifier.animateItem(fadeInSpec = tween(durationMillis = 220))
