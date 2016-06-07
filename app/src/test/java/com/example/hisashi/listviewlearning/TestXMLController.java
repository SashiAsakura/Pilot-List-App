package com.example.hisashi.listviewlearning;

import junit.framework.Assert;

import org.junit.Test;

import java.io.UnsupportedEncodingException;

/**
 * Created by hisashi on 2015-10-28.
 */
public class TestXMLController {
    @Test
    public void testParse() throws UnsupportedEncodingException {
        String stringXML = "<double xmlns=\"http://www.webserviceX.NET/\">1.1073</double>";
        String actual = null;

        try {
            actual = XMLParser.parse(stringXML);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String expected = "1.1073";
        Assert.assertEquals(expected, actual);
    }
}
