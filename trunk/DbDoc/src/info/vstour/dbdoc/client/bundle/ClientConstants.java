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
package info.vstour.dbdoc.client.bundle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Constants;

public interface ClientConstants extends Constants {

  public static final ClientConstants INSTANCE = GWT.create(ClientConstants.class);

  @DefaultStringValue("Connection:")
  String connection();

  @DefaultStringValue("Owner:")
  String owner();

  @DefaultStringValue("Filter:")
  String filter();

  @DefaultStringValue("Cached objects")
  String CachedObjects();

  @DefaultStringValue("DB objects")
  String DbObjects();

  @DefaultStringValue("Begin the documentation with a slash and two asterisks."
      + " Proceed with the text of the documentation. This text can span multiple lines. End"
      + " the documentation with an asterisk and a slash. The opening and terminating"
      + " characters need not be separated from the text by a space or a line break.")
  String help();
}
