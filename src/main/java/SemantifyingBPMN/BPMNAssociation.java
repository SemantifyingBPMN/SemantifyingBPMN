package SemantifyingBPMN;

import java.util.UUID;

import javax.xml.namespace.QName;

public class BPMNAssociation {

	private QName sourceQName;
	private QName targetQName;
	
	private QName QName_associationid;	
	private QName QName_Shape;

	private double pos_X_Start;
	private double pos_Y_Start;
	
	private double pos_X_End;
	private double pos_Y_End;
	
	

	public double getPos_X_Start() {
		return pos_X_Start;
	}
	public void setPos_X_Start(double pos_X_Start) {
		this.pos_X_Start = pos_X_Start;
	}
	public double getPos_Y_Start() {
		return pos_Y_Start;
	}
	public void setPos_Y_Start(double pos_Y_Start) {
		this.pos_Y_Start = pos_Y_Start;
	}
	public double getPos_X_End() {
		return pos_X_End;
	}
	public void setPos_X_End(double pos_X_End) {
		this.pos_X_End = pos_X_End;
	}
	public double getPos_Y_End() {
		return pos_Y_End;
	}
	public void setPos_Y_End(double pos_Y_End) {
		this.pos_Y_End = pos_Y_End;
	}
	public QName getSourceQName() {
		return sourceQName;
	}
	public void setSourceQName(QName sourceQName) {
		this.sourceQName = sourceQName;
	}
	public QName getTargetQName() {
		return targetQName;
	}
	public void setTargetQName(QName targetQName) {
		this.targetQName = targetQName;
	}

	public QName getQName_associationid() {
		return QName_associationid;
	}
	public void setQName_associationid(QName qName_associationid) {
		QName_associationid = qName_associationid;
	}
	
	public QName getQName_Shape() {
		return QName_Shape;
	}
	public void setQName_Shape(QName qName_Shape) {
		QName_Shape = qName_Shape;
	}
	
	public void Init()
	{
		setQName_associationid( new QName("Association_" +  UUID.randomUUID().toString()) );
		setQName_Shape ( new  QName(getQName_associationid().toString() + "_di") );
	}

	
	public BPMNAssociation(QName source , QName target) {
		super();
		Init();
		setSourceQName(source);
		setTargetQName(target);

	}
	

	
	
	
}
