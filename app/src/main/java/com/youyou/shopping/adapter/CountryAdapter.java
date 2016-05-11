package com.youyou.shopping.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.youyou.shopping.R;
import com.youyou.shopping.base.BaseConstants;
import com.youyou.shopping.bean.ViewHolder;
import com.youyou.shopping.utils.MyUtils;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.List;

/**
 * Created by Administrator on 2016/5/9.
 */
@EBean
public class CountryAdapter extends BaseAdapter{
    @RootContext
    Context mContext;

    LayoutInflater mInflater;
    List<String> dictList;


    @AfterInject
    void afterInject() {
        mInflater = LayoutInflater.from(mContext);
    }

    public void setData(List dictList){
        this.dictList = dictList;
    }

    @Override
    public int getCount() {
        return dictList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_country, null);
        }
        TextView item_country_tv = ViewHolder.get(convertView, R.id.item_country_tv);
        String defaultCountry = MyUtils.getPara(BaseConstants.preferencesFiled.DEFAULT_COUNTRY, mContext);
        String currentCountry = dictList.get(position).split(",")[0];//这种方法不是很好,应该使用数据库的方式来获取
        if (TextUtils.equals(defaultCountry,currentCountry)){
            item_country_tv.setTextColor(mContext.getResources().getColor(R.color.font_country_conuntry));
        }
        item_country_tv.setText(currentCountry);
        return convertView;
    }
}
