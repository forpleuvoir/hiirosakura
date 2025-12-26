package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEditor
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentWrapperRow
import moe.forpleuvoir.hiirosakura.gui.widget.EntityTypeSelector
import moe.forpleuvoir.hiirosakura.gui.widget.HolderSoundEventSelector
import moe.forpleuvoir.hiirosakura.gui.widget.RemoveButton
import moe.forpleuvoir.hiirosakura.util.asTranslateText
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.gui.base.Transform
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.*
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.scope.GuiScope.Companion.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.base.screen.closeScreen
import moe.forpleuvoir.ibukigourd.gui.base.tip.Tip
import moe.forpleuvoir.ibukigourd.gui.base.tip.TipHandler
import moe.forpleuvoir.ibukigourd.gui.base.toast.Toast
import moe.forpleuvoir.ibukigourd.gui.util.Direction
import moe.forpleuvoir.ibukigourd.gui.widget.ConfirmDialog
import moe.forpleuvoir.ibukigourd.gui.widget.DialogContent
import moe.forpleuvoir.ibukigourd.gui.widget.EnumSelector
import moe.forpleuvoir.ibukigourd.gui.widget.Selector
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.button.DeleteButton
import moe.forpleuvoir.ibukigourd.gui.widget.button.SwitchButton
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.layout.RowScope
import moe.forpleuvoir.ibukigourd.gui.widget.layout.TableWrapped
import moe.forpleuvoir.ibukigourd.gui.widget.text.Text
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextEditor
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextWidget
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.text.maxWidth
import moe.forpleuvoir.ibukigourd.text.withColor
import moe.forpleuvoir.ibukigourd.util.forEachWithLimit
import moe.forpleuvoir.ibukigourd.util.identifier
import moe.forpleuvoir.ibukigourd.util.lateInitValueOf
import moe.forpleuvoir.ibukigourd.util.state.asMutableState
import moe.forpleuvoir.ibukigourd.util.state.asState
import moe.forpleuvoir.ibukigourd.util.state.mutableStateOf
import moe.forpleuvoir.nebula.common.color.Colors
import net.minecraft.core.HolderSet
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.equipment.EquipmentAsset
import net.minecraft.world.item.equipment.EquipmentAssets
import net.minecraft.world.item.equipment.Equippable
import java.util.*
import kotlin.jvm.optionals.getOrNull
import kotlin.time.Duration.Companion.seconds

fun ContainerScope.EquippableComponentWrapper(
    key: Identifier,
    component: Equippable,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f, Alignment.Right),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    onValueChange: (Equippable, Boolean) -> Unit,
) = DataComponentWrapperRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    Button(
        Modifier.width(140f)
    ) {
        Text(IGLang.edit)
        click {
            EquippableEditor(key.asTranslateText(), component, modifier, onValueChange = onValueChange).open()
        }
    }
}

fun EquippableEditor(
    title: Text,
    component: Equippable,
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    onValueChange: (Equippable, Boolean) -> Unit,
): IGScreenImpl {
    val slot = component.slot.asMutableState
    val equipSound = component.equipSound.asMutableState
    val assetId = component.assetId.getOrNull().asMutableState
    val cameraOverlay = component.cameraOverlay.getOrNull().asMutableState
    val allowedEntities = component.allowedEntities.getOrNull().asMutableState
    val dispensable = component.dispensable.asMutableState
    val swappable = component.swappable.asMutableState
    val damageOnHurt = component.damageOnHurt.asMutableState
    val equipOnInteract = component.equipOnInteract.asMutableState
    val canBeSheared = component.canBeSheared.asMutableState
    val shearingSound = component.shearingSound.asMutableState
    return DataComponentEditor(
        title,
        {
            Equippable(
                slot.getValue(),
                equipSound.getValue(),
                Optional.ofNullable(assetId.getValue()),
                Optional.ofNullable(cameraOverlay.getValue()),
                Optional.ofNullable(allowedEntities.getValue()),
                dispensable.getValue(),
                swappable.getValue(),
                damageOnHurt.getValue(),
                equipOnInteract.getValue(),
                canBeSheared.getValue(),
                shearingSound.getValue()
            ) to true
        },
        onValueChange,
        modifier = modifier,
        screenModifier = screenModifier
    ) {
        DialogContent {
            Column(verticalArrangement = Arrangement.spacedBy(5f)) {
                EntryRow("slot") { EnumSelector(slot, EquipmentSlot.entries, modifier = Modifier.width(200f)) }
                EntryRow("equip_sound") { HolderSoundEventSelector(equipSound, modifier = Modifier.width(200f)) }
                EntryRow("asset_id") {
                    Row(Modifier.width(200f), horizontalArrangement = Arrangement.spacedBy(5f)) {
                        Button(
                            Modifier.hoverText(IGLang.edit).weight(1)
                        ) {
                            val value = assetId.getValue()
                            Text(assetId.getValue()?.identifier()?.asTranslateText() ?: Literal("null"))
                            click {
                                EquipmentAssetEdiotr(assetId.getValue() ?: EquipmentAssets.GOLD) {
                                    assetId.setValue(it)
                                    this@EntryRow.executeRecompose()
                                }.open()
                            }
                        }
                        RemoveButton {
                            assetId.setValue(null)
                            this@EntryRow.executeRecompose()
                        }
                    }
                }
                EntryRow("camera_overlay") {
                    Row(Modifier.width(200f), horizontalArrangement = Arrangement.spacedBy(5f)) {
                        Button(
                            Modifier.hoverText(IGLang.edit).weight(1)
                        ) {
                            Text(cameraOverlay.getValue()?.asTranslateText() ?: Literal("null"))
                            click {
                                IdentifierEditor(
                                    Literal("camera_overlay"),
                                    cameraOverlay.getValue() ?: identifier("minecraft", "misc/pumpkinblur")
                                ) { it, _ ->
                                    cameraOverlay.setValue(it)
                                    this@EntryRow.executeRecompose()
                                }.open()
                            }
                        }
                        RemoveButton {
                            cameraOverlay.setValue(null)
                            this@EntryRow.executeRecompose()
                        }
                    }
                }
                EntryRow("allowed_entities") {
                    Row(Modifier.width(200f), horizontalArrangement = Arrangement.spacedBy(5f)) {
                        Button(
                            Modifier.weight(1)
                                .hoverTip {
                                    val list = allowedEntities.getValue()?.toList() ?: emptyList()
                                    if (list.isEmpty()) {
                                        Text(IGLang.hasNothing)
                                    } else {
                                        Column {
                                            list.forEachWithLimit(10) {
                                                Text(it.value().description)
                                            }
                                        }
                                    }
                                }
                        ) {
                            Text(IGLang.listConfigWrapperText(allowedEntities.getValue()?.size() ?: 0))
                            click {
                                AllowedEntitiesEditor(allowedEntities.getValue() ?: HolderSet.empty()) {
                                    allowedEntities.setValue(it)
                                    this@EntryRow.executeRecompose()
                                }.open()
                            }
                        }
                        RemoveButton {
                            allowedEntities.setValue(null)
                            this@EntryRow.executeRecompose()
                        }
                    }
                }
                EntryRow("dispensable") { SwitchButton(dispensable) }
                EntryRow("swappable") { SwitchButton(swappable) }
                EntryRow("damage_on_hurt") { SwitchButton(damageOnHurt) }
                EntryRow("equip_on_interact") { SwitchButton(equipOnInteract) }
                EntryRow("can_be_sheared") { SwitchButton(canBeSheared) }
                EntryRow("shearing_sound") { HolderSoundEventSelector(shearingSound, modifier = Modifier.width(200f)) }
            }
        }
    }
}

private fun ContainerScope.EntryRow(title: String, content: RowScope. () -> Unit) {
    Row(Modifier.width(300f), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(title)
        content()
    }
}

private fun AllowedEntitiesEditor(
    value: HolderSet<EntityType<*>>,
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    onValueChange: (HolderSet<EntityType<*>>) -> Unit
) = ConfirmDialog(
    Text.literal("allowed_entities").asState,
    modifier,
    screenModifier
) {
    val set = value.map { it.value() }.toMutableList()
    var recompose = {}

    onConfirm = {
        onValueChange(HolderSet.direct(EntityType<*>::builtInRegistryHolder, *set.toTypedArray()))
        closeScreen()
    }

    Row(Modifier.matchSibling(), horizontalArrangement = Arrangement.SpaceBetween) {
        Row(horizontalArrangement = Arrangement.spacedBy(5f)) {
            var toggle by lateInitValueOf {}
            EntityTypeSelector(
                mutableStateOf(BuiltInRegistries.ENTITY_TYPE.first()),
                modifier = Modifier.hoverText(IGLang.add).width(140f),
                onSelected = { type ->
                    toggle()
                    if (!set.contains(type)) {
                        set.add(type)
                        recompose()
                    } else {
                        Toast.showToast(HSLang.itemEditorItemComponentExist(type.description))
                    }
                },
                optionsDirection = listOf(Direction.Bottom)
            ) {
                toggle = { this.toggle() }
            }
        }
    }

    TableWrapped(set, Modifier.width(260f), tableModifier = { Modifier.height(150f) }) {
        recompose = { executeRecompose() }
        Header(1) {
            Text("entityType", setting = TextWidget.Setting(horizontalAlignment = Alignment.CenterHorizontally))
        }.Column { index, _ ->
            EntityTypeSelector(set[index].asMutableState, onSelected = {
                set[index] = it
            }, modifier = Modifier.width(555f))
        }

        Header {
            Text(text = IGLang.remove, setting = TextWidget.Setting(horizontalAlignment = Alignment.CenterHorizontally))
        }.Column { index, _ ->
            DeleteButton(
                { HSLang.deleteConfirm(set[index].description) },
                { recompose() }
            ) {
                set.removeAt(index)
            }
        }
    }
}


val vanillaEquipmentAssets by lazy {
    buildList {
        add(EquipmentAssets.LEATHER)
        add(EquipmentAssets.COPPER)
        add(EquipmentAssets.CHAINMAIL)
        add(EquipmentAssets.IRON)
        add(EquipmentAssets.GOLD)
        add(EquipmentAssets.DIAMOND)
        add(EquipmentAssets.TURTLE_SCUTE)
        add(EquipmentAssets.NETHERITE)
        add(EquipmentAssets.ARMADILLO_SCUTE)
        add(EquipmentAssets.ELYTRA)
        add(EquipmentAssets.SADDLE)
        EquipmentAssets.CARPETS.forEach { add(it.value) }
        add(EquipmentAssets.TRADER_LLAMA)
        EquipmentAssets.HARNESSES.forEach { add(it.value) }
    }
}

private var TIP: Tip? = null

fun EquipmentAssetEdiotr(
    value: ResourceKey<EquipmentAsset>,
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    onValueChange: (ResourceKey<EquipmentAsset>) -> Unit
) = ConfirmDialog(
    Text.literal("EquipmentAsset").asState,
    modifier,
    screenModifier.onClose { TipHandler.popTip(TIP) }
) {
    val namespaceState = value.identifier().namespace.asMutableState
    var namespaceEditor: (() -> Transform)? = null
    val pathState = value.identifier().path.asMutableState
    var pathEditor: (() -> Transform)? = null
    onConfirm = {
        val namespaceValid = Identifier.isValidNamespace(namespaceState.getValue())
        if (!namespaceValid) {
            namespaceEditor?.let {
                TipHandler.popTip(TIP)
                TIP = TipHandler.pushTip(5.seconds, it, Tip {
                    Text(
                        Literal("Non [a-z0-9_.-] character in namespace of location: ${namespaceState.getValue()}")
                            .withColor(Colors.RED)
                    )
                })
            }
        }
        val pathValid = Identifier.isValidPath(pathState.getValue())
        if (!pathValid) {
            pathEditor?.let {
                TipHandler.popTip(TIP)
                TIP = TipHandler.pushTip(5.seconds, it, Tip {
                    Text(
                        Literal("Non [a-z0-9/._-] character in path of location: ${pathState.getValue()}")
                            .withColor(Colors.RED)
                    )
                })
            }
        }
        if (namespaceValid && pathValid) {
            onValueChange(ResourceKey.create(EquipmentAssets.ROOT_ID, Identifier.fromNamespaceAndPath(namespaceState.getValue(), pathState.getValue())))
            closeScreen()
        }
    }
    DialogContent {
        Column(Modifier.width(260f), verticalArrangement = Arrangement.spacedBy(5f)) {
            Row(Modifier.fill(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("namespace")
                TextEditor(Modifier.width(180f)) {
                    bindState(namespaceState)
                    namespaceEditor = { this.owner().transform }
                }
            }
            Row(Modifier.fill(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("path")
                TextEditor(Modifier.width(180f)) {
                    bindState(pathState)
                    pathEditor = { this.owner().transform }
                }
            }
            Row(Modifier.fill(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Form Vanilla")
                Selector(
                    vanillaEquipmentAssets,
                    (vanillaEquipmentAssets.find { it.identifier() == value.identifier() } ?: vanillaEquipmentAssets.first()).asMutableState,
                    modifier = Modifier.width(180f),
                    selectedWrapper = {
                        Text(it.identifier().toString(), modifier = Modifier.weight(1))
                    },
                    optionWrapper = {
                        Text(it.identifier().toString(), modifier = Modifier.width(vanillaEquipmentAssets.map { it.identifier().toString() }.maxWidth))
                    },
                    onSelected = {
                        namespaceState.setValue(it.identifier().namespace)
                        pathState.setValue(it.identifier().path)
                    },
                    optionsDirection = listOf(Direction.Bottom, Direction.Top),
                    amountStep = 15f
                )
            }
        }
    }
}
