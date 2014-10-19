package main;

import java.util.Arrays;


public class Main {

	public static void main(String[] args) throws Exception {

		//new GoobiOaiHelper("http://emedien.arbeiterkammer.at/viewer/oai/").getStructureElements("urn:nbn:at:at-akw:g-149164", Arrays.asList("Editorial", "Article", "Review"));
		new GoobiOaiHelper("http://emedien.arbeiterkammer.at/viewer/oai/").getStructureElements("urn:nbn:at:at-akw:g-149164", Arrays.asList("Editorial", "Article", "Review"));
		//new GoobiOaiHelper("http://emedien.arbeiterkammer.at/viewer/oai/").getStructureElements("AC05712646_2014_003", null);
	


	}

}
