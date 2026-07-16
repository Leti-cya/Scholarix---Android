# Scholarix

<p align="center">
Helping students discover scholarships while giving providers an easy way to manage opportunities.
</p>

---

# About
Scholarix is an Android scholarship management application developed using **Kotlin**, **Jetpack Compose**, and **Firebase**.
The application provides separate experiences for students and scholarship providers. Students can discover scholarship opportunities, browse provider profiles, and manage their applications, while providers can publish and manage scholarship opportunities through their own dashboard.
This project was developed as part of my university coursework and continues to evolve with new features and improvements.

---

# Features
## Student
- Create an account
- Log in securely
- Complete profile
- Edit profile
- Change password
- Browse scholarships
- View scholarship details
- Apply for scholarships
- Look up providers details and their available scholarships

## Scholarship Provider
- Register organization account
- Secure login
- Complete organization profile
- Edit profile
- Change password
- Create scholarships
- Edit scholarships
- Delete scholarships
- Manage posted scholarships

---

# Built With
- Kotlin
- Jetpack Compose
- Material 3
- Firebase (Authentication + Realtime Database)
- MVVM Architecture

---

# Project Structure
```
app/
 ├── model/
 ├── repository/
 ├── ui/
 │    ├── components/
 │    ├── navigation/
 │    ├── screens/
 │    └── theme/
 └── viewmodel/
```

---

# Screens
### Authentication
- Splash Screen
- Login
- Register
- Forgot Password
- Complete Profile

### Student
- Dashboard
- Scholarship Details
- Favorites
- Applications
- Profile
- Edit Profile
- Change Password

### Provider
- Dashboard
- Create Scholarship
- Edit Scholarship
- Scholarship Details
- Applicants
- Profile
- Edit Profile
- Change Password

---

# Getting Started

## Clone the repository
```bash
git clone https://github.com/Leti-cya/Scholarix---Android.git
```

## Open the project
Open the project in Android Studio.

## Firebase
Connect your own Firebase project and update the configuration file if necessary.

## Run
Build and run the application on an emulator or Android device.

---

# Future Improvements
Some features planned for future development include:
- Email verification
- Advanced search and filtering
- Scholarship recommendation system
- Push notifications
- Scholarship deadlines calendar
- Document upload for applications
- In-app messaging
- Scholarship analytics for providers
- Admin panel

---

# Author
Developed by Leticia