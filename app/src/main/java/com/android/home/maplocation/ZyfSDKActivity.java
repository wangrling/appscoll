package com.android.home.maplocation;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tckj.zyfsdk.ZYFSdk;
import com.tckj.zyfsdk.entity.BaseEntity;
import com.tckj.zyfsdk.entity.CodeEntity;
import com.tckj.zyfsdk.entity.LoginEntity;
import com.tckj.zyfsdk.http.zhttp.listener.ZYFGetAuthCodeListener;
import com.tckj.zyfsdk.http.zhttp.listener.ZYFLoginListener;
import com.tckj.zyfsdk.http.zhttp.listener.ZYFRegisterListener;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 登录返回信息。
 * 11-26 00:13:31.155 19581 19581 E ZHttp   : {userPassword=123456789, userName=13691425882}
 * 11-26 00:13:31.277  1408  1429 I ActivityManager: Displayed com.android.home/.maplocation.ZyfSDKActivity: +171ms
 * 11-26 00:13:31.535  1408  1429 I Timeline: Timeline: Activity_windows_visible id: ActivityRecord{5e3ee67 u0 com.android.home/.maplocation.ZyfSDKActivity t8166} time:571202978
 * 11-26 00:13:32.264 19581 19699 E ZHttp   : responseCode:200
 * 11-26 00:13:32.288 19581 19581 I ZHttp   : stCode: 200result: {"rtState":true,"rtMsg":"登录成功","rtData":{"customerUser":{"sid":77,"userName":"13691425882",
 * "userPhone":"13691425882","userPassword":"25F9E794323B453885F5181F1B624D0B","registerTime":1543161650000,"remark":null},
 * "token":"cd70d8fb374c4d1a921392e4344180fa"}}msg:
 * 11-26 00:13:32.338 19581 19581 D ZyfSDK  : login onComplete
 * 11-26 00:13:32.338 19581 19581 D ZyfSDK  : login state success, result: com.tckj.zyfsdk.entity.LoginEntity$DataBean@3e7ee40
 */

public class ZyfSDKActivity extends Activity {
    public static String phoneNumber = "13691425882";
    public static String password = "123456789";
    public static final String TAG = "ZyfSDK";

    private boolean isRegisterSuccess = false;
    private boolean isLoginSuccess = true;

    private static String smsNumber;

    // 登录成功后服务器返回的数据。
    LoginEntity.DataBean mDataBean;

    // 登录成功后的信息。
    private int sid;
    private String userName;
    private String userPassword;

    private String registerTime;
    // 转换成为可读的日期。
    private String registerDate;

    private String token;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ZYFSdk.getInstance().login(phoneNumber, "123456789", new ZYFLoginListener() {
            @Override
            public void onComplete(LoginEntity result) {
                Log.d(TAG, "login onComplete");
                if (result.isRtState()) {
                    Log.d(TAG, "login state success, result: " + result.getRtData());

                    mDataBean = result.getRtData();
                    parseLoginResult(mDataBean);
                } else {
                    Log.d(TAG, "login state fail, result: " + result.getRtData());
                    isLoginSuccess = false;

                    // 获取验证码
                    ZYFSdk.getInstance().getAuthCode(phoneNumber, new ZYFGetAuthCodeListener() {
                        @Override
                        public void onComplete(CodeEntity result) {
                            Log.d(TAG, "getAuthCode onComplete");
                            if (result.isRtState()) {
                                Log.d(TAG, "smsNumber = " + smsNumber);
                                smsNumber = result.getRtData();
                            }
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });

                    // 设备注册
                    ZYFSdk.getInstance().register(phoneNumber, "123456789", new ZYFRegisterListener() {
                        @Override
                        public void onComplete(BaseEntity result) {
                            Log.d(TAG, "register onComplete");
                            if (result.isRtState()) {
                                Log.d(TAG, "getRtMsg = " + result.getRtMsg());
                                isRegisterSuccess = true;
                            }
                        }

                        @Override
                        public void onError(Exception e) {


                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                Log.d(TAG, "login onError");
            }
        });

        if (!isLoginSuccess && isRegisterSuccess) {
            // 设备登录
            ZYFSdk.getInstance().login(phoneNumber, password, new ZYFLoginListener() {
                @Override
                public void onComplete(LoginEntity result) {
                    Log.d(TAG, "login onComplete");
                    if (result.isRtState()) {
                        Log.d(TAG, "login data = " + result.getRtData());
                        // 设备登录成功
                        isLoginSuccess = true;
                        mDataBean = result.getRtData();
                        parseLoginResult(mDataBean);
                    }
                }

                @Override
                public void onError(Exception e) {

                }
            });
        }

        // 开始解析gson数据。
        /*

        */
    }

    private void parseLoginResult(LoginEntity.DataBean dataBean) {

        LoginEntity.DataBean.UserEntity userEntity = dataBean.getCustomerUser();
        token = dataBean.getToken();

        sid = userEntity.getSid();
        userName = userEntity.getUserName();
        userPassword = userEntity.getUserPassword();
        registerTime = userEntity.getRegisterTime();

        Log.d(TAG, "sid = " + sid);
        Log.d(TAG, "userName = " + userName);
        Log.d(TAG, "userPassword = " + userPassword);
        Log.d(TAG, "registerTime = " +registerTime);
        Log.d(TAG, "registerDate = " + getDateTimeByMilliSecond(registerTime));
        Log.d(TAG, "token = " + token);
    }

    public String getDateTimeByMilliSecond(String milliSecond) {
        Date date = new Date(Long.valueOf(milliSecond));
        SimpleDateFormat format = new SimpleDateFormat();
        String time = format.format(date);
        return time;
    }


}
