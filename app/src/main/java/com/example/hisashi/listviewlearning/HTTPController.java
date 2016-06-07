package com.example.hisashi.listviewlearning;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by hisashi on 2015-10-28.
 */
public class HTTPController {

    public static String getHTTPResponseStringFrom(String stringUrl, String authorizationKey) throws IOException{
        OkHttpClient okHttpClient = new OkHttpClient();
        Request httpRequest;
        if (authorizationKey == null || authorizationKey.isEmpty()) {
            httpRequest = new Request.Builder().url(stringUrl).build();
        }
        else {
            httpRequest = new Request.Builder().url(stringUrl).header("Authorization", authorizationKey).build();
        }

        Response httpResponse = okHttpClient.newCall(httpRequest).execute();
        String result = httpResponse.body().string();

        System.out.println("getHTTPResponseStringFrom - httpRequest=" + httpRequest.toString()
            + ", httpResponse=" + httpResponse.toString() + ", result=" + result);
        return result;
    }
}
