package moe.forpleuvoir.hiirosakura.ui.util

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.placeCursorAtEnd
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.defaults.Delete

@Composable
fun NullableInputTransformation(
    isNull: Boolean,
): InputTransformation {
    return InputTransformation {
        if (isNull) {
            val before = originalText.toString()
            val after = asCharSequence().toString()

            val prefixLength = before
                .zip(after)
                .indexOfFirst { (a, b) -> a != b }
                .let { if (it == -1) minOf(before.length, after.length) else it }

            var suffixLength = 0
            while (
                suffixLength < before.length - prefixLength &&
                suffixLength < after.length - prefixLength &&
                before[before.lastIndex - suffixLength] ==
                after[after.lastIndex - suffixLength]
            ) {
                suffixLength++
            }

            val insertedText = after.substring(
                startIndex = prefixLength,
                endIndex = after.length - suffixLength,
            )

            if (insertedText.any(Char::isDigit)) {
                replace(0, length, insertedText)
                placeCursorAtEnd()
            }
        }
    }
}

@Composable
fun NullableOutputTransformation(
    isNull: Boolean
): OutputTransformation? {
    return if (isNull) {
        OutputTransformation {
            replace(start = 0, end = length, text = HSLang.Common.unset.plainText)
        }
    } else null
}

@Composable
fun NullableTrailingIcon(
    isPresent: Boolean,
    onClear: () -> Unit,
) {
    if (isPresent) {
        Row {
            IconButton(onClick = onClear) {
                Icon(Icons.Delete, null)
            }
            Spacer(Modifier.width(4.dp))
        }
    }
}