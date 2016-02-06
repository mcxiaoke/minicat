蘑菇饭项目
===========================
一个简洁的饭否App，支持Android 4.0以上版本

* **蘑菇饭以后不会更新了，请自行clone代码修改（2015.08.04）**

[蘑菇饭App](http://fanfou.com/androidsupport)  

##下载地址

* [蒲公英下载](http://www.pgyer.com/minicat)   
* [百度市场下载](http://shouji.baidu.com/software/item?docid=5606792)    
* [直接下载](https://github.com/mcxiaoke/minicat/releases/latest)  
* [Google Play](https://play.google.com/store/apps/details?id=com.mcxiaoke.minicat2)   
* [历史版本](https://github.com/mcxiaoke/minicat/releases)   


##最新版本

####1.3.1 (2014.12.02)

    1. 修复个人资料页不能滑动的问题
    2. 修复搜索结果高亮和链接解析问题
    3. 调整热门话题的界面
    
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
    

------

## 关于作者

#### 联系方式

* Blog: <http://blog.mcxiaoke.com>
* Github: <https://github.com/mcxiaoke>
* Email: [github@mcxiaoke.com](mailto:github@mcxiaoke.com)

#### 开源项目

* Awesome-Kotlin: <https://github.com/mcxiaoke/awesome-kotlin>
* Kotlin-Koi: <https://github.com/mcxiaoke/kotlin-koi>
* Next公共组件库: <https://github.com/mcxiaoke/Android-Next>
* PackerNg极速打包工具: <https://github.com/mcxiaoke/packer-ng-plugin>
* Gradle渠道打包: <https://github.com/mcxiaoke/gradle-packer-plugin>
* EventBus实现xBus: <https://github.com/mcxiaoke/xBus>
* Rx文档中文翻译: <https://github.com/mcxiaoke/RxDocs>
* MQTT协议中文版: <https://github.com/mcxiaoke/mqtt>
* 蘑菇饭App: <https://github.com/mcxiaoke/minicat>
* 饭否客户端: <https://github.com/mcxiaoke/fanfouapp-opensource>
* Volley镜像: <https://github.com/mcxiaoke/android-volley>

------

## License

    Copyright 2012 - 2016 Xiaoke Zhang

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

