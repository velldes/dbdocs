import info.vstour.dbdoc.DbDoc;

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
			catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
	private static void printHelp() {
		System.out.println("Dependencies:");
		System.out.println("  Create properties file 'connection_name.properties'");
		System.out.println("  and copy to res/props/ folder");
		System.out.println("Usage:");
		System.out.println("  java -jar dbdoccmd.jar <connection_name>");
		System.out.println("");
		System.out.println("  -h or /h          - shows this help.");
		System.out.println("  <connection_name> - properties file name without extension.");
	}
}
