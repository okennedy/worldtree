package test;


public class MemUsageMonitor extends Thread implements Runnable {
	private long delay;
	private boolean quiet = false;
	MemUnit maxUsage = null;
	public MemUsageMonitor(long delay) {
		this(delay, false);
	}
	
	public MemUsageMonitor() {
		this(1000, false);
	}
	
	public MemUsageMonitor(long delay, boolean quiet) {
		super();
		this.delay		= delay;
		this.quiet		= quiet;
		this.maxUsage	= new MemUnit(0);
	}
	
	@Override
	public void run() {
		try {
			while(true) {
				Thread.sleep(delay);
				MemUnit usage	= MemUnit.getUsedMemory();
				if(!quiet)
					System.out.println(usage);
				else {
					if(usage.bytes() > maxUsage.bytes()) {
						maxUsage = usage;
					}
				}
			}
		} catch(InterruptedException e) {
			System.out.println("Max Usage :" + maxUsage);
		}
	}
	
	public MemUnit getMax() {
		return maxUsage;
	}
}