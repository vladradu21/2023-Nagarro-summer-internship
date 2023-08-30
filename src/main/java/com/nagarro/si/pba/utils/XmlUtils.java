package com.nagarro.si.pba.utils;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.nagarro.si.pba.exceptions.WrongFormatException;

public class XmlUtils {
    private XmlUtils() {
    }

    public static Document parseXmlIntoDocument(String xmlString) {
        try {
            InputSource source = new InputSource(new StringReader(xmlString));
            
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(source);
        } catch (SAXException | IOException | ParserConfigurationException e) {
            System.out.println(e);
            throw new WrongFormatException(e.getMessage());
        }
    }
}
