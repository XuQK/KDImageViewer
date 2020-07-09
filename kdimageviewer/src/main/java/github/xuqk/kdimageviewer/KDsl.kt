package github.xuqk.kdimageviewer

/**
 * Created By：XuQK
 * Created Date：2020/7/9 14:40
 * Creator Email：xu.qiankun@xiji.com
 * Description：
 */

fun kdImageViewer(block: KDImageViewer.() -> Unit) : KDImageViewer {
    val v = KDImageViewer()
    v.block()
    v.init()
    return v
}