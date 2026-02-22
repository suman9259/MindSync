# MindSync

A modern Android wellness and productivity app built with Jetpack Compose and Material 3 design. MindSync helps you track workouts, meditation, skincare routines, medicines, sleep, and more — all in one beautifully designed dark-themed application.

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)

## Features

### 🏋️ Workout Tracking
- Start empty workouts or use routines
- Log exercises with sets, reps, and weight
- Track workout duration, volume, and progress
- Browse exercises by muscle group
- Personal records tracking

### 🧘 Meditation
- Guided meditation sessions
- Breathing exercises
- Session tracking and streaks
- Category-based meditation library

### 💊 Medicine & Supplements
- Track medications and supplements
- Set reminders for doses
- Manage dosage schedules

### 🛁 Skincare Routines
- Morning and evening routines
- Step-by-step tracking
- Streak tracking for consistency

### 😴 Sleep Tracker
- Set bedtime and wake time goals
- Track sleep quality
- View sleep history and patterns

### 💧 Water Intake
- Daily hydration goals
- Track glasses of water
- Visual progress indicators

### 🛒 Grocery List
- Add and manage grocery items
- Mark items as purchased
- Share lists with others

### 📚 Student Tools
- Assignment tracking
- Due date reminders
- Status management (pending, in progress, completed)

### 🚗 Vehicle Maintenance
- Track multiple vehicles
- Maintenance reminders
- Service history

### 🎂 Birthdays & Events
- Never forget important dates
- Gift idea notes
- Upcoming events view

### 💰 Bills & Payments
- Track recurring bills
- Due date reminders
- Payment status tracking

## Tech Stack

| Technology | Purpose |
|------------|---------|
| **Kotlin** | Primary language |
| **Jetpack Compose** | Modern declarative UI |
| **Material 3** | Design system |
| **Firebase Auth** | User authentication |
| **Firebase Firestore** | Cloud database |
| **Firebase Storage** | File storage |
| **Koin** | Dependency injection |
| **Coroutines & Flow** | Asynchronous programming |
| **Navigation Compose** | Screen navigation |
| **Room** | Local database |

## Architecture

MindSync follows **Clean Architecture** principles with MVVM pattern:

```
app/
├── data/
│   ├── local/          # Room database, DataStore
│   ├── remote/         # Firebase services
│   └── repository/     # Repository implementations
├── di/                 # Koin modules
├── domain/
│   ├── model/          # Domain models
│   ├── repository/     # Repository interfaces
│   └── usecase/        # Business logic
└── presentation/
    ├── auth/           # Login/Register screens
    ├── dashboard/      # Main dashboard
    ├── workout/        # Workout tracking
    ├── meditation/     # Meditation features
    ├── medicine/       # Medicine tracking
    ├── skincare/       # Skincare routines
    ├── lifestyle/      # Sleep, water, bills, etc.
    ├── navigation/     # Navigation graph
    ├── theme/          # App theming
    └── components/     # Reusable composables
```

## Theme

MindSync features a modern **dark theme** with blue accents:

| Color | Hex | Usage |
|-------|-----|-------|
| Dark Background | `#0D0D0D` | Screen backgrounds |
| Card Background | `#1A1A1A` | Cards and surfaces |
| Blue Accent | `#4A90D9` | Primary actions, FABs |
| Purple Accent | `#6B5CE7` | Secondary accents |

## Requirements

- **Android Studio**: Hedgehog or newer
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 36
- **JDK**: 11+

## Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/MindSync.git
   cd MindSync
   ```

2. **Firebase Setup**
   - Create a Firebase project at [Firebase Console](https://console.firebase.google.com)
   - Enable Authentication (Email/Password and Google Sign-In)
   - Enable Firestore Database
   - Download `google-services.json` and place it in `app/`

3. **Build the project**
   ```bash
   ./gradlew assembleDebug
   ```

4. **Run on device/emulator**
   ```bash
   ./gradlew installDebug
   ```

## Building

### Debug Build
```bash
./gradlew assembleDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

## Screenshots

| Dashboard | Workout | Meditation |
|:---------:|:-------:|:----------:|
| Dark themed home | Exercise logging | Guided sessions |

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [Jetpack Compose](https://developer.android.com/jetpack/compose) - Modern Android UI toolkit
- [Material Design 3](https://m3.material.io/) - Design system
- [Firebase](https://firebase.google.com/) - Backend services
- [Koin](https://insert-koin.io/) - Dependency injection framework
