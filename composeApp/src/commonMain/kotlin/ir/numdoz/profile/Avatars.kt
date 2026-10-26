package ir.numdoz.profile

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

enum class HairStyle { SPIKY, LONG, BUN, TWIN_TAILS, WAVY, PONYTAIL, CAP, FOX, CAT, NINJA, STAR, BALD }
enum class FaceExpression { HAPPY, WINK, COOL, SURPRISED, CLOSED_SMILE, GRIN }

data class AvatarSpec(
    val id: Int,
    val skinColor: Color,
    val accentColor: Color,
    val hairColor: Color,
    val hairStyle: HairStyle,
    val expression: FaceExpression,
    val hasBlush: Boolean = false
) {
    val color: Color get() = accentColor
}

val AVATARS: List<AvatarSpec> = listOf(
    AvatarSpec(0,  Color(0xFFFFE0BD), Color(0xFF22D3EE), Color(0xFF2D2640), HairStyle.SPIKY,      FaceExpression.HAPPY,        hasBlush = true),
    AvatarSpec(1,  Color(0xFFFFD4B0), Color(0xFFF472B6), Color(0xFF7C2D4B), HairStyle.LONG,       FaceExpression.CLOSED_SMILE, hasBlush = true),
    AvatarSpec(2,  Color(0xFFF4C795), Color(0xFFFB923C), Color(0xFF4A1F17), HairStyle.CAP,        FaceExpression.COOL),
    AvatarSpec(3,  Color(0xFFD4A574), Color(0xFF9D7BFF), Color(0xFF3B2210), HairStyle.BUN,        FaceExpression.SURPRISED),
    AvatarSpec(4,  Color(0xFFC4B0E8), Color(0xFF7C5CFF), Color(0xFF28154D), HairStyle.TWIN_TAILS, FaceExpression.WINK,         hasBlush = true),
    AvatarSpec(5,  Color(0xFF9EE5B5), Color(0xFF34D399), Color(0xFF0F3F26), HairStyle.SPIKY,      FaceExpression.GRIN),
    AvatarSpec(6,  Color(0xFFFFC9E0), Color(0xFFA855F7), Color(0xFF5B1E7E), HairStyle.LONG,       FaceExpression.HAPPY,        hasBlush = true),
    AvatarSpec(7,  Color(0xFFFFD4A8), Color(0xFFF59E0B), Color(0xFF8B2E0F), HairStyle.FOX,        FaceExpression.GRIN),
    AvatarSpec(8,  Color(0xFFA8E5E5), Color(0xFF0EA5E9), Color(0xFF07485F), HairStyle.CAT,        FaceExpression.WINK),
    AvatarSpec(9,  Color(0xFF232338), Color(0xFF7C5CFF), Color(0xFF08080F), HairStyle.NINJA,      FaceExpression.COOL),
    AvatarSpec(10, Color(0xFFFDE68A), Color(0xFFFFC947), Color(0xFF8B4F09), HairStyle.STAR,       FaceExpression.SURPRISED),
    AvatarSpec(11, Color(0xFFFFDCBA), Color(0xFFEF4444), Color(0xFFA01616), HairStyle.PONYTAIL,   FaceExpression.HAPPY,        hasBlush = true)
)

fun avatarById(id: Int): AvatarSpec = AVATARS.getOrElse(id) { AVATARS[0] }

@Composable
fun FaceAvatar(spec: AvatarSpec, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val r = w * 0.35f
        val faceCy = cy + r * 0.08f
        val face = Offset(cx, faceCy)

        // ── Halo ──
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(spec.accentColor.copy(alpha = 0.45f), Color.Transparent),
                center = Offset(cx, cy),
                radius = w * 0.58f
            ),
            radius = w * 0.58f, center = Offset(cx, cy)
        )

        // ── Hair back ──
        drawHairBack(spec, face, r)

        // ── Neck ──
        drawRoundRect(
            color = spec.skinColor.copy(alpha = 0.90f),
            topLeft = Offset(face.x - r * 0.25f, face.y + r * 0.78f),
            size = Size(r * 0.50f, r * 0.55f),
            cornerRadius = CornerRadius(r * 0.15f)
        )

        // ── Face (rounded shape, not circle) ──
        drawRoundRect(
            color = spec.skinColor,
            topLeft = Offset(face.x - r * 0.98f, face.y - r),
            size = Size(r * 1.96f, r * 2f),
            cornerRadius = CornerRadius(r * 0.95f)
        )

        // Face highlight (soft shine top-left)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White.copy(alpha = 0.25f), Color.Transparent),
                center = Offset(face.x - r * 0.35f, face.y - r * 0.45f),
                radius = r * 0.55f
            ),
            radius = r * 0.55f,
            center = Offset(face.x - r * 0.35f, face.y - r * 0.45f)
        )

        // ── Ears ──
        if (spec.hairStyle !in listOf(HairStyle.NINJA, HairStyle.CAT, HairStyle.FOX)) {
            drawCircle(spec.skinColor, r * 0.17f, Offset(face.x - r * 0.96f, face.y + r * 0.02f))
            drawCircle(spec.skinColor, r * 0.17f, Offset(face.x + r * 0.96f, face.y + r * 0.02f))
            drawCircle(spec.skinColor.copy(alpha = 0.6f), r * 0.09f, Offset(face.x - r * 0.96f, face.y + r * 0.02f))
            drawCircle(spec.skinColor.copy(alpha = 0.6f), r * 0.09f, Offset(face.x + r * 0.96f, face.y + r * 0.02f))
        }

        // ── Blush ──
        if (spec.hasBlush) {
            drawCircle(spec.accentColor.copy(alpha = 0.35f), r * 0.20f,
                Offset(face.x - r * 0.58f, face.y + r * 0.28f))
            drawCircle(spec.accentColor.copy(alpha = 0.35f), r * 0.20f,
                Offset(face.x + r * 0.58f, face.y + r * 0.28f))
        }

        // ── Eyes (cute + sparkles) ──
        drawEyes(spec, face, r)

        // ── Mouth ──
        drawMouth(spec, face, r)

        // ── Hair front ──
        drawHairFront(spec, face, r)

        // ── Accessories ──
        drawAccessories(spec, face, r)
    }
}

private fun DrawScope.drawHairBack(spec: AvatarSpec, c: Offset, r: Float) {
    when (spec.hairStyle) {
        HairStyle.LONG, HairStyle.WAVY -> {
            drawRoundRect(
                color = spec.hairColor,
                topLeft = Offset(c.x - r * 1.18f, c.y - r * 0.95f),
                size = Size(r * 2.36f, r * 2.35f),
                cornerRadius = CornerRadius(r * 0.70f)
            )
        }
        HairStyle.TWIN_TAILS -> {
            drawCircle(spec.hairColor, r * 0.58f, Offset(c.x - r * 1.15f, c.y + r * 0.35f))
            drawCircle(spec.hairColor, r * 0.58f, Offset(c.x + r * 1.15f, c.y + r * 0.35f))
            // Ties
            drawCircle(spec.accentColor, r * 0.15f, Offset(c.x - r * 0.98f, c.y + r * 0.05f))
            drawCircle(spec.accentColor, r * 0.15f, Offset(c.x + r * 0.98f, c.y + r * 0.05f))
        }
        HairStyle.PONYTAIL -> {
            drawCircle(spec.hairColor, r * 0.68f, Offset(c.x + r * 1.20f, c.y - r * 0.10f))
            drawCircle(spec.accentColor, r * 0.14f, Offset(c.x + r * 0.92f, c.y - r * 0.30f))
        }
        HairStyle.BUN -> {
            drawCircle(spec.hairColor, r * 0.52f, Offset(c.x, c.y - r * 1.15f))
            // Bun highlight
            drawCircle(Color.White.copy(alpha = 0.20f), r * 0.20f, Offset(c.x - r * 0.15f, c.y - r * 1.30f))
        }
        HairStyle.FOX -> {
            val earL = Path().apply {
                moveTo(c.x - r * 0.98f, c.y - r * 0.55f)
                lineTo(c.x - r * 1.35f, c.y - r * 1.55f)
                lineTo(c.x - r * 0.35f, c.y - r * 0.95f)
                close()
            }
            val earR = Path().apply {
                moveTo(c.x + r * 0.98f, c.y - r * 0.55f)
                lineTo(c.x + r * 1.35f, c.y - r * 1.55f)
                lineTo(c.x + r * 0.35f, c.y - r * 0.95f)
                close()
            }
            drawPath(earL, spec.hairColor)
            drawPath(earR, spec.hairColor)
            val innerL = Path().apply {
                moveTo(c.x - r * 0.98f, c.y - r * 0.75f)
                lineTo(c.x - r * 1.15f, c.y - r * 1.28f)
                lineTo(c.x - r * 0.55f, c.y - r * 0.90f)
                close()
            }
            val innerR = Path().apply {
                moveTo(c.x + r * 0.98f, c.y - r * 0.75f)
                lineTo(c.x + r * 1.15f, c.y - r * 1.28f)
                lineTo(c.x + r * 0.55f, c.y - r * 0.90f)
                close()
            }
            drawPath(innerL, Color(0xFFFFB4C8))
            drawPath(innerR, Color(0xFFFFB4C8))
        }
        HairStyle.CAT -> {
            val earL = Path().apply {
                moveTo(c.x - r * 0.98f, c.y - r * 0.65f)
                lineTo(c.x - r * 1.15f, c.y - r * 1.45f)
                lineTo(c.x - r * 0.50f, c.y - r * 0.95f)
                close()
            }
            val earR = Path().apply {
                moveTo(c.x + r * 0.98f, c.y - r * 0.65f)
                lineTo(c.x + r * 1.15f, c.y - r * 1.45f)
                lineTo(c.x + r * 0.50f, c.y - r * 0.95f)
                close()
            }
            drawPath(earL, spec.hairColor)
            drawPath(earR, spec.hairColor)
            drawPath(earL, Color(0xFFFBCFE8), style = Stroke(width = r * 0.06f))
            drawPath(earR, Color(0xFFFBCFE8), style = Stroke(width = r * 0.06f))
        }
        else -> {}
    }
}

private fun DrawScope.drawHairFront(spec: AvatarSpec, c: Offset, r: Float) {
    when (spec.hairStyle) {
        HairStyle.SPIKY -> {
            val p = Path().apply {
                moveTo(c.x - r * 1.05f, c.y - r * 0.35f)
                lineTo(c.x - r * 0.98f, c.y - r * 1.10f)
                lineTo(c.x - r * 0.55f, c.y - r * 0.75f)
                lineTo(c.x - r * 0.30f, c.y - r * 1.25f)
                lineTo(c.x - r * 0.05f, c.y - r * 0.80f)
                lineTo(c.x + r * 0.15f, c.y - r * 1.30f)
                lineTo(c.x + r * 0.40f, c.y - r * 0.80f)
                lineTo(c.x + r * 0.60f, c.y - r * 1.25f)
                lineTo(c.x + r * 0.98f, c.y - r * 0.75f)
                lineTo(c.x + r * 1.05f, c.y - r * 0.35f)
                lineTo(c.x + r * 0.82f, c.y - r * 0.40f)
                lineTo(c.x + r * 0.55f, c.y - r * 0.60f)
                lineTo(c.x + r * 0.25f, c.y - r * 0.42f)
                lineTo(c.x, c.y - r * 0.60f)
                lineTo(c.x - r * 0.30f, c.y - r * 0.42f)
                lineTo(c.x - r * 0.60f, c.y - r * 0.60f)
                lineTo(c.x - r * 0.85f, c.y - r * 0.40f)
                close()
            }
            drawPath(p, spec.hairColor)
            // Shine stripe
            drawLine(Color.White.copy(alpha = 0.35f),
                Offset(c.x - r * 0.55f, c.y - r * 0.82f),
                Offset(c.x - r * 0.30f, c.y - r * 0.52f),
                strokeWidth = r * 0.06f, cap = StrokeCap.Round)
        }
        HairStyle.LONG, HairStyle.WAVY -> {
            val p = Path().apply {
                moveTo(c.x - r * 1.05f, c.y - r * 0.45f)
                cubicTo(
                    c.x - r * 0.85f, c.y - r * 1.20f,
                    c.x + r * 0.85f, c.y - r * 1.20f,
                    c.x + r * 1.05f, c.y - r * 0.45f
                )
                cubicTo(
                    c.x + r * 0.75f, c.y - r * 0.68f,
                    c.x + r * 0.30f, c.y - r * 0.72f,
                    c.x, c.y - r * 0.62f
                )
                cubicTo(
                    c.x - r * 0.30f, c.y - r * 0.72f,
                    c.x - r * 0.75f, c.y - r * 0.68f,
                    c.x - r * 1.05f, c.y - r * 0.45f
                )
                close()
            }
            drawPath(p, spec.hairColor)
            drawLine(Color.White.copy(alpha = 0.30f),
                Offset(c.x - r * 0.55f, c.y - r * 0.92f),
                Offset(c.x - r * 0.15f, c.y - r * 0.72f),
                strokeWidth = r * 0.05f, cap = StrokeCap.Round)
        }
        HairStyle.BUN, HairStyle.PONYTAIL, HairStyle.TWIN_TAILS -> {
            val p = Path().apply {
                moveTo(c.x - r * 1.00f, c.y - r * 0.40f)
                cubicTo(
                    c.x - r * 0.78f, c.y - r * 1.05f,
                    c.x + r * 0.78f, c.y - r * 1.05f,
                    c.x + r * 1.00f, c.y - r * 0.40f
                )
                cubicTo(
                    c.x + r * 0.60f, c.y - r * 0.62f,
                    c.x + r * 0.15f, c.y - r * 0.66f,
                    c.x - r * 0.10f, c.y - r * 0.58f
                )
                cubicTo(
                    c.x - r * 0.45f, c.y - r * 0.68f,
                    c.x - r * 0.78f, c.y - r * 0.55f,
                    c.x - r * 1.00f, c.y - r * 0.40f
                )
                close()
            }
            drawPath(p, spec.hairColor)
            drawLine(Color.White.copy(alpha = 0.25f),
                Offset(c.x - r * 0.40f, c.y - r * 0.85f),
                Offset(c.x + r * 0.10f, c.y - r * 0.72f),
                strokeWidth = r * 0.05f, cap = StrokeCap.Round)
        }
        HairStyle.FOX -> {
            val p = Path().apply {
                moveTo(c.x - r * 1.02f, c.y - r * 0.42f)
                cubicTo(
                    c.x - r * 0.72f, c.y - r * 1.00f,
                    c.x + r * 0.72f, c.y - r * 1.00f,
                    c.x + r * 1.02f, c.y - r * 0.42f
                )
                cubicTo(
                    c.x + r * 0.50f, c.y - r * 0.68f,
                    c.x + r * 0.15f, c.y - r * 0.58f,
                    c.x - r * 0.05f, c.y - r * 0.55f
                )
                cubicTo(
                    c.x - r * 0.40f, c.y - r * 0.65f,
                    c.x - r * 0.72f, c.y - r * 0.55f,
                    c.x - r * 1.02f, c.y - r * 0.42f
                )
                close()
            }
            drawPath(p, spec.hairColor)
            // White muzzle
            drawOval(
                color = Color(0xFFF5F5FA),
                topLeft = Offset(c.x - r * 0.42f, c.y + r * 0.38f),
                size = Size(r * 0.84f, r * 0.55f)
            )
        }
        HairStyle.CAT -> {
            val p = Path().apply {
                moveTo(c.x - r * 1.02f, c.y - r * 0.38f)
                cubicTo(
                    c.x - r * 0.72f, c.y - r * 0.95f,
                    c.x + r * 0.72f, c.y - r * 0.95f,
                    c.x + r * 1.02f, c.y - r * 0.38f
                )
                cubicTo(
                    c.x + r * 0.55f, c.y - r * 0.58f,
                    c.x + r * 0.20f, c.y - r * 0.58f,
                    c.x, c.y - r * 0.52f
                )
                cubicTo(
                    c.x - r * 0.20f, c.y - r * 0.58f,
                    c.x - r * 0.55f, c.y - r * 0.58f,
                    c.x - r * 1.02f, c.y - r * 0.38f
                )
                close()
            }
            drawPath(p, spec.hairColor)
        }
        HairStyle.CAP -> {
            drawRoundRect(
                color = spec.hairColor,
                topLeft = Offset(c.x - r * 1.08f, c.y - r * 1.15f),
                size = Size(r * 2.16f, r * 0.72f),
                cornerRadius = CornerRadius(r * 0.38f)
            )
            // Shine on cap
            drawRoundRect(
                color = Color.White.copy(alpha = 0.25f),
                topLeft = Offset(c.x - r * 0.75f, c.y - r * 1.05f),
                size = Size(r * 0.55f, r * 0.20f),
                cornerRadius = CornerRadius(r * 0.10f)
            )
            drawRoundRect(
                color = spec.hairColor,
                topLeft = Offset(c.x - r * 0.78f, c.y - r * 0.50f),
                size = Size(r * 1.56f, r * 0.22f),
                cornerRadius = CornerRadius(r * 0.11f)
            )
        }
        HairStyle.NINJA -> {
            drawRoundRect(
                color = spec.hairColor,
                topLeft = Offset(c.x - r * 1.15f, c.y - r * 1.15f),
                size = Size(r * 2.30f, r * 2.30f),
                cornerRadius = CornerRadius(r * 1.15f),
                style = Stroke(width = r * 0.32f)
            )
            drawRoundRect(
                color = spec.hairColor,
                topLeft = Offset(c.x - r * 1.15f, c.y - r * 0.38f),
                size = Size(r * 2.30f, r * 0.42f),
                cornerRadius = CornerRadius(r * 0.10f)
            )
            // Headband knot
            drawCircle(spec.hairColor, r * 0.20f, Offset(c.x + r * 0.75f, c.y + r * 0.75f))
        }
        HairStyle.STAR -> {
            val p = Path().apply {
                moveTo(c.x - r * 1.02f, c.y - r * 0.42f)
                cubicTo(
                    c.x - r * 0.68f, c.y - r * 1.05f,
                    c.x + r * 0.68f, c.y - r * 1.05f,
                    c.x + r * 1.02f, c.y - r * 0.42f
                )
                cubicTo(
                    c.x + r * 0.55f, c.y - r * 0.62f,
                    c.x + r * 0.15f, c.y - r * 0.62f,
                    c.x, c.y - r * 0.55f
                )
                cubicTo(
                    c.x - r * 0.15f, c.y - r * 0.62f,
                    c.x - r * 0.55f, c.y - r * 0.62f,
                    c.x - r * 1.02f, c.y - r * 0.42f
                )
                close()
            }
            drawPath(p, spec.hairColor)
            drawStar(Offset(c.x + r * 0.70f, c.y - r * 0.85f), r * 0.28f, spec.accentColor)
        }
        else -> {}
    }
}

private fun DrawScope.drawEyes(spec: AvatarSpec, c: Offset, r: Float) {
    val eyeY = c.y - r * 0.08f
    val sp = r * 0.42f
    val eyeR = r * 0.16f
    val eyeDark = Color(0xFF14142A)
    val stroke = r * 0.06f

    when (spec.expression) {
        FaceExpression.HAPPY -> {
            // Big glossy eyes
            drawCircle(Color.White, eyeR * 1.15f, Offset(c.x - sp, eyeY))
            drawCircle(Color.White, eyeR * 1.15f, Offset(c.x + sp, eyeY))
            drawCircle(eyeDark, eyeR * 0.82f, Offset(c.x - sp, eyeY))
            drawCircle(eyeDark, eyeR * 0.82f, Offset(c.x + sp, eyeY))
            // Big highlight
            drawCircle(Color.White, eyeR * 0.40f, Offset(c.x - sp - eyeR * 0.25f, eyeY - eyeR * 0.30f))
            drawCircle(Color.White, eyeR * 0.40f, Offset(c.x + sp - eyeR * 0.25f, eyeY - eyeR * 0.30f))
            // Small highlight
            drawCircle(Color.White.copy(alpha = 0.9f), eyeR * 0.22f,
                Offset(c.x - sp + eyeR * 0.30f, eyeY + eyeR * 0.30f))
            drawCircle(Color.White.copy(alpha = 0.9f), eyeR * 0.22f,
                Offset(c.x + sp + eyeR * 0.30f, eyeY + eyeR * 0.30f))
            // Eyelashes
            drawArc(
                color = eyeDark,
                startAngle = 200f, sweepAngle = 140f, useCenter = false,
                topLeft = Offset(c.x - sp - eyeR * 1.10f, eyeY - eyeR * 1.25f),
                size = Size(eyeR * 2.20f, eyeR * 1.40f),
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
            drawArc(
                color = eyeDark,
                startAngle = 200f, sweepAngle = 140f, useCenter = false,
                topLeft = Offset(c.x + sp - eyeR * 1.10f, eyeY - eyeR * 1.25f),
                size = Size(eyeR * 2.20f, eyeR * 1.40f),
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }
        FaceExpression.WINK -> {
            drawCircle(Color.White, eyeR * 1.15f, Offset(c.x - sp, eyeY))
            drawCircle(eyeDark, eyeR * 0.82f, Offset(c.x - sp, eyeY))
            drawCircle(Color.White, eyeR * 0.40f, Offset(c.x - sp - eyeR * 0.25f, eyeY - eyeR * 0.30f))
            // Winking eye
            val p = Path().apply {
                moveTo(c.x + sp - eyeR * 1.05f, eyeY)
                quadraticBezierTo(c.x + sp, eyeY + eyeR * 0.75f, c.x + sp + eyeR * 1.05f, eyeY)
            }
            drawPath(p, eyeDark, style = Stroke(width = stroke * 1.3f, cap = StrokeCap.Round))
        }
        FaceExpression.COOL -> {
            drawCircle(Color.White, eyeR * 1.15f, Offset(c.x - sp, eyeY))
            drawCircle(Color.White, eyeR * 1.15f, Offset(c.x + sp, eyeY))
            // Narrow confident eyes
            drawRoundRect(
                color = eyeDark,
                topLeft = Offset(c.x - sp - eyeR * 0.90f, eyeY - eyeR * 0.42f),
                size = Size(eyeR * 1.80f, eyeR * 0.84f),
                cornerRadius = CornerRadius(eyeR * 0.42f)
            )
            drawRoundRect(
                color = eyeDark,
                topLeft = Offset(c.x + sp - eyeR * 0.90f, eyeY - eyeR * 0.42f),
                size = Size(eyeR * 1.80f, eyeR * 0.84f),
                cornerRadius = CornerRadius(eyeR * 0.42f)
            )
            // Shine
            drawCircle(Color.White.copy(alpha = 0.9f), eyeR * 0.20f,
                Offset(c.x - sp + eyeR * 0.30f, eyeY - eyeR * 0.15f))
            drawCircle(Color.White.copy(alpha = 0.9f), eyeR * 0.20f,
                Offset(c.x + sp + eyeR * 0.30f, eyeY - eyeR * 0.15f))
        }
        FaceExpression.SURPRISED -> {
            drawCircle(Color.White, eyeR * 1.35f, Offset(c.x - sp, eyeY))
            drawCircle(Color.White, eyeR * 1.35f, Offset(c.x + sp, eyeY))
            drawCircle(eyeDark, eyeR * 0.58f, Offset(c.x - sp, eyeY))
            drawCircle(eyeDark, eyeR * 0.58f, Offset(c.x + sp, eyeY))
            drawCircle(Color.White, eyeR * 0.28f, Offset(c.x - sp - eyeR * 0.20f, eyeY - eyeR * 0.20f))
            drawCircle(Color.White, eyeR * 0.28f, Offset(c.x + sp - eyeR * 0.20f, eyeY - eyeR * 0.20f))
        }
        FaceExpression.CLOSED_SMILE -> {
            val pL = Path().apply {
                moveTo(c.x - sp - eyeR * 1.05f, eyeY)
                quadraticBezierTo(c.x - sp, eyeY + eyeR * 0.95f, c.x - sp + eyeR * 1.05f, eyeY)
            }
            val pR = Path().apply {
                moveTo(c.x + sp - eyeR * 1.05f, eyeY)
                quadraticBezierTo(c.x + sp, eyeY + eyeR * 0.95f, c.x + sp + eyeR * 1.05f, eyeY)
            }
            drawPath(pL, eyeDark, style = Stroke(width = stroke * 1.3f, cap = StrokeCap.Round))
            drawPath(pR, eyeDark, style = Stroke(width = stroke * 1.3f, cap = StrokeCap.Round))
        }
        FaceExpression.GRIN -> {
            drawCircle(Color.White, eyeR * 1.15f, Offset(c.x - sp, eyeY))
            drawCircle(Color.White, eyeR * 1.15f, Offset(c.x + sp, eyeY))
            drawCircle(eyeDark, eyeR * 0.85f, Offset(c.x - sp, eyeY))
            drawCircle(eyeDark, eyeR * 0.85f, Offset(c.x + sp, eyeY))
            drawCircle(Color.White, eyeR * 0.42f, Offset(c.x - sp - eyeR * 0.28f, eyeY - eyeR * 0.30f))
            drawCircle(Color.White, eyeR * 0.42f, Offset(c.x + sp - eyeR * 0.28f, eyeY - eyeR * 0.30f))
            // Eyelashes
            drawArc(
                color = eyeDark, startAngle = 200f, sweepAngle = 140f, useCenter = false,
                topLeft = Offset(c.x - sp - eyeR * 1.10f, eyeY - eyeR * 1.25f),
                size = Size(eyeR * 2.20f, eyeR * 1.40f),
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
            drawArc(
                color = eyeDark, startAngle = 200f, sweepAngle = 140f, useCenter = false,
                topLeft = Offset(c.x + sp - eyeR * 1.10f, eyeY - eyeR * 1.25f),
                size = Size(eyeR * 2.20f, eyeR * 1.40f),
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }
    }
}

private fun DrawScope.drawMouth(spec: AvatarSpec, c: Offset, r: Float) {
    val mouthY = c.y + r * 0.42f
    val eyeDark = Color(0xFF14142A)
    val stroke = r * 0.055f

    when (spec.expression) {
        FaceExpression.HAPPY, FaceExpression.WINK, FaceExpression.CLOSED_SMILE -> {
            val p = Path().apply {
                moveTo(c.x - r * 0.25f, mouthY)
                quadraticBezierTo(c.x, mouthY + r * 0.30f, c.x + r * 0.25f, mouthY)
            }
            drawPath(p, eyeDark, style = Stroke(width = stroke * 1.3f, cap = StrokeCap.Round))
        }
        FaceExpression.COOL, FaceExpression.GRIN -> {
            // Open smile with teeth + tongue
            val p = Path().apply {
                moveTo(c.x - r * 0.32f, mouthY)
                quadraticBezierTo(c.x, mouthY + r * 0.42f, c.x + r * 0.32f, mouthY)
                quadraticBezierTo(c.x, mouthY + r * 0.10f, c.x - r * 0.32f, mouthY)
                close()
            }
            drawPath(p, eyeDark)
            // Teeth
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(c.x - r * 0.22f, mouthY + r * 0.02f),
                size = Size(r * 0.44f, r * 0.12f),
                cornerRadius = CornerRadius(r * 0.04f)
            )
            // Tongue
            drawOval(
                color = Color(0xFFFF7A8E),
                topLeft = Offset(c.x - r * 0.14f, mouthY + r * 0.20f),
                size = Size(r * 0.28f, r * 0.16f)
            )
        }
        FaceExpression.SURPRISED -> {
            drawCircle(eyeDark, r * 0.14f, Offset(c.x, mouthY + r * 0.06f))
            // Tongue hint
            drawOval(
                color = Color(0xFFFF7A8E),
                topLeft = Offset(c.x - r * 0.09f, mouthY + r * 0.10f),
                size = Size(r * 0.18f, r * 0.10f)
            )
        }
    }

    // Fox nose
    if (spec.hairStyle == HairStyle.FOX) {
        val noseY = c.y + r * 0.20f
        val noseP = Path().apply {
            moveTo(c.x - r * 0.10f, noseY)
            lineTo(c.x + r * 0.10f, noseY)
            lineTo(c.x, noseY + r * 0.12f)
            close()
        }
        drawPath(noseP, eyeDark)
    }
    // Cat nose
    if (spec.hairStyle == HairStyle.CAT) {
        val noseY = c.y + r * 0.20f
        val noseP = Path().apply {
            moveTo(c.x - r * 0.08f, noseY)
            lineTo(c.x + r * 0.08f, noseY)
            lineTo(c.x, noseY + r * 0.08f)
            close()
        }
        drawPath(noseP, Color(0xFFFF9AAE))
    }
}

private fun DrawScope.drawAccessories(spec: AvatarSpec, c: Offset, r: Float) {
    when (spec.hairStyle) {
        HairStyle.CAT -> {
            // Whiskers
            val wStroke = r * 0.035f
            for (dy in listOf(-0.08f, 0.08f, 0.24f)) {
                drawLine(Color(0xFF14142A),
                    Offset(c.x - r * 0.52f, c.y + r * dy),
                    Offset(c.x - r * 1.10f, c.y + r * (dy - 0.10f)),
                    strokeWidth = wStroke, cap = StrokeCap.Round)
                drawLine(Color(0xFF14142A),
                    Offset(c.x + r * 0.52f, c.y + r * dy),
                    Offset(c.x + r * 1.10f, c.y + r * (dy - 0.10f)),
                    strokeWidth = wStroke, cap = StrokeCap.Round)
            }
        }
        HairStyle.STAR -> {
            // Additional small stars
            drawStar(Offset(c.x - r * 0.75f, c.y - r * 0.60f), r * 0.14f, spec.accentColor.copy(alpha = 0.7f))
        }
        HairStyle.LONG -> {
            // Hair clip
            drawRoundRect(
                color = spec.accentColor,
                topLeft = Offset(c.x - r * 1.00f, c.y - r * 0.55f),
                size = Size(r * 0.30f, r * 0.10f),
                cornerRadius = CornerRadius(r * 0.05f)
            )
        }
        HairStyle.TWIN_TAILS -> {
            // Already drew ties
        }
        else -> {}
    }
}

private fun DrawScope.drawStar(center: Offset, radius: Float, color: Color) {
    val p = Path()
    val points = 5
    for (i in 0 until points * 2) {
        val rr = if (i % 2 == 0) radius else radius * 0.45f
        val angle = -PI.toFloat() / 2 + PI.toFloat() * i / points
        val x = center.x + rr * cos(angle)
        val y = center.y + rr * sin(angle)
        if (i == 0) p.moveTo(x, y) else p.lineTo(x, y)
    }
    p.close()
    drawPath(p, color)
}
