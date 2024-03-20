package net.coderodde.wikipedia.graph.expansion;

import com.github.coderodde.graph.pathfinding.delayed.AbstractNodeExpander;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Locale;
import org.apache.commons.io.IOUtils;

/**
 * This abstract class specifies the facilities shared by both forward and 
 * backward node expanders.
 * 
 * @version 1.0.0 (Mar 20, 2024)
 */
public abstract class AbstractWikipediaGraphNodeExpander
extends AbstractNodeExpander<String> {
   
    /**
     * The script URL template for expanding forward.
     */
    protected static final String FORWARD_REQUEST_API_URL_SUFFIX = 
            "?action=query" +
            "&titles=%s" + 
            "&prop=links" + 
            "&pllimit=max" + 
            "&format=json"; 
    
    /**
     * The script URL template for expanding backwards.
     */
    protected static final String BACKWARD_REQUEST_API_URL_SUFFIX = 
            "?action=query" +
            "&list=backlinks" +
            "&bltitle=%s" + 
            "&bllimit=max" + 
            "&format=json";
    
    protected final String languageLocaleName;
    
    protected static final String API_URL_FORMAT = 
            "https://%s.wikipedia.org/w/api.php";
    
    /**
     * Caches the textual representation of the URL pointing to the 
     * <a href="https://www.mediawiki.org/wiki/API:Main_page">Wikipedia API</a>.
     */
    protected final String apiUrl;

    /**
     * Constructs a graph node expander for the language subgraph specified in
     * the input URL.
     */
    protected AbstractWikipediaGraphNodeExpander(String languageLocaleName) {
        checkLanguageLocaleName(languageLocaleName);
        this.languageLocaleName = languageLocaleName;
        this.apiUrl = constructAPIURL(languageLocaleName);
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isValidNode(final String node) {
        return !generateSuccessors(node).isEmpty();
    }
    
    /**
     * The actual implementation of the method producing the neighbour JSON data
     * of the article graph node.
     * 
     * @param articleName the node to expand.
     * @param forward specifies the direction of the node expansion operation.
     *                if {@code forward} is {@code true}, generates the child
     *                nodes of {@code node}. Otherwise, generates the parent 
     *                nodes of {@code node}.
     * @return the JSON data describing the forward- or backward-links.
     */
    protected String downloadJson(final String articleName,
                                  final boolean forward) {
        String jsonDataUrl;
        
        try {
            jsonDataUrl =
                    apiUrl + String.format(forward ?
                                                FORWARD_REQUEST_API_URL_SUFFIX :
                                                BACKWARD_REQUEST_API_URL_SUFFIX,
                                           URLEncoder.encode(articleName,
                                                             "UTF-8"));
            
        } catch (final UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex.getMessage(), ex);
        }
        
        String jsonText;
        
        try {
            jsonText = IOUtils.toString(new URL(jsonDataUrl),
                                        Charset.forName("UTF-8"));
        } catch (final IOException ex) {
            throw new IllegalStateException(
                    "[I/O ERROR] Failed loading the JSON data from the " +
                    "Wikipedia API: " + ex.getMessage(), ex);
        }
        
        return jsonText;
    }
    
    /**
     * Constructs the full Wikipedia article URL with the input title.
     * 
     * @param title the title of the target article URL.
     * @return the full URL to the article.
     */
    protected String constructFullWikipediaLink(String title) {
        return String.format("https://%s\\.wikipedia\\.org/wiki/%s",
                             languageLocaleName,
                             title);
    }
    
    /**
     * Constructs a Wikipedia API URL from the raw {@code wikipediaUrl}. The
     * input {@code wikipediaUrl} is of the form <tt>en.wikipedia.org</tt>. The
     * idea here is that the search may be applied to article subgraphs with 
     * different languages.
     * 
     * @param wikipediaUrl the Wikipedia URL.
     * @return full URL to Wikipedia API.
     */
    private String constructAPIURL(final String languageLocaleName) {
        return String.format(API_URL_FORMAT, languageLocaleName);
    }
    
    /**
     * Checks that the input language ISO code is in the list of existing codes.
     * 
     * @param languageLocaleName the language country code to check.
     */
    private void checkLanguageLocaleName(String languageLocaleName) {
        if (!Arrays.asList(Locale.getISOLanguages())
                   .contains(languageLocaleName)) {
            
            throw new IllegalArgumentException(
                    String.format("Unknown language locale name: %s.", 
                                  languageLocaleName));
        }
    }
}
