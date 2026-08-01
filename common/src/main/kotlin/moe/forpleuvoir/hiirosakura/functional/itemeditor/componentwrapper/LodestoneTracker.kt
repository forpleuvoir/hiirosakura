package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEditorDefaults
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEntryRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.default.Add
import moe.forpleuvoir.ibukigourd.ui.icon.default.Delete
import moe.forpleuvoir.ibukigourd.ui.icon.default.EditNote
import moe.forpleuvoir.hiirosakura.ui.widget.OutlinedLabelBox
import moe.forpleuvoir.ibukigourd.ui.preset.IntField
import moe.forpleuvoir.ibukigourd.ui.preset.SimpleAlertDialog
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

@Composable
fun LodestoneTrackerComponentWrapper(
    key: Identifier,
    value: LodestoneTracker,
    onValueChange: (LodestoneTracker) -> Unit,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp, Alignment.End),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) = DataComponentEntryRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    Box(modifier = Modifier.height(DataComponentEditorDefaults.entrySize.height)) {
        var showDialog by remember { mutableStateOf(false) }
        IconButton(onClick = {
            showDialog = true
        }, modifier = Modifier.align(Alignment.CenterEnd)) {
            Icon(Icons.EditNote, null)
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
        title = { Text(key) },
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
    ElevatedCard(modifier = modifier) {
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
                    Icon(icon, null)
                }
            }
            value.getOrNull()?.let { globalPos ->
                var showIdEditor by remember { mutableStateOf(false) }
                var editX by remember { mutableStateOf(globalPos.pos.x) }
                var editY by remember { mutableStateOf(globalPos.pos.y) }
                var editZ by remember { mutableStateOf(globalPos.pos.z) }
                var currentDimension by remember { mutableStateOf(globalPos.dimension) }

                OutlinedLabelBox(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(key, suffix = "dimension") },
                    contentPadding = PaddingValues(8.dp),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(currentDimension.identifier().toString(), modifier = Modifier.weight(1f))
                        IconButton(onClick = { showIdEditor = true }) {
                            Icon(Icons.EditNote, null)
                        }
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
                        { Text(key, suffix = "dimension") },
                        { showIdEditor = false }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    IntField(editX, {
                        editX = it
                        onValueChange(Optional.of(GlobalPos.of(currentDimension, BlockPos(it, editY, editZ))))
                    }, label = { Text("X") }, modifier = Modifier.weight(1f))
                    IntField(editY, {
                        editY = it
                        onValueChange(Optional.of(GlobalPos.of(currentDimension, BlockPos(editX, it, editZ))))
                    }, label = { Text("Y") }, modifier = Modifier.weight(1f))
                    IntField(editZ, {
                        editZ = it
                        onValueChange(Optional.of(GlobalPos.of(currentDimension, BlockPos(editX, editY, it))))
                    }, label = { Text("Z") }, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}