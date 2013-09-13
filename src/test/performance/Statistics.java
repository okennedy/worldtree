package test.performance;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Statistics extends TreeMap<Integer, List<Long>> {
	private static final long serialVersionUID = -7576749890787643554L;

	public Statistics() {
		super();
	}
	
	public void store(FileWriter fileWriter, String comments) {
		try {
			BufferedWriter out = new BufferedWriter(fileWriter);
			out.write("#" + comments);
			out.newLine();
			SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
			out.write("#" + sdf.format(Calendar.getInstance().getTime()));
			out.newLine();
			
			StringBuilder sb = new StringBuilder();
			for(Map.Entry<Integer, List<Long>> entry : entrySet()) {
				sb.delete(0, sb.length());
				int key 		= entry.getKey();
				String delimiter = "";
				for(Long value : entry.getValue()) {
					sb.append(delimiter);
					sb.append(Long.toString(value));
					delimiter = ",";
				}
				out.write(Integer.toString(key) + "=" + sb.toString());
				out.newLine();
			}
			out.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized List<Long> put(Integer integer, List<Long> list) {
		return super.put(integer, list);
	}
	
	@Override
	public synchronized List<Long> get(Object key) {
		return super.get(key);
	}
	
	@Override
	public synchronized boolean containsKey(Object key) {
		return super.containsKey(key);
	}
}