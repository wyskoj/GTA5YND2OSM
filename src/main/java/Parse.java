/*
 * MIT License
 *
 * Copyright (c) %today.year Jacob Wysko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Parse {
	
	static HashMap<String, Integer> GTA52OSM = new HashMap<>();
	
	public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, TransformerException {
		int osmInt = 1;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		byte[] bytes = Files.readAllBytes(Paths.get("paths.xml"));
		ByteArrayInputStream input = new ByteArrayInputStream(
				bytes);
		Document doc = builder.parse(input);
		
		NodeList list = ((Element) doc.getElementsByTagName("objects").item(0)).getElementsByTagName("object");
		List<GTA5Node> nodes = new ArrayList<>();
		for (int i = 0; i < list.getLength(); i += 2) {
			String name = null;
			String guid = null;
			try {
				Node node = list.item(i);
				guid = node.getAttributes().getNamedItem("guid").getTextContent();
				name = node.getAttributes().getNamedItem("name").getTextContent();
				String type = node.getAttributes().getNamedItem("class").getTextContent();
				Node pos = node.getChildNodes().item(1).getChildNodes().item(1).getChildNodes().item(1);
				var posAtrs = pos.getAttributes();
				String x = posAtrs.getNamedItem("x").getTextContent();
				String y = posAtrs.getNamedItem("y").getTextContent();
				String z = posAtrs.getNamedItem("z").getTextContent();
				var attributes = node.getChildNodes().item(3).getChildNodes();
				List<Attribute> attributeList = new ArrayList<>();
				for (int j = 1; j < attributes.getLength(); j += 2) { // wtf bro
					var attribute = attributes.item(j);
					attributeList.add(new Attribute(
							attribute.getAttributes().getNamedItem("name").getTextContent(),
							attribute.getAttributes().getNamedItem("type").getTextContent(),
							attribute.getAttributes().getNamedItem("value").getTextContent()
					));
				}
				if (type.equals("vehiclelink")) {
					List<String> refs = new ArrayList<>();
					var refNodes = node.getChildNodes().item(7).getChildNodes();
					for (int j = 1; j < refNodes.getLength(); j += 2) {
						refs.add(refNodes.item(j).getAttributes().getNamedItem("guid").getTextContent());
					}
					var e = new MapLink(guid, name, new Position(x, y, z), attributeList, refs);
					e.osmID = osmInt++;
					nodes.add(e);
					GTA52OSM.put(guid, e.osmID);
				} else if (type.equals("vehiclenode")) {
					var e = new MapNode(guid, name, new Position(x, y, z), attributeList);
					e.osmID = osmInt++;
					GTA52OSM.put(guid, e.osmID);
					nodes.add(e);
				} else {
					System.out.println("wtf " + type);
				}
			} catch (Exception e) {
				System.err.println("Could not read " + name + " | " + guid);
			}
		}
		
		DocumentBuilderFactory dbFactory =
				DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document osmDoc = dBuilder.newDocument();
		Element rootElement = osmDoc.createElement("osm");
		rootElement.setAttribute("version", "0.6");
		osmDoc.appendChild(rootElement);
		int size = nodes.size();
		for (int i = 0; i < size; i++) {
			GTA5Node node = nodes.get(i);
			if (node instanceof MapNode) {
				var gta5node = (MapNode) node;
				var osmNode = osmDoc.createElement("node");
				double scalar = 112276.48754026106119681301915579; // this is so arbitrary but makes it roughly to scale
				osmNode.setAttribute("lat", String.valueOf(gta5node.position.y / (scalar)));
				osmNode.setAttribute("lon", String.valueOf(gta5node.position.x / (scalar)));
				osmNode.setAttribute("visible", "true");
				osmNode.setAttribute("version", "1");
				osmNode.setAttribute("id", String.valueOf(gta5node.osmID));
				osmNode.setAttribute("GTA5_GUID", gta5node.guid);
				
				rootElement.appendChild(osmNode);
			} else {
				var gta5link = (MapLink) node;
				var osmWay = osmDoc.createElement("way");
				osmWay.setAttribute("GTA5_GUID", gta5link.guid);
				osmWay.setAttribute("id", String.valueOf(i + 1));
				osmWay.setAttribute("visible", "true");
				osmWay.setAttribute("version", "1");
				for (String rel : gta5link.referencesGuids) {
					var osmND = osmDoc.createElement("nd");
					osmND.setAttribute("ref", String.valueOf(GTA52OSM.get(rel)));
					osmWay.appendChild(osmND);
				}
				for (Attribute attribute : gta5link.attributes) {
					var osmTag = osmDoc.createElement("tag");
					osmTag.setAttribute("k", attribute.key);
					osmTag.setAttribute("v", attribute.value);
					osmWay.appendChild(osmTag);
				}
				rootElement.appendChild(osmWay);
			}
			if (i % 1000 == 0) System.out.println(i + " / " + size);
		}
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(osmDoc);
		StreamResult result = new StreamResult(new File("%desktop%\\test.xml"));
		transformer.transform(source, result);
	}
}
