package moe.forpleuvoir.hiirosakura.util.math

import moe.forpleuvoir.nebula.serialization.Deserializer
import moe.forpleuvoir.nebula.serialization.base.SerializeArray
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.extensions.checkType
import moe.forpleuvoir.nebula.serialization.extensions.serializeObject
import net.minecraft.core.Vec3i
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3
import org.joml.*

fun Vec3.toVector(): Vector3dc = Vector3d(x, y, z)

fun Vec3i.toVector(): Vector3ic = Vector3i(x, y, z)

fun Vec2.toVector(): Vector2fc = Vector2f(x, y)


object Vector3icDeserializer : Deserializer<Vector3ic> {
    override fun deserialization(serializeElement: SerializeElement): Vector3ic {
        return serializeElement.checkType<Vector3ic>()
            .check<SerializeArray> {
                Vector3i(it[0].asInt, it[1].asInt, it[2].asInt)
            }.check<SerializeObject> {
                Vector3i(it["x"]!!.asInt, it["y"]!!.asInt, it["z"]!!.asInt)
            }.getOrThrow()
    }

}

fun Vector3ic.serialization(): SerializeElement = serializeObject {
    "x" to x()
    "y" to y()
    "z" to z()
}