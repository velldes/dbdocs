package info.vstour.dbdoc.client.widget;

import static info.vstour.dbdoc.client.Constants.UNIT;
import info.vstour.dbdoc.client.DocServiceAsync;
import info.vstour.dbdoc.client.bundle.ClientConstants;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasOpenHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class DbObjectsPanel extends Composite {

  private Tree                dbTree      = new Tree();
  private Tree                cacheTree   = new Tree();
  private ScrollPanel         treeWrapper = new ScrollPanel(dbTree);
  private FlexTable           layout      = new FlexTable();
  private Map<String, String> docCache;

  public DbObjectsPanel(final DocServiceAsync docService) {
    docCache = new HashMap<String, String>();

    treeWrapper.addStyleName("tree");
    layout.addStyleName("selected");

    layout.setText(0, 0, ClientConstants.INSTANCE.DbObjects());
    layout.setText(0, 1, ClientConstants.INSTANCE.CachedObjects());
    layout.setWidget(1, 0, treeWrapper);

    final FlexCellFormatter fmt = layout.getFlexCellFormatter();
    fmt.addStyleName(0, 0, "pointerHand");
    fmt.addStyleName(0, 1, "pointerHand");
    fmt.addStyleName(0, 1, "unselected");
    fmt.setColSpan(1, 0, 2);

    layout.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        Cell cell = layout.getCellForEvent(event);
        int row = cell.getRowIndex();
        int coll = cell.getCellIndex();
        if (row == 0 && coll == 0) {
          fmt.removeStyleName(0, 0, "unselected");
          fmt.addStyleName(0, 1, "unselected");
          treeWrapper.setWidget(dbTree);
        }
        if (row == 0 && coll == 1) {
          fmt.removeStyleName(0, 1, "unselected");
          fmt.addStyleName(0, 0, "unselected");
          treeWrapper.setWidget(cacheTree);
        }
      }
    });

    initWidget(layout);
  }

  public void clear() {
    clearDbObjects();
    clearCacheObjects();
  }

  public void clearDbObjects() {
    dbTree.clear();
  }

  public void clearCacheObjects() {
    cacheTree.clear();
    docCache.clear();
  }

  public void cacheDoc(String key, String value) {
    if (!docCache.containsKey(key)) {
      cacheTree.addItem(key);
      docCache.put(key, value);
    }
  }

  public String getCachedDoc(String key) {
    return docCache.get(key);
  }

  public void setHeight(int height) {
    treeWrapper.setHeight(height - (treeWrapper.getAbsoluteTop() + 5) + UNIT);
  };

  public void initDbObjects(String[] names) {
    for (String name : names) {
      TreeItem item = dbTree.addItem(name.trim());
      // Temporarily add an item so we can expand this node
      item.addItem("");
    }

  }

  public HasSelectionHandlers<TreeItem> getTreeSelectionHandler() {
    return dbTree;
  }

  public HasOpenHandlers<TreeItem> getTreeOpenHandler() {
    return dbTree;
  }

  public HasSelectionHandlers<TreeItem> getCacheTreeSelectionHandler() {
    return cacheTree;
  }
}
