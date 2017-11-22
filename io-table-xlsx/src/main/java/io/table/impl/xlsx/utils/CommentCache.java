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
     * @param ref
     *            The reference for the comment (E.g. A1).
     * @param comment
     *            The comment to add.
     */
    public void addComment(final String ref, final String comment) {
        this.comments.add(new Comment(ref, comment));
    }

    /**
     * Write method for the content.
     *
     * @param output
     *            Output to append.
     * @throws IOException
     *             Any I/O error
     */
    public void write(final OutputStream output) throws IOException {
        output.write(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" //$NON-NLS-1$
                + "<comments xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\"><authors><author/></authors>" //$NON-NLS-1$
                + "<commentList>").getBytes(StandardCharsets.UTF_8)); //$NON-NLS-1$
        for (final Comment comment : this.comments) {
            output.write(("<comment ref=\"" + comment.ref + "\" authorId=\"0\"><text><t>" //$NON-NLS-1$ //$NON-NLS-2$
                    + StringEscapeUtils.escapeString(comment.comment) + "</t></text></comment>") //$NON-NLS-1$
                            .getBytes(StandardCharsets.UTF_8));
        }
        output.write("</commentList></comments>".getBytes(StandardCharsets.UTF_8)); //$NON-NLS-1$
    }

    /**
     * Class for comment definition.
     *
     * @author fendtc
     */
    private class Comment {
        /** The reference in the excel table (E.g. A1). */
        private final String ref;
        /** The String of the comment. */
        private final String comment;

        /**
         * Default constructor.
         * 
         * @param ref
         *            The reference in the excel table (E.g. A1).
         * @param comment
         *            The String of the comment.
         */
        private Comment(final String ref, final String comment) {
            super();
            this.ref = ref;
            this.comment = comment;
        }
    }
}
