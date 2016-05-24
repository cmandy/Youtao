package com.youyou.uumall.ui.fragment;

import android.widget.ListView;

import com.youyou.uumall.R;
import com.youyou.uumall.adapter.ShopcartAdapter;
import com.youyou.uumall.base.BaseBusiness;
import com.youyou.uumall.base.BaseFragment;
import com.youyou.uumall.business.ShopcartBiz;
import com.youyou.uumall.model.ShopCartBean;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.List;

@EFragment(R.layout.activity_shopcart)
public class ShoppingCatFragment extends BaseFragment implements BaseBusiness.ArrayListCallbackInterface {
    @Bean
    ShopcartBiz shopcartBiz;

    @Bean
    ShopcartAdapter adapter;

    @ViewById
    ListView shopcart_fragment_lv;
    /**
     * 1访问购物车接口,对listview进行展示
     */
    @AfterViews
    void afterViews() {
        shopcartBiz.setArrayListCallbackInterface(this);
//        shopcartBiz.getcartList();
        shopcart_fragment_lv.setAdapter(adapter);
        // TODO: 2016/5/13 这里不能默认访问网络,应该根据情况做判断
    }




    @UiThread
    @Override
    public void arrayCallBack(int type, List<? extends Object> arrayList) {
        List<ShopCartBean> list = (List<ShopCartBean>) arrayList;
        adapter.setData(list);
    }
}