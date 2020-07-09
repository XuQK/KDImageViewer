package github.xuqk.kdimageviewer.sample;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import github.xuqk.kdimageviewer.KDImageViewer;
import github.xuqk.kdimageviewer.KDslKt;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * Created By：XuQK
 * Created Date：2020/7/9 15:13
 * Creator Email：xu.qiankun@xiji.com
 * Description：
 */

class MainActivityJava extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        KDImageViewer kd = KDslKt.kdImageViewer(new Function1<KDImageViewer, Unit>() {
            @Override
            public Unit invoke(KDImageViewer kdImageViewer) {

                return null;
            }
        });

        kd.setSrcImageViewFetcher(new Function1<Integer, ImageView>() {
            @Override
            public ImageView invoke(Integer integer) {
                return null;
            }
        });
    }
}
