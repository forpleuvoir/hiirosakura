package moe.forpleuvoir.hiirosakura.gui.widget.textcomponenteditor

import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.text.Text
import moe.forpleuvoir.ibukigourd.text.Text

fun ContainerScope.TextComponentEditor(
    content: String,
    textConsumer: (Text) -> Unit
) = Column {
    //Tool
    Row {
        Button {
            Text("加粗")
        }
        Button {
            Text("斜体")
        }
        Button {
            Text("下划线")
        }
        Button {
            Text("删除线")
        }
        Button {
            Text("混淆")
        }
        Button {
            Text("颜色")
        }
        Button {
            Text("阴影颜色")
        }
        Button {
            Text("点击事件")
        }
        Button {
            Text("悬浮事件")
        }
    }


}