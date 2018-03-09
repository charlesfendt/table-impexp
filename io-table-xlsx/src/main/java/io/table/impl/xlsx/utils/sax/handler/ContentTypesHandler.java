package io.table.impl.xlsx.utils.sax.handler;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ContentTypesHandler extends DefaultHandler {

    /** Mapping form key to file name. */
    private final Map<String, String> availableContents = new HashMap<>();
    /** Current identifier for file name. */
    private String identifier = null;
    private String value = null;

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(final String namespaceURI, final String localName, final String qName,
            final Attributes atts) {
        if ("Override".equals(qName)) {
            for (int i = 0; i < atts.getLength(); i++) {
                if ("ContentType".equals(atts.getQName(i))) {
                    this.identifier = atts.getValue(i);
                } else if ("PartName".equals(atts.getQName(i))) {
                    this.value = atts.getValue(i);
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        if ("Override".equals(qName)) {
            this.availableContents.put(this.identifier, this.value);
        }
    }

    /**
     * Getter for the mapping of key to file name.
     *
     * @return mapping of key to file name
     */
    public Map<String, String> getAvailableContents() {
        return this.availableContents;
    }
}
