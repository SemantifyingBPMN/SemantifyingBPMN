package SemantifyingBPMN;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.namespace.QName;

import java.util.UUID;

public class Pool {
	
	private ActorRole 	Name;
	private ArrayList<Lane> Lanes = new ArrayList<Lane>();

	private QName qname_Participant;
	private QName qname_Process;
	private QName qname_LaneSet;
	private QName qname_Shape;
	private QName qname_StartEvent;
	private QName qname_EndEvent;
	private QName qname_ShapeStartEvent;
	private QName qname_ShapeEndEvent;
	


	
	public double Width = 1200;
	public double Height = 200;
	
	
	
	
	public void SpecifyDimensions()
	{
			// Calculate Height of Lanes and pool
			double height_now = 0;			
			for (Lane lane:Lanes)
			{
				// compute max level of Lane
				int max_level = 0;
				for (BPMNElement elem:lane.getBPMNElements()) if (elem.getLevel() > max_level) max_level = elem.getLevel();				
				lane.setHeight(Lane.Height_per_level * max_level);										
				height_now += lane.getHeight(); //for pool height dimension
			}
			setHeight(height_now);
			
			// Calculate Width of Lanes and pool
			double width_now = 0;
			int max_elements_level_pool = 0;
			// compute max number of BPMNElements / level 
			for (Lane lane:Lanes)
			{
				// which is the level with more elements?
				HashMap<Integer,Integer> elements_per_level = new HashMap<Integer,Integer>();
				for (BPMNElement elem:lane.getBPMNElements())
				{
					Integer level = elem.getLevel();					
					if (elements_per_level.containsKey(level) == true)	elements_per_level.put( level , elements_per_level.get(level) + 1 );
					else elements_per_level.put( level , 1 );	
				}
				
				int max_elements_level_lane = 0;
				for (HashMap.Entry<Integer, Integer> entry : elements_per_level.entrySet()) 
				{
					if (entry.getValue() > max_elements_level_lane) max_elements_level_lane = entry.getValue(); 
				}
				//lane.setWidth( (max_elements_level_lane + 1) * (Activity.Width + (Activity.distance_between_activities - Activity.Width)) );
				lane.setWidth( (max_elements_level_lane + 5) * (Activity.Width + (Activity.distance_between_activities - Activity.Width)) );
				if ( max_elements_level_lane > max_elements_level_pool) max_elements_level_pool = max_elements_level_lane; 
			}
			// resize pool			
			setWidth( (max_elements_level_pool + 5) * (Activity.Width + (Activity.distance_between_activities - Activity.Width)) );
		
			
			for (Lane lane:Lanes) System.out.println("Lane size, width= " + lane.getWidth() + ", Height = " + lane.getHeight());
			System.out.println("Pool size, width= " + getWidth() + ", Height = " + getHeight());
	}
	
	
	
	
	
	
	
	
	
	
	public QName getQname_EndEvent() {
		return qname_EndEvent;
	}

	private void setQname_EndEvent(QName qname_EndEvent) {
		this.qname_EndEvent = qname_EndEvent;
	}

	public QName getQname_ShapeStartEvent() {
		return qname_ShapeStartEvent;
	}

	private void setQname_ShapeStartEvent(QName qname_ShapeStartEvent) {
		this.qname_ShapeStartEvent = qname_ShapeStartEvent;
	}

	public QName getQname_ShapeEndEvent() {
		return qname_ShapeEndEvent;
	}

	private void setQname_ShapeEndEvent(QName qname_ShapeEndEvent) {
		this.qname_ShapeEndEvent = qname_ShapeEndEvent;
	}

	
	
	
	public QName getQname_StartEvent() {
		return qname_StartEvent;
	}

	private void setQname_StartEvent(QName qname_StartEvent) {
		this.qname_StartEvent = qname_StartEvent;
	}


	
	
/*	public double sizePool()
	{
		return(new Lane().getHeight() * getLanes().size());
	}*/
	
	public QName getQname_Shape() {
		return qname_Shape;
	}

	private void setQname_Shape(QName qname_Shape) {
		this.qname_Shape = qname_Shape;
	}

	public QName getQname_LaneSet() {
		return qname_LaneSet;
	}

	private void setQname_LaneSet(QName qname_LaneSet) {
		this.qname_LaneSet = qname_LaneSet;
	}

	public QName getQname_Process() {
		return qname_Process;
	}

	private void setQname_Process(QName qname_Process) {
		this.qname_Process = qname_Process;
	}

	public String getShortName() {
		return Name.getName();
	}
	
	public ActorRole getName() {
		return Name;
	}

	public void setName(ActorRole name) {
		Name = name;
	}

	private void Init()
	{		
		qname_Participant = new QName("Participant_" +  UUID.randomUUID().toString());
		qname_Process = new QName("Process_" +  UUID.randomUUID().toString());
		qname_LaneSet = new QName("LaneSet_" +  UUID.randomUUID().toString());
		qname_Shape = new QName(qname_Participant.toString() + "_di");
		qname_StartEvent = new QName("Event_" +  UUID.randomUUID().toString());
		qname_EndEvent = new QName("Event_" +  UUID.randomUUID().toString());
		qname_ShapeStartEvent  = new QName(qname_StartEvent.toString() + "_di");
		qname_ShapeEndEvent  = new QName(qname_EndEvent.toString() + "_di");
		
		
	}

	public QName getQname_Participant() {
		return qname_Participant;
	}

	private void setQname_Participant(QName qname_Participant) {
		this.qname_Participant = qname_Participant;
	}

	@Override
	public String toString() {
		return "Pool [Name=" + Name + ", Lanes=" + Lanes + ", getName()=" + getName() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}

	
	public Pool(ActorRole name, ArrayList<Lane> lanes) {
		super();
		Init();
		
		Name = name;
		Lanes = lanes;
	}
	
	
	public Pool(ActorRole name) {
		super();
		Init();

		Name = name;
	}

	public ArrayList<Lane> getLanes() {
		return Lanes;
	}

	public void setLanes(ArrayList<Lane> lanes) {
		Lanes = lanes;
	}

	
	public void AddLane(Lane lane) {
			Lanes.add(lane);
		
	}
	
	public Pool() {
			super();
			Init();

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
	

}
