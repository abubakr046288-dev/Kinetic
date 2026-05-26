package com.example.data

enum class SVGIconType {
    SQUATS, PUSHUPS, PLANKS, CRUNCHES, JACKS, STRETCH, LUNGES, ARMS
}

data class Exercise(
    val id: String,
    val name: String,
    val description: String,
    val durationSeconds: Int = 30, // if > 0, time-based, otherwise rep-based
    val reps: Int = 0,             // if > 0, rep-based, otherwise time-based
    val caloriesBurned: Float,     // estimated calories burned
    val iconType: SVGIconType
)

data class WorkoutRoutine(
    val id: String,
    val name: String,
    val level: String,      // "Beginner", "Intermediate", "Advanced"
    val category: String,   // "Abs", "Chest", "Arms", "Legs", "Full Body"
    val exercises: List<Exercise>,
    val description: String
) {
    val totalEstimatedTimeSeconds: Int
        get() = exercises.sumOf { if (it.durationSeconds > 0) it.durationSeconds else 15 } + (exercises.size - 1) * 10 // adding 10s rest between each

    val totalCalories: Float
        get() = exercises.sumOf { it.caloriesBurned.toDouble() }.toFloat()
}

object WorkoutRoutinesData {
    // Standard exercises
    val jumpingJacks = Exercise(
        id = "jumping_jacks",
        name = "Jumping Jacks",
        description = "Start with your feet together and arms at your sides. Jump your feet out to the sides while raising your arms above your head. In one fluid motion, jump back to the starting position.",
        durationSeconds = 30,
        caloriesBurned = 10f,
        iconType = SVGIconType.JACKS
    )

    val pushups = Exercise(
        id = "pushups",
        name = "Push-ups",
        description = "Place your hands shoulder-width apart on the floor, body straight from head to heels. Lower your chest to the floor by bending your elbows, then push yourself back up.",
        reps = 15,
        durationSeconds = 0,
        caloriesBurned = 12f,
        iconType = SVGIconType.PUSHUPS
    )

    val kneePushups = Exercise(
        id = "knee_pushups",
        name = "Knee Push-ups",
        description = "Similar to standard push-ups, but keep your knees resting on the ground. Keep your hands flat, push upward, keeping your core tight and back straight.",
        reps = 12,
        durationSeconds = 0,
        caloriesBurned = 8f,
        iconType = SVGIconType.PUSHUPS
    )

    val diamondPushups = Exercise(
        id = "diamond_pushups",
        name = "Diamond Push-ups",
        description = "Join your index fingers and thumbs together directly under your chest to form a diamond shape. Lower your chest, flare your elbows slightly outward, and push back up.",
        reps = 10,
        durationSeconds = 0,
        caloriesBurned = 15f,
        iconType = SVGIconType.PUSHUPS
    )

    val plank = Exercise(
        id = "plank",
        name = "Forearm Plank",
        description = "Rest your weight on your forearms and toes. Keep your body in a straight line, parallel to the floor, engage your core, squeeze your glutes, and hold this position.",
        durationSeconds = 30,
        caloriesBurned = 15f,
        iconType = SVGIconType.PLANKS
    )

    val sidePlankLeft = Exercise(
        id = "side_plank_left",
        name = "Side Plank Left",
        description = "Lie on your left side with your body straight. Support your weight on your left forearm and feet, lifting your hips high in the air in a straight line.",
        durationSeconds = 20,
        caloriesBurned = 10f,
        iconType = SVGIconType.PLANKS
    )

    val sidePlankRight = Exercise(
        id = "side_plank_right",
        name = "Side Plank Right",
        description = "Lie on your right side with your body straight. Support your weight on your right forearm and feet, lifting your hips high in the air in a straight line.",
        durationSeconds = 20,
        caloriesBurned = 10f,
        iconType = SVGIconType.PLANKS
    )

    val crunches = Exercise(
        id = "crunches",
        name = "Abdominal Crunches",
        description = "Lie on your back with knees bent and feet flat on the floor. Place your hands lightly behind your head, then lift your upper body, contracting your abs.",
        reps = 15,
        durationSeconds = 0,
        caloriesBurned = 9f,
        iconType = SVGIconType.CRUNCHES
    )

    val russianTwist = Exercise(
        id = "russian_twist",
        name = "Russian Twist",
        description = "Sit on the floor, knees bent, leaning back slightly at a 45-degree angle. Clasp your hands in front and twist your torso dynamically from side to side.",
        reps = 20,
        durationSeconds = 0,
        caloriesBurned = 11f,
        iconType = SVGIconType.CRUNCHES
    )

    val mountainClimbers = Exercise(
        id = "mountain_climbers",
        name = "Mountain Climbers",
        description = "Start in a push-up position. Alternately drive your knees toward your chest as fast as you can, keeping your core tight and your hips low.",
        durationSeconds = 30,
        caloriesBurned = 14f,
        iconType = SVGIconType.JACKS
    )

    val squats = Exercise(
        id = "squats",
        name = "Squats",
        description = "Stand with feet shoulder-width apart. Lower your hips as if sitting back into a deep chair, keeping your chest up and knees behind your toes, then push up.",
        reps = 15,
        durationSeconds = 0,
        caloriesBurned = 13f,
        iconType = SVGIconType.SQUATS
    )

    val sumoSquats = Exercise(
        id = "sumo_squats",
        name = "Sumo Squats",
        description = "Stand with a wide stance, toes pointed outward at a 45-degree angle. Sit back into a squat, squeezing your inner thighs and glutes as you return to standing.",
        reps = 12,
        durationSeconds = 0,
        caloriesBurned = 14f,
        iconType = SVGIconType.SQUATS
    )

    val lunges = Exercise(
        id = "lunges",
        name = "Lunges",
        description = "Step forward with one leg until your front thigh is parallel to the ground and rear knee is hovering. Push off your front foot to return to standing. Alternate legs.",
        reps = 16,
        durationSeconds = 0,
        caloriesBurned = 12f,
        iconType = SVGIconType.LUNGES
    )

    val cobraStretch = Exercise(
        id = "cobra_stretch",
        name = "Cobra Stretch",
        description = "Lie face down with hands under your shoulders. Gently push your chest upright, keeping your pelvis anchored to the floor. Look up to stretch your abs and back.",
        durationSeconds = 20,
        caloriesBurned = 5f,
        iconType = SVGIconType.STRETCH
    )

    val tricepDips = Exercise(
        id = "tricep_dips",
        name = "Tricep Dips",
        description = "Place your hands behind you on a sturdy chair, sofa, or step. Slide your glutes off the edge, bend your elbows to lower your hips, then push back up using your triceps.",
        reps = 12,
        durationSeconds = 0,
        caloriesBurned = 10f,
        iconType = SVGIconType.ARMS
    )

    val armRaises = Exercise(
        id = "arm_raises",
        name = "Arm Raises",
        description = "Stand straight. Raise your arms straight out to your sides until they are level with your shoulders. Lower them slowly and repeat to warm up arm joints.",
        durationSeconds = 30,
        caloriesBurned = 6f,
        iconType = SVGIconType.ARMS
    )

    // Precompiled workout routines
    val routinesList = listOf(
        // Chest routines
        WorkoutRoutine(
            id = "chest_beginner",
            name = "Chest Beginner",
            level = "Beginner",
            category = "Chest",
            description = "Build a strong chest and trim chest fat. Suitable for absolute beginners.",
            exercises = listOf(jumpingJacks, kneePushups, pushups, cobraStretch)
        ),
        WorkoutRoutine(
            id = "chest_intermediate",
            name = "Chest Intermediate",
            level = "Intermediate",
            category = "Chest",
            description = "Fiercer exercises to define pectorals and build power.",
            exercises = listOf(jumpingJacks, pushups, diamondPushups, tricepDips, cobraStretch)
        ),
        WorkoutRoutine(
            id = "chest_advanced",
            name = "Chest Advanced",
            level = "Advanced",
            category = "Chest",
            description = "High-intensity home chest routine for ultimate development.",
            exercises = listOf(jumpingJacks, pushups, diamondPushups, tricepDips, mountainClimbers, pushups, cobraStretch)
        ),

        // Abs routines
        WorkoutRoutine(
            id = "abs_beginner",
            name = "Abs Beginner",
            level = "Beginner",
            category = "Abs",
            description = "Tone your belly and establish solid core support.",
            exercises = listOf(jumpingJacks, crunches, russianTwist, plank, cobraStretch)
        ),
        WorkoutRoutine(
            id = "abs_intermediate",
            name = "Abs Intermediate",
            level = "Intermediate",
            category = "Abs",
            description = "Intense core burns simulating absolute six-pack training.",
            exercises = listOf(jumpingJacks, crunches, mountainClimbers, russianTwist, sidePlankLeft, sidePlankRight, plank)
        ),
        WorkoutRoutine(
            id = "abs_advanced",
            name = "Abs Advanced",
            level = "Advanced",
            category = "Abs",
            description = "Ultimate shred to build powerful abdominal brick walls.",
            exercises = listOf(jumpingJacks, mountainClimbers, crunches, russianTwist, plank, sidePlankLeft, sidePlankRight, crunches, plank)
        ),

        // Arms routines
        WorkoutRoutine(
            id = "arms_beginner",
            name = "Arms Beginner",
            level = "Beginner",
            category = "Arms",
            description = "Tone your biceps and triceps without any weights.",
            exercises = listOf(armRaises, kneePushups, tricepDips, armRaises)
        ),
        WorkoutRoutine(
            id = "arms_intermediate",
            name = "Arms Intermediate",
            level = "Intermediate",
            category = "Arms",
            description = "Develop sleek arms and firm shoulders.",
            exercises = listOf(armRaises, pushups, tricepDips, diamondPushups, plank)
        ),
        WorkoutRoutine(
            id = "arms_advanced",
            name = "Arms Advanced",
            level = "Advanced",
            category = "Arms",
            description = "Tear down arm fats with high tension training.",
            exercises = listOf(jumpingJacks, pushups, tricepDips, diamondPushups, pushups, plank, armRaises)
        ),

        // Legs routines
        WorkoutRoutine(
            id = "legs_beginner",
            name = "Legs Beginner",
            level = "Beginner",
            category = "Legs",
            description = "Strengthen your quad muscles, hamstrings, and calves.",
            exercises = listOf(jumpingJacks, squats, lunges, squats)
        ),
        WorkoutRoutine(
            id = "legs_intermediate",
            name = "Legs Intermediate",
            level = "Intermediate",
            category = "Legs",
            description = "Sculpt gorgeous glutes and tight thighs.",
            exercises = listOf(jumpingJacks, squats, sumoSquats, lunges, squats)
        ),
        WorkoutRoutine(
            id = "legs_advanced",
            name = "Legs Advanced",
            level = "Advanced",
            category = "Legs",
            description = "An intense athletic body burner for professional leg density.",
            exercises = listOf(jumpingJacks, squats, sumoSquats, lunges, mountainClimbers, squats, lunges)
        ),

        // Full Body routine
        WorkoutRoutine(
            id = "full_body_challenge",
            name = "7x4 Full Body Challenge",
            level = "Intermediate",
            category = "Full Body",
            description = "Daily active workout targeting all major physical muscle groups.",
            exercises = listOf(jumpingJacks, squats, pushups, crunches, mountainClimbers, lunges, plank, cobraStretch)
        )
    )
}
