package smadeira.suffixtrees;

import java.io.*;

/**
 * <p>Title: Suffix Tree Printer</p>
 *
 * <p>Description: Printer of Suffix Trees.</p>
 *
 * <p>Copyright:   Copyright (C) 2007  Joana P. Gon�alves, Sara C. Madeira
 *                 This program is free software; you can redistribute
 *                 it and/or modify it under the terms of the GNU General
 *                 Public License as published by the Free Software
 *                 Foundation; either version 3 of the License, or
 *                 any later version.
 *
 *                 This program is distributed in the hope that it will
 *                 be useful, but WITHOUT ANY WARRANTY; without even the
 *                 implied warranty of MERCHANTABILITY or FITNESS FOR A
 *                 PARTICULAR PURPOSE.  See the
 *                 <a href="http://www.gnu.org/licenses/gpl.html">
 *                 GNU General Public License</a> for more details.
 * </p>
 *
 * @author Sara C. Madeira
 * @version 1.0
 */
public class SuffixTreePrinter {

  /**
   * Prints the tree in linear time.
   *
   * @param tree SuffixTreeNoErrors
   */
  public static void printSuffixTree(SuffixTreeNoErrors tree) {
    SuffixTreePrinter.printSuffixTree(tree, tree.getRoot(), "");
  }

  /**
   * @param tree SuffixTreeNoErrors
   * @param node NodeInterface
   * @param depth String
   */
  private static void printSuffixTree(SuffixTreeNoErrors tree,
                                      NodeInterface node, String depth) {

    String nodeString = new String();

    if (tree.getRoot() == node) {
      nodeString = "root";
    }
    else {
      String label = tree.computeNodePathLabel(node);
      nodeString = "label=" + label;
    }

    if (node instanceof InternalNodeNoErrors) {
      // get info in the node
      nodeString = nodeString + " " + ( (InternalNodeNoErrors) node).printInfo();
      System.out.println(depth + "<node " + nodeString + ">");
      // go down
      NodeInterface child = ( (InternalNodeNoErrors) node).
          getFirstChild();
      SuffixTreePrinter.printSuffixTree(tree, child, depth + " ");
      System.out.println(depth + "</node>");
      // go right
      NodeInterface rightSybling = node.getRightSybling();
      if (rightSybling != null) {
        SuffixTreePrinter.printSuffixTree(tree, rightSybling, depth);
      }
    }
    else {
      if (node instanceof LeafNodeNoErrors) {
        // get info in the node
        nodeString = nodeString + " " + ( (LeafNodeNoErrors) node).printInfo();
        String coord = ( (LeafNodeNoErrors) node).getCoordinates().toString();
        System.out.println(depth + "<leaf " + nodeString + " pos=" + coord +
                           ">");
        // go right
        NodeInterface rightSybling = node.getRightSybling();
        if (rightSybling != null) {
          SuffixTreePrinter.printSuffixTree(tree, rightSybling, depth);
        }
      }
    }
  }

// ########## TO FILE

  /**
   * Prints the suffix tree to a file in linear time.
   *
   * @param tree SuffixTreeNoErrors
   * @param filename String
   * @throws IOException
   */
  public static void printSuffixTreeToFile(SuffixTreeNoErrors tree,
                                           String filename) throws IOException {
    PrintWriter fileOut = new PrintWriter(new BufferedWriter(new FileWriter(
        filename)), true);
    SuffixTreePrinter.printSuffixTreeToFile(tree, fileOut, tree.getRoot(), "");
    fileOut.close();
  }

  /**
   * Prints the suffix tree to a file in linear time.
   *
   * @param tree SuffixTreeNoErrors
   * @param fileOut PrintWriter
   * @throws IOException
   */
  public static void printSuffixTreeToFile(SuffixTreeNoErrors tree,
                                           PrintWriter fileOut) {
    SuffixTreePrinter.printSuffixTreeToFile(tree, fileOut, tree.getRoot(), "");
    fileOut.close();
  }

  /**
   * Prints the suffix tree to a file in linear time.
   *
   * Used recursively !!!
   *
   * @param tree SuffixTreeNoErrors
   * @param node NodeInterface
   * @param depth String
   * @param fileOut PrintWriter
   * @throws IOException
   */
  private static void printSuffixTreeToFile(SuffixTreeNoErrors tree,
                                            PrintWriter fileOut,
                                            NodeInterface node,
                                            String depth) {
    String nodeString = new String();
    if (tree.getRoot() == node) {
      nodeString = "root";
    }
    else {
      String label = tree.computeNodePathLabel(node); //String label = tree.getSubstring(node.getLeftIndex(), node.getLength());
      nodeString = "label=" + label;
    }

    if (node instanceof InternalNodeNoErrors) {
      // get info in the node
      nodeString = nodeString + " " + ( (InternalNodeNoErrors) node).printInfo();
      fileOut.println(depth + "<node " + nodeString + ">");
      // go down
      NodeInterface child = ( (InternalNodeNoErrors) node).
          getFirstChild();
      SuffixTreePrinter.printSuffixTreeToFile(tree, fileOut, child,
                                              depth + "  ");
      fileOut.println(depth + "</node>");
      // go right
      NodeInterface rightSybling = node.getRightSybling();
      if (rightSybling != null) {
        SuffixTreePrinter.printSuffixTreeToFile(tree, fileOut, rightSybling,
                                                depth);
      }
    }
    else {
      if (node instanceof LeafNodeNoErrors) {
        // get info in the node
        nodeString = nodeString + " " + ( (LeafNodeNoErrors) node).printInfo();
        String coord = ( (LeafNodeNoErrors) node).getCoordinates().toString();
        fileOut.println(depth + "<leaf " + nodeString + " pos=" + coord + ">");
        // go right
        NodeInterface rightSybling = node.getRightSybling();
        if (rightSybling != null) {
          SuffixTreePrinter.printSuffixTreeToFile(tree, fileOut, rightSybling,
                                                  depth);
        }
      }
      else {
        return;
      }
    }
  }

} // END CLASS
