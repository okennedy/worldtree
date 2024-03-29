package internal.parser.resolve;

import internal.parser.containers.Reference;
import internal.tree.IWorldTree;

import java.util.ArrayList;
import java.util.List;

import static internal.Helper.multiLine;

/**
 * Result class is used to represent the output produced by the ResolutionEngine. <br>
 * It is designed to behave like a table
 * @author guru
 *
 */
public class Result extends ArrayList<Column> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3076712417591790867L;

	@Override
	public boolean add(Column column) {
		for(Column t : this) {
			if(t.name().equals(column.name())) {
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
	public boolean contains(Reference reference) {
		for(Column t : this) {
			if(t.name().equals(reference))
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
	public Column get(Reference reference) {
		for(Column t : this) {
			if(t.name().equals(reference))
				return t;
		}
		return null;
	}

	/**
	 * Add a new row to this result <br>
	 * If the row is already present, return immediately
	 * @param row {@code List<IWorldTree>} containing the elements to add
	 */
	public void add(List<IWorldTree> row) {
//		if(contains(row))
//			return;
		
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
		assert row.size() == size();
		
		int rowIndex = 0;
		while(rowIndex < get(0).size()) {
			boolean result 	= true;
			int columnIndex	= 0;
			while(columnIndex < size()) {
				if(!get(columnIndex).get(rowIndex).equals(row.get(columnIndex)))
					result = false;
				columnIndex++;
			}
			if(result) {
				throw new IllegalStateException("Duplicate row being added!");
			}
			rowIndex++;
		}
		return false;
	}
	
	/**
	 * Obtain the index of a {@code Column} by name
	 * @param name {@code String} containing the name of the {@code Column}
	 * @return {@code int} representing the index of the {@code Column} if found <br>
	 * {@code -1} otherwise
	 */
	public int indexOf(Reference reference) {
		for(int i = 0; i < size(); i++) {
			if(get(i).name().equals(reference))
				return i;
		}
		return -1;
	}
	
	/**
	 * Obtain a String representation of this result
	 */
	@Override
	public String toString() {
		List<String> stringList = new ArrayList<String>();
		for(Column t : this) {
			stringList.add(t.toString());
		}
		return multiLine(stringList) + "\n";
	}

	/**
	 * This method is used to remove a row corresponding to the given row index <br>
	 * @param rowIndex {@code int} specifying the index of the row to be removed
	 */
	public void removeRow(int rowIndex) {
		for(Column column : this) {
			column.remove(rowIndex);
		}
	}
	
	/**
	 * Obtain a row from this {@code Result} object
	 * @param rowIndex {@code int} specifying the row index to fetch
	 * @return {@code List<IWorldTree>} containing the elements of this row
	 */
	public List<IWorldTree> getRow(int rowIndex) {
		List<IWorldTree> result = new ArrayList<IWorldTree>();
		for(Column c : this) {
			if(rowIndex < c.size())
			result.add(c.get(rowIndex));
		}
		if(result.size() > 0)
			return result;
		else
			return null;
	}

	/**
	 * Create a new {@code Result} object that has the same columns as the <b> result </b> parameter <br>
	 * <b> No column values are copied! </b>
	 * @param result {@code Result} object to clone
	 * @return {@code Result} object containing the same fields as <b> result </b>
	 */
	public static Result newCopy(Result result) {
		Result newResult = new Result();
		for(Column c : result)
			newResult.add(new Column(c.name()));
		return newResult;
	}
}
