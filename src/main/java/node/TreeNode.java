package node;

import java.util.StringJoiner;

public class TreeNode {
    public int val;
    public TreeNode left;
    public TreeNode right;

    public TreeNode(int x) {
        val = x;
    }

    public TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(",","[","]");

        TreeNode  left=this;
        while (left!=null){
            joiner.add(String.valueOf(left.val));
            left=left.right;
        }
        return joiner.toString();
    }
}
