/**
 * CsvWriterTest.java
 *
 * Copyright (c) 2017, Charles Fendt. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package io.table.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import org.junit.Test;

import io.table.api.ITableWriter;
import io.table.impl.xlsx.TableWriterXlsxImpl;

/**
 * Test class.
 *
 * @author charles
 */
public final class XlsxWriterTest {

    /**
     * Test method for a simple output.
     *
     * @throws IOException
     *             any I/O error.
     */
    @Test
    public void testSimpleOutput() throws IOException {
        final File out = new File("./testWrite.xlsx");
        out.createNewFile();
        try (final OutputStream output = new FileOutputStream(out)) {

            final ITableWriter writer = new TableWriterXlsxImpl("IZARMobile", "2.0", "ooo");
            writer.initialize(output);
            // writer.appendNewHeaderLine("a", "b", "c");
            writer.appendCell("a", true, "Comment / a");
            writer.appendCell("b", true, "Comment <b");
            writer.appendCell("c", true, "Comment > c");
            writer.appendNewLine();
            writer.appendCell("foo");
            writer.appendCell(12345);
            writer.appendCell(12345.678);
            writer.appendNewLine();
            writer.appendCell("777.333");
            writer.appendCell(new Date());
            writer.appendNewLine();
            writer.appendCell("123456");
            writer.appendCell("123456 / ");
            writer.appendCell("123456 <");
            writer.appendCell("m³");
            writer.appendCell("IZAR RADIO EXTERN RS232 / L-BUS (v62)");
            writer.appendCell("values.volume.main (MEASUREMENT UNITS)");
            writer.appendCell((String) null);

            writer.close();
        }
    }

    /**
     * Test method for a simple output.
     *
     * @throws IOException
     *             any I/O error.
     */
    @Test
    public void testMultiOutput() throws IOException {
        final File out = new File("./testWrite-multi.xlsx");
        out.createNewFile();
        try (final OutputStream output = new FileOutputStream(out)) {

            final TableWriterXlsxImpl writer = new TableWriterXlsxImpl("IZARMobile", "2.0", "ooo");
            writer.initialize(output);
            // writer.appendNewHeaderLine("a", "b", "c");
            writer.appendCell("a", true, "Comment / a");
            writer.appendCell("b", true, "Comment <b");
            writer.appendCell("c", true, "Comment > c");
            writer.appendNewLine();
            writer.appendCell("foo");
            writer.appendCell(12345);
            writer.appendCell(12345.678);
            writer.appendNewLine();
            writer.appendCell("777.333");
            writer.appendCell(new Date());
            writer.appendNewLine();
            writer.appendCell("123456");
            writer.appendCell("123456 / ");
            writer.appendCell("123456 <");
            writer.appendCell("m³");
            writer.appendCell("IZAR RADIO EXTERN RS232 / L-BUS (v62)");
            writer.appendCell("values.volume.main (MEASUREMENT UNITS)");
            writer.appendCell((String) null);

            writer.openNewWs("Foo");
            writer.appendCell("foo");
            writer.appendNewLine();
            writer.appendCell("a", true, "Comment / a");

            writer.openNewWs("Bar");
            writer.appendCell("bar");

            writer.close();
        }
    }

    /**
     * Test method for a simple output.
     *
     * @throws IOException
     *             any I/O error.
     */
    @Test(expected = RuntimeException.class)
    @SuppressWarnings("resource")
    public void testWrongVersion() throws IOException {
        new TableWriterXlsxImpl("IZARMobile", "2", "ooo");
    }
}
