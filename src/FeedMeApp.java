import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

public class FeedMeApp extends JFrame {

	JPanel mainPanel, upper, middle, leftPanel;
	JMenuItem deleteAccountItem,removeLastFeed,save;
	JTextField txtAddURL;
	Vector<DataChannelPanel> datapanelVec;
	JScrollPane scroller;
	private JTree tree;
	DefaultMutableTreeNode root;
	DefaultMutableTreeNode nodeToAdd;
	String action;
	String user;
	String password;
	private static UserObject userObject;
	private static Client client = new Client();
	static Set<String> rssUrlList = new HashSet<String>();
	// Setting up the search field and search button
	private JTextField searchField;
	private JButton searchButton;
	private HashSet<DataChannelPanel> datapanelSet;
	public FeedMeApp(String s) {
		super(s);

		ToolTipManager.sharedInstance().setInitialDelay(200);
		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
		action = "login";
		user = "default";
		password = "1";
		userObject = new UserObject(user, password, action);
		datapanelVec = new Vector<DataChannelPanel>();
		datapanelVec = new Vector<DataChannelPanel>();
		txtAddURL = new JTextField("Enter here a new feed and then press enter.");
		txtAddURL.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nodeToAdd = new DefaultMutableTreeNode(txtAddURL.getText());
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				node.add(nodeToAdd);
				DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
				DefaultMutableTreeNode theroot = (DefaultMutableTreeNode) model.getRoot();
				model.reload(theroot);
				datapanelVec.addElement(new DataChannelPanel(txtAddURL.getText()));
				middle.add(datapanelVec.lastElement(), BorderLayout.NORTH);
				middle.setOpaque(false);
				validate();
			}
		});

		// Creating the main menu
		JMenuBar menuBar = new JMenuBar();

		// Create a new menu called "Account"
		JMenu accountMenu = new JMenu("File");

		// Create a menu option to delete an account
		deleteAccountItem = new JMenuItem("Delete Account");
		removeLastFeed = new JMenuItem("Remove Last Feed");
		save = new JMenuItem("Save");

		// Adding a listener for the account deletion option
		deleteAccountItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteAccount();


			}
		});


		// Adding a listener for the remove last feed option
		removeLastFeed.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeLast();


			}
		});


		// Adding a listener for the save option
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveFeeds();


			}
		});


		// Adding a listener to the main window
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				int choice = JOptionPane.showConfirmDialog(
						null,
						"Are you sure you want to close this window?",
						"Close Window?",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE
				);

				if (choice == JOptionPane.YES_OPTION) {
					if (userObject.getStatus().equals("user registered")){
						saveFeeds();
						System.exit(0);
					}
					else {
						System.exit(0);
					}
				} else {

				}
			}
		});


		// Adding the option to the "Account" menu and adding it to the main menu
		accountMenu.add(deleteAccountItem);
		accountMenu.add(removeLastFeed);
		accountMenu.add(save);
		menuBar.add(accountMenu);

		setJMenuBar(menuBar);

		setTitle("FeedMe Application");
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(null);

		mainPanel = new JPanel(new BorderLayout());
		leftPanel = new JPanel(new GridLayout(0, 1));


		upper = new JPanel(new BorderLayout());
		upper.add(txtAddURL, BorderLayout.NORTH);
		mainPanel.add(upper, BorderLayout.NORTH);

		middle = new JPanel(new GridLayout(7, 2));
		middle.setPreferredSize(new Dimension(1000, 3000));
		middle.validate();


		scroller = new JScrollPane(middle, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		mainPanel.add(scroller, BorderLayout.CENTER);
		mainPanel.add(leftPanel, BorderLayout.WEST);

		scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		setContentPane(mainPanel);
		setExtendedState(JFrame.MAXIMIZED_BOTH);

		client.writeUserObjectToServer(userObject);
		userObject = client.readUserObjectFromServer();


		// Inside the FeedMeApp builder - creating the search field and button and adding them to the upper panel
		searchField = new JTextField(20);
		searchButton = new JButton("Search");

		searchField.setBackground(Color.white);
		searchButton.setBackground(Color.white);
		searchButton.setForeground(Color.darkGray);
		searchButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		searchButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));


		// Adding a Listener to the Search Button
		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				datapanelVec.removeAllElements();
				middle.removeAll();

				for (int i = 0; i < userObject.getFeedCategoryVec().size(); i++)
					for (int j = 0; j < userObject.getFeedCategoryVec().get(i).getFeedDetailsObjectVec().size(); j++) {


						datapanelVec.addElement(new DataChannelPanel(
								userObject.getFeedCategoryVec().get(i).getFeedDetailsObjectVec().get(j).getFeedURL(),
								searchField.getText()));
						if (datapanelVec.lastElement().hasFeeds())
							middle.add(datapanelVec.lastElement(), BorderLayout.NORTH);
						validate();
					}

			}
		});


		// Adding the search field and button to the top panel
		JPanel searchPanel = new JPanel();
		searchPanel.add(searchField);
		searchPanel.add(searchButton);
		upper.add(searchPanel, BorderLayout.NORTH);

		setVisible(true);

		userObject = new LoginWindow(this, "Login", userObject).getUserObject();
		client.writeUserObjectToServer(userObject);
		userObject = client.readUserObjectFromServer();

		while(userObject.getStatus().equals("wrong username or password")){
			wornLogin();
		}


		if (userObject.getStatus().equals("Register")) {
			statusRegister();
			while(!userObject.getStatus().equals("user registered")){
				wornRegister();
			}
		}
		System.out.println("running...");
		System.out.println(userObject);

		client.writeUserObjectToServer(userObject);
		userObject = client.readUserObjectFromServer();

		if (userObject.getFeedCategoryVec() != null)
			initializeTree(userObject);
	}


	// A function that deletes a user and returns the default user instead.
	private void deleteAccount() {
		if (!userObject.getUsername().equals("default")){
		int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the account?");



		if (confirm == JOptionPane.YES_OPTION) {
			deleteUser();
			JOptionPane.showMessageDialog(this, "Your account has been deleted");

			userObject.setUsername("default");
			userObject.setPassword(hashPassword("1"));
			userObject.setStatus("login");
			client.writeUserObjectToServer(userObject);
			userObject = client.readUserObjectFromServer();
			leftPanel.removeAll();
			initializeTree(userObject);
			leftPanel.validate();


		}
	}
		else
			JOptionPane.showMessageDialog(this, "you can not delete default account");
	}


	// A function that sends a user deletion status to the server.
	private void deleteUser() {
		userObject.setStatus("Delete Account");
		client.writeUserObjectToServer(userObject);
		userObject = client.readUserObjectFromServer();
	}


	// A function that removes the last feed from the display
	public void removeLast(){
		if (datapanelVec.size() != 0) {
			middle.remove(datapanelVec.lastElement());
			datapanelVec.remove(datapanelVec.lastElement());
			middle.validate();
			middle.repaint();
		}
	}


	// A function that deletes all existing feeds and saves all feeds back, to prevent duplication of feeds.
	public void saveFeeds(){
		Vector<FeedCategory> userObjectFeedCategoryVec = userObject.getFeedCategoryVec();
		if (userObjectFeedCategoryVec != null) {
			userObjectFeedCategoryVec.removeAllElements();
			System.out.println(userObject + "remove");
		}

		traverseTreeToCreateUserObject(root);
		System.out.println(userObject.getFeedCategoryVec());
		userObject.setStatus("save");
		client.writeUserObjectToServer(userObject);
	}


	// Function to create a password hash
	public static String hashPassword(String password) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashedBytes = digest.digest(password.getBytes());

			// Converting the Hash to Hexadecimal Format
			StringBuilder hexString = new StringBuilder();
			for (byte b : hashedBytes) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();

		} catch (Exception e) {
			throw new RuntimeException("Error hashing password", e);
		}
	}


	// A function that pops up a message stating that there is an error in logging in and sends the status to the server.
	public void wornLogin(){
		JOptionPane.showMessageDialog(this, "wrong username or password");
		userObject = new LoginWindow(this, "Login", userObject).getUserObject();
		client.writeUserObjectToServer(userObject);
		userObject = client.readUserObjectFromServer();
	}


	// A function that sends a registration status message to the server.
	public void statusRegister(){
		userObject = new RegisterWindow(this, "Register", userObject).getUserObject();
		client.writeUserObjectToServer(userObject);
		userObject = client.readUserObjectFromServer();
	}


	// A function that pops up a message saying there is an error in registration
	public void wornRegister(){
		JOptionPane.showMessageDialog(this,
				"This username already exists. Please choose a different username.",
				"Username Taken", JOptionPane.ERROR_MESSAGE);
		userObject = new RegisterWindow(this, "Register", userObject).getUserObject();
		client.writeUserObjectToServer(userObject);
		userObject = client.readUserObjectFromServer();
	}


	// Creating the tree and entering data
	public void initializeTree(UserObject uo) {
		root = new DefaultMutableTreeNode(uo.getUsername() + "'s " + "feeds");
		DefaultMutableTreeNode dmtn;
		for (int i = 0; i < uo.getFeedCategoryVec().size(); i++) {
			dmtn = new DefaultMutableTreeNode(uo.getFeedCategoryVec().get(i).getCategoryName());
			root.add(dmtn);
			for (int j = 0; j < uo.getFeedCategoryVec().elementAt(i).getFeedDetailsObjectVec().size(); j++) {
				dmtn.add(new DefaultMutableTreeNode(
						uo.getFeedCategoryVec().elementAt(i).getFeedDetailsObjectVec().elementAt(j).getFeedURL()));
			}
		}

		tree = new JTree(root);
		DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) tree.getCellRenderer();
		renderer.setFont(new Font("Arial", Font.PLAIN, 20));
		tree.setCellRenderer(renderer);
		//tree.setBackground(Color.BLACK);
		tree.setOpaque(false);
		leftPanel.add(tree);

		renderer.setFont(new Font("Verdana", Font.ITALIC, 16));
		tree.setCellRenderer(renderer);

		// Drag and Drop setup for the tree
		tree.setDragEnabled(true);
		tree.setDropMode(DropMode.ON_OR_INSERT);
		tree.setTransferHandler(new TreeTransferHandler());



		JPopupMenu popupMenu = new JPopupMenu();

		// New item in the Add Delete Node contextual menu
		JMenuItem deleteItem = new JMenuItem("Delete Node");
		deleteItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteSelectedNode();
			}
		});
		popupMenu.add(deleteItem);

		// New item in the Add Rename contextual menu
		JMenuItem renameItem = new JMenuItem("Rename");
		renameItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				renameSelectedNode();
			}
		});
		popupMenu.add(renameItem);

		// New item in the Add Category contextual menu
		JMenuItem addCategoryItem = new JMenuItem("Add category");
		addCategoryItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				addCategory();
			}
		});
		popupMenu.add(addCategoryItem);


		// New item in the Add Feed contextual menu
		JMenuItem addFeedItem = new JMenuItem("Add Feed");
		addFeedItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addFeedToCategory();
			}
		});
		popupMenu.add(addFeedItem);


		// Adding a mouse click listener to the tree
		tree.addMouseListener(new MouseAdapter() {

			// Listens for right mouse click in the tree
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					int row = tree.getClosestRowForLocation(e.getX(), e.getY());
					tree.setSelectionRow(row);
					popupMenu.show(tree, e.getX(), e.getY());
				}
			}


			// Listening for mouse clicks in the tree
			public void mouseClicked(MouseEvent me) {
				Object nodeInfo = null;
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				if (node != null && node.isLeaf()) {
					nodeInfo = node.getUserObject();
					if (rssUrlList.contains(nodeInfo.toString())) {
						return;
					}
					else {
						System.out.println(nodeInfo.toString());
						datapanelVec.addElement(new DataChannelPanel(nodeInfo.toString()));
						middle.add(datapanelVec.lastElement(), BorderLayout.NORTH);
						validate();
						rssUrlList.add(nodeInfo.toString());
					}
				}
			}
		});
	}


	// Category renaming function
	private void renameSelectedNode() {
		TreePath selectedPath = tree.getSelectionPath();
		if (selectedPath == null) {
			JOptionPane.showMessageDialog(this, "Please select a node to rename.");
			return;
		}

		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
		String currentName = selectedNode.getUserObject().toString();
		String newName = JOptionPane.showInputDialog(this, "Enter new name:", currentName);

		if (newName != null && !newName.trim().isEmpty()) {
			selectedNode.setUserObject(newName);
			DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
			model.nodeChanged(selectedNode); // Updating the model to display the new name
		} else {
			JOptionPane.showMessageDialog(this, "Invalid name. Please try again.");
		}
	}


	// Function to add a category to the tree
	private void addCategory() {
		String categoryName = JOptionPane.showInputDialog(this, "Enter name for new category:");

		if (categoryName != null && !categoryName.trim().isEmpty()) {
			DefaultMutableTreeNode parentNode;

			TreePath selectedPath = tree.getSelectionPath();

			parentNode = root;

			DefaultMutableTreeNode newCategoryNode = new DefaultMutableTreeNode(categoryName);
			DefaultTreeModel model = (DefaultTreeModel) tree.getModel();

			// Adding the new category to the data structure
			model.insertNodeInto(newCategoryNode, parentNode, parentNode.getChildCount());

			// Expand the parent category to display the new category
			tree.expandPath(new TreePath(parentNode.getPath()));
		} else {
			JOptionPane.showMessageDialog(this, "Invalid category name. Please try again.");
		}
	}

	// Function for adding feeds to categories
	private void addFeedToCategory() {
		TreePath selectedPath = tree.getSelectionPath();
		if (selectedPath == null) {
			JOptionPane.showMessageDialog(this, "Please select a category to add a feed.");
			return;
		}

		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();

		// Checking if the selected node is a category (and not the main branch)
		if (selectedNode == root) {
			JOptionPane.showMessageDialog(this, "You cannot add a feed to the root node. Please select a category.");
			return;
		}

		// Request feed name from user
		String feedName = JOptionPane.showInputDialog(this, "Enter name for new feed:");
		if (feedName == null || feedName.trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Feed name cannot be empty.");
			return;
		}

		// Creating a new feed as a Node and adding it to the selected category
		DefaultMutableTreeNode newFeedNode = new DefaultMutableTreeNode(feedName);
		selectedNode.add(newFeedNode);

		// Updating the model to show the changes in the tree
		((DefaultTreeModel) tree.getModel()).reload(selectedNode);
	}


	// Delete an item from the tree
	private void deleteSelectedNode() {
		TreePath selectedPath = tree.getSelectionPath();
		if (selectedPath != null) {
			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
			if (selectedNode.getParent() != null) {
				DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
				model.removeNodeFromParent(selectedNode);
			} else {
				JOptionPane.showMessageDialog(this, "Cannot delete the root node.");
			}
		} else {
			JOptionPane.showMessageDialog(this, "Please select a node to delete.");
		}
	}


	// Scanning the tree for saving and sending to databases
	public static void traverseTreeToCreateUserObject(DefaultMutableTreeNode root) {
		Enumeration<TreeNode> e = root.preorderEnumeration();
		while (e.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
			if (node.toString().equals(userObject.getUsername() + "'s feeds"))
				continue;

			if (node.isLeaf()) {
				System.out.println("add feed: " + node.toString());
				userObject.getFeedCategoryVec().lastElement()
						.addFeedDetailsObject2Vec(new FeedDetailsObject("", node.toString()));
			} else {
				System.out.println("add category: " + node.toString());
				userObject.getFeedCategoryVec().add(new FeedCategory(node.toString()));
			}
		}
		System.out.println(userObject);
	}



	public static void main(String args[]) {
		try {
			UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatDarculaLaf());
			FeedMeApp rt = new FeedMeApp("FeedMe App");
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}


	}

	// מחלקת TransferHandler מותאמת אישית לגרירה ושחרור של Nodes בתוך JTree
	class TreeTransferHandler extends TransferHandler {
		private DefaultMutableTreeNode draggedNode;

		public int getSourceActions(JComponent c) {
			return MOVE;
		}

		@Override
		protected Transferable createTransferable(JComponent c) {
			JTree tree = (JTree) c;
			draggedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent(); // שמירת הענף שנגרר
			return new StringSelection(draggedNode.toString());
		}

		@Override
		public boolean canImport(TransferHandler.TransferSupport support) {
			if (!support.isDrop())
				return false;
			support.setShowDropLocation(true);

			JTree.DropLocation dropLocation = (JTree.DropLocation) support.getDropLocation();
			TreePath path = dropLocation.getPath();

			// בדיקה שה-Node אליו גוררים קיים ולא מדובר בצאצא של עצמו
			return path != null && draggedNode != null
					&& !draggedNode.isNodeAncestor((DefaultMutableTreeNode) path.getLastPathComponent());
		}

		@Override
		public boolean importData(TransferHandler.TransferSupport support) {
			if (!canImport(support))
				return false;

			JTree.DropLocation dropLocation = (JTree.DropLocation) support.getDropLocation();
			TreePath destinationPath = dropLocation.getPath();
			DefaultMutableTreeNode newParentNode = (DefaultMutableTreeNode) destinationPath.getLastPathComponent();

			// ביצוע ההעברה של הענף הנגרר לקטגוריה החדשה
			DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
			model.removeNodeFromParent(draggedNode); // הסרתו מהמיקום הישן
			model.insertNodeInto(draggedNode, newParentNode, newParentNode.getChildCount()); // הוספתו לקטגוריה החדשה

			// הרחבת ה-Node החדש להצגת הענף שהתווסף
			tree.expandPath(destinationPath);
			tree.setSelectionPath(new TreePath(draggedNode.getPath()));

			return true;
		}
	}
}