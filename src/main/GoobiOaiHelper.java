package main;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import network.Network;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import parsers.XmlParser;
import classes.Id;
import classes.StructureElement;

public class GoobiOaiHelper {


	private String oaiPmh;
	private XmlParser xmlParser = new XmlParser();

	public GoobiOaiHelper(String oaiPmh) {
		this.oaiPmh = oaiPmh;
	}

	
	public List<StructureElement> getStructureElements(String id, List<String> structureElements) throws Exception {
		Document document = new Network().getMetsXmlRecord(oaiPmh, id);
		List<StructureElement> lstStructureElements = new ArrayList<StructureElement>();
		List<Id> ids = getIds(document, structureElements);

		for(Id metsIds : ids) {
			String logId = metsIds.getLogId();
			String dmdlogId = metsIds.getDmdlogId();

			String structureElementType = xmlParser.getAttributeValue(document, "OAI-PMH/GetRecord/record/metadata/mets/structMap[@TYPE=\"LOGICAL\"]//div[@ID='" + logId + "']", "TYPE");
			String title = xmlParser.getTextValue(document, "OAI-PMH/GetRecord/record/metadata/mets/dmdSec[@ID='" + dmdlogId + "']/mdWrap/xmlData/mods/titleInfo/title");
			String subTitle = xmlParser.getTextValue(document, "OAI-PMH/GetRecord/record/metadata/mets/dmdSec[@ID='" + dmdlogId + "']/mdWrap/xmlData/mods/titleInfo/subTitle");
			List<String> authors = xmlParser.getTextValues(document, "OAI-PMH/GetRecord/record/metadata/mets/dmdSec[@ID='" + dmdlogId + "']/mdWrap/xmlData/mods/name/displayForm");
			String artAbstract = xmlParser.getTextValue(document, "OAI-PMH/GetRecord/record/metadata/mets/dmdSec[@ID='" + dmdlogId + "']/mdWrap/xmlData/mods/abstract");
			String language = xmlParser.getTextValue(document, "OAI-PMH/GetRecord/record/metadata/mets/dmdSec[@ID='" + dmdlogId + "']/mdWrap/xmlData/mods/language/languageTerm");
			String pageLabel = getPageLabelByPhysId(document, metsIds.getPhysIds());

			System.out.println("Ids: " + metsIds.toString());
			System.out.println("Type: " + structureElementType);
			System.out.println("Authors: " + authors);
			System.out.println("Title: " + title);
			System.out.println("Subtitle: " + subTitle);
			System.out.println("Abstract: " + artAbstract);
			System.out.println("Language: " + language);
			System.out.println("Pages: " + pageLabel);
			System.out.print("\n");
			
			lstStructureElements.add(new StructureElement(metsIds, structureElementType, title, subTitle, authors, artAbstract, language, pageLabel));
		}

		return lstStructureElements;
	}


	public List<Id> getIds(Document document, List<String> structureElements) throws Exception {

		List<Id> ids = new ArrayList<Id>();
		List<String> phsyIds = new ArrayList<String>();
		XPath xPath = XPathFactory.newInstance().newXPath();
		XPathExpression xPathExpression = xPath.compile("OAI-PMH/GetRecord/record/metadata/mets/structMap[@TYPE=\"LOGICAL\"]//div");
		NodeList nodeList = (NodeList)xPathExpression.evaluate(document, XPathConstants.NODESET);

		// Check if node list is empty to prevent NullPointerException. If it is empty, just return null
		if (nodeList.getLength() > 0) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Element element = (Element)nodeList.item(i);
				String dmdlogId = (element.getAttribute("DMDID") != "") ? element.getAttribute("DMDID") : null;
				String logId = (element.getAttribute("ID") != "") ? element.getAttribute("ID") : null;
				String structureElement = (element.getAttribute("TYPE") != "") ? element.getAttribute("TYPE") : null;

				// Add only elements for the given structure elements (e. g. article, editorial, etc.) if the user specified them
				if (structureElements != null) {
					if (structureElements.contains(structureElement)) {
						phsyIds = getPhysIds(document, logId);
						ids.add(new Id(dmdlogId, logId, phsyIds));
					}
				} else { // If no structure elements (e. g. article, editorial, etc.) are specified (structureElements = null), then add all of them
					phsyIds = getPhysIds(document, logId);
					ids.add(new Id(dmdlogId, logId, phsyIds));
				}
			}
		} else {
			ids = null;
		}

		return ids;
	}


	protected List<String> getPhysIds(Document document, String logId) throws Exception {	
		List<String> physIds = new ArrayList<String>();
		physIds = xmlParser.getAttributeValues(document, "OAI-PMH/GetRecord/record/metadata/mets/structLink//smLink[@from='" + logId + "']", "xlink:to");
		return physIds;
	}

	
	protected String getPageLabelByPhysId(Document document, List<String> physIds) throws XPathExpressionException {
		String pageLabel = null;

		// Prevent NullPointerException. If there are no physIds, just return null
		if (physIds != null) {
			String firstPhysId = physIds.get(0);
			String lastPhysId = physIds.get(physIds.size()-1);
			String firstPage = xmlParser.getAttributeValue(document, "OAI-PMH/GetRecord/record/metadata/mets/structMap[@TYPE=\"PHYSICAL\"]//div[@ID='" + firstPhysId + "']", "ORDERLABEL");
			String lastPage = xmlParser.getAttributeValue(document, "OAI-PMH/GetRecord/record/metadata/mets/structMap[@TYPE=\"PHYSICAL\"]//div[@ID='" + lastPhysId + "']", "ORDERLABEL");

			if (firstPage.equals(lastPage)) {
				pageLabel = firstPage;
			} else {
				pageLabel = firstPage + "-" + lastPage;
			}
		}
		return pageLabel;
	}


	public String getOaiPmh() {
		return oaiPmh;
	}

}
