/*
 * Copyright (C) 2006-2018 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package org.talend.mdm.commmon.util.core;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.exception.XmlBeanDefinitionException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class XMLUtils {

    private static final Logger LOGGER = Logger.getLogger(XMLUtils.class);

    private static final DocumentBuilderFactory DOM_BUILDER_FACTORY;

    private static final XMLReader XML_READER;

    private static final SAXParserFactory SAX_PARSER_FACTORY;

    private static final XMLInputFactory XML_INPUT_FACTORY;

    static {
        DOM_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();
        DOM_BUILDER_FACTORY.setIgnoringComments(true);
        DOM_BUILDER_FACTORY.setExpandEntityReferences(false);

        try {
            XML_READER = XMLReaderFactory.createXMLReader();
            XML_READER.setFeature(MDMXMLConstants.FEATURE_DISALLOW_DOCTYPE, true);
            XML_READER.setFeature(MDMXMLConstants.FEATURE_LOAD_EXTERNAL, false);
            XML_READER.setFeature(MDMXMLConstants.FEATURE_EXTERNAL_GENERAL_ENTITIES, false);
            XML_READER.setFeature(MDMXMLConstants.FEATURE_EXTERNAL_PARAM_ENTITIES, false);
            XML_READER.setContentHandler(new DefaultHandler());
        } catch (Exception e) {
            throw new XmlBeanDefinitionException("Error occurred to initialize xmlReader", e);
        }

        try {
            SAX_PARSER_FACTORY = SAXParserFactory.newInstance();
            SAX_PARSER_FACTORY.setFeature(MDMXMLConstants.FEATURE_EXTERNAL_GENERAL_ENTITIES, false);
            SAX_PARSER_FACTORY.setFeature(MDMXMLConstants.FEATURE_EXTERNAL_PARAM_ENTITIES, false);
            SAX_PARSER_FACTORY.setFeature(MDMXMLConstants.FEATURE_LOAD_EXTERNAL, false);
            SAX_PARSER_FACTORY.setValidating(true);
            SAX_PARSER_FACTORY.setNamespaceAware(true);
        } catch (Exception e) {
            throw new XmlBeanDefinitionException("Error occurred to initialize SAXParserFactory", e);
        }

        XML_INPUT_FACTORY = XMLInputFactory.newInstance();
        XML_INPUT_FACTORY.setProperty(XMLInputFactory.IS_VALIDATING, Boolean.FALSE);
        XML_INPUT_FACTORY.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
        XML_INPUT_FACTORY.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
        XML_INPUT_FACTORY.setProperty(MDMXMLConstants.PROPERTY_IS_SUPPORT_EXT_ENTITY, false);
    }

    public static XMLStreamReader createXMLStreamReader(InputStream inputStream) {
        try {
            return XML_INPUT_FACTORY.createXMLStreamReader(inputStream);
        } catch (Exception e) {
            throw new XmlBeanDefinitionException("Error occurred creating a new XMLStreamReader from a java.io.InputStream.", e);
        }
    }

    public static XMLEventReader createXMLEventReader(InputStream inputStream) {
        try {
            return XML_INPUT_FACTORY.createXMLEventReader(inputStream);
        } catch (Exception e) {
            throw new XmlBeanDefinitionException("Error occurred creating a new XMLEventReader from a InputStream.", e);
        }
    }

    public static XMLEventReader createXMLEventReader(StringReader source) {
        try {
            return XML_INPUT_FACTORY.createXMLEventReader(source);
        } catch (Exception e) {
            throw new XmlBeanDefinitionException("Error occurred creating a new XMLEventReader from a StringReader.", e);
        }
    }

    public static SAXParser getSAXParser() {
        try {
            return SAX_PARSER_FACTORY.newSAXParser();
        } catch (Exception e) {
            throw new XmlBeanDefinitionException("Error occurred using SAXParserFactory to create a SAXParser.", e);
        }
    }

    public static XMLReader getXMLReader() {
        return getXMLReader(new DefaultHandler());
    }

    public static XMLReader getXMLReader(DefaultHandler handler) {
        XML_READER.setContentHandler(handler);
        return XML_READER;
    }

    public static DocumentBuilderFactory getDocumentBuilderFactory() {
        return DOM_BUILDER_FACTORY;
    }

    public static Optional<DocumentBuilder> getDocumentBuilder() {
        DocumentBuilder builder = null;
        try {
            builder = getDocumentBuilderFactory().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new XmlBeanDefinitionException("Error occurred using DocumentBuilderFactory to create a DocumentBuilder.", e);
        }
        return Optional.ofNullable(builder);
    }

    public static boolean isExistExtEntity(InputStream stream) {
        boolean results = false;
        try {
            String result = new BufferedReader(new InputStreamReader(stream))
                    .lines().collect(Collectors.joining(System.lineSeparator()));
            results = isExistExtEntity(result);
        } catch (Exception e) {
            LOGGER.error("An unexpected exception occurred." + e);
        }
        return results;
    }

    /**
     * Take a security measurements against XML external entity attacks.
     *  {"<!ENTITY desc SYSTEM \"ect/passwd\">,
     * "<!ENTITY desc SYSTEM>", "<!ENTITY desc public>", "<!ENTITY desc system \"file:abc.txt\">", "<!ENTITY desc public
     * \"http://www.baidu.com\">", "<!ENTITY abc public>", "<!entity public \"http://www.baidu.com\">", }
     * 
     * @param rawXml
     * @return
     */
    public static boolean isExistExtEntity(String rawXml) {
        if (rawXml == null) {
            return false;
        }
        Pattern pattern = Pattern.compile("<!ENTITY\\s+\\S*\\s+[SYSTEM|PUBLIC]{1}.+?>", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(rawXml);
        return matcher.find();
    }
}
