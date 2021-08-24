package lk.ijse.dep7;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SplitTest {

    @Test
    public void howSplitWorks(){
        String query = "";
        String[] split = query.split("\\s");
        System.out.println(split[0].equals(""));
        Assertions.assertTrue(split.length ==1);
    }
}
