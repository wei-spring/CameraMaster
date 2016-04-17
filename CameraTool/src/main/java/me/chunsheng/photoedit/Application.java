package me.chunsheng.photoedit;


import com.baidu.autoupdatesdk.BDAutoUpdateSDK;
import com.baidu.autoupdatesdk.UICheckUpdateCallback;
import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;

import ly.img.android.ImgLySdk;


public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ImgLySdk.init(this);
        try {
            PushAgent mPushAgent = PushAgent.getInstance(this);
            mPushAgent.enable();
            mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SDK_ENABLE);
            mPushAgent.setNoDisturbMode(0, 0, 0, 0);

            BDAutoUpdateSDK.uiUpdateAction(this, new MyUICheckUpdateCallback());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private class MyUICheckUpdateCallback implements UICheckUpdateCallback {

        @Override
        public void onCheckComplete() {
        }

    }
}
