package SemantifyingBPMN;

import java.util.Arrays;

public class TransactionKind {

		private String 		Name;
		private String 		Description;
		private ActorRole 	InitiatorRole;
		private ActorRole 	ExecutorRole;
		private FactType 	ProductKind;
		
		
		

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
		public ActorRole getInitiatorRole() {
			return InitiatorRole;
		}
		public void setInitiatorRole(ActorRole initiatorRole) {
			InitiatorRole = initiatorRole;
		}
		public ActorRole getExecutorRole() {
			return ExecutorRole;
		}
		public void setExecutorRole(ActorRole executorRole) {
			ExecutorRole = executorRole;
		}
		public FactType getProductKind() {
			return ProductKind;
		}
		public void setProductKind(FactType productKind) {
			ProductKind = productKind;
		}
				

		@Override
		public String toString() {
			return "\n TransactionKind [Name=" + Name + ", Description=" + Description + ",\n InitiatorRole=" + InitiatorRole
					+ ", ExecutorRole=" + ExecutorRole + ", ProductKind=" + ProductKind + ",\n getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
					+ "]\n";
		}
		public TransactionKind(String name, String description, ActorRole initiatorRole, ActorRole executorRole,
				FactType productKind) {
			super();
			Name = name;
			Description = description;
			InitiatorRole = initiatorRole;
			ExecutorRole = executorRole;
			ProductKind = productKind;
		}
		
		

}
