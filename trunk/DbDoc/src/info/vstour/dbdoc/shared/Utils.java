package info.vstour.dbdoc.shared;

import java.util.Map;

public class Utils {

  public static boolean isEmpty(String value) {
    if (value == null || value.trim().isEmpty())
      return true;
    else
      return false;
  }

  public static String getMapValue(String key, Map<String, String> map) {
    if (map.containsKey(key)) {
      return map.get(key);
    } else {
      return "";
    }
  }

}
