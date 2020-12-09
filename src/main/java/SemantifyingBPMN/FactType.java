package SemantifyingBPMN;

public class FactType {
	
		private String Name;
		private String Description;
		
		public FactType(String name, String description) {
			super();
			Name = name;
			Description = description;
		}
		
		public FactType() {
		
		}
		
		@Override
		public String toString() {
			return "FactType [Name=" + Name + ", Description=" + Description + ", getName()=" + getName()
					+ ", getDescription()=" + getDescription() + ", getClass()=" + getClass() + ", hashCode()="
					+ hashCode() + ", toString()=" + super.toString() + "]";
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
}
