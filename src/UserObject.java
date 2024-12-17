import java.io.Serializable;
import java.util.Objects;
import java.util.Vector;

public class UserObject implements Serializable {

	private String username;
	private String password;

	private Vector<FeedCategory> feedCategoryVec;

	private String status;

	public UserObject() {
		
		feedCategoryVec = null;
		this.username=this.status =this.password= "unknown";
	}
	
	public UserObject(String username, String password) {
		this.username = username;
		this.password = password;
		feedCategoryVec = null;
		this.status = "unknown";
	}
	
	public UserObject(String username, String password, String status) {
		this.username = username;
		this.password = password;
		this.status = status;
		feedCategoryVec = null;
		
	}

	public UserObject(String status){
		this.status = status;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Vector<FeedCategory> getFeedCategoryVec() {
		return feedCategoryVec;
	}

	public void setFeedCategoryVec(Vector<FeedCategory> feedCategoryVec) {
		this.feedCategoryVec = feedCategoryVec;
	}

	public void addFeedCategory2Vec(FeedCategory fc) {

		feedCategoryVec.add(fc);
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		return Objects.hash(username, password);
	}

	@Override
	public String toString() {
		return "UserObject [username=" + username + ", password=" + password + ", feedCategoryVec=" + feedCategoryVec
				+ ", status=" + status + "]";
	}
	
	

}
