import UIKit

public final class AppDelegate: UIResponder, UIApplicationDelegate {
    
    // MARK: - UISceneSession Lifecycle

    public func application(
        _ application: UIApplication,
        configurationForConnecting connectingSceneSession: UISceneSession,
        options: UIScene.ConnectionOptions
    ) -> UISceneConfiguration {
        guard connectingSceneSession.role == .windowApplication
        else { fatalError("Unhandled scene role \(connectingSceneSession.role)") }

        let config = UISceneConfiguration(name: nil, sessionRole: connectingSceneSession.role)
        config.delegateClass = SceneDelegate.self
        return config
    }
}
