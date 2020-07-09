package github.xuqk.kdimageviewer.sample

import android.app.Activity
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import github.xuqk.kdimageviewer.KDCoverModule

/**
 * Created By：XuQK
 * Created Date：1/31/20 10:04 PM
 * Creator Email：xuqiankun66@gmail.com
 * Description：
 */

class MyCoverModule(private val activity: Activity): KDCoverModule(activity) {

    private var coverView: View? = null

    override fun getLoadFailedView(): View {
        return TextView(activity).apply {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
            setTextColor(0xffffffff.toInt())
            text = "点击重新加载"
            gravity = Gravity.CENTER
        }
    }

    override fun getCoverView(): View? {
        if (coverView == null) {
            coverView = LayoutInflater.from(activity).inflate(R.layout.app_widget_cover, null)
            coverView!!.findViewById<View>(R.id.tv_download).setOnClickListener {
                Toast.makeText(activity, "你就当我下载了吧", Toast.LENGTH_SHORT).show()
            }
            coverView!!.findViewById<View>(R.id.tv_click).setOnClickListener {
                Toast.makeText(activity, "点击", Toast.LENGTH_SHORT).show()
            }
        }
        return coverView!!
    }

}