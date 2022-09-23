package SemantifyingBPMN;

import java.util.ArrayList;
import java.util.UUID;

import javax.xml.namespace.QName;

public class Event 
extends BPMNElement{
	

	private EventType type;
	private String Name;
	private String Description;
	
	public static double Width = 36;
	public static double Height = 36;
	
	public  double offsetEvent = 60;
	
	private QName qname_BPMNElement_EventDefinition;

	
	public void Init()
	{
		setQname_BPMNElement( new QName("Event_" +  UUID.randomUUID().toString()) );
		setQname_Shape ( new  QName(getQname_BPMNElement().toString() + "_di") );
		setQname_BPMNElement_EventDefinition (new QName("EventDefinition_" +  UUID.randomUUID().toString()) );
		qname_flow_Incoming = new ArrayList<QName>();
		qname_flow_Outgoing = new ArrayList<QName>();

	}

	public double getHeight() { return(Height); };
	public double getWidth() { return(Width); };

	
	public double getEvent_Width() {
		return Width;
	}

	public void setEvent_Width(double event_Width) {
		Width = event_Width;
	}

	public double getEvent_Height() {
		return Height;
	}

	public void setEvent_Height(double event_Height) {
		Height = event_Height;
	}
	
	public Event(EventType type, String name, String description , int level_l) {
		super();
		Init();
		this.type = type;
		this.Name = name;
		this.Description = description;
		level = level_l;
	}
	
	public QName getQname_BPMNElement_EventDefinition() {
		return qname_BPMNElement_EventDefinition;
	}


	public void setQname_BPMNElement_EventDefinition(QName qname_BPMNElement_EventDefinition) {
		this.qname_BPMNElement_EventDefinition = qname_BPMNElement_EventDefinition;
	}


	public int getType() {
		return type.ordinal();
	}

	public void setType(EventType type) {
		this.type = type;
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
	
	public String getTypeS()
	{
		return(type.toString());
	}
	

}
