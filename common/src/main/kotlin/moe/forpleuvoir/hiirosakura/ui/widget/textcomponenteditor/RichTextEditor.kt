package moe.forpleuvoir.hiirosakura.ui.widget.textcomponenteditor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import moe.forpleuvoir.compose_minecraft.platform.ui.text.LocalDefaultFont
import moe.forpleuvoir.compose_minecraft.platform.ui.text.MinecraftFonts
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.ui.widget.textcomponenteditor.toolbar.RichTextEditorToolbar
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.ibukigourd.ui.sokitsu.TextField
import moe.forpleuvoir.ibukigourd.util.mc

@Composable
fun RichTextEditor(
    state: RichTextEditorState = remember { RichTextEditorState() },
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
            modifier = Modifier.fillMaxWidth().height(180.dp).richTextEditor(state),
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
                    modifier = Modifier.fillMaxWidth().height(180.dp),
                    textAlign = TextAlign.Center,
                    fontSize = ((1 / LocalDensity.current.density) * (mc.window.guiScale * mc.font.lineHeight)).sp,
                )
            }
        }
    }
}
