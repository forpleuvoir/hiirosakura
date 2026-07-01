package moe.forpleuvoir.hiirosakura.util.math

import net.minecraft.core.Vec3i
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3
import org.joml.*

fun Vec3.toVector(): Vector3dc = Vector3d(x, y, z)

fun Vec3i.toVector(): Vector3ic = Vector3i(x, y, z)

fun Vec2.toVector(): Vector2fc = Vector2f(x, y)