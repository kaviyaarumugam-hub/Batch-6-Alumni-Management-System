import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/*
 * Alumni Management System
 * Java AWT desktop application.
 * Data is stored temporarily in ArrayLists while the application is running.
 */
public class AlumniManagementAWT extends Frame {

    private class Alumni {
        String name, dept, regNo, batch, passYear;
        String position, company, workYear;
        String email, phone, password;

        Alumni(String[] d) {
            name = d[0];
            dept = d[1];
            regNo = d[2];
            batch = d[3];
            passYear = d[4];
            position = d[5];
            company = d[6];
            workYear = d[7];
            email = d[8];
            phone = d[9];
            password = d[10];
        }
    }

    private final ArrayList<Alumni> alumniList = new ArrayList<>();
    private final ArrayList<String> announcements = new ArrayList<>();
    private final ArrayList<String> notifications = new ArrayList<>();
    private final ArrayList<String> messages = new ArrayList<>();
    private final ArrayList<String> updates = new ArrayList<>();

    private final CardLayout cards = new CardLayout();
    private final Panel root = new Panel(cards);

    private TextField loginEmail, loginPassword;
    private TextArea output;
    private String role = "";
    private String currentUser = "";

    public AlumniManagementAWT() {
        setTitle("Alumni Management System");
        setSize(900, 700);
        setLocationRelativeTo(null);

        root.add(welcomePage(), "welcome");
        root.add(loginPage(), "login");
        root.add(signupPage(), "signup");
        root.add(dashboardPage(), "dashboard");

        add(root);
        cards.show(root, "welcome");

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setVisible(true);
    }

    private Panel welcomePage() {
        Panel p = new Panel(new GridLayout(6, 1, 15, 15));

        Label title = new Label("ALUMNI MANAGEMENT SYSTEM", Label.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));

        Button signUp = new Button("SIGN UP");
        Button logIn = new Button("LOG IN");

        p.add(title);
        p.add(new Label("Welcome to Our College Alumni Network", Label.CENTER));
        p.add(new Label("New Alumni? Create Your Account", Label.CENTER));
        p.add(signUp);
        p.add(new Label("Already Registered? Login", Label.CENTER));
        p.add(logIn);

        signUp.addActionListener(e -> cards.show(root, "signup"));
        logIn.addActionListener(e -> cards.show(root, "login"));
        return p;
    }

    private Panel loginPage() {
        Panel p = new Panel(new GridLayout(7, 1, 10, 10));

        Label title = new Label("ALUMNI LOGIN", Label.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 25));

        loginEmail = new TextField();
        loginPassword = new TextField();
        loginPassword.setEchoChar('*');

        Button login = new Button("LOG IN");
        Button back = new Button("BACK");

        p.add(title);
        p.add(new Label("Email ID (Admin username: admin)"));
        p.add(loginEmail);
        p.add(new Label("Password"));
        p.add(loginPassword);
        p.add(login);
        p.add(back);

        login.addActionListener(e -> loginUser());
        back.addActionListener(e -> cards.show(root, "welcome"));
        return p;
    }

    private Panel signupPage() {
        Panel p = new Panel(new GridLayout(13, 1, 5, 5));
        Label title = new Label("ALUMNI REGISTRATION", Label.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));

        TextField[] fields = new TextField[11];
        String[] labels = {
            "Full Name", "Department", "Register Number", "Batch",
            "Year of Passing", "Current Position", "Company Name",
            "Current Working Year", "Email ID", "Phone Number", "Password"
        };

        p.add(title);
        for (int i = 0; i < labels.length; i++) {
            Panel row = new Panel(new BorderLayout());
            row.add(new Label(labels[i]), BorderLayout.NORTH);
            fields[i] = new TextField();
            if (i == 10) {
                fields[i].setEchoChar('*');
            }
            row.add(fields[i], BorderLayout.CENTER);
            p.add(row);
        }

        Panel buttons = new Panel();
        Button register = new Button("REGISTER");
        Button back = new Button("BACK");
        buttons.add(register);
        buttons.add(back);
        p.add(buttons);

        register.addActionListener(e -> {
            String[] data = new String[11];
            for (int i = 0; i < fields.length; i++) {
                data[i] = fields[i].getText().trim();
            }

            if (isEmpty(data)) {
                showMessage("Please Fill All Details!");
                return;
            }

            for (Alumni a : alumniList) {
                if (a.email.equalsIgnoreCase(data[8]) ||
                    a.regNo.equalsIgnoreCase(data[2])) {
                    showMessage("Email or Register Number Already Exists!");
                    return;
                }
            }

            alumniList.add(new Alumni(data));
            showMessage("Registration Successful!");
            for (TextField field : fields) {
                field.setText("");
            }
            cards.show(root, "login");
        });

        back.addActionListener(e -> cards.show(root, "welcome"));
        return p;
    }

    private boolean isEmpty(String[] data) {
        if (data == null) return true;
        for (String value : data) {
            if (value == null || value.trim().isEmpty()) return true;
        }
        return false;
    }

    private void loginUser() {
        String email = loginEmail.getText().trim();
        String pass = loginPassword.getText();

        if (email.equalsIgnoreCase("admin") && pass.equals("admin123")) {
            role = "Admin";
            currentUser = "admin";
            showDashboard();
            return;
        }

        for (Alumni a : alumniList) {
            if (a.email.equalsIgnoreCase(email) && a.password.equals(pass)) {
                role = "Alumni";
                currentUser = a.email;
                showDashboard();
                return;
            }
        }
        showMessage("Invalid Email or Password!");
    }

    private Panel dashboardPage() {
        Panel p = new Panel(new BorderLayout());
        Label heading = new Label("ALUMNI MANAGEMENT DASHBOARD", Label.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 25));
        output = new TextArea();
        output.setEditable(false);
        p.add(heading, BorderLayout.NORTH);
        p.add(output, BorderLayout.CENTER);
        return p;
    }

    private void showDashboard() {
        Panel p = (Panel) root.getComponent(3);
        Panel menu = new Panel(new GridLayout(0, 3, 5, 5));

        if (role.equals("Admin")) {
            addButton(menu, "Dashboard", this::dashboardDetails);
            addButton(menu, "Add Alumni", this::addAlumni);
            addButton(menu, "View Alumni", this::viewAlumni);
            addButton(menu, "Update Alumni", this::updateAlumni);
            addButton(menu, "Delete Alumni", this::deleteAlumni);
            addButton(menu, "Search Alumni", this::searchAlumni);
            addButton(menu, "Add Announcement", this::addAnnouncement);
            addButton(menu, "View Announcements", this::viewAnnouncements);
            addButton(menu, "Delete Announcement", this::deleteAnnouncement);
            addButton(menu, "Send Message", this::sendMessage);
            addButton(menu, "Share College Updates", this::shareUpdate);
            addButton(menu, "View Messages", this::viewMessages);
        } else {
            addButton(menu, "Dashboard", this::dashboardDetails);
            addButton(menu, "My Profile", this::myProfile);
            addButton(menu, "Update My Profile", this::updateMyProfile);
            addButton(menu, "View Alumni", this::viewAlumni);
            addButton(menu, "Search Alumni", this::searchAlumni);
            addButton(menu, "View Announcements", this::viewAnnouncements);
            addButton(menu, "View Notifications", this::viewNotifications);
            addButton(menu, "View College Updates", this::viewUpdates);
            addButton(menu, "Send Message", this::sendMessage);
            addButton(menu, "View Messages", this::viewMessages);
        }

        addButton(menu, "Contact Alumni", this::contactAlumni);
        addButton(menu, "Logout", this::logout);

        p.removeAll();
        Label heading = new Label("ALUMNI MANAGEMENT DASHBOARD", Label.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 24));
        output = new TextArea();
        output.setEditable(false);
        p.add(heading, BorderLayout.NORTH);
        p.add(output, BorderLayout.CENTER);
        p.add(menu, BorderLayout.SOUTH);

        dashboardDetails();
        p.validate();
        p.repaint();
        cards.show(root, "dashboard");
    }

    private void addButton(Panel panel, String name, Runnable action) {
        Button button = new Button(name);
        panel.add(button);
        button.addActionListener(e -> action.run());
    }

    private void dashboardDetails() {
        output.setText(
            "WELCOME " + currentUser + "\n\n" +
            "User Type: " + role + "\n\n" +
            "Total Alumni: " + alumniList.size() + "\n" +
            "Total Announcements: " + announcements.size() + "\n" +
            "Total College Updates: " + updates.size() + "\n\n" +
            "Welcome to the College Alumni Network!"
        );
    }

    private String[] ask(String title, String... labels) {
        Dialog dialog = new Dialog(this, title, true);
        dialog.setLayout(new GridLayout(labels.length + 1, 2, 5, 5));

        TextField[] fields = new TextField[labels.length];
        for (int i = 0; i < labels.length; i++) {
            dialog.add(new Label(labels[i]));
            fields[i] = new TextField();
            dialog.add(fields[i]);
        }

        final String[][] result = {null};
        Button save = new Button("SAVE");
        Button cancel = new Button("CANCEL");
        dialog.add(save);
        dialog.add(cancel);

        save.addActionListener(e -> {
            result[0] = new String[fields.length];
            for (int i = 0; i < fields.length; i++) {
                result[0][i] = fields[i].getText().trim();
            }
            dialog.dispose();
        });
        cancel.addActionListener(e -> dialog.dispose());

        dialog.setSize(450, Math.max(250, labels.length * 55));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        return result[0];
    }

    private void showMessage(String message) {
        Dialog dialog = new Dialog(this, "Message", true);
        dialog.setLayout(new FlowLayout());
        dialog.add(new Label(message));
        Button ok = new Button("OK");
        ok.addActionListener(e -> dialog.dispose());
        dialog.add(ok);
        dialog.setSize(350, 130);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void addAlumni() {
        String[] d = ask("Add Alumni", "Name", "Department", "Register Number",
            "Batch", "Pass Out Year", "Position", "Company", "Working Year",
            "Email", "Phone", "Password");
        if (isEmpty(d)) return;

        for (Alumni a : alumniList) {
            if (a.email.equalsIgnoreCase(d[8]) || a.regNo.equalsIgnoreCase(d[2])) {
                showMessage("Duplicate Email or Register Number!");
                return;
            }
        }
        alumniList.add(new Alumni(d));
        showMessage("Alumni Added Successfully!");
    }

    private void viewAlumni() {
        output.setText("REGISTERED ALUMNI DETAILS\n\n");
        for (Alumni a : alumniList) {
            output.append("Name: " + a.name + "\n");
            output.append("Department: " + a.dept + "\n");
            output.append("Register Number: " + a.regNo + "\n");
            output.append("Batch: " + a.batch + "\n");
            output.append("Pass Out Year: " + a.passYear + "\n");
            output.append("Position: " + a.position + "\n");
            output.append("Company: " + a.company + "\n");
            output.append("Working Year: " + a.workYear + "\n");
            output.append("Email: " + a.email + "\n");
            output.append("Phone: " + a.phone + "\n");
            output.append("-----------------------------\n");
        }
        if (alumniList.isEmpty()) output.append("No Alumni Records Found!");
    }

    private void updateAlumni() {
        String[] s = ask("Update Alumni", "Enter Alumni Email");
        if (isEmpty(s)) return;

        for (Alumni a : alumniList) {
            if (a.email.equalsIgnoreCase(s[0])) {
                String[] n = ask("Update Details", "Name", "Department",
                    "Register Number", "Batch", "Pass Out Year", "Position",
                    "Company", "Working Year", "Email", "Phone");
                if (isEmpty(n)) return;
                a.name = n[0]; a.dept = n[1]; a.regNo = n[2]; a.batch = n[3];
                a.passYear = n[4]; a.position = n[5]; a.company = n[6];
                a.workYear = n[7]; a.email = n[8]; a.phone = n[9];
                showMessage("Alumni Updated Successfully!");
                return;
            }
        }
        showMessage("Alumni Not Found!");
    }

    private void deleteAlumni() {
        String[] s = ask("Delete Alumni", "Enter Alumni Email");
        if (isEmpty(s)) return;
        for (int i = 0; i < alumniList.size(); i++) {
            if (alumniList.get(i).email.equalsIgnoreCase(s[0])) {
                alumniList.remove(i);
                showMessage("Alumni Deleted Successfully!");
                return;
            }
        }
        showMessage("Alumni Not Found!");
    }

    private void searchAlumni() {
        String[] s = ask("Search Alumni", "Enter Name / Batch / Department");
        if (isEmpty(s)) return;
        output.setText("SEARCH RESULTS\n\n");
        boolean found = false;
        for (Alumni a : alumniList) {
            if (a.name.equalsIgnoreCase(s[0]) || a.batch.equalsIgnoreCase(s[0]) ||
                a.dept.equalsIgnoreCase(s[0])) {
                output.append("Name: " + a.name + "\n");
                output.append("Batch: " + a.batch + "\n");
                output.append("Department: " + a.dept + "\n");
                output.append("Position: " + a.position + "\n");
                output.append("Company: " + a.company + "\n");
                output.append("Email: " + a.email + "\n\n");
                found = true;
            }
        }
        if (!found) output.setText("No Alumni Found!");
    }

    private void addAnnouncement() {
        String[] s = ask("Add Announcement", "Announcement Type", "Title", "Date", "Description");
        if (isEmpty(s)) return;
        String announcement = "Title: " + s[1] + "\nCategory: " + s[0] +
            "\nDate: " + s[2] + "\nDescription: " + s[3];
        announcements.add(announcement);
        notifications.add("ALL: New Announcement - " + s[1]);
        showMessage("Announcement Added Successfully!");
    }

    private void viewAnnouncements() {
        output.setText("COLLEGE ANNOUNCEMENTS\n\n");
        for (String a : announcements) {
            output.append(a + "\n-----------------------\n\n");
        }
        if (announcements.isEmpty()) output.append("No Announcements Available!");
    }

    private void deleteAnnouncement() {
        String[] s = ask("Delete Announcement", "Enter Announcement Title");
        if (isEmpty(s)) return;
        for (int i = 0; i < announcements.size(); i++) {
            if (announcements.get(i).startsWith("Title: " + s[0] + "\n")) {
                announcements.remove(i);
                showMessage("Announcement Deleted Successfully!");
                return;
            }
        }
        showMessage("Announcement Not Found!");
    }

    private void viewNotifications() {
        output.setText("YOUR NOTIFICATIONS\n\n");
        boolean found = false;
        for (String n : notifications) {
            if (n.startsWith("ALL:") || n.startsWith(currentUser + ":")) {
                output.append(n.substring(n.indexOf(":") + 1) + "\n\n");
                found = true;
            }
        }
        if (!found) output.append("No Notifications!");
    }

    private void sendMessage() {
        String[] s = ask("Send Message", "Receiver Email", "Message");
        if (isEmpty(s)) return;

        boolean found = s[0].equalsIgnoreCase("admin");
        for (Alumni a : alumniList) {
            if (a.email.equalsIgnoreCase(s[0])) {
                found = true;
                break;
            }
        }
        if (!found) {
            showMessage("Receiver Not Found!");
            return;
        }

        messages.add("From: " + currentUser + " | To: " + s[0] + " | Message: " + s[1]);
        notifications.add(s[0] + ": New Message from " + currentUser);
        showMessage("Message Sent Successfully!");
    }

    private void viewMessages() {
        output.setText("YOUR MESSAGES\n\n");
        boolean found = false;
        for (String m : messages) {
            if (m.contains("To: " + currentUser) || m.contains("From: " + currentUser)) {
                output.append(m + "\n\n");
                found = true;
            }
        }
        if (!found) output.append("No Messages Found!");
    }

    private void shareUpdate() {
        String[] s = ask("Share College Update", "Update Title", "Update Details");
        if (isEmpty(s)) return;
        updates.add("Title: " + s[0] + "\nDetails: " + s[1]);
        notifications.add("ALL: College Update: " + s[0]);
        showMessage("College Update Shared Successfully!");
    }

    private void viewUpdates() {
        output.setText("COLLEGE UPDATES\n\n");
        for (String u : updates) {
            output.append(u + "\n-----------------------\n\n");
        }
        if (updates.isEmpty()) output.append("No Updates Available!");
    }

    private void myProfile() {
        for (Alumni a : alumniList) {
            if (a.email.equalsIgnoreCase(currentUser)) {
                output.setText("MY ALUMNI PROFILE\n\n");
                output.append("Name: " + a.name + "\n");
                output.append("Department: " + a.dept + "\n");
                output.append("Register Number: " + a.regNo + "\n");
                output.append("Batch: " + a.batch + "\n");
                output.append("Pass Out Year: " + a.passYear + "\n");
                output.append("Position: " + a.position + "\n");
                output.append("Company: " + a.company + "\n");
                output.append("Working Year: " + a.workYear + "\n");
                output.append("Email: " + a.email + "\n");
                output.append("Phone: " + a.phone);
                return;
            }
        }
        output.setText("Profile Not Found!");
    }

    private void updateMyProfile() {
        for (Alumni a : alumniList) {
            if (a.email.equalsIgnoreCase(currentUser)) {
                String[] n = ask("Update My Profile", "Name", "Department",
                    "Batch", "Pass Out Year", "Position", "Company",
                    "Working Year", "Phone");
                if (isEmpty(n)) return;
                a.name = n[0]; a.dept = n[1]; a.batch = n[2];
                a.passYear = n[3]; a.position = n[4]; a.company = n[5];
                a.workYear = n[6]; a.phone = n[7];
                showMessage("Profile Updated Successfully!");
                return;
            }
        }
        showMessage("Profile Not Found!");
    }

    private void contactAlumni() {
        String[] s = ask("Contact Alumni", "Enter Alumni Name");
        if (isEmpty(s)) return;
        for (Alumni a : alumniList) {
            if (a.name.equalsIgnoreCase(s[0])) {
                output.setText("ALUMNI CONTACT DETAILS\n\n");
                output.append("Name: " + a.name + "\n");
                output.append("Department: " + a.dept + "\n");
                output.append("Batch: " + a.batch + "\n");
                output.append("Email: " + a.email + "\n");
                output.append("Phone: " + a.phone);
                return;
            }
        }
        showMessage("Alumni Not Found!");
    }

    private void logout() {
        Dialog dialog = new Dialog(this, "Logout", true);
        dialog.setLayout(new FlowLayout());
        dialog.add(new Label("Are you sure you want to logout?"));
        Button yes = new Button("YES");
        Button no = new Button("NO");
        dialog.add(yes);
        dialog.add(no);

        yes.addActionListener(e -> {
            role = "";
            currentUser = "";
            loginEmail.setText("");
            loginPassword.setText("");
            dialog.dispose();
            cards.show(root, "welcome");
        });
        no.addActionListener(e -> dialog.dispose());

        dialog.setSize(350, 130);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(AlumniManagementAWT::new);
    }
}
