package com.enya.flutter.onekeylogin.enya_one_key_login.impl

import android.app.Activity
import android.view.View
import android.widget.Toast
import com.enya.flutter.onekeylogin.enya_one_key_login.R
import com.enya.flutter.onekeylogin.enya_one_key_login.api.OneKeyLoginPluginApi
import com.enya.flutter.onekeylogin.enya_one_key_login.api.OneKeyLoginPluginFlutterApi
import com.enya.flutter.onekeylogin.enya_one_key_login.util.OneKeyLoginManager
import com.mobile.auth.gatewayauth.ResultCode

/**
 * @Description:
 * @Author: Gary
 * @CreateDate: 2024/12/6 18:35
 */
class MyOneKeyLoginApi(
    private val activity: Activity,
    private val callback: OneKeyLoginPluginFlutterApi
) :
    OneKeyLoginPluginApi {

    private var oneKeyLoginManager: OneKeyLoginManager? = null

    /**
     * 一键登录相关回调
     */
    private val oneKeyLoginCallBack: OneKeyLoginManager.IOneKeyLoginCallBack = object :
        OneKeyLoginManager.IOneKeyLoginCallBack {
        override fun onStartOneKeyLogin() {
//            get<EnyaCpEventManager>().track(CpCommonConstants.ONE_KEY_LOGIN_CLICK)
//            postDelayed({
//                oneKeyLoginManager?.dismissLoading()
//                CommonToastManager.show("获取信息超时,请稍后重试")
//            }, 20000)
        }

        override fun onTokenResult(code: String, token: String) {
            callback.onTokenResult(code, token) {}
        }

        override fun onUserChecked(isChecked: Boolean) {
            callback.onTokenResult(ResultCode.CODE_ERROR_USER_CHECKBOX, isChecked.toString()) {}
        }

        override fun onViewCreated(view: View) {
            view.findViewById<View>(R.id.wxLogin)
                .setOnClickListener {
                    if (oneKeyLoginManager?.queryCheckBoxIsChecked() == true) {
                        callback.onThirdLogin(0) {}
                    } else {
                        Toast.makeText(activity, "同意服务条款才可以登录", Toast.LENGTH_SHORT).show()
                    }

                }
            view.findViewById<View>(R.id.loginPhone).setOnClickListener {
                if (oneKeyLoginManager?.queryCheckBoxIsChecked() == true) {
                    callback.onThirdLogin(1) {}
                } else {
                    Toast.makeText(activity, "同意服务条款才可以登录", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun initOneKeyLogin(apiKey: String) {

    }

    override fun init(apiKey: String, callback: (Result<Unit>) -> Unit) {
        oneKeyLoginManager = OneKeyLoginManager(activity).apply {
            initSdk(apiKey, false)
            initLayout()
            setIOneKeyLoginCallBack(oneKeyLoginCallBack)
            callback.invoke(Result.success(Unit))
        }
    }

    override fun startToLogin(callback: (Result<Unit>) -> Unit) {
        oneKeyLoginManager?.oneKeyLogin()
    }

    override fun quitLoginPage(callback: (Result<Unit>) -> Unit) {
        oneKeyLoginManager?.quitLoginPage()
    }
}