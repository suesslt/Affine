import SwiftUI

/// Main view that hosts the tree canvas with zoom/pan support and context menu.
/// Corresponds to the JFrame setup in JacksonDemo and the interactive
/// JacksonTreePanel with its JPopupMenu and JScrollPane.
struct ContentView: View {
    @StateObject private var viewModel = TreeViewModel(
        rootNode: TreeViewModel.createDemoTree()
    )
    @State private var showContextMenu = false
    @State private var scrollOffset: CGPoint = .zero

    var body: some View {
        NavigationStack {
            VStack(spacing: 0) {
                // Toolbar with zoom controls and info
                toolbar

                // Scrollable + zoomable tree canvas
                ScrollView([.horizontal, .vertical]) {
                    treeCanvas
                        .padding(10)
                }
                .background(Color.white)

                // Bottom bar with node info
                bottomBar
            }
            .navigationTitle("Program Structure Tree")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Menu {
                        Button("New Empty Tree") {
                            newEmptyTree()
                        }
                        Button("Load Demo Tree") {
                            loadDemoTree()
                        }
                    } label: {
                        Image(systemName: "doc.badge.plus")
                    }
                }
            }
        }
    }

    // MARK: - Tree Canvas with Tap and Long Press Gestures

    private var treeCanvas: some View {
        TreeCanvasView(viewModel: viewModel, scale: viewModel.zoomScale)
            .id(viewModel.treeVersion) // Force redraw on changes
            .contentShape(Rectangle()) // Make entire area tappable
            .onTapGesture { location in
                viewModel.handleTap(at: location)
                showContextMenu = false
            }
            .onLongPressGesture(minimumDuration: 0.5) {
                // Show context menu on long press if a node is selected
                if viewModel.selectedNode != nil {
                    showContextMenu = true
                }
            }
            .overlay(alignment: .topLeading) {
                if showContextMenu, let selected = viewModel.selectedNode {
                    contextMenuOverlay(for: selected)
                }
            }
    }

    // MARK: - Toolbar

    private var toolbar: some View {
        HStack {
            // Zoom out button
            Button(action: { zoomOut() }) {
                Image(systemName: "minus.magnifyingglass")
                    .font(.title3)
            }
            .disabled(viewModel.zoomScale <= 2)

            // Zoom level display
            Text("\(Int(viewModel.zoomScale))x")
                .font(.system(.body, design: .monospaced))
                .frame(width: 40)

            // Zoom in button
            Button(action: { zoomIn() }) {
                Image(systemName: "plus.magnifyingglass")
                    .font(.title3)
            }
            .disabled(viewModel.zoomScale >= 15)

            Spacer()

            // Pinch-to-zoom hint
            Text("Pinch to zoom")
                .font(.caption)
                .foregroundColor(.secondary)
        }
        .padding(.horizontal)
        .padding(.vertical, 8)
        .background(Color(.systemGroupedBackground))
    }

    // MARK: - Bottom Bar

    private var bottomBar: some View {
        HStack {
            if let selected = viewModel.selectedNode {
                Label(selected.structuredNode.nodeType.label, systemImage: nodeIcon(for: selected.structuredNode.nodeType))
                    .font(.subheadline)
                    .foregroundColor(.red)

                Spacer()

                // Quick action buttons
                if selected.canAddChildren {
                    Menu {
                        addChildMenuItems
                    } label: {
                        Label("Add Child", systemImage: "plus.circle")
                            .font(.subheadline)
                    }
                }

                if selected.canBeDeleted {
                    Button(role: .destructive) {
                        viewModel.deleteSubtree()
                    } label: {
                        Label("Delete", systemImage: "trash")
                            .font(.subheadline)
                    }
                }
            } else {
                Text("Tap a node to select it. Long press for options.")
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
        }
        .padding(.horizontal)
        .padding(.vertical, 8)
        .background(Color(.systemGroupedBackground))
    }

    // MARK: - Context Menu Overlay

    /// A floating context menu that appears on long press.
    /// Corresponds to the JPopupMenu in JacksonTreePanel.
    private func contextMenuOverlay(for node: JacksonTreeNodeView) -> some View {
        VStack(alignment: .leading, spacing: 0) {
            // Header
            Text(node.structuredNode.nodeType.label)
                .font(.headline)
                .padding(.horizontal, 12)
                .padding(.vertical, 8)
                .frame(maxWidth: .infinity, alignment: .leading)
                .background(Color(.systemGray5))

            Divider()

            // Delete actions
            if node.canBeDeleted {
                contextMenuButton(title: "Delete Subtree", icon: "trash", destructive: true) {
                    viewModel.deleteSubtree()
                    showContextMenu = false
                }
                contextMenuButton(title: "Delete Node", icon: "minus.circle", destructive: true) {
                    viewModel.deleteNode()
                    showContextMenu = false
                }
                Divider()
            }

            // Add child actions
            if node.canAddChildren {
                contextMenuButton(title: "Add Sequence", icon: "arrow.right") {
                    viewModel.addSequence()
                    showContextMenu = false
                }
                contextMenuButton(title: "Add Selection", icon: "arrow.triangle.branch") {
                    viewModel.addSelection()
                    showContextMenu = false
                }
                contextMenuButton(title: "Add Iteration", icon: "arrow.2.squarepath") {
                    viewModel.addIteration()
                    showContextMenu = false
                }
                contextMenuButton(title: "Add Fundamental Op", icon: "square") {
                    viewModel.addStatementsBlock()
                    showContextMenu = false
                }
            }

            // Close button
            Divider()
            contextMenuButton(title: "Close", icon: "xmark") {
                showContextMenu = false
            }
        }
        .frame(width: 220)
        .background(Color(.systemBackground))
        .cornerRadius(12)
        .shadow(color: .black.opacity(0.2), radius: 8, x: 0, y: 4)
        .padding(16)
    }

    private func contextMenuButton(title: String, icon: String, destructive: Bool = false, action: @escaping () -> Void) -> some View {
        Button(action: action) {
            HStack {
                Image(systemName: icon)
                    .frame(width: 20)
                Text(title)
                Spacer()
            }
            .foregroundColor(destructive ? .red : .primary)
            .padding(.horizontal, 12)
            .padding(.vertical, 10)
            .contentShape(Rectangle())
        }
    }

    // MARK: - Add Child Menu Items (for bottom bar)

    @ViewBuilder
    private var addChildMenuItems: some View {
        Button(action: { viewModel.addSequence() }) {
            Label("Sequence", systemImage: "arrow.right")
        }
        Button(action: { viewModel.addSelection() }) {
            Label("Selection", systemImage: "arrow.triangle.branch")
        }
        Button(action: { viewModel.addIteration() }) {
            Label("Iteration", systemImage: "arrow.2.squarepath")
        }
        Button(action: { viewModel.addStatementsBlock() }) {
            Label("Fundamental Operation", systemImage: "square")
        }
    }

    // MARK: - Zoom

    private func zoomIn() {
        viewModel.zoomScale = min(viewModel.zoomScale + 1, 15)
    }

    private func zoomOut() {
        viewModel.zoomScale = max(viewModel.zoomScale - 1, 2)
    }

    // MARK: - Tree Management

    private func newEmptyTree() {
        let root = StructuredNode.sequence()
        root.addChild(.statementsBlock())
        viewModel.rootNode = root
        viewModel.rebuildTree()
    }

    private func loadDemoTree() {
        viewModel.rootNode = TreeViewModel.createDemoTree()
        viewModel.rebuildTree()
    }

    // MARK: - Helpers

    private func nodeIcon(for type: NodeType) -> String {
        switch type {
        case .sequence: return "arrow.right"
        case .selection: return "arrow.triangle.branch"
        case .iteration: return "arrow.2.squarepath"
        case .statementsBlock: return "square"
        }
    }
}

#Preview {
    ContentView()
}
