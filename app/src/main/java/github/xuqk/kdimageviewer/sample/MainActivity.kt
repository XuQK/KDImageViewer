package github.xuqk.kdimageviewer.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import github.xuqk.kdimageviewer.DragPhotoViewHelper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val ivHelper: DragPhotoViewHelper by lazy {
        DragPhotoViewHelper(
            activity = this,
            imageLoader = DragPhotoViewLoader,
            coverModule = MyCoverModule(this)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val list = mutableListOf<String>()
        list.add("https://xgimg1test.hktanis.com/data/upload/mall/store/goods/63/2019/05/20/63_06116634387516739.jpeg?x-oss-process=image/resize,m_mfit,w_196,h_196/quality,Q_10/format,webp")
        list.add("https://xgimg1test.hktanis.com/data/upload/mall/store/64_06329305350921408.jpeg?x-oss-process=image/resize,m_mfit,w_196,h_196/quality,Q_10/format,webp")
        list.add("https://xgimg1test.hktanis.com/data/upload/mall/store/goods/64/2020/01/14/64_06323362325244775.jpeg?x-oss-process=image/resize,m_mfit,w_196,h_196/quality,Q_10/format,webp")
        list.add("https://xgimg1test.hktanis.com/data/upload/mall/store/goods/338/2020/01/04/338_06314621000006721.jpeg?x-oss-process=image/resize,m_mfit,w_196,h_196/quality,Q_10/format,webp")
        list.add("https://xgimg1test.hktanis.com/data/upload/mall/store/goods/338/2020/01/04/338_06314618961051248.jpeg?x-oss-process=image/resize,m_mfit,w_196,h_196/quality,Q_10/format,webp")
        list.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1551692956639&di=8ee41e070c6a42addfc07522fda3b6c8&imgtype=0&src=http%3A%2F%2Fimg.mp.itc.cn%2Fupload%2F20160413%2F75659e9b05b04eb8adf5b52669394897.jpg")
        list.add("https://user-gold-cdn.xitu.io/2019/1/25/168839e977414cc1?imageView2/2/w/800/q/100")

        val originList = mutableListOf<String>()
        originList.add("https://xgimg1test.hktanis.com/data/upload/mall/store/goods/63/2019/05/20/63_06116634387516739.jpeg?x-oss-process=image/format,webp")
        originList.add("https://xgimg1test.hktanis.com/data/upload/mall/store/64_06329305350921408.jpeg?x-oss-process=image/format,webp")
        originList.add("https://xgimg1test.hktanis.com/data/upload/mall/store/goods/64/2020/01/14/64_06323362325244775.jpeg?x-oss-process=image/format,webp")
        originList.add("https://xgimg1test.hktanis.com/data/upload/mall/store/goods/338/2020/01/04/338_06314621000006721.jpeg?x-oss-process=image/format,webp")
        originList.add("https://xgimg1test.hktanis.com/data/upload/mall/store/goods/338/2020/01/04/338_06314618961051248.jpeg?x-oss-process=image/format,webp")
        originList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1551692956639&di=8ee41e070c6a42addfc07522fda3b6c8&imgtype=0&src=http%3A%2F%2Fimg.mp.itc.cn%2Fupload%2F20160413%2F75659e9b05b04eb8adf5b52669394897.jpg")
        originList.add("https://user-gold-cdn.xitu.io/2019/1/25/168839e977414cc1?imageView2/2/w/800/q/100")

        val views = listOf(iv0, iv1, iv2, iv3, iv4, iv5, iv6)

        ivHelper.pageChangeListener = object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                ivHelper.srcView = views[position]
            }
        }

        Glide.with(iv0)
            .load(list[0])
            .override(Target.SIZE_ORIGINAL)
            .into(iv0)
        iv0.setOnClickListener {
            ivHelper.show(originList, list, iv0, 0)
        }

        Glide.with(iv1)
            .load(list[1])
            .override(Target.SIZE_ORIGINAL)
            .into(iv1)
        iv1.setOnClickListener {
            ivHelper.show(originList, list, iv1,  1)
        }

        Glide.with(iv2)
            .load(list[2])
            .override(Target.SIZE_ORIGINAL)
            .into(iv2)
        iv2.setOnClickListener {
            ivHelper.show(originList, list, iv2, 2)
        }

        Glide.with(iv3)
            .load(list[3])
            .override(Target.SIZE_ORIGINAL)
            .into(iv3)
        iv3.setOnClickListener {
            ivHelper.show(originList, list, iv3, 3)
        }

        Glide.with(iv4)
            .load(list[4])
            .override(Target.SIZE_ORIGINAL)
            .into(iv4)
        iv4.setOnClickListener {
            ivHelper.show(originList, list, iv4, 4)
        }

        Glide.with(iv5)
            .load(list[5])
            .override(Target.SIZE_ORIGINAL)
            .into(iv5)
        iv5.setOnClickListener {
            ivHelper.show(originList, list, iv5, 5)
        }

        Glide.with(iv6)
            .load(list[6])
            .override(Target.SIZE_ORIGINAL)
            .into(iv6)
        iv6.setOnClickListener {
            ivHelper.show(originList, list, iv6, 6)
        }

    }

    override fun onBackPressed() {
        if (ivHelper.handleBackPressed()) return
        super.onBackPressed()
    }
}
