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

import io.table.api.ITableWriter;
import io.table.impl.xlsx.TableIoXlsxUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import org.junit.Test;

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
		final File out = new File("./test.xlsx");
		out.createNewFile();
		try (final OutputStream output = new FileOutputStream(out)) {

			final ITableWriter writer = TableIoXlsxUtils.createWriter();
			writer.initialize(output);
			writer.appendNewLine("a", "b", "c");
			writer.appendNewLine();
			writer.appendCell("foo");
			writer.appendCell(12345);
			writer.appendCell(new Date());
			writer.close();
		}
	}
}
