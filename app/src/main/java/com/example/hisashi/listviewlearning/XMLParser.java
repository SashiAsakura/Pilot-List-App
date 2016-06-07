package com.example.hisashi.listviewlearning;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by hisashi on 2015-10-28.
 */
public class XMLParser {
    public static String parse(String xml) throws UnsupportedEncodingException {
        String result = null;
        SAXBuilder builder = new SAXBuilder();
        InputStream stream = new ByteArrayInputStream(xml.getBytes("UTF-8"));

        try {
            Document document = (Document) builder.build(stream);
            Element parentNode = document.getRootElement();
            result = parentNode.getText();
            System.out.println("conversionRate=" + result);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        return result;
    }
}
