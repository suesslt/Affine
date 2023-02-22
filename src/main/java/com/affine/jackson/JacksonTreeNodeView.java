package com.affine.jackson;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.affine.AbstractTreeNode;
import com.affine.Iteration;
import com.affine.Selection;
import com.affine.StructuredNode;
import com.jore.Assert;

public class JacksonTreeNodeView extends AbstractTreeNode<JacksonTreeNodeView> {
    public static class DrawSettings implements Cloneable {
        public final Rectangle defaultShape;
        private final double defaultHorizontalNonSiblingSpace;
        private final double defaultHorizontalSiblingSpace;
        private final double defaultVerticalSpace;

        public DrawSettings(Rectangle defaultShape, double defaultHorizontalNonSiblingSpace, double defaultHorizontalSiblingSpace, double defaultVerticalSpace) {
            this.defaultShape = defaultShape;
            this.defaultHorizontalNonSiblingSpace = defaultHorizontalNonSiblingSpace;
            this.defaultHorizontalSiblingSpace = defaultHorizontalSiblingSpace;
            this.defaultVerticalSpace = defaultVerticalSpace;
        }

        @Override
        public Object clone() {
            return new DrawSettings((Rectangle) defaultShape.clone(), defaultHorizontalNonSiblingSpace, defaultHorizontalSiblingSpace, defaultVerticalSpace);
        }
    }

    static class NodeRow {
        private NodeRow nextDeeperRow;
        private List<JacksonTreeNodeView> nodes = new ArrayList<JacksonTreeNodeView>();
        private final double y;

        public NodeRow(double y) {
            this.y = y;
        }

        public void add(JacksonTreeNodeView newNode) {
            nodes.add(newNode);
        }

        public JacksonTreeNodeView getLeftNeighbour(JacksonTreeNodeView callingNode) {
            int index = nodes.indexOf(callingNode);
            return index <= 0 ? null : nodes.get(index - 1);
        }

        public NodeRow nextDeeper(double defaultVerticalSpace) {
            if (nextDeeperRow == null) {
                nextDeeperRow = new NodeRow(y + getMaximumHeight() + defaultVerticalSpace);
            }
            return nextDeeperRow;
        }

        private double getMaximumHeight() {
            double result = 0;
            for (JacksonTreeNodeView node : nodes) {
                if (node.drawSettings.defaultShape.getHeight() > result) {
                    result = node.drawSettings.defaultShape.getHeight();
                }
            }
            return result;
        }
    }

    public static JacksonTreeNodeView buildTree(StructuredNode node) {
        JacksonTreeNodeView result = new JacksonTreeNodeView(node);
        for (StructuredNode structuredNode : node.children()) {
            result.addChild(buildTree(structuredNode));
        }
        return result;
    }

    private final StructuredNode structuredNode;
    protected DrawSettings defaultDrawSettings;
    protected DrawSettings drawSettings;
    protected Rectangle rectangle;
    protected boolean selected = false;

    public JacksonTreeNodeView(StructuredNode structuredNode) {
        super();
        this.structuredNode = structuredNode;
    }

    public void calculatePosition() {
        Assert.isTrue("Method must only be called for top node", getParent() == null);
        NodeRow topRow = new NodeRow(0);
        calculatePosition(topRow, 0, 0);
    }

    public boolean canAddFundamentalOperations() {
        return true;
    }

    public boolean canBeDeleted() {
        return !isRoot();
    }

    public DrawSettings getDefaultDrawSettings() {
        return defaultDrawSettings == null && !isRoot() ? getParent().getDefaultDrawSettings() : defaultDrawSettings;
    }

    public Rectangle getDefaultShape() {
        return defaultDrawSettings.defaultShape;
    }

    public JacksonTreeNodeView getElementAtPoint(int xClicked, int yClicked) {
        JacksonTreeNodeView result = null;
        if (rectangle.contains(xClicked, yClicked)) {
            result = this;
        }
        for (Iterator<JacksonTreeNodeView> iterator = iterator(); iterator.hasNext() && result == null;) {
            JacksonTreeNodeView node = iterator.next();
            result = node.getElementAtPoint(xClicked, yClicked);
        }
        return result;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public JacksonTreeNodeView getSelectedElement() {
        JacksonTreeNodeView result = isSelected() ? this : null;
        for (Iterator<JacksonTreeNodeView> iterator = children().iterator(); iterator.hasNext() && result == null;) {
            JacksonTreeNodeView node = iterator.next();
            result = node.getSelectedElement();
        }
        return result;
    }

    public StructuredNode getStructuredNode() {
        return structuredNode;
    }

    public JacksonTreeNodeView getStructuredNode(StructuredNode node) {
        JacksonTreeNodeView result = node.equals(structuredNode) ? this : null;
        for (Iterator<JacksonTreeNodeView> iterator = children().iterator(); iterator.hasNext() && result == null;) {
            result = iterator.next().getStructuredNode(node);
        }
        return result;
    }

    public double getTreeHeight() {
        double result = drawSettings.defaultShape.getMaxY();
        for (JacksonTreeNodeView child : children()) {
            result = Double.max(result, child.getTreeHeight());
        }
        return result;
    }

    public double getTreeWidth() {
        double result = drawSettings.defaultShape.getMaxX();
        for (JacksonTreeNodeView child : children()) {
            result = Double.max(result, child.getTreeWidth());
        }
        return result;
    }

    public boolean isSelected() {
        return selected;
    }

    public void paint(Graphics2D g, double scale) {
        rectangle = new Rectangle((int) scale(drawSettings.defaultShape.x, scale), (int) scale(drawSettings.defaultShape.y, scale), (int) scale(drawSettings.defaultShape.width, scale), (int) scale(drawSettings.defaultShape.height, scale));
        g.setColor(selected ? Color.RED : Color.BLACK);
        g.draw(rectangle);
        g.setColor(Color.GRAY);
        g.drawLine((int) rectangle.getMaxX() + 1, (int) rectangle.getY() + 1, (int) rectangle.getMaxX() + 1, (int) (rectangle.getMaxY() + 1));
        g.drawLine((int) rectangle.getX() + 1, (int) rectangle.getMaxY() + 1, (int) rectangle.getMaxX() + 1, (int) (rectangle.getMaxY() + 1));
        g.setColor(Color.LIGHT_GRAY);
        g.drawLine((int) rectangle.getMaxX() + 2, (int) rectangle.getY() + 2, (int) rectangle.getMaxX() + 2, (int) (rectangle.getMaxY() + 2));
        g.drawLine((int) rectangle.getX() + 2, (int) rectangle.getMaxY() + 2, (int) rectangle.getMaxX() + 2, (int) (rectangle.getMaxY() + 2));
        g.setColor(Color.BLACK);
        if (getChildCount() == 1) {
            g.drawLine((int) rectangle.getCenterX(), (int) scale(drawSettings.defaultShape.y + drawSettings.defaultShape.height, scale), (int) rectangle.getCenterX(), (int) scale(drawSettings.defaultShape.y + drawSettings.defaultShape.height + drawSettings.defaultVerticalSpace, scale));
            getChildAt(0).paint(g, scale);
        } else if (getChildCount() > 1) {
            double midVerticalDistance = scale(drawSettings.defaultShape.y + drawSettings.defaultShape.height + drawSettings.defaultVerticalSpace / 2, scale);
            g.drawLine((int) scale(drawSettings.defaultShape.getCenterX(), scale), (int) scale(drawSettings.defaultShape.y + drawSettings.defaultShape.height, scale), (int) scale(drawSettings.defaultShape.getCenterX(), scale), (int) midVerticalDistance);
            double leftMidPosition = scale(children().get(0).drawSettings.defaultShape.getCenterX(), scale);
            double rightMidPosition = scale(children().get(getChildCount() - 1).drawSettings.defaultShape.getCenterX(), scale);
            g.drawLine((int) leftMidPosition, (int) midVerticalDistance, (int) rightMidPosition, (int) midVerticalDistance);
            for (JacksonTreeNodeView child : children()) {
                g.drawLine((int) scale(child.drawSettings.defaultShape.getCenterX(), scale), (int) midVerticalDistance, (int) scale(child.drawSettings.defaultShape.getCenterX(), scale), (int) scale(child.drawSettings.defaultShape.y, scale));
                child.paint(g, scale);
            }
        }
        paintTypisation(g, scale);
    }

    public void setDefaultDrawSettings(DrawSettings defaultDrawSettings) {
        this.defaultDrawSettings = defaultDrawSettings;
    }

    public void setSelected() {
        selected = true;
    }

    public void unselectTree() {
        selected = false;
        for (JacksonTreeNodeView node : children()) {
            node.unselectTree();
        }
    }

    private void calculateChildrenPositions(NodeRow row) {
        NodeRow nextRow = row.nextDeeper(drawSettings.defaultVerticalSpace);
        for (JacksonTreeNodeView node : children()) {
            node.calculatePosition(nextRow, drawSettings.defaultShape.x, drawSettings.defaultShape.y);
        }
    }

    private void calculateOwnPosition(NodeRow row, double parentX, double parentY) {
        JacksonTreeNodeView leftNode = row.getLeftNeighbour(this);
        if (leftNode == null) {
            drawSettings.defaultShape.x = (int) parentX;
            drawSettings.defaultShape.y = (int) row.y;
        } else {
            double newX;
            if (leftNode.isSibling(this)) {
                newX = leftNode.drawSettings.defaultShape.getMaxX() + drawSettings.defaultHorizontalSiblingSpace;
            } else {
                newX = leftNode.drawSettings.defaultShape.getMaxX() + drawSettings.defaultHorizontalNonSiblingSpace;
            }
            drawSettings.defaultShape.x = (int) Double.max(newX, parentX);
            drawSettings.defaultShape.y = (int) row.y;
        }
    }

    private void calculatePosition(NodeRow row, double parentX, double parentY) {
        drawSettings = (DrawSettings) getDefaultDrawSettings().clone();
        row.add(this);
        calculateOwnPosition(row, parentX, parentY);
        calculateChildrenPositions(row);
        shiftMyPositionBetweenChildren();
        shiftMySubtreeAsMuchLeftAsPossible(row);
    }

    private double recursiveCalculateShiftLeftDistance(NodeRow row) {
        double shiftLeftDistance;
        JacksonTreeNodeView leftNode = row.getLeftNeighbour(this);
        if (leftNode == null) {
            shiftLeftDistance = drawSettings.defaultShape.x;
        } else {
            shiftLeftDistance = drawSettings.defaultShape.x - leftNode.drawSettings.defaultShape.getMaxX();
            if (leftNode.isSibling(this)) {
                shiftLeftDistance = shiftLeftDistance - drawSettings.defaultHorizontalSiblingSpace;
            } else {
                shiftLeftDistance = shiftLeftDistance - drawSettings.defaultHorizontalNonSiblingSpace;
            }
        }
        return getChildCount() > 0 ? Double.min(shiftLeftDistance, getChildAt(0).recursiveCalculateShiftLeftDistance(row.nextDeeper(drawSettings.defaultVerticalSpace))) : shiftLeftDistance;
    }

    private void recursiveShiftLeft(double shiftLeftDistance) {
        this.drawSettings.defaultShape.x = (int) (this.drawSettings.defaultShape.x - shiftLeftDistance);
        for (JacksonTreeNodeView child : children()) {
            child.recursiveShiftLeft(shiftLeftDistance);
        }
    }

    private void shiftMyPositionBetweenChildren() {
        if (getChildCount() == 1) {
            drawSettings.defaultShape.x = getChildAt(0).drawSettings.defaultShape.x;
        } else if (getChildCount() > 1) {
            double first = getChildAt(0).drawSettings.defaultShape.x;
            double last = getLastChild().drawSettings.defaultShape.getMaxX();
            drawSettings.defaultShape.x = (int) ((first + last) / 2 - drawSettings.defaultShape.width / 2);
        }
    }

    private void shiftMySubtreeAsMuchLeftAsPossible(NodeRow row) {
        if (getChildCount() > 0) {
            double shiftLeftDistance = recursiveCalculateShiftLeftDistance(row);
            recursiveShiftLeft(shiftLeftDistance);
        }
    }

    protected void paintTypisation(Graphics2D g, double scale) {
        int minX = (int) (rectangle.getMaxX() - scale - 2 * scale);
        int minY = (int) (rectangle.getY() + scale);
        int midX = (int) (rectangle.getMaxX() - scale - scale);
        int maxY = (int) (rectangle.getY() + scale + 2 * scale);
        int midY = (int) (rectangle.getY() + scale + scale);
        int maxX = (int) (rectangle.getMaxX() - scale);
        if (getParent() != null && getParent().getStructuredNode() instanceof Selection) {
            int circle = (int) (2 * scale);
            g.drawOval(minX, minY, circle, circle);
        } else if (getParent() != null && getParent().getStructuredNode() instanceof Iteration) {
            g.drawLine(midX, minY, midX, maxY);
            g.drawLine(minX, midY, maxX, midY);
            g.drawLine(minX, minY, maxX, maxY);
            g.drawLine(minX, maxY, maxX, minY);
        }
    }

    protected double scale(double value, double scale) {
        return value * scale;
    }
}