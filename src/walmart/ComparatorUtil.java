package walmart;

import java.util.Comparator;
import org.apache.commons.lang3.builder.CompareToBuilder;

public class ComparatorUtil implements Comparator<RatingPojo>{

	 @Override
	 //comparing the reviews and sorting the products
	    public int compare(RatingPojo o1, RatingPojo o2) {
	        return new CompareToBuilder()
	                .append(o2.getCompareTextRating1(), o1.getCompareTextRating1())
	                .append(o2.getCompareTextRating2(), o1.getCompareTextRating2())
	                .append(o2.getCompareTextRating3(), o1.getCompareTextRating3())
	                .append(o2.getCompareTextRating4(), o1.getCompareTextRating4())
	                .append(o2.getCompareTextRating5(), o1.getCompareTextRating5())
	                .toComparison();
	    }

}
