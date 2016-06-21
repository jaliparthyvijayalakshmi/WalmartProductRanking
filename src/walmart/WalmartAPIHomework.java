package walmart;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class WalmartAPIHomework {

	private final String USER_AGENT = "Mozilla/5.0";

	public static void main(String[] args) throws Exception {

		WalmartAPIHomework obj = new WalmartAPIHomework();
		@SuppressWarnings("resource")
		Scanner reader = new Scanner(System.in);  // Reading from System.in
		System.out.println("Please enter Product Name: ");
		String searchWord = reader.next();
		//String apiKey = reader.next();

		obj.search(searchWord, "swmzmd67q54jwfgssgb9eutm");

	}

	// search method takes the product name as string and key value
	private void search(String searchItem, String apiKey) throws Exception {

		/* compareTexts are the sentiments of the uses on the products.
		 * using 5 words to rank the products on these user sentiments.
		 * We can add many words based on highly used terminology to obtain user sentiments. */
		List <String> compareTexts=new ArrayList<String>();
		compareTexts.add("excellent");
		compareTexts.add("love");
		compareTexts.add("satisfied");
		compareTexts.add("good");
		compareTexts.add("average");

		//making a call to walmart search API. Format selected is XML.
		String searchUrl = "http://api.walmartlabs.com/v1/search?query="+ searchItem + "&format=xml&apiKey=" + apiKey;
		// Whole search response is stored as a string.
		String searchResponse = callWalmartAPI(searchUrl);

		//Retrieving itemID from the search Response.
		String itemId = getSearchItemId(searchResponse, "itemId");
		if(itemId.equalsIgnoreCase("error")){
			System.out.println("Product Not Found");
			return;
		}
		
		//calling Product Recommendation API.
		String productRecommendationsUrl = "http://api.walmartlabs.com/v1/nbp?itemId="+ itemId + "&format=xml&apiKey=" + apiKey;
		String productRecommendationsResponse = callWalmartAPI(productRecommendationsUrl);
		//Retrieving the ID s of all the recommended products
		List<String> recommendProductsIdList = getElementsByTagName(productRecommendationsResponse, "itemId");
		if(recommendProductsIdList==null || recommendProductsIdList.size()==0){
			System.out.println("Product recommendations Not Found");
			return;
		}
		
		//Retrieving the names of recommended Products.
		List<String> recommendProductNameList = getElementsByTagName(productRecommendationsResponse, "name");
		System.out.println("Product recommendations \n");

		List<String> finalReviewsList = new ArrayList<String>();
		List<String> reviewsList=null;

		// Iterating over every element(size=10) and making a call to Review API
		for (int i=0;i<recommendProductsIdList.size();i++) {
			String reviewUrl = "http://api.walmartlabs.com/v1/reviews/"	+ recommendProductsIdList.get(i) + "?format=xml&apiKey=" + apiKey;
			String reviewResponse = callWalmartAPI(reviewUrl);
			//Printing the 10 recommended products information i.e (ID: Name)
			System.out.println(recommendProductsIdList.get(i)+" : "+recommendProductNameList.get(i));
			
			//Capturing the user review text from XML response from review API by using Tagname.
			reviewsList=(getElementsByTagName(reviewResponse, "reviewText"));
			String reviewsText=reviewsList.toString();
			finalReviewsList.add(reviewsText);
		}

		WalmartUntil walmartUntil=new WalmartUntil();
		//calling callUtil method in WalmartUntil.java
		List<RatingPojo> list = walmartUntil.callUtil(recommendProductsIdList, finalReviewsList, compareTexts);
		System.out.println("\nOrdered Product recommendations based upon customer reviews:- \n");
		for(RatingPojo pp:list){
			for(int i=0;i<recommendProductsIdList.size();i++){
				if(pp.getPrd().equals(recommendProductsIdList.get(i))){
					System.out.println(recommendProductsIdList.get(i)+" : "+recommendProductNameList.get(i)+ " = "+pp.getCompareTextRating1()+ " : "
							+ ""+pp.getCompareTextRating2()+ " : "+pp.getCompareTextRating3()+ ""
							+ " : "+pp.getCompareTextRating4()+ " : " +pp.getCompareTextRating5());

				}
			}

		}

	}

	private List<String> getElementsByTagName(String xml,String requiredValue) throws Exception {
		List<String> recommendList = new ArrayList<String>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		InputSource is;
		try {
			builder = factory.newDocumentBuilder();
			is = new InputSource(new StringReader(xml));
			Document doc = builder.parse(is);
			NodeList list = doc.getElementsByTagName(requiredValue);
			int k;
			k=list.getLength();
			if(!"reviewText".equalsIgnoreCase(requiredValue)){
				k=10;
				if(list.getLength()<k)
					k=list.getLength();

			}
			for (int i = 0; i < k; i++) {
				recommendList.add(list.item(i).getTextContent());
			}
		} catch (ParserConfigurationException e) {
		} catch (SAXException e) {
		} catch (IOException e) {
		}
		return recommendList;
	}

	private String getSearchItemId(String xml, String requiredValue)
			throws Exception {
		String value = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		InputSource is;
		try {
			builder = factory.newDocumentBuilder();
			is = new InputSource(new StringReader(xml));
			Document doc = builder.parse(is);
			NodeList list = doc.getElementsByTagName(requiredValue);
			if(list.item(0)!=null)
				value = list.item(0).getTextContent();
			else
				value="error";

		} catch (ParserConfigurationException e) {
		} catch (SAXException e) {
		} catch (IOException e) {
		}
		return value;
	}

	private String callWalmartAPI(String url) throws Exception {
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		// add request header
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		if(responseCode!=200){
			System.out.println("There is error in while calling walmart api "+responseCode);
			
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		return response.toString();
	}

}