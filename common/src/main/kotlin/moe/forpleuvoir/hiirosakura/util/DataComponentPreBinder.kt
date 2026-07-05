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

    fun bind() {
        runCatching {
            val staticAccess = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY)
            val safeProvider = createSafeLookupProvider(staticAccess)
            BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.build(safeProvider).forEach(DataComponentInitializers.PendingComponents<*>::apply)
        }.onFailure {
            logger.warn("Pre-binding DataComponent failed.", it)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> createEmptyHolderSetNamed(tagKey: TagKey<T>): HolderSet.Named<T> {
        return HolderSet.Named(DUMMY_OWNER as HolderOwner<T>, tagKey).apply {
            bind(emptyList())
        }
    }

    private fun createSafeLookupProvider(delegate: RegistryAccess.Frozen): HolderLookup.Provider {
        return object : HolderLookup.Provider {

            override fun <T : Any> lookup(registryKey: ResourceKey<out Registry<out T>>) =
                delegate.lookup(registryKey)

            override fun listRegistryKeys() = delegate.listRegistryKeys()

            override fun <T : Any> getOrThrow(tagKey: TagKey<T>): HolderSet.Named<T> =
                delegate.get(tagKey).orElseGet { createEmptyHolderSetNamed(tagKey) }

            @Suppress("UNCHECKED_CAST")
            override fun <T : Any> getOrThrow(resourceKey: ResourceKey<T>): Holder.Reference<T> =
                delegate.lookup(resourceKey.registryKey()).map { reg -> reg.getOrThrow(resourceKey) }
                    .orElseGet { Holder.Reference.createStandAlone(DUMMY_OWNER as HolderOwner<T>, resourceKey) }
        }
    }
}
