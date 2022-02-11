import org.junit.*;

public class ArrayUtilTest3170045 {

    private int[] testArray;

    @Before
    public void SetUp(){
        testArray = new int[]{1,2,3,4,5,6,7,8,9,10};
    }

    @Test
    public void testStartFromPositiveIndex(){
        ArrayUtil util = new ArrayUtil();
        int[] reversedExpected = {1,10,9,8,7,6,5,4,3,2};
        boolean returned = ArrayUtil.reverse(testArray,1);
        Assert.assertTrue(returned);
        Assert.assertArrayEquals("Όλα τα στοιχεία της συστοιχίας εκτός του πρώτου πρέπει να έχουν αναστραφεί", reversedExpected, testArray);
    }

    @Test
    public void testNullArray(){
        boolean returned = ArrayUtil.reverse(null,1);
        Assert.assertFalse(returned);
    }

    @Test
    public void testStartOutOfBound(){
        boolean returned = ArrayUtil.reverse(testArray, testArray.length);
        Assert.assertFalse(returned);
    }

    @Test
    public void testZeroStart(){
        int[] reversedExpected = {10,9,8,7,6,5,4,3,2,1};
        boolean returned = ArrayUtil.reverse(testArray, 0);
        Assert.assertTrue(returned);
        Assert.assertArrayEquals("Όλη η συστοιχία πρέπει να έχει αναστραφεί", reversedExpected, testArray);
    }
}
