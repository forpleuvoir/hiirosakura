package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigManagerDefaults
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Surface
import moe.forpleuvoir.ibukigourd.ui.sokitsu.SurfaceDefaults
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.resolve

/**
 * 一页的骨架：工具栏与内容各坐一张内嵌面板，工具栏在上，两张面板之间留
 * [ConfigManagerDefaults.SearchResultSpacing]。
 *
 * 面板底色与内缩取 [ConfigManagerDefaults]；调用方不要再给本组件加外边距，留白由宿主统一给，
 * 否则同屏两块面板的底边会对不齐。
 *
 * @param toolbar 上方面板里的工具栏；为 null 时只有内容面板
 * @param content 下面板里的内容
 */
@Composable
fun PagePanel(
    modifier: Modifier = Modifier,
    toolbar: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val panelColor = Color.Unspecified.resolve(ConfigManagerDefaults.PanelTone)

    Column(modifier) {
        if (toolbar != null) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = panelColor,
                sprite = SurfaceDefaults.embeddedPanel,
            ) {
                Box(Modifier.fillMaxWidth().padding(ConfigManagerDefaults.EmbedContentPadding)) {
                    toolbar()
                }
            }
            Spacer(Modifier.height(ConfigManagerDefaults.SearchResultSpacing))
        }

        Surface(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            color = panelColor,
            sprite = SurfaceDefaults.embeddedPanel,
        ) {
            Box(Modifier.fillMaxSize().padding(ConfigManagerDefaults.EmbedContentPadding)) {
                content()
            }
        }
    }
}
