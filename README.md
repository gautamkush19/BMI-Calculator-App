🏥 Clinical BMI Calculator - Android App

A professional-grade health assessment application built with Kotlin and Jetpack Compose. Designed with a clinical perspective, this app features real-time vitals analysis, WHO-standard weight classifications, and dynamic medical visualizations.

✨ Key Features

🩺 Professional Medical UI

Clinical Theme: Designed using a Doctor-preferred Deep Teal & Soft Aqua color palette for a trustworthy, clean look.

Heartbeat Animation: A live pulse animation in the header giving the app a dynamic "alive" feel.

📊 Advanced Data Visualization

Medical Ruler Gauge: A custom-drawn, animated linear scale that visually indicates where the patient's BMI falls relative to healthy ranges.

Needle Animation: Smooth spring physics animation for the gauge needle.

📐 Comprehensive Analysis

Dual Unit Support: Seamless switching between Metric (kg/cm) and Imperial (lbs/ft/in) systems.

WHO Classifications: Detailed categorization from Underweight to Obese Class III.

Clinical Interpretations: specific "Doctor's Note" advice based on the calculated result.

📱 Screenshots

Home Screen (Input)

Clinical Report (Result)





Clean clinical input form

Animated gauge & diagnosis

(Note: Replace path/to/screenshot with your actual image paths)

🛠️ Tech Stack

Language: Kotlin

UI Framework: Jetpack Compose (Declarative UI)

Design System: Material Design 3

Animation: androidx.compose.animation (Core, Transition, Springs)

Graphics: Custom Canvas drawing for the Medical Ruler

🚀 How to Run

Prerequisites:

Android Studio (Latest Version recommended).

Minimum SDK: 24 (Android 7.0).

Setup:

Open Android Studio and select Open.

Navigate to the project folder.

Let Gradle sync completely.

Dependencies:
Add the following line to your build.gradle.kts (Module :app) file inside the dependencies { } block to enable extended icons (like the medical heart icon):

implementation("androidx.compose.material:material-icons-extended:1.7.5")


Run:

Connect your Android device via USB (ensure USB Debugging is ON) or create an Android Virtual Device (AVD).

Click the Run (Green Play Icon) button in the toolbar.

👨‍⚕️ Clinical Logic

The app uses the standard WHO (World Health Organization) BMI logic:

Metric Formula: $Weight(kg) / Height(m)^2$

Imperial Formula: $(Weight(lbs) / Height(in)^2) * 703$

Clinical Color Coding:

🔵 Sky Blue: Underweight (< 18.5)

🔵 Teal/Blue: Normal Range (18.5 - 24.9) - Target Healthy Zone

🟡 Yellow: Pre-obese (25.0 - 29.9) - Warning Zone

🟠 Orange: Obese Class I (30.0 - 34.9)

🔴 Red: Obese Class II+ (> 35.0) - Critical Zone

👤 Author

Name: Gautam Kushwaha
Course: BCA (Software Development)
Roll No: 0039

Developed for Android Application Development Assignment.
