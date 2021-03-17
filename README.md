# BottomNavigationCircles-Android

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