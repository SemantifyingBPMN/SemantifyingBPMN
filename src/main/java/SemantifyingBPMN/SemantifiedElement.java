package SemantifyingBPMN;

import java.util.ArrayList;

import javax.xml.namespace.QName;

public class SemantifiedElement 
{
	private boolean toConsider = false;
	private QName semantified_element = null;
	private ArrayList<Integer> referenced_semantified_elements = new ArrayList<Integer>();
	private String TKElementName = null;


	public String getTKElementName() {
		return TKElementName;
	}

	public void setTKElementName(String tKElementName) {
		TKElementName = tKElementName;
	}

	public boolean isToConsider() {
		return toConsider;
	}

	public void setToConsider(boolean toConsider) {
		this.toConsider = toConsider;
	}
	
	public SemantifiedElement() {

	}	

	public SemantifiedElement(QName element  , String name) {
		semantified_element = element;
		TKElementName = name;
	}

	public SemantifiedElement(QName element  , String name , boolean consider) {
		semantified_element = element;
		TKElementName = name;
		setToConsider(consider);
	}
	
	public QName getSemantified_element() {
		return semantified_element;
	}
	public void setSemantified_element(QName semantified_element) {
		this.semantified_element = semantified_element;
	}
	public ArrayList<Integer> getReferenced_semantified_elements() {
		return referenced_semantified_elements;
	}
	public void setReferenced_semantified_elements(ArrayList<Integer> referenced_semantified_elements) {
		this.referenced_semantified_elements = referenced_semantified_elements;
	}
	
	public void AddReferenced_semantified_element(Integer new_element)
	{
		referenced_semantified_elements.add(new_element);
	}
	
	public Integer GetReferenced_semantified_element(int index)
	{
		return(referenced_semantified_elements.get(index));
	}

	@Override
	public String toString() {
		return "SemantifiedElement [toConsider=" + toConsider + ", semantified_element=" + semantified_element
				+ ", referenced_semantified_elements=" + referenced_semantified_elements + ", TKElementName="
				+ TKElementName + "]\n";
	}
	

	
	

}
