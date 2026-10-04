package moe.forpleuvoir.hiirosakura.ui.util

import androidx.compose.runtime.staticCompositionLocalOf
import net.minecraft.core.RegistryAccess

val LocalRegistryAccess = staticCompositionLocalOf<RegistryAccess> {
    error("Local registry access is unavailable")
}