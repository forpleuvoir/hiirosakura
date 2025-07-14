package moe.forpleuvoir.hiirosakura.test

import com.mojang.serialization.JsonOps
import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.hiirosakura.functional.itemeditor.ItemStackEditor
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfoMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.MultiMatcher
import moe.forpleuvoir.hiirosakura.gui.widget.BlockInfoMatcherBuilder
import moe.forpleuvoir.hiirosakura.gui.widget.ItemStackMatcherBuilder
import moe.forpleuvoir.hiirosakura.gui.widget.RouletteSelector
import moe.forpleuvoir.hiirosakura.util.registryManager
import moe.forpleuvoir.ibukigourd.event.IbukiGourdEventManager
import moe.forpleuvoir.ibukigourd.event.events.ModInitializerEvent
import moe.forpleuvoir.ibukigourd.gui.base.extensions.drawcontext.renderAlignmentText
import moe.forpleuvoir.ibukigourd.gui.base.extensions.drawcontext.renderText
import moe.forpleuvoir.ibukigourd.gui.base.render.Size
import moe.forpleuvoir.ibukigourd.gui.base.render.shape.box.Box
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.base.toast.Toast
import moe.forpleuvoir.ibukigourd.gui.screen.BoxScreen
import moe.forpleuvoir.ibukigourd.gui.widget.renderItem
import moe.forpleuvoir.ibukigourd.input.InputHandler
import moe.forpleuvoir.ibukigourd.input.Keyboard
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.util.NebulaOps
import moe.forpleuvoir.ibukigourd.util.logger
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.common.api.ExperimentalApi
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.event.EventSubscriber
import moe.forpleuvoir.nebula.event.Subscriber
import moe.forpleuvoir.nebula.serialization.gson.gson
import moe.forpleuvoir.nebula.serialization.json.JsonSerializer.Companion.dumpAsJson
import net.minecraft.client.font.TextRenderer
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import kotlin.jvm.optionals.getOrNull

@EventSubscriber
object TestInitialization {
    internal val log = logger("HS TEST")

    @Subscriber
    fun init(event: ModInitializerEvent) {
        if (event.meta != HiiroSakura.metadata) return
        HiiroSakura.log.info("MOD测试")
        InputHandler.apply {
            register(Keyboard.KP_0) {
                IbukiGourdEventManager.eventSet().forEach {
                    println(it.qualifiedName)
                }
            }
            register(Keyboard.KP_1) {
                testScreen().open()
            }
            register(Keyboard.KP_2) {
                test2()
            }
            register(Keyboard.KP_3) {
                test3().open()
            }
            register(Keyboard.KP_4) {
                test4().open()
            }
            register(Keyboard.KP_5) {
                test5().open()
            }
        }

    }
}

fun testScreen() = ItemStackEditor { stack ->
    mc.player!!.inventory.insertStack(stack)
}


@OptIn(ExperimentalApi::class)
fun test2() {
    runCatching {
        ItemStackMatcher.handheldItemStack?.let { item ->
            ItemStack.CODEC.encodeStart(registryManager!!.getOps(NebulaOps), item).resultOrPartial {
                TestInitialization.log.info(it)
            }.getOrNull()?.let {
                TestInitialization.log.info(it.dumpAsJson(true))
            }
            ItemStack.CODEC.encodeStart(registryManager!!.getOps(JsonOps.INSTANCE), item).resultOrPartial {
                TestInitialization.log.info(it)
            }.getOrNull()?.let {
                TestInitialization.log.info(gson.toJson(it))
            }

        }
    }.onFailure {
        TestInitialization.log.error(it)
    }
}

fun test3() = BoxScreen {
    val items = listOf(
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
    val scale = 1.5f
    val (width, height) = 16f * scale to 16f * scale
    RouletteSelector(
        items,
        maxOptions = 8,
        onLeftPressSelected = {
            Toast.showToast(text = "已选择${it?.name?.string}")
        },
        selectedRenderer = { item, context, position, mouseX, mouseY, delta ->
            context.renderAlignmentText(item?.name ?: Literal("未选择"), Box(position.x() - 40f, position.y() - 20f, Size(80f, 40f)), color = Colors.WHITE)
        }
    ) { item, context, selected, position, mouseX, mouseY, delta ->
        context.useMatrixStack {
            it.scale(scale, scale, 1f)
            it.translate((position.x() - width / 2f) * (1f / scale), (position.y() - height / 2f) * (1f / scale), 0f)
            renderItem(item, 0f, 0f)
        }
        context.renderText("TSAFAF", x = position.x(), y = position.y(), layerType = TextRenderer.TextLayerType.SEE_THROUGH)
    }

}

fun test4() = ItemStackMatcherBuilder(ItemStackMatcher(MultiMatcher.MatchMode.AllMatch), {})

fun test5() = BlockInfoMatcherBuilder(BlockInfoMatcher(MultiMatcher.MatchMode.AllMatch), {})