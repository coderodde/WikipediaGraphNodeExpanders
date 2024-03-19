package net.coderodde.wikipedia.graph.expansion;

import org.junit.Test;
import static org.junit.Assert.*;

// Tests in this class require that there is connection to the Wikipedia API!
public class BackwardWikipediaGraphNodeExpanderTest {
    
    private BackwardWikipediaGraphNodeExpander generateSuccessorser;
    
    @Test
    public void testExpand1() {
        generateSuccessorser = 
                new BackwardWikipediaGraphNodeExpander(
                        "https://en.wikipedia.org/wiki/Disc_jockey");
        
        assertEquals("en.wikipedia.org", generateSuccessorser.getBasicUrl());
        assertTrue(generateSuccessorser.isValidNode("Disc_jockey"));
        assertTrue(!generateSuccessorser.generateSuccessors("Disc_jockey").isEmpty());
        
        assertFalse(generateSuccessorser.isValidNode("Disc_jfdsfsockey"));
        assertTrue(generateSuccessorser.generateSuccessors("Disc_jockeyfdsfsd").isEmpty());
    }
    
    @Test
    public void testExpand2() {    
        generateSuccessorser = 
                new BackwardWikipediaGraphNodeExpander(
                        "https://fi.wikipedia.org/wiki/DJ");
        
        assertEquals("fi.wikipedia.org", generateSuccessorser.getBasicUrl());
        assertTrue(generateSuccessorser.isValidNode("DJ"));
        assertTrue(!generateSuccessorser.generateSuccessors("DJ").isEmpty());
        
        assertFalse(generateSuccessorser.isValidNode("DJJJfdsfJJ"));
        assertTrue(generateSuccessorser.generateSuccessors("DJJJfdsafd").isEmpty());
    }
    
    @Test
    public void testExpandWithNonSecureHTTPPrefix() {
        generateSuccessorser = 
                new BackwardWikipediaGraphNodeExpander(
                        "http://en.wikipedia.org/wiki/Disc_jockey");
        
        assertEquals("en.wikipedia.org", generateSuccessorser.getBasicUrl());
        assertTrue(generateSuccessorser.isValidNode("Disc_jockey"));
        assertTrue(!generateSuccessorser.generateSuccessors("Disc_jockey").isEmpty());
        
        assertFalse(generateSuccessorser.isValidNode("Disc_jfdsfsockey"));
        assertTrue(generateSuccessorser.generateSuccessors("Disc_jockeyfdsfsd").isEmpty());
    }
    
    @Test
    public void testExpandWithoutProtocolPrefix() {
        generateSuccessorser = 
                new BackwardWikipediaGraphNodeExpander(
                        "en.wikipedia.org/wiki/Disc_jockey");
        
        assertEquals("en.wikipedia.org", generateSuccessorser.getBasicUrl());
        assertTrue(generateSuccessorser.isValidNode("Disc_jockey"));
        assertTrue(!generateSuccessorser.generateSuccessors("Disc_jockey").isEmpty());
        
        assertFalse(generateSuccessorser.isValidNode("Disc_jfdsfsockey"));
        assertTrue(generateSuccessorser.generateSuccessors("Disc_jockeyfdsfsd").isEmpty());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testOnBadWikipediaURL1() {
        new BackwardWikipediaGraphNodeExpander("en.wikipedia.org");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testOnBadWikipediaURL2() {
        new BackwardWikipediaGraphNodeExpander("en.wikipedia.org/");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testOnBadWikipediaURL3() {
        new BackwardWikipediaGraphNodeExpander("en.wikipedia.org/wki");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testOnBadWikipediaURL4() {
        new BackwardWikipediaGraphNodeExpander(
                "en.wikipedia.org/wki/Disc_jockey");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testOnBadWikipediaURL5() {
        new BackwardWikipediaGraphNodeExpander(
                "htp://en.wikipedia.org/wiki/Disc_jockey");
    }
}
