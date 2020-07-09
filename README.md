[![](https://jitpack.io/v/XuQK/KDImageViewer.svg)](https://jitpack.io/#XuQK/KDImageViewer)

![](demo.gif)

仿微信的大图浏览器，可对加载中，加载失败，大图蒙层进行自定义。

代码基本上都是[XPopup](https://github.com/li-xiaojun/XPopup)中的图片浏览部分进行的抽离与修改，将之抽出来，进行了一点修改，对缩略图和原图的关系做了一些处理。

主要代码用Kotlin改写了一下，实现效果基本上与该库一致。

大图可以添加到任意ViewGroup，而不仅仅是全屏效果，如果单纯的想要全屏，show

进行了DSL改造，可以用DSL方式调用。


# 使用方式：

## 将JitPack存储库添加到构建文件(项目根目录下build.gradle文件)

```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

## 添加依赖项

```groovy
// 版本号参看Release
implementation 'com.github.XuQK:KDImageViewer:versionCode'

// 项目依赖于以下库，如果没有需要在主工程中添加
implementation 'androidx.appcompat:appcompat:versionCode'
implementation 'androidx.transition:transition:versionCode'
```

## 使用方式

```kotlin
// 初始化
val viewer = kdImageViewer {
    context = this@MainActivity
    imageLoader = KDImageViewLoader
    animDuration = 400
    coverModule = MyCoverModule(this@MainActivity)
    onShowAnimateStart = {
        Log.d("标签", "显示动画开始")
    }
    onShowAnimateEnd = {
        Log.d("标签", "显示动画结束")
    }
    onDismissAnimateStart = {
        Log.d("标签", "消失动画开始")
    }
    onDismissAnimateEnd = {
        Log.d("标签", "消失动画结束")
    }
}

// 想达到完美的大小图切换效果，需要设置 srcImageFetcher
viewer.srcImageViewFetcher = { position ->
    // 这里返回 position 位置的缩略图ImageView
}

// 设置完毕后调用show
viewer.show(viewGroupToCover, originImageUrlList, currentImageViewPosition)

// 记得必要的时候重写返回键
override fun onBackPressed() {
    if (viewer.handleBackPressed()) return
    super.onBackPressed()
}
```


# Release Note

### 1.2.0

DSL化改造，不想使用DSL方式的，请使用1.1.2版本
