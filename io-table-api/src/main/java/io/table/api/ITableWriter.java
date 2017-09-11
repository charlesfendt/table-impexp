/**
 * ITableWriter.java
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
package io.table.api;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

/**
 * Interface for the writer class.
 *
 * @author charles
 */
public interface ITableWriter extends Closeable {
	
	/**
	 * Initialization method. The goal is to prepare the output stream for the export.
	 *
	 * @param output
	 *            Output stream to wrap.
	 * @throws IOException
	 *             Any I/O error.
	 */
	void initialize(final OutputStream output) throws IOException;

	/**
	 * Append method.
	 *
	 * @param value
	 *            The value to append.
	 * @throws IOException
	 *             Any I/O error.
	 */
	void appendCell(String value) throws IOException;
	
	/**
	 * Append method.
	 *
	 * @param value
	 *            The value to append.
	 * @throws IOException
	 *             Any I/O error.
	 */
	void appendCell(long value) throws IOException;

	/**
	 * Append method.
	 *
	 * @param value
	 *            The value to append.
	 * @throws IOException
	 *             Any I/O error.
	 */
	void appendCell(double value) throws IOException;
	
	/**
	 * Append method.
	 *
	 * @param value
	 *            The value to append.
	 * @throws IOException
	 *             Any I/O error.
	 */
	void appendCell(Date value) throws IOException;
	
	/**
	 * Append method.
	 *
	 * @throws IOException
	 *             Any I/O error.
	 */
	void appendNewLine() throws IOException;

	/**
	 * Append method.
	 *
	 * @param cells
	 *            The list of value for the new line.
	 * @throws IOException
	 *             Any I/O error.
	 */
	void appendNewLine(final String... cells) throws IOException;
}
