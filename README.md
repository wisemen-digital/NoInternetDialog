# No Internet Dialog

A beautiful Dialog which appears when you have lost your Internet connection.

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-No%20Internet%20Dialog-yellow.svg?style=flat-square)](https://android-arsenal.com/details/1/6493) [![API](https://img.shields.io/badge/API-14%2B-yellow.svg?style=flat-square)](https://android-arsenal.com/api?level=14)

## Setup

#### Gradle:

Add following line of code to your module(app) level gradle file

```java
    implementation 'am.appwise.components:NoInternetDialog:1.1.2'
```

#### Maven:

```xml
  <dependency>
    <groupId>am.appwise.components</groupId>
    <artifactId>NoInternetDialog</artifactId>
    <version>1.1.2</version>
    <type>pom</type>
  </dependency>
```

## Usage

Use simple builder to initiate the dialog. It will automatically appear if you'll lose your Internet connection, and dissapear, once it came back

```java
  NoInternetDialog noInternetDialog = new NoInternetDialog.Builder(context).build();
```
or
```java
  NoInternetDialog noInternetDialog = new NoInternetDialog.Builder(fragment).build();
```
and at the end
```java
    @Override
    protected void onDestroy() {
        super.onDestroy();
        noInternetDialog.onDestroy();
    }
```

Customize the dialog with ease

```java
  builder.setBgGradientStart() // Start color for background gradient
  builder.setBgGradientCenter() // Center color for background gradient
  builder.setBgGradientEnd() // End color for background gradient
  builder.setBgGradientOrientation() // Background gradient orientation (possible values see below)
  builder.setBgGradientType() // Type of background gradient (possible values see below)
  builder.setDialogRadius() // Set custom radius for background gradient
  builder.setTitleTypeface() // Set custom typeface for title text
  builder.setMessageTypeface() // Set custom typeface for message text
  builder.setButtonColor() // Set custom color for dialog buttons
  builder.setButtonTextColor() // Set custom text color for dialog buttons
  builder.setButtonIconsColor() // Set custom color for icons of dialog buttons
  builder.setWifiLoaderColor() // Set custom color for wifi loader
```

|![alt text](https://github.com/appwise-labs/NoInternetDialog/blob/master/Images/Screenshot_20171123-161024.jpg)|![alt text](https://github.com/appwise-labs/NoInternetDialog/blob/master/Images/Screenshot_20171123-161157.jpg)|
|----------------------------------------------------------------------------------------------|-----------|

|![alt text](https://github.com/appwise-labs/NoInternetDialog/blob/master/Images/niam.gif)|![alt text](https://github.com/appwise-labs/NoInternetDialog/blob/master/Images/ninm.gif)|
|----------------------------------------------------------------------------------------------|-----------|

This is work is is inspired from the work of Ramakrishna V. in Dribbble.

(https://dribbble.com/shots/2887886-No-internet-connection-GIF)

## Versions

#### 1.1.2

Receivers issue fixed

#### 1.1.1

Vector drawable issue fixed for < API 21
UI issues fixed for small devices

#### 1.1.0

New customization features added
Issues fixed

#### 1.0.1

Appear issue fixed

### 1.0.0

First version of library

## Licence

```
    No Internet Dialog©
    Copyright 2017 Appwise
    Url: https://github.com/appwise-labs/NoInternetDialog

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
