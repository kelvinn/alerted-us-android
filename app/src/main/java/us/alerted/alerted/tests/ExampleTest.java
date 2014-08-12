package us.alerted.alerted.tests;

import android.test.InstrumentationTestCase;

/**
 * Created by kelvinn on 12/08/2014.
 */
public class ExampleTest extends InstrumentationTestCase {
    public void test() throws Exception {
        final int expected = 1;
        final int reality = 5;
        assertEquals(expected, reality);
    }
}