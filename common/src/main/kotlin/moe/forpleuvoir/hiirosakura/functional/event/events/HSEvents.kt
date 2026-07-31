package moe.forpleuvoir.hiirosakura.functional.event.events

import moe.forpleuvoir.hiirosakura.functional.script.deobfuscation.HSBlockState
import moe.forpleuvoir.hiirosakura.functional.script.deobfuscation.HSHitResult
import moe.forpleuvoir.hiirosakura.functional.script.deobfuscation.HSItemStack
import moe.forpleuvoir.hiirosakura.functional.script.deobfuscation.HSSoundInstance
import moe.forpleuvoir.ibukigourd.event.CancellableContext
import moe.forpleuvoir.ibukigourd.event.CancellableContextImpl
import moe.forpleuvoir.nebula.event.EventFactory

//region 不可取消事件上下文

data class ServerJoinContext(
    @JvmField val serverName: String,
    @JvmField val serverAddress: String,
)

data class GameJoinContext(
    @JvmField val serverName: String,
    @JvmField val serverAddress: String,
)

data class GameExitContext(
    @JvmField val serverName: String,
    @JvmField val serverAddress: String,
)

data class DisconnectContext(
    @JvmField val serverName: String,
    @JvmField val serverAddress: String,
    @JvmField val title: String,
    @JvmField val reason: String,
)

data class PlayerDeathContext(
    @JvmField val message: String,
){
    companion object {
        @JvmStatic
        var isDead = false
    }
}

data object PlayerRespawnContext

//endregion

//region 可取消事件上下文

class BreakBlockContext(
    @JvmField val blockState: HSBlockState,
    @JvmField val direction: String,
) : CancellableContextImpl()

class CommandSendContext(
    @JvmField var command: String,
) : CancellableContextImpl()

class MessageSendContext(
    @JvmField var message: String,
) : CancellableContextImpl()

class MessageReceiveContext(
    @JvmField val message: String,
    @JvmField val uuid: String?,
) : CancellableContextImpl()

class PlayerAttackContext(
    @JvmField val hitResult: HSHitResult,
) : CancellableContextImpl()

class PlayerPickContext(
    @JvmField val hitResult: HSHitResult,
) : CancellableContextImpl()

class PlayerUseContext(
    @JvmField val hitResult: HSHitResult,
    @JvmField val itemStack: HSItemStack,
) : CancellableContextImpl()

class SoundPlayContext(
    @JvmField val sound: HSSoundInstance,
) : CancellableContextImpl()

//endregion

object HSEvents {

    //region 不可取消事件实例

    @JvmField
    val ServerJoin = EventFactory.create<(ServerJoinContext) -> Unit>({}) { ls ->
        { ctx -> ls.forEach { it(ctx) } }
    }

    @JvmField
    val GameJoin = EventFactory.create<(GameJoinContext) -> Unit>({}) { ls ->
        { ctx -> ls.forEach { it(ctx) } }
    }

    @JvmField
    val GameExit = EventFactory.create<(GameExitContext) -> Unit>({}) { ls ->
        { ctx -> ls.forEach { it(ctx) } }
    }

    @JvmField
    val Disconnect = EventFactory.create<(DisconnectContext) -> Unit>({}) { ls ->
        { ctx -> ls.forEach { it(ctx) } }
    }

    @JvmField
    val PlayerDeath = EventFactory.create<(PlayerDeathContext) -> Unit>({}) { ls ->
        { ctx -> ls.forEach { it(ctx) } }
    }

    @JvmField
    val PlayerRespawn = EventFactory.create<(PlayerRespawnContext) -> Unit>({}) { ls ->
        { ctx -> ls.forEach { it(ctx) } }
    }

    //endregion

    //region 可取消事件实例

    @JvmField
    val BreakBlock = CancellableContext.createEvent<BreakBlockContext>()

    @JvmField
    val CommandSend = CancellableContext.createEvent<CommandSendContext>()

    @JvmField
    val MessageSend = CancellableContext.createEvent<MessageSendContext>()

    @JvmField
    val MessageReceive = CancellableContext.createEvent<MessageReceiveContext>()

    @JvmField
    val PlayerAttack = CancellableContext.createEvent<PlayerAttackContext>()

    @JvmField
    val PlayerPick = CancellableContext.createEvent<PlayerPickContext>()

    @JvmField
    val PlayerUse = CancellableContext.createEvent<PlayerUseContext>()

    @JvmField
    val SoundPlay = CancellableContext.createEvent<SoundPlayContext>()

    //endregion
}