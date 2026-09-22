# RailTrack India — Live-ready Android project
Theme: Orange + Black + White.

Live endpoint wired through a backend proxy:
https://api.railradar.in/v1/trains/{number}/live

A RailRadar Bearer API key is required. Keep the key on the backend, never inside the APK.

Steps:
1. Get an API key from the RailRadar developer dashboard.
2. Put it in backend/.env as RAILRADAR_API_KEY.
3. Run/deploy backend.
4. Replace API_BASE_URL in MainActivity.kt with your HTTPS backend URL.
5. Open the project in Android Studio and build the APK.
