package SemantifyingBPMN;

import java.util.ArrayList;
import java.util.UUID;

import javax.xml.namespace.QName;

public class Gateway 
extends BPMNElement{
	
	private GatewayType type;
	private String Name;
	private String Description;
	
	public static double Width = 36;
	public static double Height = 36;
	
	public double getHeight() { return(Height); };
	public double getWidth() { return(Width); };

	
	
	public void Init()
	{
		setQname_BPMNElement( new QName("Gateway_" +  UUID.randomUUID().toString()) );
		setQname_Shape ( new  QName(getQname_BPMNElement().toString() + "_di") );
		qname_flow_Incoming = new ArrayList<QName>();
		qname_flow_Outgoing = new ArrayList<QName>();

	}

	public Gateway(GatewayType type, String name, String description , int level_l) {
		super();
		Init();
		this.type = type;
		Name = name;
		Description = description;
		level = level_l;
	}
	
	public int getType() {
		return type.ordinal();
	}
	public void setType(GatewayType type) {
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
