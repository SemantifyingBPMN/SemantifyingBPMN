package SemantifyingBPMN;

import java.util.UUID;

import javax.xml.namespace.QName;

public class BoundaryEvent 
extends BPMNElement{

	
	private String Name;
	private String Description;
	
	public static double Width = 36;
	public static double Height = 36;
	
	public  double offsetEvent = 60;

	private Object itself_forRendering;

	public void Init()
	{
		setQname_BPMNElement( new QName("BoundaryEvent_" +  UUID.randomUUID().toString()) );		
	}
	
	public BoundaryEvent(String name, String description) {
		super();
		Init();
		Name = name;
		Description = description;
	}

	
	public Object getItself_forRendering() {
		return itself_forRendering;
	}

	public void setItself_forRendering(Object itself_forRendering) {
		this.itself_forRendering = itself_forRendering;
	}

	public void setName(String name) {
		Name = name;
	}

	public void setDescription(String description) {
		Description = description;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return Name;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return Description;
	}

	@Override
	public String getTypeS() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getHeight() {
		// TODO Auto-generated method stub
		return Height;
	}

	@Override
	public double getWidth() {
		// TODO Auto-generated method stub
		return Width;
	}
	
	

}
