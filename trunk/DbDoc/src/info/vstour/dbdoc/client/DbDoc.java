/*
 * Copyright 2010 Roman Mishchenko
 * 
 * This file is part of DbDoc. Project web is http://code.google.com/p/dbdocs/
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package info.vstour.dbdoc.client;

import static info.vstour.dbdoc.client.Constants.UNIT;
import info.vstour.dbdoc.client.bundle.Resources;
import info.vstour.dbdoc.client.event.MenuUpdateEvent;
import info.vstour.dbdoc.client.widget.DbObjectsPanel;
import info.vstour.dbdoc.client.widget.MenuPanel;

import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DbDoc implements EntryPoint {

  private final EventBus        eventBus   = new SimpleEventBus();
  private final DocServiceAsync docService = GWT.create(DocService.class);
  private DbObjectsPanel        objectsTree;

  public void onModuleLoad() {

    Window.enableScrolling(false);
    Window.setMargin("0" + UNIT);

    final VerticalPanel bodyVPanel = new VerticalPanel();
    bodyVPanel.setWidth("100%");

    final HorizontalPanel bodyHPanel = new HorizontalPanel();
    bodyHPanel.setSpacing(3);

    final HTML doc = new HTML();

    final ScrollPanel docWrapper = new ScrollPanel(doc);

    objectsTree = new DbObjectsPanel(docService);

    final MenuPanel menuPanel = new MenuPanel(docService, eventBus);
    menuPanel.ownerChangeHandlers().addChangeHandler(new ChangeHandler() {
      @Override
      public void onChange(ChangeEvent event) {
        objectsTree.clearDbObjects();
        doc.setHTML("");
        objectsTree.initDbObjects(Filter.get().getDbObjects());
      }
    });

    eventBus.addHandler(MenuUpdateEvent.TYPE, new MenuUpdateEvent.Handler() {
      @Override
      public void onMenuUpdate(MenuUpdateEvent event) {
        if (event.isNewConn()) {
          objectsTree.clear();
          doc.setHTML("");
        } else {
          objectsTree.clearDbObjects();
        }
        objectsTree.initDbObjects(Filter.get().getDbObjects());
      }
    });

    objectsTree.getTreeOpenHandler().addOpenHandler(new OpenHandler<TreeItem>() {
      public void onOpen(OpenEvent<TreeItem> event) {
        final TreeItem treeItem = event.getTarget();
        if (treeItem.getChild(0).getText().isEmpty()) {

          doc.setHTML(new Image(Resources.INSTANCE.processing()).toString());

          docService.getTreeItems(Filter.get().getConnName(), Filter.get().getOwner(), treeItem.getText(), Filter.get()
              .getFilter(), new AsyncCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> items) {
              doc.setHTML("");
              for (String item : items) {
                treeItem.addItem(item);
              }
            }
            @Override
            public void onFailure(Throwable caught) {
              doc.setHTML("");
            }
          });

          // Remove the temporary item when we finish loading
          treeItem.getChild(0).remove();
        }
      }
    });

    // Handler that gets documentation
    SelectionHandler<TreeItem> sHandler = new SelectionHandler<TreeItem>() {
      public void onSelection(SelectionEvent<TreeItem> event) {
        final TreeItem treeItem = event.getSelectedItem();

        if (treeItem.getParentItem() != null) {
          final String parent = treeItem.getParentItem().getText();
          final String child = treeItem.getText();

          doc.setHTML(new Image(Resources.INSTANCE.processing()).toString());
          docService.getDoc(Filter.get().getConnName(), Filter.get().getOwner(), parent, child, new AsyncCallback<String>() {

            public void onFailure(Throwable caught) {
              doc.setHTML(caught.toString());
            }

            public void onSuccess(String result) {
              objectsTree.cacheDoc(Filter.get().getOwner() + "." + child, result);
              doc.setHTML(result);
            }
          });
        }
      }
    };
    objectsTree.getTreeSelectionHandler().addSelectionHandler(sHandler);

    objectsTree.getCacheTreeSelectionHandler().addSelectionHandler(new SelectionHandler<TreeItem>() {
      @Override
      public void onSelection(SelectionEvent<TreeItem> event) {
        doc.setHTML(objectsTree.getCachedDoc(event.getSelectedItem().getText()));
      }
    });

    bodyHPanel.add(objectsTree);
    bodyHPanel.add(docWrapper);

    bodyVPanel.add(menuPanel);
    bodyVPanel.add(bodyHPanel);

    Window.addResizeHandler(new ResizeHandler() {

      public void onResize(ResizeEvent event) {
        int height = event.getHeight();
        int width = event.getWidth();
        bodyVPanel.setHeight(height + UNIT);
        docWrapper.setHeight(height - docWrapper.getAbsoluteTop() + UNIT);
        docWrapper.setWidth(width - docWrapper.getAbsoluteLeft() + UNIT);
        objectsTree.setHeight(height);
      }
    });

    RootPanel.get().add(bodyVPanel);

    objectsTree.setHeight(Window.getClientHeight());
    docWrapper.setHeight(Window.getClientHeight() - docWrapper.getAbsoluteTop() + UNIT);
    docWrapper.setWidth(Window.getClientWidth() - docWrapper.getAbsoluteLeft() + UNIT);

  }
}
