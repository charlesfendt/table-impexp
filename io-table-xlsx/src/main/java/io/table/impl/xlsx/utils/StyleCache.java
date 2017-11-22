/**
 *
 */
package io.table.impl.xlsx.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Class to manage styles.
 *
 * @author fendtc
 */
public final class StyleCache {

    /** Default style for the EXCEL file. */
    private static final String STYLES = //
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" //$NON-NLS-1$
                    + "<styleSheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">" //$NON-NLS-1$
                    + "<numFmts count=\"1\">" //$NON-NLS-1$
                    + "<numFmt numFmtId=\"164\" formatCode=\"yyyy-MM-dd HH:mm:ss\" />" //$NON-NLS-1$
                    + "</numFmts>" //$NON-NLS-1$
                    + "<fonts count=\"2\">" //$NON-NLS-1$
                    + "<font>" //$NON-NLS-1$
                    + "<name val=\"Calibri\" />" //$NON-NLS-1$
                    + "<sz val=\"11.0\" />" //$NON-NLS-1$
                    + "</font>" //$NON-NLS-1$
                    + "<font>" //$NON-NLS-1$
                    + "<name val=\"Calibri\" />" //$NON-NLS-1$
                    + "<sz val=\"11.0\" />" //$NON-NLS-1$
                    + "<b val=\"true\" />" //$NON-NLS-1$
                    + "</font>" //$NON-NLS-1$
                    + "</fonts>" //$NON-NLS-1$
                    + "<fills count=\"1\">" //$NON-NLS-1$
                    + "<fill>" //$NON-NLS-1$
                    + "<patternFill patternType=\"none\" />" //$NON-NLS-1$
                    + "</fill>" //$NON-NLS-1$
                    + "</fills>" //$NON-NLS-1$
                    + "<borders count=\"1\">" //$NON-NLS-1$
                    + "<border>" //$NON-NLS-1$
                    + "<left />" //$NON-NLS-1$
                    + "<right />" //$NON-NLS-1$
                    + "<top />" //$NON-NLS-1$
                    + "<bottom />" //$NON-NLS-1$
                    + "<diagonal />" //$NON-NLS-1$
                    + "</border>" //$NON-NLS-1$
                    + "</borders>" //$NON-NLS-1$
                    + "<cellStyleXfs count=\"1\">" //$NON-NLS-1$
                    + "<xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\" />" //$NON-NLS-1$
                    + "</cellStyleXfs>" //$NON-NLS-1$
                    + "<cellXfs count=\"3\">" //$NON-NLS-1$
                    + "<xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\" xfId=\"0\" />" //$NON-NLS-1$
                    + "<xf numFmtId=\"0\" fontId=\"1\" fillId=\"0\" borderId=\"0\" xfId=\"0\" applyFont=\"true\" />" //$NON-NLS-1$
                    + "<xf numFmtId=\"164\" fontId=\"0\" fillId=\"0\" borderId=\"0\" xfId=\"0\" applyNumberFormat=\"true\" />" //$NON-NLS-1$
                    + "</cellXfs>" //$NON-NLS-1$
                    + "</styleSheet>"; //$NON-NLS-1$

    /**
     * Method to write the content of the style cache.
     * 
     * @param output
     *            the outputstream where to write.
     * @throws IOException
     *             Any I/O error.
     */
    public void writeStyleContent(final OutputStream output) throws IOException {
        output.write(StyleCache.STYLES.getBytes(StandardCharsets.UTF_8));
    }
}
