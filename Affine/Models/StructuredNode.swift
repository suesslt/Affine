import Foundation

/// Corresponds to Java's AbstractTreeNode<T> and StructuredNode.
/// A generic tree node that supports parent-child relationships,
/// used as the data model for Jackson Structured Programming trees.
class StructuredNode: Identifiable, ObservableObject {
    let id = UUID()
    let nodeType: NodeType
    private(set) var children: [StructuredNode] = []
    weak var parent: StructuredNode?

    init(nodeType: NodeType) {
        self.nodeType = nodeType
    }

    // MARK: - Child Management

    func addChild(_ child: StructuredNode) {
        child.parent = self
        children.append(child)
    }

    func removeChild(_ child: StructuredNode) {
        children.removeAll { $0.id == child.id }
        child.parent = nil
    }

    func removeFromParent() {
        parent?.removeChild(self)
    }

    // MARK: - Tree Queries

    var isLeaf: Bool {
        children.isEmpty
    }

    var isRoot: Bool {
        parent == nil
    }

    var childCount: Int {
        children.count
    }

    func childAt(_ index: Int) -> StructuredNode? {
        guard index >= 0 && index < children.count else { return nil }
        return children[index]
    }

    var lastChild: StructuredNode? {
        children.last
    }

    func isSibling(of other: StructuredNode) -> Bool {
        guard let myParent = parent else { return false }
        return myParent.id == other.parent?.id
    }

    func indexOf(_ child: StructuredNode) -> Int? {
        children.firstIndex { $0.id == child.id }
    }
}
