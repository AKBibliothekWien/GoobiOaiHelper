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

package betullam.goobi.oaihelper.network;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;


/**
 * This class provides methods to get the METS-XML document from an OAI-PMH interface.
 * 
 * @author Michael Birkner
 */
public class Network {

	/**
	 * Gets a METS-XML Document from an OAI-PMH interface. It uses the "GetRecord" command of OAI (further information: http://www.openarchives.org/OAI/openarchivesprotocol.html#GetRecord).
	 *  
	 * @param oaiPmh							a String of the URL to the OAI-PMH interface, without the part which begins with "?verb=GetRecord...", but with an ending slash (e. g. http://example.com/viewer/oai/)
	 * @param id								a String of the identifier of an individual record which is available over the OAI-PMH interface
	 * @return									a Document of the record as METS-XML
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws TransformerException
	 */
	public Document getMetsXmlRecord(String oaiPmh, String id) throws IOException, ParserConfigurationException, SAXException, TransformerException {
		URL uUrl = new URL(oaiPmh+"?verb=GetRecord&metadataPrefix=mets&identifier="+id);
		URLConnection conn = uUrl.openConnection();

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(conn.getInputStream());
		
		/*
		// Output XML to console:
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer xform = tf.newTransformer();
		xform.transform(new DOMSource(document), new StreamResult(System.out));
		*/
		
		return document;
	}
}
