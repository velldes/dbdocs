package info.vstour.dbdoc.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>DocService</code>.
 */
public interface DocServiceAsync {
	void getTreeItems(String connName, String itemName, String search, AsyncCallback<List<String>> callback);
	void getObjects(String connName, AsyncCallback<String[]> callback);
	void getPropsList(AsyncCallback<String[]> callback);
	void getDoc(String connName, String parent, String child, AsyncCallback<String> callback);
}
