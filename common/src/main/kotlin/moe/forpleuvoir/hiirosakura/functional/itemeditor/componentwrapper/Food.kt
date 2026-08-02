package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEditorDefaults
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEntryRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.hiirosakura.platform.PLATFORM
import moe.forpleuvoir.ibukigourd.render.extension.texture.Corner
import moe.forpleuvoir.ibukigourd.render.extension.texture.IGTexture
import moe.forpleuvoir.ibukigourd.render.extension.texture.TextureInfo
import moe.forpleuvoir.ibukigourd.render.extension.texture.TextureUVMapping
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.defaults.EditNote
import moe.forpleuvoir.ibukigourd.ui.preset.*
import net.minecraft.resources.Identifier
import net.minecraft.world.food.FoodProperties
import kotlin.math.absoluteValue
import kotlin.math.ceil

private val FOOD_EMPTY_HUNGER_TEXTURE = Identifier.withDefaultNamespace("textures/gui/sprites/hud/food_empty_hunger.png")
private val FOOD_EMPTY_TEXTURE = Identifier.withDefaultNamespace("textures/gui/sprites/hud/food_empty.png")
private val FOOD_HALF_TEXTURE = Identifier.withDefaultNamespace("textures/gui/sprites/hud/food_half.png")
private val FOOD_FULL_TEXTURE = Identifier.withDefaultNamespace("textures/gui/sprites/hud/food_full.png")

private val APPLESKIN_TEXTURE = TextureInfo(256, 256, Identifier.fromNamespaceAndPath("appleskin", "textures/icons.png"))

private val APPLESKIN_SATURATION_25 = IGTexture(TextureUVMapping(Corner.Unspecified, 0, 27, 7, 34), APPLESKIN_TEXTURE)
private val APPLESKIN_SATURATION_50 = IGTexture(TextureUVMapping(Corner.Unspecified, 7, 27, 14, 34), APPLESKIN_TEXTURE)
private val APPLESKIN_SATURATION_75 = IGTexture(TextureUVMapping(Corner.Unspecified, 14, 27, 21, 34), APPLESKIN_TEXTURE)
private val APPLESKIN_SATURATION_100 = IGTexture(TextureUVMapping(Corner.Unspecified, 21, 27, 28, 34), APPLESKIN_TEXTURE)


@Composable
fun FoodComponentWrapper(
    key: Identifier,
    value: FoodProperties,
    onValueChange: (FoodProperties) -> Unit,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp, Alignment.End),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) = DataComponentEntryRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    Box(modifier = Modifier.height(DataComponentEditorDefaults.entrySize.height).padding(vertical = 4.dp)) {
        var showDialog by remember { mutableStateOf(false) }
        AssistChip(
            {},
            modifier = Modifier
                .fillMaxHeight()
                .width(DataComponentEditorDefaults.entrySize.width),
            label = {
                val icon = 22.dp
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    //饱食度
                    Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        val nutrition = (value.nutrition / 2)
                        val remainder = value.nutrition % 2
                        if (remainder > 0) {
                            FoodIcon(FOOD_HALF_TEXTURE, Modifier.size(icon))
                        }
                        if (nutrition > 5) {
                            FoodIcon(FOOD_FULL_TEXTURE, Modifier.size(icon))
                            Spacer(Modifier.width(4.dp))
                            Text("×$nutrition")
                        } else {
                            repeat(nutrition) {
                                FoodIcon(FOOD_FULL_TEXTURE, Modifier.size(icon))
                            }
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    //饱和度
                    Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        val saturation = value.saturation / 2f
                        val absoluteSaturation = saturation.absoluteValue

                        if (PLATFORM.isModLoaded("appleskin")) {
                            val quarterCount = ceil(absoluteSaturation * 4f).toInt()
                            val fullCount = quarterCount / 4
                            val remainingQuarter = quarterCount % 4

                            val remainingTexture = when (remainingQuarter) {
                                1    -> APPLESKIN_SATURATION_25
                                2    -> APPLESKIN_SATURATION_50
                                3    -> APPLESKIN_SATURATION_75
                                else -> null
                            }

                            remainingTexture?.let {
                                BlitTexture(
                                    it,
                                    Modifier.size(icon),
                                )
                            }

                            if (absoluteSaturation > 5f) {
                                BlitTexture(
                                    APPLESKIN_SATURATION_100,
                                    Modifier.size(icon),
                                )
                                Spacer(Modifier.width(4.dp))
                                Text("×$fullCount")
                            } else {
                                repeat(fullCount) {
                                    BlitTexture(
                                        APPLESKIN_SATURATION_100,
                                        Modifier.size(icon),
                                    )
                                }
                            }
                        } else {
                            val saturationCount = saturation.toInt()

                            if (saturationCount.absoluteValue > 5) {
                                BlitTexture(
                                    FOOD_EMPTY_HUNGER_TEXTURE,
                                    Modifier.size(icon),
                                )
                                Spacer(Modifier.width(4.dp))
                                Text("×$saturationCount")
                            } else {
                                repeat(saturationCount.absoluteValue) {
                                    BlitTexture(
                                        FOOD_EMPTY_HUNGER_TEXTURE,
                                        Modifier.size(icon),
                                    )
                                }
                            }
                        }
                    }
                }
            },
            trailingIcon = {
                IconButton(onClick = {
                    showDialog = true
                }) {
                    Icon(Icons.EditNote, null)
                }
            }
        )

        if (showDialog) {
            var editingFood by remember { mutableStateOf(value) }
            SimpleAlertDialog(
                { showDialog = false },
                onConfirmRequest = {
                    onValueChange(editingFood)
                    true
                },
                title = {
                    Text(key)
                },
                content = {
                    CompositionLocalProvider(LocalNumberFieldStyle provides NumberFieldStyle.Outlined) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            IntField(
                                editingFood.nutrition,
                                {
                                    editingFood = FoodProperties(it, editingFood.saturation, editingFood.canAlwaysEat)
                                },
                                labelPosition = TextFieldLabelPosition.Attached(true),
                                label = { Text(key, suffix = "nutrition", fallback = "Nutrition") },
                                range = 0..Int.MAX_VALUE,
                                modifier = Modifier.width(300.dp)
                            )
                            FloatField(
                                editingFood.saturation,
                                {
                                    editingFood = FoodProperties(editingFood.nutrition, it, editingFood.canAlwaysEat)
                                },
                                labelPosition = TextFieldLabelPosition.Attached(true),
                                label = { Text(key, suffix = "saturation", fallback = "Saturation") },
                                modifier = Modifier.width(300.dp)
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.width(300.dp)
                            ) {
                                Text(key, suffix = "can_always_eat", fallback = "Can Always Eat")
                                Switch(
                                    editingFood.canAlwaysEat,
                                    {
                                        editingFood = FoodProperties(editingFood.nutrition, editingFood.saturation, it)
                                    },
                                )
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun FoodIcon(icon: Identifier, modifier: Modifier = Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        BlitTexture(FOOD_EMPTY_TEXTURE, Modifier.fillMaxSize())
        BlitTexture(icon, Modifier.fillMaxSize())
    }
}