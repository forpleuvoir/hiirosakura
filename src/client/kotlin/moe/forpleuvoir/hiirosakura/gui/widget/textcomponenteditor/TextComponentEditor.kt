package moe.forpleuvoir.hiirosakura.gui.widget.textcomponenteditor

import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextLabel
import moe.forpleuvoir.ibukigourd.text.Text

fun ContainerScope.TextComponentEditor(
    content: String,
    textConsumer: (Text) -> Unit
) = Column {
    //Tool
    Row {
        Button {
            TextLabel("加粗")
        }
        Button {
            TextLabel("斜体")
        }
        Button {
            TextLabel("下划线")
        }
        Button {
            TextLabel("删除线")
        }
        Button {
            TextLabel("混淆")
        }
        Button {
            TextLabel("颜色")
        }
        Button {
            TextLabel("阴影颜色")
        }
        Button {
            TextLabel("点击事件")
        }
        Button {
            TextLabel("悬浮事件")
        }
    }


}