仿微信的大图浏览器，可对加载中，加载失败，大图蒙层进行自定义。

代码基本上都是[XPopup](https://github.com/li-xiaojun/XPopup)中的图片浏览部分进行的抽离与修改，将之抽出来，进行了一点修改，更加自由灵活一点。
主要代码用Kotlin改写了一下，实现效果基本上与该库一致。

# 使用方式：

## 将JitPack存储库添加到构建文件(项目根目录下build.gradle文件)

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

## 添加依赖项

```gradle
    implementation 'com.github.XuQK:KDImageViewer:1.0.0'

    // 如果项目中没有以下两个库，需要额外添加
    implementation 'androidx.appcompat:appcompat:versionCode'
    implementation 'androidx.transition:transition:versionCode'
```
