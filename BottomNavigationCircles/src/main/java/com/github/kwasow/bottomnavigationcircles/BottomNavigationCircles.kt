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
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.properties.Delegates

class BottomNavigationCircles : BottomNavigationView {
    private var currentNavigationItemId = -1
    private var currentCircleId = -1
    private val menuViewGroupId = View.generateViewId()

    private lateinit var rootLayout: RelativeLayout
    private var disabledColor by Delegates.notNull<Int>()
    private var circleColor by Delegates.notNull<Int>()
    private var textColor by Delegates.notNull<Int>()

    var color: Int? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        setupRootLayout()
        setupListener()
        setupClipping()
        getColors()
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

    private fun getColors() {
        disabledColor = ContextCompat.getColor(context, R.color.material_on_surface_emphasis_medium)
        circleColor = ContextCompat.getColor(context, R.color.design_default_color_primary)
        val textView = TextView(context)
        textColor = textView.currentTextColor
    }

    private fun selectFirstItem() {
        val firstElement =
                ((rootLayout.getChildAt(0)) as BottomNavigationMenuView).getChildAt(0)
                        .findViewById<AppCompatImageView>(com.google.android.material.R.id.icon)

        firstElement.viewTreeObserver.addOnGlobalLayoutListener {
            animateBottomIcon(selectedItemId)
        }
    }

    private fun animateBottomIcon(itemId: Int): Boolean {
        if (color != null) {
            circleColor = color!!
        }

        if (itemId != currentNavigationItemId) {
            val itemView =
                    findViewById<BottomNavigationItemView>(itemId)
            val icon = itemView
                    .findViewById<AppCompatImageView>(com.google.android.material.R.id.icon)
            val subText = itemView
                    .findViewById<TextView>(com.google.android.material.R.id.largeLabel)
            val bottomNav = this
            val animatorSet = AnimatorSet()

            subText.setTypeface(subText.typeface, Typeface.BOLD)
            subText.setTextColor(textColor)

            // Navigate previous selection out
            if (currentNavigationItemId != -1) {
                val currentItemView =
                        findViewById<BottomNavigationItemView>(currentNavigationItemId)
                val currentView = currentItemView
                        .findViewById<AppCompatImageView>(com.google.android.material.R.id.icon)
                val oldCircle = rootLayout.findViewById<ImageView>(currentCircleId)

                currentView.drawable.setTint(Color.BLACK)

                val translateDownAnimator = ObjectAnimator.ofFloat(
                        currentView,
                        "translationY",
                        -(bottomNav.height / 4).toFloat(),
                        0f
                ).setDuration(500)
                val translateCircleDownAnimator = ObjectAnimator.ofFloat(
                        oldCircle,
                        "translationY",
                        -(bottomNav.height / 4).toFloat(),
                        0f
                ).setDuration(500)
                val animateTintWhiteToBlack = ValueAnimator.ofArgb(Color.WHITE, disabledColor)
                animateTintWhiteToBlack.duration = 500
                animateTintWhiteToBlack.addUpdateListener {
                    currentView.drawable.setTint(it.animatedValue as Int)
                }

                animatorSet.playTogether(
                        translateDownAnimator,
                        translateCircleDownAnimator,
                        animateTintWhiteToBlack
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
            val circleDrawable = ContextCompat.getDrawable(context, R.drawable.bg_green_circle)
            circleDrawable?.setTint(circleColor)
            val circleView = ImageView(context)
            circleView.id = View.generateViewId()
            currentCircleId = circleView.id
            circleView.setPadding(0, 0, 0, subText.height / 3)
            circleView.alpha = 0F
            rootLayout.addView(circleView)
            val params = circleView.layoutParams
            params.width = icon.width * 2
            params.height = icon.height * 2
            circleView.layoutParams = params
            circleView.setImageDrawable(circleDrawable)
            circleView.x = itemView.x + itemView.width / 2 - icon.width
            findViewById<BottomNavigationMenuView>(menuViewGroupId).bringToFront()

            val translateIconUpAnimator = ObjectAnimator.ofFloat(
                    icon,
                    "translationY",
                    0f,
                    -(bottomNav.height / 4).toFloat()
            ).setDuration(500)
            val translateCircleUpAnimator = ObjectAnimator.ofFloat(
                    circleView,
                    "translationY",
                    0f,
                    -(bottomNav.height / 4).toFloat()
            ).setDuration(500)
            val animateTintBlackToWhite = ValueAnimator.ofArgb(disabledColor, Color.WHITE)
            animateTintBlackToWhite.duration = 500
            animateTintBlackToWhite.addUpdateListener {
                icon.drawable.setTint(it.animatedValue as Int)
            }

            animatorSet.playTogether(
                    translateIconUpAnimator,
                    translateCircleUpAnimator,
                    animateTintBlackToWhite
            )
            circleView.animate()
                    .alpha(1F)
                    .duration = 500

            currentNavigationItemId = itemId
            animatorSet.start()
        }

        return true
    }
}