package moe.forpleuvoir.hiirosakura.ui.widget.textcomponenteditor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.input.TextFieldLineLimits
import moe.forpleuvoir.ibukigourd.ui.sokitsu.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import moe.forpleuvoir.compose_minecraft.platform.ui.text.LocalDefaultFont
import moe.forpleuvoir.compose_minecraft.platform.ui.text.MinecraftFonts
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.ui.widget.textcomponenteditor.toolbar.RichTextEditorToolbar
import moe.forpleuvoir.ibukigourd.mod.config.IGConfig
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.ibukigourd.mod.config.ThemeMode

@Composable
fun RichTextEditor(
    state: RichTextEditorState = remember { RichTextEditorState() },
    enabledPreviewRender: Boolean = true,
    previewDefaultColor: Color = if ((IGConfig.Gui.Theme.mode == ThemeMode.Light)) Colors.BLACK else Colors.WHITE,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        RichTextEditorToolbar(
            state = state,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        TextField(
            state = state.textState,
            lineLimits = TextFieldLineLimits.MultiLine(minHeightInLines = 6, maxHeightInLines = 12),
            outputTransformation = state.outputTransformation,
            modifier = Modifier.fillMaxWidth().richTextEditor(state),
        )
        Spacer(Modifier.height(12.dp))
        Text(component = HSLang.TextEditor.preview)
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            CompositionLocalProvider(LocalDefaultFont provides MinecraftFonts.Default) {
                Text(
                    component = state.mcText,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = (mc.window.guiScale * mc.font.lineHeight).sp,
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
