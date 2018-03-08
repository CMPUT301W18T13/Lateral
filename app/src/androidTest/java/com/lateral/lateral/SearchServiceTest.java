package com.lateral.lateral;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.lateral.lateral.service.implementation.SearchService;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@RunWith(AndroidJUnit4.class)
public class SearchServiceTest {

    @Test
    public void testInt(){
        int shouldBe = 5;
        SearchService testSS = new SearchService("");
        assertEquals(testSS.testInt, shouldBe);
    }

    @Test
    public void testExtractKeywords(){
        String query = "Hello World This Is A Query";
        ArrayList<String> keys;

        SearchService testSS = new SearchService("");
        keys = testSS.extractKeywords(query);

        assertEquals(keys.get(0), "Hello");
        assertNotEquals(keys.get(0), "hello");
        assertEquals(keys.get(1), "World");
        assertEquals(keys.get(2), "This");
        assertEquals(keys.get(3), "Is");
        assertEquals(keys.get(4), "A");
        assertEquals(keys.get(5), "Query");


    }


}
