# CLAUDE.md — MovieCritics

## 溝通偏好

- 回覆一律使用**繁體中文**；程式碼、指令、檔名、commit 訊息維持英文。
- 使用者是 Android 工程師（Kotlin），用公司專案的 Compose 重構學習，這個專案是個人作品，也是 Compose 練習場。

## 專案是什麼

電影評論 App：TMDB 熱門電影、搜尋、評分（雷達圖）、評論、觀看清單排程提醒、分享。
資料來源：TMDB API（Retrofit + Moshi）＋ Firebase（Firestore、Auth、Crashlytics）。
Package：`com.jim.moviecritics`，minSdk 26、targetSdk/compileSdk 36。

## 建置

工具鏈：AGP 8.10.0、Kotlin 2.2.0（含 Compose compiler plugin）、KSP、Gradle 8.11.1、JDK 17。

**本機必要檔案（都在 `.gitignore`，不進版控）：**
- `local.properties`
  - `key.api.tmdb="..."`：TMDB API key（注意值要含引號，會直接寫進 `buildConfigField`）
  - `key.store.file`、`key.store.password`、`key.alias`、`key.password`：簽署金鑰。**debug 也用 release 簽署**（為了 Google 登入的 SHA-1），缺少會在 Gradle 設定階段就失敗：`path may not be null or empty string`
- `app/google-services.json`：Firebase 設定。`dev` flavor 的套件名稱是 `com.jim.moviecritics.dev`，檔案裡沒有這個 client 就只能建置 `production`

**Flavor × buildType：** `production` / `dev` × `debug` / `release`。日常開發用 `productionDebug`。

```
./gradlew assembleProductionDebug
./gradlew ktlintCheck
./gradlew testProductionDebugUnitTest
```

目前**沒有 CI**，單元測試只有範本的 `ExampleUnitTest`。改動後至少要在 Android Studio 建置並實機點過受影響的畫面。

**版本號：** `app/build.gradle` 的 `ext { majorVersion / minorVersion / patchVersion }`，versionCode 由 `getVersionCode()` 依 flavor 計算。升版時同步更新 `README.md` 的 Version History。

## 架構

- **單一 Activity**：`MainActivity` + `NavHostFragment` + `BottomNavigationView`，導航是 XML `res/navigation/navigation.xml` + SafeArgs，參數傳 Parcelable（`Movie`、`User`、`Comment`）
- **畫面**：Home、Search、Watchlist、Profile（ViewPager2，內含 Favorite / Guide 分頁）、Detail 五個 Fragment，加上 7 個 DialogFragment（Login、Pending、Review、Report、Follow、Block、Trailer）
- **MVVM**：大多數 ViewModel 用 `LiveData` + DataBinding（`BindingAdapters.kt`）
- **DI**：沒有框架。`util/ServiceLocator` 提供 `Repository`，`factory/` 下 4 個 ViewModelFactory，Fragment 用 `getVmFactory(...)`（`ext/FragmentExt.kt`）取得
- **資料層**：`Repository` → `DefaultRepository` → `ApiDataSource`（TMDB）/ `FirebaseDataSource`（Firestore）/ `LocalDataSource`。結果包成 `data/Result`（`Success` / `Fail` / `Error`）。部分 Firebase 方法仍回傳 `MutableLiveData`
- **詳情頁的 `Movie`**：由 `data/MovieMapper.kt` 的 `buildMovie(detail, credit)` 組成，Home 和 Search 共用
- **Edge-to-edge**：`MainActivity` 只讓 toolbar 和 bottom nav 吃 system bar insets。這兩者被隱藏的畫面（目前是 Detail）要自己用 `util/ViewUtils.kt` 的 `applySystemBarInsets()` 處理

## Compose 遷移現況

做法：一頁一頁換，Fragment 內嵌 `ComposeView`，導航暫時維持 Fragment + XML。

| 畫面 | 狀態 |
|---|---|
| Search | ✅ Compose（`search/`），StateFlow + `collectAsStateWithLifecycle`；導航和 Toast 透過 `LiveData` 由 Fragment 處理 |
| Home | ✅ Compose（`home/`），同 Search 的做法；`LazyVerticalGrid` 兩欄，載入動畫用 `lottie-compose` |
| Detail | 🟡 只有骨架 `detail/DetailScreen.kt`，尚未接上，**刻意保留**作為起點（參數是 `itemId`，實際 Detail 需要 `Movie`；用的是 M2 `TopAppBar`） |
| 其他 | ❌ XML |

- **主題**：Compose 畫面一律包在 `ui/theme/Theme.kt` 的 `MovieCriticsTheme`，不要用裸的 `MaterialTheme`。顏色用 `colorResource` 讀 `colors.xml`，和 XML 主題共用一份
- 深色模式刻意維持**淺色背景 + 深色系 teal**，對應 `values-night` 和寫死白色的 toolbar / bottom nav
- 字型維持 Material 3 預設：`res/font/roboto.ttf` 只有 Regular，套用會讓 Medium 字重失效

## 已知陷阱

- `ApiDataSource` 會 catch 所有 `Exception`，**包含 `CancellationException`**。在可取消的 coroutine 裡呼叫 repository 後，要先 `ensureActive()` 再更新狀態（見 `SearchViewModel`）
- TMDB 的金額欄位（`revenue`、`budget`）可能超過 `Int`，要用 `Long`
- TMDB multi search 的 TV 和人物用 `name`，電影用 `title`
- Android 字串資源裡的 `"` 要寫成 `\"`，否則會被 aapt 去掉
- 觀看提醒用 WorkManager `setInitialDelay`，不是精確鬧鐘，Doze 下可能延後；Android 13 以上在點清單項目時才要 `POST_NOTIFICATIONS`，拒絕的話 `WatchlistReminderWorker` 會略過通知

## 開發流程

- 主要開發分支是 **`develop`**；`main` 是發布用，目前落後 `develop`
- 每個改動從最新的 `develop` 開分支，PR 合併回 `develop`，一個 PR 只做一件事
- Commit 和 PR 用英文；PR 描述寫清楚改了什麼、為什麼、怎麼驗證
- 使用者會在本機 Android Studio 建置並實機測試後才合併

## 待辦

- Compose 改寫下一個畫面（Detail）
- Search 的 query 仍以 `TextFieldValue` 放在 StateFlow，官方建議改用 Compose state
- 用 Material Theme Builder 從 `#006A6A` 產生完整 teal 色票（目前中性色是 Material 預設，帶紫調）
- 清理沒用到的 value 資源：登入範本字串、`purple_*` / `teal_*` 顏色、幾個 dimen、`Theme.MovieCritics`
- 發布 1.1.7：`develop` 合併到 `main`
- 參考：`docs/search-page-proposal.md`（搜尋頁的產品規劃）
