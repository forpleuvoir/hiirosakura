package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Button
import moe.forpleuvoir.ibukigourd.ui.sokitsu.ButtonDefaults
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.LocalColorScheme

@Composable
fun ToggleButton(
    value: Boolean,
    onValueChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = { onValueChange(!value) },
        modifier = modifier,
        colors = if (value) {
            ButtonDefaults.colors(
                color = LocalColorScheme.current.primaryContainer,
                contentColor = LocalColorScheme.current.onPrimaryContainer,
            )
        } else {
            ButtonDefaults.colors()
        },
    ) {
        Text(IGLang.Misc.coloredSwitch(value))
    }
}
