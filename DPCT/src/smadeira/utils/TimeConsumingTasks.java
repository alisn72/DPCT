package smadeira.utils;

import javax.swing.JProgressBar;
import java.io.Serializable;

/**
 * <p>Title: Time Consuming Tasks</p>
 *
 * <p>Description: </p>
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
 * @author Joana P. Gon�alves
 * @version 1.0
 */
public class TimeConsumingTasks implements Serializable {
  public static final long serialVersionUID = 8373759039781059188L;
  protected JProgressBar progressBar;
  protected TaskPercentages taskPercentages;

  public TimeConsumingTasks() {

  }

  public void setProgressBar(JProgressBar pb) {
    this.progressBar = pb;
  }

  public JProgressBar getProgressBar() {
    return this.progressBar;
  }

  public void setTaskPercentages(TaskPercentages taskPercentages) {
    this.taskPercentages = taskPercentages;
  }

  public TaskPercentages getTaskPercentages() {
    return taskPercentages;
  }

  //#####################################################################################################################
  //#####################################################################################################################
  // METHODS FOR UPDATING THE PROGRESS OF BICLUSTERING ALGORITHMS OR
  // POST-PROCESSING OPERATIONS ON THE GROUP OF BICLUSTERS
  //
  // These methods assume that both taskPercentages and progressBar
  // objects are non null and were properly initialized in a previous step.
  //
  // Joana P. Gon�alves
  //#####################################################################################################################
  //#####################################################################################################################

  /**
   * Updates the completed percentage of the biclustering task which
   * consists in the identification of the models (biclusters). This method
   * is supposed to be called each time a model is identified.
   * <code>numberOfBiclusters</code> contains the total number of
   * biclusters to identify, estimated or counted in a previous step,
   * which is also updated.
   *
   * @param numberOfBiclusters int[]
   */
  protected void updateBiclusterPercentDone(int[] numberOfBiclusters) {
    this.incrementSubtaskPercentDone();
    numberOfBiclusters[0] = numberOfBiclusters[0]-1;
    if (numberOfBiclusters[0] <= 0) {
      this.updatePercentDone();
    }
  }

  /**
   * Returns the percentage of the task completed, which is being
   * displayed in the progress bar.
   *
   * @return int the percentage completed (in [0,100])
   */
  public int getPercentDone() {
    return progressBar.getValue();
  }

  /**
   * Sets the percentage of the task completed with the given
   * <code>percentDone</code>.
   *
   * @param percentDone <code>int</code> the new value of the
   * completed percentage
   */
  public void setPercentDone(int percentDone) {
    int min = progressBar.getMinimum();
    int max = progressBar.getMaximum();
    if (percentDone < min) {
      progressBar.setValue(min);
      return;
    }
    if (percentDone > max) {
      progressBar.setValue(max);
      return;
    }
    progressBar.setValue(percentDone);
    progressBar.setVisible(true);
  }

  /**
   * Increments the task completed percentage in the progress bar
   * with the given <code>increment</code>.
   *
   * @param increment <code>int</code> the increment for the task percentage
   */
  public void incrementPercentDone(int increment) {
    if (increment < 0) {
      return;
    }
    int max = progressBar.getMaximum();
    int done = progressBar.getValue();
    if (done + increment > max) {
      progressBar.setValue(max);
      return;
    }

    progressBar.setValue(done+increment);
    progressBar.setVisible(true);
  }

  /**
   * Increments the completed percentage of the subtask that is running.
   *
   * @see TaskPercentages#incrementSubtaskIncrementSum
   * @see TaskPercentages#getSubtaskIncrementSum
   * @see TaskPercentages#getCurrentTotalPercent
   * @see TaskPercentages#setSubtaskIncrementSum
   */
  public void incrementSubtaskPercentDone() {
    taskPercentages.incrementSubtaskIncrementSum();
    double sum = taskPercentages.getSubtaskIncrementSum();
    if ((int)sum > 0) {
      int done = progressBar.getValue();
      int currentTotal = this.taskPercentages.getCurrentTotalPercent();
      int update = done + (int) sum;
      if (update > currentTotal) {
        progressBar.setValue(currentTotal);
        taskPercentages.setSubtaskIncrementSum(0);
        return;
      }

      progressBar.setValue(update);
      progressBar.setVisible(true);

      taskPercentages.setSubtaskIncrementSum(0);
    }
  }

  /**
   * Sets the percentage values for a new subtask.
   *
   * @param divisor <code>int</code>
   *
   * @see TaskPercentages#getNextPercentInc
   */
  public void setSubtaskValues(int divisor) {
    int percentInc = taskPercentages.getNextPercentInc();
    if (percentInc < 0) {
      return;
    }
    taskPercentages.setSubtaskPercents(
      ((percentInc * 1.0) / divisor), 0);
  }

  /**
   * Increments the completed percentage of the task.
   *
   * @see TaskPercentages#getNextPercentInc
   */
  public void incrementPercentDone() {
    int percentInc = taskPercentages.getNextPercentInc();
    if (percentInc < 0) {
      return;
    }
    this.incrementPercentDone(percentInc);
  }

  /**
   * Decrements the completed percentage of the task with the given
   * <code>decrement</code>.
   *
   * @param decrement <code>int</code>
   */
  public void decrementPercentDone(int decrement) {
    int min = progressBar.getMinimum();
    int done = progressBar.getValue();
    if (done-decrement < min) {
      progressBar.setValue(min);
      return;
    }
    progressBar.setValue(done-decrement);
    progressBar.setVisible(true);
  }

  /**
   * Updates the completed percentage of the task, assuming that
   * the task has completed.
   *
   * @see TaskPercentages#getCurrentTotalPercent
   */
  public void updatePercentDone() {
    if (progressBar.getValue() < taskPercentages.getCurrentTotalPercent()) {
      this.setPercentDone(taskPercentages.getCurrentTotalPercent());
    }
  }
}
