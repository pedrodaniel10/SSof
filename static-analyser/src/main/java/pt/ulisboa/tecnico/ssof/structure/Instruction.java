package pt.ulisboa.tecnico.ssof.structure;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = InstructionDeserializer.class)
public abstract class Instruction {
	private int position;
	private String address;
	
	public Instruction(int position, String address) {
		this.position = position;
		this.address = address;
	}
	
	public int getPosition() {
		return position;
	}

	public String getAddress() {
		return address;
	}
}