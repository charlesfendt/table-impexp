/**
 *
 */
package io.table.impl.xlsx.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

/**
 * Support for comments.
 *
 * @author fendtc
 */
public final class CommentCache {

    /** List of comments. */
    private final List<Comment> comments = new LinkedList<>();

    /**
     * Method to add a comment.
     *
     * @param colId
     *            The ID of the column.
     * @param rowId
     *            The row ID of the column.
     * @param ref
     *            The reference for the comment (E.g. A1).
     * @param comment
     *            The comment to add.
     */
    public void addComment(final int colId, final int rowId, final String ref, final String comment) {
        this.comments.add(new Comment(colId, rowId, ref, comment));
    }

    /**
     * Write method for the content.
     *
     * @param output
     *            Output to append.
     * @param appName
     *            The name of the application
     * @throws IOException
     *             Any I/O error
     */
    public void write(final OutputStream output, final String appName) throws IOException {
        output.write(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" //$NON-NLS-1$
                + "<comments xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\"><authors><author/><author>" //$NON-NLS-1$
                + StringEscapeUtils.escapeString(appName) + "</author></authors>" + "<commentList>") //$NON-NLS-2$
                        .getBytes(StandardCharsets.UTF_8));
        for (final Comment comment : this.comments) {
            output.write(("<comment ref=\"" + comment.ref + "\" authorId=\"1\"><text><t>" //$NON-NLS-1$ //$NON-NLS-2$
                    + StringEscapeUtils.escapeString(comment.comment) + "</t></text></comment>") //$NON-NLS-1$
                            .getBytes(StandardCharsets.UTF_8));
        }
        output.write("</commentList></comments>".getBytes(StandardCharsets.UTF_8)); //$NON-NLS-1$
    }

    /**
     * Write method for the content.
     *
     * @param output
     *            Output to append.
     * @throws IOException
     *             Any I/O error
     */
    public void writeVmlDrawing(final OutputStream output) throws IOException {
        output.write(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" //$NON-NLS-1$
                + "<xml xmlns:o=\"urn:schemas-microsoft-com:office:office\" xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:x=\"urn:schemas-microsoft-com:office:excel\">" //$NON-NLS-1$
                + "<o:shapelayout v:ext=\"edit\"><o:idmap v:ext=\"edit\" data=\"1\"/></o:shapelayout>" //$NON-NLS-1$
                + "<v:shapetype id=\"_x0000_t202\" coordsize=\"21600,21600\" o:spt=\"202.0\" path=\"m,l,21600r21600,l21600,xe\">" //$NON-NLS-1$
                + "<v:stroke joinstyle=\"miter\"/><v:path gradientshapeok=\"t\" o:connecttype=\"rect\"/>" //$NON-NLS-1$
                + "</v:shapetype>") //$NON-NLS-1$
                        .getBytes(StandardCharsets.UTF_8));
        for (final Comment comment : this.comments) {
            final int row = comment.rowId;
            final int col = comment.colId;
            output.write(("<v:shape id=\"_comment_" + comment.ref //$NON-NLS-1$
                    + "\" type=\"#_x0000_t202\" style=\"position:absolute; visibility:hidden\" fillcolor=\"#ffffe1\" o:insetmode=\"auto\">" //$NON-NLS-1$
                    + "<v:fill color=\"#ffffe1\"/>" //$NON-NLS-1$
                    + "<v:shadow on=\"t\" color=\"black\" obscured=\"t\"/>" //$NON-NLS-1$
                    + "<v:path o:connecttype=\"none\"/>" //$NON-NLS-1$
                    + "<v:textbox style=\"mso-direction-alt:auto\"/>" //$NON-NLS-1$
                    + "<x:ClientData ObjectType=\"Note\">" //$NON-NLS-1$
                    + "<x:MoveWithCells/>" //$NON-NLS-1$
                    + "<x:SizeWithCells/>" //$NON-NLS-1$
                    + "<x:Anchor>" + col + ", " + row + ", 0, 0, " + (col + 3) + ", 0, " + (row + 1) + ", 0</x:Anchor>" //$NON-NLS-1$
                    + "<x:AutoFill>False</x:AutoFill>" //$NON-NLS-1$
                    + "<x:Row>" + row + "</x:Row>" //$NON-NLS-1$ //$NON-NLS-2$
                    + "<x:Column>" + col + "</x:Column>" //$NON-NLS-1$ //$NON-NLS-2$
                    + "</x:ClientData>" //$NON-NLS-1$
                    + "</v:shape>") //$NON-NLS-1$
                            .getBytes(StandardCharsets.UTF_8));
        }
        output.write("</xml>".getBytes(StandardCharsets.UTF_8)); //$NON-NLS-1$
    }

    /**
     * Class for comment definition.
     *
     * @author fendtc
     */
    private class Comment {
        /** The ID of the column. */
        private final int colId;
        /** The ID of the row. */
        private final int rowId;
        /** The reference in the excel table (E.g. A1). */
        private final String ref;
        /** The String of the comment. */
        private final String comment;

        /**
         * Default constructor.
         *
         * @param colId
         *            The ID of the column.
         * @param rowId
         *            The ID of the row.
         * @param ref
         *            The reference in the excel table (E.g. A1).
         * @param comment
         *            The String of the comment.
         */
        private Comment(final int colId, final int rowId, final String ref, final String comment) {
            super();
            this.colId = colId;
            this.rowId = rowId;
            this.ref = ref;
            this.comment = comment;
        }
    }
}
