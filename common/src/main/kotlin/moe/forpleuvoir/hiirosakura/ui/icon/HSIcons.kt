package moe.forpleuvoir.hiirosakura.ui.icon

/**
 * HiiroSakura 自有矢量图标的宿主对象。
 *
 * 与 IbukiGourd 的 `Icons`（sokitsu 的 `.aseprite` 精灵图集）分属两套体系：这里挂的是
 * `ImageVector`，由 [VectorIcon] 渲染。两者若共用命名空间，精灵成员会遮蔽同名的扩展属性，
 * 所以自有图标一律挂在本对象下。
 */
object HSIcons
