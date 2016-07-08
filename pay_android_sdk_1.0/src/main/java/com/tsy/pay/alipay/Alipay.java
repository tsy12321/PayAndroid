package com.tsy.pay.alipay;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;

/**
 * 支付宝支付
 * Created by tsy on 16/6/1.
 */
public class Alipay {
    private String mParams;
    private PayTask mPayTask;
    private AlipayResultCallBack mCallback;

    public static final int ERROR_RESULT = 1;   //支付结果解析错误
    public static final int ERROR_PAY = 2;  //支付失败
    public static final int ERROR_NETWORK = 3;  //网络连接错误

    public interface AlipayResultCallBack {
        void onSuccess(); //支付成功
        void onDealing();    //正在处理中 小概率事件 此时以验证服务端异步通知结果为准
        void onError(int error_code);   //支付失败
        void onCancel();    //支付取消
    }

    public Alipay(Context context, String params, AlipayResultCallBack callback) {
        mParams = params;
        mCallback = callback;
        mPayTask = new PayTask((Activity) context);
    }

    //支付
    public void doPay() {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = mPayTask.pay(mParams, true);

                final AlipayResult pay_result = new AlipayResult(result);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(mCallback == null) {
                            return;
                        }

                        if(pay_result == null) {
                            mCallback.onError(ERROR_RESULT);
                            return;
                        }

                        String resultStatus = pay_result.getResultStatus();
                        if(TextUtils.equals(resultStatus, "9000")) {    //支付成功
                            mCallback.onSuccess();
                        } else if(TextUtils.equals(resultStatus, "8000")) { //支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                            mCallback.onDealing();
                        } else if(TextUtils.equals(resultStatus, "6001")) {		//支付取消
                            mCallback.onCancel();
                        } else if(TextUtils.equals(resultStatus, "6002")) {     //网络连接出错
                            mCallback.onError(ERROR_NETWORK);
                        } else if(TextUtils.equals(resultStatus, "4000")) {        //支付错误
                            mCallback.onError(ERROR_PAY);
                        }
                    }
                });
            }
        }).start();
    }
}
