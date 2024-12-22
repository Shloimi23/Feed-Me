import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.Socket;

class ServeAClient implements Runnable {
    private Socket socket; // A connected socket

    private ObjectInputStream inputFromClient;
    private ObjectOutputStream outputToClient;


    private UserObject userObject;

    public ServeAClient(Socket socket) {
        this.socket = socket;


    }

    public void run() {
        try {
            // Create data input and output streams
            outputToClient = new ObjectOutputStream(socket.getOutputStream());
            inputFromClient = new ObjectInputStream(socket.getInputStream());

            // Continuously serve the client
            while (true) {

                try {

                    // read user from client
                    userObject = (UserObject) inputFromClient.readObject();


                    if (userObject.getStatus().equals("login")) {
                        // login error
                        if (!SQLQuery.isUserValid(userObject)) {

                            userObject.setStatus("wrong username or password");
                        }
                        // get all user feeds
                        else {
                            userObject = SQLQuery.getUserObjectWithFeedCategory(userObject);
                            System.out.println("user object" + userObject);
                        }
                    }// end login option

                    else if (userObject.getStatus().equals("register")) {
                        System.out.println("server got user:" + userObject.getUsername() + ", " + userObject.getPassword());
                        boolean isRegister = SQLQuery.registerUser(userObject);
                        if (isRegister) {
                            String originalName = userObject.getUsername();
                            userObject.setUsername("default");
                            userObject = SQLQuery.getUserObjectWithFeedCategory(userObject);
                            userObject.setUsername(originalName);
                            userObject.setStatus("user registered");
                            System.out.println("User register");
                        } else {
                            userObject.setStatus("Register invalid");
                        }


                    } // end register option
                    else if (userObject.getStatus().equals("save")) {

                        // delete all feed and add all the new feed with
                        System.out.println("data to be saved: ");
                        SQLQuery.deleteUserFeeds(userObject.getUsername());
                        SQLQuery.insertUsersFeeds(userObject);

                    } else if (userObject.getStatus().equals("Delete Account")) {
                        System.out.println("Account deleted" + userObject.getUsername());
                        SQLQuery.deleteUserAccount(userObject.getUsername());

                    }


                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }


                // Send user back to the client
                outputToClient.writeObject(userObject);
                System.out.println("server sent user:" + userObject);
                outputToClient.reset();

            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    //create for testing
    public UserObject createUserObject() {

        UserObject userObj = new UserObject("shloimi", "123456");

        userObj.addFeedCategory2Vec(new FeedCategory("hitech"));
        userObj.getFeedCategoryVec().lastElement().addFeedDetailsObject2Vec(
                new FeedDetailsObject("wallaTech", "https://rss.walla.co.il/feed/6?type=main"));
        userObj.getFeedCategoryVec().lastElement().addFeedDetailsObject2Vec(
                new FeedDetailsObject("themarker", "https://www.themarker.com/srv/tm-technation"));

        userObj.addFeedCategory2Vec(new FeedCategory("sport"));
        userObj.getFeedCategoryVec().lastElement().addFeedDetailsObject2Vec(
                new FeedDetailsObject("wallasport", "https://rss.walla.co.il/feed/3?type=main"));
        userObj.getFeedCategoryVec().lastElement().addFeedDetailsObject2Vec(
                new FeedDetailsObject("one", "https://www.one.co.il/cat/coop/xml/rss/newsfeed.aspx"));

        return userObj;

    }

}
