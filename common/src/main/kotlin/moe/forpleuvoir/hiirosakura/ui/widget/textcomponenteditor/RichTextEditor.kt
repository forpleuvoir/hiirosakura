package moe.forpleuvoir.hiirosakura.ui.widget.textcomponenteditor

import androidx.compose.foundation.layout.*
import moe.forpleuvoir.ibukigourd.ui.sokitsu.TextField
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
import moe.forpleuvoir.ibukigourd.mod.config.IGConfig
import moe.forpleuvoir.ibukigourd.render.extension.pushTextLines
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.ibukigourd.mod.config.ThemeMode

@Composable
fun RichTextEditor(
    state: RichTextEditorState = remember { RichTextEditorState() },
    enabledPreviewRender: Boolean = true,
    previewDefaultColor: Color = if ((IGConfig.Gui.Theme.mode == ThemeMode.Light)) Colors.BLACK else Colors.WHITE,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        RichTextEditorToolbar(
            state = state,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        TextField(
            state = state.textState,
            
            modifier = Modifier .fillMaxWidth() .weight(1f) .richTextEditor(state),
        )
        Spacer(Modifier.height(12.dp))
        OutlinedLabelBox(
            label = {
                Text(component = HSLang.TextEditor.preview)
            },
            modifier = Modifier.weight(1.25f).fillMaxWidth()
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    component = state.mcText,
                    modifier = Modifier.fillMaxSize(),
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