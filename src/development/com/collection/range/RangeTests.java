package development.com.collection.range;

import static org.junit.Assert.*;

import java.util.Set;

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
				if(i == j) {
					Range range = IntegerRange.closed(i, j);
					set.add(range);
				}
			}
		}
		if(set.size() != 5)
			result.append("Failed!\n\t\tRangeSetInsertion failed! Expected 5 elements..found only " + set.size() + "\n\n");
		else
			result.append("Succeeded!\n");
	}

	@Test
	public void RangeSetDuplicates() {
		result.append("\nRangeSetDuplicates: ");
		RangeSet set = new RangeSet();
		for(int i = 0; i < 5; i++) {
			for(int j = 0; j < 5; j++) {
				if(i <= j) {
					Range range = IntegerRange.closed(i, j);
					set.add(range);
				}
			}
		}
		
//		Basic duplicate test
		int size = set.size();
		Range range = set.get(0).clone();
		set.add(range);
		if(set.size() != size) {
			result.append("Failed!\n\t\tRangeSet size changed when inserting duplicate range!" + "\n\n");
			failed = true;
		}
		
//		Test with different lower bound type
		range = set.get(0).clone();
		range.setLowerBoundType(BoundType.OPEN);
		set.add(range);
		if(set.size() != size) {
			result.append("Failed!\n\t\tRangeSet size changed when inserting duplicate range with different lower bound type!" + "\n\n");
			failed = true;
		}
		set.remove(range);
		
//		Test with different upper bound type
		range = set.get(0).clone();
		range.setUpperBoundType(BoundType.OPEN);
		set.add(range);
		if(set.size() != size) {
			result.append("Failed!\n\t\tRangeSet size changed when inserting duplicate range with different upper bound type!" + "\n\n");
			failed = true;
		}
		set.remove(range);
		
//		Test with different bound types
		range = set.get(0).clone();
		range.setUpperBoundType(BoundType.OPEN);
		range.setLowerBoundType(BoundType.OPEN);
		set.add(range);
		if(set.size() != size) {
			result.append("Failed!\n\t\tRangeSet size changed when inserting duplicate range with different bound types!" + "\n\n");
			failed = true;
		}
		set.remove(range);
		
		if(!failed)
			result.append("Succeeded!\n");
	}
	
	@Test
	public void RangeSetMerge() {
		result.append("\nRangeSetMerge: ");
		boolean testResult = false;
		
		RangeSet set = new RangeSet();
		set.add(IntegerRange.closed(0, 2));
		set.add(IntegerRange.closed(4, 12));
		set.add(IntegerRange.closed(20, 28));
		set.add(IntegerRange.closed(20, 32));
		set.add(IntegerRange.closed(42, 46));
		
//		Case 1: R2 lies inside R1 - return R1
		Range includedRange 	= IntegerRange.open(4, 12);
		testResult = set.add(includedRange);
		if(testResult) {
			result.append("Failed!\n" +
					"\t\tRangeSet faied on case 1: R2 lies inside R1 - return R1\n" +
					"\t\tSet is :" + set + "\n" +
					"\t\trange  :" + includedRange + "\n\n");
			
			failed = true;
		}
		testResult = false;
		
//		Case 2: R1 lies inside R2 - return R2
		includedRange	= IntegerRange.openClosed(3, 12);
		testResult = set.add(includedRange);
		if(testResult) {
			result.append("Failed!\n" +
					"\t\tRangeSet faied on case 2: R1 lies inside R2 - return R2\n" +
					"\t\tSet is :" + set + "\n" +
					"\t\trange  :" + includedRange + "\n\n");
			failed = true;
		}
		else {
			testResult = set.contains(includedRange);
			if(!testResult) {
				result.append("Failed!\n" +
						"\t\tRangeSet faied on case 2: R1 lies inside R2 - return R2\n" +
						"\t\tDid not contain the range that was inserted!\n" +
						"\t\tSet is :" + set + "\n" +
						"\t\trange  :" + includedRange + "\n\n");
				failed = true;
			}
		}
		testResult = false;
		
		
//		Case 3: Partial overlap - return span
		Range overlapRange		= IntegerRange.open(5, 44);
		set.add(overlapRange);
		for(Range range1 : set) {
			for(Range range2 : set) {
				if(range1 == range2)
					continue;
				else {
					testResult = range1.intersection(range2) == null ? true : false;
					if(!testResult) {
						result.append("Failed!\n" +
								"\t\tRangeSet faied on case 3: Partial overlap - return span\n" +
								"\t\tAfter insertion, the set did not eliminate all overlaps\n" +
								"\t\tOverlap between " + range1 + " and " + range2 + "\n" + 
								"\t\tSet is :" + set + "\n" +
								"\t\trange  :" + overlapRange + "\n\n");
						failed = true;
					}
				}
			}
		}
		
		
		if(!failed)
			result.append("Success!\n");
	}
}
