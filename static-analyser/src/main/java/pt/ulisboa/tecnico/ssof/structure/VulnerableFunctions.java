package pt.ulisboa.tecnico.ssof.structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import pt.ulisboa.tecnico.ssof.memory.Registers;
import pt.ulisboa.tecnico.ssof.memory.StackMemory;

public final class VulnerableFunctions {
	private static final Logger logger = Logger.getLogger(VulnerableFunctions.class);

	public static List<Vulnerability> gets(Registers registers, StackMemory stackMemory, String InstructionPointer) {
		List<Vulnerability> vulnerabilities = new ArrayList<>();
		Optional<Variable> variable = stackMemory.getMappedVariable(registers.read("rdi"));
		Long size = Long.valueOf(Integer.MAX_VALUE);

		if(!variable.isPresent()) {
			logger.fatal("Variable in address " + registers.read("rdi") + " not found.");
			System.exit(-1);
		}

		vulnerabilities = searchGetsVulnerabilities(stackMemory, variable, size, false);
		
		return filterVulnerabilities(vulnerabilities, "gets", InstructionPointer);
	}
	
	public static List<Vulnerability> fgets(Registers registers, StackMemory stackMemory, String InstructionPointer) {
		List<Vulnerability> vulnerabilities = new ArrayList<>();
		Optional<Variable> variable = stackMemory.getMappedVariable(registers.read("rdi"));
		Long size = registers.read("rsi");

		if(!variable.isPresent()) {
			logger.fatal("Variable in address " + registers.read("rdi") + " not found.");
			System.exit(-1);
		}

		vulnerabilities = searchGetsVulnerabilities(stackMemory, variable, size, false);

		return filterVulnerabilities(vulnerabilities, "fgets", InstructionPointer);
	}

	public static List<Vulnerability> strcpy(Registers registers, StackMemory stackMemory, String InstructionPointer) {
		List<Vulnerability> vulnerabilities = new ArrayList<>();
		Optional<Variable> variableDestination = stackMemory.getMappedVariable(registers.read("rdi"));
		Optional<Variable> variableSource = stackMemory.getMappedVariable(registers.read("rsi"));

		if(!variableDestination.isPresent()) {
			logger.fatal("Variable in address " + registers.read("rdi") + " not found.");
			System.exit(-1);
		}
		if(!variableSource.isPresent()) {
			logger.fatal("Variable in address " + registers.read("rsi") + " not found.");
			System.exit(-1);
		}
		Long limit = (long) getVariableStringSize(stackMemory, variableSource.get()) + 1;
		vulnerabilities = searchStrcpyVulnerabilities(stackMemory, variableSource.get(),
			variableDestination.get(), limit);
		
		return filterVulnerabilities(vulnerabilities, "strcpy", InstructionPointer);
	}

	public static List<Vulnerability> strncpy(Registers registers, StackMemory stackMemory, String InstructionPointer) {
		List<Vulnerability> vulnerabilities = new ArrayList<>();
		Optional<Variable> variableDestination = stackMemory.getMappedVariable(registers.read("rdi"));
		Optional<Variable> variableSource = stackMemory.getMappedVariable(registers.read("rsi"));

		if(!variableDestination.isPresent()) {
			logger.fatal("Variable in address " + registers.read("rdi") + " not found.");
			System.exit(-1);
		}
		if(!variableSource.isPresent()) {
			logger.fatal("Variable in address " + registers.read("rsi") + " not found.");
			System.exit(-1);
		}
		Long size = registers.read("rdx");
		vulnerabilities = searchStrcpyVulnerabilities(stackMemory, variableSource.get(),
			variableDestination.get(), size);

		return filterVulnerabilities(vulnerabilities, "strncpy", InstructionPointer);
	}

	public static List<Vulnerability> strcat(Registers registers, StackMemory stackMemory, String InstructionPointer) {
		List<Vulnerability> vulnerabilities = new ArrayList<>();
		Optional<Variable> variableDestination = stackMemory.getMappedVariable(registers.read("rdi"));
		Optional<Variable> variableSource = stackMemory.getMappedVariable(registers.read("rsi"));

		if(!variableDestination.isPresent()) {
			logger.fatal("Variable in address " + registers.read("rdi") + " not found.");
			System.exit(-1);
		}
		if(!variableSource.isPresent()) {
			logger.fatal("Variable in address " + registers.read("rsi") + " not found.");
			System.exit(-1);
		}

		Long limit = (long) getVariableStringSize(stackMemory, variableSource.get()) + 1;
		vulnerabilities = searchStrcatVulnerabilities(stackMemory, variableSource.get(),
			variableDestination.get(), limit);
		
		return filterVulnerabilities(vulnerabilities, "strcat", InstructionPointer);
	}

	public static List<Vulnerability> strncat(Registers registers, StackMemory stackMemory, String InstructionPointer) {
		List<Vulnerability> vulnerabilities = new ArrayList<>();
		Optional<Variable> variableDestination = stackMemory.getMappedVariable(registers.read("rdi"));
		Optional<Variable> variableSource = stackMemory.getMappedVariable(registers.read("rsi"));

		if(!variableDestination.isPresent()) {
			logger.fatal("Variable in address " + registers.read("rdi") + " not found.");
			System.exit(-1);
		}
		if(!variableSource.isPresent()) {
			logger.fatal("Variable in address " + registers.read("rsi") + " not found.");
			System.exit(-1);
		}
		Long size = registers.read("rdx");
		vulnerabilities = searchStrcatVulnerabilities(stackMemory, variableSource.get(),
			variableDestination.get(), size);
		
		return filterVulnerabilities(vulnerabilities, "strncat", InstructionPointer);
	}

    public static List<Vulnerability> read(Registers registers, StackMemory stackMemory, String InstructionPointer) {
        List<Vulnerability> vulnerabilities = new ArrayList<>();
        Optional<Variable> variable = stackMemory.getMappedVariable(registers.read("rsi"));
        Long size = registers.read("rdx");

        if(!variable.isPresent()) {
            logger.fatal("Variable in address " + registers.read("rsi") + " not found.");
            System.exit(-1);
        }

        vulnerabilities = searchGetsVulnerabilities(stackMemory, variable, size, true);

        return filterVulnerabilities(vulnerabilities, "read", InstructionPointer);
    }

	public static List<Vulnerability> scanf(Registers registers, StackMemory stackMemory, String InstructionPointer) {
		List<Vulnerability> vulnerabilities = new ArrayList<>();
		Optional<Variable> variable1 = stackMemory.getMappedVariable(registers.read("rsi"));
		Optional<Variable> variable2 = stackMemory.getMappedVariable(registers.read("rdx"));
				
		if(!variable1.isPresent()) {
			logger.fatal("Variable in address " + registers.read("rsi") + " not found.");
			System.exit(-1);
		}
		Variable firstArgument = variable1.get();
		if(variable2.isPresent() && variable2.get().getName() != null) {
			firstArgument = variable2.get();
		}

		vulnerabilities = searchScanfVulnerabilities(stackMemory, firstArgument);

		return filterVulnerabilities(vulnerabilities, "__isoc99_scanf", InstructionPointer);
	}
	
	public static List<Vulnerability> fscanf(Registers registers, StackMemory stackMemory, String InstructionPointer) {
		List<Vulnerability> vulnerabilities = new ArrayList<>();

		Optional<Variable> file = stackMemory.getMappedVariable(registers.read("rdi"));
		Optional<Variable> variable1 = stackMemory.getMappedVariable(registers.read("rdx"));
		Optional<Variable> variable2 = stackMemory.getMappedVariable(registers.read("rcx"));
		
		if(!file.isPresent()) {
			logger.fatal("Variable in address " + registers.read("rdi") + " not found.");
			System.exit(-1);
		}
		
		if(!variable1.isPresent()) {
			logger.fatal("Variable in address " + registers.read("rdx") + " not found.");
			System.exit(-1);
		}
		Variable firstArgument = variable1.get();
		if(variable2.isPresent() && variable2.get().getName() != null) {
			firstArgument = variable2.get();
		}

		vulnerabilities = searchScanfVulnerabilities(stackMemory, firstArgument);

		return filterVulnerabilities(vulnerabilities, "__isoc99_fscanf", InstructionPointer);
	}

	public static List<Vulnerability> sprintf(Registers registers, StackMemory stackMemory, String InstructionPointer) {
		List<Vulnerability> vulnerabilities = new ArrayList<>();
		Optional<Variable> variableDest = stackMemory.getMappedVariable(registers.read("rdi"));
		Optional<Variable> variable1 = stackMemory.getMappedVariable(registers.read("rdx"));
		Optional<Variable> variable2 = stackMemory.getMappedVariable(registers.read("rcx"));
		
		if(!variableDest.isPresent()) {
			logger.fatal("Variable in address " + registers.read("rdi") + " not found.");
			System.exit(-1);
		}
		if(!variable1.isPresent()) {
			logger.fatal("Variable in address " + registers.read("rdx") + " not found.");
			System.exit(-1);
		}
		
		int size = Integer.MAX_VALUE;
		vulnerabilities = searchSprintfVulnerabilities(stackMemory, variableDest, variable1, variable2, size);
		
		return filterVulnerabilities(vulnerabilities, "sprintf", InstructionPointer);
	}

    public static List<Vulnerability> snprintf(Registers registers, StackMemory stackMemory, String InstructionPointer) {
        List<Vulnerability> vulnerabilities = new ArrayList<>();
        Optional<Variable> variableDest = stackMemory.getMappedVariable(registers.read("rdi"));
        Long size = registers.read("rsi");
        Optional<Variable> variable1 = stackMemory.getMappedVariable(registers.read("rcx"));
        Optional<Variable> variable2 = stackMemory.getMappedVariable(registers.read("r8"));

        if(!variableDest.isPresent()) {
            logger.fatal("Variable in address " + registers.read("rdi") + " not found.");
            System.exit(-1);
        }
        if(!variable1.isPresent()) {
            logger.fatal("Variable in address " + registers.read("rcx") + " not found.");
            System.exit(-1);
        }

        vulnerabilities = searchSprintfVulnerabilities(stackMemory, variableDest, variable1, variable2, Math.toIntExact(size));

        return filterVulnerabilities(vulnerabilities, "snprintf", InstructionPointer);
    }
    
	private static List<Vulnerability> searchScanfVulnerabilities (StackMemory stackMemory, Variable variable) {
		List<Vulnerability> vulnerabilities = new ArrayList<>();
		int size = Integer.MAX_VALUE;
		
		Vulnerability vulnerability;
		for(int i = 0; i < size; i++) {
			vulnerability = stackMemory.writeByte(variable, variable.getRelativeAddress() + i, 0xFFL);
			if(vulnerability != null &&
				StringUtils.equals(vulnerability.getVulnerabilityType(), "SCORRUPTION")) {
				vulnerabilities.add(vulnerability);
				break;
			}
			vulnerabilities.add(vulnerability);
		}
		return vulnerabilities;
	}

    private static List<Vulnerability> searchGetsVulnerabilities(StackMemory stackMemory, Optional<Variable> variable, Long size,
                                                                 boolean read) {
		List<Vulnerability> vulnerabilities = new ArrayList<>();
		boolean scorruption = false;

		Vulnerability vulnerability;
		for(int i = 0; i < Math.toIntExact(size) - 1; i++) {
			vulnerability = stackMemory.writeByte(variable.get(), variable.get().getRelativeAddress() + i, 0xFFL);
			if(vulnerability != null &&
				StringUtils.equals(vulnerability.getVulnerabilityType(), "SCORRUPTION")) {
				scorruption = true;
				vulnerabilities.add(vulnerability);
				break;
			}
			vulnerabilities.add(vulnerability);
		}
		if(scorruption){
			return vulnerabilities;
		}

		if(read){
            vulnerability = stackMemory.writeByte(variable.get(), variable.get().getRelativeAddress() + Math.toIntExact(size) - 1, 0xFFL);
        } else {
            vulnerability = stackMemory.writeByte(variable.get(), variable.get().getRelativeAddress() + Math.toIntExact(size) - 1, 0x00L);
        }
        vulnerabilities.add(vulnerability);

		return vulnerabilities;
	}

	private static List<Vulnerability> searchStrcpyVulnerabilities(StackMemory stackMemory,
			Variable variableSource, Variable variableDestination, Long limit) {
		List<Vulnerability> vulnerabilities = new ArrayList<>();
		boolean scorruption = false;
		/* contentSize stores the size of the content of the sourceVariable to copy */
		int contentSize = Integer.min(getVariableStringSize(stackMemory, variableSource), Math.toIntExact(limit));

		Vulnerability vulnerability;
		int i;
		for(i = 0; i < contentSize; i++) {
			Long content = stackMemory.readByte(variableSource.getRelativeAddress() + i);
			vulnerability = stackMemory.writeByte(variableDestination,
				variableDestination.getRelativeAddress() + i, content);
			if(vulnerability != null &&
				StringUtils.equals(vulnerability.getVulnerabilityType(), "SCORRUPTION")) {
				scorruption = true;
	 			vulnerabilities.add(vulnerability);
				break;
			}
			vulnerabilities.add(vulnerability);
		}
		if(scorruption) {
 			return vulnerabilities;
 		}
		/*If the length of sourceVariable is less than n, strncpy() writes additional null bytes to dest
		 * to ensure that a total of n bytes are written. In strcpy, the n is equal to the sourceVariable size
		 * so this loop is not performed. */
		for( ; i < Math.toIntExact(limit); i++) {
			vulnerability = stackMemory.writeByte(variableDestination,
					variableDestination.getRelativeAddress() + i, 0x00L);
				vulnerabilities.add(vulnerability);
				if(vulnerability != null &&
					StringUtils.equals(vulnerability.getVulnerabilityType(), "SCORRUPTION")) {
					break;
				}
		}
		return vulnerabilities;
	}

	private static List<Vulnerability> searchStrcatVulnerabilities(StackMemory stackMemory,
			Variable variableSource, Variable variableDestination, Long limit) {
		List<Vulnerability> vulnerabilities = new ArrayList<>();
		/* sourceSize stores the size of the content of the sourceVariable to copy */
		int sourceSize = Integer.min(getVariableStringSize(stackMemory, variableSource), Math.toIntExact(limit));
		int destinationSize = getVariableStringSize(stackMemory, variableDestination);
		boolean scorruption = false;

		Vulnerability vulnerability;
		int i;
		for(i = 0; i < sourceSize; i++) {
			Long content = stackMemory.readByte(variableSource.getRelativeAddress() + i);
			vulnerability = stackMemory.writeByte(variableDestination,
				variableDestination.getRelativeAddress() + destinationSize + i, content);
			if(vulnerability != null &&
				StringUtils.equals(vulnerability.getVulnerabilityType(), "SCORRUPTION")) {
				scorruption = true;
	 			vulnerabilities.add(vulnerability);
				break;
			}
			vulnerabilities.add(vulnerability);
		}
		if(scorruption) {
 			return vulnerabilities;
 		}
		/*If the length of sourceVariable is less than n, strncat() writes additional null bytes to dest
		 * to ensure that a total of n bytes are written. In strcat, the n is equal to the sourceVariable size
		 * so this loop is not performed. */
		for( ; i < Math.toIntExact(limit) + 1; i++) {
			vulnerability = stackMemory.writeByte(variableDestination,
					variableDestination.getRelativeAddress() + destinationSize + i, 0x00L);
				vulnerabilities.add(vulnerability);
				if(vulnerability != null &&
					StringUtils.equals(vulnerability.getVulnerabilityType(), "SCORRUPTION")) {
					break;
				}
		}
		return vulnerabilities;
	}
	
	private static List<Vulnerability> searchSprintfVulnerabilities(StackMemory stackMemory,
			Optional<Variable> variableDest, Optional<Variable> variable1, Optional<Variable> variable2, int size) {
		List<Vulnerability> vulnerabilities = new ArrayList<>();
		boolean scorruption = false;

		int byteCounter = 0;
		
		int j = 0;
		Vulnerability vulnerability;
		for(int i = 0; i < getVariableStringSize(stackMemory, variable1.get()); i++) {
			Long content = stackMemory.readByte(variable1.get().getRelativeAddress() + i);
			vulnerability = stackMemory.writeByte(variableDest.get(), variableDest.get().getRelativeAddress() + i, content);
            byteCounter++;
			if(vulnerability != null &&
				StringUtils.equals(vulnerability.getVulnerabilityType(), "SCORRUPTION")) {
                vulnerabilities.add(vulnerability);
				scorruption = true;
				break;
			}

            if (byteCounter == size - 1){
                vulnerabilities.add(stackMemory.writeByte(variableDest.get(), variableDest.get().getRelativeAddress() + i + 1, 0x00L));
                byteCounter++;
                break;
            }

            vulnerabilities.add(vulnerability);
			j = i;
		}
		if(scorruption) {
 			return vulnerabilities;
 		} else if (byteCounter == size){
            return vulnerabilities;
        } else if(!(variable2.isPresent() && variable2.get().getName() != null)){
            vulnerabilities.add(stackMemory.writeByte(variableDest.get(), variableDest.get().getRelativeAddress() + j, 0x00L));
            return vulnerabilities;
        }

		for(int i = 0; i < getVariableStringSize(stackMemory, variable2.get()); i++) {
			Long content = stackMemory.readByte(variable2.get().getRelativeAddress() + i);
			vulnerability = stackMemory.writeByte(variableDest.get(), variableDest.get().getRelativeAddress() + j + i, content);
            byteCounter++;
			if(vulnerability != null &&
				StringUtils.equals(vulnerability.getVulnerabilityType(), "SCORRUPTION")) {
                vulnerabilities.add(vulnerability);
				break;
			}

			if (byteCounter == size - 1){
                vulnerabilities.add(stackMemory.writeByte(variableDest.get(), variableDest.get().getRelativeAddress() + j + i + 1, 0x00L));
                break;
            }

			vulnerabilities.add(vulnerability);
		}
		return vulnerabilities;
	}
	
	/**
	 * This method returns the size of the string saved in the variable (until /0)
	 */
	private static int getVariableStringSize(StackMemory stackMemory, Variable variable) {
		int size = 0;
		while( stackMemory.readByte(variable.getRelativeAddress() + size) != 0x00L) {
			size++;
		}
		return size;
	}
	
	/**
	 * This method returns the list of vulnerabilites without null objects and duplicates and sets the function name
	 * and instruction pointer for each vulnerability
	 */
	private static List<Vulnerability> filterVulnerabilities (List<Vulnerability> vulnerabilities, String functionName, 
    		String InstructionPointer) {
    	return vulnerabilities.stream()
        .filter(Objects::nonNull)
        .distinct()
        .peek(vuln -> {
            vuln.setFunctionName(functionName);
            vuln.setAddress(InstructionPointer);
        }).collect(Collectors.toList());
    }

}
