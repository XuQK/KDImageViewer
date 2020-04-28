package github.xuqk.kdimageviewer.sample

import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import github.xuqk.kdimageviewer.ImageLoader

/**
 * Created By：XuQK
 * Created Date：1/24/20 7:07 PM
 * Creator Email：xuqiankun66@gmail.com
 * Description：
 */

object KDImageViewLoader : ImageLoader {
    override fun load(imageView: ImageView, url: String?, loaderListener: ImageLoader.ImageLoaderListener?) {
        Glide.with(imageView).load(url)
            .override(Target.SIZE_ORIGINAL)
            .into(object : CustomTarget<Drawable>() {
                override fun onLoadCleared(placeholder: Drawable?) {

                }

                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    imageView.post {
                        imageView.setImageDrawable(resource)
                        if (resource is Animatable && !resource.isRunning) {
                            resource.start()
                        }

                        loaderListener?.onLoadSuccess(resource)
                    }
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    imageView.post { loaderListener?.onLoadFailed(errorDrawable) }
                }

                override fun onLoadStarted(placeholder: Drawable?) {

                }
            })
    }

    override fun stopLoad(imageView: ImageView) {
        Glide.with(imageView).clear(imageView)
    }
}