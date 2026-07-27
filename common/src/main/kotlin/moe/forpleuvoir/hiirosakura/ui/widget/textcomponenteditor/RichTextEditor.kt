package moe.forpleuvoir.hiirosakura.ui.widget.textcomponenteditor

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.roundToIntRect
import kotlinx.coroutines.isActive
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.ui.widget.OutlinedLabelBox
import moe.forpleuvoir.hiirosakura.ui.widget.textcomponenteditor.toolbar.RichTextEditorToolbar
import moe.forpleuvoir.ibukigourd.render.extension.pushTextLines
import moe.forpleuvoir.ibukigourd.ui.skia.LocalSkiaSurface
import moe.forpleuvoir.ibukigourd.ui.preset.Text
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.common.color.Colors

@Composable
fun RichTextEditor(
    state: RichTextEditorState = remember { RichTextEditorState() },
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        RichTextEditorToolbar(
            state = state,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            state = state.textState,
            outputTransformation = state.outputTransformation,
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onSurface,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .richTextEditor(state),
        )
        Spacer(Modifier.height(12.dp))
        OutlinedLabelBox(
            label = {
                Text(HSLang.TextEditor.preview)
            },
            modifier = Modifier.weight(1.25f).fillMaxWidth()
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                var area by remember { mutableStateOf<Rect?>(null) }
                val surface = LocalSkiaSurface.current
                LaunchedEffect(surface) {
                    while (isActive) {
                        withFrameNanos {
                            area ?: return@withFrameNanos
                            surface.postRender {
                                pushTextLines(
                                    state.mcText,
                                    area = area!!.roundToIntRect(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    defaultColor = Colors.WHITE,
                                )
                            }
                        }
                    }
                }
                Spacer(
                    modifier
                        .fillMaxSize()
                        .onGloballyPositioned { area = it.rectInMcWindow() }
                )
            }
        }

    }
}


fun LayoutCoordinates.rectInMcWindow(): Rect {
    val guiScale = mc.window.guiScale.toFloat()
    require(guiScale > 0f) { "guiScale must be greater than 0" }

    val position = positionInWindow()

    return Rect(
        left = position.x / guiScale,
        top = position.y / guiScale,
        right = (position.x + size.width) / guiScale,
        bottom = (position.y + size.height) / guiScale,
    )
}