package com.sean.rao.ali_auth;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.hjq.toast.CustomToast;
import com.hjq.toast.ToastStrategy;
import com.hjq.toast.Toaster;
import com.hjq.toast.config.IToast;
import com.hjq.toast.config.IToastStyle;
import com.mobile.auth.gatewayauth.PhoneNumberAuthHelper;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import util.OneKeyLoginManager;

import com.sean.rao.ali_auth.auth.TestActivity;
import com.sean.rao.ali_auth.common.LoginParams;
import com.sean.rao.ali_auth.login.OneKeyLoginPublic;
import com.sean.rao.ali_auth.utils.UtilTool;

/** AliAuthPlugin */
public class AliAuthPlugin extends FlutterActivity implements FlutterPlugin, ActivityAware, MethodCallHandler, EventChannel.StreamHandler {
  private static final String TAG = "ali_auth 一键登录插件";

  private Context mContext;
  private Activity mActivity;
  /// The MethodChannel that will the communication between Flutter and native Android
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  public static EventChannel.EventSink _events;

  private static final String METHOD_CHANNEL = "ali_auth";
  private static final String EVENT_CHANNEL = "ali_auth/event";

  /**
   * 延时登录
   */
  private OneKeyLoginPublic oneKeyLoginPublic;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), METHOD_CHANNEL);

    // 原生通讯
    EventChannel eventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), EVENT_CHANNEL);
    eventChannel.setStreamHandler(this);
//    // Activity
//    flutterView.getLookupKeyForAsset("images/ic_launcher.png");
//    // Fragment
//    (FlutterView) getView().getLookupKeyForAsset("images/ic_launcher.png");
//    // 通用
//    FlutterMain.getLookupKeyForAsset("images/ic_launcher.png");

    channel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    switch (call.method){
      case "getPlatformVersion":
        result.success("当前Android信息：" + android.os.Build.VERSION.RELEASE);
        break;
      case "getCurrentCarrierName":
        // CMCC(移动)、CUCC(联通)、CTCC(电信)
        String carrierName = "获取失败";
        if (oneKeyLoginPublic == null) {
          PhoneNumberAuthHelper phoneNumberAuthHelper = PhoneNumberAuthHelper.getInstance(mActivity, null);
          carrierName = phoneNumberAuthHelper.getCurrentCarrierName();
        } else {
          carrierName = oneKeyLoginPublic.getCurrentCarrierName();
        }
        if (carrierName.contains("CMCC")) {
          carrierName = "中国移动";
        } else if (carrierName.contains("CUCC")) {
          carrierName = "中国联通";
        } else if (carrierName.contains("CTCC")) {
          carrierName = "中国电信";
        }
        result.success(carrierName);
        break;
      case "initSdk":
//        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(call.arguments));
//        if (!jsonObject.getBooleanValue("isHideToast")) {
//          Toaster.init(mActivity.getApplication(), new ToastStrategy(){
//            @RequiresApi(api = Build.VERSION_CODES.N)
//            @Override
//            public IToast createToast(IToastStyle<?> style) {
//              IToast toast = super.createToast(style);
//              CustomToast customToast = ((CustomToast) toast);
//              // 设置 Toast 动画效果
//              //customToast.setAnimationsId(R.anim.xxx);
//              // 设置短 Toast 的显示时长（默认是 2000 毫秒）
//              customToast.setShortDuration(jsonObject.getIntValue("toastDelay") * 1000);
//              if (UtilTool.dataStatus(jsonObject, "toastPositionMode")) {
//                String mode = jsonObject.getString("toastPositionMode");
//                switch (mode){
//                  case "top":
//                    customToast.setGravity(Gravity.TOP, 0, jsonObject.getIntValue("marginTop") + 10);
//                    break;
//                  case "bottom":
//                    customToast.setGravity(Gravity.BOTTOM, 0, jsonObject.getIntValue("marginBottom") + 10);
//                    break;
//                  default:
//                    customToast.setGravity(Gravity.CENTER, 0, 0);
//                    break;
//                }
//              }
//              View view = customToast.getView();
//              // 设置背景颜色
//              view.setBackgroundColor(Color.parseColor(jsonObject.getString("toastBackground")));
//              // 设置内边剧
//              view.setPadding(
//                      jsonObject.getIntValue("toastPadding"),
//                      jsonObject.getIntValue("toastPadding"),
//                      jsonObject.getIntValue("toastPadding"),
//                      jsonObject.getIntValue("toastPadding")
//              );
//              // 重置view样式
//              customToast.setView(view);
//              return toast;
//            }
//          });
//        }
//
//        if (_events == null) {
//          result.error("500001", "请先对插件进行监听！", null);
//        } else {
//          boolean isDelay = jsonObject.getBoolean("isDelay");
//          /// 判断是否初始化过或者是否是同步登录，如果是将进行再次初始化
//          if (oneKeyLoginPublic == null || !isDelay) {
            OneKeyLoginManager oneKeyLoginManager =new OneKeyLoginManager(mContext);
        oneKeyLoginManager.initSdk("rrpky/r8QtcrKmI/9KN1NU/wzr+Gz2Tdb6RXViG3CtsJ5sR4U4exSulQFoJSUkEDC1g7shtLCYhHS2i+psGS+y7x07u1aPE7BstcdcmdirkA7kcSwU0tC88r6FZMrxUPJx6VSxvDOmwxMBlTWeuQHaswj41VpreJdJo4SNrazdvU08nxoBCw4w5PvvkM8OtSH92yWKu9o6JuUHohObSfvMS/YoUDVdrvXBjimD08oXuyaqSfiUibVfHlrQr/WB6TAyQZyU+7Q72y+NuIytAWh8CWfXLjC0k9kGwg5O16uLm1U8CY0D3erg==", false);
        oneKeyLoginManager.initLayout();
        oneKeyLoginManager.oneKeyLogin();
//            oneKeyLoginPublic = new OneKeyLoginPublic(mActivity, _events, call.arguments);
//          }
//        }
        break;
      case "login":
        if (oneKeyLoginPublic != null) {
          oneKeyLoginPublic.startLogin(LoginParams.jsonObject.getIntValue("timeout", 5000));
        } else {
          // result.error("500002", "该接口为延时登录接口，请先初始化后再次调用该接口！", null);
          _events.success(UtilTool.resultFormatData("500003", null, ""));
        }
        break;
      case "checkEnvAvailable":
        oneKeyLoginPublic.checkEnvAvailable(2);
        break;
      case "quitPage":
        oneKeyLoginPublic.quitPage();
        break;
      default:
        result.notImplemented();
    }
  }

  @Override
  public void onListen(Object o, EventChannel.EventSink eventSink) {
    Log.d(TAG, "listen 初始化完毕！");
    String version = PhoneNumberAuthHelper.getVersion();
    eventSink.success(UtilTool.resultFormatData("500004", String.format("插件启动监听成功, 当前SDK版本: %s", version), ""));
//    if( _events == null ){
      _events = eventSink;
//    }
  }

  @Override
  public void onCancel(Object o) {
    if( _events != null){
      _events = null;
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    Log.d(TAG, "listen onDetachedFromEngine！");
    channel.setMethodCallHandler(null);
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    Log.d(TAG, "listen onAttachedToActivity！");
    mActivity = binding.getActivity();
    mContext = mActivity.getBaseContext();
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
  }

  @Override
  public void onDetachedFromActivity() {
    Log.d(TAG, "listen onDetachedFromActivity！");
    if (_events != null) {
      _events.endOfStream();
    }
    mActivity = null;
  }
}
