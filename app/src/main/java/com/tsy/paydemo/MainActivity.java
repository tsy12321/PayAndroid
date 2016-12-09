package com.tsy.paydemo;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tsy.sdk.pay.PayUtils;
import com.tsy.sdk.pay.alipay.Alipay;
import com.tsy.sdk.pay.weixin.WXPay;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editWXParam, editAlipayParam;
    private Button btnWXPay, btnAlipay, btnWXClear, btnWXPaste, btnAliPayClear, btnAliPayPaste, btnGetIp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editAlipayParam = (EditText)findViewById(R.id.editAlipayParam);
        editWXParam = (EditText)findViewById(R.id.editWXParam);
        btnAlipay = (Button)findViewById(R.id.btnAliPay);
        btnWXPay = (Button)findViewById(R.id.btnWXPay);
        btnWXClear = (Button)findViewById(R.id.btnWXClear);
        btnWXPaste = (Button)findViewById(R.id.btnWXPaste);
        btnAliPayClear = (Button)findViewById(R.id.btnAliPayClear);
        btnAliPayPaste = (Button)findViewById(R.id.btnAliPayPaste);
        btnGetIp = (Button)findViewById(R.id.btnGetIp);

        btnAlipay.setOnClickListener(this);
        btnWXPay.setOnClickListener(this);
        btnWXClear.setOnClickListener(this);
        btnWXPaste.setOnClickListener(this);
        btnAliPayClear.setOnClickListener(this);
        btnAliPayPaste.setOnClickListener(this);
        btnGetIp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnWXPay:
                String wx_pay_param = editWXParam.getText().toString();
                if(TextUtils.isEmpty(wx_pay_param)) {
                    Toast.makeText(getApplication(), "请输入参数", Toast.LENGTH_SHORT).show();
                    return;
                }
                doWXPay(wx_pay_param);
                break;

            case R.id.btnWXClear:
                editWXParam.setText("");
                break;

            case R.id.btnWXPaste:
                ClipboardManager cbm=(ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                editWXParam.setText(cbm.getText());
                break;

            case R.id.btnAliPay:
                String alipay_pay_param = editAlipayParam.getText().toString();
                if(TextUtils.isEmpty(alipay_pay_param)) {
                    Toast.makeText(getApplication(), "请输入参数", Toast.LENGTH_SHORT).show();
                    return;
                }
                doAlipay(alipay_pay_param);
                break;

            case R.id.btnAliPayClear:
                editAlipayParam.setText("");
                break;

            case R.id.btnAliPayPaste:
                ClipboardManager cbm2=(ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                editAlipayParam.setText(cbm2.getText());
                break;

            case R.id.btnGetIp:
                String ip = PayUtils.getIpAddress();
                if(ip != null) {
                    Toast.makeText(getApplication(), ip, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplication(), "获取ip失败", Toast.LENGTH_SHORT).show();
                }

            default:
                break;
        }
    }

    /**
     * 支付宝支付
     * @param pay_param 支付服务生成的支付参数
     */
    private void doAlipay(String pay_param) {
        new Alipay(this, pay_param, new Alipay.AlipayResultCallBack() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplication(), "支付成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDealing() {
                Toast.makeText(getApplication(), "支付处理中...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int error_code) {
                switch (error_code) {
                    case Alipay.ERROR_RESULT:
                        Toast.makeText(getApplication(), "支付失败:支付结果解析错误", Toast.LENGTH_SHORT).show();
                        break;

                    case Alipay.ERROR_NETWORK:
                        Toast.makeText(getApplication(), "支付失败:网络连接错误", Toast.LENGTH_SHORT).show();
                        break;

                    case Alipay.ERROR_PAY:
                        Toast.makeText(getApplication(), "支付错误:支付码支付失败", Toast.LENGTH_SHORT).show();
                        break;

                    default:
                        Toast.makeText(getApplication(), "支付错误", Toast.LENGTH_SHORT).show();
                        break;
                }

            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplication(), "支付取消", Toast.LENGTH_SHORT).show();
            }
        }).doPay();
    }

    /**
     * 微信支付
     * @param pay_param 支付服务生成的支付参数
     */
    private void doWXPay(String pay_param) {
        String wx_appid = "wx6b69bdbf2adca4f8";     //替换为自己的appid
        WXPay.init(getApplicationContext(), wx_appid);      //要在支付前调用
        WXPay.getInstance().doPay(pay_param, new WXPay.WXPayResultCallBack() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplication(), "支付成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int error_code) {
                switch (error_code) {
                    case WXPay.NO_OR_LOW_WX:
                        Toast.makeText(getApplication(), "未安装微信或微信版本过低", Toast.LENGTH_SHORT).show();
                        break;

                    case WXPay.ERROR_PAY_PARAM:
                        Toast.makeText(getApplication(), "参数错误", Toast.LENGTH_SHORT).show();
                        break;

                    case WXPay.ERROR_PAY:
                        Toast.makeText(getApplication(), "支付失败", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplication(), "支付取消", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
