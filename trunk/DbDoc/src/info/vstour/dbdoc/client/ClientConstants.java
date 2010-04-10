package info.vstour.dbdoc.client;

import com.google.gwt.i18n.client.Constants;

public interface ClientConstants extends Constants {
	@DefaultStringValue("dbDocSettings")
	String settingsCookies();

	@DefaultStringValue("Connection:")
	String connection();

	@DefaultStringValue("Search:")
	String search();

	@DefaultStringValue("Begin the documentation with a slash and an asterisk."
	        + " Proceed with the text of the documentation. This text can span multiple lines. End"
	        + " the documentation with an asterisk and a slash. The opening and terminating"
	        + " characters need not be separated from the text by a space or a line break.")
	String help();
}
