import Core
import SwiftUI
import PhotosUI
import WelcomeFeature

public final class AppCoordinator {

    private let window: UIWindow

    private var topController: UIViewController? {
        window.rootViewController
    }

    private var photoPickerController: PHPickerViewController?
    private var welcomeController: UIViewController?

    private var isAccessGranted: Bool {
        switch PHPhotoLibrary.authorizationStatus(for: .readWrite) {
        case .authorized, .limited: true
        default: false
        }
    }

    // MARK: - Initialize

    init(window: UIWindow) {
        self.window = window
    }

    // MARK: - Public methods

    func start() {
        showPhotoPicker()
//        if isAccessGranted {
            showWelcome()
//        }
    }

    // MARK: - Private methods

    private func showWelcome() {
        let onFinish = Command { [weak self] in
            self?.welcomeController?.dismiss(animated: true)
            self?.welcomeController = nil
        }
        let welcomeView = WelcomeView(onFinish: onFinish)
        let vc = UIHostingController(rootView: welcomeView)
        vc.isModalInPresentation = true
        topController?.present(vc, animated: true)
        welcomeController = vc
    }

    private func showPhotoPicker() {
        var configuration = PHPickerConfiguration(photoLibrary: .shared())
        configuration.filter = .images
        configuration.selectionLimit = 1
        let pickerViewController = PHPickerViewController(configuration: configuration)
        pickerViewController.delegate = self
        window.rootViewController = pickerViewController
        photoPickerController = pickerViewController
    }
}

extension AppCoordinator: PHPickerViewControllerDelegate {
    public func picker(
        _ picker: PHPickerViewController,
        didFinishPicking results: [PHPickerResult]
    ) {
        let itemProvider = results.first?.itemProvider
        itemProvider?.loadObject(ofClass: UIImage.self) { image, error in
            Command {
                if let image = image as? UIImage {
                    print(image)
                }
                else if let error {
                    print(error.localizedDescription)
                }
            }
            .dispatchedAsync(on: .main)
            .perform()
        }
    }
}
