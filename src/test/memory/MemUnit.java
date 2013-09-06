package test.memory;

public class MemUnit {
	long bytes;
	
	public MemUnit(long bytes) {
		this.bytes	= bytes;
	}
	
	@Override
	public String toString() {
		long kb = bytes / 1024;
		long mb = kb 	/ 1024;
		
		return mb + " Mb" + " (" + kb + " Kb)" + " (" + bytes + " b)";
	}
	
	public long bytes() {
		return bytes;
	}
	
	public MemUnit difference(MemUnit memUnit) {
		return new MemUnit(this.bytes - memUnit.bytes);
	}
	
	public static MemUnit getUsedMemory() {
		long total	= Runtime.getRuntime().totalMemory();
		long free	= Runtime.getRuntime().freeMemory();
		
		return new MemUnit(total - free);
	}
}
