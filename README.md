# SafeNest AI 🏠🛡️

> **Verified Homes. Trusted People. Safe Living.**  
> An AI-powered Android application built with Jetpack Compose for rental discovery, scam detection, roommate compatibility matching, and women's safety insights.

---

## 🌟 Overview

**SafeNest AI** reimagines urban rental living by prioritizing safety, transparency, and compatibility. Built specifically for students and working professionals navigating complex rental markets in major Indian metros (Delhi NCR, Noida, Gurugram, Bengaluru, Mumbai), SafeNest AI integrates Gemini-powered AI models with local offline-first persistence to protect users from rental fraud, help them find verified co-living spaces, and discover compatible roommates.

---

## ✨ Key Features

### 1. 🏠 Verified Rental Discovery & Search
- **Filterable Listings:** Search properties across Delhi NCR by locality, budget, property type (PG, Flat, Co-Living, Independent House), and gender suitability.
- **Pull-To-Refresh Dashboard:** Instantly refresh real-time mock property listings and active booking statuses directly from the home feed.
- **Property Details & Verification Badges:** View detailed amenities, security features (CCTV, 24/7 Security, Biometric Entry), owner verification status, and direct escrow deposit options.

### 2. 🛡️ AI Scam Detection & Fraud Prevention
- **Broker & Listing Analysis:** Paste suspicious broker WhatsApp messages, rental terms, deposit demands, or property location to run real-time AI fraud checks.
- **Risk Score & Red Flags:** Evaluates red flags like upfront token money requests, unverified army/diplomat owner claims, and unrealistic pricing.
- **Preset Test Scenarios:** Built-in scam samples (e.g., Google Pay advance deposit traps vs. verified local PG visits) for quick testing.

### 3. 👥 AI Roommate Compatibility Matching
- **Lifestyle Preference Alignment:** Matches candidates based on budget, work/sleep schedules, cleanliness standards, food/pets preferences, and noise levels.
- **AI Harmony Advice:** Provides tailored AI insights on potential lifestyle differences and recommendations for harmonious living.

### 4. 🚨 Safety Hub & Locality Trust Insights
- **Emergency SOS:** Direct emergency trigger with pre-configured emergency contacts and local authority numbers.
- **Local Services Finder:** Quick directory and map links to nearby police stations, hospitals, and verified women's helpdesks.
- **Locality Trust Score:** Displays neighborhood safety ratings based on police patrolling, metro access, and student density.

### 5. 🔒 Escrow Booking & Admin Dashboard
- **Token Booking:** Reserve properties securely with logged escrow token deposits.
- **Admin Management:** Verification pipeline for approving or reviewing rental listings and tenant identities.

---

## 🛠️ Architecture & Tech Stack

SafeNest AI follows modern Android development best practices and **MVVM / Clean Architecture**:

- **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material Design 3 (M3) edge-to-edge design system.
- **Language:** 100% Kotlin with Coroutines & `StateFlow` for dynamic reactive UI updates.
- **Local Database:** [Room Database](https://developer.android.com/training/data-storage/room) with KSP for offline data persistence (properties, roommates, user profiles, bookings, and scam reports).
- **Networking & AI:** Retrofit, Moshi JSON converter, OkHttp logging interceptor, and Google Gemini API integration for real-time scam and roommate analysis.
- **Navigation:** Jetpack Navigation Compose with type-safe state routing.
- **Testing:** Local JVM testing support with Robolectric & Roborazzi screenshot verification.

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio:** Ladybug or newer recommended
- **JDK:** Java 17+
- **Min SDK:** 26 (Android 8.0 Oreo)
- **Target SDK:** 35 (Android 15)

### Building & Running
1. Clone the repository:
   ```bash
   git clone https://github.com/your-org/safenest-ai.git
   cd safenest-ai
   ```
2. Open the project in Android Studio.
3. Build and run the app on an Android Emulator or physical device:
   ```bash
   ./gradlew assembleDebug
   ```

---

## 👨‍💻 Author

**Annanya Bagga And Ashmeet Kaur**  
BCA Student | Android Developer | AI & Full-Stack Enthusiast

- GitHub: https://github.com/annanyabagga01
- LinkedIn: https://www.linkedin.com/in/annanyabagga24/

---

## 📄 License

This project is created as part of the AI Studio platform application showcase.
