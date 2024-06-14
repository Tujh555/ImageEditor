import Foundation

public final class CommandWith<T> {

    // MARK: - Properties

    private let id: String
    private let line: Int
    private let file: StaticString
    private let function: StaticString
    private let action: (T) -> ()

    // MARK: - Initialize

    public init(
        id: String = "<unknown>",
        line: Int = #line,
        file: StaticString = #file,
        function: StaticString = #function,
        action: @escaping (T) -> Void
    ) {
        self.id = id
        self.line = line
        self.file = file
        self.function = function
        self.action = action
    }

    // MARK: - Public methods

    public func perform(with value: T) {
        action(value)
    }

    public func map<U>(transform: @escaping (U) -> T) -> CommandWith<U> {
        CommandWith<U> { u in
            self.perform(with: transform(u))
        }
    }

    public func dispatchedAsync(on queue: DispatchQueue) -> CommandWith {
        CommandWith { value in
            queue.async { self.perform(with: value) }
        }
    }

    public func dispatchedSync(on queue: DispatchQueue) -> CommandWith {
        CommandWith { value in
            queue.sync { self.perform(with: value) }
        }
    }
}

// MARK: - Hashable
extension CommandWith: Hashable {
    public static func ==(left: CommandWith, right: CommandWith) -> Bool {
      return ObjectIdentifier(left) == ObjectIdentifier(right)
    }

    public func hash(into hasher: inout Hasher) {
      hasher.combine(ObjectIdentifier(self))
    }
}


// MARK: - CustomStringConvertible
extension CommandWith: CustomStringConvertible {
  public var description: String {
    return """
    type: \(String(describing: type(of: self)))
    id: \(id)
    file: \(file)
    function: \(function)
    line: \(line)
    """
  }
}

// MARK: - CustomDebugStringConvertible

extension CommandWith: CustomDebugStringConvertible {
    public var debugDescription: String { description }
}
