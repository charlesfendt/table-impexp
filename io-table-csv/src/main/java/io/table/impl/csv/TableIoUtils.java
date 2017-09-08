/**
 * TableIoUtils.java
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
package io.table.impl.csv;

import io.table.api.ITableWriter;

import java.nio.charset.StandardCharsets;

/**
 * Provider for the CSV implementation.
 *
 * @author charles
 */
public final class TableIoUtils {

	/**
	 * Hidden constructor of the utility class.
	 */
	private TableIoUtils() {
		super();
	}

	/**
	 * Creator method for the CSV implementation of the ITableWriter service. The default quote character <code>"</code> whill be used.
	 *
	 * @param separator
	 *            The separator to use.
	 * @return a new instance of the writer implementation.
	 */
	public static ITableWriter createWriter(final char separator) {
		return new TableWriterCsvImpl(Character.toString(separator), "\"", "\"\"", "\n", StandardCharsets.UTF_8);
	}

	/**
	 * Creator method for the CSV implementation of the ITableWriter service.
	 *
	 * @param separator
	 *            The separator to use.
	 * @param quote
	 *            the quote character.
	 * @return a new instance of the writer implementation.
	 */
	public static ITableWriter createWriter(final char separator, final char quote) {
		final String quoteStr = Character.toString(separator);
		return new TableWriterCsvImpl(Character.toString(separator), quoteStr, quoteStr + quoteStr, "\n", StandardCharsets.UTF_8);
	}
}
