package SemantifyingBPMN;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


public class PatternView {
	
	private String name;
	private String pattern;
	
	// Map tkview types into Classification Transaction Acts
	private HashMap <String,String> MapTKviewClassificationATLAS = new HashMap<String,String>();
	
	public HashMap<String, String> getMapTKviewClassificationATLAS() {
		return MapTKviewClassificationATLAS;
	}
	public void setMapTKviewClassificationATLAS(HashMap<String, String> mapTKviewClassificationATLAS) {
		MapTKviewClassificationATLAS = mapTKviewClassificationATLAS;
	}

	private HashMap <String,String> CustomViewDetail = new HashMap<String,String>();
	
	public HashMap<String, String> getCustomViewDetail() {
		return CustomViewDetail;
	}
	public void setCustomViewDetail(HashMap<String, String> customViewDetail) {
		CustomViewDetail = customViewDetail;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	
	public PatternView() {
		

		MapTKviewClassificationATLAS.put("Request Decision","1_Request_Decision");   
		MapTKviewClassificationATLAS.put("Request","2_Request"); 
		MapTKviewClassificationATLAS.put("Promise Decision","3_Promise_Decision"); 
		MapTKviewClassificationATLAS.put("Promise","4_Promise"); 
		MapTKviewClassificationATLAS.put("Decline","5_Decline"); 
		MapTKviewClassificationATLAS.put("After Decline Decision","6_After_Decline_Decision"); 
		MapTKviewClassificationATLAS.put("Execute","7_Execute"); 
		MapTKviewClassificationATLAS.put("Declare","8_Declare");  				
		MapTKviewClassificationATLAS.put("Decision Accept","9_Decision_Accept"); 
		MapTKviewClassificationATLAS.put("Accept","10_Accept");
		MapTKviewClassificationATLAS.put("Reject","11_Reject"); 
		MapTKviewClassificationATLAS.put("Evaluate Rejection","12_Evaluate_Rejection");
		MapTKviewClassificationATLAS.put("Stop","13_Stop"); 
	
		
		
	}
	
	
	public PatternView(String name, String pattern) {
		this.name = name;
		this.pattern = pattern;
		

		MapTKviewClassificationATLAS.put("Request Decision","1_Request_Decision");   
		MapTKviewClassificationATLAS.put("Request","2_Request"); 
		MapTKviewClassificationATLAS.put("Promise Decision","3_Promise_Decision"); 
		MapTKviewClassificationATLAS.put("Promise","4_Promise"); 
		MapTKviewClassificationATLAS.put("Decline","5_Decline"); 
		MapTKviewClassificationATLAS.put("After Decline Decision","6_After_Decline_Decision"); 
		MapTKviewClassificationATLAS.put("Execute","7_Execute"); 
		MapTKviewClassificationATLAS.put("Declare","8_Declare");  				
		MapTKviewClassificationATLAS.put("Decision Accept","9_Decision_Accept"); 
		MapTKviewClassificationATLAS.put("Accept","10_Accept");
		MapTKviewClassificationATLAS.put("Reject","11_Reject"); 
		MapTKviewClassificationATLAS.put("Evaluate Rejection","12_Evaluate_Rejection");
		MapTKviewClassificationATLAS.put("Stop","13_Stop"); 
	
		
		
	}
	

	public String Translate2SemantifyingBPMN(String TransactionAct)
	{		
		Set<String> keys = MapTKviewClassificationATLAS.keySet();
		Iterator<String> it = keys.iterator();
				
		for (int idx = 0 ; idx < keys.size() ;idx++) 
		{	
			String key = (String) it.next();
			
			if (MapTKviewClassificationATLAS.get(key).compareTo(TransactionAct) == 0) return (key);
		}		
				
		return ("");
	}

	
	public String Translate2Atlas(String TransactionAct)
	{
		return(MapTKviewClassificationATLAS.get(TransactionAct));
	}
	
	@Override
	public String toString() {
		String to_return = new String();
		
		to_return = "PatternView [name=" + name + ", pattern=" + pattern ;
				
		Set<String> keys = CustomViewDetail.keySet();
		Iterator<String> it = keys.iterator();
				
		for (int idx= 0 ; idx < keys.size() ;idx++) 
		{
			String key = (String) it.next();
			to_return += ", " + key +  "=" +
						 CustomViewDetail.get(key);
		}		
		
		to_return += "]";		
		return (to_return);
		
	}
	
	public void addTKStep(String Key, String Value) {
		
		CustomViewDetail.put(Key, Value);
		
	}
	
	public String getTKStepValue(String Key)
	{
		return ( (String) CustomViewDetail.get(Key) );
	}

}
