蘑菇饭项目
===========================
一个简洁的饭否App，支持Android 4.0以上版本

##下载地址

[Google Play](https://play.google.com/store/apps/details?id=com.mcxiaoke.minicat)  
[直接下载](release/app-1.2.8.apk)

##使用说明

###API KEY
    在进行修改和编译之前，请找到main/app/res/values/api.xml文件，在里面填入你申请的饭否OAuth API KEY
    如果没有，可以去(<http://fanfou.com/apps>)申请
    
###使用Gradle+Android Studio
    目前只支持使用Gradle构建，直接项目目录运行./gradlew clean build即可，
    (Windows用户使用 gradlew.bat clean build)
    也可以直接使用Android Studio打开项目根目录的build.gradle  
    (需要Gradle 1.11以上，Android Studio 0.8.0以上)



##License

```
Copyright 2013 mcxiaoke support@mcxiaoke.com

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
