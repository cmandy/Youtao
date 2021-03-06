package com.youyou.uumall.ui;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.youyou.uumall.R;
import com.youyou.uumall.base.BaseActivity;
import com.youyou.uumall.base.BaseBusiness;
import com.youyou.uumall.base.BaseConstants;
import com.youyou.uumall.bean.Response;
import com.youyou.uumall.business.LoginBiz;
import com.youyou.uumall.business.RegisterBiz;
import com.youyou.uumall.event.MineTriggerEvent;
import com.youyou.uumall.event.MobileBindingEvent;
import com.youyou.uumall.event.ShopCartTriggerEvent;
import com.youyou.uumall.event.ShopCartUpdateEvent;
import com.youyou.uumall.event.WxLoginEvent;
import com.youyou.uumall.model.UserInfoBean;
import com.youyou.uumall.utils.MyUtils;
import com.youyou.uumall.utils.UserUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@EActivity(R.layout.activity_login)
public class LoginActivity extends BaseActivity implements BaseBusiness.ObjectCallbackInterface, BaseBusiness.ArrayListCallbackInterface {

    @ViewById
    EditText login_phone_et;
    @ViewById
    EditText login_pwd_et;
    @ViewById
    LinearLayout login_pb;

    @Bean
    LoginBiz loginBiz;
    @Bean
    RegisterBiz registerBiz;
    @Bean
    UserUtils userUtils;
    private IWXAPI api;
    Map<String, String> paramMap;
    private String userPhone;
    private String userPwd;

    @AfterViews
    void afterViews() {
        api = WXAPIFactory.createWXAPI(this, BaseConstants.ImportantField.APP_ID, false);
        api.registerApp(BaseConstants.ImportantField.APP_ID);
        loginBiz.setObjectCallbackInterface(this);
        registerBiz.setArrayListCallbackInterface(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (statusView != null) {
            ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
            decorView.removeView(statusView);
//            statusView.setBackgroundColor(getResources().getColor(R.color.white));
        }
    }

    @Override
    protected void registerEvent() {
        super.registerEvent();
        eventBus.register(this);
    }

    @Override
    protected void unRegisterEvent() {
        super.unRegisterEvent();
        eventBus.unregister(this);
    }

    @Subscribe(sticky = false, threadMode = ThreadMode.BACKGROUND)
    public void onEventBackgroundThread(WxLoginEvent event) {//eventBus接收数据,并后台调用
        String openId = event.getEvent();
        MyUtils.savePara(this,BaseConstants.preferencesFiled.OPEN_ID,openId);
        registerBiz.wechatLogin(openId, "", MyUtils.getPara(BaseConstants.preferencesFiled.DEVICE_TOKEN, this), "3");
//        registerBiz.wechatLogin("1", "", MyUtils.getPara(BaseConstants.preferencesFiled.DEVICE_TOKEN, this), "3")
    }

    private long firstTime;

    @Click
    public void login_weixin_ll() {

//        if (System.currentTimeMillis() - firstTime > 10000) {
            if (!api.isWXAppInstalled()) {
                showToastShort("请先安装微信");
                return;
            }
            login_pb.setVisibility(View.VISIBLE);
            SendAuth.Req req = new SendAuth.Req();//请求CODE
            req.scope = "snsapi_userinfo";
            req.state = "wechat_login";
            api.sendReq(req);
//            firstTime = System.currentTimeMillis();
//        }
//        progressBar.show();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        int action = event.getAction();
        if (keyCode == KeyEvent.KEYCODE_BACK && action == KeyEvent.ACTION_DOWN) {//首先做双层判断，如果点击是的回退键&&并且动作是按压的时候，触发操作
            if (login_pb.isShown()) {
            return false;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onStop() {
        super.onStop();
        login_pb.setVisibility(View.GONE);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (login_pb.isShown()) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Click
    void login_login_bt() {
        userPhone = login_phone_et.getText().toString();
        userPwd = login_pwd_et.getText().toString();
        if (TextUtils.isEmpty(userPhone)) {
            showToastShort("手机号不能为空");
            return;
        }
        if (TextUtils.isEmpty(userPwd)) {
            showToastShort("密码不能为空");
            return;
        }
        String deviceToken = MyUtils.getPara(BaseConstants.preferencesFiled.DEVICE_TOKEN, this);
        loginBiz.userLogin(userPhone, userPwd, deviceToken);
    }

    @Click
    void login_forgetpwd_tv() {
        RetrievePasswordActivity_.intent(this).start();
//        overridePendingTransition(R.anim.from_right_enter, R.anim.anim_none);
    }

//    @Click
//    void login_cancel() {
//        finish();
//        overridePendingTransition(R.anim.anim_none, R.anim.to_center_exit);
//    }

    @Click
    void register() {
        RegisterActivity_.intent(this).start();
//        finish();
//        overridePendingTransition(R.anim.from_right_enter, R.anim.anim_none);
    }


    @UiThread
    @Override
    public void objectCallBack(int type, Object t) {
        if (type == LoginBiz.USER_LOGIN) {
            Response response = (Response) t;
            if (response != null) {
                if (response.code == 0 && TextUtils.equals(response.msg, "请求成功")) {
                    MyUtils.savePara(getApplicationContext(),BaseConstants.preferencesFiled.USER_INFO,userPhone+","+userPwd);
                    ArrayList list = (ArrayList) response.data;
                    UserInfoBean bean = (UserInfoBean) list.get(0);
                    userUtils.saveUserId(bean.mobile);
                    eventBus.post(new MineTriggerEvent());
                    eventBus.postSticky(new ShopCartUpdateEvent());
                    eventBus.post(new ShopCartTriggerEvent());
//                    showToastShort("登录成功");
                    finish();
                    overridePendingTransition(R.anim.anim_none,R.anim.from_top_exit);
                } else {
                    showToastShort(response.msg);
                }
            }
        }
    }

    @UiThread
    @Override
    public void arrayCallBack(int type, List<? extends Object> arrayList) {
        if (RegisterBiz.WECHAT_LOGIN == type) {
            final UserInfoBean bean = (UserInfoBean) arrayList.get(0);
//            userUtils.saveUserId(bean.userId);
//            log.e(bean.toString());
//            showToastShort("注册成功");
            if (TextUtils.isEmpty(bean.mobile)) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setTitle(R.string.dialog_bind_title);
//                builder.setMessage(R.string.dialog_bind_message);
//                builder.setPositiveButton(R.string.dialog_bind_pos, null);
//                builder.setNegativeButton(R.string.dialog_bind_neg, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
                eventBus.postSticky(new MobileBindingEvent(bean.openId));
                MobileBindingActivity_.intent(LoginActivity.this).start();
                finish();
                overridePendingTransition(R.anim.anim_none,R.anim.from_top_exit);
//                        overridePendingTransition(R.anim.from_right_enter, R.anim.anim_none);
//                    }
//                });
//                builder.show();
            } else {
                eventBus.post(new ShopCartTriggerEvent());
                eventBus.post(new MineTriggerEvent());
                eventBus.postSticky(new ShopCartUpdateEvent());
                finish();
                overridePendingTransition(0,0);
//                MainActivity_.intent(LoginActivity.this).start();
            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_none,R.anim.from_top_exit);
    }
}
