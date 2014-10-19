/**
 * This file is part of GoobiOaiHelper.
 * 
 * GoobiOaiHelper is free software: you can redistribute it and/or modify
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * GoobiOaiHelper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with GoobiOaiHelper.  If not, see <http://www.gnu.org/licenses/>.
 */

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

/**
 * This class provides methods to receive some information from an OAI-PMH interface. Provide a URL to such an interface with an ending slash. Example: "http://example.com/viewer/oai/"
 * 
 * @author Michael Birkner
 */
public class GoobiOaiHelper {

	private String oaiPmh;
	private XmlParser xmlParser = new XmlParser();

	public GoobiOaiHelper(String oaiPmh) {
		this.oaiPmh = oaiPmh;
	}

	/**
	 * Gets information of structure elements (e. g. "Article", "Chapter", etc.) from an OAI-PMH interface. The document must be a METS-XML. An identifier of an individual record
	 * that is available over the OAI interface must be submitted. Example: http://example.com/oai/?verb=GetRecord&metadataPrefix=oai_dc&identifier=USE_THIS_ID
	 * Returns a list with "StructureElement" objects. You could iterate over the list to get title, subtitle, authors, etc. of the structure element.
	 * If an information is not found, you will get "null".
	 * 
	 * @param id					a String of the identifier of an individual record that is available over the OAI interface
	 * @param structureElements		a List<String> of stucture elements to parse, e. g. "Article", "Chapter", etc. Use "null" to parse all structure elements
	 * @return						a List<StructureElement>
	 * @throws Exception
	 */
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


	/**
	 * Get all relevent identifiers of the METS-XML document for further usage. Returns a List<Id>.
	 * 
	 * @param document				a Document object (METS-XML)
	 * @param structureElements		a List<String> of stucture elements to parse, e. g. "Article", "Chapter", etc. Use "null" to parse all structure elements
	 * @return						a List<Id>
	 * @throws Exception
	 */
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

	/**
	 * Gets the identifiers of the physical structure map (see element <mets:structMap TYPE="PHYSICAL">) of the METS-XML.
	 * 
	 * @param document		a Document object (METS-XML)
	 * @param logId			a List<String> of the itentifiers of the logical structure map (see element <mets:structMap TYPE="LOGICAL">) of the METS-XML
	 * @return				a List<String> with the identifiers of the physical structure map
	 * @throws Exception
	 */
	protected List<String> getPhysIds(Document document, String logId) throws Exception {	
		List<String> physIds = new ArrayList<String>();
		physIds = xmlParser.getAttributeValues(document, "OAI-PMH/GetRecord/record/metadata/mets/structLink//smLink[@from='" + logId + "']", "xlink:to");
		return physIds;
	}

	
	/**
	 * Gets a String with the page label for a structure element. E. g. if the first page of a structure element named "Article" starts at page no. 23 and ends at page no. 42, the
	 * returned String would be "23-42". If the "Article" would be only on page no. 23, the returned String would be "23".
	 * 
	 * @param document						a Document object (METS-XML)
	 * @param physIds						a List<String> with the identifiers of the physical structure map (see element <mets:structMap TYPE="PHYSICAL">) of the METS-XML.
	 * @return								a String with the page labels
	 * @throws XPathExpressionException
	 */
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


	/**
	 * Gets the URL to the OAI-PMH interface as a String.
	 * 
	 * @return a String with the OAI-PMH URL
	 */
	public String getOaiPmh() {
		return oaiPmh;
	}

}
