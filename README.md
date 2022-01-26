# playground-2022
### 안드로이드 개발 2022년 [![Hits](https://hits.seeyoufarm.com/api/count/incr/badge.svg?url=https%3A%2F%2Fgithub.com%2FJungJongSeok%2FAndroid-Playground-2022&count_bg=%2379C83D&title_bg=%23555555&icon=&icon_color=%23E7E7E7&title=hits&edge_flat=false)](https://hits.seeyoufarm.com)


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
    - [x] LayoutManager with two types ( Grid,Staggered )
    
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

## Target 대응

### 1. Android 10 target 29
 - [x] Dark mode
 - [ ] Foldables layout


### 1. Android 11 target 30
 - [ ] Scoped storage enforcement


### 1. Android 12 target 31
 - [x] SplashScreen
 - [x] android:exported 명시 
 - [ ] Custom notification 



## DEMO VIDEO

https://user-images.githubusercontent.com/12586065/150128510-d634f2fa-e5c8-4363-ac15-93cac1a02813.mp4


