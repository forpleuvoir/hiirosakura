package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDialogTitle
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDisplay
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDisplayRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentField
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentSection
import moe.forpleuvoir.hiirosakura.ui.widget.LabeledFieldDefaults
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IntField
import moe.forpleuvoir.ibukigourd.ui.sokitsu.SimpleAlertDialog
import moe.forpleuvoir.ibukigourd.util.mc
import net.minecraft.core.BlockPos
import net.minecraft.core.GlobalPos
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.component.LodestoneTracker
import net.minecraft.world.level.Level
import java.util.*
import kotlin.jvm.optionals.getOrNull
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Surface
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IconButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Switch
import androidx.compose.foundation.layout.fillMaxWidth

@Composable
fun LodestoneTrackerComponentWrapper(
    key: Identifier,
    value: LodestoneTracker,
    onValueChange: (LodestoneTracker) -> Unit,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) {
    var showDialog by remember { mutableStateOf(false) }

    DataComponentDisplayRow(
        key, removeAction, modifier, horizontalArrangement, verticalAlignment,
        onEdit = { showDialog = true },
    ) {
        val target = value.target.getOrNull()
        Text(
            if (target == null) "tracked ${value.tracked}"
            else "tracked ${value.tracked} · ${target.dimension.identifier()} ${target.pos.toShortString()}",
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )
    }

    if (showDialog) {
        LodestoneTrackerEditorDialog(
            value = value,
            onValueChange = onValueChange,
            onDismissRequest = { showDialog = false },
            key = key
        )
    }
}

@Composable
private fun LodestoneTrackerEditorDialog(
    value: LodestoneTracker,
    onValueChange: (LodestoneTracker) -> Unit,
    onDismissRequest: () -> Unit,
    key: Identifier,
) {
    var tracked by remember { mutableStateOf(value.tracked) }

    var target by remember { mutableStateOf(value.target) }

    SimpleAlertDialog(
        onDismissRequest = onDismissRequest,
        onConfirmRequest = {
            onValueChange(LodestoneTracker(target, tracked))
            true
        },
        title = { DataComponentDialogTitle(key) },
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(key, suffix = "tracked")
                    Switch(tracked, { tracked = it })
                }
                OptionalGlobalPosCard(
                    value = target,
                    onValueChange = { target = it },
                    key = key,
                )
            }
        }
    )
}

@Composable
private fun OptionalGlobalPosCard(
    value: Optional<GlobalPos>,
    onValueChange: (Optional<GlobalPos>) -> Unit,
    key: Identifier,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp, 8.dp, 16.dp, if (value.isPresent) 16.dp else 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(key, suffix = "target")
                IconButton(onClick = {
                    if (value.isEmpty) {
                        onValueChange(
                            Optional.of(
                                GlobalPos.of(
                                    mc.player?.level()?.dimension() ?: Level.OVERWORLD,
                                    mc.player?.blockPosition() ?: BlockPos(0, 0, 0)
                                )
                            )
                        )
                    } else {
                        onValueChange(Optional.empty())
                    }
                }) {
                    val icon = if (value.isEmpty) Icons.Add else Icons.Delete
                    Icon(icon)
                }
            }
            value.getOrNull()?.let { globalPos ->
                var showIdEditor by remember { mutableStateOf(false) }

                var editX by remember { mutableStateOf(globalPos.pos.x) }

                var editY by remember { mutableStateOf(globalPos.pos.y) }

                var editZ by remember { mutableStateOf(globalPos.pos.z) }

                var currentDimension by remember { mutableStateOf(globalPos.dimension) }

                DataComponentField(key, suffix = "dimension", modifier = Modifier.fillMaxWidth()) {
                    DataComponentDisplay(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        onEdit = { showIdEditor = true },
                    ) {
                        Text(currentDimension.identifier().toString(), overflow = TextOverflow.Ellipsis, maxLines = 1)
                    }
                }

                if (showIdEditor) {
                    IdentifierEditorDialog(
                        currentDimension.identifier(),
                        {
                            currentDimension = ResourceKey.create(Registries.DIMENSION, it)
                            onValueChange(Optional.of(GlobalPos.of(currentDimension, BlockPos(editX, editY, editZ))))
                            showIdEditor = false
                        },
                        { DataComponentDialogTitle(key, suffix = "dimension") },
                        { showIdEditor = false }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    DataComponentSection(
                        title = { Text("X", fontSize = SokitsuTheme.typography.body.fontSize) },
                        modifier = Modifier.weight(1f),
                    ) {
                        IntField(
                            editX,
                            {
                                editX = it
                                onValueChange(Optional.of(GlobalPos.of(currentDimension, BlockPos(it, editY, editZ))))
                            },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    DataComponentSection(
                        title = { Text("Y", fontSize = SokitsuTheme.typography.body.fontSize) },
                        modifier = Modifier.weight(1f),
                    ) {
                        IntField(
                            editY,
                            {
                                editY = it
                                onValueChange(Optional.of(GlobalPos.of(currentDimension, BlockPos(editX, it, editZ))))
                            },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    DataComponentSection(
                        title = { Text("Z", fontSize = SokitsuTheme.typography.body.fontSize) },
                        modifier = Modifier.weight(1f),
                    ) {
                        IntField(
                            editZ,
                            {
                                editZ = it
                                onValueChange(Optional.of(GlobalPos.of(currentDimension, BlockPos(editX, editY, it))))
                            },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}
