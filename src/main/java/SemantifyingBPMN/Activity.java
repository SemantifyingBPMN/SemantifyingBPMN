package SemantifyingBPMN;

import java.util.ArrayList;
import java.util.UUID;

import javax.xml.namespace.QName;

public class Activity 
extends BPMNElement{
	
	private ActivityType type;
	private String Name;
	private String Description;
	

	public static double Width = 100;
	public static double Height = 80;
	
	public static double distance_between_activities = 150; //130 //200
	public static double offsetInitial = 120;
	
	public double getHeight() { return(Height); };
	public double getWidth() { return(Width); };

	private BoundaryEvent event_boundary = null;
	
	private Object itself_forRendering;

	public Object getItself_forRendering() {
		return itself_forRendering;
	}
	public void setItself_forRendering(Object itself_forRendering) {
		this.itself_forRendering = itself_forRendering;
	}
	public BoundaryEvent getEvent_boundary() {
		return event_boundary;
	}
	public void setEvent_boundary(BoundaryEvent event_boundary) {
		this.event_boundary = event_boundary;
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
	
	public boolean hasAttachedEvent()
	{
		if ( event_boundary != null) return (true);
		else return(false);
	}

	@Override
	public String toString() {
		return "Activity [Name=" + Name + ", Description=" + Description + ", getName()=" + getName()
				+ ", getDescription()=" + getDescription() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
				+ ", toString()=" + super.toString() + "]";
	}
	
	public void Init()
	{
		setQname_BPMNElement( new QName("Activity_" +  UUID.randomUUID().toString()) );
		setQname_Shape ( new  QName(getQname_BPMNElement().toString() + "_di") );
		
		qname_flow_Incoming = new ArrayList<QName>();
		qname_flow_Outgoing = new ArrayList<QName>();
	}

	
	public Activity(ActivityType t_task , String name, String description , int level_l) {
		super();
		Init();
		Name = name;
		Description = description;
		type = t_task;		
		level = level_l;
	}


	public Activity(ActivityType t_task , String name, String description , int level_l , BoundaryEvent bevt) {
		super();
		Init();
		Name = name;
		Description = description;
		type = t_task;		
		level = level_l;
		event_boundary = bevt;
	}

	public int getType() {	return type.ordinal();	}
	
	public void setType(ActivityType type) { this.type = type; }
	
	public String getTypeS(){return(type.toString());}
	
	public void SetPositionAttachedBoundaryEvent(double X_t , double Y_t)
	{
		event_boundary.setX(X_t);
		event_boundary.setY(Y_t);
	}
	
}
