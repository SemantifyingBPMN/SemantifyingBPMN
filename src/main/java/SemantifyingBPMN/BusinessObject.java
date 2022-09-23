package SemantifyingBPMN;

public class BusinessObject 
{
	private String TransactionKind;
	private String TransactionStep;
	private String name;
	
	public String getTransactionKind() {
		return TransactionKind;
	}
	public void setTransactionKind(String transactionKind) {
		TransactionKind = transactionKind;
	}
	public String getTransactionStep() {
		return TransactionStep;
	}
	public void setTransactionStep(String transactionStep) {
		TransactionStep = transactionStep;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "BusinessObject [TransactionKind=" + TransactionKind + ", TransactionStep=" + TransactionStep + ", name="
				+ name + "]";
	}
	
	public BusinessObject(String transactionKind, String transactionStep, String name) {
		super();
		TransactionKind = transactionKind;
		TransactionStep = transactionStep;
		this.name = name;
	}
	
	
	
	

}
