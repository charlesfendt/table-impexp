package io.table.test;

import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import io.table.impl.xlsx.TableReaderXlsxImpl.EnumDataType;
import io.table.impl.xlsx.utils.pojo.Row;
import io.table.impl.xlsx.utils.pojo.Value;
import io.table.impl.xlsx.utils.sax.handler.SharedStringHandler;
import io.table.impl.xlsx.utils.sax.handler.SheetHandler;
import org.junit.Assert;
import org.junit.Test;

public class SheetHandlerTest {

    @Test
    public void test() {
        try {
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            final SAXParser saxParser = factory.newSAXParser();
            final SharedStringHandler sharedStringHandler = new SharedStringHandler();
            saxParser.parse(
                    this.getClass().getClassLoader().getResourceAsStream("./test.xlsx.unzipped/xl/sharedStrings.xml"),
                    sharedStringHandler);

            final SheetHandler handler = new SheetHandler(sharedStringHandler.getMappingTable());
            saxParser.parse(this.getClass().getClassLoader()
                    .getResourceAsStream("./test.xlsx.unzipped/xl/worksheets/sheet1.xml"), handler);

            // check size all rows
            final Map<Integer, Row> rows = handler.getRows();
            Assert.assertEquals(rows.size(), 5);

            // check first row
            final Row row1 = rows.get(1);
            Assert.assertEquals(3, row1.getColumnCount());
            Assert.assertEquals(1, row1.getIndex());
            final Map<String, Value> valuesRow1 = row1.getValues();
            Assert.assertEquals(3, valuesRow1.size());

            // check first value
            final Value valueA1 = valuesRow1.get("A1");
            Assert.assertEquals("a", valueA1.getVal());
            Assert.assertEquals("A1", valueA1.getCell());
            Assert.assertEquals(EnumDataType.STRING, valueA1.getDataType());

            // check second value
            final Value valueB1 = valuesRow1.get("B1");
            Assert.assertEquals("b", valueB1.getVal());
            Assert.assertEquals("B1", valueB1.getCell());
            Assert.assertEquals(EnumDataType.STRING, valueB1.getDataType());

            // check third value
            final Value valueC1 = valuesRow1.get("C1");
            // Assert.assertEquals("b", valueC1.getVal());
            // Assert.assertEquals("B1", valueC1.getCell());
            // Assert.assertEquals(EnumDataType.DATE, valueC1.getDataType());

            // check second row
            final Row row2 = rows.get(2);
            Assert.assertEquals(0, row2.getColumnCount());
            Assert.assertEquals(2, row2.getIndex());
            Assert.assertTrue(row2.getValues().isEmpty());

            // check third row
            final Row row3 = rows.get(3);
            Assert.assertEquals(3, row3.getColumnCount());
            Assert.assertEquals(3, row3.getIndex());
            Assert.assertEquals(3, row3.getIndex());
            final Map<String, Value> valuesRow3 = row3.getValues();
            Assert.assertEquals(3, valuesRow3.size());

            // check first value
            final Value valueA3 = row3.getValues().get("A3");
            Assert.assertEquals("foo", valueA3.getVal());
            Assert.assertEquals(EnumDataType.STRING, valueA3.getDataType());
            Assert.assertEquals("A3", valueA3.getCell());

            // check fifth row
            final Row row5 = rows.get(5);
            Assert.assertEquals(2, row5.getColumnCount());
            Assert.assertEquals(5, row5.getIndex());

            final Map<String, Value> valuesRow5 = row5.getValues();
            Assert.assertEquals(1, valuesRow5.size());
            final Value valueB5 = valuesRow5.get("B5");
            Assert.assertEquals("Hallo", valueB5.getVal());
            Assert.assertEquals(EnumDataType.STRING, valueB5.getDataType());
            Assert.assertEquals("B5", valueB5.getCell());

        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

}
