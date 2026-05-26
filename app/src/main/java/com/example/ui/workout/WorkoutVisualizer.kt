package com.example.ui.workout

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.data.SVGIconType

@Composable
fun WorkoutVisualizer(
    iconType: SVGIconType,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFFFF5722) // Sporty Orange Accent!
) {
    val infiniteTransition = rememberInfiniteTransition(label = "workout_animation")
    
    // Smooth infinite breathing animation used to interpolate exercise stance transitions
    val animationFraction by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "exercise_phase"
    )

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cx = w / 2
            val cy = h / 2
            val radius = w.coerceAtMost(h) / 8
            
            // Grid line / floor
            drawLine(
                color = Color.Gray.copy(alpha = 0.3f),
                start = Offset(cx - w / 2.5f, cy + h / 3),
                end = Offset(cx + w / 2.5f, cy + h / 3),
                strokeWidth = 6f,
                cap = StrokeCap.Round
            )

            val strokeWidth = 14f
            val bodyColor = color
            val limbColor = color.copy(alpha = 0.85f)
            val joinColor = Color.White

            when (iconType) {
                SVGIconType.JACKS -> {
                    // Jumping jacks: stance goes from narrow and arms down, to wide and arms up
                    val legSpread = 40f + (60f * animationFraction)
                    val armAngle = -40f + (220f * animationFraction) // in degrees

                    // Head
                    drawCircle(
                        color = bodyColor,
                        radius = radius * 0.8f,
                        center = Offset(cx, cy - h / 5)
                    )

                    // Torso
                    val spineStart = Offset(cx, cy - h / 7)
                    val spineEnd = Offset(cx, cy + h / 12)
                    drawLine(
                        color = bodyColor,
                        start = spineStart,
                        end = spineEnd,
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )

                    // Legs
                    drawLine(
                        color = limbColor,
                        start = spineEnd,
                        end = Offset(cx - legSpread, cy + h / 4),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = limbColor,
                        start = spineEnd,
                        end = Offset(cx + legSpread, cy + h / 4),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )

                    // Arms
                    val armLength = w / 4
                    val leftArmX = cx - armLength * kotlin.math.cos(Math.toRadians(armAngle.toDouble())).toFloat()
                    val leftArmY = (cy - h/7) - armLength * kotlin.math.sin(Math.toRadians(armAngle.toDouble())).toFloat()
                    val rightArmX = cx + armLength * kotlin.math.cos(Math.toRadians(armAngle.toDouble())).toFloat()
                    val rightArmY = (cy - h/7) - armLength * kotlin.math.sin(Math.toRadians(armAngle.toDouble())).toFloat()

                    drawLine(
                        color = limbColor,
                        start = spineStart,
                        end = Offset(leftArmX, leftArmY),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = limbColor,
                        start = spineStart,
                        end = Offset(rightArmX, rightArmY),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                }

                SVGIconType.PUSHUPS -> {
                    // Pushups: Body moves closer or farther from floor
                    val heightOffset = h / 8 * animationFraction
                    val bodyY = cy + h / 12 + heightOffset

                    // Body line: From feet (left) to neck (right under head)
                    val feet = Offset(cx - w / 3, cy + h / 6)
                    val headJoint = Offset(cx + w / 5, bodyY)

                    // Draw body line
                    drawLine(
                        color = bodyColor,
                        start = feet,
                        end = headJoint,
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )

                    // Head
                    drawCircle(
                        color = bodyColor,
                        radius = radius * 0.8f,
                        center = Offset(cx + w / 4, bodyY - h / 15)
                    )

                    // Supporting arm
                    val armJoint = Offset(cx + w / 8, bodyY)
                    val floorHandPoint = Offset(cx + w / 8, cy + h / 6)
                    drawLine(
                        color = limbColor,
                        start = armJoint,
                        end = floorHandPoint,
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                }

                SVGIconType.PLANKS -> {
                    // Planks: Small shaking or holds. High contrast chest squeeze.
                    val vibration = 4f * animationFraction
                    val bodyY = cy + h / 8 + vibration

                    val feet = Offset(cx - w / 3, cy + h / 6)
                    val shoulder = Offset(cx + w / 6, bodyY)

                    // Body
                    drawLine(
                        color = bodyColor,
                        start = feet,
                        end = shoulder,
                        strokeWidth = strokeWidth + 2f,
                        cap = StrokeCap.Round
                    )

                    // Head
                    drawCircle(
                        color = bodyColor,
                        radius = radius * 0.8f,
                        center = Offset(cx + w / 4, bodyY - h / 20)
                    )

                    // Forearm resting on deck
                    val hand = Offset(cx + w / 4, cy + h / 6)
                    val elbow = Offset(cx + w / 6, cy + h / 6)
                    drawLine(
                        color = limbColor,
                        start = shoulder,
                        end = elbow,
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = limbColor,
                        start = elbow,
                        end = hand,
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                }

                SVGIconType.SQUATS -> {
                    // Squats - Standing to squatting
                    val squatDepth = (h / 7) * animationFraction
                    
                    val headCenter = Offset(cx, cy - h / 5 + squatDepth)
                    val waist = Offset(cx - 30f * animationFraction, cy + h / 14 + squatDepth)
                    val knees = Offset(cx + 40f + (15f * animationFraction), cy + h / 8 + (30f * animationFraction))
                    val feet = Offset(cx + 20f, cy + h / 4)

                    // Head
                    drawCircle(
                        color = bodyColor,
                        radius = radius * 0.8f,
                        center = headCenter
                    )

                    // Torso
                    drawLine(
                        color = bodyColor,
                        start = Offset(cx, cy - h / 10 + squatDepth),
                        end = waist,
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )

                    // Thighs
                    drawLine(
                        color = limbColor,
                        start = waist,
                        end = knees,
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )

                    // Lower legs
                    drawLine(
                        color = limbColor,
                        start = knees,
                        end = feet,
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )

                    // Extended arms
                    drawLine(
                        color = limbColor,
                        start = Offset(cx, cy - h / 12 + squatDepth),
                        end = Offset(cx + w / 5, cy - h / 10 + squatDepth),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                }

                SVGIconType.CRUNCHES -> {
                    // Abdominal crunches: Lie on back, shoulder curling.
                    val liftAngle = 30f * animationFraction

                    // Floor reference
                    val backHip = Offset(cx - w / 8, cy + h / 6)
                    val knees = Offset(cx + w / 8, cy + h / 14)
                    val feet = Offset(cx + w / 4, cy + h / 6)

                    // Legs
                    drawLine(
                        color = limbColor,
                        start = backHip,
                        end = knees,
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = limbColor,
                        start = knees,
                        end = feet,
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )

                    // Torso curling lift
                    val neckX = cx - w / 4 + (20f * animationFraction)
                    val neckY = (cy + h / 6) - (40f * animationFraction)
                    val spineStart = Offset(neckX, neckY)

                    drawLine(
                        color = bodyColor,
                        start = backHip,
                        end = spineStart,
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )

                    // Head
                    drawCircle(
                        color = bodyColor,
                        radius = radius * 0.8f,
                        center = Offset(neckX - 15f, neckY - 20f)
                    )

                    // Arm curled on back of neck
                    drawLine(
                        color = limbColor,
                        start = spineStart,
                        end = Offset(neckX - 25f, neckY),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                }

                SVGIconType.STRETCH -> {
                    // Cobra Stretch
                    val archOffset = (h / 12) * animationFraction
                    
                    val feetY = cy + h / 6
                    val kneesY = cy + h / 6
                    val hips = Offset(cx - w / 16, cy + h / 6)
                    
                    // Chest is pushed up high, arching back
                    val chest = Offset(cx + w / 6, cy + h / 14 - archOffset)

                    // Leg/Thigh anchor line
                    drawLine(
                        color = limbColor,
                        start = Offset(cx - w / 3, feetY),
                        end = hips,
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )

                    // Arching Torso
                    drawLine(
                        color = bodyColor,
                        start = hips,
                        end = chest,
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )

                    // Head looking up
                    drawCircle(
                        color = bodyColor,
                        radius = radius * 0.8f,
                        center = Offset(cx + w / 5 + archOffset/2, cy + h / 30 - archOffset - 15f)
                    )

                    // Support Arms extended straight down
                    drawLine(
                        color = limbColor,
                        start = chest,
                        end = Offset(cx + w / 6, cy + h / 6),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                }

                SVGIconType.LUNGES -> {
                    // Lunges - Back leg bent down, front leg forward
                    val depthOffset = 30f * animationFraction
                    
                    val headCenter = Offset(cx, cy - h / 5 + depthOffset)
                    val waist = Offset(cx, cy + h / 12 + depthOffset)
                    
                    // Front foot is forward
                    val frontKnee = Offset(cx + w / 5, cy + h / 10 + depthOffset)
                    val frontFoot = Offset(cx + w / 5, cy + h / 4)

                    // Back foot is far behind, bent toward floor
                    val backKnee = Offset(cx - w / 5, cy + h / 8 + depthOffset)
                    val backFoot = Offset(cx - w / 4, cy + h / 4)

                    // Head
                    drawCircle(
                        color = bodyColor,
                        radius = radius * 0.8f,
                        center = headCenter
                    )

                    // Torso
                    drawLine(
                        color = bodyColor,
                        start = headCenter,
                        end = waist,
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )

                    // Front limb
                    drawLine(
                        color = limbColor,
                        start = waist,
                        end = frontKnee,
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = limbColor,
                        start = frontKnee,
                        end = frontFoot,
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )

                    // Back limb
                    drawLine(
                        color = limbColor,
                        start = waist,
                        end = backKnee,
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = limbColor,
                        start = backKnee,
                        end = backFoot,
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                }

                SVGIconType.ARMS -> {
                    // Arm raises / circles
                    val radiusOffset = (30f * animationFraction)
                    
                    // Head
                    drawCircle(
                        color = bodyColor,
                        radius = radius * 0.8f,
                        center = Offset(cx, cy - h / 5)
                    )

                    // Torso
                    drawLine(
                        color = bodyColor,
                        start = Offset(cx, cy - h / 7),
                        end = Offset(cx, cy + h / 10),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )

                    // Static Legs
                    drawLine(
                        color = limbColor,
                        start = Offset(cx, cy + h / 10),
                        end = Offset(cx - w / 12, cy + h / 4),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = limbColor,
                        start = Offset(cx, cy + h / 10),
                        end = Offset(cx + w / 12, cy + h / 4),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )

                    // Raised wings spinning up & down
                    drawLine(
                        color = limbColor,
                        start = Offset(cx, cy - h / 8),
                        end = Offset(cx - w / 4, cy - h / 8 - radiusOffset),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = limbColor,
                        start = Offset(cx, cy - h / 8),
                        end = Offset(cx + w / 4, cy - h / 8 - radiusOffset),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                }
            }
        }
    }
}
