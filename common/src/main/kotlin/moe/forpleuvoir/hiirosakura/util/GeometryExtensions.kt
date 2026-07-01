package moe.forpleuvoir.hiirosakura.util

import androidx.compose.ui.geometry.Rect

fun Rect.expandEdges(top: Float, bottom: Float, left: Float, right: Float): Rect =
    Rect(this.left - left, this.top - top, this.right + right, this.bottom + bottom)

fun Rect.expandEdges(width: Float, height: Float): Rect =
    expandEdges(height / 2, height / 2, width / 2, width / 2)

fun Rect.expandEdges(size: Float): Rect =
    expandEdges(size, size, size, size)