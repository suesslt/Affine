import Foundation
import SwiftUI

/// The main view model that manages the tree state and user interactions.
/// Corresponds to the state management in Java's JacksonTreePanel.
class TreeViewModel: ObservableObject {
    @Published var rootNode: StructuredNode
    @Published var rootView: JacksonTreeNodeView
    @Published var selectedNode: JacksonTreeNodeView?
    @Published var zoomScale: Double = 5.0
    @Published var treeVersion: Int = 0 // Incremented to trigger re-renders

    init(rootNode: StructuredNode, drawSettings: DrawSettings = .default) {
        self.rootNode = rootNode
        let view = JacksonTreeNodeView.buildTree(from: rootNode)
        view.defaultDrawSettings = drawSettings
        view.calculatePosition()
        self.rootView = view
    }

    // MARK: - Tree Rebuilding

    /// Rebuilds the view tree after structural changes to the data model.
    /// Preserves the current draw settings.
    func rebuildTree() {
        let settings = rootView.defaultDrawSettings ?? .default
        let newView = JacksonTreeNodeView.buildTree(from: rootNode)
        newView.defaultDrawSettings = settings
        newView.calculatePosition()
        rootView = newView
        selectedNode = nil
        treeVersion += 1
    }

    // MARK: - Selection

    /// Handles a tap at the given point. Selects/deselects nodes.
    /// Corresponds to the mousePressed logic in JacksonTreePanel.
    func handleTap(at point: CGPoint) {
        let tapped = rootView.elementAt(point: point, scale: zoomScale)
        if let tapped = tapped {
            if tapped.isSelected {
                // Deselect if already selected
                rootView.unselectAll()
                selectedNode = nil
            } else {
                // Select the tapped node
                rootView.unselectAll()
                tapped.isSelected = true
                selectedNode = tapped
            }
        } else {
            // Tapped empty area: deselect
            rootView.unselectAll()
            selectedNode = nil
        }
        treeVersion += 1
    }

    // MARK: - Node Operations (Context Menu Actions)

    /// Deletes the selected node's entire subtree.
    /// Corresponds to Java's "Delete Subtree" action.
    func deleteSubtree() {
        guard let selected = selectedNode, selected.canBeDeleted else { return }
        selected.structuredNode.removeFromParent()
        selectedNode = nil
        rebuildTree()
    }

    /// Deletes only the selected node, reparenting its children to its parent.
    /// Corresponds to Java's "Delete Node" action.
    func deleteNode() {
        guard let selected = selectedNode, selected.canBeDeleted else { return }
        let parent = selected.structuredNode.parent
        let dataNode = selected.structuredNode

        // Reparent children to the parent
        for child in dataNode.children {
            parent?.addChild(child)
        }
        dataNode.removeFromParent()
        selectedNode = nil
        rebuildTree()
    }

    /// Appends a StatementsBlock (Fundamental Operation) child to the selected node.
    /// Corresponds to Java's "Append Child Fundamental Operations" action.
    func addStatementsBlock() {
        guard let selected = selectedNode, selected.canAddChildren else { return }
        selected.structuredNode.addChild(.statementsBlock())
        rebuildTree()
    }

    /// Appends a Sequence child (with 2 StatementsBlock children) to the selected node.
    /// Corresponds to Java's "Append Child Sequence" action.
    func addSequence() {
        guard let selected = selectedNode, selected.canAddChildren else { return }
        let seq = StructuredNode.sequence()
        seq.addChild(.statementsBlock())
        seq.addChild(.statementsBlock())
        selected.structuredNode.addChild(seq)
        rebuildTree()
    }

    /// Appends a Selection child (with 1 StatementsBlock child) to the selected node.
    /// Corresponds to Java's "Append Child Selection" action.
    func addSelection() {
        guard let selected = selectedNode, selected.canAddChildren else { return }
        let sel = StructuredNode.selection()
        sel.addChild(.statementsBlock())
        selected.structuredNode.addChild(sel)
        rebuildTree()
    }

    /// Appends an Iteration child (with 1 StatementsBlock child) to the selected node.
    /// Corresponds to Java's "Append Child Iteration" action.
    func addIteration() {
        guard let selected = selectedNode, selected.canAddChildren else { return }
        let iter = StructuredNode.iteration()
        iter.addChild(.statementsBlock())
        selected.structuredNode.addChild(iter)
        rebuildTree()
    }

    // MARK: - Demo Tree

    /// Creates the same demo tree as Java's `JacksonDemo.createTree()`.
    static func createDemoTree() -> StructuredNode {
        let a = StructuredNode.sequence()
        let b = StructuredNode.sequence()
        let c = StructuredNode.sequence()
        let d = StructuredNode.selection()
        let e = StructuredNode.statementsBlock()
        let f = StructuredNode.iteration()
        let g = StructuredNode.statementsBlock()
        let h = StructuredNode.selection()
        let i = StructuredNode.statementsBlock()
        let k = StructuredNode.selection()
        let l = StructuredNode.iteration()
        let m = StructuredNode.iteration()
        let n = StructuredNode.statementsBlock()
        let o = StructuredNode.statementsBlock()
        let p = StructuredNode.statementsBlock()
        let q = StructuredNode.statementsBlock()
        let r = StructuredNode.statementsBlock()
        let s = StructuredNode.statementsBlock()
        let t = StructuredNode.statementsBlock()
        let u = StructuredNode.statementsBlock()
        let v = StructuredNode.selection()
        let w = StructuredNode.statementsBlock()
        let x = StructuredNode.statementsBlock()

        a.addChild(b)
        a.addChild(c)
        a.addChild(d)
        b.addChild(e)
        b.addChild(f)
        b.addChild(g)
        c.addChild(h)
        c.addChild(i)
        c.addChild(k)
        d.addChild(l)
        d.addChild(m)
        f.addChild(n)
        h.addChild(o)
        h.addChild(p)
        h.addChild(q)
        h.addChild(r)
        k.addChild(s)
        k.addChild(t)
        l.addChild(u)
        m.addChild(v)
        v.addChild(w)
        v.addChild(x)

        return a
    }
}
