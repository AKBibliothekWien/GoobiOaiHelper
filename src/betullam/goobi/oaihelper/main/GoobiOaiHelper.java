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

package betullam.goobi.oaihelper.main;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import betullam.goobi.oaihelper.classes.Id;
import betullam.goobi.oaihelper.parsers.XmlParser;


/**
 * This class provides methods to receive some information from an OAI-PMH interface. Provide a URL to such an interface with an ending slash. Example: "http://example.com/viewer/oai/"
 * 
 * @author Michael Birkner
 */
public class GoobiOaiHelper extends XmlParser {

	private String oaiPmh;
	private XmlParser xmlParser = new XmlParser();

	public GoobiOaiHelper(String oaiPmh) {
		this.oaiPmh = oaiPmh;
	}

	/**
	 * Get all relevent identifiers (LogId, DmdlogId, PhysIds) of the METS-XML document for further usage. Returns a List<Id>.
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
	public List<String> getPhysIds(Document document, String logId) throws Exception {	
		List<String> physIds = new ArrayList<String>();
		physIds = xmlParser.getAttributeValues(document, "OAI-PMH/GetRecord/record/metadata/mets/structLink//smLink[@from='" + logId + "']", "xlink:to");
		return physIds;
	}


	/**
	 * Gets a String with the page label for a structure element. E. g. if the first page of a structure element named "Article" starts at page no. 23 and ends at page no. 42, the
	 * returned String would be "23-42". If the "Article" would be only on page no. 23, the returned String would be "23".
	 * 
	 * @param document						a Document object (METS-XML)
	 * @param physIds						a List<String> with the identifiers of the physical structure map (see element <mets:structMap TYPE="PHYSICAL">) of the METS-XML
	 * @return								a String with the page labels
	 * @throws XPathExpressionException
	 */
	public String getPageLabelByPhysId(Document document, List<String> physIds) throws XPathExpressionException {
		String pageLabel = null;

		// Prevent NullPointerException. If there are no physIds, just return null
		if (physIds != null) {
			String firstPhysId = physIds.get(0);
			String lastPhysId = physIds.get(physIds.size()-1);
			String firstPage = xmlParser.getAttributeValue(document, "OAI-PMH/GetRecord/record/metadata/mets/structMap[@TYPE=\"PHYSICAL\"]//div[@ID='" + firstPhysId + "']", "ORDERLABEL");
			String lastPage = xmlParser.getAttributeValue(document, "OAI-PMH/GetRecord/record/metadata/mets/structMap[@TYPE=\"PHYSICAL\"]//div[@ID='" + lastPhysId + "']", "ORDERLABEL");

			if (firstPage.equals(lastPage)) {
				pageLabel = firstPage.trim();
			} else {
				pageLabel = firstPage.trim() + "-" + lastPage.trim();
			}
		}
		return pageLabel;
	}


	/**
	 * Gets a List<String> with separated fist page label and last page label for a structure element. E. g. if the first page of a structure element named "Article" starts at page no. 23 and ends at page no. 42, the
	 * returned List<String> would be List<23,42>. If the "Article" would be only on page no. 23, the returned List<String> would be List<23,23>.
	 * 
	 * @param document						a Document object (METS-XML)
	 * @param physIds						a List<String> with the identifiers of the physical structure map (see element <mets:structMap TYPE="PHYSICAL">) of the METS-XML
	 * @return								a List<String>, where the first element ist the first page, the second element is the second page
	 * @throws XPathExpressionException
	 */
	public List<String> getFirstLastLabelByPhysId(Document document, List<String> physIds) throws XPathExpressionException {
		List<String> lstPageLabels = new ArrayList<String>();
		String firstPage = null;
		String lastPage = null;

		if (physIds != null) {
			String firstPhysId = physIds.get(0);
			String lastPhysId = physIds.get(physIds.size()-1);
			firstPage = xmlParser.getAttributeValue(document, "OAI-PMH/GetRecord/record/metadata/mets/structMap[@TYPE=\"PHYSICAL\"]//div[@ID='" + firstPhysId + "']", "ORDERLABEL");
			lastPage = xmlParser.getAttributeValue(document, "OAI-PMH/GetRecord/record/metadata/mets/structMap[@TYPE=\"PHYSICAL\"]//div[@ID='" + lastPhysId + "']", "ORDERLABEL");
			lstPageLabels.add(firstPage);
			lstPageLabels.add(lastPage);	
		}
		return lstPageLabels;
	}





	/**
	 * Gets a List<String> which contains the 8-digit image numbers that orders a structure element. You could use these numbers to get the image files for a structure element.
	 * @param document						a Document object (METS-XML)
	 * @param physIds						a List<String> with the identifiers of the physical structure map (see element <mets:structMap TYPE="PHYSICAL">) of the METS-XML
	 * @return								a List<String> containing the 8-digit image numbers that orders a structure element
	 * @throws XPathExpressionException
	 */
	public List<String> getOrderNoByPhysId(Document document, List<String> physIds) throws XPathExpressionException {
		List<String> images = new ArrayList<String>();

		for (String physId : physIds) {
			String imageNo = xmlParser.getAttributeValue(document, "OAI-PMH/GetRecord/record/metadata/mets/structMap[@TYPE=\"PHYSICAL\"]//div[@ID='" + physId + "']", "ORDER");

			int intImageNo = Integer.parseInt(imageNo); // Convert to int to be able to add leading zeros and make it 8 digits long
			imageNo = String.format("%08d", intImageNo); // Image-number-string with leading zeros by using String.format

			images.add(imageNo);
		}

		return images;
	}

	/**
	 * Gets a List<String> of all URNs of the given PhysIDs.
	 * 
	 * @param document						a Document object (METS-XML)
	 * @param physIds						a List<String> with the identifiers of the physical structure map (see element <mets:structMap TYPE="PHYSICAL">) of the METS-XML
	 * @return								a List<String> containing the URNs for the given PhysIDs
	 * @throws XPathExpressionException
	 */
	public List<String> getUrnsByPhysIds(Document document, List<String> physIds) throws XPathExpressionException {
		List<String> urns = new ArrayList<String>();
		for (String physId : physIds) {
			String urn = xmlParser.getAttributeValue(document, "OAI-PMH/GetRecord/record/metadata/mets/structMap[@TYPE=\"PHYSICAL\"]//div[@ID='" + physId + "']", "CONTENTIDS");
			urns.add(urn);
		}

		return urns;
	}

	/**
	 * Gets a List<String> of author names for a given DmdLogId (= ID of a structure element). The names will be in the format "FirstName LastName".
	 * 
	 * @param document	a Document object (METS-XML)
	 * @param dmdlogId	a String containing a DmdLogId
	 * @return			a List<String> containing author names or null if no names were found
	 */
	public List<String> getAuthorsByDmdlogId(Document document, String dmdlogId) {
		List<String> authorNames = new ArrayList<String>();

		String xPathName				= "/OAI-PMH/GetRecord/record/metadata/mets/dmdSec[@ID='" + dmdlogId + "']/mdWrap/xmlData/mods/name[@type='personal']";
		String xpathAuthorGivenName		= "/OAI-PMH/GetRecord/record/metadata/mets/dmdSec[@ID='" + dmdlogId + "']/mdWrap/xmlData/mods/name/namePart[@type='given']";
		String xpathAuthorFamilyName	= "/OAI-PMH/GetRecord/record/metadata/mets/dmdSec[@ID='" + dmdlogId + "']/mdWrap/xmlData/mods/name/namePart[@type='family']";

		try {
			XPath xPath =  XPathFactory.newInstance().newXPath();
			NodeList nameNodes = (NodeList)xPath.compile(xPathName).evaluate(document, XPathConstants.NODESET);

			for (int i = 0; i < nameNodes.getLength(); i++) {
				NodeList givenNameNode = (NodeList)xPath.compile(xpathAuthorGivenName).evaluate(document, XPathConstants.NODESET);
				NodeList familyNameNode = (NodeList)xPath.compile(xpathAuthorFamilyName).evaluate(document, XPathConstants.NODESET);
				String givenName = givenNameNode.item(i).getFirstChild().getNodeValue();
				String familyName = familyNameNode.item(i).getFirstChild().getNodeValue();
				authorNames.add(givenName + " " + familyName);
			}
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		authorNames = (authorNames.isEmpty() == false) ? authorNames : null;
		return authorNames;
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
