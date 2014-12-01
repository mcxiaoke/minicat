蘑菇饭项目
===========================
一个简洁的饭否App，支持Android 4.0以上版本

##下载地址

[蒲公英下载](http://www.pgyer.com/minicat)  
[直接下载](release/minicat-1.3.0.apk?raw=true)

##特别说明

饭否支持帐号： [蘑菇饭App](http://fanfou.com/androidsupport)  
Google Play 地址已失效，又莫名其妙的被Google下架，不再有后续版本更新，将不再上架

##最新版本
    1.3.0 (2014.12.01)
    
##扫码下载

![qrcode](qrcode.png)

##使用说明
    
###使用Gradle+Android Studio
    目前只支持使用Gradle构建，直接项目目录运行./gradlew clean build即可，
    (Windows用户使用 gradlew.bat clean build)
    也可以直接使用Android Studio打开项目根目录的build.gradle  
    (需要Gradle 2.0以上，Android Studio 0.9以上)

###签名注意事项

默认打包的apk是没有签名的，如需签名，请按如下配置：

####方法一
```
在你的 ~/.gradle/gradle.properties中加入如下配置：  
(在项目根目录的gradle.properties里添加也可以)

ANDROID_KEY_STORE=your_keystore_file
ANDROID_KEY_ALIAS=your_keystore_alias
ANDROID_STORE_PASSWORD=your_store_password
ANDROID_KEY_PASSWORD=your_key_password

等号后面的内容请替换为实际值

```

####方法二 
```
在项目的app/build.gradle里假如如下配置：

project.ext.ANDROID_KEY_STORE = 'your_keystore_file'
project.ext.ANDROID_KEY_ALIAS = 'your_keystore_alias'
project.ext.ANDROID_STORE_PASSWORD = 'your_store_password'
project.ext.ANDROID_KEY_PASSWORD = 'your_key_password'

等号后面的内容请替换为实际值

```   
    
####方法三
```

在项目的app/build.gradle里android.signingConfigs.release为：

    signingConfigs {
        release {
            storeFile file("your_real.keystore")
            storePassword "your_keystore_password"
            keyAlias "your_keystore_alias"
            keyPassword "your_key_password"
        }
    }
    
    后面的内容请替换为实际值
    
```
   

##License

```
Copyright 2012-2014 mcxiaoke support@mcxiaoke.com

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
