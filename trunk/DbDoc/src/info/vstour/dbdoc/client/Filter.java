package info.vstour.dbdoc.client;

public class Filter {

  private static Filter instance;

  private String        connName;
  private String        owner;
  private String        filter;
  private String[]      dbObjects;

  private Filter() {};

  public static Filter get() {
    return instance == null ? instance = new Filter() : instance;
  }

  public String getConnName() {
    return connName;
  }

  public void setConnName(String connName) {
    this.connName = connName;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public String getFilter() {
    return filter;
  }

  public void setFilter(String filter) {
    this.filter = filter;
  }

  public String[] getDbObjects() {
    return dbObjects;
  }

  public void setDbObjects(String[] dbObjects) {
    this.dbObjects = dbObjects;
  }
}
