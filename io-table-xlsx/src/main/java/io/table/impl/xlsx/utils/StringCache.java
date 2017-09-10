/**
 * StringCache.java
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
package io.table.impl.xlsx.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * String cache for XLSX output.
 *
 * @author charles
 */
public final class StringCache {

	private int cacheSize;
	private final Map<String, Integer> cacheMap = new HashMap<>();
	private final List<String> cacheLst = new LinkedList<>();
	
	/**
	 * Method to add (if necessary) the given string to the cache.
	 *
	 * @param str
	 *            String to add.
	 * @return The index of the string in the insert order.
	 */
	public int addToCache(final String str) {
		synchronized (this.cacheMap) {
			final int result;
			final Integer cur = this.cacheMap.get(str);
			if (cur == null) {
				result = ++this.cacheSize;
				this.cacheLst.add(str);
				this.cacheMap.put(str, result);
			} else {
				result = cur.intValue();
			}
			return result;
		}
	}
	
	/**
	 * Write this cache as an XML file.
	 *
	 * @param output
	 *            Write where to dump the cache.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public void write(final OutputStream output) throws IOException {
		synchronized (this.cacheMap) {
			final String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><sst xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" count=\""
					+ this.cacheSize + "\" uniqueCount=\"" + this.cacheSize + "\">";
			output.write(header.getBytes(StandardCharsets.UTF_8));
			for (final String str : this.cacheLst) {
				final String data = "<si><t>" + str + "</t></si>";
				output.write(data.getBytes(StandardCharsets.UTF_8));
			}
			output.write("</sst>".getBytes(StandardCharsets.UTF_8));
		}
	}

}
