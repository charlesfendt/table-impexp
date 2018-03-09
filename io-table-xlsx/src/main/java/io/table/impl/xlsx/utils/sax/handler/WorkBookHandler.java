package io.table.impl.xlsx.utils.sax.handler;

import java.util.HashMap;
import java.util.Map;

import io.table.impl.xlsx.utils.pojo.Sheet;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class WorkBookHandler extends DefaultHandler {

    /** Id. */
    private String rId;
    /** Type. */
    private String sheetId;
    /** Target. */
    private String name;

    /** Mapping form key to file name. */
    private final Map<String, Sheet> availableSheets = new HashMap<>();

    @Override
    public void startElement(final String namespaceURI, final String localName, final String qName,
            final Attributes atts) {

        if ("sheet".equals(qName)) {
            for (int i = 0; i < atts.getLength(); i++) {
                if ("name".equals(atts.getQName(i))) {
                    this.name = atts.getValue(i);
                } else if ("sheetId".equals(atts.getQName(i))) {
                    this.sheetId = atts.getValue(i);
                } else if ("r:id".equals(atts.getQName(i))) {
                    this.rId = atts.getValue(i);
                }
            }
        }
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        if ("sheet".equals(qName)) {
            this.availableSheets.put(this.rId, new Sheet(this.rId, this.name, this.sheetId));
        }
    }

    /**
     * Getter for the available sheets.
     *
     * @return available sheets
     */
    public Map<String, Sheet> getAvailableContents() {
        return this.availableSheets;
    }

}
