package smadeira.suffixtrees;

import java.io.*;

/**
 * <p>Title: Suffix Tree With Errors Right Maximal Printer</p>
 *
 * <p>Description: Printer of suffix trees with errors right maximal.</p>
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
public class SuffixTreeWithErrorsRightMaximalPrinter
    extends SuffixTreePrinter {

  // ########################## PRINT SUFFIX TREE WITH COLORS #####################################

  /**
   * Prints the tree with colors in linear time.
   *
   * @param tree SuffixTreeWithErrorsRightMaximal
   */
  public static void printSuffixTreeWithColors(SuffixTreeWithErrorsRightMaximal
                                               tree) {
    SuffixTreeWithErrorsRightMaximalPrinter.printSuffixTreeWithColors(tree,
        tree.getRoot(), "");
  }

  /**
   *
   * @param tree SuffixTreeWithErrorsRightMaximal
   * @param node NodeInterface
   * @param depth String
   */
  private static void printSuffixTreeWithColors(
      SuffixTreeWithErrorsRightMaximal tree, NodeInterface node, String depth) {

    String nodeString = new String();

    if (tree.getRoot() == node) {
      nodeString = "root";
    }
    else {
      String label = tree.computeNodePathLabel(node);
      nodeString = "label=" + label;
    }

    if (node instanceof InternalNode) {
      // get info in the node
      nodeString = nodeString + " " + ( (InternalNode) node).printInfo();
      // print colors
      nodeString = nodeString + " " + "colors=" +
          ( (InternalNodeWithErrorsRightMaximal) node).colors.toString();
      System.out.println(depth + "<node " + nodeString + ">");
      // go down
      NodeInterface child = ( (InternalNode) node).
          getFirstChild();
      SuffixTreeWithErrorsRightMaximalPrinter.printSuffixTreeWithColors(tree,
          child, depth + " ");
      System.out.println(depth + "</node>");
      // go right
      NodeInterface rightSybling = node.getRightSybling();
      if (rightSybling != null) {
        SuffixTreeWithErrorsRightMaximalPrinter.printSuffixTreeWithColors(tree,
            rightSybling, depth);
      }
    }
    else {
      if (node instanceof LeafNode) {
        // get info in the node
        nodeString = nodeString + " " + ( (LeafNode) node).printInfo();
        // print colors
        nodeString = nodeString + " " + "color=" +
            ( (LeafNodeWithErrorsRightMaximal) node).getColor();
        System.out.println(depth + "<leaf " + nodeString + ">");
        // go right
        NodeInterface rightSybling = node.getRightSybling();
        if (rightSybling != null) {
          SuffixTreeWithErrorsRightMaximalPrinter.printSuffixTreeWithColors(
              tree, rightSybling, depth);
        }
      }
    }
  }

  // TO FILE

  /**
   * Prints the tree with colors in linear time to the file passed as parameter.
   *
   * @param tree SuffixTreeWithErrorsRightMaximal
   * @param filename String
   * @throws IOException
   */
  public static void printSuffixTreeWithColorsToFile(
      SuffixTreeWithErrorsRightMaximal tree, String filename) throws
      IOException {
    PrintWriter fileOut = new PrintWriter(new BufferedWriter(new FileWriter(
        filename)), true);
    SuffixTreeWithErrorsRightMaximalPrinter.printSuffixTreeWithColorsToFile(
        tree, fileOut, tree.getRoot(),
        "");
    fileOut.close();
  }

  /**
   * Used recursively !!!
   *
   * @param tree SuffixTreeWithErrorsRightMaximal
   * @param fileOut PrintWriter
   * @param node NodeInterface
   * @param depth String
   * @throws IOException
   */
  private static void printSuffixTreeWithColorsToFile(
      SuffixTreeWithErrorsRightMaximal tree, PrintWriter fileOut,
      NodeInterface node, String depth) throws
      IOException {
    String nodeString = new String();
    if (tree.getRoot() == node) {
      nodeString = "root";
    }
    else {
      String label = tree.computeNodePathLabel(node);
      nodeString = "label=" + label;
    }

    if (node instanceof InternalNode) {
      // get info in the node
      nodeString = nodeString + " " + ( (InternalNode) node).printInfo();
      // print colors
      nodeString = nodeString + " " + "colors=" +
          ( (InternalNodeWithErrorsRightMaximal) node).colors.toString();
      fileOut.println(depth + "<node " + nodeString + ">");
      // go down
      NodeInterface firstChild = ( (InternalNode) node).
          getFirstChild();
      SuffixTreeWithErrorsRightMaximalPrinter.printSuffixTreeWithColorsToFile(
          tree, fileOut, firstChild, depth + " ");
      fileOut.println(depth + "</node>");
      // go right
      NodeInterface rightSybling = node.getRightSybling();
      if (rightSybling != null) {
        SuffixTreeWithErrorsRightMaximalPrinter.printSuffixTreeWithColorsToFile(
            tree, fileOut, rightSybling, depth);
      }
    }
    else {
      if (node instanceof LeafNode) {
        // get info in the node
        nodeString = nodeString + " " + ( (LeafNode) node).printInfo();
        // print colors
        nodeString = nodeString + " " + "color=" +
            ( (LeafNodeWithErrorsRightMaximal) node).color;
        fileOut.println(depth + "<leaf " + nodeString + ">");
        // go right
        NodeInterface rightSybling = node.getRightSybling();
        if (rightSybling != null) {
          SuffixTreeWithErrorsRightMaximalPrinter.
              printSuffixTreeWithColorsToFile(tree, fileOut, rightSybling,
                                              depth);
        }
      }
    }
  }

} // END CLASS
