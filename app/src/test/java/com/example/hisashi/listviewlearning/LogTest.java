package com.example.hisashi.listviewlearning;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Created by hisashi on 2015-09-24.
 */
public class LogTest {

    @Test
    public void testGetInstance() throws Exception {
        Log thisInstance = Log.getInstance(null);
        Log thatInstance = Log.getInstance(null);
        Assert.assertEquals(thisInstance, thatInstance);
    }
}