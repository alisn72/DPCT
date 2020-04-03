package bicat.preprocessor;

/**
 * <p>Title: BicAT Tool </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * @author Amela Prelic
 * @version 1.0
 *
 **/

public class FileOffsetException extends Exception {

  static String err_msg = "File offset of the input file is incorrect.\n";

  public FileOffsetException() { }

  public String printStackTraceMy() { return err_msg; }

}