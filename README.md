# Wowza Theme Player Issue

A repo illustrating Wowza issue of invisibility when app uses a theme. [Support Ticket link.](https://www.wowza.com/community/questions/53331/android-wowzplayerview-background-settable-by-them.html#comment-53591)

### Steps to replicate
- [Have player point to sample video](https://github.com/seljabali/wowza-themed-player-issue/blob/master/template-android/app/src/main/java/com/seljabali/templateapplication/ui/HomeActivity.kt#L30)
- [Have theme background attribute uncommented thereby applied app wide](https://github.com/seljabali/wowza-themed-player-issue/blob/master/template-android/core/src/main/res/values/themes.xml#L14)

![demo](https://github.com/seljabali/wowza-themed-player-issue/blob/master/Screen%20Shot%202019-11-13%20at%2013.39.38.png)
