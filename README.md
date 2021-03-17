# BottomNavigationCircles-Android

[![](https://jitpack.io/v/Kwasow/BottomNavigationCircles-Android.svg)](https://jitpack.io/#Kwasow/BottomNavigationCircles-Android)

![out](https://user-images.githubusercontent.com/10947344/111543019-c1a88380-8772-11eb-8fd2-2d84f5142f5a.gif)

## Instalation

Add jitpack.io in your top-level build.gradle:
```gradle
allprojects {
        repositories {
                ...
                maven { url 'https://jitpack.io' }
        }
}
```
Add the dependency:
```gradle
dependencies {
        implementation 'com.github.Kwasow:BottomNavigationCircles-Android:1.0'
}

```

## Usage

Just add the view to your layout:
```xml
<androidx.coordinatorlayout.widget.CoordinatorLayout
        android:layout_height="match_parent"
        android:layout_width="match_parent">

    <com.github.kwasow.bottomnavigationcircles.BottomNavigationCircles
        android:layout_width="match_parent"
        android:layout_height="?attr/actionBarSize"
        android:layout_gravity="start|bottom"
        app:menu="@menu/menu_bottom_navigation_bar"/>

</androidx.coordinatorlayout.widget.CoordinatorLayout>
```

The view is an extension to the material.io BottomNavigationView, and implements all it's functions.
The background color will be set to your themes primary color. You may also set the color with:
```kotlin
navigationView.color = Color.RED
```
