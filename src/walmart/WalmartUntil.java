package walmart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WalmartUntil {

	public List<RatingPojo> callUtil(List<String> productIDs,List<String> reviews,List<String> compareTexts) {

		List<RecommendedProductsPojo> countList=new ArrayList<RecommendedProductsPojo>();

		for(int i=0;i<productIDs.size();i++){

			String review=reviews.get(i);
			if(review!=null){

				RecommendedProductsPojo recommendedProductsPojo=null;
				for(String ss:compareTexts){
					Integer compareTextCount=0;
					/*comparing whether the review given by the user contains the the text in compareTexts.
					Since this project uses compare text like "excellent", "good", etc. to rank the products based on review,
					number of times the compare texts values are repeated in the reviews are calculated.*/
					if(review.toLowerCase().contains(ss)){
						//counting the number of times the compareText value is present in the review.
						compareTextCount=getCountOfStr(review,ss);
						recommendedProductsPojo=new RecommendedProductsPojo();
						//set the values 
						recommendedProductsPojo.setProductId(productIDs.get(i));
						recommendedProductsPojo.setRateText(ss);
						recommendedProductsPojo.setCount(compareTextCount);
						countList.add(recommendedProductsPojo);}
				}

			}

		}


		List<RatingPojo> finalList=new ArrayList<RatingPojo>();
		for(String pID:productIDs){
			RatingPojo ratingPojo=new RatingPojo();
			ratingPojo.setPrd(pID);
			int i=0;

			for(String ss:compareTexts){
				for(RecommendedProductsPojo recommendedProductsPojo:countList){
					if(recommendedProductsPojo.getProduct().equals(pID)){
						if(recommendedProductsPojo.getRateText().equalsIgnoreCase(ss)){
							if(i==0)
								ratingPojo.setCompareTextRating1(ratingPojo.getCompareTextRating1()+recommendedProductsPojo.getCount());
							if(i==1)
								ratingPojo.setCompareTextRating2(ratingPojo.getCompareTextRating2()+recommendedProductsPojo.getCount());
							if(i==2)
								ratingPojo.setCompareTextRating3(ratingPojo.getCompareTextRating3()+recommendedProductsPojo.getCount());
							if(i==3)
								ratingPojo.setCompareTextRating4(ratingPojo.getCompareTextRating4()+recommendedProductsPojo.getCount());
							if(i==4)
								ratingPojo.setCompareTextRating4(ratingPojo.getCompareTextRating5()+recommendedProductsPojo.getCount());

						}
					}
				}
				i++;
			}
			finalList.add(ratingPojo);
		}
		//sorting the products based on user sentiments
		Collections.sort(finalList, new ComparatorUtil());
		return finalList;

	}

//method to calculate the count value of the compareText in the user review.
	public int getCountOfStr(String str,String findStr){

		int lastIndex = 0;
		int count = 0;

		while(lastIndex != -1){

			lastIndex = str.indexOf(findStr,lastIndex);

			if(lastIndex != -1){
				count ++;
				lastIndex += findStr.length();
			}
		}

		return count;
	}

}
