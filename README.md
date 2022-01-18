# playground-2022
### 안드로이드 개발 2022년

#### This project uses the following stack:

- Kotlin
    - Kotlin (kotlin-android)
    - dataBinding layout
- Android architecture components
    - Koin for DI
    - MVVM pattern
- Network
    - Retrofit2
    - Okhttp3
- AndroidX components
    - Constraint layout
    - Material layout
    - Recyclerview
- Image Processing
    - Glide
- Debugging
    - Stetho for Okhttp and db debugging
    - Flipper for Okhttp and db debugging
- Logging
    - Timber
- CI/CD
    - Github Actions
- Coding Style
    - Kotlin Coding Conventions
    - https://velog.io/@lsb156/IntelliJ-Kotlin-Code-Style-%EC%84%A4%EC%A0%95%EB%B2%95

![structure](https://user-images.githubusercontent.com/12586065/149857108-9c15a05b-3e8c-4077-8d4e-667ee605e98c.png)


## Previous

You have to apply API-KEY first.

1. Open local.properties in your project.
2. https://developer.marvel.com/ Get an API-KEY from the site
3. Finally, Insert a value corresponding to the following API-KEY

in `local.properties`
```
marvel_private_key = "marvel_private_key"
marvel_public_key = "marvel_public_key"
```

## Feature

### 1. Search
- Requirements
    - [x] 300 millis threshold
    - [x] Recent searches feature
    - [x] Scroll save be reused

### 2. ViewPager
- Requirements
    - [x] LayoutManager with two types ( Grid,Stagged )
    
### 3. RecyclerView
- Requirements
    - [x] Select save be resused
    - [x] Single slected
    - [x] Pagination
    - [x] Pull to Refresh

### 4. TestCode
- Requirements
    - [x] Start unit test code
    - [ ] Code coverage over 50%

### 5. ZoomEffectLayout

![ezgif com-video-to-gif-2](https://user-images.githubusercontent.com/12586065/149887467-b7b30beb-7b7d-4707-a3b6-9a94d8e4c6eb.gif)


## DEMO VIDEO

https://user-images.githubusercontent.com/12586065/149955385-0639c1f9-7827-4c1e-ace0-31c5b087c7f9.mp4



