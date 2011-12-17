package info.vstour.dbdoc.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class MenuUpdateEvent extends GwtEvent<MenuUpdateEvent.Handler> {

  public static final Type<Handler> TYPE    = new Type<Handler>();

  boolean                           newConn = false;

  public interface Handler extends EventHandler {
    void onMenuUpdate(MenuUpdateEvent event);
  }

  public MenuUpdateEvent(boolean newConn) {
    this.newConn = newConn;
  }

  public boolean isNewConn() {
    return newConn;
  }

  @Override
  public Type<Handler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(Handler handler) {
    handler.onMenuUpdate(this);
  }

}
