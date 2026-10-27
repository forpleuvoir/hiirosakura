> Entries for v4.0.0+alpha and earlier are maintained in Chinese only; see `CHANGE_LOG.md`.

v4.1.1+alpha
  - Fixed chat filtering and the message-receive event not applying to server system messages: the mixin now hooks the entry point that writes into the chat queue, so both player and system messages are handled

v4.1.0+alpha
  - Updated to `Minecraft` `26.3` (Fabric API `0.165.0+26.3` / Loader `0.20.1` / Loom `1.19.3` / Fabric Kotlin `1.14.2+kotlin.2.4.21`; NeoForge `26.3.0.99` / moddev `2.0.148` / neoForm `26.3-1` / Kotlin for Forge `6.4.0`; ModMenu `21.0.0`)
  - Toolchain updated to `Kotlin` `2.4.21` / `Gradle` `9.8.2`; `Mixin` `0.8.7` + MixinExtras `0.5.5`
  - Requires `IbukiGourd` `1.1.0-alpha` (coordinates `-26.3`); the UI requires `compose-minecraft` `0.2.0`
  - **[Rendering]** Custom render pipelines follow 26.3 to `renderpearl`: `RenderPipeline` / `BlendFunction` / `ColorTargetState` / `DepthStencilState` / `PrimitiveTopology` now live in `com.mojang.renderpearl.api.pipeline` and `FilterMode` in `com.mojang.renderpearl.api.textures`; pipelines bind the projection and dynamic-transform bind groups
  - **[Rendering]** Overhead text in the world (dropped item names, chat bubbles, TNT and creeper fuse progress) uses the new pose rotation API; facing and tilt are unchanged
  - **[Input]** Input simulation uses the vanilla key action constants; unrecognised mouse buttons are no longer dispatched
  - **[Item editor]** The `map_color` component was removed in 26.3, so its editor row is gone; `swing_animation` is split into `attack_animation` and `interact_animation` rows
  - **[Script]** `isShovel` / `isHoe` / `isAxe` now resolve through item tags
  - **[Gameplay]** Disabling the firework interaction while gliding now uses the new swing and item-use animation APIs
  - **[Breaking]** Dependency coordinates follow the Minecraft version: `moe.forpleuvoir:ibukigourd-<platform>-26.3` / `compose_minecraft-<platform>-26.3`

