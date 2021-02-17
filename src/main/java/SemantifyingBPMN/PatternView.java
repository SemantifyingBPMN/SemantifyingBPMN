package SemantifyingBPMN;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


public class PatternView {
	
	private String name;
	private String pattern;
	
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
	public PatternView(String name, String pattern) {
		this.name = name;
		this.pattern = pattern;
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
