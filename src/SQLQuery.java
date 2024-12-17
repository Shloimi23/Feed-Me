import java.security.MessageDigest;
import java.sql.*;
import java.util.ArrayList;
import java.util.Vector;

public class SQLQuery {

	private static Connection con;


	// A function that connects to a database
	public static void connect(String username, String password) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");

			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/users?useSSL=false&allowPublicKeyRetrieval=true", username, password);
		} catch (SQLException e) {

			e.printStackTrace();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
	}


	// A function that checks whether the username and password are correct.
	public static boolean isUserValid(UserObject uobj) {
		String sql = "SELECT COUNT(*) FROM users WHERE userName = ? AND password = ?";

		try (PreparedStatement pstmt = con.prepareStatement(sql)) {

			pstmt.setString(1, uobj.getUsername());
			pstmt.setString(2, uobj.getPassword());

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					int count = rs.getInt(1);
					return count > 0;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();

		}

		return false;
	}

	// A function that checks whether a username is valid.
	public static boolean isUserNameValid(UserObject uobj) {
		String sql = "SELECT COUNT(*) FROM users WHERE userName = ?";

		try (PreparedStatement pstmt = con.prepareStatement(sql)) {

			pstmt.setString(1, uobj.getUsername());


			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					int count = rs.getInt(1);
					return count > 0;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();

		}

		return false;
	}


	// New user registration
	public static boolean registerUser(UserObject uobj) {
		if (isUserNameValid(uobj)) {
			System.out.println("User already exists.");
			return false;
		}

		String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

		try (PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, uobj.getUsername());
			pstmt.setString(2, uobj.getPassword());

			int affectedRows = pstmt.executeUpdate();
			return affectedRows > 0;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}

	public static boolean registerCategory(FeedCategory category) {
		String sql = "INSERT INTO categories (category) VALUES (?)";

		try (PreparedStatement pstmt = con.prepareStatement(sql)) {

			pstmt.setString(1, category.getCategoryName());
			// make password unreadable

			int affectedRows = pstmt.executeUpdate();
			return affectedRows > 0;

		} catch (SQLException e) {
			e.printStackTrace();

			return false;
		}
	}

	// used for getUserObjectWithFeedCategory to check if category object already
	// found in vector
	private static FeedCategory findCategoryByCategoryName(Vector<FeedCategory> vector, String searchStringCategory) {
		for (FeedCategory obj : vector) {
			if (obj.getCategoryName().equals(searchStringCategory)) {
				return obj;
			}
		}
		return null;
	}

	// prepare feed category vector for user object to get back to client
	public static UserObject getUserObjectWithFeedCategory(UserObject uobj) {

		// to keep data of all category and feeds inside
		Vector<FeedCategory> feedCategoryVec = new Vector<FeedCategory>();

		// to check if we already insert category with same name
		FeedCategory isObjectFound;

		uobj.setFeedCategoryVec(feedCategoryVec);

		String sql = "SELECT feed,category FROM usersfeed WHERE user = ?";

		try (PreparedStatement pstmt = con.prepareStatement(sql)) {

			pstmt.setString(1, uobj.getUsername());

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {

					String feed = rs.getString(1);
					String category = rs.getString(2);

					System.out.println("category: " + category + ", feed: " + feed);
					// search category in vector
					isObjectFound = findCategoryByCategoryName(feedCategoryVec, category);

					// if we don't have already this category in vector
					if (isObjectFound == null) {

						// create category object and add feed object to it's vector

						feedCategoryVec.add(new FeedCategory(category));
						feedCategoryVec.lastElement().addFeedDetailsObject2Vec(new FeedDetailsObject("", feed));

					} else {

						// add new feed object to current category object
						isObjectFound.addFeedDetailsObject2Vec(new FeedDetailsObject("", feed));
					}

					isObjectFound = null; // to reset for next category search
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();

		}

		uobj.setFeedCategoryVec(feedCategoryVec);
		return uobj;

	}

	public static void deleteUserFeeds(String username) {
		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			String sql = "DELETE FROM usersfeed WHERE user = ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, username);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();

		}
	}

	public static void deleteUserAccount(String username) {
		// תחילה מחיקת כל הפידים של המשתמש
		deleteUserFeeds(username);

		// כעת מחיקת המשתמש עצמו
		String sql = "DELETE FROM users WHERE userName = ?";
		try (PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, username);
			int affectedRows = pstmt.executeUpdate();

			if (affectedRows > 0) {
				System.out.println("User account deleted successfully.");
			} else {
				System.out.println("User not found or could not be deleted.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// disconnect
	public static void disconnect() {

		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public static void insertUsersFeeds(UserObject userObject) {
		
		PreparedStatement pstmt = null;

		Vector<FeedCategory> feedCategoryVec = userObject.getFeedCategoryVec();
		try {
			String sql = "INSERT INTO usersfeed (user, feed, category) VALUES (?, ?, ?)";
			pstmt = con.prepareStatement(sql);

			for (FeedCategory feedCategory : feedCategoryVec) {
				for (FeedDetailsObject feedDetailsObjecty : feedCategory.getFeedDetailsObjectVec()) {

					pstmt.setString(1, userObject.getUsername());
					pstmt.setString(2, feedDetailsObjecty.getFeedURL());
					pstmt.setString(3, feedCategory.getCategoryName());

					pstmt.addBatch();
				}
			}
			pstmt.executeBatch();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		SQLQuery.connect("root", "12345678");


		// is user found ->works
		// boolean isFound=SQLQuery.isUserValid(new UserObject("udi", "123456"));
		// System.out.println(isFound);

		//UserObject userObj = new UserObject("shloimi", "12345");


		//userObj = SQLQuery.getUserObjectWithFeedCategory(userObj);

		//System.out.println(userObj);
		// SQLQuery.deleteUserFeeds("moshe");

		// SQLQuery.registerUser(new UserObject("avi", "ABCDEF"));

		SQLQuery.disconnect();

	}

}
