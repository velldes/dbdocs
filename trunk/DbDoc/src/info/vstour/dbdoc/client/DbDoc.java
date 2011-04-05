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

import java.util.Date;
import java.util.Iterator;
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
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DbDoc implements EntryPoint {

	private final String	        UNIT	     = "px";
	private final DocServiceAsync	docService	= GWT.create(DocService.class);
	private ClientConstants	      constants	 = GWT.create(ClientConstants.class);
	private String	              connName;

	public void onModuleLoad() {

		Window.enableScrolling(false);
		Window.setMargin("0px");

		final VerticalPanel bodyVPanel = new VerticalPanel();
		final HorizontalPanel bodyHPanel = new HorizontalPanel();
		bodyHPanel.setSpacing(3);

		final HTML doc = new HTML();

		// Menu START
		HorizontalPanel menuWrapper = new HorizontalPanel();
		final int menuHeight = 40;
		final int diffHeight = menuHeight + 10;
		final int diffWidth = 260;
		menuWrapper.setWidth("100%");
		menuWrapper.setHeight(menuHeight + UNIT);
		menuWrapper.addStyleName("menu");
		menuWrapper.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		final ListBox propsListBox = new ListBox();
		propsListBox.addItem("");
		docService.getPropsList(new AsyncCallback<String[]>() {
			@Override
			public void onSuccess(String[] result) {
				if (result != null)
					for (int i = 0; i < result.length; i++) {
						propsListBox.addItem(result[i].substring(0, result[i].indexOf(".")));
					}
			}
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}
		});

		Label connLabel = new Label(constants.connection());

		Label searchLabel = new Label(constants.search());
		final TextBox searchTextBox = new TextBox();
		searchTextBox.addStyleName("searchTextBox");

		HorizontalPanel menuHPanel = new HorizontalPanel();
		menuHPanel.setSpacing(3);
		menuHPanel.add(connLabel);
		menuHPanel.add(propsListBox);
		menuHPanel.add(searchLabel);
		menuHPanel.add(searchTextBox);

		menuWrapper.add(menuHPanel);
		// Menu END

		final ScrollPanel docWrapper = new ScrollPanel(doc);
		docWrapper.setWidth(Window.getClientWidth() - diffWidth + UNIT);
		docWrapper.setHeight(Window.getClientHeight() - diffHeight + UNIT);

		// Tree START
		final Tree tree = new Tree();
		final ScrollPanel treeWrapper = new ScrollPanel(tree);
		treeWrapper.addStyleName("tree");
		treeWrapper.setHeight(Window.getClientHeight() - diffHeight + UNIT);

		propsListBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				tree.clear();
				doc.setHTML("");
				int index = propsListBox.getSelectedIndex();
				connName = propsListBox.getItemText(index);
				if (!connName.isEmpty()) {
					docService.getObjects(connName, new AsyncCallback<String[]>() {
						@Override
						public void onSuccess(String[] result) {
							for (int i = 0; i < result.length; i++) {
								TreeItem item = tree.addItem(result[i].trim());
								// Temporarily add an item so we can expand this node
								item.addItem("");
							}
						}
						@Override
						public void onFailure(Throwable caught) {
							// TODO Auto-generated method stub

						}
					});
				}
				// save menu state
				String settings = searchTextBox.getText();
				final long DURATION = 1000 * 60 * 60 * 24 * 14;
				Date expires = new Date(new Date().getTime() + DURATION);
				Cookies.setCookie(constants.settingsCookies(), settings, expires);
			}
		});

		// Add a handler that automatically generates some children
		tree.addOpenHandler(new OpenHandler<TreeItem>() {
			public void onOpen(OpenEvent<TreeItem> event) {
				final TreeItem item = event.getTarget();
				if (item.getChild(0).getText().isEmpty()) {
					// Close the item immediately
					//					item.setState(false, false);

					String itemName = item.getText();
					String search = searchTextBox.getText();
					docService.getTreeItems(connName, itemName, search, new AsyncCallback<List<String>>() {
						@Override
						public void onSuccess(List<String> result) {
							for (Iterator<String> iterator = result.iterator(); iterator.hasNext();) {
								item.addItem((String) iterator.next());
							}
						}
						@Override
						public void onFailure(Throwable caught) {
							// TODO Auto-generated method stub
						}
					});

					// Remove the temporary item when we finish loading
					item.getChild(0).remove();

					// Reopen the item
					//					item.setState(true, false);
				}
			}
		});

		// Handler that gets documentation
		SelectionHandler<TreeItem> sHandler = new SelectionHandler<TreeItem>() {
			public void onSelection(SelectionEvent<TreeItem> event) {
				TreeItem item = event.getSelectedItem();

				if (item.getParentItem() != null) {
					final String parent = item.getParentItem().getText();
					String child = item.getText();
					int index = propsListBox.getSelectedIndex();
					docService.getDoc(propsListBox.getItemText(index), parent, child, new AsyncCallback<String>() {

						public void onFailure(Throwable caught) {
							doc.setHTML(caught.toString());
						}

						public void onSuccess(String result) {
							doc.setHTML(result);
						}
					});
				}
			}

		};
		tree.addSelectionHandler(sHandler);
		// Tree END

		bodyHPanel.add(treeWrapper);
		bodyHPanel.add(docWrapper);

		bodyVPanel.add(menuWrapper);
		bodyVPanel.add(bodyHPanel);
		bodyVPanel.setWidth("100%");
		bodyVPanel.setHeight(Window.getClientHeight() + UNIT);
		Window.addResizeHandler(new ResizeHandler() {

			public void onResize(ResizeEvent event) {
				int height = event.getHeight();
				int width = event.getWidth();
				bodyVPanel.setHeight(height + UNIT);
				docWrapper.setWidth(width - diffWidth + UNIT);
				docWrapper.setHeight(height - diffHeight + UNIT);
				treeWrapper.setHeight(height - diffHeight + UNIT);
			}
		});

		RootPanel.get().add(bodyVPanel);

		// Load menu state
		if (Cookies.getCookie(constants.settingsCookies()) != null) {
			try {
				searchTextBox.setText(Cookies.getCookie(constants.settingsCookies()));
			}
			catch (Exception e) {
				Cookies.removeCookie(constants.settingsCookies());
			}
		}
	}
}
