package smadeira.utils;

import smadeira.biclustering.CCC_Biclustering;
import smadeira.biclustering.CCC_Biclustering_TimeLags_Jumping_MissingValues;
import smadeira.biclustering.CCC_Biclustering_SignChanges_TimeLags_Jumping_MissingValues;
import smadeira.biclustering.CCC_Biclustering_SignChanges_TimeLags;
import smadeira.biclustering.CCC_Biclustering_TimeLags;
import smadeira.biclustering.CCC_Biclustering_SignChanges_Jumping_MissingValues;
import smadeira.biclustering.CCC_Biclustering_SignChanges;
import smadeira.biclustering.CCC_Biclustering_GeneShifts_Jumping_MissingValues;
import smadeira.biclustering.CCC_Biclustering_GeneShifts;
import smadeira.biclustering.CCC_Biclustering_Jumping_MissingValues;
import smadeira.biclustering.E_CCC_Biclustering;
import smadeira.biclustering.E_CCC_Biclustering_Allowing_MissingValues;
import smadeira.biclustering.E_CCC_Biclustering_SignChanges_TimeLags_RestrictedErrors_Jumping_MissingValues;
import smadeira.biclustering.E_CCC_Biclustering_SignChanges_TimeLags_Jumping_MissingValues;
import smadeira.biclustering.E_CCC_Biclustering_SignChanges_TimeLags;
import smadeira.biclustering.E_CCC_Biclustering_TimeLags;
import smadeira.biclustering.E_CCC_Biclustering_SignChanges_TimeLags_RestrictedErrors;
import smadeira.biclustering.E_CCC_Biclustering_TimeLags_Jumping_MissingValues;
import smadeira.biclustering.E_CCC_Biclustering_SignChanges_TimeLags_Allowing_MissingValues;
import smadeira.biclustering.E_CCC_Biclustering_SignChanges_Jumping_MissingValues;
import smadeira.biclustering.E_CCC_Biclustering_SignChanges;
import smadeira.biclustering.E_CCC_Biclustering_TimeLags_Allowing_MissingValues;
import smadeira.biclustering.E_CCC_Biclustering_TimeLags_RestrictedErrors;
import smadeira.biclustering.E_CCC_Biclustering_TimeLags_RestrictedErrors_Jumping_MissingValues;
import smadeira.biclustering.E_CCC_Biclustering_GeneShifts_RestrictedErrors_Jumping_MissingValues;
import smadeira.biclustering.E_CCC_Biclustering_GeneShifts_Jumping_MissingValues;
import smadeira.biclustering.E_CCC_Biclustering_GeneShifts;
import smadeira.biclustering.E_CCC_Biclustering_SignChanges_Allowing_MissingValues;
import smadeira.biclustering.E_CCC_Biclustering_SignChanges_RestrictedErrors;
import smadeira.biclustering.E_CCC_Biclustering_SignChanges_RestrictedErrors_Jumping_MissingValues;
import smadeira.biclustering.E_CCC_Biclustering_Jumping_MissingValues;
import smadeira.biclustering.E_CCC_Biclustering_RestrictedErrors;
import smadeira.biclustering.E_CCC_Biclustering_GeneShifts_Allowing_MissingValues;
import smadeira.biclustering.E_CCC_Biclustering_GeneShifts_RestrictedErrors;
import smadeira.biclustering.E_CCC_Biclustering_RestrictedErrors_Jumping_MissingValues;
import smadeira.biclustering.CC_TSB_Biclustering;
import java.io.Serializable;

/**
 * <p>Title: Task Percentages</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright:   Copyright (C) 2007  Joana P. Gonçalves, Sara C. Madeira
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
 * @author Joana P. Gonçalves
 * @version 1.0
 */
public class TaskPercentages implements Serializable {
  public static final long serialVersionUID = -525634809238858306L;
  public static final int FILTER = 36;
  public static final int FILTER_OVERLAPPING = 37;
  public static final int SORT = 38;
  public static final int FUNCTION_ANALYSIS_GROUPS = 39;
  public static final int HIERARCHICAL_CLUSTERING = 40;

  private static int[][] percentages = new int[][]{
      // ####################################################################
      // ####################################################################
      // CCC-Biclustering percentages
      // ####################################################################
      // 0 - get matrix to construct suffix tree
      // 1 - add tokens
      // 2 - add path length
      // 3 - add number of leaves
      // 4 - mark non left maximal nodes
      // (5) - add tokens 2
      // (6) - add path length 2
      // (7) - add number of leaves 2
      // (8) - mark non left maximal nodes 2
      // (9) - add tokens 3
      // (10) - add path length 3
      // (11) - add number of leaves 3
      // (12) - mark non left maximal nodes 3
      // 5 (or 9) (or 13) - create maximal biclusters
      // ####################################################################
      // ####################################################################
  /*  0  - CCC_Biclustering*/ {7, 65, 4, 2, 2, 20},
  /*  1  - CCC_Biclustering_JumpingMissingValues*/ {1, 36, 0, 0, 3, 60},
  /*  2  - CCC_Biclustering_GeneShifts*/ {7, 13, 1, 1, 1, 65, 1, 1, 1, 9},
  /*  3  - CCC_Biclustering_GeneShifts_JumpingMissingValues*/ {7, 13, 1, 1, 1, 65, 1, 1, 1, 9},
  /*  4  - CCC_Biclustering_SignChanges*/ {6, 26, 1, 1, 1, 25, 1, 2, 2, 35},
  /*  5  - CCC_Biclustering_SignChanges_JumpingMissingValues*/ {1,3,3,3,4,17,17,17,22,13},
  /*  6  - CCC_Biclustering_TimeLags*/ {0,1,0,0,0,1,0,0,0,2,96},
  /*  7  - CCC_Biclustering_TimeLags_JumpingMissingValues*/ {0,1,0,0,0,1,0,0,0,2,96},
  /*  8  - CCC_Biclustering_SignChanges_TimeLags*/ {0,1,0,0,0,1,0,0,0,1,0,0,0,2,95},
  /*  9  - CCC_Biclustering_SignChanges_TimeLags_JumpingMissingValues*/ {0,1,0,0,0,1,0,0,0,1,0,0,0,2,95},
      // ####################################################################
      // ####################################################################
      // e-CCC-Biclustering percentages
      // ####################################################################
      // 0 - get matrix to construct suffix trees
      // 1 - add tokens
      // 2 - add number of leaves
      // 3 - add color array
      // 4 - spell models
      // (5) - create time lag models
      // (6) - add time lag models
      // (7) - delete non right maximal
      // 5 (8) - delete non left maximal
      // 6 (9) - delete repeated
      // 7 (10) - create biclusters
  /*  10 - E_CCC_Biclustering */ {2,22,1,3,24,1,1,46},
  /*  11 - E_CCC_Biclustering_Allowing_MissingValues */ {1,9,1,2,40,1,1,45},
  /*  12 - E_CCC_Biclustering_GeneShifts_Allowing_MissingValues */ {1,8,1,3,45,1,1,40},
  /*  13 - E_CCC_Biclustering_GeneShifts_Jumping_MissingValues */ {1,32,1,6,57,1,1,1},
  /*  14 - E_CCC_Biclustering_GeneShifts_RestrictedErrors_Jumping_MissingValues */ {0,22,0,4,71,1,1,1},
  /*  15 - E_CCC_Biclustering_GeneShifts_RestrictedErrors */ {0,34,0,8,55,1,1,1},
  /*  16 - E_CCC_Biclustering_GeneShifts */ {1,2,0,1,91,1,1,3},
  /*  17 - E_CCC_Biclustering_Jumping_MissingValues */ {1,32,0,6,57,0,0,4},
  /*  18 - E_CCC_Biclustering_RestrictedErrors_Jumping_MissingValues */ {0,20,0,4,73,0,0,3},
  /*  19 - E_CCC_Biclustering_RestrictedErrors */ {2,22,1,3,24,1,1,46},
  /*  20 - E_CCC_Biclustering_SignChanges_Allowing_MissingValues */ {1,2,0,1,89,1,1,5},
  /*  21 - E_CCC_Biclustering_SignChanges_Jumping_MissingValues */ {1,20,1,3,72,1,1,1},
  /*  22 - E_CCC_Biclustering_SignChanges_RestrictedErrors_Jumping_MissingValues */ {1,24,1,5,66,1,1,1},
  /*  23 - E_CCC_Biclustering_SignChanges_RestrictedErrors */ {1,4,1,1,84,4,4,5},
  /*  24 - E_CCC_Biclustering_SignChanges_TimeLags_Allowing_MissingValues */ {0,0,0,0,0,1,0,24,51,24,0},
  /*  25 - E_CCC_Biclustering_SignChanges_TimeLags_Jumping_MissingValues */ {0,0,0,0,0,1,0,24,51,24,0},
  /*  26 - E_CCC_Biclustering_SignChanges_TimeLags_RestrictedErrors_Jumping_MissingValues */ {0,0,0,0,0,1,0,24,51,24,0},
  /*  27 - E_CCC_Biclustering_SignChanges_TimeLags_RestrictedErrors */ {0,0,0,0,0,1,0,13,51,35,0},
  /*  28 - E_CCC_Biclustering_SignChanges_TimeLags */ {0,0,0,0,0,1,0,30,68,1,0},
  /*  29 - E_CCC_Biclustering_SignChanges */ {1,4,1,1,84,4,4,5},
  /*  30 - E_CCC_Biclustering_TimeLags_Allowing_MissingValues */ {0,0,0,0,1,1,0,7,89,2,0},
  /*  31 - E_CCC_Biclustering_TimeLags_Jumping_MissingValues */ {0,0,0,0,1,1,0,7,89,2,0},
  /*  32 - E_CCC_Biclustering_TimeLags_RestrictedErrors_Jumping_MissingValues */ {0,0,0,0,0,1,0,13,51,35,0},
  /*  33 - E_CCC_Biclustering_TimeLags_RestrictedErrors */ {0,0,0,0,0,1,0,13,51,35,0},
  /*  34 - E_CCC_Biclustering_TimeLags */ {0,0,0,0,0,1,0,30,68,1,0},
  /*  35 - CC_TSB_Biclustering */ {100},
  /*  36 - FILTER BICLUSTERS */ {100},
  /*  37 - FILTER OVERLAPPING BICLUSTERS */ {92,8},
  /*  38 - SORT */ {95,5},
  /*  39 - FUNCTION ANALYSIS GROUPS OF BICLUSTERS */ {100},
  /*  40 - HIERARCHICAL CLUSTERING CENTROID */ {1, 98, 0, 1}
  };

  private int rowIndex;
  private int columnIndex;
  private int currentTotalPercent;
  private double subtaskIncrement;
  private double subtaskIncrementSum;
  private int numberOfShifts;

  public TaskPercentages(CCC_Biclustering biclustering) {
    if (biclustering instanceof CCC_Biclustering_TimeLags_Jumping_MissingValues) {
      rowIndex = 7;
    }
    else if (biclustering instanceof CCC_Biclustering_SignChanges_TimeLags_Jumping_MissingValues) {
      rowIndex = 9;
    }
    else if (biclustering instanceof CCC_Biclustering_SignChanges_TimeLags) {
      rowIndex = 8;
    }
    else if (biclustering instanceof CCC_Biclustering_TimeLags) {
      rowIndex = 6;
    }
    else if (biclustering instanceof CCC_Biclustering_SignChanges_Jumping_MissingValues) {
      rowIndex = 5;
    }
    else if (biclustering  instanceof CCC_Biclustering_SignChanges) {
      rowIndex = 4;
    }
    else if (biclustering instanceof CCC_Biclustering_GeneShifts_Jumping_MissingValues) {
      rowIndex = 3;
    }
    else if (biclustering instanceof CCC_Biclustering_GeneShifts) {
      rowIndex = 2;
    }
    else if (biclustering instanceof CCC_Biclustering_Jumping_MissingValues) {
      rowIndex = 1;
    }
    else if (biclustering instanceof CCC_Biclustering) {
      rowIndex = 0;
    }
    columnIndex = -1;
    currentTotalPercent = 0;
  }

  public TaskPercentages(E_CCC_Biclustering biclustering) {
    if (biclustering instanceof E_CCC_Biclustering_Allowing_MissingValues) {
      rowIndex = 11;
    }
    else if (biclustering instanceof E_CCC_Biclustering_RestrictedErrors_Jumping_MissingValues) {
      rowIndex = 18;
    }
    else if (biclustering instanceof E_CCC_Biclustering_Jumping_MissingValues) {
      rowIndex = 17;
    }
    else if (biclustering instanceof E_CCC_Biclustering_RestrictedErrors) {
      rowIndex = 19;
    }
    else if (biclustering instanceof E_CCC_Biclustering_GeneShifts_Allowing_MissingValues) {
      numberOfShifts = ((E_CCC_Biclustering_GeneShifts_Allowing_MissingValues)biclustering).getNumberOfShifts();
      rowIndex = 12;
    }
    else if (biclustering instanceof E_CCC_Biclustering_GeneShifts_RestrictedErrors) {
      numberOfShifts = ((E_CCC_Biclustering_GeneShifts_RestrictedErrors)biclustering).getNumberOfShifts();
      rowIndex = 15;
    }
    else if (biclustering instanceof E_CCC_Biclustering_GeneShifts_RestrictedErrors_Jumping_MissingValues) {
      numberOfShifts = ((E_CCC_Biclustering_GeneShifts_RestrictedErrors_Jumping_MissingValues)biclustering).getNumberOfShifts();
      rowIndex = 14;
    }
    else if (biclustering instanceof E_CCC_Biclustering_GeneShifts_Jumping_MissingValues) {
      numberOfShifts = ((E_CCC_Biclustering_GeneShifts_Jumping_MissingValues)biclustering).getNumberOfShifts();
      rowIndex = 13;
    }
    else if (biclustering instanceof E_CCC_Biclustering_GeneShifts) {
      numberOfShifts = ((E_CCC_Biclustering_GeneShifts)biclustering).getNumberOfShifts();
      rowIndex = 16;
    }
    else if (biclustering instanceof E_CCC_Biclustering_SignChanges_Allowing_MissingValues) {
      rowIndex = 20;
    }
    else if (biclustering instanceof E_CCC_Biclustering_SignChanges_RestrictedErrors) {
      rowIndex = 23;
    }
    else if (biclustering instanceof E_CCC_Biclustering_SignChanges_RestrictedErrors_Jumping_MissingValues) {
      rowIndex = 22;
    }
    else if (biclustering instanceof E_CCC_Biclustering_SignChanges_Jumping_MissingValues) {
      rowIndex = 21;
    }
    else if (biclustering instanceof E_CCC_Biclustering_SignChanges) {
      rowIndex = 29;
    }
    else if (biclustering instanceof E_CCC_Biclustering_TimeLags_Allowing_MissingValues) {
      rowIndex = 30;
    }
    else if (biclustering instanceof E_CCC_Biclustering_TimeLags_RestrictedErrors) {
      rowIndex = 33;
    }
    else if (biclustering instanceof E_CCC_Biclustering_TimeLags_RestrictedErrors_Jumping_MissingValues) {
      rowIndex = 32;
    }
    else if (biclustering instanceof E_CCC_Biclustering_TimeLags_Jumping_MissingValues) {
      rowIndex = 31;
    }
    else if (biclustering instanceof E_CCC_Biclustering_SignChanges_TimeLags_RestrictedErrors) {
      rowIndex = 27;
    }
    else if (biclustering instanceof E_CCC_Biclustering_SignChanges_TimeLags_Allowing_MissingValues) {
      rowIndex = 24;
    }
    else if (biclustering instanceof E_CCC_Biclustering_SignChanges_TimeLags_RestrictedErrors_Jumping_MissingValues) {
      rowIndex = 26;
    }
    else if (biclustering instanceof E_CCC_Biclustering_SignChanges_TimeLags_Jumping_MissingValues) {
      rowIndex = 25;
    }
    else if (biclustering instanceof E_CCC_Biclustering_SignChanges_TimeLags) {
      rowIndex = 28;
    }
    else if (biclustering instanceof E_CCC_Biclustering_TimeLags) {
      rowIndex = 34;
    }
    else if (biclustering instanceof E_CCC_Biclustering) {
      rowIndex = 10;
    }

    columnIndex = -1;
    currentTotalPercent = 0;
  }

  public TaskPercentages(CC_TSB_Biclustering biclustering) {
    rowIndex = 35;
    columnIndex = -1;
    currentTotalPercent = 0;
  }

  public TaskPercentages(int rowIndex) {
    if (rowIndex < 0 || rowIndex > this.percentages.length-1) {
      return;
    }
    this.rowIndex = rowIndex;
    columnIndex = -1;
    currentTotalPercent = 0;
  }

  public int getNextPercentInc() {
    if (columnIndex == percentages[rowIndex].length-1) {
      return -1;
    }
    columnIndex++;
    int percentInc = percentages[rowIndex][columnIndex];
    currentTotalPercent += percentInc;
    return percentInc;
  }

  public int getCurrentPercentInc() {
    return percentages[rowIndex][columnIndex];
  }

  public int getCurrentTotalPercent() {
    return this.currentTotalPercent;
  }

  public double getSubtaskIncrement() {
    return subtaskIncrement;
  }

  public double getSubtaskIncrementSum() {
    return subtaskIncrementSum;
  }

  public void setSubtaskIncrementSum(double incrementSum) {
    this.subtaskIncrementSum = incrementSum;
  }

  public void incrementSubtaskIncrementSum() {
    subtaskIncrementSum += subtaskIncrement;
  }

  public void setSubtaskPercents(double increment, double incrementSum) {
    this.subtaskIncrement = increment;
    this.subtaskIncrementSum = incrementSum;
  }

  public int computeSpellModelsTaskDivisor(int numRows, int numColumns, int maxNumberOfErrors, int alphabetSize) {
    if (rowIndex > 11 && rowIndex < 17) {
      return (int)
        ((numRows * Math.pow(numColumns, 3 + maxNumberOfErrors) *
          2*Math.pow(alphabetSize, this.numberOfShifts))
          / (Math.pow(numColumns, 1 + maxNumberOfErrors) + 2*numRows/numColumns));
    }
    return (int)
        ((numRows * Math.pow(numColumns, 3 + maxNumberOfErrors))
          / (Math.pow(numColumns, 1 + maxNumberOfErrors) + 2*numRows/numColumns));
  }
}
