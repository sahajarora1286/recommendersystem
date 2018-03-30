package edu.carleton.comp4601.utilities;

import java.util.HashMap;
import java.util.Map;

public class Sentiments {

	private Map<String, String> positive;
	private Map<String, String> negative;
	private Map<String, String> neutral;
	
	public Sentiments() {
		positive = new HashMap<String, String>();
		positive.put("A2M1N8G4W4END8", "B00005B238");
		positive.put("A25ZVI6RH1KA5L", "B00005B238");
		positive.put("AFLQGO7CJVK50", "B00005B238");
		positive.put("A3K5IMGDCDBCNF", "B00005B238");
		positive.put("A1YC9XEDFKXPLK", "B000CCW2RU");
		positive.put("A30TK6U7DNS82R", "B000CCW2RU");
		negative = new HashMap<String, String>();
		negative.put("A2ATWKOFJXRRR1", "B000CCW2RU");
		negative.put("A18IK6YI6T3RK2", "B001H20HVM");
		negative.put("A3QH6BEY6RYQR0", "B004UXUX4Q");
		negative.put("AXOS8IWBXNZGT", "B000CD9U4M");
		negative.put("A3JLOIXFM75QNV", "B002KGREJW");
		negative.put("A141HP4LYPWMSR", "B000K2UVZM");
		neutral = new HashMap<String, String>();
		neutral.put("A1IUI3CJUMB7J0", "B000CCW2RU");
		neutral.put("A1JH5J1KQAUBMP", "B0083SJFZ2");		
	}

	public Map<String, String> getPositive() {
		return positive;
	}

	public Map<String, String> getNegative() {
		return negative;
	}

	public Map<String, String> getNeutral() {
		return neutral;
	}

}
