package com.affine.jackson;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.affine.Iteration;
import com.affine.Selection;
import com.affine.Sequence;
import com.affine.StatementsBlock;
import com.affine.StructuredNode;
import com.affine.jackson.JacksonTreeNodeView.DrawSettings;

public class JacksonDemo {
    public static StructuredNode createTree() {
        StructuredNode a = new Sequence();
        StructuredNode b = new Sequence();
        StructuredNode c = new Sequence();
        StructuredNode d = new Selection();
        StructuredNode e = new StatementsBlock();
        StructuredNode f = new Iteration();
        StructuredNode g = new StatementsBlock();
        StructuredNode h = new Selection();
        StructuredNode i = new StatementsBlock();
        StructuredNode k = new Selection();
        StructuredNode l = new Iteration();
        StructuredNode m = new Iteration();
        StructuredNode n = new StatementsBlock();
        StructuredNode o = new StatementsBlock();
        StructuredNode p = new StatementsBlock();
        StructuredNode q = new StatementsBlock();
        StructuredNode r = new StatementsBlock();
        StructuredNode s = new StatementsBlock();
        StructuredNode t = new StatementsBlock();
        StructuredNode u = new StatementsBlock();
        StructuredNode v = new Selection();
        StructuredNode w = new StatementsBlock();
        StructuredNode x = new StatementsBlock();
        a.addChild(b);
        a.addChild(c);
        a.addChild(d);
        b.addChild(e);
        b.addChild(f);
        b.addChild(g);
        c.addChild(h);
        c.addChild(i);
        c.addChild(k);
        d.addChild(l);
        d.addChild(m);
        f.addChild(n);
        h.addChild(o);
        h.addChild(p);
        h.addChild(q);
        h.addChild(r);
        k.addChild(s);
        k.addChild(t);
        l.addChild(u);
        m.addChild(v);
        v.addChild(w);
        v.addChild(x);
        return a;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        JFrame f = new JFrame("Program Structure Tree");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        StructuredNode tree = createTree();
        JacksonTreeNodeView jacksonView = JacksonTreeNodeView.buildTree(tree);
        jacksonView.setDefaultDrawSettings(new DrawSettings(new Rectangle(0, 0, 15, 10), 2, 1, 4));
        jacksonView.calculatePosition();
        JacksonTreePanel jacksonTreePanel = new JacksonTreePanel(jacksonView);
        f.add(new JScrollPane(jacksonTreePanel));
        f.setSize(250, 250);
        f.setVisible(true);
    }
}

@SuppressWarnings("serial")
class MyPanel extends JPanel {
    RedSquare redSquare = new RedSquare();

    public MyPanel() {
        setBorder(BorderFactory.createLineBorder(Color.black));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                moveSquare(e.getX(), e.getY());
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                moveSquare(e.getX(), e.getY());
            }
        });
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(250, 200);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawString("This is my custom Panel!", 10, 20);
        redSquare.paintSquare(g);
    }

    private void moveSquare(int x, int y) {
        // Current square state, stored as final variables
        // to avoid repeat invocations of the same methods.
        final int CURR_X = redSquare.getX();
        final int CURR_Y = redSquare.getY();
        final int CURR_W = redSquare.getWidth();
        final int CURR_H = redSquare.getHeight();
        final int OFFSET = 1;
        if ((CURR_X != x) || (CURR_Y != y)) {
            // The square is moving, repaint background
            // over the old square location.
            repaint(CURR_X, CURR_Y, CURR_W + OFFSET, CURR_H + OFFSET);
            // Update coordinates.
            redSquare.setX(x);
            redSquare.setY(y);
            // Repaint the square at the new location.
            repaint(redSquare.getX(), redSquare.getY(), redSquare.getWidth() + OFFSET, redSquare.getHeight() + OFFSET);
        }
    }
}

class RedSquare {
    private int height = 20;
    private int width = 20;
    private int xPos = 50;
    private int yPos = 50;

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getX() {
        return xPos;
    }

    public int getY() {
        return yPos;
    }

    public void paintSquare(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(xPos, yPos, width, height);
        g.setColor(Color.BLACK);
        g.drawRect(xPos, yPos, width, height);
    }

    public void setX(int xPos) {
        this.xPos = xPos;
    }

    public void setY(int yPos) {
        this.yPos = yPos;
    }
}