package github.xuqk.kdimageviewer.sample

import android.content.Context
import android.util.Log
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule

/**
 * Created By：XuQK
 * Created Date：2/2/20 11:33 AM
 * Creator Email：xuqiankun66@gmail.com
 * Description：
 */

@GlideModule
class DemoGlideModule : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setLogLevel(Log.WARN)
    }
}