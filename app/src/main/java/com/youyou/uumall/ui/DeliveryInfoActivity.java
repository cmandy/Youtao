package com.youyou.uumall.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.Time;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.youyou.uumall.R;
import com.youyou.uumall.base.BaseActivity;
import com.youyou.uumall.base.BaseBusiness;
import com.youyou.uumall.business.AddressBiz;
import com.youyou.uumall.model.DictBean;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/5/17.
 */
@EActivity(R.layout.activity_delivery_info)
public class DeliveryInfoActivity extends BaseActivity implements BaseBusiness.ArrayListCallbackInterface, View.OnClickListener, TextWatcher, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    public static final int DOWN_ANIMATION = 0;
    public static final int UP_ANIMATION = 1;
    private boolean isOpen;

    @ViewById
    EditText delivery_info_name_et;

    @ViewById
    EditText delivery_info_phone_et;

    @ViewById
    TextView delivery_info_date_tv;

    @ViewById
    EditText delivery_info_flt_no_et;

    @ViewById
    LinearLayout delivery_info_delivery_ll;

    @ViewById
    LinearLayout delivery_info_add_ll;

    @ViewById
    TextView delivery_info_delivery_tv;

    @ViewById
    TextView delivery_info_delivery_tv2;

    @ViewById
    ImageView delivery_info_delivery_iv;

    @ViewById
    Button delivery_info_confirm_btn;


    @Bean
    AddressBiz addressBiz;
    private List<DictBean> mDeliveryList;
    private String name;
    private String phone;
    private String date;
    private String fltNO;
    private String delivery;

    @AfterViews
    void afterViews() {
        addressBiz.setArrayListCallbackInterface(this);
        getDelivery();
        delivery_info_name_et.addTextChangedListener(this);
        delivery_info_phone_et.addTextChangedListener(this);
        delivery_info_flt_no_et.addTextChangedListener(this);
        delivery_info_date_tv.setOnClickListener(this);
    }

    private void getDelivery() {
        Map param = new HashMap();
        param.put("countryCode", "KR");// TODO: 2016/5/18 未完待续
        addressBiz.queryDelivery(param);
    }

    @Override
    public void arrayCallBack(int type, List<? extends Object> arrayList) {
        mDeliveryList = (List<DictBean>) arrayList;
    }


    @Click
    void delivery_info_delivery_ll() {//选择自提点的ll
        if (mDeliveryList != null) {
            if (!isOpen) {//如果不是开启状态,就选择开启
                for (int i = 0; i < mDeliveryList.size(); i++) {
                    DictBean dictBean = mDeliveryList.get(i);
                    View item = View.inflate(mApp, R.layout.item_delivery_info, null);
                    TextView tv = (TextView) item.findViewById(R.id.item_delivery_info_tv);
                    tv.setText(dictBean.deliveryName);

                    Animation rotateAnimation = getRotateAnimation(DOWN_ANIMATION);
                    delivery_info_delivery_iv.setAnimation(rotateAnimation);
                    delivery_info_delivery_tv.setText("");
                    delivery_info_add_ll.addView(item);
                    item.setOnClickListener(this);
                }
                isOpen = true;
            } else {
                String delivery = delivery_info_delivery_tv2.getText().toString();
                if (TextUtils.isEmpty(delivery)) {
                    delivery_info_delivery_tv.setText("请选择自提点");
                }
                Animation rotateAnimation = getRotateAnimation(UP_ANIMATION);
                delivery_info_delivery_iv.setAnimation(rotateAnimation);
                delivery_info_add_ll.removeAllViews();
                isOpen = false;
            }

        } else {
            showToastLong("网络数据获取异常");
        }
    }

    public Animation getRotateAnimation(int type) {
        RotateAnimation downrotateAnimation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        downrotateAnimation.setDuration(100);
        downrotateAnimation.setFillAfter(true);
        downrotateAnimation.start();

        RotateAnimation uprotateAnimation = new RotateAnimation(180, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        uprotateAnimation.setDuration(100);
        uprotateAnimation.setFillAfter(true);
        uprotateAnimation.start();
        switch (type) {
            case DOWN_ANIMATION:
                return downrotateAnimation;
            case UP_ANIMATION:
                return uprotateAnimation;
        }
        return null;
    }

    @Click
    void delivery_info_confirm_btn() {//提交信息
        Intent intent = new Intent();
        for (int i = 0; i < mDeliveryList.size(); i++) {
            DictBean dictBean = mDeliveryList.get(i);
            if (TextUtils.equals(dictBean.deliveryName,delivery)){
                intent.putExtra("deliveryId",dictBean.deliveryId);
            }
        }
        intent.putExtra("name",name);
        intent.putExtra("phone",phone);
        intent.putExtra("date",date);
        intent.putExtra("fltNO",fltNO);
        intent.putExtra("delivery",delivery);
        setResult(RESULT_OK, intent);
        finish();
    }
//    private boolean isDeliveryChecked = false;
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.delivery_info_date_tv) {//选择日期和时间

//            Date date = new Date();
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss");
//            String dormatDate = dateFormat.format(date);
//            String[] splitDate = dormatDate.split("/");
//            for (int i = 0; i < splitDate.length; i++) {
//
//            }
            Time t=new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料。
            t.setToNow(); // 取得系统时间。
            int year = t.year;
            int month = t.month;
            int date = t.monthDay;
            DatePickerDialog pickerDialog = new DatePickerDialog(this,this,year,month,date);
            pickerDialog.show();
        }else{//选择自提点
            isOpen = false;
            delivery_info_add_ll.removeAllViews();
            Animation rotateAnimation = getRotateAnimation(UP_ANIMATION);
            delivery_info_delivery_iv.setAnimation(rotateAnimation);
            delivery_info_delivery_tv.setText("");
            TextView tv = (TextView) v.findViewById(R.id.item_delivery_info_tv);
            String info = tv.getText().toString();
            delivery_info_delivery_tv2.setText(info);
//        isDeliveryChecked=true;
//        delivery_info_confirm_btn.setEnabled(true);
            setButtonState();
        }

    }

    private void setButtonState() {
        name = delivery_info_name_et.getText().toString();
        phone = delivery_info_phone_et.getText().toString();
        date = delivery_info_date_tv.getText().toString();
        fltNO = delivery_info_flt_no_et.getText().toString();
        delivery = delivery_info_delivery_tv2.getText().toString();
        if (!TextUtils.isEmpty(name)&&!TextUtils.isEmpty(phone)&&!TextUtils.isEmpty(date)&&!TextUtils.isEmpty(fltNO)&&!TextUtils.isEmpty(delivery)) {
            delivery_info_confirm_btn.setEnabled(true);
        }else{
            delivery_info_confirm_btn.setEnabled(false);
        }
    }

    @Click
    void delivery_info_pro_iv() {
        finish();
//        overridePendingTransition(R.anim.anim_none, R.anim.from_right_exit);
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//        log.e("beforeTextChanged-->CharSequence:"+s+";start:"+start+";count:"+count+";after:"+after);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
//        log.e("onTextChanged-->CharSequence:"+s+";start:"+start+";count:"+count+";before:"+before);
    }

    @Override
    public void afterTextChanged(Editable s) {
//        log.e("afterTextChanged-->Editable:"+s);
        setButtonState();
    }
    private int year;
    private int month;
    private int day;
//    private int hour;
//    private int minute;
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {//日期
        this.year = year;
        this.month = monthOfYear;
        this.day = dayOfMonth;
        Time t=new Time();
        t.setToNow();
        int hour = t.hour; // 0-23
        int minute = t.minute;
//        int second = t.second;
        TimePickerDialog pickerDialog  = new TimePickerDialog(this,this,hour+1,minute,true);
        pickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {//时间
        String hour = ""+hourOfDay;
        String min = ""+minute;
        if (hourOfDay < 10) {
            hour = "0"+hour;
        }
        if (minute < 10) {
            min = "0"+min;
        }
        delivery_info_date_tv.setText(year+"年"+month+"月"+day+"日  "+hour+":"+min);
    }
}
