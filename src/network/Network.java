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
