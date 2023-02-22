package com.affine.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import com.affine.blockdiagram.BlockTreeNode;
import com.affine.blockdiagram.BlockTreeNode.BlockDrawSettings;

public class BlockDiagramTest {
    @Test
    public void test() {
        BlockTreeNode a = new BlockTreeNode();
        BlockTreeNode b = new BlockTreeNode();
        BlockTreeNode c = new BlockTreeNode();
        BlockTreeNode d = new BlockTreeNode();
        BlockTreeNode e = new BlockTreeNode();
        BlockTreeNode f = new BlockTreeNode();
        BlockTreeNode g = new BlockTreeNode();
        BlockTreeNode h = new BlockTreeNode();
        BlockTreeNode i = new BlockTreeNode();
        BlockTreeNode j = new BlockTreeNode();
        BlockTreeNode k = new BlockTreeNode();
        BlockTreeNode l = new BlockTreeNode();
        BlockTreeNode m = new BlockTreeNode();
        BlockTreeNode n = new BlockTreeNode();
        BlockTreeNode o = new BlockTreeNode();
        BlockTreeNode p = new BlockTreeNode();
        BlockTreeNode q = new BlockTreeNode();
        BlockTreeNode r = new BlockTreeNode();
        BlockTreeNode s = new BlockTreeNode();
        BlockTreeNode t = new BlockTreeNode();
        BlockTreeNode u = new BlockTreeNode();
        BlockTreeNode v = new BlockTreeNode();
        BlockTreeNode w = new BlockTreeNode();
        BlockTreeNode x = new BlockTreeNode();
        a.addChild(b);
        a.addChild(c);
        a.addChild(d);
        b.addChild(e);
        b.addChild(f);
        b.addChild(g);
        c.addChild(h);
        c.addChild(i);
        c.addChild(j);
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
        a.setDefaultBlockDrawSettings(new BlockDrawSettings(10, 8, 2, 2, 30));
        assertEquals(60, a.getHeight());
        fail("Not yet implemented");
    }
}
