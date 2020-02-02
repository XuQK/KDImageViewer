package github.xuqk.kdimageviewer

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar

/**
 * Created By：XuQK
 * Created Date：1/31/20 10:04 PM
 * Creator Email：xuqiankun66@gmail.com
 * Description：
 */

open class KDCoverModule(private val activity: Activity) {

    open fun getLoadingView(): View {
        val wrapView = FrameLayout(activity)
        val progressBarLp = FrameLayout.LayoutParams(
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36f, activity.resources.displayMetrics).toInt(),
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36f, activity.resources.displayMetrics).toInt())
        progressBarLp.gravity = Gravity.CENTER
        val progressBar = ProgressBar(activity)
        progressBar.alpha = 0.7f
        progressBar.indeterminateTintList = ColorStateList.valueOf(Color.WHITE)
        wrapView.addView(progressBar, progressBarLp)
        return wrapView
    }

    open fun getLoadFailedView(): View {
        return View(activity)
    }

    open fun getCoverView(): View? {
        return null
    }

}