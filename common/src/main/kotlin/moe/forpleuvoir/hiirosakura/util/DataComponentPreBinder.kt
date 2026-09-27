package moe.forpleuvoir.hiirosakura.util

import net.minecraft.core.*
import net.minecraft.core.component.DataComponentInitializers
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.TagKey

object DataComponentPreBinder {

    private val logger = logger()

    private val DUMMY_OWNER = object : HolderOwner<Any> {
        override fun canSerializeIn(registry: HolderOwner<Any>) = false
    }

    /** 已经用「静态注册表」兜底绑定过（主菜单等还没有服务端注册表的场合）。 */
    private var boundWithStaticAccess = false

    /** 已经用「客户端完整注册表」绑定过（进世界后，此时动态注册表的 Holder 才是真值）。 */
    private var boundWithFullAccess = false

    /**
     * 确保物品的「待应用 DataComponent」已经绑定。可以任意次数调用：已经用同等或更完整的注册表
     * 绑定过就直接返回，所以挂在 tick 上也不会有额外开销。
     *
     * 为什么要绑定：不绑定的话物品这类注册表元素的 `Holder.Reference.components()` 为 null，
     * 连 `ItemStack` 都构造不出来（`NullPointerException: Components not bound yet`），
     * 主菜单里的物品界面直接打不开。
     *
     * 为什么要分两段：
     * - 启动时只有静态注册表（`RegistryAccess.fromRegistryOfRegistries`），
     *   `minecraft:jukebox_song` / `painting_variant` / `instrument` 这些**动态**注册表
     *   （随存档 / 数据包加载）查不到键，只能退化成 `Holder.Reference.createStandAlone` 的**占位 Holder**：
     *   物品能构造、能显示，但真要读它的值（唱片模型 / 提示）会抛
     *   `Trying to access unbound value …` —— 这种数据在原版主菜单里本来也不存在；
     * - 进世界后 [registryAccess]（`player.level().registryAccess()`）带完整动态注册表，再绑一次就把
     *   占位 Holder 换成真值，那个异常随之消失。
     *
     * 完整注册表下不再允许占位：注册表 / 条目缺失就抛，让这次绑定整批不生效（`runCatching` 接住），
     * 绝不把取不出值的 Holder 写进物品组件。
     */
    fun ensureBound() {
        val fullAccess = registryAccess
        if (fullAccess != null) {
            if (boundWithFullAccess) return
        } else if (boundWithStaticAccess) {
            return
        }

        runCatching {
            val access = fullAccess ?: RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY)
            val safeProvider = createSafeLookupProvider(access, allowPlaceholderHolders = fullAccess == null)
            BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.build(safeProvider)
                .forEach(DataComponentInitializers.PendingComponents<*>::apply)
            if (fullAccess != null) boundWithFullAccess = true else boundWithStaticAccess = true
        }.onFailure {
            logger.warn("Binding DataComponent failed.", it)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> createEmptyHolderSetNamed(tagKey: TagKey<T>): HolderSet.Named<T> {
        return HolderSet.Named(DUMMY_OWNER as HolderOwner<T>, tagKey).apply {
            bind(emptyList())
        }
    }

    private fun createSafeLookupProvider(
        delegate: RegistryAccess,
        allowPlaceholderHolders: Boolean,
    ): HolderLookup.Provider {
        return object : HolderLookup.Provider {

            override fun <T : Any> lookup(registryKey: ResourceKey<out Registry<out T>>) =
                delegate.lookup(registryKey)

            override fun listRegistryKeys() = delegate.listRegistryKeys()

            /** 标签缺失时给一个**已绑定**的空集合：空集合可以安全读取，不会有未绑定值。 */
            override fun <T : Any> getOrThrow(tagKey: TagKey<T>): HolderSet.Named<T> =
                delegate.get(tagKey).orElseGet { createEmptyHolderSetNamed(tagKey) }

            @Suppress("UNCHECKED_CAST")
            override fun <T : Any> getOrThrow(resourceKey: ResourceKey<T>): Holder.Reference<T> {
                val registry = delegate.lookup(resourceKey.registryKey()).orElse(null)
                if (registry != null) return registry.getOrThrow(resourceKey)
                if (!allowPlaceholderHolders) {
                    throw IllegalStateException(
                        "Registry ${resourceKey.registryKey().identifier()} is not available for item component binding."
                    )
                }
                return Holder.Reference.createStandAlone(DUMMY_OWNER as HolderOwner<T>, resourceKey)
            }
        }
    }
}
