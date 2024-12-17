import java.io.Serializable;
import java.util.Vector;

public class FeedCategory implements Serializable {

	private String categoryName;

	private Vector<FeedDetailsObject> vec = new Vector<FeedDetailsObject>();

	public FeedCategory(String categoryName) {
		this.categoryName = categoryName;

	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public Vector<FeedDetailsObject> getFeedDetailsObjectVec() {
		return vec;
	}

	public void setVec(Vector<FeedDetailsObject> vec) {
		this.vec = vec;
	}

	public void addFeedDetailsObject2Vec(FeedDetailsObject obj) {
		vec.add(obj);
	}

	public static boolean containsCategory(Vector<FeedCategory> categories, String categoryName) {
		for (FeedCategory category : categories) {
			if (category.getCategoryName().equals(categoryName)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "FeedCategory [categoryName=" + categoryName + ", vec=" + vec + "]";
	}

}
