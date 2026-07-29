package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEditorDefaults
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEntryRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.hiirosakura.ui.util.canScroll
import moe.forpleuvoir.hiirosakura.ui.widget.HolderSetEntityTypeEditorDialog
import moe.forpleuvoir.hiirosakura.ui.widget.HolderSoundEventSelector
import moe.forpleuvoir.hiirosakura.ui.widget.OutlinedLabelBox
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.default.Delete
import moe.forpleuvoir.ibukigourd.ui.icon.default.EditNote
import moe.forpleuvoir.ibukigourd.ui.preset.EnumSelector
import moe.forpleuvoir.ibukigourd.ui.preset.Selector
import moe.forpleuvoir.ibukigourd.ui.preset.SimpleAlertDialog
import moe.forpleuvoir.ibukigourd.ui.preset.Text
import moe.forpleuvoir.ibukigourd.ui.preset.modifier.plainTooltip
import moe.forpleuvoir.ibukigourd.util.identifier
import moe.forpleuvoir.ibukigourd.util.mc
import net.minecraft.core.Holder
import net.minecraft.core.HolderSet
import net.minecraft.resources.FileToIdConverter
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.equipment.EquipmentAsset
import net.minecraft.world.item.equipment.EquipmentAssets
import net.minecraft.world.item.equipment.Equippable
import java.util.*
import kotlin.jvm.optionals.getOrElse
import kotlin.jvm.optionals.getOrNull

@Composable
fun EquippableComponentWrapper(
    key: Identifier,
    value: Equippable,
    onValueChange: (Equippable) -> Unit,
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
            EquippableEditorDialog(
                key = key,
                value = value,
                onValueChange = onValueChange,
                onDismissRequest = { showDialog = false },
                title = { Text(key) }
            )
        }
    }
}

@Composable
fun EquippableEditorDialog(
    key: Identifier,
    value: Equippable,
    onValueChange: (Equippable) -> Unit,
    onDismissRequest: () -> Unit,
    title: @Composable () -> Unit
) {
    var result by remember { mutableStateOf(value) }

    SimpleAlertDialog(
        title = title,
        modifier = Modifier.padding(24.dp),
        onDismissRequest = onDismissRequest,
        onConfirmRequest = {
            onValueChange(result)
            true
        },
        content = {
            Box(
                modifier = Modifier.heightIn(max = 720.dp)
            ) {
                val scrollState = rememberScrollState()
                Column(
                    Modifier
                        .padding(end = if (scrollState.canScroll) 12.dp else 0.dp)
                        .fillMaxWidth()
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    //slot
                    EnumSelector(
                        result.slot,
                        { result = result.copy(slot = it) },
                        label = {
                            Text(key, suffix = "slot", fallback = "Slot")
                        }
                    )
                    //equipSound
                    HolderSoundEventSelector(
                        result.equipSound,
                        { result = result.copy(equipSound = it) },
                        label = { Text(key, suffix = "equip_sound", fallback = "Equip Sound") },
                        contentPadding = OutlinedTextFieldDefaults.contentPadding(top = 4.dp, bottom = 4.dp, start = 16.dp, end = 16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    //assetId
                    AssetId(key, result) { result = it }
                    //cameraOverlay
                    CameraOverlay(key, result) { result = it }
                    //allowedEntities
                    AllowedEntities(key, result) { result = it }
                    //dispensable
                    EntryRow(key, "dispensable", "Dispensable") {
                        Switch(result.dispensable, { result = result.copy(dispensable = it) })
                    }
                    //swappable
                    EntryRow(key, "swappable", "Swappable") {
                        Switch(result.swappable, { result = result.copy(swappable = it) })
                    }
                    //damageOnHurt
                    EntryRow(key, "damage_on_hurt", "Damage On Hurt") {
                        Switch(result.damageOnHurt, { result = result.copy(damageOnHurt = it) })
                    }
                    //equipOnInteract
                    EntryRow(key, "equip_on_interact", "Equip On Interact") {
                        Switch(result.equipOnInteract, { result = result.copy(equipOnInteract = it) })
                    }
                    //canBeSheared
                    EntryRow(key, "can_be_sheared", "Can Be Sheared") {
                        Switch(result.canBeSheared, { result = result.copy(canBeSheared = it) })
                    }
                    //shearingSound
                    HolderSoundEventSelector(
                        result.shearingSound,
                        { result = result.copy(shearingSound = it) },
                        label = { Text(key, suffix = "shearing_sound", fallback = "Shearing Sound") },
                        contentPadding = OutlinedTextFieldDefaults.contentPadding(top = 4.dp, bottom = 4.dp, start = 16.dp, end = 16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                }
                VerticalScrollbar(
                    adapter = rememberScrollbarAdapter(scrollState),
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }
    )


}

@Composable
private fun AllowedEntities(
    key: Identifier,
    value: Equippable,
    onValueChange: (Equippable) -> Unit
) {
    var showAllowedEntitiesDialog by remember { mutableStateOf(false) }
    val allowedEntities = value.allowedEntities
    val tip = if (allowedEntities.isPresent && allowedEntities.get().size() > 0) {
        Modifier.plainTooltip {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                allowedEntities.get().take(10).forEach {
                    Text(it.value().description)
                }
                if (allowedEntities.get().size() > 10) {
                    Text("...")
                }
            }
        }
    } else Modifier
    OutlinedLabelBox(
        modifier = Modifier
            .fillMaxWidth()
            .then(tip),
        label = { Text(key, suffix = "allowed_entities", fallback = "Allowed Entities") },
        contentPadding = OutlinedTextFieldDefaults.contentPadding(top = 4.dp, bottom = 4.dp, start = 16.dp, end = 16.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(1f, false),
                verticalAlignment = Alignment.CenterVertically
            ) {
                value.allowedEntities.getOrNull()?.let { allowedEntities ->
                    Text(IGLang.ConfigWrapper.listConfigWrapperText(allowedEntities.size()), overflow = TextOverflow.Ellipsis, maxLines = 1)
                } ?: run {
                    Text(HSLang.Common.unset)
                }
            }
            Row {
                IconButton({
                    showAllowedEntitiesDialog = true
                }) {
                    Icon(Icons.EditNote, contentDescription = null)
                }
                IconButton(onClick = {
                    onValueChange(value.copy(allowedEntities = Optional.ofNullable(null)))
                }) {
                    Icon(Icons.Delete, null)
                }
            }
        }
    }

    if (showAllowedEntitiesDialog) {
        HolderSetEntityTypeEditorDialog(
            key = key,
            value = value.allowedEntities.getOrNull() ?: HolderSet.empty(),
            onValueChange = {
                onValueChange(value.copy(allowedEntities = Optional.ofNullable(it)))
            },
            onDismissRequest = { showAllowedEntitiesDialog = false },
            title = { Text(key, suffix = "allowed_entities", fallback = "Allowed Entities") },
        )
    }
}


@Composable
private fun CameraOverlay(
    key: Identifier,
    value: Equippable,
    onValueChange: (Equippable) -> Unit
) {
    var showCameraOverlayDialog by remember { mutableStateOf(false) }
    OutlinedLabelBox(
        modifier = Modifier
            .fillMaxWidth(),
        label = { Text(key, suffix = "camera_overlay", fallback = "Camera Overlay") },
        contentPadding = OutlinedTextFieldDefaults.contentPadding(top = 4.dp, bottom = 4.dp, start = 16.dp, end = 16.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(1f, false),
                verticalAlignment = Alignment.CenterVertically
            ) {
                value.cameraOverlay.getOrNull()?.let { overlay ->
                    Text(overlay, overflow = TextOverflow.Ellipsis, maxLines = 1)
                } ?: run {
                    Text(HSLang.Common.unset)
                }
            }
            Row {
                IconButton({
                    showCameraOverlayDialog = true
                }) {
                    Icon(Icons.EditNote, contentDescription = null)
                }
                IconButton(onClick = {
                    onValueChange(value.copy(cameraOverlay = Optional.ofNullable(null)))
                }) {
                    Icon(Icons.Delete, null)
                }
            }
        }
    }
    if (showCameraOverlayDialog) {
        IdentifierEditorDialog(
            value.cameraOverlay.getOrElse { identifier("minecraft", "misc/pumpkinblur") },
            { onValueChange(value.copy(cameraOverlay = Optional.of(it))) },
            { Text(key, suffix = "camera_overlay", fallback = "Camera Overlay") },
            { showCameraOverlayDialog = false }
        )
    }
}

@Composable
private fun AssetId(
    key: Identifier,
    value: Equippable,
    onValueChange: (Equippable) -> Unit
) {
    var showAssetIDDialog by remember { mutableStateOf(false) }
    OutlinedLabelBox(
        modifier = Modifier
            .fillMaxWidth(),
        label = { Text(key, suffix = "asset_id", fallback = "Asset ID") },
        contentPadding = OutlinedTextFieldDefaults.contentPadding(top = 4.dp, bottom = 4.dp, start = 16.dp, end = 16.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(1f, false),
                verticalAlignment = Alignment.CenterVertically
            ) {
                value.assetId.getOrNull()?.let { assetId ->
                    Text(assetId.identifier(), overflow = TextOverflow.Ellipsis, maxLines = 1)
                } ?: run {
                    Text(HSLang.Common.unset)
                }
            }
            Row {
                IconButton({
                    showAssetIDDialog = true
                }) {
                    Icon(Icons.EditNote, contentDescription = null)
                }
                IconButton(onClick = {
                    onValueChange(value.copy(assetId = Optional.ofNullable(null)))
                }) {
                    Icon(Icons.Delete, null)
                }
            }
        }
    }
    if (showAssetIDDialog) {
        val equipmentAssetKeys = equipmentAssetKeys()

        val namespace = rememberTextFieldState(
            value.assetId.getOrNull()?.identifier()?.namespace ?: equipmentAssetKeys.first().identifier().namespace
        )
        val path = rememberTextFieldState(
            value.assetId.getOrNull()?.identifier()?.path ?: equipmentAssetKeys.first().identifier().path
        )

        val checkNamespace: Boolean = Identifier.isValidNamespace(namespace.text.toString())
        val checkPath: Boolean = Identifier.isValidPath(path.text.toString())

        SimpleAlertDialog(
            onDismissRequest = { showAssetIDDialog = false },
            onConfirmRequest = { true },
            title = { Text(key, suffix = "asset_id", fallback = "Asset ID") },
            content = {
                Column {
                    OutlinedTextField(
                        namespace,
                        labelPosition = TextFieldLabelPosition.Attached(true),
                        label = {
                            Row {
                                Text("Namespace")
                                if (!checkNamespace) {
                                    Spacer(Modifier.width(8.dp))
                                    Text("Non [a-z0-9_.-] character in namespace of location")
                                }
                            }
                        },
                        isError = !checkNamespace,
                        modifier = Modifier.fillMaxWidth().height(68.dp),
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        path,
                        labelPosition = TextFieldLabelPosition.Attached(true),
                        label = {
                            Row {
                                Text("Path")
                                if (!checkPath) {
                                    Spacer(Modifier.width(8.dp))
                                    Text("Non [a-z0-9/._-] character in path of location")
                                }
                            }
                        },
                        isError = !checkPath,
                        modifier = Modifier.fillMaxWidth().height(68.dp),
                    )
                    Spacer(Modifier.height(8.dp))
                    var selected by remember { mutableStateOf(equipmentAssetKeys.first()) }
                    Selector(
                        equipmentAssetKeys.first(),
                        {
                            namespace.edit {
                                replace(0, length, it.identifier().namespace)
                            }
                            path.edit {
                                replace(0, length, it.identifier().path)
                            }
                            selected = it
                        },
                        items = equipmentAssetKeys,
                        label = { Text(key, suffix = "form_resource_manager", fallback = "From Resource Manager") },
                        content = {
                            Text(it.identifier(), maxLines = 1, overflow = TextOverflow.Ellipsis)
                        },
                        itemContent = { item, _ ->
                            Text(item.identifier())
                        },
                        searchFilter = { str, it ->
                            it.identifier().toString().contains(str, ignoreCase = true)
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (checkNamespace && checkPath) {
                        val newId = Identifier.fromNamespaceAndPath(namespace.text.toString(), path.text.toString())
                        val resourceKey = ResourceKey.create(EquipmentAssets.ROOT_ID, newId)
                        onValueChange(
                            value.copy(assetId = Optional.of(resourceKey)),
                        )
                        showAssetIDDialog = false
                    }
                }, enabled = checkNamespace && checkPath) {
                    Text(IGLang.Misc.confirm)
                }
            }
        )
    }
}

@Composable
private fun EntryRow(key: Identifier, suffix: String, fallback: String, content: @Composable RowScope. () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Text(key, suffix = suffix, fallback = fallback)
        content()
    }
}

@Composable
fun equipmentAssetKeys(): List<ResourceKey<EquipmentAsset>> {
    val manager = mc.resourceManager
    val converter = remember { FileToIdConverter.json("equipment") }
    return remember(manager) {
        converter.listMatchingResources(manager).keys.map { fileId ->
            ResourceKey.create(EquipmentAssets.ROOT_ID, converter.fileToId(fileId))
        }
    }
}

fun Equippable.copy(
    slot: EquipmentSlot = this.slot,
    equipSound: Holder<SoundEvent> = this.equipSound,
    assetId: Optional<ResourceKey<EquipmentAsset>> = this.assetId,
    cameraOverlay: Optional<Identifier> = this.cameraOverlay,
    allowedEntities: Optional<HolderSet<EntityType<*>>> = this.allowedEntities,
    dispensable: Boolean = this.dispensable,
    swappable: Boolean = this.swappable,
    damageOnHurt: Boolean = this.damageOnHurt,
    equipOnInteract: Boolean = this.equipOnInteract,
    canBeSheared: Boolean = this.canBeSheared,
    shearingSound: Holder<SoundEvent> = this.shearingSound,
): Equippable = Equippable(
    slot, equipSound, assetId, cameraOverlay, allowedEntities,
    dispensable, swappable, damageOnHurt, equipOnInteract,
    canBeSheared, shearingSound
)