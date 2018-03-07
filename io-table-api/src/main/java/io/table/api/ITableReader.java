/**
 * ITableReader.java
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
import java.io.InputStream;
import java.util.Date;

/**
 * Interface for the reader class.
 *
 * @author charles
 */
public interface ITableReader extends Closeable {

    /**
     * Initialization method. The goal is to prepare the input stream for the
     * import.
     *
     * @param input
     *            Input stream to wrap.
     * @throws IOException
     *             Any I/O error.
     */
    void initialize(final InputStream input) throws IOException;

    /**
     * Method to change the current row to the next available row.
     * 
     * @return False if there is no more rows in the input.
     */
    boolean nextRow();

    /**
     * Getter method.
     * 
     * @return The amount of column in the current row. If the count is unknown
     *         (E.g. no current row), the method will return -1.
     */
    int getColumnCount();

    /**
     * Getter method.
     * 
     * @param columnId
     *            ID of the column
     * @return Content of the cell defined by the column ID in the current row. The
     *         method will return null if the column does not exists.
     */
    String getCellAsString(final int columnId);

    /**
     * Getter method.
     * 
     * @param columnId
     *            ID of the column
     * @return Content of the cell defined by the column ID in the current row. The
     *         method will return null if the column does not exists.
     */
    Number getCellAsNumber(final int columnId);

    /**
     * Getter method.
     * 
     * @param columnId
     *            ID of the column
     * @return Content of the cell defined by the column ID in the current row. The
     *         method will return null if the column does not exists.
     */
    Date getCellAsDate(final int columnId);
}
