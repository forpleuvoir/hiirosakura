package moe.forpleuvoir.hiirosakura.ui.editor

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange

/**
 * Adds code-editor-style keyboard shortcuts to any Compose text field that uses
 * [TextFieldState].
 *
 * Must be used with the same [TextFieldState] instance that the text field receives.
 *
 * Usage:
 * ```
 * val state = rememberTextFieldState()
 *
 * OutlinedTextField(
 *     state = state,
 *     modifier = Modifier
 *         .fillMaxSize()
 *         .codeEditorShortcuts(
 *             state = state,
 *             indent = "    ",
 *             lineComment = "//",
 *         ),
 * )
 * ```
 *
 * Supported shortcuts:
 * - Ctrl/Command + X (no selection): cut current line
 * - Ctrl/Command + D: duplicate selection or current line
 * - Ctrl/Command + W: expand selection (word → segment → line → full line → all)
 * - Ctrl/Command + Shift + W: shrink selection (undo expand)
 * - Tab: indent (insert at cursor or indent selected lines)
 * - Shift + Tab: unindent selected lines
 * - Ctrl/Command + /: toggle line comment
 * - Alt + Shift + ↑/↓: move line(s) up/down
 * - Shift + Enter: insert line below
 * - Ctrl/Command + Alt + Enter: insert line above
 * - Home: toggle between first non-whitespace and line start
 * - Shift + Home: same as Home but extends selection
 * - Enter (no modifiers): new line with inherited indentation
 *
 * Native Compose shortcuts (copy, paste, select all, undo/redo, backspace, delete,
 * word-delete, arrow keys) are left untouched.
 */
@Suppress("DEPRECATION")
@Composable
fun Modifier.codeEditorShortcuts(
    state: TextFieldState,
    indent: String = "    ",
    lineComment: String = "//",
    enabled: Boolean = true,
): Modifier {
    require(indent.isNotEmpty())
    require(lineComment.isNotEmpty())
    require('\n' !in indent && '\r' !in indent)
    require('\n' !in lineComment && '\r' !in lineComment)

    val clipboardManager = LocalClipboardManager.current
    val history = remember(state) { ArrayDeque<SelectionHistoryEntry>() }

    return this.onPreviewKeyEvent { event ->
        if (!enabled) {
            history.clear()
            return@onPreviewKeyEvent false
        }
        if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
        handleKeyEvent(event, state, indent, lineComment, clipboardManager, history)
    }
}

private data class SelectionHistoryEntry(
    val before: TextRange,
    val after: TextRange,
    val textSnapshot: String,
)

@Suppress("DEPRECATION")
private fun handleKeyEvent(
    event: KeyEvent,
    state: TextFieldState,
    indent: String,
    lineComment: String,
    clipboardManager: ClipboardManager,
    history: ArrayDeque<SelectionHistoryEntry>,
): Boolean {
    val ctrl = event.isCtrlPressed || event.isMetaPressed
    val shift = event.isShiftPressed
    val alt = event.isAltPressed

    return when (event.key) {
        Key.Tab -> {
            when {
                ctrl || alt -> false
                shift -> {
                    if (state.unindent(indent)) {
                        history.clear(); true
                    } else false
                }
                else -> {
                    state.indent(indent)
                    history.clear()
                    true
                }
            }
        }

        Key.Enter -> {
            when {
                ctrl && alt && !shift -> {
                    state.insertLineAbove()
                    history.clear()
                    true
                }
                shift && !ctrl && !alt -> {
                    state.insertLineBelow()
                    history.clear()
                    true
                }
                !ctrl && !alt -> {
                    state.insertIndentedNewLine()
                    history.clear()
                    true
                }
                else -> false
            }
        }

        Key.DirectionUp -> {
            alt && shift && !ctrl && if (state.moveSelectedLinesUp()) {
                history.clear(); true
            } else false
        }

        Key.DirectionDown -> {
            alt && shift && !ctrl && if (state.moveSelectedLinesDown()) {
                history.clear(); true
            } else false
        }

        Key.MoveHome -> {
            if (!ctrl && !alt) {
                state.smartHome(shift)
                true
            } else false
        }

        Key.Slash -> {
            if (ctrl && !alt && !shift) {
                state.toggleLineComment(lineComment)
                history.clear()
                true
            } else false
        }

        Key.W -> {
            when {
                ctrl && shift && !alt -> shrinkSelection(state, history)
                ctrl && !alt -> expandSelection(state, history)
                else -> false
            }
        }

        Key.D -> {
            if (ctrl && !alt && !shift) {
                state.duplicateSelectionOrLine()
                history.clear()
                true
            } else false
        }

        Key.X -> {
            if (ctrl && !alt && !shift) {
                if (!state.selection.collapsed) return false
                cutLine(state, clipboardManager, history)
            } else false
        }

        else -> false
    }
}

private fun expandSelection(
    state: TextFieldState,
    history: ArrayDeque<SelectionHistoryEntry>,
): Boolean {
    val text = state.text.toString()
    val currentSel = state.selection
    val next = text.expandSelection(currentSel) ?: return false

    history.addLast(
        SelectionHistoryEntry(
            before = currentSel,
            after = next,
            textSnapshot = text,
        )
    )
    state.edit { selection = next }
    return true
}

private fun shrinkSelection(
    state: TextFieldState,
    history: ArrayDeque<SelectionHistoryEntry>,
): Boolean {
    val entry = history.lastOrNull() ?: return false
    val currentText = state.text.toString()
    val currentSel = state.selection

    if (currentSel != entry.after || currentText != entry.textSnapshot) {
        history.clear()
        return false
    }

    val clampedStart = entry.before.start.coerceIn(0, currentText.length)
    val clampedEnd = entry.before.end.coerceIn(0, currentText.length)
    val restored = TextRange(clampedStart, clampedEnd)

    state.edit { selection = restored }
    history.removeLast()
    return true
}

@Suppress("DEPRECATION")
private fun cutLine(
    state: TextFieldState,
    clipboardManager: ClipboardManager,
    history: ArrayDeque<SelectionHistoryEntry>,
): Boolean {
    val plan = state.buildCutLinePlan() ?: return false
    clipboardManager.setText(AnnotatedString(plan.text))
    state.edit {
        replace(plan.deletion.start, plan.deletion.end, "")
        selection = TextRange(plan.resultingCursor, plan.resultingCursor)
    }
    history.clear()
    return true
}
