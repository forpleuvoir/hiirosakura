package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDialogTitle
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDisplayRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentField
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentSection
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import com.mojang.blaze3d.textures.FilterMode
import moe.forpleuvoir.compose_minecraft.platform.render.plugins.UVMapping
import moe.forpleuvoir.compose_minecraft.platform.ui.draw.minecraftTexture
import moe.forpleuvoir.hiirosakura.platform.PLATFORM
import moe.forpleuvoir.ibukigourd.render.extension.texture.Corner
import moe.forpleuvoir.ibukigourd.render.extension.texture.IGTexture
import moe.forpleuvoir.ibukigourd.render.extension.texture.TextureInfo
import moe.forpleuvoir.ibukigourd.render.extension.texture.TextureUVMapping
import net.minecraft.resources.Identifier
import net.minecraft.world.food.FoodProperties
import kotlin.math.absoluteValue
import kotlin.math.ceil
import moe.forpleuvoir.ibukigourd.ui.sokitsu.SimpleAlertDialog
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IntField
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FloatField
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Switch
import androidx.compose.foundation.layout.fillMaxWidth

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
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) {
    var showDialog by remember { mutableStateOf(false) }
    DataComponentDisplayRow(
        key, removeAction, modifier, horizontalArrangement, verticalAlignment,
        onEdit = { showDialog = true },
    ) {
        val icon = 22.dp
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            //楗遍搴?
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
            //楗卞拰搴?
            Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                val saturation = value.saturation / 2f
                val absoluteSaturation = saturation.absoluteValue

                if (PLATFORM.isModLoaded("appleskin")) {
                    val quarterCount = ceil(absoluteSaturation * 4f).toInt()
                    val fullCount = quarterCount / 4
                    val remainingQuarter = quarterCount % 4

                    val remainingTexture = when (remainingQuarter) {
                        1 -> APPLESKIN_SATURATION_25
                        2 -> APPLESKIN_SATURATION_50
                        3 -> APPLESKIN_SATURATION_75
                        else -> null
                    }

                    remainingTexture?.let {
                        FoodSprite(it, Modifier.size(icon))
                    }


                    if (absoluteSaturation > 5f) {
                        FoodSprite(APPLESKIN_SATURATION_100, Modifier.size(icon))
                        Spacer(Modifier.width(4.dp))
                        Text("×$fullCount")
                    } else {
                        repeat(fullCount) {
                            FoodSprite(APPLESKIN_SATURATION_100, Modifier.size(icon))
                        }
                    }
                } else {
                    val saturationCount = saturation.toInt()

                    if (saturationCount.absoluteValue > 5) {
                        Spacer(
                            Modifier
                                .size(icon)
                                .minecraftTexture(FOOD_EMPTY_HUNGER_TEXTURE, filterMode = FilterMode.NEAREST)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("×$saturationCount")
                    } else {
                        repeat(saturationCount.absoluteValue) {
                            Spacer(
                                Modifier
                                    .size(icon)
                                    .minecraftTexture(FOOD_EMPTY_HUNGER_TEXTURE, filterMode = FilterMode.NEAREST)
                            )
                        }
                    }
                }
            }
        }
    }
    if (showDialog) {
        var editingFood by remember { mutableStateOf(value) }
        SimpleAlertDialog(
            { showDialog = false },
            onConfirmRequest = {
                onValueChange(editingFood)
                true
            },
            title = {
                DataComponentDialogTitle(key)
            },
            content = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DataComponentSection(
                            key,
                            suffix = "nutrition",
                            fallback = "Nutrition",
                            modifier = Modifier.weight(1f),
                        ) {
                            IntField(
                                editingFood.nutrition,
                                { editingFood = FoodProperties(it, editingFood.saturation, editingFood.canAlwaysEat) },
                                valueRange = 0..Int.MAX_VALUE,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                        DataComponentSection(
                            key,
                            suffix = "saturation",
                            fallback = "Saturation",
                            modifier = Modifier.weight(1f),
                        ) {
                            FloatField(
                                editingFood.saturation,
                                { editingFood = FoodProperties(editingFood.nutrition, it, editingFood.canAlwaysEat) },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth(),
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
        )
    }
}

@Composable
private fun FoodIcon(icon: Identifier, modifier: Modifier = Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Spacer(Modifier.fillMaxSize().minecraftTexture(FOOD_EMPTY_TEXTURE, filterMode = FilterMode.NEAREST))
        Spacer(Modifier.fillMaxSize().minecraftTexture(icon, filterMode = FilterMode.NEAREST))
    }
}

/** 按贴图携带的 UV 子区域绘制；绘制尺寸由 [modifier] 给出。 */
@Composable
private fun FoodSprite(texture: IGTexture, modifier: Modifier = Modifier) = Spacer(
    modifier.minecraftTexture(
        textureId = texture.textureInfo.textureId,
        filterMode = FilterMode.NEAREST,
        uv = UVMapping(texture.uStart, texture.vStart, texture.uEnd, texture.vEnd),
    )
)
