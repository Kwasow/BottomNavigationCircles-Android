package com.github.kwasow.bottomnavigationcircles

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarItemView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.properties.Delegates

class BottomNavigationCircles : BottomNavigationView {
    private val LOG_TAG = "BottomNavigationCircles"

    private var currentNavigationItemId = -1
    private var currentCircleId = -1
    private val menuViewGroupId = View.generateViewId()

    private lateinit var rootLayout: RelativeLayout
    private var disabledColor =
        ContextCompat.getColor(context, R.color.material_on_surface_emphasis_medium)
    private var enabledColor = Color.WHITE
    private var textColor by Delegates.notNull<Int>()

    var circleColor = Color.GREEN
    var darkIcon = false
        set(value) {
            field = value
            updateEnabledColor()
        }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) :
        super(context, attrs, defStyleAttr) {
            init(attrs)
        }

    private fun init(attrs: AttributeSet? = null) {
        getColors(attrs)
        setupRootLayout()
        setupListener()
        setupClipping()
        selectFirstItem()
    }

    private fun setupRootLayout() {
        val menuViewGroup = getChildAt(0) as BottomNavigationMenuView
        menuViewGroup.id = menuViewGroupId
        rootLayout = RelativeLayout(context)
        removeView(menuViewGroup)
        rootLayout.addView(menuViewGroup)
        addView(rootLayout)
    }

    private fun getColors(attrs: AttributeSet?) {
        circleColor = getAttributeColorOrDefault(attrs)
        val textView = TextView(context)
        textColor = textView.currentTextColor
    }

    private fun getAttributeColorOrDefault(attrs: AttributeSet?): Int {
        var color: Int

        context.theme.obtainStyledAttributes(
            attrs, R.styleable.BottomNavigationCircles, 0, 0
        ).apply {
            try {
                color = getInteger(
                    R.styleable.BottomNavigationCircles_circleColor,
                    ContextCompat.getColor(context, R.color.design_default_color_primary)
                )
                darkIcon = getBoolean(
                    R.styleable.BottomNavigationCircles_darkIcon,
                    false
                )
            } finally {
                updateEnabledColor()
                recycle()
            }
        }

        return color
    }

    private fun setupListener() {
        setOnNavigationItemSelectedListener {
            return@setOnNavigationItemSelectedListener animateBottomIcon(it.itemId)
        }
    }

    private fun setupClipping() {
        viewTreeObserver.addOnGlobalLayoutListener {
            clipChildren = false
            rootLayout.clipChildren = false
            findViewById<BottomNavigationMenuView>(menuViewGroupId).clipChildren = false

            disableClipOnParents(this)
        }
    }

    private fun disableClipOnParents(view: View) {
        if (view is ViewGroup) {
            view.clipChildren = false
        }

        if (view.parent is View) {
            disableClipOnParents(view.parent as View)
        }
    }

    private fun selectFirstItem() {
        if (
            rootLayout.childCount > 0 &&
            ((rootLayout.getChildAt(0)) as BottomNavigationMenuView).childCount > 0
        ) {
            val navigationItemView =
                (
                    (rootLayout.getChildAt(0) as BottomNavigationMenuView)
                        .getChildAt(0) as NavigationBarItemView
                )

            navigationItemView.viewTreeObserver.addOnGlobalLayoutListener {
                animateBottomIcon(selectedItemId)
            }
        }
    }

    private fun updateEnabledColor() {
        enabledColor = if (darkIcon) Color.BLACK else Color.WHITE
    }

    private fun animateBottomIcon(itemId: Int): Boolean {
        if (itemId != currentNavigationItemId) {
            val itemView = getNavigationBarItemView(itemId)
            val icon = getAppCompatImageView(itemView)
            val subText = getSubTextView(itemView)
            val bottomNav = this
            val animatorSet = AnimatorSet()

            setSubTextStyle(subText)

            // Navigate previous selection out
            if (currentNavigationItemId != -1) {
                val currentItemView = getNavigationBarItemView(currentNavigationItemId)
                val currentView = getAppCompatImageView(currentItemView)
                val oldCircle = rootLayout.findViewById<ImageView>(currentCircleId)

                currentView.drawable.setTint(Color.BLACK)

                animatorSet.playTogether(
                    buildTranslateIconAnimator(currentView, -(height / 4).toFloat(), 0f),
                    buildTranslateCircleAnimator(oldCircle, -(height / 4).toFloat(), 0f),
                    buildTintAnimator(currentView, enabledColor, disabledColor)
                )
                oldCircle.animate()
                    .alpha(0F)
                    .duration = 500

                GlobalScope.launch {
                    delay(500)
                    withContext(Dispatchers.Main) {
                        rootLayout.removeView(oldCircle)
                    }
                }
            }

            // Navigate new selection in
            val circleView = buildBackgroundCircle()
            currentCircleId = circleView.id

            rootLayout.addView(circleView)
            findViewById<BottomNavigationMenuView>(menuViewGroupId).bringToFront()

            setCircleSizeAndPosition(
                circleView,
                subText.height,
                icon.width * 2,
                itemView.x + itemView.width / 2 - icon.width
            )

            animatorSet.playTogether(
                buildTranslateIconAnimator(icon, 0f, -(bottomNav.height / 4).toFloat()),
                buildTranslateCircleAnimator(circleView, 0f, -(height / 4).toFloat()),
                buildTintAnimator(icon, disabledColor, enabledColor)
            )

            circleView.animate()
                .alpha(1F)
                .duration = 500

            currentNavigationItemId = itemId
            animatorSet.start()
        }

        return true
    }

    private fun getNavigationBarItemView(itemId: Int): NavigationBarItemView {
        return findViewById(itemId)
    }

    private fun getAppCompatImageView(itemView: NavigationBarItemView): AppCompatImageView {
        return itemView.findViewById(
            com.google.android.material.R.id.navigation_bar_item_icon_view
        )
    }

    private fun getSubTextView(itemView: NavigationBarItemView): TextView {
        return itemView.findViewById(
            com.google.android.material.R.id.navigation_bar_item_large_label_view
        )
    }

    private fun setSubTextStyle(textView: TextView) {
        textView.setTypeface(textView.typeface, Typeface.BOLD)
        textView.setTextColor(textColor)
    }

    private fun buildTranslateIconAnimator(currentView: View, from: Float, to: Float):
        ObjectAnimator {
            return ObjectAnimator.ofFloat(
                currentView,
                "translationY",
                from, to
            ).setDuration(500)
        }

    private fun buildTranslateCircleAnimator(oldCircle: View, from: Float, to: Float):
        ObjectAnimator {
            return ObjectAnimator.ofFloat(
                oldCircle,
                "translationY",
                from, to
            ).setDuration(500)
        }

    private fun buildTintAnimator(currentView: AppCompatImageView, from: Int, to: Int):
        ValueAnimator {
            val animateTint = ValueAnimator.ofArgb(from, to)
            animateTint.duration = 500
            animateTint.addUpdateListener {
                currentView.drawable.setTint(it.animatedValue as Int)
            }

            return animateTint
        }

    private fun buildBackgroundCircle(): ImageView {
        val circleDrawable = ContextCompat.getDrawable(context, R.drawable.bg_green_circle)
        circleDrawable?.setTint(circleColor)
        val circleView = ImageView(context)
        circleView.id = View.generateViewId()
        circleView.setImageDrawable(circleDrawable)
        circleView.alpha = 0F

        return circleView
    }

    private fun setCircleSizeAndPosition(
        circleView: ImageView,
        paddingBottom: Int,
        size: Int,
        x: Float
    ) {
        val params = circleView.layoutParams
        circleView.setPadding(0, 0, 0, paddingBottom / 3)
        params.width = size
        params.height = size
        circleView.layoutParams = params
        circleView.x = x
    }
}
