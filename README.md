<h1 align="center">
  Android 启动任务调度库
</h1>

<p align="center">
  <a href="http://www.apache.org/licenses/LICENSE-2.0">
    <img src="https://img.shields.io/hexpm/l/plug.svg" alt="License" />
  </a>
  <img src="https://img.shields.io/maven-metadata/v/https/s01.oss.sonatype.org/service/local/repo_groups/public/content/com/github/Shouheng88/startup/maven-metadata.xml.svg" alt="Version" />
  <a href="https://www.codacy.com/gh/Shouheng88/AndroidStartup/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=Shouheng88/AndroidStartup&amp;utm_campaign=Badge_Grade">
    <img src="https://app.codacy.com/project/badge/Grade/99d198d3eb2446bd864946f35c13bcaa" alt="Code Grade"/>
  </a>
  <a href="https://travis-ci.org/Shouheng88/AndroidStartup">
    <img src="https://travis-ci.org/Shouheng88/AndroidStartup.svg?branch=master" alt="Build"/>
  </a>
    <a href="https://developer.android.com/about/versions/android-4.2.html">
    <img src="https://img.shields.io/badge/API-17%2B-blue.svg?style=flat-square" alt="Min Sdk Version" />
  </a>
   <a href="https://github.com/Shouheng88">
    <img src="https://img.shields.io/badge/Author-Shouheng-orange.svg?style=flat-square" alt="Author" />
  </a>
  <a target="_blank" href="https://shang.qq.com/wpa/qunwpa?idkey=2711a5fa2e3ecfbaae34bd2cf2c98a5b25dd7d5cc56a3928abee84ae7a984253">
    <img src="https://img.shields.io/badge/QQ%E7%BE%A4-1018235573-orange.svg?style=flat-square" alt="QQ Group" />
  </a>
</P>

## 简介

这里我只介绍下经过新的版本迭代之后该项目与其他项目的不同点。对于其基础的实现原理，可以参考我之前的文章 
- [《异步、非阻塞式 Android 启动任务调度库》](https://www.fullstack.fan/posts/3c9957f0/)
- [《更高级的 Android 启动任务调度库》](https://www.fullstack.fan/posts/10196514/)

## 优势

### 1. 支持多种线程模型

这是相对于 Jetpack 的启动任务库的优势，在指定任务的时候，你可以通过 `ISchedulerJob` 的 `threadMode()` 方法指定该任务执行的线程，当前支持主线程（`ThreadMode.MAIN`）和非主线程（`ThreadMode.BACKGROUND`）两种情况。前者在主线程当中执行，后者在线程池当中执行，同时，该库还允许你自定义自己的线程池。关于这块的实现原理可以参考之前的文章或者项目源码。

### 2. 非阻塞的任务调度方式

在之前的文章中也提到了，如果说采用 CountDownLatch 等阻塞的方式来实现任务调度，虽然不会占用主线程的 CPU，但是子线程会被阻塞，一样会导致 CPU 空转，影响程序执行的性能，尤其启动的时候大量任务执行时的情况。所以，在这个库的设计中，我们使用了通知唤醒的方式进行任务调度。也就是，

### 3. 非 Class 的依赖方式

之前在本项目中，以及其他的项目中可能采用了基于 Class 的形式进行任务依赖。这种使用方式存在一些问题，即在组件化开发的时候，Class 之间需要直接进行引用。这导致各个组件之间的强耦合。这显然不是我们希望的。

所以，为了更好地支持组件化，在该库的新版本中，我们允许通过 `name()` 方法执行任务的名称，以及通过 ` dependencies()` 方法指定该任务依赖的其他任务的名称。`name()` 默认使用任务 Class 的全限定名。这样，当多个组件之间进行相互依赖的时候，只需要通过字符串指定名称而无需引用具体的类。

### 4. 支持任务的优先级

在实际开发中，我们可能会遇到需要为所有的根任务或者一个任务的所有的子任务指定执行的先后顺序的场景。或者在组件化中，存在依赖关系，但是我们希望某个根任务优先执行，但是不想为每个子任务都执行依赖关系的时候，我们可以通过指定这个任务的优先级为最高来使其最先被执行。你可以通过 `priority()` 方法传递一个 0 到 100 的整数来指定任务的优先级。

*优先级局限于依赖关系相同的任务，所以是依赖关系的补充，不会造成歧义。*

### 5. 支持指定任务执行的进程，可自定义进程匹配策略

如果我们的项目支持多进程，而我们希望某些启动任务只在某个进程中执行而其他进程不需要执行，以此避免没必要的任务来提升任务执行的性能的时候，我们可以通过指定任务执行的进程来进行优化。你可以通过 `targetProcesses()` 传递一个进程的列表来指定该任务执行的所有进程。默认列表为空，表示运行在所有的进程。

### 6. 支持注解形式的组件化调用

在之前的版本中，通过 ContentProvider 的形式我们一样可以实现所有组件内任务的收集和调用。但是使用 ContentProvider 存在一些不便之处，比如 ContentProvider 的初始化实际在 Application 的 `attachBaseContext()`，如果我们的任务中一些操作需要放到 Application 的 `onCreate()` 中执行的时候，通过 ContentProvider 默认装载任务的调度方式就存在问题。而通过基于**注解 + APT**的形式，我们可以随意指定任务收集、整理和执行的时机，灵活性更好。

## 使用

### 1. 添加依赖

添加 MavenCentral,

```groovy
repositories { mavenCentral() }
```

添加启动器依赖

```groovy
implementation "com.github.Shouheng88:startup:$latest-version"
```

如果需要使用基于注解 `@StartupJob` 的组件化任务调度，你需要添加如下依赖，

```groovy
kapt "com.github.Shouheng88:startup-compiler:$latest-version"
```

然后，在每个组件的 gradle.build 脚本中添加如下代码，

```groovy
javaCompileOptions {
    annotationProcessorOptions {
        arguments = [STARTUP_MODULE_NAME: project.getName()]
    }
}
```

如果你只想使用任务调度器，只使用如下依赖即可，

```groovy
implementation "com.github.Shouheng88:scheduler:$latest-version"
```

### 2. 定义任务

让你的任务类实现 ISchedulerJob 接口即可，

```kotlin
@StartupJob class BlockingBackgroundJob : ISchedulerJob {

    override fun name(): String = "blocking"

    override fun threadMode(): ThreadMode = ThreadMode.BACKGROUND

    override fun dependencies(): List<String> = emptyList()

    override fun run(context: Context) {
        Thread.sleep(5_000L) // 5 seconds
        L.d("BlockingBackgroundJob done! ${Thread.currentThread()}")
        toast("BlockingBackgroundJob done!")
    }
}
```

### 3. 启动：通过 ContentProvider 启动任务

示例代码如下，在每个组件内定义 ContentProvider 一样可以实现组件化，但是如文章所述，有一些限制，因此建议采用基于注解的组件化，

```xml
<provider
   android:authorities="${applicationId}.androidx-startup"
   android:exported="false"
   tools:node="merge"
   android:name="me.shouheng.startup.AndroidStartupProvider">
   <meta-data android:name="me.shouheng.startupsample.jobs.BlockingBackgroundJob"
         android:value="android.startup" />
   <meta-data android:name="me.shouheng.startupsample.jobs.CrashHelperInitializeJob"
         android:value="android.startup" />
   <meta-data android:name="me.shouheng.startupsample.jobs.DependentBlockingBackgroundJob"
         android:value="android.startup" />
   <meta-data android:name="me.shouheng.startupsample.jobs.ThirdPartLibrariesInitializeJob"
         android:value="android.startup" />
</provider>
```

### 4. 启动：手动指定任务

示例代码，

```kotlin
AndroidStartup.newInstance(this).jobs(
   CrashHelperInitializeJob(),
   ThirdPartLibrariesInitializeJob(),
   DependentBlockingBackgroundJob(),
   BlockingBackgroundJob()
).launch()
```

### 5. 启动：基于注解 @StartupJob 的组件化调用

首先为每个任务添加注解，

```kotlin
@StartupJob class BlockingBackgroundJob : ISchedulerJob {

    override fun name(): String = "blocking"

    override fun threadMode(): ThreadMode = ThreadMode.BACKGROUND

    override fun dependencies(): List<String> = emptyList()

    override fun run(context: Context) {
        Thread.sleep(5_000L) // 5 seconds
        L.d("BlockingBackgroundJob done! ${Thread.currentThread()}")
        toast("BlockingBackgroundJob done!")
    }
}
```

然后，在你希望初始化的地方调用下面代码即可启动任务，

```kotlin
launchStartup(this) {
    scanAnnotations()
}
```

## License

```
Copyright (c) 2021 ShouhengWang.

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

