package com.affine.blockdiagram;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

@SuppressWarnings("serial")
public class BlockTreePanel extends JPanel {
    private final BlockTreeNode root;
    private JPopupMenu popup;
    private BlockTreeNode selectedElement = null;
    private Action deleteAction;
    private AbstractAction addFundamentalOperationsAction;
    private double zoomFactor = 5;
    private AbstractAction addIterator;
    private AbstractAction addSelection;
    private AbstractAction addSequence;
    private AbstractAction deleteNodeAction;

    public BlockTreePanel(BlockTreeNode root) {
        this.root = root;
        setBackground(Color.WHITE);
        createActions();
        popup = createPopupMenu();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                BlockTreeNode clickedElement = root.getElementAtPoint(e.getX(), e.getY());
                if (e.isPopupTrigger() && selectedElement != null && selectedElement.equals(clickedElement)) {
                    popup.setLocation(e.getPoint());
                    popup.setVisible(true);
                } else {
                    popup.setVisible(false);
                    if (clickedElement != null) {
                        if (!clickedElement.isSelected()) {
                            root.unselectTree();
                            clickedElement.setSelected();
                            selectedElement = clickedElement;
                        } else {
                            root.unselectTree();
                            selectedElement = null;
                        }
                        repaint();
                    }
                }
                if (selectedElement != null) {
                    deleteAction.setEnabled(selectedElement.canBeDeleted());
                    addFundamentalOperationsAction.setEnabled(selectedElement.canAddFundamentalOperations());
                    addIterator.setEnabled(selectedElement.canAddFundamentalOperations());
                    addSelection.setEnabled(selectedElement.canAddFundamentalOperations());
                    addSequence.setEnabled(selectedElement.canAddFundamentalOperations());
                } else {
                    deleteAction.setEnabled(false);
                    addFundamentalOperationsAction.setEnabled(false);
                    addIterator.setEnabled(false);
                    addSelection.setEnabled(false);
                    addSequence.setEnabled(false);
                }
            }
        });
    }

    private void createActions() {
        deleteAction = new AbstractAction("Delete Subtree") {
            @Override
            public void actionPerformed(ActionEvent e) {
                BlockTreeNode parent = selectedElement.getParent();
                parent.removeChild(selectedElement);
                root.calculatePosition();
                BlockTreePanel.this.repaint();
                popup.setVisible(false);
            }
        };
        deleteNodeAction = new AbstractAction("Delete Node") {
            @Override
            public void actionPerformed(ActionEvent e) {
                BlockTreeNode parent = selectedElement.getParent();
                parent.removeChild(selectedElement);
                root.calculatePosition();
                BlockTreePanel.this.repaint();
                popup.setVisible(false);
            }
        };
        addFundamentalOperationsAction = new AbstractAction("Append Child Fundamental Operations") {
            @Override
            public void actionPerformed(ActionEvent e) {
                // JacksonTreeNode newChild = new FundamentalOperationBlock();
                // selectedElement.addChild(newChild);
                // root.calculatePosition();
                // BlockTreePanel.this.repaint();
                // popup.setVisible(false);
            }
        };
        addIterator = new AbstractAction("Append Child Iteration") {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Iteration newChild = new Iteration();
                // newChild.addChild(new FundamentalOperationBlock());
                // selectedElement.addChild(newChild);
                // root.calculatePosition();
                // BlockTreePanel.this.repaint();
                // popup.setVisible(false);
            }
        };
        addSelection = new AbstractAction("Append Child Selection") {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Selection newChild = new Selection();
                // newChild.addChild(new FundamentalOperationBlock());
                // selectedElement.addChild(newChild);
                // root.calculatePosition();
                // BlockTreePanel.this.repaint();
                // popup.setVisible(false);
            }
        };
        addSequence = new AbstractAction("Append Child Sequence") {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Sequence newChild = new Sequence();
                // newChild.addChild(new FundamentalOperationBlock());
                // newChild.addChild(new FundamentalOperationBlock());
                // selectedElement.addChild(newChild);
                // root.calculatePosition();
                // BlockTreePanel.this.repaint();
                // popup.setVisible(false);
            }
        };
    }

    private JPopupMenu createPopupMenu() {
        JPopupMenu result = new JPopupMenu();
        result.add(deleteAction);
        result.add(deleteNodeAction);
        result.addSeparator();
        result.add(addSequence);
        result.add(addSelection);
        result.add(addIterator);
        result.add(addFundamentalOperationsAction);
        return result;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension((int) (root.getTreeWidth() * zoomFactor), (int) (root.getHeight() * zoomFactor));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        root.paint((Graphics2D) g, zoomFactor);
        // root.paint((Graphics2D) g, Double.min((double) getWidth() / (double) root.getTreeWidth(), (double) getHeight() / (double) root.getTreeHeight()));
    }
}