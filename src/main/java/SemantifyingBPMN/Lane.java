package SemantifyingBPMN;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;

import java.util.UUID;


public class Lane {

	private ArrayList<BPMNElement> BPMNElements = new ArrayList<BPMNElement>();
	private ArrayList<BPMNSequenceFlow> BPMNSequenceFlows = new ArrayList<BPMNSequenceFlow>();
	private ArrayList<BPMNAssociation> BPMNAssociations = new ArrayList<BPMNAssociation>();
	private HashMap<Integer, ArrayList<BPMNElement> > OrganizedBPMNElementByLevel = new HashMap<Integer, ArrayList<BPMNElement> >();
	
	
	private DEMOPatternInitiator initiator = null;
	private DEMOPatternExecutor  executor = null;
	
	private String Name;
	private String Description;
	private QName qname_Lane;
	private QName qname_Shape;

	public  double Width = 1200;
	public  double Height = 200;
	
	public static double Height_per_level = 170; //180
	
	private int n_levels = 0;
	
	private double OffSet_X_Position_of_a_Lane = 25;
	
	
	public double getOffSet_X_Position_of_a_Lane() {
		return OffSet_X_Position_of_a_Lane;
	}


	public void setOffSet_X_Position_of_a_Lane(double OffSet_X_Position_of_a_Lane) {
		this.OffSet_X_Position_of_a_Lane = OffSet_X_Position_of_a_Lane;
	}


	public int getN_levels() {
	//	return n_levels;
		// Now returning the number of provisioned levels
		
		//how many levels?				
		int max_absolute_level = 0;
		int max_level = 0;
		for (BPMNElement elem:getBPMNElements()) if (elem.getLevel() > max_absolute_level) max_absolute_level = elem.getLevel();
		
		for (int  l = 1 ; l <=  max_absolute_level ; l++)
		{
			boolean exists_in_level = false;
			for (BPMNElement elem:getBPMNElements()) 
				if (elem.getLevel() == l) exists_in_level = true;
			
			if (exists_in_level == true) max_level++;
		}
		
		return (max_level);
	}


	public void setN_levels(int n_levels) {		
		this.n_levels = n_levels;
	}

	public void UpdateBPMNSequenceFlowsPositions(Pool pool_t)
	{		
			for (BPMNSequenceFlow flow: getBPMNSequenceFlows() )
			{
				for (BPMNElement elem: getBPMNElements())
				{
					// using sourceQName for position Start in the lane lane
					if (flow.getSourceQName() == elem.getQname_BPMNElement())
					{
						flow.setPos_X_Start( elem.getX() + elem.getWidth() );
						flow.setPos_Y_Start( elem.getY() + elem.getHeight()/2 );
					}
				
					// using targetQName for position End in the same lane
					if (flow.getTargetQName() == elem.getQname_BPMNElement())
					{
						flow.setPos_X_End( elem.getX());
						flow.setPos_Y_End( elem.getY() + elem.getHeight()/2 );
					}
					
					
					for (Lane lane_rt:pool_t.getLanes()) //check if this elem is connected with other lanes of this pool - dependencies require this					
					{					
						for (BPMNElement elem2: lane_rt.getBPMNElements())
						{
								// using sourceQName for position Start in the lane lane
								if (flow.getSourceQName() == elem2.getQname_BPMNElement())
								{
									flow.setPos_X_Start( elem2.getX() + elem2.getWidth() );
									flow.setPos_Y_Start( elem2.getY() + elem2.getHeight()/2 );
								}
							
								// using targetQName for position End in the same lane
								if (flow.getTargetQName() == elem2.getQname_BPMNElement())
								{
									flow.setPos_X_End( elem2.getX());
									flow.setPos_Y_End( elem2.getY() + elem2.getHeight()/2 );
								}
							
						}	
					} // end of check with other lanes - special case of dependencies		
				}
			}
	}
	
	
	public void StoreObject4FlowRenderingInPool(Object object_current, BPMNElement element_current , Pool pool_toSearch) 
	{
			// search Object incomings and define it as targetRef of a flow
			for (QName flow_id_to_update:element_current.getQname_flow_Incoming() ) 
			{
				for (Lane lane_t:pool_toSearch.getLanes())
				{
					for (BPMNSequenceFlow flow_id: lane_t.getBPMNSequenceFlows() )
						if ( flow_id_to_update == flow_id.getQName_Flowid()) 
						{
							flow_id.setTargetRef(object_current);		
						}
				}
			}
			
			// search Object outgoings and define it as sourceRef of a flow
			for (QName flow_id_to_update:element_current.getQname_flow_Outgoing() ) 
			{
				for (Lane lane_t:pool_toSearch.getLanes())
				{
					for (BPMNSequenceFlow flow_id: lane_t.getBPMNSequenceFlows() )
						if ( flow_id_to_update == flow_id.getQName_Flowid()) 
						{
							flow_id.setSourceRef(object_current);		
						}
				}
			}
	}


	public void OrganizeBPMNElementsByLevel()
	{
		
		
		int max_absolute_level = 0;
		
		for (BPMNElement elem:getBPMNElements()) if (elem.getLevel() > max_absolute_level) max_absolute_level = elem.getLevel();

	
		// compact top levels 
			// for each level
			for (int level_to_check = 1 ; level_to_check < max_absolute_level ; level_to_check++)
			{
				boolean is_there_any_element = false;
				for (BPMNElement element:BPMNElements)	
					if (element.getLevel() == level_to_check) is_there_any_element = true;
					
			
					
				// if not then compact
				if ( is_there_any_element == false)
				{
					boolean anythingdone = false;
					
					for (BPMNElement element:BPMNElements)	
						if (element.getLevel() > level_to_check ) 
						{
							element.DecrementLevel();
							anythingdone = true;	
						}

					if (anythingdone) level_to_check--;
					
				}
			}
		

		
		for (BPMNElement element:BPMNElements)
		{
			if ( OrganizedBPMNElementByLevel.containsKey(element.getLevel()) == true)
			{
				OrganizedBPMNElementByLevel.get(element.getLevel()).add(element);

			}
			else
			{
				ArrayList<BPMNElement> new_element = new ArrayList<BPMNElement>();
				new_element.add(element);
				OrganizedBPMNElementByLevel.put(element.getLevel() , new_element );
			}
			
			if (max_absolute_level < element.getLevel()) max_absolute_level = element.getLevel();
			
		}

		
		
		// set number of level of this lane
		//setN_levels(OrganizedBPMNElementByLevel.size());
			// by level maximum due to optional provision of elements
		setN_levels(max_absolute_level);
		
	}
	
	
		
	public HashMap<Integer, ArrayList<BPMNElement>> getOrganizedBPMNElementByLevel() {
		return OrganizedBPMNElementByLevel;
	}



	public void setOrganizedBPMNElementByLevel(HashMap<Integer, ArrayList<BPMNElement>> organizedBPMNElementByLevel) {
		OrganizedBPMNElementByLevel = organizedBPMNElementByLevel;
	}



	public QName getQname_Shape() {
		return qname_Shape;
	}
	private void setQname_Shape(QName qname_Shape) {
		this.qname_Shape = qname_Shape;
	}
	public QName getQname_Lane() {
		return qname_Lane;
	}
	private void setQname_Lane(QName qname_Lane) {
		this.qname_Lane = qname_Lane;
	}

	
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getDescription() {
		return Description;
	}
	public void setDescription(String description) {
		Description = description;
	}

	private void Init()
	{
		qname_Lane = new QName("Lane_" +  UUID.randomUUID().toString());
		qname_Shape = new QName(qname_Lane.toString() + "_di");
	}

	public Lane(String name, String description) {
		super();
		Init();
		Name = name;
		Description = description;
	}
	public Lane() {
		super();
		Init();

	}

	
	@Override
	public String toString() {
		return "Lane [Name=" + Name + ", Description=" + Description +  ", getName()="
				+ getName() + ", getDescription()=" + getDescription()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
				+ "]";
	}
	

	
	public QName addElementWithShift(BPMNElement bpmn_e , double percentage)
	{
		
		bpmn_e.setOffset(percentage);
		return (addElement(bpmn_e));
	}
	
	public QName addElement(BPMNElement bpmn_e)
	{
		BPMNElements.add(bpmn_e);
		return(bpmn_e.getQname_BPMNElement());
	}
	
	
	public QName addSequenceFlow(BPMNSequenceFlow bpmn_seq)
	{
		BPMNSequenceFlows.add(bpmn_seq);
		return(bpmn_seq.getQName_Flowid());
	}

	
	public QName addAssociation(BPMNAssociation bpmn_association)
	{
		BPMNAssociations.add(bpmn_association);
		return(bpmn_association.getQName_associationid());
	}

	
	public ArrayList<BPMNElement> getBPMNElements() {
		return BPMNElements;
	}
	public void setBPMNElements(ArrayList<BPMNElement> bPMNElements) {
		BPMNElements = bPMNElements;
	}
	public ArrayList<BPMNSequenceFlow> getBPMNSequenceFlows() {
		return BPMNSequenceFlows;
	}
	public void setBPMNSequenceFlows(ArrayList<BPMNSequenceFlow> bPMNSequenceFlows) {
		BPMNSequenceFlows = bPMNSequenceFlows;
	}
	public DEMOPatternInitiator getInitiator() {
		return initiator;
	}
	public void setInitiator(DEMOPatternInitiator initiator) {
		this.initiator = initiator;
	}
	public DEMOPatternExecutor getExecutor() {
		return executor;
	}
	public void setExecutor(DEMOPatternExecutor executor) {
		this.executor = executor;
	}
	public double getWidth() {
		return Width;
	}
	public void setWidth(double width) {
		Width = width;
	}
	public double getHeight() {
		return Height;
	}
	public void setHeight(double height) {
		Height = height;
	}


	public ArrayList<BPMNAssociation> getBPMNAssociations() {
		return BPMNAssociations;
	}


	public void setBPMNAssociations(ArrayList<BPMNAssociation> bPMNAssociations) {
		BPMNAssociations = bPMNAssociations;
	}


	public void removeElement(QName semantified_element) {

		BPMNElement Element2Remove = null;
	    
		for (BPMNElement element:BPMNElements)
		{
			if (element.getQname_BPMNElement() == semantified_element) Element2Remove = element;
		}
		
		if (Element2Remove != null) BPMNElements.remove(Element2Remove);
		
	}


	public QName SearchQNameByString(String name_to_find) {

		for (BPMNElement element_to_verify: BPMNElements)
		{
			if (element_to_verify.getDescription().compareTo(name_to_find) == 0 )
				return (element_to_verify.getQname_BPMNElement());
		}
		
		
		return null;
	}
	
	
	
	
	
}
