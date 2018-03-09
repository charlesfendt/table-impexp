package io.table.impl.xlsx.utils.sax.handler;

import java.util.HashMap;
import java.util.Map;

import io.table.impl.xlsx.utils.pojo.RelationShip;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class RelsHandler extends DefaultHandler {

    /** Mapping form key to file name. */
    private final Map<String, RelationShip> availableRelationsShips = new HashMap<>();
    /** Current identifier for file name. */
    private String identifier = null;
    private String type = null;
    private String target = null;

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(final String namespaceURI, final String localName, final String qName,
            final Attributes atts) {
        if ("Relationship".equals(qName)) {
            for (int i = 0; i < atts.getLength(); i++) {
                if ("Id".equals(atts.getQName(i))) {
                    this.identifier = atts.getValue(i);
                } else if ("Type".equals(atts.getQName(i))) {
                    this.type = atts.getValue(i);
                } else if ("Target".equals(atts.getQName(i))) {
                    this.target = atts.getValue(i);
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
        if ("Relationship".equals(qName)) {
            this.availableRelationsShips.put(this.identifier,
                    new RelationShip(this.identifier, this.type, this.target));
        }
    }

    /**
     * Getter for the mapping of key to relations ship.
     *
     * @return mapping of key to relationship
     */
    public Map<String, RelationShip> getAvailableContents() {
        return this.availableRelationsShips;
    }
}
