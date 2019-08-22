package tools;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

// From http://stackoverflow.com/questions/19396834/dom-xml-parser-example
public class XMLParser{
	
	private static String path;
	private static int incipitSize; 
	private static List<String> counted;

	private static URL seq;
	private static URL meo;
	private static URL chord;
	private static URL dct;
	private static URL rdf;
	
	private static Alteration[] keySigInfo;
	private static Alteration[] accidInfo;
	private static String notes = "abcdefg";
	private enum Alteration {NONE, FLAT, SHARP};
	private static boolean accidentalValid;
	
	
	private class MusicalInstance{
		
		private String name;
		private List<String[]> attributes;

		
		private MusicalInstance(){
		}
		
		private MusicalInstance(String s, List<String[]> l){
			this.name = s;
			this.attributes = l;
		}
		
		private void setName(String s) {
			this.name = s;
		}
		
		private void setAttributes(List<String[]> l){
			this.attributes = l;
		}
		
		private String getName() {
			return this.name;
		}
		
		private List<String[]> getAttributes() {
			return this.attributes;
		}
		
		private String getAttributeValue(String aName) {
			String value = null; 
			for (String[] a : getAttributes()) {
				if (a[0].equals(aName)) {
					value = a[1];
				}
			}
			return value;
		}
		
	}
	
	
	public static void main (String[] args) throws ParserConfigurationException{
		XMLParser t = new XMLParser();
				
		counted = Arrays.asList(new String[]{"note", "dot"}); //, "rest"}); //, "dot"});
		incipitSize = 20;
		
		path = "C:/Users/Reinier/Desktop/LD-City/Three_chansons/"; 
//		File f = new File(path + "Damour_me_plains/mei/K3a1_009_b.mei"); 	
//		File f = new File(path + "Damour_me_plains/mei/K3a1_025_b.mei"); 	
//		File f = new File(path + "Damour_me_plains/mei/K3a1_041_b.mei"); 	
//		File f = new File(path + "Damour_me_plains/mei/K3a1_057_b.mei"); 	
		File f = new File(path + "Je_prens_en_gre/mei_corr_renamed/1_K3a1_011b.mei");
//		File f = new File(path + "Je_prens_en_gre/mei_corr_renamed/2_K3a1_027b.mei");
//		File f = new File(path + "Je_prens_en_gre/mei_corr_renamed/3_K3a1_043b.mei");
//		File f = new File(path + "Je_prens_en_gre/mei_corr_renamed/4_K3a1_059b.mei");
		
		
		try{
			seq = new URL("http://mi.soi.city.ac.uk/ontologies/SEQ#");
		} catch (MalformedURLException e1) {
				e1.printStackTrace();
		}
		try{
			meo = new URL("http://mi.soi.city.ac.uk/ontologies/MEO#");
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		try{
			chord = new URL("http://purl.org/ontology/chord/");
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		try{
			dct = new URL("http://purl.org/dc/terms/");
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		try{
			rdf = new URL("http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
			
		List<List<MusicalInstance>> instances = t.readXml(f);
		
		List<MusicalInstance> firstSystem = instances.get(0);
		for (MusicalInstance in : firstSystem) {
			System.out.println(in.getName());
			List<String[]> attributes = in.getAttributes();
			for (String[] a : attributes) {
				System.out.println("  " + a[0] + " = " + a[1]); 
			}
		}
//		System.exit(0);
		
		System.out.println("\n\n" + generate(f, firstSystem));
		
		
	}


	private static String generate(File f, List<MusicalInstance> instances) {
		String output = "";
		String prefix = "@prefix seq: <" + seq + ">." + "\n" +
						"@prefix meo: <" + meo + ">." + "\n" +
						"@prefix rdf: <" + rdf + ">." + "\n" +
//						"PREFIX mo: <" + mo + ">" + "\n" + 
						"@prefix note: <notes#>." + "\n" +
						"@prefix temp: <temps#>." + "\n";
		output = output.concat(prefix);
//		output = output.concat("{" + "\n");
		output = output.concat("\n");
		
		String hasNext = "seq:hasNext";
		String hasCont = "seq:hasContent";
		String pit = "meo:pitch";
		String oct = "meo:octave";
		String dur = "meo:duration";
		String xml = "meo:xmlID";
//		String nat = "chord:natural";
//		String not = "chord:note";
		String mod = "chord:modifier";
		String type = "rdf:type";
		String seqComp = "seq:SeqComp";
//		String alp = "seq:alpha";
//		String ome = "seq:omega";
		String inc = "meo:incipitFor";
		String mei = "meo:mei";
//		String mus = "mo:MusicalWork";
		String alt = "meo:alteration";
		
		String temp = "temp:";
		String workURI = "http://slickmem.data.t-mus.org/resource/items/0/1/20040806000253";
		String fileName = f.getName();
		
		// TODO fix alteration
		
		// alpha
//		String itemID = "<SOME_ITEM_ID_LITERAL>"; // get this somehow from SLICKMEM
		output = output.concat(
			temp + "0" + "\t" + "\t" + type + "\t" + "seq:Alpha" + ";" + "\n");
		output = output.concat(
			"\t" + "\t" + hasNext + "\t" + temp + 0 + ";" + "\n");
		output = output.concat(
			"\t" + "\t" + inc + "\t" + "\"" + workURI + "\"" + ";" + "\n");
		output = output.concat(
			"\t" + "\t" + mei + "\t" + "\t" + "\"" + fileName + "\"" + "." + "\n");
		

		// Instances: clef, mensur, accid, note, rest, dot, custos
		// Only notes and rests for now
		int counter = 1;
		for (int i = 0; i < instances.size(); i++) {
				
			MusicalInstance in = instances.get(i);
			
			if (in.getName().equals("note")) {
				
//				String currPrefix = prefix;
//				currPrefix = currPrefix.concat("PREFIX chord: <" + chord + ">" + "\n");
//				currPrefix = currPrefix.concat("PREFIX note: <notes#>" + "\n");
//				currPrefix = currPrefix.concat("PREFIX temp: <temps#>" + "\n");
//				String temp = "temp:" + counter;
				temp = "temp:" + counter;
				String tempNext = "temp:" + (counter + 2);
				String note = "note:" + (counter + 1);
				counter++;
				
//				String currOutput = currPrefix;
				String currOutput = "";
				// temp
//				currOutput = currOutput.concat("INSERT DATA" + "\n" + "{" + "\n");
				currOutput = currOutput.concat(
					temp + "\t" + "\t" + type + "\t" + seqComp + ";" + "\n");
				currOutput = currOutput.concat(
					"\t" + "\t" + hasCont + "\t" + note + ";" + "\n");
				// If Instance is not the last one
				if (i != instances.size() - 1) {
					currOutput = currOutput.concat(
							"\t" + "\t" + hasNext + "\t" + tempNext + "." + "\n");
				}
				// If Instance is the last one
				else {
					currOutput = currOutput.concat(
						"\t" + "\t" + hasNext + "\t" + "seq:omega" + "." + "\n");
				}
				
				// note
				currOutput = currOutput.concat(
						note + "\t" + "\t" + type + "\t" + "meo:Note" + ";" + "\n");	
				currOutput = currOutput.concat(
					"\t" + "\t" + pit + "\t" + "meo:" + in.getAttributeValue("pname") + ";" + "\n");
				currOutput = currOutput.concat(
					"\t" + "\t" + oct + "\t" + "meo:" + in.getAttributeValue("oct") + ";" + "\n");
				if (i != 0 && instances.get(i-1).getName().equals("accid")) {
					String acc = "sharp";
					if (instances.get(i-1).getAttributeValue("accid").equals("f")) {
						acc = "flat";
					}
					currOutput = currOutput.concat(
						"\t" + "\t" + alt + "\t" + "meo:" + acc + ";" + "\n");  
				}
				currOutput = currOutput.concat(
					"\t" + "\t" + dur + "\t" + "meo:" + in.getAttributeValue("dur") + 
					 ";" + "\n");
				currOutput = currOutput.concat(
					"\t" + "\t" + xml + "\t" + "\"" + in.getAttributeValue("xml:id") + "\"" + "." + "\n");
				
				// TODOs: octave, natural, alteration
				// meo:octave
//				currOutput = currOutput.concat(
//					"\t" + "\t" + "\t" + mo + "\t" + "meo:octave/" + in.getAttributeValue("oct") + " ;" + "\n"); 	
//				// chord:natural
//				currOutput = currOutput.concat(
//				"\t"  + "\t" + "\t" + cna + "\t" + cno + "/" + 
//					in.getAttributeValue("pname").toUpperCase() + " ;" + "\n"); 	  
				// chord:modifier
				if (i != 0 && instances.get(i-1).getName().equals("accid")) {
					String acc = "sharp";
					if (instances.get(i-1).getAttributeValue("accid").equals("f")) {
						acc = "flat";
					}
					currOutput = currOutput.concat(
						"\t" + "\t" + mod + "\t" + "chord:" + acc + ";" + "\n");  
				}
				// meo:duration 
				// TODO: followed by dot: duration * 1.5
//				currOutput = currOutput.concat(
//						"\t" + "\t" + "\t" + md + "\t" + "a meo:Duration" + " ." + "\n"); 	
				
//				currOutput = currOutput.concat("}");
				output = output.concat(currOutput);
				counter++;
				
//				System.out.println(currOutput);
//				System.out.println();
			}
		}
//		output = output.concat("}");		
		return output;
	}


	private void initKeySigInfo() {
		 keySigInfo = new Alteration[7];
		 Arrays.fill(keySigInfo, Alteration.NONE);
	}


	private void initAccidInfo() {
		accidInfo = new Alteration[7];
		Arrays.fill(accidInfo, Alteration.NONE);
	}


	private void setAlterationsInfo(String note, String altString, boolean isAccidental) {
		Alteration alt = null;
		switch(altString) {
			case "f":
				alt = Alteration.FLAT;
				break;
			case "ff":
				alt = Alteration.FLAT;
				break;	
			case "s":
				alt = Alteration.SHARP;
				break;
			case "n":
				alt = Alteration.NONE;
		}
		if (isAccidental) {
			accidInfo[notes.indexOf(note)] = alt;
		}
		else {
			keySigInfo[notes.indexOf(note)] = alt;
		}
	}


	private String checkForAlteration(String note) {
		int index = notes.indexOf(note);
		if (keySigInfo[index] == Alteration.SHARP || accidInfo[index] == Alteration.SHARP) {
			note = note + "_sharp";
		}
		else if (keySigInfo[index] == Alteration.FLAT || accidInfo[index] == Alteration.FLAT) {
			note = note + "_flat";
		}
		return note;
	}


	private List<List<MusicalInstance>> readXml (File f) throws ParserConfigurationException{
		List<List<MusicalInstance>> instances = new ArrayList<List<MusicalInstance>>();	
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = null;
		try {
			doc = dBuilder.parse(f);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		doc.getDocumentElement().normalize();
		
		String root = doc.getDocumentElement().getNodeName();
//		System.out.println("root element: " + root);
//		System.out.println("----------------------------");
		
					
		// 0. Get all systems
		NodeList systemNodes = doc.getElementsByTagName("system");
//		for (int i = 0; i < systemNodes.getLength(); i++) {	
		for (int i = 0; i < 1; i++) {	
			initKeySigInfo();
			initAccidInfo();
			Node systemNode = systemNodes.item(i);			
			// If the current node is an Element node
			if (systemNode.getNodeType() == Node.ELEMENT_NODE) {
//				System.out.println("current element: " + systemNode.getNodeName());
				// 1. Get all staffs in the system
				NodeList staffNodes = systemNode.getChildNodes();
				for (int j = 0; j < staffNodes.getLength(); j++) {
					Node staffNode = staffNodes.item(j);					
					// If the current node is an Element node
					if (staffNode.getNodeType() == Node.ELEMENT_NODE) {
//						System.out.println("  current element: " + staffNode.getNodeName());
						// 2. Get all layers in the staff 
						NodeList layerNodes = staffNode.getChildNodes();
						for (int k = 0; k < layerNodes.getLength(); k++) {
							Node layerNode = layerNodes.item(k);
							// If the current node is an Element node
							if (layerNode.getNodeType() == Node.ELEMENT_NODE) {
//								System.out.println("    current element: " + layerNode.getNodeName());
								// 3. Get all contents in the layer
								NodeList allNodes = layerNode.getChildNodes();
								// Make list of Instances for current system and add Instances
								List<MusicalInstance> instancesCurrentSystem = 
									new ArrayList<MusicalInstance>();
								// Gather only the element Nodes
								List<Node> elementNodes = new ArrayList<Node>();
								for (int l = 0; l < allNodes.getLength(); l++) { 
									Node contentNode = allNodes.item(l);
									if (contentNode.getNodeType() == Node.ELEMENT_NODE) {
										elementNodes.add(contentNode);
									}
								}
								// 
								int noteCounter = 0; // counts only notes
								int elementCounter = -1; // counts all Elements
								for (int l = 0; l < elementNodes.size(); l++) {
									elementCounter++;
									if (noteCounter < incipitSize) {
										Node contentNode = elementNodes.get(l);
										// Make new Instance
										MusicalInstance in = new MusicalInstance();
										String nodeName = contentNode.getNodeName();
										in.setName(nodeName);
										// Increment noteCounter if needed
										if (nodeName.equals("note")) { 
											noteCounter++;
										}
										// 4. Get all attributes of contentNode
										NamedNodeMap attributes = contentNode.getAttributes();											
										// If contentNode is an accidental 
										if(nodeName.equals("accid")) {
											// a. Key sig 
											// NB: It is assumed that a key sig is an accid that follows 
											// (i) a clef; (ii) another key sig (i.e., an accid) TODO Fix assumptions
											String nodeNamePrev = instancesCurrentSystem.get(elementCounter-1).getName();
											String alt = attributes.getNamedItem("accid").getTextContent().trim();
											String note = attributes.getNamedItem("ploc").getTextContent().trim();
											if (nodeNamePrev.equals("clef") || nodeNamePrev.equals("accid")) {
												setAlterationsInfo(note, alt, false);
											}
											// b. Accidental
											else {
												accidentalValid = true;
												setAlterationsInfo(note, alt, true);
											}
										}
										// Set Instance attributes and add to list
										List<String[]> instanceAttributes = new ArrayList<String[]>(); 
										for (int m = 0; m < attributes.getLength(); m++) {	
											Node att = attributes.item(m);
											String attName = att.getNodeName();
											String attValue = att.getTextContent().trim();
											// If contentNode is a note: check whether alteration is necessary
											if (nodeName.equals("note")) {
												// a. Of pitch
												if (attName.equals("pname")) {
													// Check whether the note must be altered and do so
													String altAttValue = checkForAlteration(attValue);
													// If there is an accidental switched on and the current
													// note is the affected note: see whether the accidental
													// must be kept switched on by checking whether the next
													// note has the same pitch
													// NB It is assumed that only one accidental is
													// switched on simultaneously TODO FIx assumption
													if (accidentalValid && 
														accidInfo[notes.indexOf(attValue)] != Alteration.NONE) {
														for (int x = l+1; x < elementNodes.size(); x++) {
															Node nextNode = elementNodes.get(x); 
															if (nextNode.getNodeName().equals("note")) {
																String noteNameNext = 
																	nextNode.getAttributes().getNamedItem("pname").getTextContent();
																if(!noteNameNext.equals(attValue)) {
																	accidentalValid = false;
																	setAlterationsInfo(attValue, "n", true);
																}
																break;
															}
														}
													}
													attValue = altAttValue;
												}
												// b. Of duration
												if (attName.equals("dur") && elementNodes.get(l+1).getNodeName().equals("dot")) {
													attValue = "dotted_" + attValue;
												}
											}
											// Add to list; do not include ulx
											if(!attName.equals("ulx")) {
												instanceAttributes.add(new String[]{attName, attValue});
											}
										}
										in.setAttributes(instanceAttributes);
										instancesCurrentSystem.add(in);
									}
									else {
										break;
									}
								}
								// System done? Add all instances in it to the list
								instances.add(instancesCurrentSystem);
							}
						}
					}
				}
			}
		}
		return instances;
	}
}
