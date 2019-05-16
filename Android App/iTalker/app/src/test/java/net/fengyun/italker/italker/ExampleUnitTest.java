package net.fengyun.italker.italker;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }


    @Test
    public void main(){
        String[][] sta = new String[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j <10 ; j++) {
                sta[i][j]= String.valueOf(i) + ","+  String.valueOf(j);
                System.out.println( "坐标点" +sta[i][j]);
            }
        }
    }
}