package com.affine.test;

import static org.junit.Assert.assertEquals;

import java.awt.Rectangle;

import org.junit.Test;

import com.affine.Iteration;
import com.affine.Selection;
import com.affine.Sequence;
import com.affine.StatementsBlock;
import com.affine.jackson.JacksonTreeNodeView;
import com.affine.jackson.JacksonTreeNodeView.DrawSettings;

public class JacksonTreeTest {
    // Height and width of a node are 10 units
    // Sibling space is 2
    // Non-sibling space is 4
    // Most left node is expected to be on x-position 0
    @Test
    public void testFullTree() {
        Sequence a = new Sequence();
        Sequence b = new Sequence();
        Sequence c = new Sequence();
        Selection d = new Selection();
        StatementsBlock e = new StatementsBlock();
        Iteration f = new Iteration();
        StatementsBlock g = new StatementsBlock();
        Selection h = new Selection();
        StatementsBlock i = new StatementsBlock();
        Selection k = new Selection();
        StatementsBlock l = new StatementsBlock();
        StatementsBlock m = new StatementsBlock();
        StatementsBlock n = new StatementsBlock();
        StatementsBlock o = new StatementsBlock();
        StatementsBlock p = new StatementsBlock();
        StatementsBlock q = new StatementsBlock();
        StatementsBlock r = new StatementsBlock();
        StatementsBlock s = new StatementsBlock();
        StatementsBlock t = new StatementsBlock();
        StatementsBlock u = new StatementsBlock();
        Selection v = new Selection();
        StatementsBlock w = new StatementsBlock();
        StatementsBlock x = new StatementsBlock();
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
        JacksonTreeNodeView treeNodeView = JacksonTreeNodeView.buildTree(a);
        treeNodeView.setDefaultDrawSettings(new DrawSettings(new Rectangle(0, 0, 10, 10), 2, 1, 4));
        treeNodeView.calculatePosition();
        assertEquals(60, treeNodeView.getStructuredNode(a).getDefaultShape().x);
        assertEquals(0, treeNodeView.getStructuredNode(a).getDefaultShape().y);
        assertEquals(12, treeNodeView.getStructuredNode(b).getDefaultShape().x);
        assertEquals(14, treeNodeView.getStructuredNode(b).getDefaultShape().y);
        assertEquals(63, treeNodeView.getStructuredNode(c).getDefaultShape().x);
        assertEquals(14, treeNodeView.getStructuredNode(c).getDefaultShape().y);
        assertEquals(109, treeNodeView.getStructuredNode(d).getDefaultShape().x);
        assertEquals(14, treeNodeView.getStructuredNode(d).getDefaultShape().y);
        assertEquals(0, treeNodeView.getStructuredNode(e).getDefaultShape().x);
        assertEquals(28, treeNodeView.getStructuredNode(e).getDefaultShape().y);
        assertEquals(12, treeNodeView.getStructuredNode(f).getDefaultShape().x);
        assertEquals(28, treeNodeView.getStructuredNode(f).getDefaultShape().y);
        assertEquals(24, treeNodeView.getStructuredNode(g).getDefaultShape().x);
        assertEquals(28, treeNodeView.getStructuredNode(g).getDefaultShape().y);
        assertEquals(44, treeNodeView.getStructuredNode(h).getDefaultShape().x);
        assertEquals(28, treeNodeView.getStructuredNode(h).getDefaultShape().y);
        assertEquals(82, treeNodeView.getStructuredNode(k).getDefaultShape().x);
        assertEquals(28, treeNodeView.getStructuredNode(k).getDefaultShape().y);
        assertEquals(102, treeNodeView.getStructuredNode(l).getDefaultShape().x);
        assertEquals(28, treeNodeView.getStructuredNode(l).getDefaultShape().y);
        assertEquals(116, treeNodeView.getStructuredNode(m).getDefaultShape().x);
        assertEquals(28, treeNodeView.getStructuredNode(m).getDefaultShape().y);
        assertEquals(12, treeNodeView.getStructuredNode(n).getDefaultShape().x);
        assertEquals(42, treeNodeView.getStructuredNode(n).getDefaultShape().y);
        assertEquals(26, treeNodeView.getStructuredNode(o).getDefaultShape().x);
        assertEquals(42, treeNodeView.getStructuredNode(o).getDefaultShape().y);
        assertEquals(76, treeNodeView.getStructuredNode(s).getDefaultShape().x);
        assertEquals(42, treeNodeView.getStructuredNode(s).getDefaultShape().y);
        assertEquals(102, treeNodeView.getStructuredNode(u).getDefaultShape().x);
        assertEquals(42, treeNodeView.getStructuredNode(u).getDefaultShape().y);
        assertEquals(116, treeNodeView.getStructuredNode(v).getDefaultShape().x);
        assertEquals(42, treeNodeView.getStructuredNode(v).getDefaultShape().y);
        assertEquals(122, treeNodeView.getStructuredNode(x).getDefaultShape().x);
        assertEquals(56, treeNodeView.getStructuredNode(x).getDefaultShape().y);
        assertEquals(132, (int) treeNodeView.getStructuredNode(a).getTreeWidth());
        assertEquals(66, (int) treeNodeView.getStructuredNode(a).getTreeHeight());
    }
}
