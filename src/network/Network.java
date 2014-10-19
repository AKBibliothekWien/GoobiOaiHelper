package network;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

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
