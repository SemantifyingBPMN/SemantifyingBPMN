package SemantifyingBPMN;

import java.util.ArrayList;

import javax.xml.namespace.QName;

public abstract class BPMNElement {

	private QName qname_BPMNElement;
	private QName qname_Shape;
	
	protected ArrayList<QName> qname_flow_Incoming;
	protected ArrayList<QName> qname_flow_Outgoing;
	
	protected int level;

	public double X;
	public double Y;
	
	public double Offset = 0;
	
	public abstract String getName();
	public abstract void setName(String Name);
	public abstract String getDescription();
	public abstract String getTypeS();
	public abstract int getType();
	public abstract double getHeight();
	public abstract double getWidth();
	
	public  QName getQname_BPMNElement() {return qname_BPMNElement;};
	public  QName getQname_Shape() {return qname_Shape;};
	public  void setQname_BPMNElement(QName received) {qname_BPMNElement = received;};
	public  void setQname_Shape(QName received) {qname_Shape = received;};
	
	
	
	public double getOffset() {
		return Offset;
	}
	public void setOffset(double offset) {
		Offset = offset;
	}
	public ArrayList<QName> getQname_flow_Incoming() {
		return qname_flow_Incoming;
	}
	public void setQname_flow_Incoming(ArrayList<QName> qname_flow_Incoming) {
		this.qname_flow_Incoming = qname_flow_Incoming;
	}
	public ArrayList<QName> getQname_flow_Outgoing() {
		return qname_flow_Outgoing;
	}
	public void setQname_flow_Outgoing(ArrayList<QName> qname_flow_Outgoing) {
		this.qname_flow_Outgoing = qname_flow_Outgoing;
	}
	public int getLevel() {return (level);}
	
	public double getX() {return (X);}
	public double getY() {return (Y);}
	public void setX(double x_e) {X = x_e;}
	public void setY(double y_e) {Y = y_e;}
	
	public void add_Flow_Incoming(QName flow)	{	qname_flow_Incoming.add(flow); }
	public void add_Flow_Outgoing(QName flow)	{	qname_flow_Outgoing.add(flow); }
	
	
	
}
