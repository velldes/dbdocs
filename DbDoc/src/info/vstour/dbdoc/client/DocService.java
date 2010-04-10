package info.vstour.dbdoc.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("doc")
public interface DocService extends RemoteService {
	List<String> getTreeItems(String connName, String itemName, String search);
	String[] getObjects(String connName);
	String[] getPropsList();
	String getDoc(String connName, String parent, String child);
}
