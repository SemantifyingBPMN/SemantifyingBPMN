package SemantifyingBPMN;

public class ActorRole {

	private String Name;
	private String Description;
	
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
	@Override
	public String toString() {
		return "\nActorRole [Name=" + Name + ", Description=" + Description + ",\n getClass()=" + getClass() + ", hashCode()=" + hashCode()
				+ ", toString()=" + super.toString() + "]\n";
	}
	public ActorRole(String name, String description) {
		super();
		Name = name;
		Description = description;
	}
	

	
	public ActorRole()
	{
		
		
	}
	
	
}
