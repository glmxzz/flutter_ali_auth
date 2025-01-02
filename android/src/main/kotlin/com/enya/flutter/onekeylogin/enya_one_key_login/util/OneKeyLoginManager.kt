package com.enya.flutter.onekeylogin.enya_one_key_login.util

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.enya.flutter.onekeylogin.enya_one_key_login.R
import com.mobile.auth.gatewayauth.AuthRegisterXmlConfig
import com.mobile.auth.gatewayauth.AuthUIConfig
import com.mobile.auth.gatewayauth.PhoneNumberAuthHelper
import com.mobile.auth.gatewayauth.PreLoginResultListener
import com.mobile.auth.gatewayauth.ResultCode
import com.mobile.auth.gatewayauth.TokenResultListener
import com.mobile.auth.gatewayauth.model.TokenRet
import com.mobile.auth.gatewayauth.ui.AbstractPnsViewDelegate
import org.json.JSONException
import org.json.JSONObject

/**
 * @author wfx
 * date 2021.09.02
 * Description:
 */
class OneKeyLoginManager(private val context: Context) {
    companion object {
        var authUiConfig: AuthUIConfig? = null
    }

    private var mPhoneNumberAuthHelper: PhoneNumberAuthHelper? = null
    private var iOneKeyLoginCallBack: IOneKeyLoginCallBack? = null
    private val timeOut = 5000
    private var isInit = true

    fun initSdk(apiKey: String, needPrefetch: Boolean) {
        val mTokenResultListener: TokenResultListener = object : TokenResultListener {
            override fun onTokenSuccess(s: String) {
                Log.i("OneKeyLoginManagerHost", s);
                val tokenRet: TokenRet
                try {
                    tokenRet = TokenRet.fromJson(s)
                    val token = if (tokenRet.token.isNullOrBlank()) "" else tokenRet.token
                    iOneKeyLoginCallBack?.onTokenResult(tokenRet.code, token)
                    //                    BuglyUtils.INSTANCE.uploadCustomException(new Exception("一键登录###成功回调### code=" + tokenRet.getCode()));
                    if (ResultCode.CODE_START_AUTHPAGE_SUCCESS == tokenRet.code) {  //拉取授权页
//                        iOneKeyLoginCallBack?.onOneKeyPageSuccess()
                    } else if (ResultCode.CODE_ERROR_START_AUTHPAGE_FAIL == tokenRet.code) {  //拉授权页失败
//                        iOneKeyLoginCallBack?.onOneKeyPageFailed()
                    } else if (ResultCode.CODE_SUCCESS == tokenRet.code) {   //获取token
//                        iOneKeyLoginCallBack?.onTokenSuccess(tokenRet.token)
                    } else if (ResultCode.CODE_ERROR_ENV_CHECK_SUCCESS == tokenRet.code) {  //检测客户端环境 预取号
                        if (needPrefetch) {
                            accelerateLoginPage()
                        }
                    } else if (ResultCode.CODE_ERROR_FUNCTION_TIME_OUT == tokenRet.code) {  //超时,可能是拉取授权页,也可能是获取token等其他情况
                        if (isInit) {
//                            iOneKeyLoginCallBack?.onOneKeyPageFailed()
                        } else {
//                            iOneKeyLoginCallBack?.onTokenFailed()
                        }
                    } else {
                        if (isInit) {
//                            iOneKeyLoginCallBack?.onOneKeyPageFailed()
                        } else {
//                            iOneKeyLoginCallBack?.onTokenFailed()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                isInit = false
            }

            override fun onTokenFailed(s: String) {
                val tokenRet: TokenRet
                try {
                    dismissLoading()
                    tokenRet = TokenRet.fromJson(s)
                    iOneKeyLoginCallBack?.onTokenResult(tokenRet.code, "")
                    //                    BuglyUtils.INSTANCE.uploadCustomException(new Exception("一键登录###失败回调### code=" + tokenRet.getCode()));
                    if (iOneKeyLoginCallBack != null) {
                        if (ResultCode.CODE_ERROR_USER_CANCEL == tokenRet.code) {  //取消操作
//                            iOneKeyLoginCallBack!!.onOneKeyPageFailed()
                        } else if (ResultCode.CODE_ERROR_START_AUTHPAGE_FAIL == tokenRet.code) {  //拉取授权页失败
//                            iOneKeyLoginCallBack!!.onOneKeyPageFailed()
                        } else if (ResultCode.CODE_GET_TOKEN_FAIL == tokenRet.code) {  //获取token失败
//                            iOneKeyLoginCallBack!!.onTokenFailed()
                        } else if (ResultCode.CODE_ERROR_FUNCTION_TIME_OUT == tokenRet.code) {
                            if (isInit) {
//                                iOneKeyLoginCallBack!!.onOneKeyPageFailed()
                            } else {
//                                iOneKeyLoginCallBack!!.onTokenFailed()
                            }
                        } else {
                            if (isInit) {
//                                iOneKeyLoginCallBack!!.onOneKeyPageFailed()
                            } else {
//                                iOneKeyLoginCallBack!!.onTokenFailed()
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                isInit = false
            }
        }
        mPhoneNumberAuthHelper =
            PhoneNumberAuthHelper.getInstance(context, mTokenResultListener).also {
                it.reporter.setLoggerEnable(true)
                it?.setAuthSDKInfo(apiKey)
            }

        //        if (needPrefetch) {
//            mPhoneNumberAuthHelper.checkEnvAvailable(PhoneNumberAuthHelper.SERVICE_TYPE_LOGIN);
//        }
    }

    fun initLayout() {
        mPhoneNumberAuthHelper!!.setUIClickListener { code: String, context: Context, jsonString: String? ->
            if (TextUtils.isEmpty(jsonString)) {
                return@setUIClickListener
            }
            val jsonObj: JSONObject = try {
                JSONObject(jsonString)
            } catch (e: JSONException) {
                JSONObject()
            }
            if (ResultCode.CODE_ERROR_USER_PROTOCOL_CONTROL == code) {   //协议点击
                mPhoneNumberAuthHelper!!.setAuthUIConfig(
                    AuthUIConfig.Builder()
                        .setLightColor(true) //状态栏字体颜色
                        .setStatusBarUIFlag(View.SYSTEM_UI_FLAG_FULLSCREEN)
                        .setWebNavColor(Color.WHITE)
                        .setWebViewStatusBarColor(Color.WHITE)
                        .setWebNavTextColor(context.resources.getColor(R.color.color_363C54))
                        .setWebNavTextSizeDp(18)
                        .setWebSupportedJavascript(true)
                        .setWebNavReturnImgPath("login_one_key_web_backimg")
                        .create()
                )
            } else if (ResultCode.CODE_ERROR_USER_CHECKBOX == code) {
                if (iOneKeyLoginCallBack != null) {
                    iOneKeyLoginCallBack!!.onUserChecked(jsonObj.optBoolean("isChecked"))
                }
            } else if (ResultCode.CODE_ERROR_USER_LOGIN_BTN == code) {
                if (!jsonObj.optBoolean("isChecked")) {
                    Toast.makeText(context, "同意服务条款才可以登录", Toast.LENGTH_SHORT).show()
                } else {
                    iOneKeyLoginCallBack?.onStartOneKeyLogin()
                }
            }
        }
        mPhoneNumberAuthHelper!!.removeAuthRegisterXmlConfig()
        mPhoneNumberAuthHelper!!.removeAuthRegisterViewConfig()
        mPhoneNumberAuthHelper!!.addAuthRegisterXmlConfig(
            AuthRegisterXmlConfig.Builder()
                .setLayout(R.layout.layout_onekey_login, object : AbstractPnsViewDelegate() {
                    override fun onViewCreated(view: View) {
                        iOneKeyLoginCallBack?.onViewCreated(view)
                    }
                })
                .build()
        )
        mPhoneNumberAuthHelper!!.setAuthUIConfig(
            authUiConfig ?: AuthUIConfig.Builder()
                .setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT)

                .setSwitchAccHidden(true) //切换方式隐藏
                .setPrivacyState(false) //隐私条款是否默认勾选
                .setLightColor(false) //状态栏字体颜色
                .setNavHidden(true)
                .setStatusBarColor(Color.TRANSPARENT)
                .setStatusBarUIFlag(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

                //logo
                .setLogoHidden(true)

                .setSloganText("本机号登录")
                .setSloganTextSizeDp(24)
                .setSloganHidden(false)
                .setSloganTextColor(Color.WHITE)

                //手机号码
                .setNumberColor(Color.WHITE)
                .setNumberSizeDp(24)
                .setNumFieldOffsetY(254)

                //一键登录按钮
                .setLogBtnOffsetY(301)
                .setLogBtnText("一键登录")
                .setLogBtnTextColor(context.resources.getColor(R.color.color_363C54))
                .setLogBtnTextSizeDp(17)
                .setLogBtnBackgroundPath("background_login_onekey")
                .setLogBtnHeight(48)
                .setLogBtnMarginLeftAndRight(54)
                .setLogBtnToastHidden(true)



                .setPrivacyTextSize(11)
                .setPrivacyBefore("我已阅读并同意")
                .setAppPrivacyOne("《用户服务条款》", "baidu")
                .setAppPrivacyTwo("《隐私保护指引》", "baidu")
                .setAppPrivacyColor(context.resources.getColor(R.color.color_F4F5F7), Color.WHITE)
                .setCheckedImgPath("btn_checked")
                .setUncheckedImgPath("btn_unchecked")
                .setCheckBoxWidth(22)
                .setCheckBoxHeight(22)
                .setPrivacyOffsetY_B(38)
                .setVendorPrivacyPrefix("《")
                .setVendorPrivacySuffix("》")
                .setPrivacyConectTexts(arrayOf("", "和"))
                .create()
        )
    }

    /**
     * 拉取授权页
     */
    fun oneKeyLogin() {
        mPhoneNumberAuthHelper!!.getLoginToken(context, timeOut)
    }

    fun setProtocolChecked(flag: Boolean) {
        mPhoneNumberAuthHelper?.setProtocolChecked(flag)
    }

    /**
     * 预取号
     */
    fun accelerateLoginPage() {
        mPhoneNumberAuthHelper!!.accelerateLoginPage(timeOut, object : PreLoginResultListener {
            override fun onTokenSuccess(s: String) {
//                MyLog.info("预取号成功: $s")
            }

            override fun onTokenFailed(s: String, s1: String) {
//                MyLog.info("预取号失败: $s1")
            }
        })
    }

    fun dismissLoading() {
        if (mPhoneNumberAuthHelper != null) {
            mPhoneNumberAuthHelper!!.hideLoginLoading()
        }
    }

    fun destroy() {
        if (mPhoneNumberAuthHelper != null) {
            mPhoneNumberAuthHelper!!.setAuthListener(null)
            mPhoneNumberAuthHelper = null
        }
    }

    fun quitLoginPage() {
        mPhoneNumberAuthHelper?.quitLoginPage()
    }

    fun queryCheckBoxIsChecked(): Boolean {
        return mPhoneNumberAuthHelper?.queryCheckBoxIsChecked() ?: false
    }

    fun setIOneKeyLoginCallBack(iOneKeyLoginCallBack: IOneKeyLoginCallBack?) {
        this.iOneKeyLoginCallBack = iOneKeyLoginCallBack
    }

    interface IOneKeyLoginCallBack {
        fun onStartOneKeyLogin()
        fun onTokenResult(code: String, token: String)
        fun onUserChecked(isChecked: Boolean)
        fun onViewCreated(view: View)
    }
}