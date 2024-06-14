import SwiftUI

public final class SwiftUIView<Content: View>: UIView {

    private let view: _UIHostingView<Content>

    public init(content: Content) {
        view = _UIHostingView(rootView: content)
        super.init(frame: .zero)
        view.backgroundColor = .clear
        view.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        addSubview(view)
    }

    required init?(coder: NSCoder) {
        assertionFailure("init(coder:) has not been implemented")
        return nil
    }
}
