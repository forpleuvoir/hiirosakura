package moe.forpleuvoir.hiirosakura.util.math

import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import org.joml.Vector2f
import org.joml.Vector2fc
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.Vector3i
import org.joml.Vector3ic

fun Vec3d.toVector(): Vector3dc = Vector3d(x, y, z)

fun Vec3i.toVector(): Vector3ic = Vector3i(x, y, z)

fun Vec2f.toVector(): Vector2fc = Vector2f(x, y)


