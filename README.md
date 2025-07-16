
# 🌤️ Cloudy Weather App

Cloudy Weather App is a sleek, minimalist Android application that provides **real-time current weather updates** based on your location or searched city. Designed with a modern UI, it features cinematic animations and dynamic background visuals to elevate the user experience.

---

## 📱 Features

* 📍 Detects your current location using Fused Location Provider
* 🔍 Search weather by city name
* 🌡️ Displays key current weather data:

  * Temperature (Current, Max, Min)
  * Humidity
  * Wind Speed
  * Atmospheric Pressure
  * Sunrise & Sunset times
* 🎞️ Dynamic background videos based on weather conditions
* 🎨 Smooth Lottie animations for visual appeal
* 🗓️ Displays current date and time

---

## 🚀 Tech Stack

| Technology     | Purpose                             |
| -------------- | ----------------------------------- |
| Kotlin       | Core development (Android)          |
| Retrofit       | Fetches weather data from the API   |
| Gson           | Parses JSON data from the API       |
| Lottie         | Provides animated weather icons     |
| ExoPlayer      | Plays background weather videos     |
| Fused Location | Retrieves current user location     |
| ViewBinding    | Efficient and type-safe view access |

---

## 🔧 Setup Instructions

Follow these steps to set up the project locally:

### 1. Clone the Repository

```bash
git clone https://github.com/muskan464/cloudy-weather-app.git
cd cloudy-weather-app
```

### 2. Add API Configuration

Create or edit your `local.properties` file and add your OpenWeatherMap API details:

```
WEATHER_API_KEY=your_openweathermap_api_key
WEATHER_BASE_URL=https://api.openweathermap.org/data/2.5/
```

> ☑️ **Note**: Access them in build.gradle (App-level)


Edit your app/build.gradle:
```bash
android {
    ...

    defaultConfig {
        ...
        buildConfigField "String", "WEATHER_API_KEY", "\"${project.properties['WEATHER_API_KEY']}\""
        buildConfigField "String", "WEATHER_BASE_URL", "\"${project.properties['WEATHER_BASE_URL']}\""
    }
}
```


### 3. Build and Run

* Open the project in Android Studio
* Allow Gradle to sync and build the project
* Run the app on your device or emulator

---

## 🌐 API Reference

Data is powered by the [OpenWeatherMap API](https://openweathermap.org/api), specifically the **Current Weather Data** endpoint.

---

## 📸 Screenshots

![Weather UI Screenshot](https://github.com/user-attachments/assets/694e546d-c7bc-4b21-a7f9-fa9182e45408)

---

## 👤 Author

**Muskan Jaiswal**
🔗 GitHub: [@muskan464](https://github.com/muskan464)

---

## 💬 Contributions

Feel free to open issues or submit pull requests. Your feedback and ideas are always welcome to improve this project.

---
