package io.table.impl.xlsx.utils.sax.handler;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Parses the shared string table.
 *
 * @author smarty
 *
 */
public class SharedStringHandler extends DefaultHandler {

    /** Boolean indicates if in shared string element. */
    private boolean inSharedString;
    /** The current value of shared string. */
    private String sharedStringValue;
    /** Current counter, meaning id of the string. */
    private int count = 0;
    /** Map of shared strings. */
    private final Map<Integer, String> sharedStrings = new HashMap<>();

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(final String namespaceURI, final String localName, final String qName,
            final Attributes atts) {
        if (qName.equals("t")) {
            this.inSharedString = true;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        if (qName.equals("t")) {
            this.inSharedString = false;
            this.sharedStrings.put(this.count, this.sharedStringValue);
            this.count++;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    @Override
    public void characters(final char[] ch, final int start, final int length) {

        if (this.inSharedString) {
            this.sharedStringValue = new String(ch, start, length);
        }

    }

    /**
     * Getter for the string mapping table
     * 
     * @return the string mapping table
     */
    public Map<Integer, String> getMappingTable() {
        return this.sharedStrings;
    }

}
