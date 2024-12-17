import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;

class LoginWindow extends JDialog implements ActionListener {

    private JButton btnGuest, btnRegister, btnOK;
    private JTextField txtUserName;
    private JPasswordField txtPassword;
    private JLabel lblUserName, lblPassword, lblTile;
    private Font font;
    private JPanel pnlMain, pnlTitle, pnlUserName, pnlPassword, pnlButtons;
    private UserObject userObject;
    private JFrame frame;

    public LoginWindow(JFrame f, String s, UserObject u) {
        super(f,s);
        this.userObject=u;

        this.setModal(true);


        for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                try {
                    UIManager.setLookAndFeel(info.getClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException| UnsupportedLookAndFeelException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            }
        }

        //setLayout(new GridLayout(0, 2));
        font = new Font("Arial", Font.PLAIN, 14);

        // title
        pnlTitle = new JPanel();
        lblTile = new JLabel("login:");
        pnlTitle.add(lblTile);

        // user name
        pnlUserName = new JPanel(new FlowLayout());
        lblUserName = new JLabel("username:");
        txtUserName = new JTextField(15);
        pnlUserName.add(lblUserName);
        pnlUserName.add(txtUserName);

        // password
        pnlPassword = new JPanel(new FlowLayout());
        lblPassword = new JLabel("Password:");
        txtPassword = new JPasswordField(15);
        pnlPassword.add(lblPassword);
        pnlPassword.add(txtPassword);





        // buttons
        pnlButtons = new JPanel(new FlowLayout());
        btnGuest = new JButton("Guest");
        btnGuest.setBackground(Color.darkGray);
        btnOK = new JButton("OK");
        btnOK.setBackground(Color.green);
        btnRegister = new JButton("Register");
        btnRegister.setBackground(Color.orange);
        btnGuest.addActionListener(this);
        btnRegister.addActionListener(this);
        btnOK.addActionListener(this);
        pnlButtons.add(btnGuest);
        pnlButtons.add(btnRegister);
        pnlButtons.add(btnOK);

        // main panel
        pnlMain = new JPanel(new GridLayout(5, 1));
        pnlMain.add(pnlTitle);
        pnlMain.add(pnlUserName);
        pnlMain.add(pnlPassword);

        pnlMain.add(pnlButtons);


        setContentPane(pnlMain);

        setLocation(400, 300);
        pack();
        setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnGuest) {
            userObject.setUsername("default");
            userObject.setPassword(FeedMeApp.hashPassword("1"));
            userObject.setStatus("login");

            this.dispose();
        }
        if(e.getSource() == btnOK) {
            userObject.setUsername(txtUserName.getText());
            userObject.setPassword(FeedMeApp.hashPassword(new String(txtPassword.getPassword())));
            userObject.setStatus("login");

            this.dispose();
        }
        if(e.getSource() == btnRegister) {
            userObject.setStatus("Register");
            //userObject = new RegisterWindow(frame,"Register", userObject).getUserObject();

            this.dispose();  // סוגר את החלון לאחר ההרשמה
        }

    }

    public UserObject getUserObject() {
        return userObject;
    }

    public void setUserObject(UserObject u) {
        this.userObject = u;
    }



}