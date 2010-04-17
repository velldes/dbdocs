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
