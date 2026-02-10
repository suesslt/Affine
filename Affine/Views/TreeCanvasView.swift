import SwiftUI

/// The main canvas that renders the Jackson tree diagram.
/// Corresponds to Java's JacksonTreePanel and the paint logic in JacksonTreeNodeView.
/// Uses SwiftUI Canvas for high-performance 2D rendering with gesture support.
struct TreeCanvasView: View {
    @ObservedObject var viewModel: TreeViewModel
    let scale: Double

    var body: some View {
        Canvas { context, size in
            drawTree(context: &context, node: viewModel.rootView)
        }
        .frame(
            width: max(viewModel.rootView.treeWidth * scale + 20, 300),
            height: max(viewModel.rootView.treeHeight * scale + 20, 300)
        )
    }

    // MARK: - Tree Rendering

    /// Recursively draws the tree starting from the given node.
    /// Direct port of Java's `JacksonTreeNodeView.paint(Graphics2D, double)`.
    private func drawTree(context: inout GraphicsContext, node: JacksonTreeNodeView) {
        let scaledRect = CGRect(
            x: node.rect.origin.x * scale,
            y: node.rect.origin.y * scale,
            width: node.rect.width * scale,
            height: node.rect.height * scale
        )

        // Draw the node rectangle (red if selected, black otherwise)
        let strokeColor: Color = node.isSelected ? .red : .black
        context.stroke(
            Path(scaledRect),
            with: .color(strokeColor),
            lineWidth: 1
        )

        // Draw 3D shadow effect (gray and light gray lines)
        // Right shadow
        let shadowPath1 = Path { path in
            path.move(to: CGPoint(x: scaledRect.maxX + 1, y: scaledRect.minY + 1))
            path.addLine(to: CGPoint(x: scaledRect.maxX + 1, y: scaledRect.maxY + 1))
            path.move(to: CGPoint(x: scaledRect.minX + 1, y: scaledRect.maxY + 1))
            path.addLine(to: CGPoint(x: scaledRect.maxX + 1, y: scaledRect.maxY + 1))
        }
        context.stroke(shadowPath1, with: .color(.gray), lineWidth: 1)

        // Outer shadow
        let shadowPath2 = Path { path in
            path.move(to: CGPoint(x: scaledRect.maxX + 2, y: scaledRect.minY + 2))
            path.addLine(to: CGPoint(x: scaledRect.maxX + 2, y: scaledRect.maxY + 2))
            path.move(to: CGPoint(x: scaledRect.minX + 2, y: scaledRect.maxY + 2))
            path.addLine(to: CGPoint(x: scaledRect.maxX + 2, y: scaledRect.maxY + 2))
        }
        context.stroke(shadowPath2, with: .color(Color(white: 0.75)), lineWidth: 1)

        // Draw node type abbreviation text inside the rectangle
        let text = Text(node.structuredNode.nodeType.abbreviation)
            .font(.system(size: max(scale * 1.5, 6)))
            .foregroundColor(node.isSelected ? .red : .black)
        context.draw(
            context.resolve(text),
            at: CGPoint(x: scaledRect.midX, y: scaledRect.midY),
            anchor: .center
        )

        // Draw connector lines to children
        drawConnectors(context: &context, node: node)

        // Draw type symbol (circle for Selection children, X for Iteration children)
        drawTypeSymbol(context: &context, node: node, scaledRect: scaledRect)

        // Recursively draw children
        for child in node.children {
            drawTree(context: &context, node: child)
        }
    }

    // MARK: - Connector Lines

    /// Draws lines connecting parent to children.
    /// Direct port of the connector drawing in Java's paint method.
    private func drawConnectors(context: inout GraphicsContext, node: JacksonTreeNodeView) {
        let ds = node.drawSettings
        let childCount = node.children.count

        guard childCount > 0 else { return }

        if childCount == 1 {
            // Single child: draw straight vertical line
            let startY = (node.rect.origin.y + node.rect.height) * scale
            let endY = (node.rect.origin.y + node.rect.height + ds.verticalSpace) * scale
            let centerX = node.rect.midX * scale

            let path = Path { p in
                p.move(to: CGPoint(x: centerX, y: startY))
                p.addLine(to: CGPoint(x: centerX, y: endY))
            }
            context.stroke(path, with: .color(.black), lineWidth: 1)
        } else {
            // Multiple children: draw vertical line down, horizontal line across, then vertical lines to each child
            let midVerticalY = (node.rect.origin.y + node.rect.height + ds.verticalSpace / 2) * scale
            let parentBottomY = (node.rect.origin.y + node.rect.height) * scale
            let parentCenterX = node.rect.midX * scale

            // Vertical line from parent to mid-point
            let vertPath = Path { p in
                p.move(to: CGPoint(x: parentCenterX, y: parentBottomY))
                p.addLine(to: CGPoint(x: parentCenterX, y: midVerticalY))
            }
            context.stroke(vertPath, with: .color(.black), lineWidth: 1)

            // Horizontal line connecting leftmost to rightmost child
            let leftCenterX = node.children.first!.rect.midX * scale
            let rightCenterX = node.children.last!.rect.midX * scale

            let horizPath = Path { p in
                p.move(to: CGPoint(x: leftCenterX, y: midVerticalY))
                p.addLine(to: CGPoint(x: rightCenterX, y: midVerticalY))
            }
            context.stroke(horizPath, with: .color(.black), lineWidth: 1)

            // Vertical lines from horizontal bar down to each child
            for child in node.children {
                let childCenterX = child.rect.midX * scale
                let childTopY = child.rect.origin.y * scale

                let childPath = Path { p in
                    p.move(to: CGPoint(x: childCenterX, y: midVerticalY))
                    p.addLine(to: CGPoint(x: childCenterX, y: childTopY))
                }
                context.stroke(childPath, with: .color(.black), lineWidth: 1)
            }
        }
    }

    // MARK: - Type Symbols

    /// Draws the JSP type indicator symbol in the top-right corner of each node.
    /// - Circle: child of a Selection node
    /// - Star/X: child of an Iteration node
    /// Corresponds to Java's `paintTypisation`.
    private func drawTypeSymbol(context: inout GraphicsContext, node: JacksonTreeNodeView, scaledRect: CGRect) {
        guard let parentNode = node.parent else { return }

        let symbolSize = scale * 2
        let minX = scaledRect.maxX - scale - 2 * scale
        let minY = scaledRect.minY + scale
        let midX = scaledRect.maxX - scale - scale
        let midY = scaledRect.minY + scale + scale
        let maxX = scaledRect.maxX - scale
        let maxY = scaledRect.minY + scale + 2 * scale

        if parentNode.structuredNode.nodeType == .selection {
            // Draw circle (Selection indicator)
            let circleRect = CGRect(x: minX, y: minY, width: symbolSize, height: symbolSize)
            context.stroke(Path(ellipseIn: circleRect), with: .color(.black), lineWidth: 1)
        } else if parentNode.structuredNode.nodeType == .iteration {
            // Draw X pattern (Iteration indicator) - vertical, horizontal, and two diagonals
            let xPath = Path { p in
                // Vertical line
                p.move(to: CGPoint(x: midX, y: minY))
                p.addLine(to: CGPoint(x: midX, y: maxY))
                // Horizontal line
                p.move(to: CGPoint(x: minX, y: midY))
                p.addLine(to: CGPoint(x: maxX, y: midY))
                // Diagonal top-left to bottom-right
                p.move(to: CGPoint(x: minX, y: minY))
                p.addLine(to: CGPoint(x: maxX, y: maxY))
                // Diagonal bottom-left to top-right
                p.move(to: CGPoint(x: minX, y: maxY))
                p.addLine(to: CGPoint(x: maxX, y: minY))
            }
            context.stroke(xPath, with: .color(.black), lineWidth: 1)
        }
    }
}
