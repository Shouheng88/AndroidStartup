# AndroidStartup

<p align="center">
  <a href="http://www.apache.org/licenses/LICENSE-2.0">
    <img src="https://img.shields.io/hexpm/l/plug.svg" alt="License" />
  </a>
  <a href="https://bintray.com/beta/#/easymark/Android/vmlib-core?tab=overview">
    <img src="https://img.shields.io/maven-metadata/v/https/s01.oss.sonatype.org/service/local/repo_groups/public/content/com/github/Shouheng88/startup/maven-metadata.xml.svg" alt="Version" />
  </a>
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

## Introduction

AndroidStartup is an open source project used to refine your Andriod App startup. Compared with Jetpack Startup, this project can used for **ASYNC** circumstance. As we konw, in most cases, to accelerate the App startup, we may run our jobs in background threads. You are allow to use background and main thread jobs and specify their dependencies in AndroidStarup. The AndroidStartup could handle their relations and run jobs by their dependencies.

## Setup

Add MavenCentral,

```groovy
repositories { mavenCentral() }
```

Add the dependency to use startup in your project,

```groovy
implementation "com.github.Shouheng88:startup:$latest-version"
```

If you want to use the `@StartupJob` annotation to define the job, append the below dependenc,

```groovy
kapt "com.github.Shouheng88:startup-compiler:$latest-version"
```

If you want just use the scheduler of startup, you can just use the scheduler by,

```groovy
implementation "com.github.Shouheng88:scheduler:$latest-version"
```

## Initialize at Android Startup

You have multiple ways to use AndroidStartup.

### Implement ISchedulerJob to deinfe your job

The `ISchedulerJob` interface is used to define the job in scheduler. You have to implement its three methods,

- `threadMode()` to specify the thread job that the task will run
- `dependencies()` dependeny jobs the current job relies on
- `run()` the business for the job

### Set up manifest entries

Android Startup includes a special content provider called `AndroidStartupProvider` that it uses to discover and call your scheduler jobs. Android Startup discovers jobs by first checking for a `<meta-data>` entry under the `AndroidStartupProvider` manifest entry. 

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

The `tools:node="merge"` attribute ensures that the manifest merger tool properly resolves any conflicting entries.

### Manually initialize jobs

The Android Startup also allows you to initialize Startup by directly calling `AndroidStartup` builder. You can specify jobs by its `jobs()` method and then call `launch()` to start these jobs.

```kotlin
AndroidStartup.newInstance(this).jobs(
   CrashHelperInitializeJob(),
   ThirdPartLibrariesInitializeJob(),
   DependentBlockingBackgroundJob(),
   BlockingBackgroundJob()
).launch()
```

### Use @StartupJob annotation to initialize jobs

You can alswo use `@StartupJob` to define jobs and then call `scanAnnotations()` method of `AndroidStartup` to scan jobs with `@StartupJob` annotation.

```kotlin
AndroidStartup.newInstance(this).scanAnnotations().launch()
```

To use the annotation driver you will have to add the `kotlin-kapt` plugin and the startup-compiler.

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

