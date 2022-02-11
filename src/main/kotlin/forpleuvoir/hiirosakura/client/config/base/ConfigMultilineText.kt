package forpleuvoir.hiirosakura.client.config.base

import forpleuvoir.ibuki_gourd.config.gui.ConfigWrapper
import forpleuvoir.ibuki_gourd.config.options.ConfigString
import forpleuvoir.ibuki_gourd.gui.button.Button
import forpleuvoir.ibuki_gourd.gui.dialog.DialogConfirm
import forpleuvoir.ibuki_gourd.gui.screen.ScreenBase.Companion.current
import forpleuvoir.ibuki_gourd.gui.screen.ScreenBase.Companion.openScreen
import forpleuvoir.ibuki_gourd.gui.widget.MultilineTextField
import forpleuvoir.ibuki_gourd.mod.utils.IbukiGourdLang
import org.lwjgl.glfw.GLFW

/**
 * 多行文本编辑器的String配置选项

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.config.base

 * 文件名 ConfigMultilineText

 * 创建时间 2022/2/5 22:46

 * @author forpleuvoir

 */
class ConfigMultilineText(name: String, defaultValue: String) :
    ConfigString(name, defaultValue = defaultValue) {

    override fun wrapper(x: Int, y: Int, width: Int, height: Int): ConfigWrapper {
        var textValue: String = this@ConfigMultilineText.getValue()
        val button = Button(x, y, width, height, IbukiGourdLang.Edit.tText()) {
            //打开GUI
            current?.let {
                openScreen(
                    object : DialogConfirm(
                        400.coerceAtMost(it.width),
                        320.coerceAtMost(it.height),
                        this@ConfigMultilineText.displayName,
                        it,
                        {
                            this@ConfigMultilineText.setValue(textValue)
                            true
                        }
                    ) {
                        override fun init() {
                            super.init()
                            addDrawableChild(MultilineTextField(
                                this.left + 1,
                                this.top + 1,
                                this.contentWidth - 2,
                                this.contentHeight - 2,
                                4
                            ).apply {
                                text = textValue
                                setTextChangedListener { text ->
                                    textValue = text
                                }
                            })
                        }

                        override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
                            for (child in children()) {
                                if (child is MultilineTextField) {
                                    if (keyCode == GLFW.GLFW_KEY_TAB) {
                                        if (child.isFocused) {
                                            child.keyPressed(keyCode, scanCode, modifiers)
                                            return true
                                        }
                                    }
                                }
                            }
                            return super.keyPressed(keyCode, scanCode, modifiers)
                        }

                        override fun tick() {
                            for (child in children()) {
                                if (child is MultilineTextField) {
                                    child.tick()
                                }
                            }
                        }
                    }
                )
            }
        }
        return object : ConfigWrapper(this, x, y, width, height) {
            override fun initWidget() {
                addDrawableChild(button)
            }
        }
    }
}