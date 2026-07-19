package moe.forpleuvoir.hiirosakura.test

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

import moe.forpleuvoir.hiirosakura.ui.widget.radialmenu.RadialMenu
import moe.forpleuvoir.hiirosakura.ui.widget.radialmenu.RadialMenuDefaults
import moe.forpleuvoir.hiirosakura.ui.widget.radialmenu.rememberRadialMenuState
import moe.forpleuvoir.ibukigourd.ui.openComposeScreen
import moe.forpleuvoir.ibukigourd.ui.platformcontext.IbukiGourdTheme
import moe.forpleuvoir.ibukigourd.ui.preset.ItemIcon
import moe.forpleuvoir.ibukigourd.ui.preset.Text

private val testItems = listOf(
    ItemStack(Items.LAPIS_LAZULI),
    ItemStack(Items.QUARTZ),
    ItemStack(Items.AMETHYST_SHARD),
    ItemStack(Items.RAW_IRON),
    ItemStack(Items.IRON_INGOT),
    ItemStack(Items.RAW_COPPER),
    ItemStack(Items.COPPER_INGOT),
    ItemStack(Items.RAW_GOLD),
    ItemStack(Items.GOLD_INGOT),
    ItemStack(Items.NETHERITE_INGOT),
    ItemStack(Items.NETHERITE_SCRAP),
    ItemStack(Items.WOODEN_SWORD),
    ItemStack(Items.WOODEN_SHOVEL),
    ItemStack(Items.WOODEN_PICKAXE),
    ItemStack(Items.WOODEN_AXE),
    ItemStack(Items.WOODEN_HOE),
    ItemStack(Items.STONE_SWORD),
    ItemStack(Items.STONE_SHOVEL),
    ItemStack(Items.STONE_PICKAXE),
    ItemStack(Items.STONE_AXE),
    ItemStack(Items.STONE_HOE),
    ItemStack(Items.GOLDEN_SWORD),
    ItemStack(Items.GOLDEN_SHOVEL),
    ItemStack(Items.GOLDEN_PICKAXE),
    ItemStack(Items.GOLDEN_AXE),
    ItemStack(Items.GOLDEN_HOE),
    ItemStack(Items.IRON_SWORD),
    ItemStack(Items.IRON_SHOVEL),
    ItemStack(Items.IRON_PICKAXE),
)

fun openRadialMenuTest() = openComposeScreen(shouldRenderLevel = { true }) {
    IbukiGourdTheme {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            val selectedStack = remember { mutableStateOf<ItemStack?>(null) }
            RadialMenu(
                options = testItems,
                state = rememberRadialMenuState(),
                innerRadius = 150.dp,
                outerRadius = 330.dp,
                optionRadius = 240.dp,
                optionsPerPage = 8,
                optionContentSize = 72.dp,
                onOptionClick = { stack, _ ->
                    selectedStack.value = stack
                },
                optionContent = { stack, _ ->
                    ItemIcon(stack, showTooltip = false)
                },
                centerContent = { stack ->
                    stack?.let { s ->
                        Text(
                            s.displayName,
                            color = RadialMenuDefaults.SelectedOuterColor,
                        )
                    }
                },
            )
        }
    }
}
