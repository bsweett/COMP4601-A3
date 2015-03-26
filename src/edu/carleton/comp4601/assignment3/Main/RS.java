package edu.carleton.comp4601.assignment3.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Map.Entry;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import edu.carleton.comp4601.assignment3.algorithms.Cluster;
import edu.carleton.comp4601.assignment3.algorithms.KMeans;
import edu.carleton.comp4601.assignment3.algorithms.Apriori;
import edu.carleton.comp4601.assignment3.dao.Advertisement;
import edu.carleton.comp4601.assignment3.dao.Page;
import edu.carleton.comp4601.assignment3.dao.Rule;
import edu.carleton.comp4601.assignment3.dao.User;
import edu.carleton.comp4601.assignment3.util.Category;
import edu.carleton.comp4601.assignment3.util.Utils;

@Path("/rs")
public class RS {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	
	final String homePath = System.getProperty("user.home");
	final String dataFolder = homePath + "/datasets/";
	final int SUPPORT = 50;
	
	private ContentAnalyzer analyzer;
	private String name;

	public RS() {
		name = "COMP4601 Restful Service: Benjamin Sweett and Brayden Girard";
		analyzer = new ContentAnalyzer();
	}

	// Gets the SDA name as a String
	@GET
	public String printName() {
		return name;
	}

	// Gets the SDA name as XML
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public String sayXML() {
		return "<?xml version=\"1.0\"?>" + "<sda> " + name + " </sda>";
	}

	// Gets the SDA name as HTML
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String sayHtml() {
		return "<html> " + "<title>" + name + "</title>" + "<body><h1>" + name
				+ "</body></h1>" + "</html> ";
	}
	
	@GET
	@Path("reset/{dir}")
	@Produces(MediaType.TEXT_HTML)
	public String reset(@PathParam("dir") String dir) {
		
		StringBuilder htmlBuilder = new StringBuilder();
		htmlBuilder.append("<html>");
		htmlBuilder.append("<head><title> Reset </title></head>");
		
		if(dir != null && !dir.isEmpty()) {
		
			SocialGraph.getInstance().clearAssignment3Data();
			SocialGraph.getInstance().clearAssignment4Data();
			DataParser parser = new DataParser(dataFolder + dir);
			parser.parseAssignment3Content();
			parser.parseAssignment4Content();
			
			htmlBuilder.append("<body><p>Reset Complete</p>");
			htmlBuilder.append("</body></html>");
			
			return htmlBuilder.toString();
			
		}
		
		htmlBuilder.append("<body><p>ERROR: Please add a directory to reset too</p>");
		htmlBuilder.append("</body></html>");
		
		return htmlBuilder.toString();
	}
	
	@GET
	@Path("context")
	@Produces(MediaType.TEXT_HTML)
	public String context() {
		
		boolean ready = SocialGraph.getInstance().isA3ParseFinished();
		StringBuilder htmlBuilder = new StringBuilder();
		
		htmlBuilder.append("<html>");
		htmlBuilder.append("<head><title> Data Context </title></head>");
		
		if(!ready) {
			
			htmlBuilder.append("<body><p>ERROR: Data is not finished parsing. Please visit /reset and wait for it to display 'Done'</p>");
			htmlBuilder.append("</body></html>");
			
			return htmlBuilder.toString();
		}
		
		analyzer.setDataGraph(SocialGraph.getInstance());
		SocialGraph updatedGraph = analyzer.run();
		SocialGraph.setInstance(updatedGraph);
		
		htmlBuilder.append("<body><table>");
		
		for(Entry<String, User> entry : SocialGraph.getInstance().getUsers().entrySet()) {
			User user = entry.getValue();
			htmlBuilder.append("<tr>" + user.toHTML() + "</tr>");
		}
		
		htmlBuilder.append("</table></body></html>");
		
		return htmlBuilder.toString();
	}
	
	@GET
	@Path("community")
	@Produces(MediaType.TEXT_HTML)
	public String community() {
		
		boolean ready = SocialGraph.getInstance().isContextReady();
		StringBuilder htmlBuilder = new StringBuilder();
		
		htmlBuilder.append("<html>");
		htmlBuilder.append("<head><title> Community </title></head>");
		
		if(!ready) {
			
			htmlBuilder.append("<body><p>ERROR: Please visit /context first and wait for profiles to be displayed</p>");
			htmlBuilder.append("</body></html>");
			return htmlBuilder.toString();
		}
		
		htmlBuilder.append("<body><table>");
		
		KMeans algo = new KMeans(new ArrayList<User>(SocialGraph.getInstance().getUsers().values()));
		List<Cluster> groups = algo.run();
		SocialGraph.getInstance().setCommunity(groups);
		SocialGraph.getInstance().setCommunityReady(true);
		
		for(int i = 0; i < groups.size(); i++) {
			Cluster cluster = groups.get(i);
			htmlBuilder.append("<tr>");
			htmlBuilder.append("<td>" + i + "</td>");
			htmlBuilder.append("<td>" + cluster.toHTML() + "</td>");
			htmlBuilder.append("</tr>");
		}
	
		htmlBuilder.append("</table></body></html>");
		
		return htmlBuilder.toString();
	}
	
	@GET
	@Path("fetch/{user}/{page}")
	@Produces(MediaType.TEXT_HTML)
	public String fetch(@PathParam("user") String user, @PathParam("page") String page) {
		
		boolean ready = SocialGraph.getInstance().isContextReady() && SocialGraph.getInstance().isA3ParseFinished() && SocialGraph.getInstance().isCommunityReady();
		StringBuilder htmlBuilder = new StringBuilder();
		
		htmlBuilder.append("<html>");
		htmlBuilder.append("<head><title> Fetch Page </title></head>");
		
		if(!ready) {
			htmlBuilder.append("<body><p>ERROR: Please visit /community first and wait for clusters to be displayed</p>");
			htmlBuilder.append("</body></html>");
			return htmlBuilder.toString();
		}
		
		
		if((user != null && !user.isEmpty()) && (page != null && !page.isEmpty())) {
			User userResult = SocialGraph.getInstance().getUserByName(user);
			Page pageResult = SocialGraph.getInstance().getPageByName(page);
			
			if(pageResult == null || userResult == null) {
				htmlBuilder.append("<body><p>404: Page or user not found</p>");
				htmlBuilder.append("</body></html>");
				return htmlBuilder.toString();
			}
			
			String ad = SocialGraph.getInstance().getAdvertForUserAndPage(userResult, pageResult);
			htmlBuilder.append("<div style=\"float: left; width: 300px;\"><p>"+ ad + "</p></div>");
			
			htmlBuilder.append(pageResult.toHTML());
			return htmlBuilder.toString();
		}
		
		htmlBuilder.append("<body><p>ERROR: Missing parameter (user or page)</p>");
		htmlBuilder.append("</body></html>");
		return htmlBuilder.toString();
	}
	
	@GET
	@Path("advertising/{category}")
	@Produces(MediaType.TEXT_HTML)
	public String advertising(@PathParam("category") String category) {
		
		boolean ready = SocialGraph.getInstance().isContextReady() && SocialGraph.getInstance().isA3ParseFinished();
		StringBuilder htmlBuilder = new StringBuilder();
		
		htmlBuilder.append("<html>");
		htmlBuilder.append("<head><title> Advertising Category </title></head>");
		
		if(!ready) {
			htmlBuilder.append("<body><p>ERROR: Please visit /context first and wait for profiles to be displayed</p>");
			htmlBuilder.append("</body></html>");
			return htmlBuilder.toString();
		}
		
		if((category != null && !category.isEmpty())) {
			try {
				int result = Integer.parseInt(category);
				Category cat = Category.fromInteger(result);
				Set<Advertisement> results = SocialGraph.getInstance().getAdFactory().getAdsForCategory(cat);
				
				if(results.size() == 0) {
					htmlBuilder.append("<body><p>No adverts found for number</p>");
					htmlBuilder.append("</body></html>");
					return htmlBuilder.toString();
				}
				
				htmlBuilder.append("<body>");
				for(Advertisement ad : results) {
					htmlBuilder.append("<div style=\"float: left; width: 500;\">"+ ad.toHTML() + "</div>");
				}
				
				htmlBuilder.append("</body></html>");
				return htmlBuilder.toString();
			} catch (NumberFormatException | NullPointerException e) {
				
				htmlBuilder.append("<body><p>ERROR: Please enter a path containing a numbered catergory (0 - 12)</p>");
				htmlBuilder.append("</body></html>");
				return htmlBuilder.toString();
			}
			
			
		}
		
		htmlBuilder.append("<body><p>ERROR: Missing parameter category</p>");
		htmlBuilder.append("</body></html>");
		return htmlBuilder.toString();
	}
	
	//Assignment 4
	
	@GET
	@Path("apriori")
	@Produces(MediaType.TEXT_HTML)
	public String apriori() {
		Apriori apriori = new Apriori(SocialGraph.getInstance().getTransactions());
		
		try {
			apriori.runApriori(SUPPORT);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ArrayList<Rule> rules = SocialGraph.getInstance().getRules();
		
		StringBuilder htmlBuilder = new StringBuilder();
		htmlBuilder.append("<html>");
		htmlBuilder.append("<head><title> All Transactions </title></head>");
		htmlBuilder.append("<body><p>The following rules were created: </p>");
		htmlBuilder.append("<ul>");
		for(Rule rule: rules) {
			htmlBuilder.append("<li>" + Arrays.toString(rule.getSetA()) + " ---> " + Arrays.toString(rule.getSetB()) + " " + rule.getConfidence() + "% confidence" + "</li>");
		}
		htmlBuilder.append("</ul>");
		htmlBuilder.append("</body>");
		htmlBuilder.append("</html>");
		
		return htmlBuilder.toString();
	}
	
	@GET
	@Path("suggest/{products}")
	@Produces(MediaType.TEXT_HTML)
	public String suggest(@PathParam("products") String products) {
		boolean ready = SocialGraph.getInstance().isA4ParseFinished();
		StringBuilder htmlBuilder = new StringBuilder();
		
		if(!ready) {
			
			htmlBuilder.append("<html>");
			htmlBuilder.append("<head><title> Retail Suggest </title></head>");
			htmlBuilder.append("<body><p>Transactions are not finished parsing. Please visit /apriori and wait for it to display</p>");
			htmlBuilder.append("</body></html>");
			
			return htmlBuilder.toString();
		} else {
			int[] productArray = Utils.stringToIntArray(products);
			ArrayList<int[]> recommendations = SocialGraph.getInstance().giveSuggestions(productArray);
			
			htmlBuilder.append("<html>");
			htmlBuilder.append("<head><title> Retail Suggest </title></head>");
			htmlBuilder.append("<body><p>Users who bought " + Arrays.toString(productArray) + " also bought the following: </p>");
			htmlBuilder.append("<ul>");
			for(int[] items: recommendations) {
				htmlBuilder.append("<li>" + Arrays.toString(items) + "</li>");
			}
			htmlBuilder.append("</ul>");
			htmlBuilder.append("</body></html>");
			return htmlBuilder.toString();
		}
	}
}
