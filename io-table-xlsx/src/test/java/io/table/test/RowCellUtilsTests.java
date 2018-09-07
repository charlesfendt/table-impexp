package io.table.test;

import java.util.Calendar;
import java.util.Date;

import io.table.impl.xlsx.utils.RowCellUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class.
 *
 * @author smarty
 *
 */
public class RowCellUtilsTests {

    /** Test method. */
    @Test
    public void test() {
        Assert.assertEquals(27, RowCellUtils.stringToColIndex("AA1"));
        Assert.assertEquals(28, RowCellUtils.stringToColIndex("AB1"));
        Assert.assertEquals(54, RowCellUtils.stringToColIndex("BB1111"));

        for (int i = 1; i < 100_000; i++) {
            final String colIndexToString = RowCellUtils.colIndexToString(i);
            Assert.assertEquals(i, RowCellUtils.stringToColIndex(colIndexToString));
        }
    }

    /** Test method. */
    @Test
    public void testDate() {
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        final Date dateVal = cal.getTime();
        final double days = RowCellUtils.getDays(dateVal);
        final Date dateFromDays = RowCellUtils.getDateFromDays(days);
        Assert.assertEquals(dateFromDays.getTime(), dateVal.getTime());

    }

}
