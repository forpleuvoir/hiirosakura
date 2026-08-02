package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEditorDefaults
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEntryRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.hiirosakura.ui.widget.*
import moe.forpleuvoir.hiirosakura.util.asTranslateText
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.Texts
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.defaults.Add
import moe.forpleuvoir.ibukigourd.ui.icon.defaults.EditNote
import moe.forpleuvoir.ibukigourd.ui.preset.*
import moe.forpleuvoir.ibukigourd.ui.preset.modifier.fabVisibilityAnimation
import moe.forpleuvoir.ibukigourd.ui.preset.modifier.plainTooltip
import moe.forpleuvoir.ibukigourd.ui.preset.state.rememberFabVisibilityByScroll
import moe.forpleuvoir.ibukigourd.ui.util.Keyed
import moe.forpleuvoir.ibukigourd.ui.util.copyValue
import moe.forpleuvoir.ibukigourd.ui.util.rememberKeyedList
import moe.forpleuvoir.ibukigourd.ui.util.values
import moe.forpleuvoir.ibukigourd.util.moveElement
import net.minecraft.core.Holder
import net.minecraft.core.HolderSet
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.item.ItemUseAnimation
import net.minecraft.world.item.component.Consumable
import net.minecraft.world.item.consume_effects.*
import sh.calvin.reorderable.ReorderableCollectionItemScope

@Composable
fun ConsumableComponentWrapper(
    key: Identifier,
    value: Consumable,
    onValueChange: (Consumable) -> Unit,
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
            ConsumableEditorDialog(
                key = key,
                value = value,
                onValueChange = onValueChange,
                onDismissRequest = { showDialog = false },
                title = { Text(key) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ConsumableEditorDialog(
    key: Identifier,
    value: Consumable,
    onValueChange: (Consumable) -> Unit,
    onDismissRequest: () -> Unit,
    title: @Composable () -> Unit,
) {
    var editing by remember { mutableStateOf(value) }
    val onConsumeEffects = rememberKeyedList(value.onConsumeEffects)
    var nextKey by remember { mutableLongStateOf(onConsumeEffects.size.toLong()) }
    FlexibleDialog(
        onDismissRequest,
        onConfirmRequest = {
            onValueChange(editing.copy(onConsumeEffects = onConsumeEffects.values().toMutableList()))
            true
        },
        modifier = Modifier
            .width(920.dp)
            .height(720.dp)
            .padding(24.dp),
        title = title,
        content = {
            Column {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Bottom,
                ) {
                    FloatField(
                        editing.consumeSeconds,
                        { editing = editing.copy(consumeSeconds = it) },
                        range = 0f..64.0f,
                        label = { Text(key, suffix = "consume_seconds") },
                        modifier = Modifier.weight(1f).height(64.dp)
                    )
                    EnumSelector(
                        editing.animation,
                        { editing = editing.copy(animation = it) },
                        label = { Text(key, suffix = "animation") },
                        modifier = Modifier.weight(1f).height(64.dp)
                    )
                    HolderSoundEventSelector(
                        editing.sound,
                        { editing = editing.copy(sound = it) },
                        label = { Text(key, suffix = "sound") },
                        modifier = Modifier.weight(1f).height(64.dp)
                    )
                    OutlinedToggleButton(
                        editing.hasConsumeParticles,
                        { editing = editing.copy(hasConsumeParticles = it) },
                        modifier = Modifier.weight(1f).height(56.dp)
                    ) {
                        Text(key, suffix = "has_consume_particles")
                        Spacer(Modifier.width(8.dp))
                        Text(IGLang.Misc.coloredSwitch(editing.hasConsumeParticles))
                    }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedLabelBox(
                    label = { Text(key, suffix = "on_consume_effects") },
                    contentPadding = PaddingValues(8.dp, 12.dp),
                ) {
                    ReorderableEditorList(
                        onConsumeEffects,
                        key = { it.key },
                        onMove = onConsumeEffects::removeRange,
                        modifier = Modifier.fillMaxSize(),
                        floatingActionButton = { lazyListState ->
                            FloatingAddButton(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd),
                                fabVisibilityState = rememberFabVisibilityByScroll(lazyListState),
                                addMenuOptions = ConsumableEditor.consumeEffectAddMenuOptions,
                                addAction = {
                                    onConsumeEffects.add(Keyed(nextKey++, it))
                                }
                            )
                        },
                        itemContent = { index, effect, isDragging, hapticFeedback ->
                            val scale by animateFloatAsState(if (isDragging) 1.015f else 1.0f)
                            val handleInteraction = remember { MutableInteractionSource() }
                            val handleHovered by handleInteraction.collectIsHoveredAsState()

                            val wrapper = ConsumableEditor.consumeEffectsWrappers[effect.value.type]
                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth().scale(scale)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    ) {
                                        DragHandle(
                                            hapticFeedback,
                                            handleInteraction,
                                            handleHovered,
                                            isDragging
                                        )
                                        wrapper?.title()
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        wrapper?.content(effect.value, {
                                            onConsumeEffects[index] = effect.copyValue(it)
                                        })

                                        RemoveConfirmButton(
                                            ConsumeEffect.Type.APPLY_EFFECTS.id.asTranslateText().plainText,
                                            { onConsumeEffects.removeAt(index) }
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    )
}

object ConsumableEditor {

    val LocalConsumeEffectWrapperSize = staticCompositionLocalOf {
        DpSize(280.dp, 56.dp)
    }

    val consumeEffectsWrappers = mutableMapOf<ConsumeEffect.Type<*>, ConsumeEffectWrapper>(
        ConsumeEffect.Type.APPLY_EFFECTS to ConsumeEffectWrapper(
            title = { Text(ConsumeEffect.Type.APPLY_EFFECTS.id) },
            content = { value, onValueChange ->
                val value = value as ApplyStatusEffectsConsumeEffect
                Box(
                    modifier = Modifier.size(LocalConsumeEffectWrapperSize.current).padding(vertical = 4.dp),
                ) {
                    var showDialog by remember { mutableStateOf(false) }
                    AssistChip(
                        {},
                        modifier = Modifier.fillMaxSize().plainTooltip {
                            value.effects.take(20).forEach {
                                Row {
                                    Text(it.effect.value().displayName)
                                    Spacer(Modifier.width(8.dp))
                                    Text(Texts.translatable("enchantment.level.${it.amplifier}", it.amplifier.toString()))
                                }
                            }
                            if (value.effects.size > 20) Text("...")
                        },
                        label = {
                            Text(IGLang.ConfigWrapper.listConfigWrapperText(value.effects.size))
                        },
                        trailingIcon = {
                            IconButton({ showDialog = true }) {
                                Icon(Icons.EditNote, null)
                            }
                        }
                    )
                    if (showDialog) {
                        ApplyStatusEditorDialog({ showDialog = false }, value, { onValueChange(it) })
                    }
                }
            }
        ),
        ConsumeEffect.Type.REMOVE_EFFECTS to ConsumeEffectWrapper(
            title = { Text(ConsumeEffect.Type.REMOVE_EFFECTS.id) },
            content = { value, onValueChange ->
                val value = value as RemoveStatusEffectsConsumeEffect
                Box(
                    modifier = Modifier.size(LocalConsumeEffectWrapperSize.current).padding(vertical = 4.dp),
                ) {
                    var showDialog by remember { mutableStateOf(false) }
                    AssistChip(
                        {},
                        modifier = Modifier.fillMaxSize().plainTooltip {
                            value.effects.take(20).forEach {
                                Row {
                                    Text(it.value().displayName)
                                }
                            }
                            if (value.effects.count() > 20) Text("...")
                        },
                        label = {
                            Text(IGLang.ConfigWrapper.listConfigWrapperText(value.effects.count()))
                        },
                        trailingIcon = {
                            IconButton({ showDialog = true }) {
                                Icon(Icons.EditNote, null)
                            }
                        }
                    )
                    if (showDialog) {
                        RemoveStatusEditorDialog({ showDialog = false }, value, { onValueChange(it) })
                    }
                }
            }
        ),
        ConsumeEffect.Type.CLEAR_ALL_EFFECTS to ConsumeEffectWrapper(
            title = { Text(ConsumeEffect.Type.CLEAR_ALL_EFFECTS.id) },
            content = { _, _ -> Spacer(Modifier.size(LocalConsumeEffectWrapperSize.current)) }
        ),
        ConsumeEffect.Type.TELEPORT_RANDOMLY to ConsumeEffectWrapper(
            title = { Text(ConsumeEffect.Type.TELEPORT_RANDOMLY.id) },
            content = { value, onValueChange ->
                val value = value as TeleportRandomlyConsumeEffect
                Box(
                    modifier = Modifier.size(LocalConsumeEffectWrapperSize.current).padding(vertical = 4.dp),
                ) {
                    FloatField(
                        value.diameter,
                        { onValueChange(TeleportRandomlyConsumeEffect(it)) },
                        range = 0f..Float.MAX_VALUE,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        ),
        ConsumeEffect.Type.PLAY_SOUND to ConsumeEffectWrapper(
            title = { Text(ConsumeEffect.Type.PLAY_SOUND.id) },
            content = { value, onValueChange ->
                val value = value as PlaySoundConsumeEffect
                Box(
                    modifier = Modifier.size(LocalConsumeEffectWrapperSize.current).padding(vertical = 4.dp),
                ) {
                    HolderSoundEventSelector(
                        value.sound,
                        { onValueChange(PlaySoundConsumeEffect(it)) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        )
    )

    class ConsumeEffectWrapper(
        val title: @Composable () -> Unit,
        val content: @Composable (value: ConsumeEffect, onValueChange: (ConsumeEffect) -> Unit) -> Unit
    )

    val consumeEffectAddMenuOptions: MutableList<AddMenuOption<ConsumeEffect>> = mutableListOf(
        AddMenuOption(
            { Text(ConsumeEffect.Type.APPLY_EFFECTS.id) },
            { ApplyStatusEffectsConsumeEffect(MobEffectInstance(MobEffects.LUCK)) }
        ) { addAction, defaultValue, onDismiss ->
            ApplyStatusEditorDialog(onDismiss, defaultValue() as ApplyStatusEffectsConsumeEffect, addAction)
        },
        AddMenuOption(
            { Text(ConsumeEffect.Type.REMOVE_EFFECTS.id) },
            { RemoveStatusEffectsConsumeEffect(HolderSet.empty()) }
        ) { addAction, defaultValue, onDismiss ->
            RemoveStatusEditorDialog(onDismiss, defaultValue() as RemoveStatusEffectsConsumeEffect, addAction)
        },
        AddMenuOption(
            { Text(ConsumeEffect.Type.CLEAR_ALL_EFFECTS.id) },
            { ClearAllStatusEffectsConsumeEffect.INSTANCE },
            null
        ),
        AddMenuOption(
            { Text(ConsumeEffect.Type.TELEPORT_RANDOMLY.id) },
            { TeleportRandomlyConsumeEffect() },
            null
        ),
        AddMenuOption(
            { Text(ConsumeEffect.Type.PLAY_SOUND.id) },
            { PlaySoundConsumeEffect(BuiltInRegistries.SOUND_EVENT.get(0).get()) },
            null
        )
    )

}


//region ApplyStatus

@Composable
fun ApplyStatusEditorDialog(
    onDismissRequest: () -> Unit,
    value: ApplyStatusEffectsConsumeEffect,
    onValueChange: (ApplyStatusEffectsConsumeEffect) -> Unit,
) {
    var probability by remember { mutableFloatStateOf(value.probability) }
    val list = rememberKeyedList(value.effects)
    var nextKey by remember { mutableLongStateOf(list.size.toLong()) }
    val id = ConsumeEffect.Type.APPLY_EFFECTS.id
    FlexibleDialog(
        onDismissRequest,
        onConfirmRequest = {
            onValueChange(ApplyStatusEffectsConsumeEffect(list.values().toMutableList(), probability))
            true
        },
        title = { Text(id) },
        modifier = Modifier.padding(24.dp),
        content = {
            BoxWithConstraints {
                Column(
                    Modifier.size(if (maxWidth > 1150.dp) 1150.dp else 780.dp, 725.dp)
                ) {
                    Row {
                        FloatField(
                            probability,
                            { probability = it },
                            label = { Text(id, suffix = "probability") },
                            range = 0f..1f,
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedLabelBox(
                        label = { Text(id, suffix = "effects") },
                        contentPadding = PaddingValues(8.dp, 12.dp),
                    ) {
                        ReorderableEditorVerticalGrid(
                            list,
                            key = { it.key },
                            onMove = list::moveElement,
                            columns = GridCells.Adaptive(360.dp),
                            floatingActionButton = { lazyGridState ->
                                var showAddDialog by remember { mutableStateOf(false) }
                                FloatingActionButton(
                                    onClick = { showAddDialog = true },
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(12.dp)
                                        .size(40.dp)
                                        .fabVisibilityAnimation(rememberFabVisibilityByScroll(lazyGridState))
                                ) {
                                    Icon(Icons.Add, IGLang.Misc.add.plainText)
                                }
                                if (showAddDialog) {
                                    MobEffectInstanceEditorDialog(
                                        MobEffectInstance(MobEffects.LUCK),
                                        { list.add(Keyed(nextKey++, it)) },
                                        id,
                                        { showAddDialog = false },
                                        {
                                            Row {
                                                Text(IGLang.Misc.add)
                                                Spacer(Modifier.width(8.dp))
                                                Text(id)
                                            }
                                        }
                                    )
                                }
                            },
                            itemContent = { index, instance, isDragging, hapticFeedback ->
                                val scale by animateFloatAsState(if (isDragging) 1.015f else 1.0f)
                                val handleInteraction = remember { MutableInteractionSource() }
                                val handleHovered by handleInteraction.collectIsHoveredAsState()
                                MobEffectInstanceCard(
                                    instance.value,
                                    {
                                        if (instance == it) {
                                            list[index] = Keyed(nextKey++, it)
                                        } else {
                                            list[index] = instance.copyValue(it)
                                        }
                                    },
                                    { list.removeAt(index) },
                                    id,
                                    hapticFeedback,
                                    handleInteraction,
                                    handleHovered,
                                    isDragging,
                                    modifier = Modifier.scale(scale).width(360.dp),
                                )
                            }
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun ReorderableCollectionItemScope.MobEffectInstanceCard(
    value: MobEffectInstance,
    onValueChange: (MobEffectInstance) -> Unit,
    onRemove: () -> Unit,
    key: Identifier,
    hapticFeedback: HapticFeedback,
    handleInteraction: MutableInteractionSource,
    handleHovered: Boolean,
    isDragging: Boolean,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(16.dp, 8.dp, 16.dp, 16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                DragHandle(
                    hapticFeedback,
                    handleInteraction,
                    handleHovered,
                    isDragging
                )
                RemoveConfirmButton(
                    key.asTranslateText(suffix = "mob_effect_instance").plainText,
                    onRemove
                )
            }
            MobEffectInstanceEditorContent(value, onValueChange, key)
        }
    }
}

//endregion

//region RemoveStatus
@Composable
fun RemoveStatusEditorDialog(
    onDismissRequest: () -> Unit,
    value: RemoveStatusEffectsConsumeEffect,
    onValueChange: (RemoveStatusEffectsConsumeEffect) -> Unit,
) {
    HolderSetMobEffectEditorDialog(
        value.effects,
        { onValueChange(RemoveStatusEffectsConsumeEffect(it)) },
        onDismissRequest = onDismissRequest,
        { Text(ConsumeEffect.Type.CLEAR_ALL_EFFECTS.id) }
    )
}

//endregion

fun Consumable.copy(
    consumeSeconds: Float = this.consumeSeconds,
    animation: ItemUseAnimation = this.animation,
    sound: Holder<SoundEvent> = this.sound,
    hasConsumeParticles: Boolean = this.hasConsumeParticles,
    onConsumeEffects: List<ConsumeEffect> = this.onConsumeEffects,
) = Consumable(
    consumeSeconds.coerceIn(0f..Float.MAX_VALUE),
    animation,
    sound,
    hasConsumeParticles,
    onConsumeEffects,
)

val ConsumeEffect.Type<*>.id: Identifier
    get() = BuiltInRegistries.CONSUME_EFFECT_TYPE.getKey(this) ?: Identifier.parse("minecraft:unknown")