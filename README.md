<h1 align="center">Little Music - Kotlin</h1>

<p align="center">
 <a><img alt="Min SDK" src="https://img.shields.io/badge/Min SDK-24-020290?logo=android&logoColor=white"/></a>
 <a><img alt="Target SDK" src="https://img.shields.io/badge/Target SDK-37-0EB265?logo=android&logoColor=0EB265"/></a>
 <a href="https://kotlinlang.org"><img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-2.4.20-blue?logo=kotlin&logoColor=white"/></a>
 <a href="https://opensource.org/licenses/MIT"><img alt="License" src="https://img.shields.io/badge/License-MIT-CC9900?logo=MIT&logoColor=white"/></a>
</p>

## Day Mode
Splash | Welcome | Login 
--- | --- | --- | 
![](UI/1.jpg) | ![](UI/2.jpg) | ![](UI/3.jpg) | 

Register | Forget Pass  | Home 
--- | --- | --- | 
![](UI/4.jpg) | ![](UI/5.jpg) | ![](UI/6.jpg) | 

Favorites Music | Categories | My Profile 
--- | --- | --- | 
![](UI/7.jpg) | ![](UI/8.jpg) | ![](UI/9.jpg) | 

Edit Profile | Category Albums | Category Songs 
--- | --- | --- | 
![](UI/10.jpg) | ![](UI/11.jpg) | ![](UI/12.jpg) | 

Editors Choice | Most Views | New Songs 
--- | --- | --- | 
![](UI/13.jpg) | ![](UI/14.jpg) | ![](UI/15.jpg) | 

My Albums | My Artists | My Categories
--- | --- | --- | 
![](UI/16.jpg) | ![](UI/17.jpg) | ![](UI/18.jpg) | 

Favorites | Search | About App 
--- | --- | --- | 
![](UI/19.jpg) | ![](UI/20.jpg) | ![](UI/21.jpg) | 

Logout | Share App | Privacy Policy 
--- | --- | --- | 
![](UI/22.jpg) | ![](UI/23.jpg) | ![](UI/24.jpg) | 

## Night Mode
Splash | Welcome | Login 
--- | --- | --- | 
![](UI/25.jpg) | ![](UI/26.jpg) | ![](UI/27.jpg) | 

Register | Forget Pass  | Home 
--- | --- | --- | 
![](UI/28.jpg) | ![](UI/29.jpg) | ![](UI/30.jpg) | 

Favorites Music | Categories | My Profile 
--- | --- | --- | 
![](UI/31.jpg) | ![](UI/32.jpg) | ![](UI/33.jpg) | 

Edit Profile | Category Albums | Category Songs 
--- | --- | --- | 
![](UI/34.jpg) | ![](UI/35.jpg) | ![](UI/36.jpg) | 

Editors Choice | Most Views | New Songs 
--- | --- | --- | 
![](UI/37.jpg) | ![](UI/38.jpg) | ![](UI/39.jpg) |

My Albums | My Artists | My Categories
--- | --- | --- | 
![](UI/40.jpg) | ![](UI/41.jpg) | ![](UI/42.jpg) | 

Favorites | Search | About App 
--- | --- | --- | 
![](UI/43.jpg) | ![](UI/44.jpg) | ![](UI/45.jpg) | 

Logout | Share App | Privacy Policy 
--- | --- | --- | 
![](UI/46.jpg) | ![](UI/47.jpg) | ![](UI/48.jpg) | 

---

### ✨ Core Functionalities
*   **Robust Authentication Flow**: Secure identity management via **Firebase Authentication**.
*   **Seamless Music Streaming**: Play songs, albums, and explore artist discographies.
*   **Personalized Playlists & Favorites**: Create custom music collections and bookmark favorite tracks.
*   **Context-Aware Theming**: Adaptive UI support for dark and light modes.
*   **Resilient Offline Capabilities**: "Local-first" data strategy using **Room Database**.
*   **Elastic Cloud Synchronization**: Distributed data persistence via **Firebase**.

---

## 🛠️ Built With
*   **Language:** [Kotlin](https://kotlinlang.org/)
*   **UI Framework:** Material Design 3, ViewBinding
*   **Database:** [Room](https://developer.android.com/training/data-storage/room)
*   **Cloud Infrastructure:** [Firebase](https://firebase.google.com/) (Auth, Realtime Database)
*   **Dependency Injection:** [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
*   **Async Operations:** Coroutines & Flow

---

### 🏗️ Technical Architecture

#### User App
```text
app/src/main/java/com/flatcode/littlemusic/
├── db/                 # Room Database Configuration & DAOs
├── di/                 # Dependency Injection (Hilt modules)
├── model/              # Data Entities & Models
├── repository/         # Repository Pattern Implementation
├── ui/                 # Presentation Layer
│   ├── album/          # Albums & Track Lists
│   ├── artist/         # Artists Profiles & Discography
│   ├── auth/           # Authentication (Login, Register)
│   ├── category/       # Category Songs & Genres
│   ├── favorites/      # Liked Songs & Favorites
│   ├── main/           # Main Music Player & Dashboard Navigation
│   ├── profile/        # User Profile Settings
│   ├── settings/       # App Settings & Privacy Policy
│   ├── showmore/       # Curated Selections & Most Viewed Tracks
│   └── song/           # Audio Player Screen & Song Details
└── utils/              # Extensions, Constants & Utility Classes
```

#### Admin App
```text
app/src/main/java/com/flatcode/littlemusicadmin/
├── di/                 # Dependency Injection (Hilt modules)
├── model/              # Data Entities & Models
├── repository/         # Repository Pattern Implementation
├── ui/                 # Admin Presentation Layer
│   ├── album/          # Album Creation & Editing
│   ├── artist/         # Artist Profile Management
│   ├── auth/           # Admin Authentication
│   ├── category/       # Category & Genre Management
│   ├── editorschoice/  # Editor's Choice Picks
│   ├── main/           # Admin Dashboard
│   ├── others/         # Additional Admin Tools
│   ├── profile/        # Admin Profile Settings
│   ├── song/           # Song Uploads, Audio Links & Metadata
│   └── user/           # User Management
└── utils/              # Extensions, Constants & Utility Classes
```

---

## 🚀 Getting Started

### Prerequisites
*   Android Studio Ladybug or newer.
*   JDK 21.
*   Android SDK Level 37 (Compile SDK).

### Installation
1.  Clone the repository:
    ```bash
    git clone https://github.com/selimdawa/LittleMusicKotlin.git
    ```
2.  Open the project in Android Studio.
3.  Sync Project with Gradle Files.
4.  Add your `google-services.json` file to the `app/` directory.
5.  Run the app on your device or emulator.

---

### 🛠️ Technology Stack
*   **Language**: Kotlin (Modern, expressive, and safe development).
*   **Dependency Injection**: **Dagger Hilt** (Simplified, standard DI for Android).
*   **Cloud Infrastructure**: **Firebase** (Scalable authentication and real-time NoSQL storage).
*   **Local Persistence**: **Room** (SQLite abstraction layer for robust offline data).
*   **Concurrency**: **Coroutines & Flow** (High-performance asynchronous data streams).
*   **View Interop**: **ViewBinding** (Safe and efficient UI component interaction).

---

## 🔗 Links & Resources
*   **Legacy Versions:**
    *   [Kotlin Old Code Version](https://github.com/selimdawa/LittleMusicKotlinOld/)
    *   [Java Old Code Version](https://github.com/selimdawa/LittleMusic/)
*   **Author:** [Selim Dawa](https://github.com/selimdawa)

---

## 📜 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
