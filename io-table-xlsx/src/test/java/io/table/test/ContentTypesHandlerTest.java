package io.table.test;

import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import io.table.impl.xlsx.utils.sax.handler.ContentTypesHandler;
import org.junit.Assert;
import org.junit.Test;

public class ContentTypesHandlerTest {

    /**
     * Test method.
     *
     * @throws Exception
     *             in case of error
     */
    @Test
    public void test() throws Exception {
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        final SAXParser saxParser = factory.newSAXParser();
        final ContentTypesHandler handler = new ContentTypesHandler();
        saxParser.parse(
                this.getClass().getClassLoader().getResourceAsStream("./test.xlsx.unzipped/[Content_Types].xml"),
                handler);
        final Map<String, String> mapping = handler.getAvailableContents();
        Assert.assertEquals("/xl/worksheets/sheet1.xml",
                mapping.get("application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"));
        Assert.assertEquals("/xl/sharedStrings.xml",
                mapping.get("application/vnd.openxmlformats-officedocument.spreadsheetml.sharedStrings+xml"));
        Assert.assertEquals(7, mapping.size());
    }

}
