import Core
import Photos
import Lottie
import SwiftUI

public struct WelcomeView {

    @State
    private var status = PHPhotoLibrary.authorizationStatus(for: .readWrite)
    private let onFinish: Command

    // MARK: - Initialize
    public init(onFinish: Command) {
        self.onFinish = onFinish
    }

    // MARK: - Private methods
    private func onDidTapAction() {
        switch status {
        case .authorized, .limited:
            onFinish.perform()
            return
        case .restricted, .denied:
            openAppSettings()
            return
        @unknown default:
            break
        }

        requestAuthorization()
    }

    private func requestAuthorization() {
        PHPhotoLibrary.requestAuthorization(for: .readWrite) { status in
            Command {
                switch status {
                case .authorized, .limited:
                    self.onFinish.perform()
                    return
                case .restricted, .denied:
                    self.status = status
                    return
                @unknown default:
                    return
                }
            }
            .dispatchedAsync(on: .main)
            .perform()
        }
    }

    private func openAppSettings() {
        let settingsURLString = UIApplication.openSettingsURLString
        guard let url = URL(string: settingsURLString), UIApplication.shared.canOpenURL(url)
        else { return }

        UIApplication.shared.open(url)
    }
}

extension WelcomeView: View {
    public var body: some View {
        Content()
    }

    private func Content() -> some View {
        VStack(spacing: 20) {
            Spacer()

            CoverView()

            TitleView()

            ButtonView()

            Spacer()
        }
    }

    private func CoverView() -> some View {
        LottieView(animation: .named("sticker", bundle: .module))
            .playing(loopMode: .loop)
            .frame(width: 144, height: 144)
    }

    private func TitleView() -> some View {
        Text("Разрешите доступ к вашим фото")
            .font(.system(size: 20, weight: .semibold))
            .multilineTextAlignment(.center)
            .foregroundStyle(.primary)
    }

    private func ButtonView() -> some View {
        let title = switch status {
        case .notDetermined: notDeterminedTitle
        case .authorized, .limited: authorizedTitle
        default: deniedTitle
        }

        return Button(action: onDidTapAction) {
            Text(title)
                .font(.system(size: 17, weight: .semibold))
                .multilineTextAlignment(.center)
                .foregroundStyle(.white)
                .padding(.horizontal, 32)
                .padding(.vertical, 12)
                .background(.blue)
                .clipShape(.rect(cornerRadius: 12))
        }
    }
}

private let deniedTitle = "Открыть Настройки"
private let authorizedTitle = "Готово"
private let notDeterminedTitle = "Разрешить"
