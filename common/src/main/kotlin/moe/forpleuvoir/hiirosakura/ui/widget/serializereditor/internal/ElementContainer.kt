package moe.forpleuvoir.hiirosakura.ui.widget.serializereditor.internal

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IconButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Surface
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Button
import moe.forpleuvoir.hiirosakura.ui.icon.VectorIcon

/**
 * 对象 / 数组编辑器的统一容器：
 * 圆角卡片 + 可折叠头部（展开箭头、标题、数量徽标、添加按钮）+ 带引导线的条目区。
 */
@Composable
internal fun ElementContainer(
    title: @Composable () -> Unit,
    count: Int,
    expanded: Boolean,
    onToggle: () -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier,
    emptyText: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 90f else 0f,
        animationSpec = tween(durationMillis = 160),
        label = "elementContainerArrow",
    )
    Surface(
        modifier = modifier.fillMaxWidth(),
        
        color = SokitsuTheme.colorScheme.surfaceVariant,
        outlineColor = SokitsuTheme.colorScheme.outline.copy(alpha = 0.5f),
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onToggle)
                    .padding(start = 8.dp, end = 6.dp, top = 4.dp, bottom = 4.dp),
            ) {
                Icon(Icons.ArrowRight,
                    modifier = Modifier
                        .size(18.dp)
                        .rotate(rotation),
                    tint = SokitsuTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.width(4.dp))
                title()
                Spacer(Modifier.weight(1f))
                Surface(
                    
                    color = SokitsuTheme.colorScheme.secondary,
                ) {
                    Text(
                        text = count.toString(),
                        style = SokitsuTheme.typography.button,
                        color = SokitsuTheme.colorScheme.onSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    )
                }
                IconButton(
                    onClick = onAdd,
                    modifier = Modifier.size(30.dp),
                ) {
                    Icon(Icons.Add,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }

            if (expanded) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 10.dp, top = 2.dp, bottom = 8.dp)
                        .height(IntrinsicSize.Min),
                ) {
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .fillMaxHeight()
                            .background(
                                color = SokitsuTheme.colorScheme.outline.copy(alpha = 0.5f),
                                
                            ),
                    )
                    Spacer(Modifier.width(8.dp))
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        if (emptyText != null && count == 0) {
                            Text(
                                text = emptyText,
                                style = SokitsuTheme.typography.body,
                                color = SokitsuTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 4.dp),
                            )
                        }
                        content()
                    }
                }
            }
        }
    }
}