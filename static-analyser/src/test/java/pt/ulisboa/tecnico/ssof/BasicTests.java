package pt.ulisboa.tecnico.ssof;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

public class BasicTests {
	
	@SuppressWarnings("resource")
	@Test
	public void test01() throws IOException, JSONException {
		InputStream is1 = this.getClass().getResourceAsStream("/public_basic_tests/01_gets_all.output.json");
		java.util.Scanner s1 = new Scanner(is1).useDelimiter("\\A");
	    String jsonData1 = s1.hasNext() ? s1.next() : "";
	    
	    String[] testString = new String[1];
	    testString[0] = "/public_basic_tests/01_gets_all.json";
	    
	    StaticAnalyser.main(testString);
	    
	    /*
		InputStream is2 = this.getClass().getResourceAsStream("/01_gets_all.result.json");
		java.util.Scanner s2 = new Scanner(is2).useDelimiter("\\A");
	    String jsonData2 = s2.hasNext() ? s2.next() : "";

	    JSONAssert.assertEquals(jsonData1, jsonData2, JSONCompareMode.NON_EXTENSIBLE);
	    */
	}
}