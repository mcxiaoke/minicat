## 以后真的没时间更新了，有兴趣的可以自己修改

蘑菇饭App
===========================
一个简洁的饭否App，最新版本是1.5.5，[更新日志](CHANGELOG.md)。

[蘑菇饭App](http://fanfou.com/androidsupport)  

##下载地址

* [Google Play](https://play.google.com/store/apps/details?id=com.mcxiaoke.minicat2)  
* [直接下载](https://github.com/mcxiaoke/minicat/releases/latest)  
* [历史版本](https://github.com/mcxiaoke/minicat/releases)   
    
### 扫码下载

![qrcode](qrcode.png)

##开发说明
    
###使用Gradle+Android Studio

    需要Gradle 2.10以上，Android Studio 2.0以上

###签名注意事项

默认打包的apk是没有签名的，如需签名，请按如下配置：

```
// 修改app/build.gradle里android.signingConfigs.release
    signingConfigs {
        release {
            storeFile file("your_real.keystore")
            storePassword "your_keystore_password"
            keyAlias "your_keystore_alias"
            keyPassword "your_key_password"
        }
    }
    // 后面的内容请替换为实际值
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

