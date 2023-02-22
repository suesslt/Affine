package com.affine.blockdiagram;

import java.awt.Graphics2D;

import com.affine.AbstractTreeNode;
import com.affine.jackson.JacksonTreeNodeView.DrawSettings;
import com.jore.Assert;

public class BlockTreeNode extends AbstractTreeNode<BlockTreeNode> {
    public static class BlockDrawSettings {
        private int blockHeader;
        private int closedBlock;
        private int distance;
        private int line;

        public BlockDrawSettings(int closedBlock, int blockHeader, int distance, int line, int width) {
            this.closedBlock = closedBlock;
            this.blockHeader = blockHeader;
            this.distance = distance;
            this.line = line;
        }
    }

    private BlockDrawSettings blockDrawSettings;

    public void calculatePosition() {
        Assert.fail("not implemented");
    }

    public boolean canAddFundamentalOperations() {
        Assert.fail("not implemented");
        return false;
    }

    public boolean canBeDeleted() {
        Assert.fail("not implemented");
        return false;
    }

    public BlockTreeNode getElementAtPoint(int x, int y) {
        Assert.fail("not implemented");
        return null;
    }

    public int getHeight() {
        int result = 0;
        if (isLeaf()) {
            result = getBlockDrawSettings().closedBlock;
        } else {
            for (BlockTreeNode node : children()) {
                result += node.getHeight();
            }
            result += getBlockDrawSettings().blockHeader;
            result += (getBlockDrawSettings().distance * 2);
            result += (getBlockDrawSettings().line * 2);
        }
        return result;
    }

    public double getTreeWidth() {
        Assert.fail("not implemented");
        return 0;
    }

    public boolean isSelected() {
        Assert.fail("not implemented");
        return false;
    }

    public void paint(Graphics2D g, double zoomFactor) {
        Assert.fail("not implemented");
    }

    public void setDefaultBlockDrawSettings(BlockDrawSettings blockDrawSettings) {
        this.blockDrawSettings = blockDrawSettings;
    }

    public void setDefaultDrawSettings(DrawSettings drawSettings) {
    }

    public void setSelected() {
        Assert.fail("not implemented");
    }

    public void unselectTree() {
        Assert.fail("not implemented");
    }

    private BlockDrawSettings getBlockDrawSettings() {
        return blockDrawSettings == null && !isRoot() ? getParent().getBlockDrawSettings() : blockDrawSettings;
    }
}
