package moe.forpleuvoir.hiirosakura.util

import moe.forpleuvoir.ibukigourd.util.mc
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.flag.FeatureFlagSet
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.ItemStack

object ItemRegistryHelper {

    /**
     * 初始化/刷新所有创造模式分类的内容。
     * 在游戏启动完成、可以获取到 HolderLookup.Provider 时调用一次。
     *
     * @param enabledFeatures 启用的特性标志，默认 FeatureFlags.VANILLA_SET
     * @param hasPermissions 是否有管理员权限，单人模式默认 true
     * @param lookup 注册表查找提供者，可从 server.registryAccess() 或 client 连接获取
     */
    fun initialize(
        enabledFeatures: FeatureFlagSet = mc.player?.connection?.enabledFeatures() ?: FeatureFlags.VANILLA_SET,
        hasPermissions: Boolean = true,
        lookup: HolderLookup.Provider = registryAccess!!,
    ) {
        CreativeModeTabs.tryRebuildTabContents(enabledFeatures, hasPermissions, lookup)
    }

    /** 判断是否为原版分类 */
    private fun ResourceKey<CreativeModeTab>.isVanilla(): Boolean =
        identifier().namespace == "minecraft"

    /** 获取分类的排序键：(原版优先, 行, 列) */
    private fun CreativeModeTab.sortKey(): Triple<Int, Int, Int> =
        Triple(if (this.displayName.string.startsWith("minecraft.")) 0 else 1, row().ordinal, column())

    val allItem get() = BuiltInRegistries.ITEM

    val allBlock get() = BuiltInRegistries.BLOCK

    /**
     * 获取所有分类（仅 CATEGORY 类型）
     */
    fun getAllTabs(): List<ResourceKey<CreativeModeTab>> {
        initialize()
        return BuiltInRegistries.CREATIVE_MODE_TAB.registryKeySet()
            .filter { tabKey ->
                val tab = BuiltInRegistries.CREATIVE_MODE_TAB.getValueOrThrow(tabKey)
                tab.type == CreativeModeTab.Type.CATEGORY
            }
            .sortedWith(compareBy({ tabKey ->
                // 原版 (minecraft) 优先，模组在后
                if (tabKey.isVanilla()) 0 else 1
            }, { tabKey ->
                val tab = BuiltInRegistries.CREATIVE_MODE_TAB.getValueOrThrow(tabKey)
                tab.row().ordinal  // TOP=0, BOTTOM=1
            }, { tabKey ->
                val tab = BuiltInRegistries.CREATIVE_MODE_TAB.getValueOrThrow(tabKey)
                tab.column()       // 同一行内从左到右
            }))
    }

    /**
     * 根据创造模式分类键获取该分类下的所有物品
     */
    fun getItemsByTab(tabKey: ResourceKey<CreativeModeTab>): Collection<ItemStack> {
        return BuiltInRegistries.CREATIVE_MODE_TAB.getValueOrThrow(tabKey).displayItems
    }

    /**
     * 获取所有分类及其物品的映射
     */
    fun getAllTabsGrouped(): Map<ResourceKey<CreativeModeTab>, Collection<ItemStack>> {
        return getAllTabs().associateWith { tabKey ->
            BuiltInRegistries.CREATIVE_MODE_TAB.getValueOrThrow(tabKey).displayItems
        }
    }

    /**
     * 获取所有分类及其物品的映射（含分类的显示名称）
     */
    fun getAllTabsGroupedWithName(): Map<String, Collection<ItemStack>> {
        return getAllTabs().associate { tabKey ->
            val tab = BuiltInRegistries.CREATIVE_MODE_TAB.getValueOrThrow(tabKey)
            tab.displayName.string to tab.displayItems
        }
    }

}
