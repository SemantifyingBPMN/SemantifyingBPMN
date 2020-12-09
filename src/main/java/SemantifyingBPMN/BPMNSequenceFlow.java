package SemantifyingBPMN;


import java.util.UUID;

import javax.xml.namespace.QName;


public class BPMNSequenceFlow {
	
	private Object sourceRef;
	private Object targetRef;
	
	private QName QName_flowid;
	
	private QName sourceQName;
	private QName targetQName;
	
	
	private double pos_X_Start;
	private double pos_Y_Start;
	
	private double pos_X_End;
	private double pos_Y_End;

	
	public double getPos_X_Start() { 		return pos_X_Start;	}
	public void setPos_X_Start(double pos_X_Start) {		this.pos_X_Start = pos_X_Start;}
	public double getPos_Y_Start() {		return pos_Y_Start;}
	public void setPos_Y_Start(double pos_Y_Start) {		this.pos_Y_Start = pos_Y_Start;}
	public double getPos_X_End() {		return pos_X_End;	}
	public void setPos_X_End(double pos_X_End) {		this.pos_X_End = pos_X_End;	}
	public double getPos_Y_End() {		return pos_Y_End;	}
	public void setPos_Y_End(double pos_Y_End) {		this.pos_Y_End = pos_Y_End;}
	public Object getSourceRef() {	return sourceRef;	}
	public void setSourceRef(Object sourceRef) {	this.sourceRef = sourceRef;	}
	public Object getTargetRef() {		return targetRef;	}
	public void setTargetRef(Object targetRef) {		this.targetRef = targetRef;	}
	public QName getQName_Flowid() {		return QName_flowid;	}
	public void setQName_Flowid(QName flowid) {		this.QName_flowid = flowid;	}


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
	public void Init()
	{
		setQName_Flowid( new QName("Flow_" +  UUID.randomUUID().toString()) );
	}

	
	public BPMNSequenceFlow(QName source , QName target) {
		super();
		Init();
		setSourceQName(source);
		setTargetQName(target);

	}

}
