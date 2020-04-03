package smadeira.suffixtrees;

import java.io.*;

/**
 * <p>Title: Suffix Tree No Errors Printer</p>
 *
 * <p>Description: Printer of a Suffix Tree for identifying
 *                 biclusters with exact patterns (CCC Biclusters).</p>
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
public class SuffixTreeNoErrorsPrinter
    extends SuffixTreePrinter {

// ##################### PRINT MAXIMAL BICLUSTERS - LINEAR TIME #############################3333

  /**
   * Prints all the maximal biclusters in linear time.
   *
   * @param tree SuffixTreeNoErrors
   * @param isVBTP boolean
   * @param numberOfGenesInExpressionMatrix int
   */
  public static void printMaximalBiclustersInLinearTime(SuffixTreeNoErrors tree,
      boolean isVBTP, int numberOfGenesInExpressionMatrix) {
    // the root is not a bicluster
    boolean isMaximalBicluster = false;
    NodeInterface firstChild = ( (InternalNodeNoErrors) tree.getRoot()).
        getFirstChild();
    SuffixTreeNoErrorsPrinter.printMaximalBiclustersInLinearTime(tree,
        isMaximalBicluster, firstChild, "", isVBTP,
        numberOfGenesInExpressionMatrix);
  }

  /**
   * Used recursively.
   * Called by: public void printMaximalBiclustersInLinearTime().
   *
   * @param tree SuffixTreeNoErrors
   * @param previousNodeIsMaximalBicluster boolean
   * @param node NodeInterface
   * @param depth String
   * @param isVBTP boolean
   * @param numberOfGenesInExpressionMatrix int
   */
  private static void printMaximalBiclustersInLinearTime(SuffixTreeNoErrors
      tree,
      boolean previousNodeIsMaximalBicluster,
      NodeInterface node,
      String depth,
      boolean isVBTP,
      int numberOfGenesInExpressionMatrix) {
    if (node == null) {
      return;
    }
    else {
      String nodeString = new String();
      int firstColumn = -1;
      int lastColumn = -1;
      int numberOfColumns = 0;
      int numberOfRows = 0;
      boolean SuffixTreeNoErrorsPrinterNodeIsMaximalBicluster = false;
      if (node instanceof InternalNodeNoErrors) {
        SuffixTreeNoErrorsPrinterNodeIsMaximalBicluster = ( (
            InternalNodeNoErrors) node).
            getIsMaximal();
        if (SuffixTreeNoErrorsPrinterNodeIsMaximalBicluster == true) { // the bicluster is maximal
          // print bicluster identification together with its rows
          //compute the first column
          firstColumn = tree.computeFirstColumn(node);
          //compute the last column
          lastColumn = tree.computeLastColumnGivenFirst(node, firstColumn);
          //compute number of columns
          numberOfColumns = tree.computeNumberOfColumns(node);
          //compute number of rows
          numberOfRows = tree.computeNumberOfRows(node);
          if (isVBTP == false) {
            nodeString = "#BICLUSTER" + "\t" + numberOfColumns + "\t" +
                firstColumn + "-" + lastColumn + "\t" + numberOfRows + "\t";
          }
          else {
            nodeString = "#BICLUSTER" + "\t" + (numberOfColumns + 1) + "\t" +
                firstColumn + "-" + (lastColumn + 1) + "\t" + numberOfRows +
                "\t";
          }

          System.out.println(depth + nodeString);
        }
        // go down
        NodeInterface firstChild = ( (InternalNodeNoErrors)
                                    node).
            getFirstChild();
        SuffixTreeNoErrorsPrinter.printMaximalBiclustersInLinearTime(tree,
            SuffixTreeNoErrorsPrinterNodeIsMaximalBicluster ||
            previousNodeIsMaximalBicluster,
                  firstChild, depth + "\t", isVBTP,
                  numberOfGenesInExpressionMatrix);
        // go right
        NodeInterface rightSybling = node.getRightSybling();
        if (rightSybling != null) {
          SuffixTreeNoErrorsPrinter.printMaximalBiclustersInLinearTime(tree,
              previousNodeIsMaximalBicluster, rightSybling, depth, isVBTP,
              numberOfGenesInExpressionMatrix);
        }
      }
      else {
        if (node instanceof LeafNodeNoErrors) {
          SuffixTreeNoErrorsPrinterNodeIsMaximalBicluster = ( (LeafNodeNoErrors)
              node).getIsMaximal();
          if (SuffixTreeNoErrorsPrinterNodeIsMaximalBicluster == true) { // the bicluster is maximal
            // print bicluster identification together with its rows
            //compute the first column
            firstColumn = tree.computeFirstColumn(node);
            //compute the last column
            lastColumn = tree.computeLastColumnGivenFirst(node,
                firstColumn);
            //compute number of columns
            numberOfColumns = tree.computeNumberOfColumns(node);
            //compute number of rows
            numberOfRows = tree.computeNumberOfRows(node);
            if (isVBTP == false) {
              nodeString = "#BICLUSTER" + "\t" + (numberOfColumns - 1) + "\t" +
                  firstColumn + "-" + lastColumn + "\t" + numberOfRows + "\t";
            }
            else {
              nodeString = "#BICLUSTER" + "\t" + numberOfColumns + "\t" +
                  firstColumn + "-" + (lastColumn + 1) + "\t" + numberOfRows +
                  "\t";
            }

            System.out.println(depth + nodeString);
            // print row number
            // int rowNumber = tree.computeRowNumber( (LeafNodeNoErrors) node);
            // IN ORDER TO WORK WITH GENE SHIFTS
            int rowNumber = tree.computeRowNumber( (LeafNodeNoErrors) node) %
                numberOfGenesInExpressionMatrix;
            if (rowNumber == 0) {
              rowNumber = numberOfGenesInExpressionMatrix;
            }
            System.out.println(depth + rowNumber);
          }
          else {
            if (previousNodeIsMaximalBicluster == true) {
              // get row number
              // IN ORDER TO WORK WITH GENE SHIFTS
              int rowNumber = tree.computeRowNumber( (LeafNodeNoErrors) node) %
                  numberOfGenesInExpressionMatrix;
              if (rowNumber == 0) {
                rowNumber = numberOfGenesInExpressionMatrix;
              }
              System.out.println(depth + rowNumber);
            }
          }
          // go right
          NodeInterface rightSybling = node.getRightSybling();
          if (rightSybling != null) {
            SuffixTreeNoErrorsPrinter.printMaximalBiclustersInLinearTime(tree,
                previousNodeIsMaximalBicluster, rightSybling, depth, isVBTP,
                numberOfGenesInExpressionMatrix);
          }
        }
      }
    }
  }

//######### TO FILE

  /**
   * Prints all the maximal biclusters into a file in linear time.
   *
   * @param tree SuffixTreeNoErrors
   * @param filename String
   * @param isVBTP boolean
   * @param numberOfGenesInExpressionMatrix int
   * @throws IOException
   */
  public static void printMaximalBiclustersInLinearTimeToFile(
      SuffixTreeNoErrors tree, String filename, boolean isVBTP,
      int numberOfGenesInExpressionMatrix) throws IOException {

    PrintWriter fileOut = new PrintWriter(new BufferedWriter(new FileWriter(
        filename)), true);

    // the root is not a bicluster
    boolean isMaximalBicluster = false;
    NodeInterface firstChild = ( (InternalNodeNoErrors) tree.
                                getRoot()).
        getFirstChild();
    SuffixTreeNoErrorsPrinter.printMaximalBiclustersInLinearTimeToFile(tree,
        fileOut, isMaximalBicluster,
        firstChild, "", isVBTP, numberOfGenesInExpressionMatrix);
    fileOut.close();
  }

  /**
   * Used recursively. Called by: public void printMaximalBiclustersInLinearTimeTo().
   *
   * @param tree SuffixTreeNoErrors
   * @param fileOut PrintWriter
   * @param previousNodeIsMaximalBicluster boolean
   * @param node NodeInterface
   * @param depth String
   * @param VBTP boolean
   * @param numberOfGenesInExpressionMatrix int
   */
  private static void printMaximalBiclustersInLinearTimeToFile(
      SuffixTreeNoErrors tree, PrintWriter fileOut,
      boolean previousNodeIsMaximalBicluster, NodeInterface node, String depth,
      boolean VBTP, int numberOfGenesInExpressionMatrix) {

    if (node == null) {
      return;
    }
    else {
      String nodeString = new String();
      int firstColumn = -1;
      int lastColumn = -1;
      int numberOfColumns = 0;
      int numberOfRows = 0;
      boolean SuffixTreeNoErrorsPrinterNodeIsMaximalBicluster = false;
      if (node instanceof InternalNodeNoErrors) {
        SuffixTreeNoErrorsPrinterNodeIsMaximalBicluster = ( (
            InternalNodeNoErrors) node).
            getIsMaximal();
        if (SuffixTreeNoErrorsPrinterNodeIsMaximalBicluster == true) { // the bicluster is maximal
          // print bicluster identification together with its rows
          //compute the first column
          firstColumn = tree.computeFirstColumn(node);
          //compute the last column
          lastColumn = tree.computeLastColumnGivenFirst(node, firstColumn);
          //compute number of columns
          numberOfColumns = tree.computeNumberOfColumns(node);
          //compute number of rows
          numberOfRows = tree.computeNumberOfRows(node);
          if (VBTP == false) {
            nodeString = "#BICLUSTER" + "\t" + numberOfColumns + "\t" +
                firstColumn + "-" + lastColumn + "\t" + numberOfRows + "\t";
          }
          else {
            nodeString = "#BICLUSTER" + "\t" + (numberOfColumns + 1) + "\t" +
                firstColumn + "-" + (lastColumn + 1) + "\t" + numberOfRows +
                "\t";
          }
          fileOut.println(depth + nodeString);
        }
        // go down
        NodeInterface firstChild = ( (InternalNodeNoErrors)
                                    node).
            getFirstChild();
        SuffixTreeNoErrorsPrinter.printMaximalBiclustersInLinearTimeToFile(tree,
            fileOut,
            SuffixTreeNoErrorsPrinterNodeIsMaximalBicluster ||
            previousNodeIsMaximalBicluster,
            firstChild, depth + "\t", VBTP, numberOfGenesInExpressionMatrix);
        // go right
        NodeInterface rightSybling = node.getRightSybling();
        if (rightSybling != null) {
          SuffixTreeNoErrorsPrinter.printMaximalBiclustersInLinearTimeToFile(
              tree, fileOut,
              previousNodeIsMaximalBicluster,
              rightSybling, depth, VBTP, numberOfGenesInExpressionMatrix);
        }
      }
      else {
        if (node instanceof LeafNodeNoErrors) {
          SuffixTreeNoErrorsPrinterNodeIsMaximalBicluster = ( (LeafNodeNoErrors)
              node).getIsMaximal();
          if (SuffixTreeNoErrorsPrinterNodeIsMaximalBicluster == true) { // the bicluster is maximal
            // print bicluster identification together with its rows
            //compute the first column
            firstColumn = tree.computeFirstColumn(node);
            //compute the last column
            lastColumn = tree.computeLastColumnGivenFirst(node, firstColumn);
            //compute number of columns
            numberOfColumns = tree.computeNumberOfColumns(node);
            //compute number of rows
            numberOfRows = tree.computeNumberOfRows(node);
            if (VBTP == false) {
              nodeString = "#BICLUSTER" + "\t" + (numberOfColumns - 1) + "\t" +
                  firstColumn + "-" + lastColumn + "\t" + numberOfRows + "\t";
            }
            else {
              nodeString = "#BICLUSTER" + "\t" + numberOfColumns + "\t" +
                  firstColumn + "-" + (lastColumn + 1) + "\t" + numberOfRows +
                  "\t";
            }
            fileOut.println(depth + nodeString);
            //print row number
            //int rowNumber = tree.computeRowNumber( (LeafNodeNoErrors) node);
            // IN ORDER TO WORK WITH GENE SHIFTS
            int rowNumber = tree.computeRowNumber( (LeafNodeNoErrors) node) %
                numberOfGenesInExpressionMatrix;
            if (rowNumber == 0) {
              rowNumber = numberOfGenesInExpressionMatrix;
            }
            fileOut.println(depth + rowNumber);
          }
          else {
            if (previousNodeIsMaximalBicluster == true) {
              //get row number
              //int rowNumber = tree.computeRowNumber( (LeafNodeNoErrors) node);
              // IN ORDER TO WORK WITH GENE SHIFTS
              int rowNumber = tree.computeRowNumber( (LeafNodeNoErrors) node) %
                  numberOfGenesInExpressionMatrix;
              if (rowNumber == 0) {
                rowNumber = numberOfGenesInExpressionMatrix;
              }
              fileOut.println(depth + rowNumber);
            }
          }
          // go right
          NodeInterface rightSybling = node.getRightSybling();
          if (rightSybling != null) {
            SuffixTreeNoErrorsPrinter.printMaximalBiclustersInLinearTimeToFile(
                tree, fileOut,
                previousNodeIsMaximalBicluster,
                rightSybling, depth, VBTP, numberOfGenesInExpressionMatrix);
          }
        }
      }
    }
  }

// ##################### PRINT MAXIMAL BICLUSTERS WITH GENE NAMES - LINEAR TIME #############################

  /**
   * Prints all the maximal biclusters together with the gene names in linear time .
   *
   * @param tree SuffixTreeNoErrors
   * @param geneNames String[]
   * @param isVBTP boolean
   * @param numberOfGenesInExpressionMatrix int
   */
  public static void printMaximalBiclustersWithGeneNamesInLinearTime(
      SuffixTreeNoErrors tree, String[]
      geneNames, boolean isVBTP, int numberOfGenesInExpressionMatrix) {
    boolean isMaximalBicluster = false;
    NodeInterface firstChild = ( (InternalNodeNoErrors) tree.
                                getRoot()).
        getFirstChild();
    SuffixTreeNoErrorsPrinter.printMaximalBiclustersWithGeneNamesInLinearTime(
        tree, geneNames,
        isMaximalBicluster, firstChild, "", isVBTP,
        numberOfGenesInExpressionMatrix);
  }

  /**
   * Prints all the maximal biclusters together with the gene names in linear time.
   *
   * @param tree SuffixTreeNoErrors
   * @param geneNames String[]
   * @param previousNodeIsMaximalBicluster boolean
   * @param node NodeInterface
   * @param depth String
   * @param isVBTP boolean
   * @param numberOfGenesInExpressionMatrix int
   */
  private static void printMaximalBiclustersWithGeneNamesInLinearTime(
      SuffixTreeNoErrors tree,
      String[] geneNames,
      boolean previousNodeIsMaximalBicluster,
      NodeInterface node,
      String depth, boolean isVBTP, int numberOfGenesInExpressionMatrix) {
    if (node == null) {
      return;
    }
    else {
      String nodeString = new String();
      int firstColumn = -1;
      int lastColumn = -1;
      int numberOfColumns = 0;
      int numberOfRows = 0;
      boolean SuffixTreeNoErrorsPrinterNodeIsMaximalBicluster = false;
      if (node instanceof InternalNodeNoErrors) {
        SuffixTreeNoErrorsPrinterNodeIsMaximalBicluster = ( (
            InternalNodeNoErrors) node).
            getIsMaximal();
        if (SuffixTreeNoErrorsPrinterNodeIsMaximalBicluster == true) { // the bicluster is maximal
          // print bicluster identification together with its rows
          //compute the first column
          firstColumn = tree.computeFirstColumn(node);
          //compute the last column
          lastColumn = tree.computeLastColumnGivenFirst(node, firstColumn);
          //compute number of columns
          numberOfColumns = tree.computeNumberOfColumns(node);
          //compute number of rows
          numberOfRows = tree.computeNumberOfRows(node);
          if (isVBTP == false) {
            nodeString = "#BICLUSTER" + "\t" + numberOfColumns + "\t" +
                firstColumn +
                "-" + lastColumn + "\t" + numberOfRows + "\t";
          }
          else {
            nodeString = "#BICLUSTER" + "\t" + (numberOfColumns + 1) + "\t" +
                firstColumn +
                "-" + (lastColumn + 1) + "\t" + numberOfRows + "\t";
          }

          System.out.println(depth + nodeString);
        }

        // go down
        NodeInterface firstChild = ( (InternalNodeNoErrors)
                                    node).
            getFirstChild();
        SuffixTreeNoErrorsPrinter.
            printMaximalBiclustersWithGeneNamesInLinearTime(tree, geneNames,
            SuffixTreeNoErrorsPrinterNodeIsMaximalBicluster ||
            previousNodeIsMaximalBicluster,
            firstChild, depth + "\t", isVBTP, numberOfGenesInExpressionMatrix);
        // go right
        NodeInterface rightSybling = node.getRightSybling();
        if (rightSybling != null) {
          SuffixTreeNoErrorsPrinter.
              printMaximalBiclustersWithGeneNamesInLinearTime(tree, geneNames,
              previousNodeIsMaximalBicluster,
              rightSybling, depth, isVBTP, numberOfGenesInExpressionMatrix);
        }
      }
      else {
        if (node instanceof LeafNodeNoErrors) {
          SuffixTreeNoErrorsPrinterNodeIsMaximalBicluster = ( (LeafNodeNoErrors)
              node).getIsMaximal();
          if (SuffixTreeNoErrorsPrinterNodeIsMaximalBicluster == true) { // the bicluster is maximal
            // print bicluster identification together with its rows
            //compute the first column
            firstColumn = tree.computeFirstColumn(node);
            //compute the last column
            lastColumn = tree.computeLastColumnGivenFirst(node,
                firstColumn);
            //compute number of columns
            numberOfColumns = tree.computeNumberOfColumns(node);
            //compute number of rows
            numberOfRows = tree.computeNumberOfRows(node);
            if (isVBTP == false) {
              nodeString = "#BICLUSTER" + "\t" + (numberOfColumns - 1) + "\t" +
                  firstColumn + "-" + lastColumn + "\t" + numberOfRows + "\t";
            }
            else {
              nodeString = "#BICLUSTER" + "\t" + numberOfColumns + "\t" +
                  firstColumn + "-" + (lastColumn + 1) + "\t" + numberOfRows +
                  "\t";
            }

            System.out.println(depth + nodeString);
            // get row number
            int rowNumber = tree.computeRowNumber( (LeafNodeNoErrors) node) %
                numberOfGenesInExpressionMatrix;
            if (rowNumber == 0) {
              rowNumber = numberOfGenesInExpressionMatrix;
            }
            // get gene name
            String geneName = geneNames[rowNumber - 1];
            nodeString = "" + rowNumber + " " + geneName;
            System.out.println(depth + nodeString);
          }
          else {
            if (previousNodeIsMaximalBicluster == true) {
              //print row number and gene name
              // get row number
              int rowNumber = tree.computeRowNumber( (LeafNodeNoErrors)
                  node) % numberOfGenesInExpressionMatrix;
              if (rowNumber == 0) {
                rowNumber = numberOfGenesInExpressionMatrix;
              }
              // get gene name
              String geneName = geneNames[rowNumber - 1];
              nodeString = "" + rowNumber + " " + geneName;
              System.out.println(depth + nodeString);
            }
          }

          // go right
          NodeInterface rightSybling = node.getRightSybling();
          if (rightSybling != null) {
            SuffixTreeNoErrorsPrinter.
                printMaximalBiclustersWithGeneNamesInLinearTime(tree, geneNames,
                previousNodeIsMaximalBicluster,
                rightSybling, depth, isVBTP, numberOfGenesInExpressionMatrix);
          }
        }
      }
    }
  }

//########## TO FILE

  /**
   * Prints all the maximal biclusters together with the gene names to a file in linear time.
   *
   * @param tree SuffixTreeNoErrors
   * @param filename String
   * @param geneNames String[]
   * @param isVBTP boolean
   * @param numberOfGenesInExpressionMatrix int
   * @throws IOException
   */
  public static void printMaximalBiclustersWithGeneNamesInLinearTimeToFile(
      SuffixTreeNoErrors tree, String
      filename,
      String[] geneNames, boolean isVBTP, int numberOfGenesInExpressionMatrix) throws
      IOException {

    PrintWriter fileOut = new PrintWriter(new BufferedWriter(new FileWriter(
        filename)), true);

    // the root is not a bicluster
    boolean isMaximalBicluster = false;
    NodeInterface firstChild = ( (InternalNodeNoErrors) tree.
                                getRoot()).
        getFirstChild();
    SuffixTreeNoErrorsPrinter.
        printMaximalBiclustersWithGeneNamesInLinearTimeToFile(tree, fileOut,
        geneNames,
        isMaximalBicluster,
        firstChild, "", isVBTP, numberOfGenesInExpressionMatrix);
    fileOut.close();
  }

  /**
   * Used recursively. Called by public void printMaximalBiclustersInLinearTimeToFile(String filename, String[] geneNames).
   *
   * @param tree SuffixTreeNoErrors
   * @param fileOut PrintWriter
   * @param geneNames String[]
   * @param previousNodeIsMaximalBicluster boolean
   * @param node NodeInterface
   * @param depth String
   * @param isVBTP boolean
   * @param numberOfGenesInExpressionMatrix int
   */
  private static void printMaximalBiclustersWithGeneNamesInLinearTimeToFile(
      SuffixTreeNoErrors tree,
      PrintWriter fileOut, String[] geneNames,
      boolean previousNodeIsMaximalBicluster, NodeInterface node, String depth,
      boolean isVBTP, int numberOfGenesInExpressionMatrix) {
    if (node == null) {
      return;
    }
    else {
      String nodeString = new String();
      int firstColumn = -1;
      int lastColumn = -1;
      int numberOfColumns = 0;
      int numberOfRows = 0;
      boolean SuffixTreeNoErrorsPrinterNodeIsMaximalBicluster = false;
      if (node instanceof InternalNodeNoErrors) {
        SuffixTreeNoErrorsPrinterNodeIsMaximalBicluster = ( (
            InternalNodeNoErrors) node).
            getIsMaximal();
        if (SuffixTreeNoErrorsPrinterNodeIsMaximalBicluster == true) { // the bicluster is maximal
          // print bicluster identification together with its rows
          //compute the first column
          firstColumn = tree.computeFirstColumn(node);
          //compute the last column
          lastColumn = tree.computeLastColumnGivenFirst(node, firstColumn);
          //compute number of columns
          numberOfColumns = tree.computeNumberOfColumns(node);
          //compute number of rows
          numberOfRows = tree.computeNumberOfRows(node);
          if (isVBTP == false) {
            nodeString = "#BICLUSTER" + "\t" + numberOfColumns + "\t" +
                firstColumn +
                "-" + lastColumn + "\t" + numberOfRows + "\t";
          }
          else {
            nodeString = "#BICLUSTER" + "\t" + (numberOfColumns + 1) + "\t" +
                firstColumn +
                "-" + (lastColumn + 1) + "\t" + numberOfRows + "\t";
          }

          fileOut.println(depth + nodeString);
        }
        // go down
        NodeInterface firstChild = ( (InternalNodeNoErrors)
                                    node).
            getFirstChild();
        SuffixTreeNoErrorsPrinter.
            printMaximalBiclustersWithGeneNamesInLinearTimeToFile(tree, fileOut,
            geneNames,
            SuffixTreeNoErrorsPrinterNodeIsMaximalBicluster ||
            previousNodeIsMaximalBicluster,
            firstChild, depth + "\t", isVBTP, numberOfGenesInExpressionMatrix);
        // go right
        NodeInterface rightSybling = node.getRightSybling();
        if (rightSybling != null) {
          SuffixTreeNoErrorsPrinter.
              printMaximalBiclustersWithGeneNamesInLinearTimeToFile(tree,
              fileOut,
              geneNames,
              previousNodeIsMaximalBicluster,
              rightSybling, depth, isVBTP, numberOfGenesInExpressionMatrix);
        }
      }
      else {
        if (node instanceof LeafNodeNoErrors) {
          SuffixTreeNoErrorsPrinterNodeIsMaximalBicluster = ( (LeafNodeNoErrors)
              node).getIsMaximal();
          if (SuffixTreeNoErrorsPrinterNodeIsMaximalBicluster == true) { // the bicluster is maximal
            // print bicluster identification together with its rows
            //compute the first column
            firstColumn = tree.computeFirstColumn(node);
            //compute the last column
            lastColumn = tree.computeLastColumnGivenFirst(node,
                firstColumn);
            //compute number of columns
            numberOfColumns = tree.computeNumberOfColumns(node);
            //compute number of rows
            numberOfRows = tree.computeNumberOfRows(node);
            if (isVBTP == false) {
              nodeString = "#BICLUSTER" + "\t" + (numberOfColumns - 1) + "\t" +
                  firstColumn + "-" + lastColumn + "\t" + numberOfRows + "\t";
            }
            else {
              nodeString = "#BICLUSTER" + "\t" + numberOfColumns + "\t" +
                  firstColumn + "-" + (lastColumn + 1) + "\t" + numberOfRows +
                  "\t";
            }

            fileOut.println(depth + nodeString);

            // get row number
            int rowNumber = tree.computeRowNumber( (LeafNodeNoErrors) node) %
                numberOfGenesInExpressionMatrix;
            if (rowNumber == 0) {
              rowNumber = numberOfGenesInExpressionMatrix;
            }
            // get gene name
            String geneName = geneNames[rowNumber - 1];
            nodeString = "" + rowNumber + " " + geneName;
            fileOut.println(depth + nodeString);
          }
          else {
            if (previousNodeIsMaximalBicluster == true) {
              //print row number and gene name
              // get row number
              int rowNumber = tree.computeRowNumber( (LeafNodeNoErrors)
                  node) % numberOfGenesInExpressionMatrix;
              if (rowNumber == 0) {
                rowNumber = numberOfGenesInExpressionMatrix;
              }
              // get gene name
              String geneName = geneNames[rowNumber - 1];
              nodeString = "" + rowNumber + " " + geneName;
              fileOut.println(depth + nodeString);
            }
          }

          // go right
          NodeInterface rightSybling = node.getRightSybling();
          if (rightSybling != null) {
            SuffixTreeNoErrorsPrinter.
                printMaximalBiclustersWithGeneNamesInLinearTimeToFile(tree,
                fileOut,
                geneNames,
                previousNodeIsMaximalBicluster,
                rightSybling, depth, isVBTP, numberOfGenesInExpressionMatrix);
          }
        }
      }
    }
  }

  // ##################### PRINT MAXIMAL BICLUSTERS WITH GENE NAMES - NOT LINEAR TIME !!!#############################

  /**
   * Prints all maximal biclusters with gene names (NOT LINEAR TIME !!!).
   *
   * @param tree SuffixTreeNoErrors
   * @param geneNames String[]
   * @param isVBTP boolean
   * @param numberOfGenesInExpressionMatrix int
   */
  public static void printMaximalBiclustersWithGeneNames(SuffixTreeNoErrors
      tree, String[] geneNames, boolean isVBTP,
      int numberOfGenesInExpressionMatrix) {
    // the root is not a bicluster
    NodeInterface firstChild = ( (InternalNodeNoErrors) tree.getRoot()).
        getFirstChild();
    SuffixTreeNoErrorsPrinter.printMaximalBiclustersWithGeneNames(tree,
        geneNames, firstChild, isVBTP, numberOfGenesInExpressionMatrix);
  }

  /**
   *
   * @param tree SuffixTreeNoErrors
   * @param geneNames String[]
   * @param node NodeInterface
   * @param isVBTP boolean
   * @param numberOfGenesInExpressionMatrix int
   */
  private static void printMaximalBiclustersWithGeneNames(SuffixTreeNoErrors
      tree, String[] geneNames,
      NodeInterface node, boolean isVBTP, int numberOfGenesInExpressionMatrix) {

    if (node == null) {
      return;
    }
    else {
      String nodeString = new String();
      int firstColumn = -1;
      int lastColumn = -1;
      int numberOfColumns = 0;
      int numberOfRows = 0;
      boolean SuffixTreeNoErrorsPrinterNodeIsMaximalBicluster = false;
      if (node instanceof InternalNodeNoErrors) {
        SuffixTreeNoErrorsPrinterNodeIsMaximalBicluster = ( (
            InternalNodeNoErrors) node).
            getIsMaximal();
        if (SuffixTreeNoErrorsPrinterNodeIsMaximalBicluster == true) { // the bicluster is maximal
          // print bicluster identification together with its rows
          //compute the first column
          firstColumn = tree.computeFirstColumn(node);
          //compute the last column
          lastColumn = tree.computeLastColumnGivenFirst(node, firstColumn);
          //compute number of columns
          numberOfColumns = tree.computeNumberOfColumns(node);
          //compute number of rows
          numberOfRows = tree.computeNumberOfRows(node);
          if (isVBTP == false) {
            nodeString = "#BICLUSTER" + "\t" + numberOfColumns + "\t" +
                firstColumn +
                "-" + lastColumn + "\t" + numberOfRows + "\t";
          }
          else {
            nodeString = "#BICLUSTER" + "\t" + (numberOfColumns + 1) + "\t" +
                firstColumn +
                "-" + (lastColumn + 1) + "\t" + numberOfRows + "\t";
          }

          System.out.println(nodeString);
          // go down
          NodeInterface firstChild = ( (InternalNodeNoErrors)
                                      node).
              getFirstChild();
          SuffixTreeNoErrorsPrinter.printLeavesWithGeneNames(tree, geneNames,
              firstChild, isVBTP, numberOfGenesInExpressionMatrix);
        }
        // go down
        NodeInterface firstChild = ( (InternalNodeNoErrors)
                                    node).
            getFirstChild();
        SuffixTreeNoErrorsPrinter.printMaximalBiclustersWithGeneNames(tree,
            geneNames, firstChild, isVBTP, numberOfGenesInExpressionMatrix);
        // go right
        NodeInterface rightSybling = node.getRightSybling();
        if (rightSybling != null) {
          SuffixTreeNoErrorsPrinter.printMaximalBiclustersWithGeneNames(tree,
              geneNames, rightSybling,
              isVBTP, numberOfGenesInExpressionMatrix);
        }
      }
      else {
        if (node instanceof LeafNodeNoErrors) {
          SuffixTreeNoErrorsPrinterNodeIsMaximalBicluster = ( (LeafNodeNoErrors)
              node).getIsMaximal();
          if (SuffixTreeNoErrorsPrinterNodeIsMaximalBicluster == true) { // the bicluster is maximal
            // print bicluster identification together with its rows
            //compute the first column
            firstColumn = tree.computeFirstColumn(node);
            //compute the last column
            lastColumn = tree.computeLastColumnGivenFirst(node,
                firstColumn);
            //compute number of columns
            numberOfColumns = tree.computeNumberOfColumns(node);
            //compute number of rows
            numberOfRows = tree.computeNumberOfRows(node);
            if (isVBTP == false) {
              nodeString = "#BICLUSTER" + "\t" + (numberOfColumns - 1) + "\t" +
                  firstColumn + "-" + lastColumn + "\t" + numberOfRows + "\t";
            }
            else {
              nodeString = "#BICLUSTER" + "\t" + numberOfColumns + "\t" +
                  firstColumn + "-" + (lastColumn + 1) + "\t" + numberOfRows +
                  "\t";
            }

            System.out.println("" + nodeString);
            // get row number
            int rowNumber = tree.computeRowNumber( (LeafNodeNoErrors) node) %
                numberOfGenesInExpressionMatrix;
            if (rowNumber == 0) {
              rowNumber = numberOfGenesInExpressionMatrix;
            }
            // get gene name
            String geneName = geneNames[rowNumber - 1];
            System.out.println(geneName);
          }
          // go right
          NodeInterface rightSybling = node.getRightSybling();
          if (rightSybling != null) {
            SuffixTreeNoErrorsPrinter.printMaximalBiclustersWithGeneNames(tree,
                geneNames, rightSybling,
                isVBTP, numberOfGenesInExpressionMatrix);
          }
        }
      }
    }
  }

  /**
   * Prints the gene names of the leaves in the subtree of node.
   *
   * @param tree SuffixTreeNoErrors
   * @param geneNames String[]
   * @param node NodeInterface
   * @param isVBTP boolean
   * @param numberOfGenesInExpressionMatrix int
   */
  private static void printLeavesWithGeneNames(SuffixTreeNoErrors tree,
                                               String[] geneNames,
                                               NodeInterface node,
                                               boolean isVBTP,
                                               int numberOfGenesInExpressionMatrix) {

    if (node == null) {
      return;
    }
    else {
      if (node instanceof InternalNodeNoErrors) {
        // go down
        NodeInterface firstChild = ( (InternalNodeNoErrors)
                                    node).
            getFirstChild();
        SuffixTreeNoErrorsPrinter.printLeavesWithGeneNames(tree, geneNames,
            firstChild, isVBTP, numberOfGenesInExpressionMatrix);

        // go right
        NodeInterface rightSybling = node.getRightSybling();
        if (rightSybling != null) {
          SuffixTreeNoErrorsPrinter.printLeavesWithGeneNames(tree, geneNames,
              rightSybling, isVBTP, numberOfGenesInExpressionMatrix);
        }
      }
      else {
        if (node instanceof LeafNodeNoErrors) {
          //get row number
          int rowNumber;
          if (isVBTP) {
            rowNumber = tree.computeRowNumber( (LeafNodeNoErrors) node) %
                numberOfGenesInExpressionMatrix;
            if (rowNumber == 0) {
              rowNumber = numberOfGenesInExpressionMatrix;
            }
          }
          else {
            rowNumber = tree.computeRowNumber( (LeafNodeNoErrors) node);
          }
          // print gene name
          String geneName = geneNames[rowNumber - 1];
          System.out.println(geneName);

          // go right
          NodeInterface rightSybling = node.getRightSybling();
          if (rightSybling != null) {
            SuffixTreeNoErrorsPrinter.printLeavesWithGeneNames(tree, geneNames,
                rightSybling, isVBTP, numberOfGenesInExpressionMatrix);
          }
        }
      }
    }
  }

//################ TO FILE

  /**
   * Prints all the maximal biclusters to a file (NOT LINEAR !!).
   *
   * @param tree SuffixTreeNoErrors
   * @param filename String
   * @param geneNames String[]
   * @param isVBTP boolean
   * @param numberOfGenesInExpressionMatrix int
   * @throws IOException
   */
  public static void printMaximalBiclustersWithGeneNamesToFile(
      SuffixTreeNoErrors tree, String filename,
      String[] geneNames, boolean isVBTP, int numberOfGenesInExpressionMatrix) throws
      IOException {

    PrintWriter fileOut = new PrintWriter(new BufferedWriter(new FileWriter(
        filename)), true);
    // the root is not a bicluster
    NodeInterface firstChild = ( (InternalNodeNoErrors) tree.
                                getRoot()).
        getFirstChild();
    SuffixTreeNoErrorsPrinter.printMaximalBiclustersWithGeneNamesToFile(tree,
        fileOut, geneNames,
        firstChild, isVBTP, numberOfGenesInExpressionMatrix);
    fileOut.close();
  }

  /**
   * Used recursively. Called by: public void printMaximalBiclustersToFile(String filename, String[] geneNames).
   *
   * @param tree SuffixTreeNoErrors
   * @param fileOut PrintWriter
   * @param geneNames String[]
   * @param node NodeInterface
   * @param isVBTP boolean
   * @param numberOfGenesInExpressionMatrix int
   */
  private static void printMaximalBiclustersWithGeneNamesToFile(
      SuffixTreeNoErrors tree, PrintWriter fileOut,
      String[] geneNames, NodeInterface node, boolean isVBTP,
      int numberOfGenesInExpressionMatrix) {

    if (node == null) {
      return;
    }
    else {
      String nodeString = new String();
      int firstColumn = -1;
      int lastColumn = -1;
      int numberOfColumns = 0;
      int numberOfRows = 0;
      boolean SuffixTreeNoErrorsPrinterNodeIsMaximalBicluster = false;
      if (node instanceof InternalNodeNoErrors) {
        SuffixTreeNoErrorsPrinterNodeIsMaximalBicluster = ( (
            InternalNodeNoErrors) node).
            getIsMaximal();
        if (SuffixTreeNoErrorsPrinterNodeIsMaximalBicluster == true) { // the bicluster is maximal
          // print bicluster identification together with its rows
          //compute the first column
          firstColumn = tree.computeFirstColumn(node);
          //compute the last column
          lastColumn = tree.computeLastColumnGivenFirst(node, firstColumn);
          //compute number of columns
          numberOfColumns = tree.computeNumberOfColumns(node);
          //compute number of rows
          numberOfRows = tree.computeNumberOfRows(node);
          if (isVBTP == false) {
            nodeString = "#BICLUSTER" + "\t" + numberOfColumns + "\t" +
                firstColumn +
                "-" + lastColumn + "\t" + numberOfRows + "\t";
          }
          else {
            nodeString = "#BICLUSTER" + "\t" + (numberOfColumns + 1) + "\t" +
                firstColumn +
                "-" + (lastColumn + 1) + "\t" + numberOfRows + "\t";
          }

          fileOut.println(nodeString);
          // go down
          NodeInterface firstChild = ( (InternalNodeNoErrors)
                                      node).
              getFirstChild();
          SuffixTreeNoErrorsPrinter.printLeavesWithGeneNamesToFile(tree,
              fileOut, geneNames, firstChild, isVBTP,
              numberOfGenesInExpressionMatrix);
        }
        // go down
        NodeInterface firstChild = ( (InternalNodeNoErrors)
                                    node).
            getFirstChild();
        SuffixTreeNoErrorsPrinter.printMaximalBiclustersWithGeneNamesToFile(
            tree, fileOut, geneNames,
            firstChild, isVBTP, numberOfGenesInExpressionMatrix);
        // go right
        NodeInterface rightSybling = node.getRightSybling();
        if (rightSybling != null) {
          SuffixTreeNoErrorsPrinter.printMaximalBiclustersWithGeneNamesToFile(
              tree, fileOut, geneNames,
              rightSybling, isVBTP, numberOfGenesInExpressionMatrix);
        }
      }
      else {
        if (node instanceof LeafNodeNoErrors) {
          SuffixTreeNoErrorsPrinterNodeIsMaximalBicluster = ( (LeafNodeNoErrors)
              node).getIsMaximal();
          if (SuffixTreeNoErrorsPrinterNodeIsMaximalBicluster == true) { // the bicluster is maximal
            // print bicluster identification together with its rows
            //compute the first column
            firstColumn = tree.computeFirstColumn(node);
            //compute the last column
            lastColumn = tree.computeLastColumnGivenFirst(node,
                firstColumn);
            //compute number of columns
            numberOfColumns = tree.computeNumberOfColumns(node);
            //compute number of rows
            numberOfRows = tree.computeNumberOfRows(node);
            if (isVBTP == false) {
              nodeString = "#BICLUSTER" + "\t" + (numberOfColumns - 1) + "\t" +
                  firstColumn + "-" + lastColumn + "\t" + numberOfRows + "\t";
            }
            else {
              nodeString = "#BICLUSTER" + "\t" + numberOfColumns + "\t" +
                  firstColumn +
                  "-" + (lastColumn + 1) + "\t" + numberOfRows + "\t";
            }
            fileOut.println(nodeString);
            // get row number
            int rowNumber;
            if (isVBTP) {
              rowNumber = tree.computeRowNumber( (LeafNodeNoErrors) node) %
                  numberOfGenesInExpressionMatrix;
              if (rowNumber == 0) {
                rowNumber = numberOfGenesInExpressionMatrix;
              }
            }
            else {
              rowNumber = tree.computeRowNumber( (LeafNodeNoErrors) node);
            }
            // get gene name
            String geneName = geneNames[rowNumber - 1];
            fileOut.println(geneName);
          }
          // go right
          NodeInterface rightSybling = node.getRightSybling();
          if (rightSybling != null) {
            SuffixTreeNoErrorsPrinter.printMaximalBiclustersWithGeneNamesToFile(
                tree, fileOut, geneNames,
                rightSybling, isVBTP, numberOfGenesInExpressionMatrix);
          }
        }
      }
    }
  }

  /**
   * Prints the gene names of the leaves in the subtree of node to a file.
   *
   * @param tree SuffixTreeNoErrors
   * @param fileOut PrintWriter
   * @param geneNames String[]
   * @param node NodeInterface
   * @param isVBTP boolean
   * @param numberOfGenesInExpressionMatrix int
   */
  private static void printLeavesWithGeneNamesToFile(SuffixTreeNoErrors tree,
      PrintWriter fileOut,
      String[] geneNames,
      NodeInterface node, boolean isVBTP, int numberOfGenesInExpressionMatrix) {

    if (node instanceof InternalNodeNoErrors) {
      // go down
      NodeInterface firstChild = ( (InternalNodeNoErrors) node).
          getFirstChild();
      SuffixTreeNoErrorsPrinter.printLeavesWithGeneNamesToFile(tree, fileOut,
          geneNames, firstChild, isVBTP, numberOfGenesInExpressionMatrix);
      // go right
      NodeInterface rightSybling = node.getRightSybling();
      if (rightSybling != null) {
        SuffixTreeNoErrorsPrinter.printLeavesWithGeneNamesToFile(tree, fileOut,
            geneNames, rightSybling, isVBTP, numberOfGenesInExpressionMatrix);
      }
    }
    else {
      if (node instanceof LeafNodeNoErrors) {
        //get row number
        int rowNumber;
        if (isVBTP) {
          rowNumber = tree.computeRowNumber( (LeafNodeNoErrors) node) %
              numberOfGenesInExpressionMatrix;
          if (rowNumber == 0) {
            rowNumber = numberOfGenesInExpressionMatrix;
          }
        }
        else {
          rowNumber = tree.computeRowNumber( (LeafNodeNoErrors) node);
        }
        // print gene name
        String geneName = geneNames[rowNumber - 1];
        fileOut.println(geneName);
        // go right
        NodeInterface rightSybling = node.getRightSybling();
        if (rightSybling != null) {
          SuffixTreeNoErrorsPrinter.printLeavesWithGeneNamesToFile(tree,
              fileOut, geneNames, rightSybling, isVBTP,
              numberOfGenesInExpressionMatrix);
        }
      }
    }
  }

// ########################## PRINT GENES NAMES AND BICLUSTER PATTERN

  /**
   * Prints all the maximal biclusters together with the genes names and the bicluster pattern.
   *
   * @param tree SuffixTreeNoErrors
   * @param geneNames String[]
   * @param isVBTP boolean
   * @param numberOfGenesInExpressionMatrix int
   * @param matrixUsedToComputeSuffixTree int[][]
   */
  public static void printMaximalBiclustersWithGeneNamesAndPattern(
      SuffixTreeNoErrors tree,
      String[] geneNames, boolean isVBTP, int numberOfGenesInExpressionMatrix,
      int[][] matrixUsedToComputeSuffixTree) {
    // the root is not a bicluster
    NodeInterface firstChild = ( (InternalNodeNoErrors) tree.
                                getRoot()).
        getFirstChild();
    SuffixTreeNoErrorsPrinter.printMaximalBiclustersWithGeneNamesAndPattern(
        tree, geneNames, firstChild,
        isVBTP, numberOfGenesInExpressionMatrix, matrixUsedToComputeSuffixTree);
  }

  /**
   * Prints all the maximal biclusters together with the genes names and the bicluster pattern.
   *
   * @param tree SuffixTreeNoErrors
   * @param geneNames String[]
   * @param node NodeInterface
   * @param isVBTP boolean
   * @param numberOfGenesInExpressionMatrix int
   * @param matrixUsedToComputeSuffixTree int[][]
   */
  private static void printMaximalBiclustersWithGeneNamesAndPattern(
      SuffixTreeNoErrors tree, String[] geneNames,
      NodeInterface node, boolean isVBTP, int numberOfGenesInExpressionMatrix,
      int[][] matrixUsedToComputeSuffixTree) {
    if (node == null) {
      return;
    }
    else {
      String nodeString = new String();
      int firstColumn = -1;
      int lastColumn = -1;
      int numberOfColumns = 0;
      int numberOfRows = 0;
      boolean SuffixTreeNoErrorsPrinterNodeIsMaximalBicluster = false;
      if (node instanceof InternalNodeNoErrors) {
        SuffixTreeNoErrorsPrinterNodeIsMaximalBicluster = ( (
            InternalNodeNoErrors) node).
            getIsMaximal();
        if (SuffixTreeNoErrorsPrinterNodeIsMaximalBicluster == true) { // the bicluster is maximal
          // print bicluster identification together with its rows
          //compute the first column
          firstColumn = tree.computeFirstColumn(node);
          //compute the last column
          lastColumn = tree.computeLastColumnGivenFirst(node, firstColumn);
          //compute number of columns
          numberOfColumns = tree.computeNumberOfColumns(node);
          //compute number of rows
          numberOfRows = tree.computeNumberOfRows(node);
          if (isVBTP == false) {
            nodeString = "#BICLUSTER" + "\t" + numberOfColumns + "\t" +
                firstColumn + "-" + lastColumn + "\t" + numberOfRows + "\t";
          }
          else {
            nodeString = "#BICLUSTER" + "\t" + (numberOfColumns + 1) + "\t" +
                firstColumn + "-" + (lastColumn + 1) + "\t" + numberOfRows +
                "\t";
          }

          System.out.println(nodeString);
          //go down
          NodeInterface firstChild = ( (InternalNodeNoErrors)
                                      node).
              getFirstChild();
          SuffixTreeNoErrorsPrinter.printLeavesWithGeneNamesAndPattern(tree,
              geneNames, firstChild,
              firstColumn, lastColumn, isVBTP,
              numberOfGenesInExpressionMatrix,
              matrixUsedToComputeSuffixTree);
        }
        // go down
        NodeInterface firstChild = ( (InternalNodeNoErrors)
                                    node).
            getFirstChild();
        SuffixTreeNoErrorsPrinter.printMaximalBiclustersWithGeneNamesAndPattern(
            tree, geneNames,
            firstChild, isVBTP, numberOfGenesInExpressionMatrix,
            matrixUsedToComputeSuffixTree);
        // go right
        NodeInterface rightSybling = node.getRightSybling();
        if (rightSybling != null) {
          SuffixTreeNoErrorsPrinter.
              printMaximalBiclustersWithGeneNamesAndPattern(tree, geneNames,
              rightSybling, isVBTP, numberOfGenesInExpressionMatrix,
              matrixUsedToComputeSuffixTree);
        }
      }
      else {
        if (node instanceof LeafNodeNoErrors) {
          SuffixTreeNoErrorsPrinterNodeIsMaximalBicluster = ( (LeafNodeNoErrors)
              node).getIsMaximal();
          if (SuffixTreeNoErrorsPrinterNodeIsMaximalBicluster == true) { // the bicluster is maximal
            // print bicluster identification together with its rows
            //compute the first column
            firstColumn = tree.computeFirstColumn(node);
            //compute the last column
            lastColumn = tree.computeLastColumnGivenFirst(node,
                firstColumn);
            //compute number of columns
            numberOfColumns = tree.computeNumberOfColumns(node);
            //compute number of rows
            numberOfRows = tree.computeNumberOfRows(node);
            if (isVBTP == false) {
              nodeString = "#BICLUSTER" + "\t" + (numberOfColumns - 1) + "\t" +
                  firstColumn + "-" + lastColumn + "\t" + numberOfRows + "\t";
            }
            else {
              nodeString = "#BICLUSTER" + "\t" + numberOfColumns + "\t" +
                  firstColumn + "-" + (lastColumn + 1) + "\t" + numberOfRows +
                  "\t";
            }
            System.out.println("" + nodeString);
            // get row number
            int rowNumber = tree.computeRowNumber( (LeafNodeNoErrors) node);
            //get gene number
            int geneNumber = rowNumber % numberOfGenesInExpressionMatrix;
            if (geneNumber == 0) {
              geneNumber = numberOfGenesInExpressionMatrix;
            }
            // get gene name
            String geneName = geneNames[geneNumber - 1];
            // get pattern
            String pattern = SuffixTreeNoErrorsPrinter.getPattern(
                matrixUsedToComputeSuffixTree, rowNumber, firstColumn,
                lastColumn);
            // print gene name and pattern
            System.out.println(geneName + "\t" + pattern);
          }
          // go right
          NodeInterface rightSybling = node.getRightSybling();
          if (rightSybling != null) {
            SuffixTreeNoErrorsPrinter.
                printMaximalBiclustersWithGeneNamesAndPattern(tree, geneNames,
                rightSybling, isVBTP, numberOfGenesInExpressionMatrix,
                matrixUsedToComputeSuffixTree);
          }
        }
      }
    }
  }

  /**
   * Returns the expression pattern of a ccc-bicluster.
   *
   * @param matrixUsedToComputeSuffixTree int[][]
   * @param rowNumber int
   * @param firstColumn int
   * @param lastColumn int
   * @return String
   */
  public static String getPattern(int[][] matrixUsedToComputeSuffixTree,
                                  int rowNumber,
                                  int firstColumn, int lastColumn) {
    int[] row = matrixUsedToComputeSuffixTree[rowNumber - 1];
    String symbols = new String("");
    for (int i = firstColumn - 1; i < lastColumn; i++) {
      Integer c = new Integer( (new Integer(row[i])).toString().substring(0, 2));
      char ca = (char) c.intValue();
      symbols = symbols + (ca);
    }
    return (symbols);
  }

  /**
   * Prints the gene names of the leaves in the subtree of node.
   *
   * @param tree SuffixTreeNoErrors
   * @param geneNames String[]
   * @param node NodeInterface
   * @param firstColumn int
   * @param lastColumn int
   * @param isVBTP boolean
   * @param numberOfGenesInExpressionMatrix int
   * @param matrixUsedToComputeSuffixTree int[][]
   */
  private static void printLeavesWithGeneNamesAndPattern(SuffixTreeNoErrors
      tree, String[] geneNames,
      NodeInterface node,
      int firstColumn,
      int lastColumn,
      boolean isVBTP,
      int numberOfGenesInExpressionMatrix,
      int[][] matrixUsedToComputeSuffixTree) {

    if (node instanceof InternalNodeNoErrors) {
      // go down
      NodeInterface firstChild = ( (InternalNodeNoErrors) node).
          getFirstChild();
      SuffixTreeNoErrorsPrinter.printLeavesWithGeneNamesAndPattern(tree,
          geneNames, firstChild,
          firstColumn, lastColumn, isVBTP, numberOfGenesInExpressionMatrix,
          matrixUsedToComputeSuffixTree);

      // go right
      NodeInterface rightSybling = node.getRightSybling();
      if (rightSybling != null) {
        SuffixTreeNoErrorsPrinter.printLeavesWithGeneNamesAndPattern(tree,
            geneNames, rightSybling,
            firstColumn, lastColumn, isVBTP, numberOfGenesInExpressionMatrix,
            matrixUsedToComputeSuffixTree);
      }
    }
    else {
      if (node instanceof LeafNodeNoErrors) {
        // get row number
        int geneNumber;
        if (isVBTP) {
          geneNumber = tree.computeRowNumber( (LeafNodeNoErrors) node) %
              numberOfGenesInExpressionMatrix;
          if (geneNumber == 0) {
            geneNumber = numberOfGenesInExpressionMatrix;
          }
        }
        else {
          geneNumber = tree.computeRowNumber( (LeafNodeNoErrors) node);
        }
        // get gene name
        String geneName = geneNames[geneNumber - 1];
        // get pattern
        String pattern = SuffixTreeNoErrorsPrinter.getPattern(
            matrixUsedToComputeSuffixTree, geneNumber,
            firstColumn, lastColumn);
        // print gene name and pattern
        System.out.println(geneName + "\t" + pattern);
        // go right
        NodeInterface rightSybling = node.getRightSybling();
        if (rightSybling != null) {
          SuffixTreeNoErrorsPrinter.printLeavesWithGeneNamesAndPattern(tree,
              geneNames, rightSybling,
              firstColumn, lastColumn, isVBTP, numberOfGenesInExpressionMatrix,
              matrixUsedToComputeSuffixTree);
        }
      }
    }
  }

//#### TO FILE

  /**
   * Prints all the maximal biclusters to a file together with the genes names and the bicluster pattern.
   *
   * @param tree SuffixTreeNoErrors
   * @param filename String
   * @param geneNames String[]
   * @param isVBTP boolean
   * @param numberOfGenesInExpressionMatrix int
   * @param matrixUsedToComputeSuffixTree int[][]
   * @throws IOException
   */
  public static void printMaximalBiclustersWithGeneNamesAndPatternToFile(
      SuffixTreeNoErrors tree, String
      filename, String[] geneNames, boolean isVBTP,
      int numberOfGenesInExpressionMatrix,
      int[][] matrixUsedToComputeSuffixTree) throws IOException {
    PrintWriter fileOut = new PrintWriter(new BufferedWriter(new FileWriter(
        filename)), true);
    // the root is not a bicluster
    NodeInterface firstChild = ( (InternalNodeNoErrors) tree.
                                getRoot()).
        getFirstChild();
    SuffixTreeNoErrorsPrinter.
        printMaximalBiclustersWithGeneNamesAndPatternToFile(tree, fileOut,
        geneNames,
        firstChild, isVBTP, numberOfGenesInExpressionMatrix,
        matrixUsedToComputeSuffixTree);
    fileOut.close();
  }

  /**
   * Used recursively. Called by: Prints all the maximal biclusters to a file together with the genes names and the bicluster pattern.
   *
   * @param tree SuffixTreeNoErrors
   * @param fileOut PrintWriter
   * @param geneNames String[]
   * @param node NodeInterface
   * @param isVBTP boolean
   * @param numberOfGenesInExpressionMatrix int
   * @param matrixUsedToComputeSuffixTree int[][]
   */
  private static void printMaximalBiclustersWithGeneNamesAndPatternToFile(
      SuffixTreeNoErrors tree, PrintWriter
      fileOut, String[] geneNames, NodeInterface node, boolean isVBTP,
      int numberOfGenesInExpressionMatrix,
      int[][] matrixUsedToComputeSuffixTree) {

    if (node == null) {
      return;
    }
    else {
      String nodeString = new String();
      int firstColumn = -1;
      int lastColumn = -1;
      int numberOfColumns = 0;
      int numberOfRows = 0;
      boolean SuffixTreeNoErrorsPrinterNodeIsMaximalBicluster = false;
      if (node instanceof InternalNodeNoErrors) {
        SuffixTreeNoErrorsPrinterNodeIsMaximalBicluster = ( (
            InternalNodeNoErrors) node).
            getIsMaximal();
        if (SuffixTreeNoErrorsPrinterNodeIsMaximalBicluster == true) { // the bicluster is maximal
          // print bicluster identification together with its rows
          //compute the first column
          firstColumn = tree.computeFirstColumn(node);
          //compute the last column
          lastColumn = tree.computeLastColumnGivenFirst(node, firstColumn);
          //compute number of columns
          numberOfColumns = tree.computeNumberOfColumns(node);
          //compute number of rows
          numberOfRows = tree.computeNumberOfRows(node);
          if (isVBTP == false) {
            nodeString = "#BICLUSTER" + "\t" + numberOfColumns + "\t" +
                firstColumn + "-" + lastColumn + "\t" + numberOfRows + "\t";
          }
          else {
            nodeString = "#BICLUSTER" + "\t" + (numberOfColumns + 1) + "\t" +
                firstColumn + "-" + (lastColumn + 1) + "\t" + numberOfRows +
                "\t";
          }
          fileOut.println(nodeString);
          // go down
          NodeInterface firstChild = ( (InternalNodeNoErrors)
                                      node).
              getFirstChild();
          SuffixTreeNoErrorsPrinter.printLeavesWithGeneNamesAndPatternToFile(
              tree, fileOut, geneNames,
              firstChild, firstColumn, lastColumn, isVBTP,
              numberOfGenesInExpressionMatrix, matrixUsedToComputeSuffixTree);
        }
        // go down
        NodeInterface firstChild = ( (InternalNodeNoErrors)
                                    node).
            getFirstChild();
        SuffixTreeNoErrorsPrinter.
            printMaximalBiclustersWithGeneNamesAndPatternToFile(tree, fileOut,
            geneNames, firstChild, isVBTP, numberOfGenesInExpressionMatrix,
            matrixUsedToComputeSuffixTree);
        // go right
        NodeInterface rightSybling = node.getRightSybling();
        if (rightSybling != null) {
          SuffixTreeNoErrorsPrinter.
              printMaximalBiclustersWithGeneNamesAndPatternToFile(tree, fileOut,
              geneNames, rightSybling, isVBTP, numberOfGenesInExpressionMatrix,
              matrixUsedToComputeSuffixTree);
        }
      }
      else {
        if (node instanceof LeafNodeNoErrors) {
          SuffixTreeNoErrorsPrinterNodeIsMaximalBicluster = ( (LeafNodeNoErrors)
              node).getIsMaximal();
          if (SuffixTreeNoErrorsPrinterNodeIsMaximalBicluster == true) { // the bicluster is maximal
            // print bicluster identification together with its rows
            //compute the first column
            firstColumn = tree.computeFirstColumn(node);
            //compute the last column
            lastColumn = tree.computeLastColumnGivenFirst(node,
                firstColumn);
            //compute number of columns
            numberOfColumns = tree.computeNumberOfColumns(node);
            //compute number of rows
            numberOfRows = tree.computeNumberOfRows(node);
            if (isVBTP == false) {
              nodeString = "#BICLUSTER" + "\t" + (numberOfColumns - 1) + "\t" +
                  firstColumn + "-" + lastColumn + "\t" + numberOfRows + "\t";
            }
            else {
              nodeString = "#BICLUSTER" + "\t" + numberOfColumns + "\t" +
                  firstColumn + "-" + (lastColumn + 1) + "\t" + numberOfRows +
                  "\t";
            }

            fileOut.println(nodeString);
            // get row number
            int rowNumber = tree.computeRowNumber( (LeafNodeNoErrors) node);
            //get gene number
            int geneNumber = rowNumber % numberOfGenesInExpressionMatrix;
            if (geneNumber == 0) {
              geneNumber = numberOfGenesInExpressionMatrix;
            }
            // get gene name
            String geneName = geneNames[geneNumber - 1];
            // get pattern
            String pattern = SuffixTreeNoErrorsPrinter.getPattern(
                matrixUsedToComputeSuffixTree, rowNumber,
                firstColumn, lastColumn);
            // print gene name and pattern
            fileOut.println(geneName + "\t" + pattern);
          }
          // go right
          NodeInterface rightSybling = node.getRightSybling();
          if (rightSybling != null) {
            SuffixTreeNoErrorsPrinter.
                printMaximalBiclustersWithGeneNamesAndPatternToFile(tree,
                fileOut,
                geneNames, rightSybling, isVBTP,
                numberOfGenesInExpressionMatrix, matrixUsedToComputeSuffixTree);
          }
        }
      }
    }
  }

  /**
   * Prints the gene names of the leaves in the subtree of node to a file.
   *
   * @param tree SuffixTreeNoErrors
   * @param fileOut PrintWriter
   * @param geneNames String[]
   * @param node NodeInterface
   * @param firstColumn int
   * @param lastColumn int
   * @param isVBTP boolean
   * @param numberOfGenesInExpressionMatrix int
   * @param matrixUsedToComputeSuffixTree int[][]
   */
  private static void printLeavesWithGeneNamesAndPatternToFile(
      SuffixTreeNoErrors tree, PrintWriter fileOut,
      String[] geneNames, NodeInterface node, int firstColumn, int lastColumn,
      boolean isVBTP, int numberOfGenesInExpressionMatrix,
      int[][] matrixUsedToComputeSuffixTree) {

    if (node instanceof InternalNodeNoErrors) {
      // go down
      NodeInterface firstChild = ( (InternalNodeNoErrors) node).
          getFirstChild();
      SuffixTreeNoErrorsPrinter.printLeavesWithGeneNamesAndPatternToFile(tree,
          fileOut, geneNames,
          firstChild, firstColumn, lastColumn, isVBTP,
          numberOfGenesInExpressionMatrix, matrixUsedToComputeSuffixTree);

      // go right
      NodeInterface rightSybling = node.getRightSybling();
      if (rightSybling != null) {
        SuffixTreeNoErrorsPrinter.printLeavesWithGeneNamesAndPatternToFile(tree,
            fileOut, geneNames,
            rightSybling, firstColumn, lastColumn, isVBTP,
            numberOfGenesInExpressionMatrix, matrixUsedToComputeSuffixTree);
      }
    }
    else {
      if (node instanceof LeafNodeNoErrors) {
        // get row number
        int rowNumber = tree.computeRowNumber( (LeafNodeNoErrors) node);
        //get gene number
        int geneNumber = rowNumber % numberOfGenesInExpressionMatrix;
        if (geneNumber == 0) {
          geneNumber = numberOfGenesInExpressionMatrix;
        }
        // get gene name
        String geneName = geneNames[geneNumber - 1];
        // get pattern
        String pattern = SuffixTreeNoErrorsPrinter.getPattern(
            matrixUsedToComputeSuffixTree, rowNumber, firstColumn, lastColumn);
        // print gene name and pattern
        fileOut.println(geneName + "\t" + pattern);
        // go right
        NodeInterface rightSybling = node.getRightSybling();
        if (rightSybling != null) {
          SuffixTreeNoErrorsPrinter.printLeavesWithGeneNamesAndPatternToFile(
              tree, fileOut, geneNames,
              rightSybling, firstColumn, lastColumn, isVBTP,
              numberOfGenesInExpressionMatrix, matrixUsedToComputeSuffixTree);
        }
      }
    }
  }

  // ########################## PRINT SUFFIX TREE WITH COLORS #####################################

  /**
   * Prints the tree with colors in linear time.
   *
   * @param tree SuffixTreeNoErrors
   */
  public static void printSuffixTreeNoErrorsWithColors(SuffixTreeNoErrors tree) {
    SuffixTreeNoErrorsPrinter.printSuffixTreeNoErrorsWithColors(tree,
        tree.getRoot(), "");
  }

  /**
   *
   * @param tree SuffixTreeNoErrors
   * @param node NodeInterface
   * @param depth String
   */
  private static void printSuffixTreeNoErrorsWithColors(SuffixTreeNoErrors tree,
      NodeInterface node,
      String depth) {

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
      SuffixTreeNoErrorsPrinter.printSuffixTreeNoErrorsWithColors(tree, child,
          depth + " ");
      System.out.println(depth + "</node>");
      // go right
      NodeInterface rightSybling = node.getRightSybling();
      if (rightSybling != null) {
        SuffixTreeNoErrorsPrinter.printSuffixTreeNoErrorsWithColors(tree,
            rightSybling, depth);
      }
    }
    else {
      if (node instanceof LeafNodeNoErrors) {
        // get info in the node
        nodeString = nodeString + " " + ( (LeafNodeNoErrors) node).printInfo();
        System.out.println(depth + "<leaf " + nodeString + ">");
        // go right
        NodeInterface rightSybling = node.getRightSybling();
        if (rightSybling != null) {
          SuffixTreeNoErrorsPrinter.printSuffixTreeNoErrorsWithColors(tree,
              rightSybling, depth);
        }
      }
    }
  }

  // TO FILE

  /**
   * Prints the tree with colors in linear time to the file passed as parameter.
   *
   * @param tree SuffixTreeNoErrors
   * @param filename String
   * @throws IOException
   */
  public static void printSuffixTreeNoErrorsWithColorsToFile(SuffixTreeNoErrors
      tree, String filename) throws
      IOException {
    PrintWriter fileOut = new PrintWriter(new BufferedWriter(new FileWriter(
        filename)), true);
    SuffixTreeNoErrorsPrinter.printSuffixTreeNoErrorsWithColorsToFile(tree,
        fileOut,
        tree.getRoot(),
        "");
    fileOut.close();
  }

  /**
   * Used recursively !!!
   *
   * @param tree SuffixTreeNoErrors
   * @param fileOut PrintWriter
   * @param node NodeInterface
   * @param depth String
   * @throws IOException
   */
  private static void printSuffixTreeNoErrorsWithColorsToFile(
      SuffixTreeNoErrors tree, PrintWriter fileOut,
      NodeInterface node, String depth) throws IOException {
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
      fileOut.println(depth + "<node " + nodeString + ">");
      // go down
      NodeInterface firstChild = ( (InternalNodeNoErrors) node).
          getFirstChild();
      SuffixTreeNoErrorsPrinter.printSuffixTreeNoErrorsWithColorsToFile(tree,
          fileOut, firstChild,
          depth + " ");
      fileOut.println(depth + "</node>");
      // go right
      NodeInterface rightSybling = node.getRightSybling();
      if (rightSybling != null) {
        SuffixTreeNoErrorsPrinter.printSuffixTreeNoErrorsWithColorsToFile(tree,
            fileOut, rightSybling,
            depth);
      }
    }
    else {
      if (node instanceof LeafNodeNoErrors) {
        // get info in the node
        nodeString = nodeString + " " + ( (LeafNodeNoErrors) node).printInfo();
        fileOut.println(depth + "<leaf " + nodeString + ">");
        // go right
        NodeInterface rightSybling = node.getRightSybling();
        if (rightSybling != null) {
          SuffixTreeNoErrorsPrinter.printSuffixTreeNoErrorsWithColorsToFile(
              tree, fileOut, rightSybling,
              depth);
        }
      }
    }
  }

} // END CLASS
