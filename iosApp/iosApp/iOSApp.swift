import SwiftUI
import ComposeApp

class AppDelegate: NSObject, UIApplicationDelegate {
    lazy var rinku = RinkuIos(deepLinkFilter: nil, deepLinkMapper: nil)
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
