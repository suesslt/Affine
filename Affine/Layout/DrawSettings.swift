import Foundation

/// Corresponds to Java's JacksonTreeNodeView.DrawSettings.
/// Defines the visual dimensions and spacing for tree node rendering.
struct DrawSettings {
    /// Default node width and height
    var nodeWidth: Double
    var nodeHeight: Double

    /// Horizontal space between non-sibling nodes (nodes with different parents)
    var horizontalNonSiblingSpace: Double

    /// Horizontal space between sibling nodes (nodes sharing the same parent)
    var horizontalSiblingSpace: Double

    /// Vertical space between parent and child rows
    var verticalSpace: Double

    /// Creates default draw settings matching the Java version:
    /// Rectangle(0, 0, 15, 10), nonSibling=2, sibling=1, vertical=4
    static let `default` = DrawSettings(
        nodeWidth: 15,
        nodeHeight: 10,
        horizontalNonSiblingSpace: 2,
        horizontalSiblingSpace: 1,
        verticalSpace: 4
    )
}
