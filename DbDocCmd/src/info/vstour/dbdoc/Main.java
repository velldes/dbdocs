package info.vstour.dbdoc;
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

public class Main {

  public static void main(String[] args) {

    if ((args.length == 0) || (args[0] == "-h") || (args[0] == "/h")) {
      printHelp();
      System.exit(0);
    } else {
      try {
        DbDoc dbDoc = new DbDoc(args[0]);
        dbDoc.createDoc();
        System.out.println("Done");
        System.exit(0);
      }
      catch (ExitException e) {
        System.exit(e.getStatus());
      }
    }
  }
  private static void printHelp() {
    System.out.println("Dependencies:");
    System.out.println("  Create properties file '<connection_name>.properties'");
    System.out.println("  and copy to res/props/ folder");
    System.out.println("Usage:");
    System.out.println("  java -jar dbdoccmd.jar <connection_name>");
    System.out.println("");
    System.out.println("  -h or /h          - shows this help.");
    System.out.println("  <connection_name> - properties file name without extension.");
    System.out.println("--");
    System.out.println("Project web is http://code.google.com/p/dbdocs/");
  }
}
