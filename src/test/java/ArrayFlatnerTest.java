import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ArrayFlatnerTest {
    private static final Object[]  arrays = new Object[]{new Object[]{1,2,new Object[]{3}},4,"a"}; //[[1,2,[3]],4]

    @Test
    void flattenerTest() {
        Assertions.assertArrayEquals(new Object[]{1, 2, 3, 4}, ArrayFlatner.flattener(arrays).toArray());
    }

}
