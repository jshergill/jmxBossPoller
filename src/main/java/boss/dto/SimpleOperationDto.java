package boss.dto;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SimpleOperationDto {
	private String operation;
	private String name;
    @JsonProperty("include-runtime")
    private boolean includeRuntime;
    @JsonProperty("include-defaults")
    private boolean includeDefaults;
    public boolean recursive;
    private List<String> address;

    /**
     * builds command object to read-resource recursively
     * @param operation
     * @param includeRuntime
     * @param recursive
     * @param address
     */
    public SimpleOperationDto(String operation, boolean includeRuntime, 
                          boolean recursive, String...address) {
        this.operation = operation;
        this.includeRuntime = includeRuntime;
        this.address = Arrays.asList(address);
    }
    
    /**
     * builds command object to read-attribute
     * @param operation
     * @param includeDefaults
     * @param address
     * @param name
     */
    public SimpleOperationDto(String operation, boolean includeDefaults, List<String> address, String name) {
	this.operation = operation;
	this.includeDefaults = includeDefaults;
	this.address = address;
	this.name=name;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isincludeDefaults() {
		return includeDefaults;
	}

	public void setincludeDefaults(boolean includeDefaults) {
		this.includeDefaults = includeDefaults;
	}
	
	public boolean isIncludeRuntime() {
		return includeRuntime;
	}

	public void setIncludeRuntime(boolean includeRuntime) {
		this.includeRuntime = includeRuntime;
	}

	public boolean isRecursive() {
		return recursive;
	}

	public void setRecursive(boolean recursive) {
		this.recursive = recursive;
	}

	public List<String> getAddress() {
		return address;
	}

	public void setAddress(List<String> address) {
		this.address = address;
	}

}
