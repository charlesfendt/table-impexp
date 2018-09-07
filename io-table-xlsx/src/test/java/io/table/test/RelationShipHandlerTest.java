package io.table.test;

import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import io.table.impl.xlsx.utils.pojo.RelationShip;
import io.table.impl.xlsx.utils.sax.handler.RelsHandler;
import org.junit.Assert;
import org.junit.Test;

public class RelationShipHandlerTest {

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
        final RelsHandler handler = new RelsHandler();
        saxParser.parse(this.getClass().getClassLoader().getResourceAsStream("./test.xlsx.unzipped/_rels/.rels"),
                handler);
        final Map<String, RelationShip> mapping = handler.getAvailableContents();
        final RelationShip relationShip = mapping.get("rId1");
        Assert.assertEquals("xl/workbook.xml", relationShip.getTarget());
        Assert.assertEquals("rId1", relationShip.getrId());
        Assert.assertEquals("http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument",
                relationShip.getType());
        Assert.assertEquals(3, mapping.size());
    }

    /**
     * Test method.
     *
     * @throws Exception
     *             in case of error
     */
    @Test
    public void testWorkbookRels() throws Exception {
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        final SAXParser saxParser = factory.newSAXParser();
        final RelsHandler handler = new RelsHandler();
        saxParser.parse(
                this.getClass().getClassLoader().getResourceAsStream("./test.xlsx.unzipped/xl/_rels/workbook.xml.rels"),
                handler);
        final Map<String, RelationShip> mapping = handler.getAvailableContents();
        final RelationShip relationShip = mapping.get("rId1");
        Assert.assertEquals("worksheets/sheet1.xml", relationShip.getTarget());
        Assert.assertEquals("rId1", relationShip.getrId());
        Assert.assertEquals("http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet",
                relationShip.getType());
        Assert.assertEquals(4, mapping.size());
    }

}
