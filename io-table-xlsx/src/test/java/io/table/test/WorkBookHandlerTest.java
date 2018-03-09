package io.table.test;

import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import io.table.impl.xlsx.utils.pojo.Sheet;
import io.table.impl.xlsx.utils.sax.handler.WorkBookHandler;
import org.junit.Assert;
import org.junit.Test;

public class WorkBookHandlerTest {

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
        final WorkBookHandler handler = new WorkBookHandler();
        saxParser.parse(this.getClass().getClassLoader().getResourceAsStream("./test.xlsx.unzipped/xl/workbook.xml"),
                handler);
        final Map<String, Sheet> mapping = handler.getAvailableContents();
        final Sheet sheet = mapping.get("rId1");
        Assert.assertEquals("data", sheet.getName());
        Assert.assertEquals("rId1", sheet.getrId());
        Assert.assertEquals("1", sheet.getSheetId());
    }

}
