import 'src/one_key_login_api.g.dart';

class EnyaOneKeyLogin {
  final _api = OneKeyLoginPluginApi();
  OneKeyLoginPluginFlutterApi callBack;

  EnyaOneKeyLogin(this.callBack) {
    OneKeyLoginPluginFlutterApi.setUp(callBack);
  }

  Future<void> init (String apiKey) {
    return _api.init(apiKey);
  }

  void startToLogin() {
    _api.startToLogin();
  }

  void quitLoginPage() {
    _api.quitLoginPage();
  }
}
