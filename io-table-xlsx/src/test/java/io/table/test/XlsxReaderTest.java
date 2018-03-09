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
import java.io.FileInputStream;
import java.io.IOException;

import io.table.api.ITableReader;
import io.table.impl.xlsx.TableIoXlsxUtils;
import org.junit.Test;

/**
 * Test class.
 *
 * @author charles
 */
public final class XlsxReaderTest {

    /**
     * Test method for a simple output.
     *
     * @throws IOException
     *             any I/O error.
     */
    @Test
    public void testSimpleInput() throws IOException {
        final File in = new File("./test.xlsx");
        try (final FileInputStream output = new FileInputStream(in)) {

            final ITableReader reader = TableIoXlsxUtils.createReader();
            reader.initialize(output);

            while (reader.nextRow()) {
                System.out.println("New Row");
                for (int i = 1; i <= reader.getColumnCount(); i++) {
                    System.out.println(reader.getCellAsString(i));
                }
            }

            reader.close();
        }
    }
}
