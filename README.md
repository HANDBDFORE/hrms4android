本项目依赖以下几个工程，构建时请确保依赖项目同时存在：

+ [ActionBarSherlock][1] 提供ActionBar支持，使Android3.0以下版本也拥有ActionBar功能。
+ [Android-PullToRefresh][2] 提供列表界面下拉刷新功能。
+ [android-menudrawer][3]  提供抽屉视图功能。  
+ [android-async-http][4]  网络请求框架（无需额外下载，源码已包含在本项目中）


另，[new3rdlib][5] 分支，做了如下改进：  

+ 重构了类间的继承依赖关系；
+ 使用了[Support Library V7][6]进行重构，解除了对[ActionBarSherlock][7]和[android-menudrawer][8]的依赖；
+ 使用[Volley][9]替换了原有的网络请求框架，但在Android 3.0以下系统中，cookie同步有问题，暂未解决。





  [1]: https://github.com/HAND-MAS/ActionBarSherlock
  [2]: https://github.com/HAND-MAS/Android-PullToRefresh
  [3]: https://github.com/HAND-MAS/android-menudrawer
  [4]: https://github.com/loopj/android-async-http
  [5]: https://github.com/HAND-MAS/hrms4android/tree/new3rdlib
  [6]: http://developer.android.com/tools/support-library/index.html
  [7]: https://github.com/HAND-MAS/ActionBarSherlock
  [8]: https://github.com/HAND-MAS/android-menudrawer
  [9]: https://android.googlesource.com/platform/frameworks/volley/
