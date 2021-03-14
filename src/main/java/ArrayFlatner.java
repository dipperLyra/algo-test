/**
 * @author Eche Mkpadi
 * Algorithm test.
 * Question: Write some code, that will flatten an array of arbitrarily nested arrays of
 * integers into a flat array of integers. e.g. `[[1,2,[3]],4] -> [1,2,3,4]`.
 */


import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ArrayFlatner {
    private static final List<Object> nonIntegers = new ArrayList<>();

    public static List<Object> getNonIntegers() {
        return nonIntegers;
    }



    /**
     * <p>This functional approach, Streams, was chosen for its conciseness and readability over an iterative approach.</p><br/>
     * <p>
     *     It takes an array of arbitrarily nested arrays.
     *     For each element in the main, it checks if the value is an array.
     *     It does this by recursively calling itself until a base case, an element that is not an array, is reached.
     *
     *     The function also filters off all non-integer elements, but before that it stores the non-integer elements in the class property,
     *     nonIntegers. Just in case use wants to access the elements filtered off.
     * </p>
     * @param arr Arbitrarily nested array, <br> new Object[]{new Object[]{1,2,new Object[]{3}},4,"a"}
     * @return Flat array of integers - [1,2,3,4]
     */
    public static Stream<Object> flattener(Object[] arr) {
        return Arrays.stream(arr)
                .flatMap(object -> object instanceof Object[] ? flattener((Object[])object) : Stream.of(object))
                .peek(obj -> { if (!(obj instanceof Integer)) nonIntegers.add(obj); })
                .filter(o -> o instanceof Integer);
    }
}