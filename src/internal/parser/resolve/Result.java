package internal.parser.resolve;

import internal.tree.IWorldTree;

import java.util.ArrayList;
import java.util.List;
import static test.ui.UIDebugEngine.multiLine;

/**
 * Result class is used to represent the output produced by the ResolutionEngine. <br>
 * It is designed to behave like a table
 * @author guru
 *
 */
public class Result extends ArrayList<Column> {
	
	@Override
	public boolean add(Column column) {
		for(Column t : this) {
			if(t.name.equals(column.name)) {
				int index = this.indexOf(t);
				this.remove(index);
				this.add(index, column);
				return true;
			}
		}
		return super.add(column);
	}
	
	/**
	 * Check if this result contains a {@code Column} with the specified name
	 * @param name {@code String} specifying the name to check
	 * @return {@code true} if there exists a {@code Column} with this name <br>
	 * {@code false} otherwise
	 */
	public boolean contains(String name) {
		for(Column t : this) {
			if(t.name.equals(name))
				return true;
		}
		return false;
	}

	/**
	 * Obtain a reference to a {@code Column} from its name
	 * @param name {@code String} specifying the name of the {@code Column}
	 * @return {@code Column} corresponding to the specified name <br>
	 * <b> null </b> otherwise
	 */
	public Column get(String name) {
		for(Column t : this) {
			if(t.name.equals(name))
				return t;
		}
		return null;
	}

	/**
	 * Add a new row to this result
	 * @param row {@code List<IWorldTree>} containing the elements to add
	 */
	public void add(List<IWorldTree> row) {
		assert(row.size() == size());
		int index = 0;
		for(Column t : this) {
			t.add(row.get(index));
			index++;
		}
	}
	
	/**
	 * Check if a particular row exists in this result
	 * @param row {@code List<IWorldTree>} containing the elements to check for
	 * @return {@code true} if this result contains the specified row <br>
	 * {@code false} otherwise
	 */
	public boolean contains(List<IWorldTree> row) {
		assert(row.size() == size());
		
		int rowIndex = 0;
		while(rowIndex < get(0).size()) {
			boolean result 	= true;
			int columnIndex	= 0;
			while(columnIndex < size()) {
				if(!get(columnIndex).get(rowIndex).equals(row.get(columnIndex)))
					result = false;
				columnIndex++;
			}
			if(result)
				return true;
			rowIndex++;
		}
		return false;
	}
	
	/**
	 * Obtain a String representation of this result
	 */
	@Override
	public String toString() {
		List<String> stringList = new ArrayList<String>();
		for(Column t : this) {
			
			StringBuffer sb = new StringBuffer();
			sb.append(t.name + "    \n");
			for(IWorldTree obj : t) {
				sb.append(obj.name() + "\n");
			}
			stringList.add(sb.toString());
		}
		return multiLine(stringList) + "\n";
	}

	public void removeRow(int index) {
		for(Column c : this)
			c.remove(index);
	}
}
