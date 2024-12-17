import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;

class RegisterWindow extends JDialog implements ActionListener {

    private JButton btnExit, btnRegister, btnOK;
    private JTextField txtUserName;
    private JPasswordField txtPassword, txtRePassword;
    private JLabel lblUserName, lblPassword, lblRePassword, lblTile;
    private Font font;
    private JPanel pnlMain, pnlTitle, pnlUserName, pnlPassword ,pnlrepassword, pnlButtons;
    private UserObject userObject;
    private JFrame frame;

    public RegisterWindow(JFrame f, String s, UserObject u) {
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
        lblTile = new JLabel("Register");
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

        pnlrepassword = new JPanel(new FlowLayout());
        lblRePassword = new JLabel("Re Password:");
        txtRePassword = new JPasswordField(15);
        pnlrepassword.add(lblRePassword);
        pnlrepassword.add(txtRePassword);





        // buttons
        pnlButtons = new JPanel(new FlowLayout());
        btnExit = new JButton("Exit");
        btnExit.setBackground(Color.red);
        btnOK = new JButton("OK");
        btnOK.setBackground(Color.green);
        btnExit.addActionListener(this);
        btnOK.addActionListener(this);
        pnlButtons.add(btnExit);
        pnlButtons.add(btnOK);

        // main panel
        pnlMain = new JPanel(new GridLayout(6, 1));
        pnlMain.add(pnlTitle);
        pnlMain.add(pnlUserName);
        pnlMain.add(pnlPassword);
        pnlMain.add(pnlrepassword);
        pnlMain.add(pnlButtons);


        setContentPane(pnlMain);

        setLocation(400, 300);
        pack();
        setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnExit)
            System.exit(0);
        if(e.getSource() == btnOK) {



            userObject.setStatus("register");
            if(Arrays.equals(txtPassword.getPassword(), txtRePassword.getPassword())){
            String usernameInput = txtUserName.getText();
            String passwordInput = new String(txtPassword.getPassword());
                if (passwordLength(passwordInput)) {    // לפחות ספרה
                    JOptionPane.showMessageDialog(this,
                            "Password must be at least 8 characters long, contain a lowercase letter, an uppercase letter, and a digit.",
                            "Invalid Password", JOptionPane.ERROR_MESSAGE);
                    return; // אם הסיסמה לא עברה את הבדיקות, לא נמשיך להרשמה
                }
            passwordInput = FeedMeApp.hashPassword(passwordInput);





            if (Character.isDigit(usernameInput.charAt(0))) {
                JOptionPane.showMessageDialog(this,
                        "Username cannot start with a number.",
                        "Invalid Username", JOptionPane.ERROR_MESSAGE);
                return; // אם שם המשתמש מתחיל בספרה, לא נמשיך להרשמה
            }

            // בדיקת אם הסיסמה ארוכה מ-8 תווים וכוללת אות גדולה, אות קטנה וספרה



            // אם הסיסמה תקינה, תמשיך להירשם
            userObject.setUsername(usernameInput);
            userObject.setPassword(passwordInput);
            userObject.setStatus("register");

            this.dispose();  // סוגר את החלון לאחר ההרשמה
        }}
        if (!Arrays.equals(txtPassword.getPassword(), txtRePassword.getPassword())) {
            JOptionPane.showMessageDialog(this,
                    "Wrong Password",
                    "Password", JOptionPane.ERROR_MESSAGE);
            System.out.println(txtPassword.getPassword());
            System.out.println(txtRePassword.getPassword());
        }
            }

    private static boolean passwordLength(String string){
        if(string.length() > 7 && string.matches(".*[A-Z].*") && string.matches(".*[a-z].*") && string.matches(".*\\d.*")) {
            return false;
        }
        return true;

    }



    public UserObject getUserObject() {
        return userObject;
    }

    public void setUserObject(UserObject u) {
        this.userObject = u;
    }



}