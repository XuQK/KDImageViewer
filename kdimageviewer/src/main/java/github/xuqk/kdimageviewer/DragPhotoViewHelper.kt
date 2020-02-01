package github.xuqk.kdimageviewer

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.*
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.transition.*
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener
import github.xuqk.kdimageviewer.photoview.PhotoView

/**
 * Created By：XuQK
 * Created Date：1/24/20 4:52 PM
 * Creator Email：xuqiankun66@gmail.com
 * Description：
 *
 * @param activity
 * @param animDuration 动画时长
 * @param defaultBgColor 大图模式的背景色
 * @param coverModule 蒙层模块
 * @param onAnimateListener 动画起始结束监听
 */
class DragPhotoViewHelper(
    private val activity: AppCompatActivity,
    private val imageLoader: ImageLoader,
    private val animDuration: Long = 300L,
    @ColorInt private val defaultBgColor: Int = Color.BLACK,
    private var coverModule: DefaultCoverModule = DefaultCoverModule(
        activity
    ),
    var onAnimateListener: OnAnimateListener? = null
) : OnDragChangeListener {

    private val rootView: ViewGroup = FrameLayout(activity)
    private val photoViewContainer = PhotoViewContainer(activity)
    private val pager = HackyViewPager(activity)
    private val snapshotView = PhotoView(activity)

    private var coverView: View? = null

    private val originUrlList = mutableListOf<String?>()
    private val thumbUrlList = mutableListOf<String?>()

    private var argbEvaluator: ArgbEvaluator = ArgbEvaluator()

    /**当前展示状态*/
    var showing: Boolean = false
        private set

    var currentPosition = 0
    var srcView: ImageView? = null
        set(value) {
            if (!showing) {
                field = value
            }
        }

    var pageChangeListener: SimpleOnPageChangeListener? = null

    init {
        photoViewContainer.addView(pager)
        pager.addOnPageChangeListener(object : SimpleOnPageChangeListener() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                pageChangeListener?.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(i: Int) {
                currentPosition = i
                pageChangeListener?.onPageSelected(i)
            }

            override fun onPageScrollStateChanged(state: Int) {
                pageChangeListener?.onPageScrollStateChanged(state)
            }
        })

        photoViewContainer.addView(snapshotView)
        photoViewContainer.viewPager = pager
        photoViewContainer.dragChangeListener = this

        rootView.addView(photoViewContainer)
        rootView.addView(coverModule.getCoverView())
    }

    fun setImageData(originUrlList: List<String?>, thumbUrlList: List<String?>, srcView: ImageView, position: Int): DragPhotoViewHelper {
        this.originUrlList.clear()
        this.originUrlList.addAll(originUrlList)
        this.thumbUrlList.clear()
        this.thumbUrlList.addAll(thumbUrlList)

        this.currentPosition = position

        pager.adapter = PhotoViewAdapter()
        pager.currentItem = position

        this.srcView = srcView
        return this
    }

    fun show() {
        if (photoViewContainer.isAnimating) return
        showing = true
        photoViewContainer.isAnimating = true

        // 将snapshotView设置成列表中的srcView的样子
        updateSrcViewParams()

        photoViewContainer.setBackgroundColor(Color.TRANSPARENT)

        (activity.window.decorView as ViewGroup).addView(rootView)
        pager.visibility = View.INVISIBLE

        snapshotView.run {
            visibility = View.VISIBLE
            translationX = rect.left
            translationY = rect.top
            scaleX = 1f
            scaleY = 1f
            scaleType = if (srcView != null) srcView!!.scaleType else ImageView.ScaleType.CENTER_CROP

            layoutParams = layoutParams.apply {
                width = rect.width().toInt()
                height = rect.height().toInt()
            }

            setImageDrawable(srcView?.drawable)
        }

        snapshotView.post {
            onAnimateListener?.onShowAnimateStart()
            TransitionManager.beginDelayedTransition(
                snapshotView.parent as ViewGroup,
                TransitionSet()
                    .setDuration(animDuration)
                    .addTransition(ChangeBounds())
                    .addTransition(ChangeTransform())
                    .addTransition(ChangeImageTransform())
                    .setInterpolator(FastOutSlowInInterpolator())
                    .addListener(object : TransitionListenerAdapter() {
                        override fun onTransitionEnd(transition: Transition) {
                            photoViewContainer.isAnimating = false

                            pager.visibility = View.VISIBLE
                            snapshotView.visibility = View.INVISIBLE
                            pager.scaleX = 1f
                            pager.scaleY = 1f

                            onAnimateListener?.onShowAnimateEnd()
                        }
                    })
            )

            snapshotView.run {
                translationY = 0f
                translationX = 0f
                scaleX = 1f
                scaleY = 1f
                scaleType = ImageView.ScaleType.FIT_CENTER

                layoutParams = layoutParams.apply {
                    width = rootView.width
                    height = rootView.height
                }
            }

            animateShadowBg(defaultBgColor)
            coverView?.animate()?.alpha(1f)?.setDuration(animDuration)?.start()
        }
    }

    private fun dismiss() {
        if (photoViewContainer.isAnimating) return
        photoViewContainer.isAnimating = true

        updateSrcViewParams()

        // 将snapshotView设置成当前pager中photoView的样子(matrix)
        (pager.adapter as PhotoViewAdapter).primaryPhotoView?.let {
            snapshotView.run {
                setImageDrawable(it.drawable)

                val matrix = Matrix()
                it.getSuppMatrix(matrix)
                setSuppMatrix(matrix)
            }
        }

        // 替换成snapshotView来进行动画
        pager.visibility = View.INVISIBLE
        snapshotView.visibility = View.VISIBLE

        onAnimateListener?.onDismissAnimateStart()
        TransitionManager.beginDelayedTransition(
            snapshotView.parent as ViewGroup,
            TransitionSet()
                .setDuration(animDuration)
                .addTransition(ChangeBounds())
                .addTransition(ChangeTransform())
                .addTransition(ChangeImageTransform())
                .setInterpolator(FastOutSlowInInterpolator())
                .addListener(object : TransitionListenerAdapter() {
                    override fun onTransitionEnd(transition: Transition) {
                        photoViewContainer.isAnimating = false
                        (rootView.parent as? ViewGroup)?.removeView(rootView)
                        pager.visibility = View.INVISIBLE
                        snapshotView.visibility = View.VISIBLE
                        pager.scaleX = 1f
                        pager.scaleY = 1f
                        snapshotView.scaleX = 1f
                        snapshotView.scaleY = 1f

                        onAnimateListener?.onDismissAnimateEnd()
                        reset()
                        showing = false
                    }
                })
        )

        snapshotView.run {
            translationY = rect.top
            translationX = rect.left
            scaleX = 1f
            scaleY = 1f
            scaleType = if (srcView != null) srcView!!.scaleType else ImageView.ScaleType.CENTER_CROP
            layoutParams = layoutParams.apply {
                width = rect.width().toInt()
                height = rect.height().toInt()
            }
        }

        animateShadowBg(Color.TRANSPARENT)
        coverView?.animate()?.alpha(0f)?.setDuration(animDuration)?.start()
    }

    fun handleBackPressed(): Boolean {
        if (photoViewContainer.isAnimating) return true

        if (showing) {
            dismiss()
            return true
        }
        return false
    }

    private fun animateShadowBg(endColor: Int) {
        val startColor = (photoViewContainer.background as ColorDrawable).color
        ValueAnimator.ofFloat(0f, 1f).apply {
            addUpdateListener {
                photoViewContainer.setBackgroundColor(
                    argbEvaluator.evaluate(it.animatedFraction, startColor, endColor) as Int
                )
            }
            duration = animDuration
            interpolator = LinearInterpolator()
        }.start()
    }

    private val rect = RectF()
    private val currentOriginViewLocation = intArrayOf(0, 0)
    /**
     * 更新srcView参数
     */
    private fun updateSrcViewParams() {
        if (srcView == null) {
            rect.set(0f, 0f, 0f, 0f)
        } else {
            srcView!!.getLocationInWindow(currentOriginViewLocation)
            rect.set(
                currentOriginViewLocation[0].toFloat(),
                currentOriginViewLocation[1].toFloat(),
                (currentOriginViewLocation[0] + srcView!!.width).toFloat(),
                (currentOriginViewLocation[1] + srcView!!.height).toFloat()
            )
        }
    }

    override fun onRelease() {
        dismiss()
    }

    override fun onDragChange(dy: Int, scale: Float, fraction: Float) {
        coverView?.let {
            it.alpha = 1 - fraction
        }

        photoViewContainer.setBackgroundColor(
            argbEvaluator.evaluate(
                fraction * 0.8f,
                Color.BLACK,
                Color.TRANSPARENT
            ) as Int
        )
    }

    private fun reset() {
        snapshotView.setImageDrawable(null)
        srcView = null
        originUrlList.clear()
        thumbUrlList.clear()
        pager.adapter = null
    }

    inner class PhotoViewAdapter : PagerAdapter() {
        private lateinit var primaryItem: View
        val primaryPhotoView: PhotoView?
            get() = (primaryItem as ViewGroup).findViewWithTag("primaryPhotoView")

        override fun getCount(): Int {
            return originUrlList.size
        }

        override fun isViewFromObject(view: View, o: Any) = o === view

        override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
            super.setPrimaryItem(container, position, `object`)
            primaryItem = `object` as View
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = FrameLayout(activity)
            val photoView = PhotoView(activity)
            photoView.tag = "primaryPhotoView"
            val loadingView = coverModule.getLoadingView()
            val loadFailedView = coverModule.getLoadFailedView()

            view.addView(photoView)
            view.addView(loadingView, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            view.addView(loadFailedView, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            loadFailedView.setOnClickListener {
                loadOriginImage(photoView, position, loadFailedView, loadingView)
            }

            // 先加载已有缩略图
            imageLoader.load(photoView, thumbUrlList[position], null)

            // 再加载原图
            loadOriginImage(photoView, position, loadFailedView, loadingView)

            container.addView(view)
            photoView.setOnClickListener {
                dismiss()
            }
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        private fun loadOriginImage(photoView: PhotoView, position: Int, loadFailedView: View, loadingView: View) {
            if (!showing) return

            loadFailedView.visibility = View.GONE
            loadingView.visibility = View.VISIBLE

            imageLoader.load(photoView, originUrlList[position], object :
                ImageLoader.ImageLoaderListener {
                override fun onLoadFailed(errorDrawable: Drawable?) {
                    loadingView.visibility = View.INVISIBLE
                    loadFailedView.visibility = View.VISIBLE
                }

                override fun onLoadSuccess(drawable: Drawable?) {
                    loadingView.visibility = View.INVISIBLE
                    loadFailedView.visibility = View.INVISIBLE
                }
            })
        }
    }
}

interface OnDragChangeListener {
    fun onRelease()
    fun onDragChange(dy: Int, scale: Float, fraction: Float)
}

interface OnAnimateListener {
    fun onShowAnimateStart()
    fun onShowAnimateEnd()
    fun onDismissAnimateStart()
    fun onDismissAnimateEnd()
}

interface ImageLoader {
    fun load(imageView: ImageView, url: String?, loaderListener: ImageLoaderListener?)

    interface ImageLoaderListener {
        fun onLoadFailed(errorDrawable: Drawable?)

        fun onLoadSuccess(drawable: Drawable?)
    }
}
