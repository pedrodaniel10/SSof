package pt.ulisboa.tecnico.ssof.structure.operations;

import pt.ulisboa.tecnico.ssof.structure.Instruction;
import pt.ulisboa.tecnico.ssof.visitor.Visitor;

public class Sub extends Instruction {
	private String destination;
	private String value;
	
	public Sub(int position, String address, String destination, String value) {
		super(position, address);
		this.destination = destination;
		this.value = value;
	}

	public String getDestination() {
		return destination;
	}

	public String getValue() {
		return value;
	}

	@Override
	public void accept(Visitor visitor){
		visitor.visitSub(this);
	}
}
