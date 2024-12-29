package moe.forpleuivoir.hiirosakura.test

import moe.forpleuvoir.hiirosakura.gui.widget.calculateQuadrilaterals
import kotlin.test.Test

class OtherTest{


    @Test
    fun test1(){
        val centerX = 300f
        val centerY = 300f
        val rInner = 50f
        val rOuter = 100f
        val options = 6
        val gapAngle = 10f // 每个间隔角度为 10 度

        val quadrilaterals = calculateQuadrilaterals(centerX, centerY, rInner, rOuter, options, gapAngle)

        quadrilaterals.forEachIndexed { index, quad ->
            println("选项 ${index + 1}: $quad")
        }

    }


}