package SemantifyingBPMN;

import java.util.UUID;

import javax.xml.namespace.QName;

public class BPMNMessageFlow {

	private QName sourceQName;
	private QName targetQName;
	
	private TransactionKind transaction_origin;
	private TransactionKind transaction_destination;
	
	private String ActKey;

	private QName QName_messageflowid;	
	private QName QName_Shape;

	private double pos_X_Start;
	private double pos_Y_Start;
	
	private double pos_X_End;
	private double pos_Y_End;
	
	private boolean direction;
	
	public void Init()
	{
		this.setQName_messageflowid( new QName("MessageFlow_" +  UUID.randomUUID().toString()) );
		this.setQName_Shape( new  QName(this.getQName_messageflowid().toString() + "_di")  );
	}

	
	
	public boolean isDirection() {
		return direction;
	}



	public void setDirection(boolean direction) {
		this.direction = direction;
	}



	public BPMNMessageFlow(TransactionKind tk , QName source , String ActKey_t , boolean dir) {
		super();
		Init();
		this.setTransaction_origin(tk);
		this.setSourceQName(source);
		this.setActKey(ActKey_t);
		this.setDirection(dir);
	}
	
	
	
	public String getActKey() {
		return ActKey;
	}



	public void setActKey(String actKey) {
		ActKey = actKey;
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
	public TransactionKind getTransaction_origin() {
		return transaction_origin;
	}
	public void setTransaction_origin(TransactionKind transaction_origin) {
		this.transaction_origin = transaction_origin;
	}
	public TransactionKind getTransaction_destination() {
		return transaction_destination;
	}
	public void setTransaction_destination(TransactionKind transaction_destination) {
		this.transaction_destination = transaction_destination;
	}
	public QName getQName_messageflowid() {
		return QName_messageflowid;
	}
	public void setQName_messageflowid(QName qName_messageflowid) {
		QName_messageflowid = qName_messageflowid;
	}
	public QName getQName_Shape() {
		return QName_Shape;
	}
	public void setQName_Shape(QName qName_Shape) {
		QName_Shape = qName_Shape;
	}
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
	
	
	
}
