import Foundation

public final class Weak<T> {
    public let objectIdentifier: ObjectIdentifier
    public var didSet: (_ newValue: T?) -> Void = doNothing

    private weak var _value: AnyObject?

    public init(_ value: T? = nil) {
        _value = value.map({ $0 as AnyObject })
        objectIdentifier = value.flatMap({ ObjectIdentifier($0 as AnyObject) }) ?? nilObjectIdentifier
    }
}

extension Weak {
    public var value: T? {
        get {
            _value as? T
        }
        set {
            _value = newValue.map { $0 as AnyObject }
            didSet(_value as? T)
        }
    }

    public func matches(_ object: T) -> Bool {
        _value === (object as AnyObject)
    }

    public func matches(_ other: Weak<T>) -> Bool {
        _value === other._value
    }
}

private let nilObjectIdentifier = ObjectIdentifier(NSNull())
public func doNothing<A0>(_ a0: A0) {}
