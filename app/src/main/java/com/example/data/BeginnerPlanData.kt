package com.example.data

data class PlanExercise(
    val id: String,
    val name: String,
    val sets: Int,
    val repsOrDuration: String,
    val restTimeSeconds: Int,
    val description: String
)

data class PlanDay(
    val dayNumber: Int,
    val weekNumber: Int,
    val isRestDay: Boolean,
    val title: String,
    val focusDescription: String,
    val exercises: List<PlanExercise> = emptyList()
)

object BeginnerPlanData {
    val planDays: List<PlanDay> = listOf(
        // === WEEK 1 ===
        PlanDay(
            dayNumber = 1,
            weekNumber = 1,
            isRestDay = false,
            title = "Strength Foundations",
            focusDescription = "Establish base muscular endurance with essential bodyweight movements.",
            exercises = listOf(
                PlanExercise("squats", "Bodyweight Squats", 3, "10 Reps", 60, "Lower your hips back and down, keeping chest upright and knees tracking over toes."),
                PlanExercise("knee_pushups", "Knee Push-ups", 3, "8 Reps", 60, "Keep your knees flat on the ground, torso straight, lowering chest to the floor."),
                PlanExercise("plank", "Forearm Plank", 3, "20 Secs", 45, "Hold a straight-line position resting on forearms and toes, engaging core and glutes.")
            )
        ),
        PlanDay(
            dayNumber = 2,
            weekNumber = 1,
            isRestDay = false,
            title = "Core Stabilization",
            focusDescription = "Ignite your core muscles to secure daily stability and posture block.",
            exercises = listOf(
                PlanExercise("crunches", "Abdominal Crunches", 3, "12 Reps", 45, "Lie on back with knees bent, curling head and shoulders up off the ground."),
                PlanExercise("russian_twist", "Russian Twist", 3, "12 Reps", 45, "Sit with legs bent, torso inclined at 45 degrees, twisting side-to-side."),
                PlanExercise("plank", "Forearm Plank", 2, "20 Secs", 45, "Keep body straight and rigid on forearms and toes to solidify isometric strength.")
            )
        ),
        PlanDay(
            dayNumber = 3,
            weekNumber = 1,
            isRestDay = true,
            title = "Active Recovery & Mobility",
            focusDescription = "Allow muscles to heal. Take a light walk or perform gentle full body stretches.",
            exercises = emptyList()
        ),
        PlanDay(
            dayNumber = 4,
            weekNumber = 1,
            isRestDay = false,
            title = "Lower Body Endurance",
            focusDescription = "Power up leg progression & stimulate glutes with unilateral reps.",
            exercises = listOf(
                PlanExercise("squats", "Bodyweight Squats", 3, "12 Reps", 60, "Perform clean, full range squats to prime your quad stamina."),
                PlanExercise("lunges", "Alternate Lunges", 3, "10 Reps", 60, "Step one foot forward, lower back knee until front thigh is parallel to ground."),
                PlanExercise("sumo_squats", "Sumo Squats", 2, "10 Reps", 60, "Stand wide with toes pointed 45 degrees outward, squatting deep into hips.")
            )
        ),
        PlanDay(
            dayNumber = 5,
            weekNumber = 1,
            isRestDay = false,
            title = "Upper Body Sculpt",
            focusDescription = "Target the chest, triceps, and shoulders using gravitational resistance.",
            exercises = listOf(
                PlanExercise("knee_pushups", "Knee Push-ups", 3, "10 Reps", 45, "Maintain a solid plank line from knees up, squeezing pectorals at top."),
                PlanExercise("tricep_dips", "Tricep Sofa Dips", 3, "8 Reps", 45, "Support weight on sofa edge behind you, dipping glutes by bending elbows.")
            )
        ),
        PlanDay(
            dayNumber = 6,
            weekNumber = 1,
            isRestDay = false,
            title = "Intro Full Body Synergy",
            focusDescription = "Brief high-energy routine to integrate Week 1 push, pull, and core components.",
            exercises = listOf(
                PlanExercise("jumping_jacks", "Jumping Jacks", 2, "30 Secs", 60, "Rapidly leap feet out while bringing hands over head to raise heart rate."),
                PlanExercise("squats", "Bodyweight Squats", 2, "10 Reps", 60, "Clean steady squads to activate lower body pump."),
                PlanExercise("knee_pushups", "Knee Push-ups", 2, "8 Reps", 60, "Keep elbows tucked to hit chest and triceps evenly."),
                PlanExercise("plank", "Forearm Plank", 2, "20 Secs", 45, "Brace core fully to lock in a straight line from neck to heels.")
            )
        ),
        PlanDay(
            dayNumber = 7,
            weekNumber = 1,
            isRestDay = true,
            title = "Weekend Rest & Restore",
            focusDescription = "Settle down, enjoy standard rest, hydrate well, and let your muscles grow.",
            exercises = emptyList()
        ),

        // === WEEK 2 ===
        PlanDay(
            dayNumber = 8,
            weekNumber = 2,
            isRestDay = false,
            title = "Upper Body Push & Hold",
            focusDescription = "Up the loading volume slightly for pectoral wall and tricep density.",
            exercises = listOf(
                PlanExercise("pushups", "Knee/Std Pushups", 3, "12 Reps", 45, "Attempt a full pushup on toes, scaling to knees when power drains."),
                PlanExercise("tricep_dips", "Tricep Sofa Dips", 3, "10 Reps", 45, "Dip low and press back up dynamically to target standard tricep heads."),
                PlanExercise("plank", "Forearm Plank", 3, "25 Secs", 45, "Exert structural core resistance to build deep breathing stamina.")
            )
        ),
        PlanDay(
            dayNumber = 9,
            weekNumber = 2,
            isRestDay = false,
            title = "Lower Body Sculpt II",
            focusDescription = "Slightly higher repetitions to ignite metabolic burn on your legs.",
            exercises = listOf(
                PlanExercise("squats", "Bodyweight Squats", 3, "15 Reps", 45, "Move up and down smoothly without pausing, locking glutes at top."),
                PlanExercise("lunges", "Alternate Lunges", 3, "12 Reps", 45, "Deep steady strides to develop lower body single-leg balance."),
                PlanExercise("sumo_squats", "Sumo Squats", 3, "12 Reps", 45, "Stand wide to target inner thighs, pressing hips backward cleanly.")
            )
        ),
        PlanDay(
            dayNumber = 10,
            weekNumber = 2,
            isRestDay = true,
            title = "Mid-week Restoration",
            focusDescription = "Recharge energy levels. Walk, stay flexible, and recover joints safely.",
            exercises = emptyList()
        ),
        PlanDay(
            dayNumber = 11,
            weekNumber = 2,
            isRestDay = false,
            title = "Absolute Core Strength",
            focusDescription = "Build deeper abdominal bracing by adding static lateral supports.",
            exercises = listOf(
                PlanExercise("crunches", "Abdominal Crunches", 3, "15 Reps", 45, "Control deceleration when returning shoulders back to ground."),
                PlanExercise("russian_twist", "Russian Twist", 3, "16 Reps", 45, "Keep hips anchored and rotate solely from standard ribcage torso."),
                PlanExercise("plank", "Forearm Plank", 3, "30 Secs", 45, "Keep spine entirely parallel to floor, squeezing stomach tightly.")
            )
        ),
        PlanDay(
            dayNumber = 12,
            weekNumber = 2,
            isRestDay = false,
            title = "Stamina & Climbers",
            focusDescription = "Incorporate rapid agility components to shed fat and bolster endurance.",
            exercises = listOf(
                PlanExercise("jumping_jacks", "Jumping Jacks", 3, "40 Secs", 60, "Rhythmic execution keeping calf tension high and fluid."),
                PlanExercise("mountain_climbers", "Mountain Climbers", 3, "20 Secs", 60, "Drive knees alternatingly to chest in quick, synchronized speed."),
                PlanExercise("plank", "Forearm Plank", 3, "30 Secs", 45, "Lock down upper spine to transition after heavy aerobic exhaustion.")
            )
        ),
        PlanDay(
            dayNumber = 13,
            weekNumber = 2,
            isRestDay = false,
            title = "Full Body Progressive II",
            focusDescription = "Midway test of solid integration combining progressive push and leg volume.",
            exercises = listOf(
                PlanExercise("squats", "Bodyweight Squats", 3, "12 Reps", 60, "Keep heels flat on ground, looking straight to protect neck."),
                PlanExercise("knee_pushups", "Knee Push-ups", 3, "10 Reps", 60, "Push through full palm base, engaging front shoulders."),
                PlanExercise("lunges", "Alternate Lunges", 2, "12 Reps", 60, "Control downward descent to master posture stabilization."),
                PlanExercise("plank", "Forearm Plank", 2, "30 Secs", 45, "Keep glutes active and prevent hips from sagging low.")
            )
        ),
        PlanDay(
            dayNumber = 14,
            weekNumber = 2,
            isRestDay = true,
            title = "Rest & Muscle Synthesis",
            focusDescription = "Take a well deserved full system break. Your body builds muscle when resting!",
            exercises = emptyList()
        ),

        // === WEEK 3 ===
        PlanDay(
            dayNumber = 15,
            weekNumber = 3,
            isRestDay = false,
            title = "Target Upper Mastery",
            focusDescription = "Level up to standard push-ups. Test your chest limits with deep dips.",
            exercises = listOf(
                PlanExercise("pushups", "Standard Push-ups", 3, "12 Reps", 45, "Torso rigid on toes, fingers flared forward, pushing fully up."),
                PlanExercise("tricep_dips", "Tricep Sofa Dips", 3, "12 Reps", 45, "Keep body close to chair/sofa to put absolute load on triceps."),
                PlanExercise("plank", "Forearm Plank", 3, "35 Secs", 45, "Intense isometric core hold with minimal breath fluctuation.")
            )
        ),
        PlanDay(
            dayNumber = 16,
            weekNumber = 3,
            isRestDay = false,
            title = "Quadriceps & Glute Focus",
            focusDescription = "Introduce high-rep set scheme to test and build absolute leg power.",
            exercises = listOf(
                PlanExercise("sumo_squats", "Sumo Squats", 4, "15 Reps", 60, "Sumo stance deep squats to fully stretch external hamstrings."),
                PlanExercise("lunges", "Alternate Lunges", 4, "14 Reps", 60, "Control lunging motion, pushing powerfully back to start."),
                PlanExercise("squats", "Bodyweight Squats", 3, "15 Reps", 60, "Unbroken sets to flush lower body with blood oxygen.")
            )
        ),
        PlanDay(
            dayNumber = 17,
            weekNumber = 3,
            isRestDay = true,
            title = "Mid-week Rest & Stretch",
            focusDescription = "Full physical break. Do deep floor stretches to prevent stiffness.",
            exercises = emptyList()
        ),
        PlanDay(
            dayNumber = 18,
            weekNumber = 3,
            isRestDay = false,
            title = "Extreme Core Burn",
            focusDescription = "Combine aerobic core drive with abdominal endurance components.",
            exercises = listOf(
                PlanExercise("crunches", "Abdominal Crunches", 3, "18 Reps", 45, "Press lower back flat on floor during standard crunch execution."),
                PlanExercise("mountain_climbers", "Mountain Climbers", 3, "30 Secs", 45, "High velocity alternating leg drives to ignite rectus abdominis."),
                PlanExercise("russian_twist", "Russian Twist", 3, "20 Reps", 45, "Add deep breathing on twists to hit outer obliques."),
                PlanExercise("plank", "Forearm Plank", 2, "40 Secs", 45, "Maintain absolute rigidity, refusing to buckle core.")
            )
        ),
        PlanDay(
            dayNumber = 19,
            weekNumber = 3,
            isRestDay = false,
            title = "Upper Push Stability",
            focusDescription = "Solid set counts to push upper chest limits.",
            exercises = listOf(
                PlanExercise("pushups", "Standard Push-ups", 4, "10 Reps", 60, "Focus on depth, bringing chest within 2 inches of floor."),
                PlanExercise("tricep_dips", "Tricep Sofa Dips", 4, "12 Reps", 60, "Full tricep extension locking solid elbows at peak."),
                PlanExercise("plank", "Forearm Plank", 3, "40 Secs", 45, "Tuck pelvis slightly to maximize direct ab activation.")
            )
        ),
        PlanDay(
            dayNumber = 20,
            weekNumber = 3,
            isRestDay = false,
            title = "Full Body Power Burn III",
            focusDescription = "A challenging endurance system to maximize compound calorie output.",
            exercises = listOf(
                PlanExercise("jumping_jacks", "Jumping Jacks", 3, "45 Secs", 60, "Execute high and wide, building whole vascular breath capacity."),
                PlanExercise("squats", "Bodyweight Squats", 3, "15 Reps", 60, "Descend past parallel if comfortable, loading quads cleanly."),
                PlanExercise("pushups", "Standard Push-ups", 3, "12 Reps", 60, "Hinge fully with tight, supportive glutes and core."),
                PlanExercise("lunges", "Alternate Lunges", 3, "16 Reps", 60, "Drive body upright using front heel power.")
            )
        ),
        PlanDay(
            dayNumber = 21,
            weekNumber = 3,
            isRestDay = true,
            title = "Weekend Restoration Phase",
            focusDescription = "Relax, reflect, keep active with non-weight bearing movements, hydrate.",
            exercises = emptyList()
        ),

        // === WEEK 4 ===
        PlanDay(
            dayNumber = 22,
            weekNumber = 4,
            isRestDay = false,
            title = "Maximum Upper Power",
            focusDescription = "The peak push challenge. Target triceps & chest with high overload sets.",
            exercises = listOf(
                PlanExercise("pushups", "Standard Push-ups", 4, "12 Reps", 45, "Push to near failure, drop to knees for knee pushups to complete reps if raw."),
                PlanExercise("tricep_dips", "Tricep Sofa Dips", 4, "12 Reps", 45, "Exhale with force on press, keeping control on descent."),
                PlanExercise("plank", "Forearm Plank", 3, "45 Secs", 45, "Engage every single muscle fiber to maintain strict form.")
            )
        ),
        PlanDay(
            dayNumber = 23,
            weekNumber = 4,
            isRestDay = false,
            title = "Lower Body Champion",
            focusDescription = "Mastery of squatting depth and single-leg landing progression.",
            exercises = listOf(
                PlanExercise("squats", "Bodyweight Squats", 4, "20 Reps", 45, "Control movement, keeping tempo steady and focus deep."),
                PlanExercise("sumo_squats", "Sumo Squats", 4, "15 Reps", 45, "Feel inner thigh extension, pressing out with outer glutes."),
                PlanExercise("lunges", "Alternate Lunges", 4, "18 Reps", 45, "Full step and pushback, stabilizing torso without wobbles.")
            )
        ),
        PlanDay(
            dayNumber = 24,
            weekNumber = 4,
            isRestDay = true,
            title = "Active Reset",
            focusDescription = "Prepare for the final routines of Week 4. Stretch shoulders and lower back.",
            exercises = emptyList()
        ),
        PlanDay(
            dayNumber = 25,
            weekNumber = 4,
            isRestDay = false,
            title = "Abs Solid Steel",
            focusDescription = "An outstanding final challenge to carve out muscular lines and flat support.",
            exercises = listOf(
                PlanExercise("plank", "Forearm Plank", 4, "45 Secs", 45, "Steady and immoveable like a rock, breathing in small sips."),
                PlanExercise("crunches", "Abdominal Crunches", 4, "20 Reps", 45, "Focus on deep muscular contraction rather than head pulling."),
                PlanExercise("russian_twist", "Russian Twist", 4, "24 Reps", 45, "Rotate wide and slow to lock in maximum abdominal time under tension."),
                PlanExercise("mountain_climbers", "Mountain Climbers", 3, "30 Secs", 45, "Drive high speed knees forward, breathing out deeply on loops.")
            )
        ),
        PlanDay(
            dayNumber = 26,
            weekNumber = 4,
            isRestDay = false,
            title = "Supreme Athletic Power",
            focusDescription = "Burn high calorie loops with full body power combos.",
            exercises = listOf(
                PlanExercise("jumping_jacks", "Jumping Jacks", 4, "50 Secs", 60, "Maximum height and speed to maximize cardiorespiratory endurance."),
                PlanExercise("squats", "Bodyweight Squats", 4, "18 Reps", 60, "Deep parallel squats maintaining neutral straight spine posture."),
                PlanExercise("pushups", "Standard Push-ups", 4, "12 Reps", 60, "Push through triceps, locking out arms cleanly at the apex."),
                PlanExercise("plank", "Forearm Plank", 4, "45 Secs", 45, "Final ab burnout hold, clenching arms close under chin.")
            )
        ),
        PlanDay(
            dayNumber = 27,
            weekNumber = 4,
            isRestDay = false,
            title = "Synergy Finale Challenge",
            focusDescription = "The ultimate test. Execute all exercises in sequence to finalize your 4-week transformation.",
            exercises = listOf(
                PlanExercise("squats", "Bodyweight Squats", 4, "15 Reps", 45, "Steady form and breathing to maintain rhythm."),
                PlanExercise("pushups", "Standard Push-ups", 4, "12 Reps", 45, "Perfect alignment from head to heels without sagging."),
                PlanExercise("lunges", "Alternate Lunges", 4, "16 Reps", 45, "Full knee flexion in step, alternating with perfect stance."),
                PlanExercise("plank", "Forearm Plank", 4, "40 Secs", 45, "Engage glutes to prevent back strain under exhaustion."),
                PlanExercise("jumping_jacks", "Jumping Jacks", 2, "30 Secs", 45, "Unleash all energy with rapid pacing to finish.")
            )
        ),
        PlanDay(
            dayNumber = 28,
            weekNumber = 4,
            isRestDay = true,
            title = "Ultimate Coronation Rest",
            focusDescription = "CONGRATULATIONS! You have completed the 4-week home strength academy. Check your records and logs!",
            exercises = emptyList()
        )
    )
}
