package io.table.impl.xlsx.utils.sax.handler;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class XlsxHandler extends DefaultHandler {
    @Override
    public void startDocument() {
        System.out.println("Document starts.");
    }

    @Override
    public void endDocument() {
        System.out.println("Document ends.");
    }

    @Override
    public void startElement(final String namespaceURI, final String localName, final String qName,
            final Attributes atts) {
        System.out.println("namespaceURI: " + namespaceURI);
        System.out.println("localName: " + localName);
        System.out.println("qName: " + qName);
        for (int i = 0; i < atts.getLength(); i++) {
            // if("application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml".eq {
            // System.out.printf("Attribut no. %d: %s = %s%n", i, atts.getQName(i), atts.getValue(i));
            // }
        }
    }

    @Override
    public void characters(final char[] ch, final int start, final int length) {
        System.out.println("Characters:");

        for (int i = start; i < (start + length); i++) {
            System.out.printf("%1$c (%1$x) ", (int) ch[i]);
        }

        System.out.println();
    }

}
