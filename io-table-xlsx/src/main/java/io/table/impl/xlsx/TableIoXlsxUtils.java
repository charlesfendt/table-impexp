/**
 * TableIoXlsxUtils.java
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
package io.table.impl.xlsx;

import io.table.api.ITableReader;
import io.table.api.ITableWriter;

/**
 * Utility class for the XLSX implementation.
 *
 * @author charles
 */
public final class TableIoXlsxUtils {

	/**
	 * Hidden constructor of the utility class.
	 */
	private TableIoXlsxUtils() {
		super();
	}
	
	/**
	 * Creator method for the XLSX implementation of the ITableWriter service.
	 *
	 * @return a new instance of the writer implementation.
	 */
	public static ITableWriter createWriter() {
		return new TableWriterXlsxImpl("io.table", "1.0", "data");
	}
	
	/**
	 * Creator method for the XLSX implementation of the ITableReader service.
	 *
	 * @return a new instance of the reader implementation.
	 */
	public static ITableReader createReader() {
		return new TableReaderXlsxImpl();
	}
	
}
