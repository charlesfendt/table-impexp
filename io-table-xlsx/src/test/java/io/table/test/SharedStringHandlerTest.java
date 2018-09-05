package io.table.test;

import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import io.table.impl.xlsx.utils.sax.handler.SharedStringHandler;
import org.junit.Assert;
import org.junit.Test;

public class SharedStringHandlerTest {

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
        final SharedStringHandler handler = new SharedStringHandler();
        saxParser.parse(
                this.getClass().getClassLoader().getResourceAsStream("./test.xlsx.unzipped/xl/sharedStrings.xml"),
                handler);
        final Map<Integer, String> mapping = handler.getMappingTable();
        Assert.assertEquals("a", mapping.get(0));
        Assert.assertEquals("b", mapping.get(1));
        Assert.assertEquals("c", mapping.get(2));
        Assert.assertEquals("foo", mapping.get(3));
        Assert.assertEquals("Hallo", mapping.get(4));
    }

}
