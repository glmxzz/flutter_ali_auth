package com.enya.flutter.onekeylogin.enya_one_key_login

import com.enya.flutter.onekeylogin.enya_one_key_login.api.OneKeyLoginPluginApi
import com.enya.flutter.onekeylogin.enya_one_key_login.api.OneKeyLoginPluginFlutterApi
import com.enya.flutter.onekeylogin.enya_one_key_login.impl.MyOneKeyLoginApi
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger

/** EnyaOneKeyLoginPlugin */
class EnyaOneKeyLoginPlugin : FlutterPlugin, ActivityAware {
    private lateinit var binaryMessagener: BinaryMessenger

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        binaryMessagener = flutterPluginBinding.binaryMessenger
    }

    override fun onDetachedFromEngine(p0: FlutterPlugin.FlutterPluginBinding) {
        OneKeyLoginPluginApi.setUp(binaryMessagener, null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        OneKeyLoginPluginApi.setUp(
            binaryMessagener,
            MyOneKeyLoginApi(binding.activity, OneKeyLoginPluginFlutterApi(binaryMessagener))
        )
    }

    override fun onDetachedFromActivityForConfigChanges() {
    }

    override fun onReattachedToActivityForConfigChanges(p0: ActivityPluginBinding) {
    }

    override fun onDetachedFromActivity() {
    }

}
