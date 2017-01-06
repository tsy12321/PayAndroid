# PayAndroid

[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Download](https://api.bintray.com/packages/tangsiyuan/maven/pay/images/download.svg) ](https://bintray.com/tangsiyuan/maven/pay/_latestVersion)

> 对微信支付和支付宝支付的App端SDK进行二次封装，对外提供一个较为简单的接口和支付结果回调


## 注意注意：支付宝开放平台新支付的通知！

支付宝升级了支付模式，增加开放平台的概念，之前只要在商户平台申请pid即可实现支付，现在是在开放平台创建App，根据相应APP_ID发起支付。更新后调用方式不变。

1. 老版本接口支付
   pay_param生成参见：http://www.jianshu.com/p/9b353529f02c

2. 新版本接口支付
   pay_param生成参见：http://www.jianshu.com/p/59341ea9d86d

## 1. 如何添加

#### 在app目录下的build.gradle中添加依赖

```gradle
compile 'com.tsy:pay:1.0.0'
```

## 2. Android Manifest配置

**权限声明**

```xml
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

**注册activity**

```xml
        <!-- 微信支付 -->
        <activity
            android:name="com.tsy.sdk.pay.weixin.WXPayCallbackActivity"
            android:configChanges="orientation|keyboardHidden|navigation|screenSize"
            android:launchMode="singleTop"
            android:theme="@android:style/Theme.Translucent.NoTitleBar" />

        <activity-alias
            android:name=".wxapi.WXPayEntryActivity"
            android:exported="true"
            android:targetActivity="com.tsy.sdk.pay.weixin.WXPayCallbackActivity" />

        <!-- 支付宝支付 -->
        <activity
            android:name="com.alipay.sdk.app.H5PayActivity"
            android:configChanges="orientation|keyboardHidden|navigation"
            android:exported="false"
            android:screenOrientation="behind"></activity>
        <activity
            android:name="com.alipay.sdk.auth.AuthActivity"
            android:configChanges="orientation|keyboardHidden|navigation"
            android:exported="false"
            android:screenOrientation="behind"></activity>
```

## 3. 发起支付

### 3.1 微信支付

```java
   /**
     * 微信支付
     * @param pay_param 支付服务生成的支付参数
     */
    private void doWXPay(String pay_param) {
        String wx_appid = "wxXXXXXXX";     //替换为自己的appid
        WXPay.init(getApplicationContext(), wx_appid);      //要在支付前调用
        WXPay.getInstance().doPay(pay_param, new WXPay.WXPayResultCallBack() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplication(), "支付成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int error_code) {
                switch (error_code) {
                    case WXPay.NO_OR_LOW_WX:
                        Toast.makeText(getApplication(), "未安装微信或微信版本过低", Toast.LENGTH_SHORT).show();
                        break;

                    case WXPay.ERROR_PAY_PARAM:
                        Toast.makeText(getApplication(), "参数错误", Toast.LENGTH_SHORT).show();
                        break;

                    case WXPay.ERROR_PAY:
                        Toast.makeText(getApplication(), "支付失败", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplication(), "支付取消", Toast.LENGTH_SHORT).show();
            }
        });
    }
```

### 3.2 支付宝支付

```java
   /**
     * 支付宝支付
     * @param pay_param 支付服务生成的支付参数
     */
    private void doAlipay(String pay_param) {
        new Alipay(this, pay_param, new Alipay.AlipayResultCallBack() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplication(), "支付成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDealing() {
                Toast.makeText(getApplication(), "支付处理中...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int error_code) {
                switch (error_code) {
                    case Alipay.ERROR_RESULT:
                        Toast.makeText(getApplication(), "支付失败:支付结果解析错误", Toast.LENGTH_SHORT).show();
                        break;

                    case Alipay.ERROR_NETWORK:
                        Toast.makeText(getApplication(), "支付失败:网络连接错误", Toast.LENGTH_SHORT).show();
                        break;

                    case Alipay.ERROR_PAY:
                        Toast.makeText(getApplication(), "支付错误:支付码支付失败", Toast.LENGTH_SHORT).show();
                        break;

                    default:
                        Toast.makeText(getApplication(), "支付错误", Toast.LENGTH_SHORT).show();
                        break;
                }

            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplication(), "支付取消", Toast.LENGTH_SHORT).show();
            }
        }).doPay();
    }
```

## Demo使用

项目中Demo支付宝支付可以直接使用。微信支付需要修改以下：

1. appid需要替换为自己的微信appid
1. PackageName和ApplicationId设置为自己的（与微信开放平台填入的包名一致）
1. 使用自己的签名进行打包（与微信开放平台填入签名一致）


Demo示例：

![Demo](https://github.com/tsy12321/PayAndroid/blob/master/preview/demo.jpg)

## 混淆

```
#pay
-dontwarn com.tsy.sdk.pay.**
-keep class com.tsy.sdk.pay.**{*;}

#weixin
-dontwarn  com.tencent.**
-keep class com.tencent.** {*;}

#alipay
-dontwarn com.alipay.**
-keep class com.alipay.** {*;}

-dontwarn  com.ta.utdid2.**
-keep class com.ta.utdid2.** {*;}

-dontwarn  com.ut.device.**
-keep class com.ut.device.** {*;}
```

## About Me
简书地址：http://www.jianshu.com/users/21716b19302d/latest_articles

微信公众号

![我的公众号](https://github.com/tsy12321/PayAndroid/blob/master/wxmp_avatar.jpg)

License
-------

    Copyright 2017 SY.Tang

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
