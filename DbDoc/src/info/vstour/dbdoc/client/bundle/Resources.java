package info.vstour.dbdoc.client.bundle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Resources extends ClientBundle {

  public static final Resources INSTANCE = GWT.create(Resources.class);

  @Source("processing.gif")
  ImageResource processing();

}
