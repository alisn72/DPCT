package smadeira.utils;

import smadeira.suffixtrees.*;

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
public class NodeOccurrence {

  /**
   * A node in the suffix tree corresponding the occurrence of a model with x_err errors.
   */
  private NodeInterface x;

  /**
   * Number of errors in the path label from the root to node x.
   */
  private int x_err;

  /**
   * Position where we are:
   * p = 0 => we at node x.
   * p > 0 => we are in a point p inside branch b between node x and another node x' (one of its children).
   */
  private int p;

  /**
   * Default constuctor.
   */
  public NodeOccurrence() {
  }

  /**
   *
   * @param x NodeInterface
   * @param x_err short
   * @param p short
   */
  public NodeOccurrence(NodeInterface x, int x_err, int p) {
    this.x = x;
    this.x_err = x_err;
    this.p = p;
  }

  /**
   *
   * @param x NodeInterface
   */
  public void setX(NodeInterface x) {
    this.x = x;
  }

  /**
   *
   * @param x_err int
   */
  public void setXErr(int x_err) {
    this.x_err = x_err;
  }

  /**
   *
   * @param p int
   */
  public void setP(int p) {
    this.p = p;
  }

  /**
   *
   * @return NodeInterface
   */
  public NodeInterface getX() {
    return (this.x);
  }

  /**
   *
   * @return int
   */
  public int getXErr() {
    return (this.x_err);
  }

  /**
   *
   * @return int
   */
  public int getP() {
    return (this.p);
  }

  /**
   *
   * @param o Object
   * @return boolean
   */
  public boolean equals(Object o) {
    return (o instanceof NodeOccurrence &&
            this.getP() == ( (NodeOccurrence) o).getP() &&
            this.getXErr() == ( (NodeOccurrence) o).getXErr() &&
            this.getX().equals( ( (NodeOccurrence) o).getX()));
  }
}
