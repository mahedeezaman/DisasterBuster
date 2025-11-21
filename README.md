# DisasterBuster

**DisasterBuster** is an Android app that provides information about natural disasters and emergency events worldwide. Users can view disaster events on a map, tap markers to see brief titles, and open detailed WebViews for more information. The app fetches disaster data via the **EONET API** (NASA’s Earth Observatory Natural Event Tracker).  

- **API Endpoint:** `https://eonet.gsfc.nasa.gov/api/v3/events`  
- **Documentation:** [EONET API Docs](https://eonet.gsfc.nasa.gov/docs/v3)

---

## Features & Constraints

- Displays disaster events on a Google Map with markers.  
- Clicking a marker shows the event title; clicking the title opens a WebView with detailed information.  
- Icons for each event type are loaded from URLs; cached locally to improve performance.  
- Placeholder icons are used while the actual icon is downloading.  
- Supports Android phones (minimum SDK 29).  

---

## Requirements to Run the Project

1. Clone the repository.  
2. Open the project in **Android Studio**.  
3. Make sure your device has **Google Play Services** for Maps.  
4. Build and run the project.  

---

## Technologies Used

- **Kotlin** for all app development.  
- **MVVM architecture** for clear separation of concerns.  
- **Google Maps SDK** for displaying disaster events.  
- **Retrofit + Coroutines** for asynchronous API calls.  
- **Glide** for image loading and caching of icons.  
- **ViewModel + LiveData / Observer pattern** for updating the UI.  

---

## Architecture

The app follows **MVVM architecture** and **SOLID principles** for maintainable, scalable code.

### Model

- Represents disaster events.  
- Responsible for communicating with **ServiceManager** to fetch API data.  

### View

- Shows a Google Map with disaster markers.  
- Handles marker clicks and opening WebViews.  
- Displays placeholder icons while real icons are being downloaded.  

### ViewModel

- Contains business logic.  
- Observes disaster data and updates the View.  
- Handles icon caching:  
  1. If the icon is already cached, it is used immediately.  
  2. Otherwise, a placeholder is shown, and the icon is downloaded silently.  

### ServiceManager

- Coordinates **NetworkManager** (Retrofit) for API requests.  
- Handles caching of downloaded icons to storage.  

---

## App Workflow Overview

1. On app launch, **ViewModel requests disaster data** from the Model.  
2. Model fetches the data **directly from the API**.  
3. For each event:  
   - If the icon URL has been cached locally, it is displayed immediately.  
   - Otherwise, a placeholder icon is shown, and the icon is downloaded silently. Once downloaded, the icon updates automatically.  
4. Users can tap markers to see event titles and open a WebView with details.  

---

## Design Choices & Learning Outcomes

- Focused on **Android best practices**, including MVVM, LiveData, and Coroutines.  
- Used **Retrofit + Coroutines** for clean asynchronous network calls.  
- Used **Glide** for efficient image loading and caching.  
- Maintained **SOLID architecture** and separation of concerns.  
- Built for **extensibility**, allowing new features to be added without breaking existing functionality.  

---

## Supported Devices

- Android phones only.  
- Minimum SDK: 29 (Android 10).  
- Google Play Services required for Maps.  
