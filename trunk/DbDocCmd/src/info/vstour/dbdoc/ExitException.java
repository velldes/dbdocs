package info.vstour.dbdoc;

public class ExitException extends Exception {

  private int               status           = 1;

  private static final long serialVersionUID = 4272425892811620053L;

  public int getStatus() {
    return status;
  }
}
