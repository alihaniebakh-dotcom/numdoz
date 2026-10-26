package ir.numdoz.ui.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

enum class LogoShape {
    CIRCLE, HEXAGON, DIAMOND, TRIANGLE, STAR, OVAL,
    SHIELD, OCTAGON, PENTAGON, DROP
}

// ─── Years — premium shield/octagon ───
fun shapeForYear(logoName: String): LogoShape = when (logoName) {
    "star" -> LogoShape.SHIELD          // سال آرکا
    "brightness" -> LogoShape.OCTAGON   // سال لومِنا
    else -> LogoShape.SHIELD
}

// ─── Chapters ، 4 distinct shapes ───
fun shapeForChapter(logoName: String): LogoShape = when (logoName) {
    "sun" -> LogoShape.HEXAGON          // کالیرا
    "wave" -> LogoShape.CIRCLE          // سورِوا
    "flame" -> LogoShape.TRIANGLE       // وکسیرا
    "snow" -> LogoShape.DIAMOND         // نیوالا
    else -> LogoShape.HEXAGON
}

// ─── Months ، 12 unique (no collision with parent chapter) ───
fun shapeForMonth(logoName: String): LogoShape = when (logoName) {
    // کالیرا (chapter=HEXAGON)
    "sunrise" -> LogoShape.PENTAGON
    "brightness" -> LogoShape.STAR
    "wind" -> LogoShape.OVAL
    // سورِوا (chapter=CIRCLE)
    "wave" -> LogoShape.DROP
    "water" -> LogoShape.OCTAGON
    "cloud" -> LogoShape.SHIELD
    // وکسیرا (chapter=TRIANGLE)
    "ember" -> LogoShape.PENTAGON
    "flame" -> LogoShape.HEXAGON
    "sunset" -> LogoShape.STAR
    // نیوالا (chapter=DIAMOND)
    "snow" -> LogoShape.TRIANGLE
    "moon" -> LogoShape.OVAL
    "star" -> LogoShape.DROP
    else -> LogoShape.CIRCLE
}

internal fun DrawScope.drawShapeFilled(
    shape: LogoShape,
    center: Offset,
    radius: Float,
    color: Color,
    fillAlpha: Float,
    strokeAlpha: Float,
    strokeWidth: Float
) {
    when (shape) {
        LogoShape.CIRCLE -> {
            drawCircle(color.copy(alpha = fillAlpha), radius, center)
            drawCircle(color.copy(alpha = strokeAlpha), radius, center, style = Stroke(strokeWidth))
        }
        LogoShape.HEXAGON -> drawPolygon(center, radius, 6, color, fillAlpha, strokeAlpha, strokeWidth)
        LogoShape.OCTAGON -> drawPolygon(center, radius, 8, color, fillAlpha, strokeAlpha, strokeWidth)
        LogoShape.PENTAGON -> drawPolygon(center, radius, 5, color, fillAlpha, strokeAlpha, strokeWidth)
        LogoShape.DIAMOND -> drawPolygon(center, radius, 4, color, fillAlpha, strokeAlpha, strokeWidth)
        LogoShape.TRIANGLE -> drawPolygon(center, radius * 1.1f, 3, color, fillAlpha, strokeAlpha, strokeWidth)
        LogoShape.STAR -> {
            val p = starPath(center, radius, 6)
            drawPath(p, color.copy(alpha = fillAlpha))
            drawPath(p, color.copy(alpha = strokeAlpha), style = Stroke(strokeWidth))
        }
        LogoShape.OVAL -> {
            val size = Size(radius * 1.8f, radius * 1.1f)
            val topLeft = Offset(center.x - size.width / 2f, center.y - size.height / 2f)
            drawOval(color.copy(alpha = fillAlpha), topLeft, size)
            drawOval(color.copy(alpha = strokeAlpha), topLeft, size, style = Stroke(strokeWidth))
        }
        LogoShape.SHIELD -> {
            val p = shieldPath(center, radius)
            drawPath(p, color.copy(alpha = fillAlpha))
            drawPath(p, color.copy(alpha = strokeAlpha), style = Stroke(strokeWidth))
        }
        LogoShape.DROP -> {
            val p = dropPath(center, radius)
            drawPath(p, color.copy(alpha = fillAlpha))
            drawPath(p, color.copy(alpha = strokeAlpha), style = Stroke(strokeWidth))
        }
    }
}

private fun DrawScope.drawPolygon(
    center: Offset, radius: Float, sides: Int,
    color: Color, fillAlpha: Float, strokeAlpha: Float, strokeWidth: Float
) {
    val p = regularPolygon(center, radius, sides)
    drawPath(p, color.copy(alpha = fillAlpha))
    drawPath(p, color.copy(alpha = strokeAlpha), style = Stroke(strokeWidth))
}

internal fun regularPolygon(center: Offset, radius: Float, sides: Int, rotation: Float = -PI.toFloat() / 2): Path {
    val p = Path()
    for (i in 0 until sides) {
        val angle = rotation + 2 * PI.toFloat() * i / sides
        val x = center.x + radius * cos(angle)
        val y = center.y + radius * sin(angle)
        if (i == 0) p.moveTo(x, y) else p.lineTo(x, y)
    }
    p.close()
    return p
}

internal fun starPath(center: Offset, radius: Float, points: Int): Path {
    val p = Path()
    val innerR = radius * 0.42f
    for (i in 0 until points * 2) {
        val r = if (i % 2 == 0) radius else innerR
        val angle = -PI.toFloat() / 2 + PI.toFloat() * i / points
        val x = center.x + r * cos(angle)
        val y = center.y + r * sin(angle)
        if (i == 0) p.moveTo(x, y) else p.lineTo(x, y)
    }
    p.close()
    return p
}

private fun shieldPath(center: Offset, r: Float): Path {
    val p = Path()
    p.moveTo(center.x - r * 0.9f, center.y - r * 0.7f)
    p.lineTo(center.x + r * 0.9f, center.y - r * 0.7f)
    p.lineTo(center.x + r * 0.9f, center.y + r * 0.1f)
    p.lineTo(center.x, center.y + r)
    p.lineTo(center.x - r * 0.9f, center.y + r * 0.1f)
    p.close()
    return p
}

private fun dropPath(center: Offset, r: Float): Path {
    val p = Path()
    p.moveTo(center.x, center.y - r)
    p.cubicTo(
        center.x + r * 0.85f, center.y - r * 0.2f,
        center.x + r * 0.7f, center.y + r * 0.5f,
        center.x, center.y + r
    )
    p.cubicTo(
        center.x - r * 0.7f, center.y + r * 0.5f,
        center.x - r * 0.85f, center.y - r * 0.2f,
        center.x, center.y - r
    )
    p.close()
    return p
}
