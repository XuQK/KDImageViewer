package github.xuqk.kdimageviewer

import android.app.Activity
import android.content.Context
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

open class KDCoverModule(private val context: Context) {

    open fun getLoadingView(): View {
        val wrapView = FrameLayout(context)
        val progressBarLp = FrameLayout.LayoutParams(
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36f, context.resources.displayMetrics).toInt(),
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36f, context.resources.displayMetrics).toInt())
        progressBarLp.gravity = Gravity.CENTER
        val progressBar = ProgressBar(context)
        progressBar.alpha = 0.7f
        progressBar.indeterminateTintList = ColorStateList.valueOf(Color.WHITE)
        wrapView.addView(progressBar, progressBarLp)
        return wrapView
    }

    open fun getLoadFailedView(): View {
        return View(context)
    }

    open fun getCoverView(): View? {
        return null
    }

}