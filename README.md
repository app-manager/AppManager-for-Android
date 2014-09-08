AppManager for Android
===

[![Build Status](https://travis-ci.org/app-manager/AppManager-for-Android.svg?branch=master)](https://travis-ci.org/app-manager/AppManager-for-Android)

AppManager is the application to download and install the apps
from outside the Google Play Store.

[![Download app from Google Play](app/en_generic_rgb_wo_60.png "Banner")](https://play.google.com/store/apps/details?id=com.appmanager.android)

Testers can download your app from your server very easily!

![Demo](app/demo.gif "Demo")


## Why do you need this app?

* For alpha / beta application testing,
  you can distribute app from your web servers, but
  some Web browsers of Android devices are basically
  not be able to download
  the apk files from the site with BASIC / DIGEST auth,
  which makes it difficult to distribute apps to limited users.
* You can now use Google Play's alpha / beta distribution feature,
  but it is limited to only 2 versions (alpha and beta).
  We often need more variations of an app in some situations.
  For example, the app uses a Web service and it contains an API key
  for each clients.
* And this app will solve these problems! (Maybe)


## Features

* You can download the app from the website with Basic auth.
* You can add entries with URL. (Using Android's intent)
* Apps will be automatically updated if there are any updates after booting device.

### And in the future...

* Administrator can restrict add/modify/delete entries with password.
* Administrator can manage application lifetime, which means
  that you can control when the users can download and use the app.

## How to add entries using Intent

If you want to an entry on multiple devices, you can send email with a special format URL.

### import-to-manager host

AppManager supports `http` and `https` scheme with `app-manager.github.io` or `import-to-manager` host.

e.g.

If you send an email with following URL,

> https://**user**:**pass**@**app-manager.github.io**/github.com/app-manager/AppManager-for-Android/blob/master/tests/apk/dummy.apk?raw=true#**appName**

or

> https://**user**:**pass**@**import-to-appmanager**/github.com/app-manager/AppManager-for-Android/blob/master/tests/apk/dummy.apk?raw=true#**appName**

↓

AppManager will parse URL and set information for a new entry:

```
Name: appName
Basic Auth User: user
Basic Auth Password: pass
URL: https://github.com/app-manager/AppManager-for-Android/blob/master/tests/apk/dummy.apk?raw=true
```

Note that `import-to-appmanager` is simple but not a valid domain format, so some mailers won't assume it as an URL.  
If you have any troubles with using `import-to-appmanager` as a host name, please use `app-manager.github.io` instead.

## Automatic update

AppManager checks if there are any updates when the device has been booted, by sending HTTP request to each URLs and checking Etags.

## Developed By

* Soichiro Kashima - <soichiro.kashima@gmail.com>


## License

    Copyright 2014 Soichiro Kashima

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

