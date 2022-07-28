# Movie Critics
<img src="https://github.com/MIN-JING/MovieCritics/blob/develop/app/src/main/res/drawable/bg_tokusatsu_kaiju.png" height="200" />

:zap: Let movie fans record watched movies, comment, and schedule watching times :zap:

![](https://img.shields.io/badge/build-Kotlin-green) ![](https://img.shields.io/badge/release-v1.0.6-blue) ![](https://img.shields.io/badge/license-Apache_2.0-blue) 

[<img src="https://github.com/MIN-JING/MovieCritics/blob/develop/app/src/main/res/mipmap-xxhdpi/ic_app.png" height="70" /><img src="https://github.com/MIN-JING/MovieCritics/blob/develop/app/src/main/res/drawable/bg_google_play.png" height="70" />](https://play.google.com/store/apps/details?id=com.jim.moviecritics)

## Features
#### Display popular movies
* Be familiar with [`Retrofit`](https://github.com/square/retrofit) for communication with a server and parsed JSON response with [`Moshi`](https://github.com/square/moshi)
* Optimized images download caching and minimized decode times with [`Glide`](https://github.com/bumptech/glide)
#### Rating
* Use [`MPAndroidChart`](https://github.com/PhilJay/MPAndroidChart) for showing ratings
#### Comment
* Updated comments immediately and authenticated users simply with [`Firebase Firestor`](https://firebase.google.com/docs/firestore)
#### Watch trailer
* Used [`WebView`](https://developer.android.com/reference/android/webkit/WebView) to play YouTube videos
#### Share movie information
* Suggested relevant person and application for sharing information through [`Sharesheet`](https://developer.android.com/training/sharing/send)
#### Schedule watching movies
* Scheduled movie watching time by [`WorkManager`](https://developer.android.com/topic/libraries/architecture/workmanager)
* Set [`Notification`](https://developer.android.com/guide/topics/ui/notifiers/notifications) to remind users until the appointed time
* Added movie information and watching time to [`Google Calendar`](https://developer.android.com/guide/topics/providers/calendar-provider) efficiently
#### Search in movies, TVs, and casts
* Achieved searching by [`The Movie Database(TMDB) APIs`](https://developers.themoviedb.org/3/getting-started/introduction)
#### Personal recent activities
* Created better user experience by quickly switching through [`ViewPager2`](https://developer.android.com/training/animation/screen-slide-2) and [`TabLayout`](https://developer.android.com/reference/com/google/android/material/tabs/TabLayout)

## Test Account
E-mail : `moviecritics0728@gmail.com`

Password : `829517`

## Requirement
![](https://img.shields.io/badge/SDK_version-26%2B-orange)
![](https://img.shields.io/badge/Gradle_version-7.2-orange)

## Version History
|    Version    |                    Detail                     |
| ------------- | --------------------------------------------- |
|     1.0.6     |   Add Notifications                           |
|     1.0.5     |   Add block user and report comment features  |

## License
```
Copyright 2022 MIN JING SU

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
## Contact
MIN JING SU : goodcack@gmail.com
