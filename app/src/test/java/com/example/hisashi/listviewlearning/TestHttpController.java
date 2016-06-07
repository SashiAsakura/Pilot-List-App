package com.example.hisashi.listviewlearning;

import junit.framework.Assert;

import org.junit.Test;

import java.io.IOException;

/**
 * Created by hisashi on 2015-10-28.
 */
public class TestHttpController {
    @Test
    public void testGetDataFrom() throws IOException {
        String stringURL = "http://www.webservicex.net/CurrencyConvertor.asmx/ConversionRate?FromCurrency=EUR&ToCurrency=USD";
        String httpResponse = HTTPController.getHTTPResponseStringFrom(stringURL, null);
        String actual = XMLParser.parse(httpResponse);
        String expectedResponse = "1.0944";

        System.out.println("expectedResponse=" + expectedResponse + ", httpResponse=" + httpResponse);
        Assert.assertEquals(expectedResponse, actual);
    }

    @Test
    public void testGetMattersJson() throws IOException {
        String stringURL = "https://app.goclio.com/api/v2/matters";
        String authorizationKey = "Bearer Xzd7LAtiZZ6HBBjx0DVRqalqN8yjvXgzY5qaD15a";
        String httpResponse = HTTPController.getHTTPResponseStringFrom(stringURL, authorizationKey);

        System.out.println("httpResponse=" + httpResponse);
        Assert.assertEquals(false, true);
    }
}
