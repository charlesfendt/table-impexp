/**
 * TableWriterCsvImpl.java
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

import java.io.IOException;
import java.io.OutputStream;

/**
 * The Writer implementation.
 *
 * @author charles
 *
 */
public final class TableWriterCsvImpl implements ITableWriter {

	/** The separator between value. */
	private final String separator;
	/** The quote string. */
	private final String quote;
	/** the escape sequence for the quote. */
	private final String escape;

	/** The stream to wrap. */
	private OutputStream output;

	/**
	 * Default constructor.
	 *
	 * @param separator
	 *            The separator between value.
	 * @param quote
	 *            The quote string.
	 * @param escape
	 *            the escape sequence for the quote.
	 * @param newLine
	 *            The newline separator.
	 */
	protected TableWriterCsvImpl(final String separator, final String quote, final String escape, final String newLine) {
		super();

		this.separator = separator;
		this.quote = quote;
		this.escape = escape;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() throws IOException {
		if (this.output != null) {
			this.output.close();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see io.table.api.ITableWriter#initialize(java.io.OutputStream)
	 */
	@Override
	public void initialize(final OutputStream output) throws IOException {
		
		if (this.output == null) {
			throw new IllegalArgumentException("The output stream must not be null.");
		}

		if (this.output == null) {
			throw new IllegalStateException("The stream is already initialized.");
		}
		this.output = output;
		
	}

}
