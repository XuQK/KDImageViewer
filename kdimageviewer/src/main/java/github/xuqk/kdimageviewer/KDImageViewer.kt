package github.xuqk.kdimageviewer

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.*
import androidx.viewpager2.widget.ViewPager2
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
class KDImageViewer(
    private val activity: AppCompatActivity,
    private val imageLoader: ImageLoader,
    private val animDuration: Long = 300L,
    @ColorInt private val defaultBgColor: Int = Color.BLACK,
    val coverModule: KDCoverModule = KDCoverModule(activity),
    var onAnimateListener: OnAnimateListener? = null
) : OnDragChangeListener {

    private val rootView: ViewGroup = FrameLayout(activity)
    private val photoViewContainer = PhotoViewContainer(activity)
    private val pager = ViewPager2(activity)
    private val snapshotView = PhotoView(activity)

    private val originUrlList = mutableListOf<String?>()

    private var argbEvaluator: ArgbEvaluator = ArgbEvaluator()

    /**当前展示状态*/
    var showing: Boolean = false
        private set

    var pageChangeCallback: ViewPager2.OnPageChangeCallback? = null
        set(value) {
            if (field != value && value != null) {
                if (field != null) {
                    pager.unregisterOnPageChangeCallback(field!!)
                }
                pager.registerOnPageChangeCallback(value)

                field = value
            }
        }

    var srcImageViewFetcher: SrcImageViewFetcher = SrcImageViewFetcher()

    init {
        pager.adapter = ImageViewerAdapter()

        photoViewContainer.run {
            addView(pager, generateDefaultLayoutParams())
            addView(snapshotView, generateDefaultLayoutParams())
            viewPager = pager
            dragChangeListener = this@KDImageViewer
        }

        rootView.addView(photoViewContainer, generateDefaultLayoutParams())
        coverModule.getCoverView()?.let {
            rootView.addView(it, generateDefaultLayoutParams())
        }
    }

    fun getCurrentPosition(): Int = pager.currentItem

    fun show(originUrlList: List<String?>, position: Int) {
        if (photoViewContainer.isAnimating) return
        showing = true

        this.originUrlList.clear()
        this.originUrlList.addAll(originUrlList)
        pager.adapter!!.notifyDataSetChanged()

        pager.setCurrentItem(position, false)

        photoViewContainer.isAnimating = true

        // 将snapshotView设置成列表中的srcView的样子
        val srcView = srcImageViewFetcher.getSrcImageView(getCurrentPosition())
        updateSrcViewParams(srcView)

        photoViewContainer.setBackgroundColor(Color.TRANSPARENT)

        (activity.window.decorView as ViewGroup).addView(rootView, generateDefaultLayoutParams())
        pager.visibility = View.INVISIBLE

        snapshotView.run {
            visibility = View.VISIBLE
            translationX = rect.left
            translationY = rect.top
            scaleX = 1f
            scaleY = 1f
            scaleType = if (srcView != null) srcView.scaleType else ImageView.ScaleType.CENTER_CROP

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
            coverModule.getCoverView()?.animate()?.alpha(1f)?.setDuration(animDuration)?.start()
        }
    }

    private fun dismiss() {
        if (photoViewContainer.isAnimating) return
        photoViewContainer.isAnimating = true

        val srcView = srcImageViewFetcher.getSrcImageView(getCurrentPosition())
        updateSrcViewParams(srcView)

        // 将snapshotView设置成当前pager中photoView的样子(matrix)
        (pager.adapter as ImageViewerAdapter).getCurrentPhotoView(getCurrentPosition())?.let {
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
            scaleType = if (srcView != null) srcView.scaleType else ImageView.ScaleType.CENTER_CROP
            layoutParams = layoutParams.apply {
                width = rect.width().toInt()
                height = rect.height().toInt()
            }
        }

        animateShadowBg(Color.TRANSPARENT)
        coverModule.getCoverView()?.animate()?.alpha(0f)?.setDuration(animDuration)?.start()
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
    private fun updateSrcViewParams(srcView: ImageView?) {
        if (srcView == null) {
            rect.set(0f, 0f, 0f, 0f)
        } else {
            srcView.getLocationInWindow(currentOriginViewLocation)
            rect.set(
                currentOriginViewLocation[0].toFloat(),
                currentOriginViewLocation[1].toFloat(),
                (currentOriginViewLocation[0] + srcView.width).toFloat(),
                (currentOriginViewLocation[1] + srcView.height).toFloat()
            )
        }
    }

    override fun onRelease() {
        dismiss()
    }

    override fun onDragChange(dy: Int, scale: Float, fraction: Float) {
        coverModule.getCoverView()?.let {
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
    }

    private fun generateDefaultLayoutParams() = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

    inner class ImageViewerAdapter : RecyclerView.Adapter<ImageViewerAdapter.ImageViewerViewHolder>() {
        private var recyclerView: RecyclerView? = null

        private val onClickListener = View.OnClickListener { dismiss() }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewerViewHolder {
            val view = FrameLayout(activity)
            view.layoutParams = generateDefaultLayoutParams()

            val photoView = PhotoView(activity)
            photoView.tag = "primaryPhotoView"
            val loadingView = coverModule.getLoadingView()
            val loadFailedView = coverModule.getLoadFailedView()

            view.addView(photoView, generateDefaultLayoutParams())
            view.addView(loadingView, generateDefaultLayoutParams())
            view.addView(loadFailedView, generateDefaultLayoutParams())

            photoView.setOnClickListener(onClickListener)

            return ImageViewerViewHolder(view, photoView, loadingView, loadFailedView)
        }

        override fun getItemCount(): Int {
            return originUrlList.size
        }

        override fun onBindViewHolder(holder: ImageViewerViewHolder, position: Int) {
            holder.loadFailedView.setOnClickListener {
                loadOriginImage(holder, position)
            }

            // 先加载已有缩略图
            holder.photoView.setImageDrawable(srcImageViewFetcher.getSrcImageView(position)?.drawable)

            // 再加载原图
            loadOriginImage(holder, position)
        }

        private fun loadOriginImage(holder: ImageViewerViewHolder, position: Int) {
            if (!showing) return

            holder.loadFailedView.visibility = View.GONE
            holder.loadingView.visibility = View.VISIBLE

            imageLoader.load(holder.photoView, originUrlList[position], object :
                ImageLoader.ImageLoaderListener {
                override fun onLoadFailed(errorDrawable: Drawable?) {
                    holder.loadingView.visibility = View.INVISIBLE
                    holder.loadFailedView.visibility = View.VISIBLE
                }

                override fun onLoadSuccess(drawable: Drawable?) {
                    holder.loadingView.visibility = View.INVISIBLE
                    holder.loadFailedView.visibility = View.INVISIBLE
                }
            })
        }

        override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
            this.recyclerView = recyclerView
        }

        override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
            this.recyclerView = null
        }

        fun getCurrentPhotoView(position: Int): PhotoView? {
            return (recyclerView?.findViewHolderForLayoutPosition(position) as? ImageViewerViewHolder)?.photoView
        }

        inner class ImageViewerViewHolder(
            itemView: View,
            val photoView: PhotoView,
            val loadingView: View,
            val loadFailedView: View
        ) : RecyclerView.ViewHolder(itemView)
    }

    /**
     * 根据position获取srcView的工具，如果要实现完美效果，必须实现它
     */
    open class SrcImageViewFetcher {
        open fun getSrcImageView(position: Int): ImageView? {
            return null
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
