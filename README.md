# Movie Critics
<img src="https://github.com/MIN-JING/MIN-JING/blob/main/image_MovieCritics/bg_tokusatsu_kaiju.png" height="200" alt="MovieCritics App banner" />

:zap: Let movie fans record watched movies, comment, and schedule watching times :zap:

![](https://img.shields.io/badge/build-Kotlin-green) ![](https://img.shields.io/badge/release-v1.1.2-blue) ![](https://img.shields.io/badge/license-Apache_2.0-blue)  ![](https://img.shields.io/badge/SDK_version-26%2B-orange) ![](https://img.shields.io/badge/Gradle_version-7.2-orange) 

[<img src="https://github.com/MIN-JING/MovieCritics/blob/develop/app/src/main/res/mipmap-xxhdpi/ic_app.png" height="70" /><img src="https://github.com/MIN-JING/MIN-JING/blob/main/image_MovieCritics/bg_google_play.png" height="70" />](https://play.google.com/store/apps/details?id=com.jim.moviecritics)

## Test Account
E-mail : `moviecritics0728@gmail.com`

Password : `yd829517`

## Attribution
<img src="https://www.themoviedb.org/assets/2/v4/logos/v2/blue_long_2-9665a76b1ae401a510ec1e0ca40ddcb3b0cfe45f1d51b77a308fea0845885648.svg" width="300" alt="The Movie Database (TMDB) logo" />

The Movie Critics application that uses data is attributed to [The Movie Database (TMDB)](https://developers.themoviedb.org/3/getting-started/introduction) as the source

## Features
#### Display popular movies
* Be familiar with [`Retrofit`](https://github.com/square/retrofit) for communication with a server and parsed JSON response with [`Moshi`](https://github.com/square/moshi)
* Optimized images download caching and minimized decode times with [`Glide`](https://github.com/bumptech/glide)
<img src="https://github.com/MIN-JING/MIN-JING/blob/main/image_MovieCritics/screen_popular_movies.gif" width="170" />

#### Rating
* Use [`MPAndroidChart`](https://github.com/PhilJay/MPAndroidChart) for showing ratings
<img src="https://github.com/MIN-JING/MIN-JING/blob/main/image_MovieCritics/screen_rating.gif" width="170" />

#### Comment
* Updated comments immediately and authenticated users simply with [`Firebase Firestore`](https://firebase.google.com/docs/firestore)
<img src="https://github.com/MIN-JING/MIN-JING/blob/main/image_MovieCritics/screen_comment.gif" width="170" />

#### Watch trailer
* Used [`WebView`](https://developer.android.com/reference/android/webkit/WebView) to play YouTube trailers
<img src="https://github.com/MIN-JING/MIN-JING/blob/main/image_MovieCritics/screen_webview.gif" width="170" />
                                                                                                          
#### Share movie information
* Suggested relevant person and application for sharing information through [`Sharesheet`](https://developer.android.com/training/sharing/send)
<img src="https://github.com/MIN-JING/MIN-JING/blob/main/image_MovieCritics/screen_share.gif" width="170" />

#### Schedule watching movies
* Scheduled movie watching time by [`WorkManager`](https://developer.android.com/topic/libraries/architecture/workmanager)
* Set [`Notification`](https://developer.android.com/guide/topics/ui/notifiers/notifications) to remind users until the appointed time
* Added movie information and watching time to [`Google Calendar`](https://developer.android.com/guide/topics/providers/calendar-provider) efficiently
<img src="https://github.com/MIN-JING/MIN-JING/blob/main/image_MovieCritics/screen_schedule.gif" width="170" />

#### Search in movies, TVs, and casts
* Achieved searching by [`The Movie Database (TMDB) APIs`](https://developers.themoviedb.org/3/getting-started/introduction)
<img src="https://github.com/MIN-JING/MIN-JING/blob/main/image_MovieCritics/screen_search.gif" width="170" />

#### Personal recent activities
* Created better user experience by quickly switching through [`ViewPager2`](https://developer.android.com/training/animation/screen-slide-2) and [`TabLayout`](https://developer.android.com/reference/com/google/android/material/tabs/TabLayout)
<img src="https://github.com/MIN-JING/MIN-JING/blob/main/image_MovieCritics/screen_viewpager2.gif" width="170" />

## Installation
1. GitHub Clone or Download ZIP, and import it as a project into [Android Studio](https://developer.android.com/studio)
2. Sign up for The Movie Database (TMDB) member and request an API key from the [setting page](https://www.themoviedb.org/settings/api). Here is an [instructional video](https://www.youtube.com/watch?v=mO3gvkiLkio)
3. Once you have it open `MovieCritics/local.properties` file in the **Project** folder tree and add your own API key in line 9 `key.api.tmdb="YOUR_TMDB_API_KEY_STRING"`
```
## This file must *NOT* be checked into Version Control Systems,
# as it contains information specific to your local configuration.
#
# Location of the SDK. This is only used by Gradle.
# For customization when using a Version Control System, please read the
# header note.
#Mon Jul 04 23:38:49 CST 2022
sdk.dir=C\:\\Users\\goodc\\AppData\\Local\\Android\\Sdk
key.api.tmdb="YOUR_TMDB_API_KEY_STRING"
```

## Requirement
SDK version 26+

Gradle version 7.2

## Version History
| Version | Detail                                                                        |
|---------|-------------------------------------------------------------------------------|
| 1.1.5   | Prevent passing empty Firestore document ID on the watchlist page             |
| 1.1.4   | Add click event on search page                                                |
| 1.1.3   | Refactor the search page by Jetpack Compose; Upgrade libraries version        |
| 1.1.2   | Modify radar chart UI, refactor rating page                                   |
| 1.1.1   | Refactor radar chart                                                          |
| 1.1.0   | Modify colors in dark mode                                                    |
| 1.0.9   | Fix login flow                                                                |
| 1.0.8   | Fix login flow                                                                |
| 1.0.7   | Add WebView for YouTube trailer, movie info sharing features, and refactoring |
| 1.0.6   | Add Notifications                                                             |
| 1.0.5   | Add blocking user and reporting comment features                              |

## License
```
Copyright 2022 MINJING SU

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
## Contact
MINJING SU : goodcack@gmail.com
