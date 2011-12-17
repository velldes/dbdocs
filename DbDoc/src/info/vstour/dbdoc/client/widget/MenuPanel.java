package info.vstour.dbdoc.client.widget;

import static info.vstour.dbdoc.client.Constants.UNIT;
import info.vstour.dbdoc.client.DocServiceAsync;
import info.vstour.dbdoc.client.Filter;
import info.vstour.dbdoc.client.bundle.ClientConstants;
import info.vstour.dbdoc.client.event.MenuUpdateEvent;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

public class MenuPanel extends Composite {

  private ListBox connListBox   = new ListBox();
  private ListBox ownerListBox  = new ListBox();
  private TextBox filterTextBox = new TextBox();

  public MenuPanel(final DocServiceAsync docService, final EventBus eventBus) {

    HorizontalPanel menuWrapper = new HorizontalPanel();
    menuWrapper.setWidth("100%");
    menuWrapper.setHeight("40" + UNIT);
    menuWrapper.addStyleName("menu");
    menuWrapper.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

    ownerListBox.setWidth("100" + UNIT);

    connListBox.addItem("");
    docService.getPropsList(new AsyncCallback<String[]>() {
      @Override
      public void onSuccess(String[] names) {
        if (names != null)
          for (String name : names) {
            connListBox.addItem(name.substring(0, name.indexOf(".")));
          }
      }
      @Override
      public void onFailure(Throwable caught) {
        // TODO Auto-generated method stub
      }
    });

    connListBox.addChangeHandler(new ChangeHandler() {
      @Override
      public void onChange(ChangeEvent event) {
        ownerListBox.clear();
        final String connName = connListBox.getItemText(connListBox.getSelectedIndex());
        Filter.get().setConnName(connName);
        if (!connName.isEmpty()) {
          docService.getOwners(connName, new AsyncCallback<String[]>() {
            @Override
            public void onSuccess(String[] owners) {
              if (owners != null) {
                for (String owner : owners) {
                  ownerListBox.addItem(owner);
                }
              }
              Filter.get().setOwner(ownerListBox.getItemText(ownerListBox.getSelectedIndex()));

              docService.getObjects(Filter.get().getConnName(), new AsyncCallback<String[]>() {
                @Override
                public void onSuccess(String[] names) {
                  Filter.get().setDbObjects(names);
                  eventBus.fireEvent(new MenuUpdateEvent(true));
                }
                @Override
                public void onFailure(Throwable caught) {
                  // TODO Auto-generated method stub
                }
              });
            }
            @Override
            public void onFailure(Throwable caught) {
              // TODO Auto-generated method stub
            }
          });
        }
      }
    });

    ownerListBox.addChangeHandler(new ChangeHandler() {
      @Override
      public void onChange(ChangeEvent event) {
        Filter.get().setOwner(ownerListBox.getItemText(ownerListBox.getSelectedIndex()));
      }
    });

    filterTextBox.addKeyUpHandler(new KeyUpHandler() {
      @Override
      public void onKeyUp(KeyUpEvent event) {
        Filter.get().setFilter(filterTextBox.getText());
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
          eventBus.fireEvent(new MenuUpdateEvent(false));
        }
      }
    });

    Label connLabel = new Label(ClientConstants.INSTANCE.connection());
    Label ownerLabel = new Label(ClientConstants.INSTANCE.owner());

    Label filterLabel = new Label(ClientConstants.INSTANCE.filter());

    filterTextBox.addStyleName("filterTextBox");

    HorizontalPanel menuHPanel = new HorizontalPanel();
    menuHPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
    menuHPanel.setSpacing(3);
    menuHPanel.add(connLabel);
    menuHPanel.add(connListBox);
    menuHPanel.add(ownerLabel);
    menuHPanel.add(ownerListBox);
    menuHPanel.add(filterLabel);
    menuHPanel.add(filterTextBox);

    menuWrapper.add(menuHPanel);

    initWidget(menuWrapper);
  }

  public HasChangeHandlers connChangeHandlers() {
    return connListBox;
  }

  public HasChangeHandlers ownerChangeHandlers() {
    return ownerListBox;
  }
}
