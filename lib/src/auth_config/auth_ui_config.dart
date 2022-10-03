import 'part_ui_config.dart';

abstract class AuthUIConfig {
  LogoConfig? logoConfig;
  SloganConfig? sloganConfig;
  PhoneNumberConfig? phoneNumberConfig;
  LoginButtonConfig? loginButtonConfig;
  ChangeButtonConfig? changeButtonConfig;
  CheckBoxConfig? checkBoxConfig;
  PrivacyConfig? privacyConfig;

  AuthUIConfig({
    this.logoConfig,
    this.sloganConfig,
    this.phoneNumberConfig,
    this.loginButtonConfig,
    this.changeButtonConfig,
    this.checkBoxConfig,
    this.privacyConfig,
  });

  MapWithStringKey toJson();
}
