package smadeira.utils;

import java.util.*;

/**
 * <p>Title: </p>
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
 * @author Sara C. Madeira
 * @version 1.0
 */
public class Shift_Pattern {

  /**
   *
   * @param pattern char[]
   * @param alphabet char[]
   * @param numberOfShifts int
   * @return ArrayList
   */
  private static ArrayList<char[]> shiftPatternDown(char[] pattern,
      char[] alphabet, int numberOfShifts) {
    ArrayList<char[]> patternshiftedDown = new ArrayList<char[]> (0);
    char[] patternToShift = pattern.clone();
    boolean firstSymbolReached = false;
    for (int i = 1; i <= numberOfShifts && !firstSymbolReached; i++) {
      char[] patternShifted = new char[pattern.length];
      // shift each symbol down
      for (int j = 0; j < pattern.length; j++) {
        int positionInAlphabet = 0;
        boolean symbolFound = false;
        while (symbolFound == false && positionInAlphabet < alphabet.length) {
          if (alphabet[positionInAlphabet] == patternToShift[j]) {
            symbolFound = true;
          }
          else {
            positionInAlphabet++;
          }
        }
        if (positionInAlphabet == 0) {
          firstSymbolReached = true;
        }
        else {
          patternShifted[j] = alphabet[positionInAlphabet - 1];
        }
      }
      if (!firstSymbolReached) {
        patternshiftedDown.add(patternShifted);
        patternToShift = patternShifted.clone();
      }
    }
    return patternshiftedDown;
  }

//###############################################################################################################################

  /**
   *
   * @param pattern char[]
   * @param alphabet char[]
   * @param numberOfShifts int
   * @return ArrayList
   */
  private static ArrayList<char[]> shiftPatternUp(char[] pattern,
                                                  char[] alphabet,
                                                  int numberOfShifts) {
    ArrayList<char[]> patternShiftedUp = new ArrayList<char[]> (0);
    char[] patternToShift = pattern.clone();
    boolean lastSymbolReached = false;
    for (int i = 1; i <= numberOfShifts && !lastSymbolReached; i++) {
      char[] patternShifted = new char[pattern.length];
      // shift each symbol up
      for (int j = 0; j < pattern.length; j++) {
        int positionInAlphabet = 0;
        boolean symbolFound = false;
        while (symbolFound == false && positionInAlphabet < alphabet.length) {
          if (alphabet[positionInAlphabet] == patternToShift[j]) {
            symbolFound = true;
          }
          else {
            positionInAlphabet++;
          }
        }
        if (positionInAlphabet == alphabet.length - 1) {
          lastSymbolReached = true;
        }
        else {
          patternShifted[j] = alphabet[positionInAlphabet + 1];
        }
      }
      if (!lastSymbolReached) {
        patternShiftedUp.add(patternShifted);
        patternToShift = patternShifted.clone();
      }
    }
    return patternShiftedUp;
  }

  //###############################################################################################################################
  //###############################################################################################################################

  /**
   *
   * @param pattern char[]
   * @param alphabet char[]
   * @param numberOfShifts int
   * @return ArrayList
   */
  public static ArrayList<char[]> shiftPattern(char[] pattern, char[] alphabet,
                                               int numberOfShifts) {
    ArrayList<char[]> patternShiffted = new ArrayList<char[]> (0);
    patternShiffted.addAll(Shift_Pattern.shiftPatternDown(pattern, alphabet,
        numberOfShifts));
    patternShiffted.addAll(Shift_Pattern.shiftPatternUp(pattern, alphabet,
        numberOfShifts));
    return (patternShiffted);
  }

  //###############################################################################################################################
  //###############################################################################################################################

  /**
   *
   * @param pattern char[]
   * @param alphabet char[]
   * @param numberOfShifts int
   * @param missingValue char
   * @return ArrayList
   */
  private static ArrayList<char[]> shiftPatternDown(char[] pattern,
      char[] alphabet, int numberOfShifts, char missingValue) {
    ArrayList<char[]> patternshiftedDown = new ArrayList<char[]> (0);
    char[] patternToShift = pattern.clone();
    boolean firstSymbolReached = false;
    for (int i = 1; i <= numberOfShifts && !firstSymbolReached; i++) {
      char[] patternShifted = new char[pattern.length];
      // shift each symbol down
      for (int j = 0; j < pattern.length; j++) {
        // MISSING VALUE IS NOT SHIFTED
        if (patternToShift[j] == missingValue) {
          patternShifted[j] = patternToShift[j];
        }
        else {
          int positionInAlphabet = 0;
          boolean symbolFound = false;
          while (symbolFound == false && positionInAlphabet < alphabet.length) {
            if (alphabet[positionInAlphabet] == patternToShift[j]) {
              symbolFound = true;
            }
            else {
              positionInAlphabet++;
            }
          }
          if (positionInAlphabet == 0) {
            firstSymbolReached = true;
          }
          else {
            patternShifted[j] = alphabet[positionInAlphabet - 1];
          }
        }
      }
      if (!firstSymbolReached) {
        patternshiftedDown.add(patternShifted);
        patternToShift = patternShifted.clone();
      }
    }
    return patternshiftedDown;
  }

//###############################################################################################################################

  /**
   *
   * @param pattern char[]
   * @param alphabet char[]
   * @param numberOfShifts int
   * @param missingValue char
   * @return ArrayList
   */
  private static ArrayList<char[]> shiftPatternUp(char[] pattern,
                                                  char[] alphabet,
                                                  int numberOfShifts, char missingValue) {
    ArrayList<char[]> patternShiftedUp = new ArrayList<char[]> (0);
    char[] patternToShift = pattern.clone();
    boolean lastSymbolReached = false;
    for (int i = 1; i <= numberOfShifts && !lastSymbolReached; i++) {
      char[] patternShifted = new char[pattern.length];
      // shift each symbol up
      for (int j = 0; j < pattern.length; j++) {
        // MISSING VALUE IS NOT SHIFTED
        if (patternToShift[j] == missingValue) {
          patternShifted[j] = patternToShift[j];
        }
        else {
          int positionInAlphabet = 0;
          boolean symbolFound = false;
          while (symbolFound == false && positionInAlphabet < alphabet.length) {
            if (alphabet[positionInAlphabet] == patternToShift[j]) {
              symbolFound = true;
            }
            else {
              positionInAlphabet++;
            }
          }
          if (positionInAlphabet == alphabet.length - 1) {
            lastSymbolReached = true;
          }
          else {
            patternShifted[j] = alphabet[positionInAlphabet + 1];
          }
        }
      }
      if (!lastSymbolReached) {
        patternShiftedUp.add(patternShifted);
        patternToShift = patternShifted.clone();
      }
    }
    return patternShiftedUp;
  }

  //###############################################################################################################################
  //###############################################################################################################################

  /**
   *
   * @param pattern char[]
   * @param alphabet char[]
   * @param numberOfShifts int
   * @param missingValue char
   * @return ArrayList
   */
  public static ArrayList<char[]> shiftPattern(char[] pattern, char[] alphabet,
                                               int numberOfShifts, char missingValue) {
    ArrayList<char[]> patternShiffted = new ArrayList<char[]> (0);
    patternShiffted.addAll(Shift_Pattern.shiftPatternDown(pattern, alphabet, numberOfShifts, missingValue));
    patternShiffted.addAll(Shift_Pattern.shiftPatternUp(pattern, alphabet,numberOfShifts, missingValue));
    return (patternShiffted);
  }

}
