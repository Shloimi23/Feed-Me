import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.TitledBorder;

class DataChannelPanel extends JPanel implements MouseListener {

	Vector<JLabel> lblVector = new Vector<JLabel>();
	TitledBorder border; // panel border will be the description of feed
	JLabel lblTemp;
	Font font;

	Iterator iterator;
	Article article;
	RSSFeedParser parser;
	Feed feed;
	Boolean hasFeeds = false;

	String searchString;

	public DataChannelPanel(String rssURL) {

		setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 5));

		font = new Font("Arial", Font.PLAIN, 18);

		setOpaque(true);

	try {
		parser = new RSSFeedParser(rssURL);
		feed = parser.readFeed();


		// create panel border with feed description
		border = new TitledBorder(feed.getDescription());
		this.setBorder(border);
	}catch(NullPointerException e){
		JOptionPane.showMessageDialog(null, "Illegal feed address");
	}
		addAllArticlesToPanel();

	}

	// for search option
	public DataChannelPanel(String rssURL, String searchString) {

		setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 5));

		font = new Font("Arial", Font.PLAIN, 18);

		setOpaque(true);


			parser = new RSSFeedParser(rssURL);
			feed = parser.readFeed();


		// create panel border with feed description
		border = new TitledBorder(feed.getDescription());
		this.setBorder(border);

		addSearchArticlesToPanel(searchString);

	}

	public static boolean findIfPresent(String source, HashSet<String> set)
	{
		if (set.contains(source)) {
			for (String obj : set) {
				if (obj.equals(source))
					return false;
			}
		}

		return true;
	}
	public void updateLabelByArticle(JLabel label, Article article) {

		// set title on labels
		label = new JLabel(article.getTitle(), SwingConstants.RIGHT);

		label.setOpaque(true);

		// keep background colors bright
		label.setBackground(new Color(

				(int) (100 + Math.random() * 155), (int) (100 + Math.random() * 155), (int) (100 + Math.random() * 155)

		));
		label.setForeground(Color.black);

		// set description on tooltip -> the text is: message.getDescription()
		// lblTemp.setToolTipText();
		FontMetrics fontMetrics = label.getFontMetrics(label.getFont());
		int length = fontMetrics.stringWidth(article.getDescription());
		label.setToolTipText("<html><p align=right width=\"" + (length > 350 ? 350 : length) + "px\">"
				+ article.getDescription() + "</p></html>\"");

		UIManager.put("ToolTip.font", new Font("Arial", Font.BOLD, 16));

		// set font
		label.setFont(font);

		// add labels to vector
		lblVector.addElement(label);

		// add label to screen
		add(label);

		String feedMessage = article.getLink();

		label.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent arg0) {

				try {
					openWebpage(new URI(feedMessage));
					System.out.println("wep page" + feedMessage);
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});

	}

	public boolean hasFeeds(){
		return this.hasFeeds;
	}

	public void addSearchArticlesToPanel(String searchString) {
		// iterator for moving on feeds
		iterator = feed.getMessages().iterator();

		// get the first feed
		article = (Article) iterator.next();

		// move along all messages where title or description contain searchString
		for (; iterator.hasNext()  ; article = (Article) iterator.next()) {
			if ((article.getTitle().contains(searchString)
					|| article.getDescription().contains(searchString))){
				hasFeeds = true;
				updateLabelByArticle(lblTemp, article);

		}}
	}

	public void addAllArticlesToPanel() {

		// iterator for moving on feeds
		iterator = feed.getMessages().iterator();

		// get the first feed
		article = (Article) iterator.next();

		// move along all messages
		for (; iterator.hasNext(); article = (Article) iterator.next()) {
			updateLabelByArticle(lblTemp, article);
		}
	}

	public Feed getFeed() {
		return feed;
	}

	public static boolean openWebpage(URI uri) {
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				desktop.browse(uri);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	// when click on title - open Internet page with feed url
	@Override
	public void mousePressed(MouseEvent arg0) {
		try {
			Process p = Runtime.getRuntime()
					.exec("C:\\Program Files\\Internet Explorer\\iexplore.exe \"" + article.getLink() + "\"");

			System.out.println("open link" +article);

		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}

}