import Foundation

/// The four node types from Jackson Structured Programming,
/// matching the Java classes: Sequence, Selection, Iteration, StatementsBlock.
enum NodeType: String, CaseIterable {
    case sequence = "Sequence"
    case selection = "Selection"
    case iteration = "Iteration"
    case statementsBlock = "Statements"

    /// Display label for the node type
    var label: String {
        rawValue
    }

    /// Short abbreviation shown inside the node rectangle
    var abbreviation: String {
        switch self {
        case .sequence: return "SEQ"
        case .selection: return "SEL"
        case .iteration: return "ITR"
        case .statementsBlock: return "FOP"
        }
    }

    /// Whether this node type can have children added to it.
    /// StatementsBlock is a leaf (fundamental operation) and cannot have children in the Java version.
    var canHaveChildren: Bool {
        switch self {
        case .statementsBlock: return false
        default: return true
        }
    }
}

// MARK: - Factory Methods

extension StructuredNode {
    /// Creates a Sequence node (corresponds to Java's `new Sequence()`)
    static func sequence() -> StructuredNode {
        StructuredNode(nodeType: .sequence)
    }

    /// Creates a Selection node (corresponds to Java's `new Selection()`)
    static func selection() -> StructuredNode {
        StructuredNode(nodeType: .selection)
    }

    /// Creates an Iteration node (corresponds to Java's `new Iteration()`)
    static func iteration() -> StructuredNode {
        StructuredNode(nodeType: .iteration)
    }

    /// Creates a StatementsBlock node (corresponds to Java's `new StatementsBlock()`)
    static func statementsBlock() -> StructuredNode {
        StructuredNode(nodeType: .statementsBlock)
    }
}
