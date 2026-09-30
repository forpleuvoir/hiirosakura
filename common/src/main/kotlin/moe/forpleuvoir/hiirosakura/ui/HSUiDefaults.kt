package moe.forpleuvoir.hiirosakura.ui

/**
 * HiiroSakura 界面里跨页面共用的排版量。
 */
object HSUiDefaults {

    /**
     * 列表项、菜单项与对话框内图标按钮的图标倍率。
     *
     * 取固定 2（像素素材 1x 的整数倍），不跟随主题像素倍率浮动 —— 这些图标是界面的固定装饰，
     * 换主题时不该跟着变大小。
     */
    const val ICON_SCALE: Int = 2
}
