package lk.ijse.dep7;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.IntStream;

public class ReducerTest {

    @Test
    public void howReducersWork(){
        int[] numbers = {1,2,3,4,5};
        IntStream numStream = Arrays.stream(numbers);

        int result = numStream.reduce((accumulator, element) -> accumulator += element).getAsInt();
        System.out.println(result);
    }

}
