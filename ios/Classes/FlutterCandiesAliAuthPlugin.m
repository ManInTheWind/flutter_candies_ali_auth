#import "FlutterCandiesAliAuthPlugin.h"
#if __has_include(<flutter_candies_ali_auth/flutter_candies_ali_auth-Swift.h>)
#import <flutter_candies_ali_auth/flutter_candies_ali_auth-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "flutter_candies_ali_auth-Swift.h"
#endif

@implementation FlutterCandiesAliAuthPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterCandiesAliAuthPlugin registerWithRegistrar:registrar];
}
@end
