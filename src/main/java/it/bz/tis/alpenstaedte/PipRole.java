package it.bz.tis.alpenstaedte;


public enum PipRole {
	ADMIN("ADMIN"),
	MANAGER("MANAGER"),
	USER("USER");
	
	private String name;
	private PipRole() {
	}
	private PipRole(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
}
