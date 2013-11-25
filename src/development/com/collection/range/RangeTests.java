package development.com.collection.range;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import development.com.collection.range.Range.BoundType;

public class RangeTests {
	static StringBuilder result;
	static boolean failed;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		result = new StringBuilder();
		failed = false;
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println(result.toString());
		result = null;
	}

	@Test
	public void RangeSetInsertion() {
		result.append("\nRangeSetInsertion: ");
//		Basic insertion test
		RangeSet set = new RangeSet();
		for(int i = 0; i < 5; i++) {
			for(int j = 0; j < 5; j++) {
				Range range = IntegerRange.closed(i, j);
				set.add(range);
			}
		}
		if(set.size() != 25) 
			result.append("Failed!\n\t\tRangeSetInsertion failed! Expected 25 elements..found only " + set.size() + "\n");
		else
			result.append("Succeeded!\n");
	}

	@Test
	public void RangeSetDuplicates() {
		result.append("\nRangeSetDuplicates: ");
		RangeSet set = new RangeSet();
		for(int i = 0; i < 5; i++) {
			for(int j = 0; j < 5; j++) {
				Range range = IntegerRange.closed(i, j);
				set.add(range);
			}
		}
		
//		Basic duplicate test
		Range range = set.get(0).clone();
		set.add(range);
		if(set.size() != 25) {
			result.append("Failed!\n\t\tRangeSet size changed when inserting duplicate range!\n");
			failed = true;
		}
		
//		Test with different lower bound type
		range = set.get(0).clone();
		range.setLowerBoundType(BoundType.OPEN);
		set.add(range);
		if(set.size() != 26) {
			result.append("Failed!\n\t\tRangeSet size did not change when inserting duplicate range with different lower bound type!\n");
			failed = true;
		}
		set.remove(range);
		
//		Test with different upper bound type
		range = set.get(0).clone();
		range.setUpperBoundType(BoundType.OPEN);
		set.add(range);
		if(set.size() != 26) {
			result.append("Failed!\n\t\tRangeSet size did not change when inserting duplicate range with different upper bound type!\n");
			failed = true;
		}
		set.remove(range);
		
//		Test with different upper bound type
		range = set.get(0).clone();
		range.setUpperBoundType(BoundType.OPEN);
		range.setLowerBoundType(BoundType.OPEN);
		set.add(range);
		if(set.size() != 26) {
			result.append("Failed!\n\t\tRangeSet size did not change when inserting duplicate range with different bound types!\n");
			failed = true;
		}
		set.remove(range);
		
		if(!failed)
			result.append("Succeeded!\n");
	}
}
