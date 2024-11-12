import SwiftUI
import ComposeApp

class AppDelegate: NSObject, UIApplicationDelegate {
    lazy var rinku = RinkuIos(deepLinkFilter: nil, deepLinkMapper: nil)

    /*func application(_ app: UIApplication, open url: URL, options: [UIApplication.OpenURLOptionsKey : Any] = [:]) -> Bool {
        rinku.onDeepLinkReceived(url: url.absoluteString)
        return true
    }

    func application(_ application: UIApplication, continue userActivity: NSUserActivity, restorationHandler: @escaping ([UIUserActivityRestoring]?) -> Void) -> Bool {
        guard userActivity.activityType == NSUserActivityTypeBrowsingWeb,
              let url = userActivity.webpageURL else {
            return false
        }

        rinku.onDeepLinkReceived(userActivity: userActivity)
        return true
    }*/
}

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate

    init() {
        MainViewControllerKt.initialise()
    }

    var body: some Scene {
        WindowGroup {
            ContentView().onOpenURL { url in
                self.appDelegate.rinku.onDeepLinkReceived(url: url.absoluteString)
            }
        }
    }
}
