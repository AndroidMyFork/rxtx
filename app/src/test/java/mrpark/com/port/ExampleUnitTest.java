package mrpark.com.port;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        String s="sdasd;asdasd,asdasdsad";
        String[] res=s.split(";|\\,");
        for (String ss:res){
            System.out.print(ss+"\n");
        }
        assertEquals(4, 2 + 2);
    }
}