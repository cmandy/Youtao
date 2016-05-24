package com.youyou.uumall.base;

import android.os.Environment;

import java.io.File;

/**
 * Created by len on 2015/11/26.
 */
public class BaseConstants {

    public static final class connection {
        public static final int TIMEOUT = 10 * 1000; //10秒
        //        接口地址

//        public static final String ROOT_URL = "http://58.96.188.197";
      public static final String ROOT_URL = "http://120.26.75.225:8090";  //生产环境
//		public static final String ROOT_URL = "http://pptest.youyoumob.com"; //测试环境

        public static final String API_VERSION = "/uumall/itf";

        public static final String API_ROOT_URL = ROOT_URL + API_VERSION;


    }

    public static final class path {
        /**
         * 应用的目录
         */
        public static final String APP_DIR = Environment
                .getExternalStorageDirectory()
                + File.separator
                + "SP";

        public static final String FILE_DIR = APP_DIR + File.separator
                + "file" + File.separator;//一般文件存放
        public static final String IMAGE_DIR = "SP" + File.separator
                + "image" + File.separator; //存放缓存图片
        public static final String PHOTO_DIR = APP_DIR + File.separator
                + "photo" + File.separator; //存放发送的图片
        public static final String STICKER_DIR = APP_DIR + File.separator
                + "sticker" + File.separator; //存放贴纸图片
        public static final String LOG_DIR = APP_DIR + File.separator
                + "log"; //日志存放
    }

    public static final class preferencesFiled {
        public static final String ACCESS_TOKEN = "AccessToken"; //登录用户token
        public static final String PP_USER_ID = "pp_user_id"; //用户ID
        public static final String PP_USER_AVATAR = "pp_user_avatar"; //用头头像地址
        public static final String PP_USER_NICK = "pp_user_nick"; //用头昵称

        public static final String LAT = "lat";
        public static final String LNG = "lng";
        public static final String ADDRESS = "address";
        public static final String ISFIRST_INSTALL = "first_install";
        public static final String ISFIRST_CLICK_BUILD = "first_build";
        public static final String ISFIRST_CLICK_CITY_SELECT = "first_city_select";
        public static final String DEFAULT_COUNTRY = "defaultCountry";
        public static final String DICT_LIST = "dictList";
        public static final String GOODS_ID = "goodsId";//商品id
    }

    public static final class ImportantField{
        public static final String APP_ID = "wxdfc1314988fb84a9"; //登录用户token
    }
}