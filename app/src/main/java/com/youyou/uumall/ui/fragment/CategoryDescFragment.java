package com.youyou.uumall.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.youyou.uumall.R;
import com.youyou.uumall.base.BaseBusiness;
import com.youyou.uumall.base.BaseConstants;
import com.youyou.uumall.base.BaseFragment;
import com.youyou.uumall.business.CategoryDescBiz;
import com.youyou.uumall.model.GalleryBean;
import com.youyou.uumall.model.GoodsDescBean;
import com.youyou.uumall.view.GalleryView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/10.
 */
@EFragment(R.layout.fragment_cate_desc)
public class CategoryDescFragment extends BaseFragment implements BaseBusiness.ObjectCallbackInterface {
    @ViewById
    TextView cate_frag_name;
    @ViewById
    TextView cate_frag_price;
    @ViewById
    TextView cate_frag_price1;
    @ViewById
    TextView cate_frag_price2;
    @ViewById
    GalleryView cate_frag_vp;
    @ViewById
    LinearLayout cate_frag_ll;
    @Bean
    CategoryDescBiz categoryDescBiz;
    private FragmentActivity activity;

    @AfterViews
    void afterViews() {
        activity = getActivity();
        Bundle bundle = getArguments();
        String goodsId = bundle.getString(BaseConstants.preferencesFiled.GOODS_ID);
        //访问网络的操作
        categoryDescBiz.setObjectCallbackInterface(this);
        categoryDescBiz.queryGoodsById(goodsId);
    }

    @UiThread
    @Override
    public void objectCallBack(int type, Object t) {
        if (CategoryDescBiz.QUERY_GOODS_BY_ID == type) {
            GoodsDescBean bean = (GoodsDescBean) t;
//            log.e(bean.toString());
            initData(bean);
        }
    }

    private void initData(GoodsDescBean bean) {
        if (bean != null) {
            cate_frag_name.setText(bean.brandName);
            cate_frag_price.setText(bean.price);
            cate_frag_price1.setText(bean.customizedPriceName + ":￥" + bean.customizedPrice);
            cate_frag_price2.setText(bean.customizedPriceName + ":￥" + bean.customizedPrice);
            String description = bean.description;
            String[] pics = description.split("\\|");
            ImageLoader imageLoader = ImageLoader.getInstance();
            DisplayMetrics metric = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
            mScreenWidth = metric.widthPixels;
            for (String
                    pic : pics) {
                ImageView imageView = new ImageView(activity);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mScreenWidth,mScreenWidth);
                imageView.setLayoutParams(params);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                cate_frag_ll.addView(imageView);
                imageLoader.displayImage(BaseConstants.connection.ROOT_URL + pic, imageView);
            }
            String image = bean.image;
            String[] gallery = image.split("\\|");
            List<GalleryBean> list = new ArrayList<>();
            for (int i = 0; i < gallery.length; i++) {
                GalleryBean galleryBean = new GalleryBean();
                galleryBean.location = gallery[i];
                list.add(galleryBean);
            }
            cate_frag_vp.setParams(list);
        }
    }

}
