import Foundation
import CoreGraphics

/// Corresponds to Java's JacksonTreeNodeView.
/// Computes layout positions for each node in the tree and provides
/// hit-testing and rendering data. This is a direct port of the Java
/// layout algorithm that handles position calculation, centering parents
/// between children, and left-shifting subtrees to minimize width.
class JacksonTreeNodeView: Identifiable {
    let id = UUID()
    let structuredNode: StructuredNode
    var children: [JacksonTreeNodeView] = []
    weak var parent: JacksonTreeNodeView?

    /// The computed rectangle for this node (in tree coordinate space, before scaling)
    var rect: CGRect = .zero

    /// The draw settings (cloned per node during layout, inherited from parent)
    var drawSettings: DrawSettings = .default
    var defaultDrawSettings: DrawSettings?

    /// Whether this node is currently selected
    var isSelected: Bool = false

    init(structuredNode: StructuredNode) {
        self.structuredNode = structuredNode
    }

    // MARK: - Tree Building

    /// Recursively builds a view tree from a StructuredNode tree.
    /// Corresponds to Java's `JacksonTreeNodeView.buildTree(StructuredNode)`.
    static func buildTree(from node: StructuredNode) -> JacksonTreeNodeView {
        let view = JacksonTreeNodeView(structuredNode: node)
        for child in node.children {
            let childView = buildTree(from: child)
            childView.parent = view
            view.children.append(childView)
        }
        return view
    }

    /// Rebuilds the view tree from the current structured node tree.
    /// Used after modifications to the data model.
    func rebuild() -> JacksonTreeNodeView {
        let newTree = JacksonTreeNodeView.buildTree(from: structuredNode)
        if let settings = defaultDrawSettings {
            newTree.defaultDrawSettings = settings
        }
        return newTree
    }

    // MARK: - Draw Settings Resolution

    func resolvedDrawSettings() -> DrawSettings {
        if let ds = defaultDrawSettings { return ds }
        if let p = parent { return p.resolvedDrawSettings() }
        return .default
    }

    // MARK: - Layout Calculation

    /// Main entry point for layout. Must be called on the root node.
    /// Corresponds to Java's `calculatePosition()`.
    func calculatePosition() {
        let topRow = NodeRow(y: 0)
        calculatePosition(row: topRow, parentX: 0, parentY: 0)
    }

    private func calculatePosition(row: NodeRow, parentX: Double, parentY: Double) {
        drawSettings = resolvedDrawSettings()
        rect = CGRect(x: 0, y: 0, width: drawSettings.nodeWidth, height: drawSettings.nodeHeight)
        row.add(self)
        calculateOwnPosition(row: row, parentX: parentX, parentY: parentY)
        calculateChildrenPositions(row: row)
        shiftPositionBetweenChildren()
        shiftSubtreeLeft(row: row)
    }

    /// Positions this node based on its left neighbour in the same row.
    /// Corresponds to Java's `calculateOwnPosition`.
    private func calculateOwnPosition(row: NodeRow, parentX: Double, parentY: Double) {
        let leftNode = row.getLeftNeighbour(of: self)
        if leftNode == nil {
            rect.origin.x = parentX
            rect.origin.y = row.y
        } else {
            let left = leftNode!
            let newX: Double
            if left.isSibling(of: self) {
                newX = left.rect.maxX + drawSettings.horizontalSiblingSpace
            } else {
                newX = left.rect.maxX + drawSettings.horizontalNonSiblingSpace
            }
            rect.origin.x = max(newX, parentX)
            rect.origin.y = row.y
        }
    }

    /// Recursively calculates positions for all children.
    private func calculateChildrenPositions(row: NodeRow) {
        let nextRow = row.nextDeeper(verticalSpace: drawSettings.verticalSpace)
        for child in children {
            child.calculatePosition(row: nextRow, parentX: rect.origin.x, parentY: rect.origin.y)
        }
    }

    /// Centers this node between its first and last child.
    /// Corresponds to Java's `shiftMyPositionBetweenChildren`.
    private func shiftPositionBetweenChildren() {
        if children.count == 1 {
            rect.origin.x = children[0].rect.origin.x
        } else if children.count > 1 {
            let first = children[0].rect.origin.x
            let last = children[children.count - 1].rect.maxX
            rect.origin.x = (first + last) / 2 - rect.width / 2
        }
    }

    /// Shifts the entire subtree as far left as possible without overlapping.
    /// Corresponds to Java's `shiftMySubtreeAsMuchLeftAsPossible`.
    private func shiftSubtreeLeft(row: NodeRow) {
        if !children.isEmpty {
            let shiftDistance = recursiveCalculateShiftLeftDistance(row: row)
            recursiveShiftLeft(distance: shiftDistance)
        }
    }

    /// Recursively calculates how far left this subtree can shift.
    private func recursiveCalculateShiftLeftDistance(row: NodeRow) -> Double {
        let shiftDistance: Double
        let leftNode = row.getLeftNeighbour(of: self)
        if leftNode == nil {
            shiftDistance = rect.origin.x
        } else {
            let left = leftNode!
            var dist = rect.origin.x - left.rect.maxX
            if left.isSibling(of: self) {
                dist -= drawSettings.horizontalSiblingSpace
            } else {
                dist -= drawSettings.horizontalNonSiblingSpace
            }
            shiftDistance = dist
        }
        if !children.isEmpty {
            let childDist = children[0].recursiveCalculateShiftLeftDistance(
                row: row.nextDeeper(verticalSpace: drawSettings.verticalSpace)
            )
            return min(shiftDistance, childDist)
        }
        return shiftDistance
    }

    /// Recursively shifts this node and all descendants left by the given distance.
    private func recursiveShiftLeft(distance: Double) {
        rect.origin.x -= distance
        for child in children {
            child.recursiveShiftLeft(distance: distance)
        }
    }

    // MARK: - Sibling Check

    func isSibling(of other: JacksonTreeNodeView) -> Bool {
        guard let myParent = parent else { return false }
        return myParent.id == other.parent?.id
    }

    // MARK: - Tree Dimensions

    /// Total width of the tree from this node downward.
    var treeWidth: Double {
        var result = rect.maxX
        for child in children {
            result = max(result, child.treeWidth)
        }
        return result
    }

    /// Total height of the tree from this node downward.
    var treeHeight: Double {
        var result = rect.maxY
        for child in children {
            result = max(result, child.treeHeight)
        }
        return result
    }

    // MARK: - Hit Testing

    /// Returns the node view at the given point (in scaled coordinates).
    func elementAt(point: CGPoint, scale: Double) -> JacksonTreeNodeView? {
        let scaledRect = CGRect(
            x: rect.origin.x * scale,
            y: rect.origin.y * scale,
            width: rect.width * scale,
            height: rect.height * scale
        )
        if scaledRect.contains(point) {
            return self
        }
        for child in children {
            if let found = child.elementAt(point: point, scale: scale) {
                return found
            }
        }
        return nil
    }

    // MARK: - Selection

    /// Deselects this node and all descendants.
    func unselectAll() {
        isSelected = false
        for child in children {
            child.unselectAll()
        }
    }

    /// Returns the currently selected node in the subtree, if any.
    var selectedElement: JacksonTreeNodeView? {
        if isSelected { return self }
        for child in children {
            if let selected = child.selectedElement {
                return selected
            }
        }
        return nil
    }

    // MARK: - Node Capabilities

    var canBeDeleted: Bool {
        parent != nil
    }

    var canAddChildren: Bool {
        structuredNode.nodeType.canHaveChildren
    }
}

// MARK: - NodeRow

/// Tracks all nodes at the same vertical level.
/// Corresponds to Java's `JacksonTreeNodeView.NodeRow`.
class NodeRow {
    let y: Double
    private var nodes: [JacksonTreeNodeView] = []
    private var _nextDeeperRow: NodeRow?

    init(y: Double) {
        self.y = y
    }

    func add(_ node: JacksonTreeNodeView) {
        nodes.append(node)
    }

    func getLeftNeighbour(of node: JacksonTreeNodeView) -> JacksonTreeNodeView? {
        guard let index = nodes.firstIndex(where: { $0.id == node.id }) else { return nil }
        return index > 0 ? nodes[index - 1] : nil
    }

    func nextDeeper(verticalSpace: Double) -> NodeRow {
        if _nextDeeperRow == nil {
            let maxHeight = nodes.map { $0.drawSettings.nodeHeight }.max() ?? 0
            _nextDeeperRow = NodeRow(y: y + maxHeight + verticalSpace)
        }
        return _nextDeeperRow!
    }
}
