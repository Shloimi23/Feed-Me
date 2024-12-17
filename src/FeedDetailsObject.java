import java.io.Serializable;
import java.util.List;

class FeedDetailsObject implements Serializable {
 
   	//private static final long serialVersionUID = -7478396453816473169L;
   	private String feedCaption;
   	private String feedURL;
	
   	public FeedDetailsObject() {
   	  	  feedCaption= feedURL="unknown";
	}

	public FeedDetailsObject(String feedCaption, String feedURL) {
		super();
		this.feedCaption = feedCaption;
		this.feedURL = feedURL;
	}

	public String getFeedCaption() {
		return feedCaption;
	}

	public void setFeedCaption(String feedCaption) {
		this.feedCaption = feedCaption;
	}

	public String getFeedURL() {
		return feedURL;
	}

	public void setFeedURL(String feedURL) {
		this.feedURL = feedURL;
	}

	@Override
	public String toString() {
		return "FeedDetailsObject [feedCaption=" + feedCaption + ", feedURL=" + feedURL + "]";
	}







}

