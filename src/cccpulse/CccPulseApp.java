package cccpulse;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
/** CCC Pulse - in-memory school clinic physical assessment demonstration. */
public class CccPulseApp {
    public static void main(String[] args) {
        // Start Swing on its event-dispatch thread so UI creation and events stay coordinated.
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            catch (Exception ignored) {
            }
            ClinicStore store = ClinicStore.demo();
            new LoginFrame(store).setVisible(true);
        }
        );
    }
}
// Restrict each account to one of the two portal roles supported by this prototype.
enum Role {
    STUDENT, NURSE
}
// Encapsulates sign-in data and the first-login password-change rule.
class Account {
    private final String username;
    private String password;
    private final Role role;
    private boolean mustChangePassword;
    private final String studentId;
    Account(String username, String password, Role role, boolean mustChangePassword, String studentId) {
        this.username=username;
        this.password=password;
        this.role=role;
        this.mustChangePassword=mustChangePassword;
        this.studentId=studentId;
    }
    String getUsername() {
        return username;
    }
    Role getRole() {
        return role;
    }
    boolean mustChangePassword() {
        return mustChangePassword;
    }
    String getStudentId() {
        return studentId;
    }
    boolean authenticate(String candidate) {
        return password.equals(candidate);
    }
    void changePassword(String next) {
        if(next==null||next.length()<6) throw new IllegalArgumentException("Use at least 6 characters.");
        password=next;
        mustChangePassword=false;
    }
}
// Shared profile model for students and faculty clinic visitors.
class Person {
    private final String id;
    private String lastName, firstName, middleName, suffix, course, year, contact, email;
    Person(String id, String fullName, String course, String year, String contact, String email) {
        this(id, parseLast(fullName), parseFirst(fullName), "", "", course, year, contact, email);
    }
    Person(String id, String lastName, String firstName, String middleName, String suffix, String course, String year,
    String contact, String email) {
        this.id=id;
        this.lastName=lastName;
        this.firstName=firstName;
        this.middleName=middleName;
        this.suffix=suffix;
        this.course=course;
        this.year=year;
        this.contact=contact;
        this.email=email;
    }
    private static String parseFirst(String name) {
        String[] parts=name.trim().split("\\s+");
        return parts.length==0?"":parts[0];
    }
    private static String parseLast(String name) {
        String[] parts=name.trim().split("\\s+");
        return parts.length<2?"":String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));
    }
    String getId() {
        return id;
    }
    String getName() {
        String value=lastName.isEmpty()?firstName:lastName+", "+firstName;
        if(!middleName.isEmpty())value+=" "+middleName;
        if(!suffix.isEmpty())value+=" "+suffix;
        return value;
    }
    String getLastName() {
        return lastName;
    }
    String getFirstName() {
        return firstName;
    }
    String getMiddleName() {
        return middleName;
    }
    String getSuffix() {
        return suffix;
    }
    String getCourse() {
        return course;
    }
    String getYear() {
        return year;
    }
    String getContact() {
        return contact;
    }
    String getEmail() {
        return email;
    }
    void updateProfile(String lastName, String firstName, String middleName, String suffix, String course, String year,
    String contact, String email) {
        this.lastName=lastName;
        this.firstName=firstName;
        this.middleName=middleName;
        this.suffix=suffix;
        this.course=course;
        this.year=year;
        this.contact=contact;
        this.email=email;
    }
}
// One nurse-recorded physical assessment. The record owns its six-month validity rule.
class HealthRecord {
    private final LocalDate checkedOn;
    private final double height, weight;
    private final String bloodPressure, oxygen, bloodType, history, allergies, concerns, fitness, remarks;
    private final LocalDate validUntil;
    private final boolean certificate;
    HealthRecord(double height, double weight, String bp, String oxygen, String bloodType, String history, String allergies,
    String concerns, String fitness, String remarks, boolean certificate) {
        this.checkedOn=LocalDate.now();
        this.height=height;
        this.weight=weight;
        this.bloodPressure=bp;
        this.oxygen=oxygen;
        this.bloodType=bloodType;
        this.history=history;
        this.allergies=allergies;
        this.concerns=concerns;
        this.fitness=fitness;
        this.remarks=remarks;
        this.validUntil=checkedOn.plusMonths(6);
        this.certificate=certificate;
    }
    LocalDate getCheckedOn() {
        return checkedOn;
    }
    LocalDate getValidUntil() {
        return validUntil;
    }
    String getFitness() {
        return fitness;
    }
    String getRemarks() {
        return remarks;
    }
    double getHeight() {
        return height;
    }
    double getWeight() {
        return weight;
    }
    String getBloodPressure() {
        return bloodPressure;
    }
    String getOxygen() {
        return oxygen;
    }
    String getBloodType() {
        return bloodType;
    }
    String getHistory() {
        return history;
    }
    String getAllergies() {
        return allergies;
    }
    String getConcerns() {
        return concerns;
    }
    boolean hasCertificate() {
        return certificate;
    }
    boolean isExpired() {
        // An assessment is due on its valid-through date or any later date.
        return !LocalDate.now().isBefore(validUntil);
    }
}
// A single clinic visit; the nurse can update its remarks and treatment after logging it.
class ClinicVisit {
    private final String personId;
    private final LocalDate date;
    private final String reason;
    private String nurseRemarks, treatment;
    ClinicVisit(String personId, String reason, String nurseRemarks, String treatment) {
        this.personId=personId;
        this.date=LocalDate.now();
        this.reason=reason;
        this.nurseRemarks=nurseRemarks;
        this.treatment=treatment;
    }
    String getPersonId() {
        return personId;
    }
    LocalDate getDate() {
        return date;
    }
    String getReason() {
        return reason;
    }
    String getNurseRemarks() {
        return nurseRemarks;
    }
    String getTreatment() {
        return treatment;
    }
    void update(String remarks, String treatment) {
        this.nurseRemarks=remarks;
        this.treatment=treatment;
    }
}
// In-memory repository for this sample. IDs connect accounts, people, assessments, and visits.
class ClinicStore {
    final Map<String, Account> accounts=new LinkedHashMap<>();
    final Map<String, Person> students=new LinkedHashMap<>();
    final Map<String, Person> faculty=new LinkedHashMap<>();
    final Map<String, List<HealthRecord>> assessments=new LinkedHashMap<>();
    final List<ClinicVisit> visits=new ArrayList<>();
    static ClinicStore demo() {
        ClinicStore s=new ClinicStore();
        s.accounts.put("25-00001", new Account("25-00001", "pulse123", Role.STUDENT, true, "25-00001"));
        s.accounts.put("nurse", new Account("nurse", "nurse123", Role.NURSE, false, null));
        s.students.put("25-00001", new Person("25-00001", "Alex Dela Cruz", "BS Information Technology", "2nd Year",
        "09171234567", "alex.delacruz@ccc.edu.ph"));
        s.students.put("25-00002", new Person("25-00002", "Jamie Santos", "BS Nursing", "3rd Year", "09187654321", "jamie.santos@ccc.edu.ph"));
        s.faculty.put("F-001", new Person("F-001", "Maria Reyes", "Faculty", "Instructor", "09181112222", "maria.reyes@ccc.edu.ph"));
        s.assessments.put("25-00001", new ArrayList<>(Arrays.asList(new HealthRecord(168, 62, "118/76", "98%", "O+",
        "None reported", "None", "Seasonal asthma", "Fit", "Cleared for campus activities.", true))));
        s.assessments.put("25-00002", new ArrayList<>());
        s.visits.add(new ClinicVisit("25-00001", "Headache", "Rest and hydration advised.", "Paracetamol 500 mg (1 tablet)"));
        s.visits.add(new ClinicVisit("F-001", "Routine check", "Vitals stable.", "No treatment"));
        return s;
    }
    Account login(String u, String p) {
        Account a=accounts.get(u);
        return a!=null&&a.authenticate(p)?a:null;
    }
    List<HealthRecord> records(String id) {
        return assessments.computeIfAbsent(id, k->new ArrayList<>());
    }
    HealthRecord latest(String id) {
        List<HealthRecord> rs=records(id);
        return rs.isEmpty()?null:rs.get(rs.size()-1);
    }
}
// Shared visual helpers keep colors, fonts, buttons, and cards consistent across screens.
class Theme {
    static final Color NAVY = new Color(20, 39, 62);
    static final Color TEAL = new Color(18, 150, 137);
    static final Color BUTTON_TEAL = new Color(12, 105, 97);
    static final Color BG = new Color(244, 247, 249);
    static final Color INK = new Color(34, 49, 66);
    static final Color MUTED = new Color(112, 128, 143);
    static final Color LINE = new Color(226, 233, 237);
    static final Color WHITE = Color.WHITE;
    static final Color PALE = new Color(228, 246, 243);
    static Font font(int size, boolean bold) {
        return new Font("Segoe UI", bold?Font.BOLD:Font.PLAIN, size);
    }
    static JLabel label(String text, int size, boolean bold, Color color) {
        JLabel l=new JLabel(text);
        l.setFont(font(size, bold));
        l.setForeground(color);
        return l;
    }
    static JButton button(String text, boolean primary) {
        JButton b=new JButton(text);
        b.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        b.setFont(font(13, true));
        b.setForeground(NAVY);
        b.setBackground(primary?TEAL:WHITE);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setFocusPainted(false);
        b.setBorderPainted(true);
        b.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(primary?BUTTON_TEAL:LINE), new EmptyBorder(9,
        15, 9, 15)));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
    static JPanel card() {
        JPanel p=new JPanel();
        p.setBackground(WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(LINE), new EmptyBorder(18, 18,
        18, 18)));
        return p;
    }
    static ImageIcon logoIcon(int size) {
        try {
            ImageIcon source=new ImageIcon("CCC_logo.png");
            if(source.getIconWidth()>0)return new ImageIcon(source.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH));
        }
        catch(Exception ignored) {
        }
        return null;
    }
}
// Inheritance: this screen extends JFrame to reuse Swing's top-level window behavior.
class LoginFrame extends JFrame {
    private final ClinicStore store;
    private JTextField username;
    private JPasswordField password;
    LoginFrame(ClinicStore store) {
        this.store=store;
        setTitle("CCC Pulse | School Clinic");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 610);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 540));
        build();
    }
    private void build() {
        JPanel root=new JPanel(new GridLayout(1, 2));
        root.setBackground(Theme.BG);
        setContentPane(root);
        JPanel brand=new JPanel(new GridBagLayout());
        brand.setBackground(Theme.NAVY);
        JPanel bl=new JPanel();
        bl.setOpaque(false);
        bl.setLayout(new BoxLayout(bl, BoxLayout.Y_AXIS));
        JLabel mark=new JLabel(Theme.logoIcon(80));
        mark.setAlignmentX(0f);
        bl.add(mark);
        bl.add(Box.createVerticalStrut(18));
        bl.add(Theme.label("CCC Pulse", 38, true, Theme.WHITE));
        bl.add(Box.createVerticalStrut(12));
        JLabel sub=Theme.label("School Clinic Physical Assessment System", 16, false, new Color(204, 218, 229));
        sub.setAlignmentX(0f);
        bl.add(sub);
        bl.add(Box.createVerticalStrut(32));
        JLabel note=Theme.label("Care that keeps your campus moving.", 15, false, new Color(177, 195, 210));
        note.setAlignmentX(0f);
        bl.add(note);
        brand.add(bl);
        root.add(brand);
        JPanel right=new JPanel(new GridBagLayout());
        right.setBackground(Theme.BG);
        JPanel form=Theme.card();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Theme.LINE), new EmptyBorder(24,
        24, 22, 24)));
        form.add(Theme.label("Welcome back", 28, true, Theme.INK));
        form.add(Box.createVerticalStrut(8));
        form.add(Theme.label("Sign in to your clinic workspace", 14, false, Theme.MUTED));
        form.add(Box.createVerticalStrut(24));
        form.add(Theme.label("Username or student number", 13, true, Theme.INK));
        username=input();
        username.setText("25-00001");
        form.add(username);
        form.add(Box.createVerticalStrut(14));
        form.add(Theme.label("Password", 13, true, Theme.INK));
        password=new JPasswordField();
        styleInput(password);
        password.setText("pulse123");
        form.add(password);
        JCheckBox showPassword=new JCheckBox("Show password");
        showPassword.setOpaque(false);
        showPassword.setForeground(Theme.MUTED);
        showPassword.setAlignmentX(LEFT_ALIGNMENT);
        char echo=password.getEchoChar();
        showPassword.addActionListener(e->password.setEchoChar(showPassword.isSelected()?'\0':echo));
        form.add(showPassword);
        form.add(Box.createVerticalStrut(16));
        JButton sign=Theme.button("Sign in", true);
        sign.setAlignmentX(Component.LEFT_ALIGNMENT);
        sign.setMaximumSize(new Dimension(400, 46));
        sign.setPreferredSize(new Dimension(400, 46));
        sign.addActionListener(e->authenticate());
        form.add(sign);
        form.add(Box.createVerticalStrut(20));
        form.add(Theme.label("Demo: 25-00001 / pulse123 · nurse / nurse123", 11, false, Theme.MUTED));
        right.add(form);
        root.add(right);
        getRootPane().setDefaultButton(sign);
    }
    private JTextField input() {
        JTextField f=new JTextField();
        styleInput(f);
        return f;
    }
    private void authenticate() {
        // The account role and first-login flag decide which window opens next.
        Account a=store.login(username.getText().trim(), new String(password.getPassword()));
        if(a==null) {
            JOptionPane.showMessageDialog(this, "Username or password is incorrect.", "Sign-in failed", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(a.mustChangePassword()) {
            Person person=store.students.get(a.getStudentId());
            if(person==null) {
                JOptionPane.showMessageDialog(this, "The student profile could not be found.", "Sign-in failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
            new FirstLoginFrame(store, a, person).setVisible(true);
            dispose();
            return;
        }
        new MainFrame(store, a).setVisible(true);
        dispose();
    }
    static JCheckBox showPasswordToggle(JPasswordField... fields) {
        JCheckBox show=new JCheckBox("Show password");
        show.setOpaque(false);
        show.setForeground(Theme.MUTED);
        show.setAlignmentX(Component.LEFT_ALIGNMENT);
        char[] echoChars=new char[fields.length];
        for(int i=0; i<fields.length; i++)echoChars[i]=fields[i].getEchoChar();
        show.addActionListener(e-> {
            for(int i=0; i<fields.length; i++)fields[i].setEchoChar(show.isSelected()?'\0':echoChars[i]);
        }
        );
        return show;
    }
    static void styleInput(JComponent f) {
        f.setFont(Theme.font(14, false));
        f.setForeground(Theme.INK);
        f.setBackground(Theme.WHITE);
        if(f instanceof javax.swing.text.JTextComponent)((javax.swing.text.JTextComponent)f).setCaretColor(Theme.INK);
        f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Theme.LINE), new EmptyBorder(10,
        11, 10, 11)));
        f.setPreferredSize(new Dimension(520, 42));
        f.setMaximumSize(new Dimension(540, 42));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
    }
}
// A separate JFrame guides first-time students through profile review and password setup.
class FirstLoginFrame extends JFrame {
    private static final String[] COURSES= {
        "BS Information Technology", "BS Nursing", "BS Criminology", "BS Hospitality Management", "BS Tourism Management", "Bachelor of Elementary Education", "Bachelor of Secondary Education", "Other"
    }
    ;
    private static final String[] YEARS= {
        "1st Year", "2nd Year", "3rd Year", "4th Year", "5th Year"
    }
    ;
    private final ClinicStore store;
    private final Account account;
    private final Person person;
    private final JPanel pages=new JPanel(new CardLayout());
    private JTextField last, first, middle, suffix, contact, email;
    private JComboBox<String> course, year;
    private JPasswordField newPassword, confirmPassword;
    FirstLoginFrame(ClinicStore store, Account account, Person person) {
        this.store=store;
        this.account=account;
        this.person=person;
        setTitle("CCC Pulse | Complete your account");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 760);
        setMinimumSize(new Dimension(720, 650));
        setLocationRelativeTo(null);
        build();
    }
    private void build() {
        JPanel root=new JPanel(new BorderLayout());
        root.setBackground(Theme.BG);
        root.setBorder(new EmptyBorder(22, 28, 24, 28));
        JPanel header=new JPanel(new BorderLayout());
        header.setOpaque(false);
        JPanel brand=new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        brand.setOpaque(false);
        JLabel logo=new JLabel(Theme.logoIcon(48));
        brand.add(logo);
        JPanel name=new JPanel();
        name.setOpaque(false);
        name.setLayout(new BoxLayout(name, BoxLayout.Y_AXIS));
        name.add(Theme.label("CCC Pulse", 20, true, Theme.NAVY));
        name.add(Theme.label("School Clinic Physical Assessment System", 12, false, Theme.MUTED));
        brand.add(name);
        header.add(brand, BorderLayout.WEST);
        root.add(header, BorderLayout.NORTH);
        pages.add(reviewPage(), "review");
        pages.add(editPage(), "edit");
        pages.add(passwordPage(), "password");
        root.add(pages, BorderLayout.CENTER);
        setContentPane(root);
        show("review");
    }
    private JPanel frame(String title, String subtitle) {
        JPanel p=new JPanel(new BorderLayout(0, 18));
        p.setBackground(Theme.BG);
        p.setBorder(new EmptyBorder(26, 8, 4, 8));
        JPanel h=new JPanel();
        h.setOpaque(false);
        h.setLayout(new BoxLayout(h, BoxLayout.Y_AXIS));
        h.add(Theme.label("First sign-in setup", 12, true, Theme.TEAL));
        h.add(Box.createVerticalStrut(6));
        h.add(Theme.label(title, 26, true, Theme.INK));
        h.add(Box.createVerticalStrut(5));
        h.add(Theme.label(subtitle, 14, false, Theme.MUTED));
        p.add(h, BorderLayout.NORTH);
        return p;
    }
    private JPanel reviewPage() {
        JPanel p=frame("Review your profile", "Check your information and make any corrections before setting your password.");
        JPanel card=Theme.card();
        card.setLayout(new GridLayout(0, 2, 18, 12));
        addReview(card, "Student number", person.getId());
        addReview(card, "Last name", person.getLastName());
        addReview(card, "First name", person.getFirstName());
        addReview(card, "Middle name", person.getMiddleName());
        addReview(card, "Suffix", person.getSuffix());
        addReview(card, "Course", person.getCourse());
        addReview(card, "Year level", person.getYear());
        addReview(card, "Contact number", person.getContact());
        addReview(card, "Email", person.getEmail());
        p.add(card, BorderLayout.CENTER);
        JPanel buttons=new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setOpaque(false);
        JButton edit=Theme.button("Edit profile", false);
        edit.addActionListener(e->show("edit"));
        JButton next=Theme.button("Continue to set password", true);
        next.addActionListener(e->show("password"));
        buttons.add(edit);
        buttons.add(next);
        p.add(buttons, BorderLayout.SOUTH);
        return p;
    }
    private void addReview(JPanel p, String label, String value) {
        JPanel cell=new JPanel();
        cell.setOpaque(false);
        cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));
        cell.add(Theme.label(label, 11, true, Theme.MUTED));
        cell.add(Box.createVerticalStrut(4));
        cell.add(Theme.label(value==null||value.trim().isEmpty()?"Not provided":value, 14, true, Theme.INK));
        p.add(cell);
    }
    private JPanel editPage() {
        JPanel p=frame("Edit your profile", "Last and first name are required. Middle name and suffix are optional.");
        JPanel card=Theme.card();
        card.setLayout(new GridLayout(0, 2, 14, 8));
        last=field(person.getLastName());
        first=field(person.getFirstName());
        middle=field(person.getMiddleName());
        suffix=field(person.getSuffix());
        contact=field(person.getContact());
        email=field(person.getEmail());
        course=new JComboBox<>(COURSES);
        year=new JComboBox<>(YEARS);
        course.setSelectedItem(person.getCourse());
        year.setSelectedItem(person.getYear());
        LoginFrame.styleInput(course);
        LoginFrame.styleInput(year);
        addField(card, "Last name *", last);
        addField(card, "First name *", first);
        addField(card, "Middle name", middle);
        addField(card, "Suffix", suffix);
        addField(card, "Course", course);
        addField(card, "Year level", year);
        addField(card, "Contact number *", contact);
        addField(card, "Email", email);
        p.add(card, BorderLayout.CENTER);
        JPanel buttons=new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setOpaque(false);
        JButton cancel=Theme.button("Cancel", false);
        cancel.addActionListener(e->show("review"));
        JButton save=Theme.button("Save profile", true);
        save.addActionListener(e->saveProfile());
        buttons.add(cancel);
        buttons.add(save);
        p.add(buttons, BorderLayout.SOUTH);
        return p;
    }
    private JTextField field(String value) {
        JTextField f=new JTextField(value==null?"":value);
        LoginFrame.styleInput(f);
        return f;
    }
    private void addField(JPanel p, String label, JComponent field) {
        JPanel cell=new JPanel();
        cell.setOpaque(false);
        cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));
        cell.add(Theme.label(label, 12, true, Theme.INK));
        cell.add(Box.createVerticalStrut(5));
        cell.add(field);
        p.add(cell);
    }
    private void saveProfile() {
        // Validate required identity fields before changing the shared Person model.
        if(last.getText().trim().isEmpty()||first.getText().trim().isEmpty()||contact.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete the required fields: last name, first name, and contact number.",
            "Profile incomplete", JOptionPane.WARNING_MESSAGE);
            return;
        }
        person.updateProfile(last.getText().trim(), first.getText().trim(), middle.getText().trim(), suffix.getText().trim(),
        String.valueOf(course.getSelectedItem()), String.valueOf(year.getSelectedItem()), contact.getText().trim(), email.getText().trim());
        pages.removeAll();
        pages.add(reviewPage(), "review");
        pages.add(editPage(), "edit");
        pages.add(passwordPage(), "password");
        show("review");
    }
    private JPanel passwordPage() {
        JPanel p=frame("Set a new password", "Your temporary password must be replaced before you can continue.");
        JPanel card=Theme.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.add(Theme.label("New password", 12, true, Theme.INK));
        newPassword=new JPasswordField();
        LoginFrame.styleInput(newPassword);
        card.add(newPassword);
        card.add(Box.createVerticalStrut(14));
        card.add(Theme.label("Confirm new password", 12, true, Theme.INK));
        confirmPassword=new JPasswordField();
        LoginFrame.styleInput(confirmPassword);
        card.add(confirmPassword);
        card.add(Box.createVerticalStrut(8));
        card.add(LoginFrame.showPasswordToggle(newPassword, confirmPassword));
        card.add(Box.createVerticalStrut(8));
        card.add(Theme.label("Use at least 6 characters.", 12, false, Theme.MUTED));
        p.add(card, BorderLayout.CENTER);
        JPanel buttons=new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setOpaque(false);
        JButton back=Theme.button("Back to profile", false);
        back.addActionListener(e->show("review"));
        JButton finish=Theme.button("Save password and continue", true);
        finish.addActionListener(e->finish());
        buttons.add(back);
        buttons.add(finish);
        p.add(buttons, BorderLayout.SOUTH);
        return p;
    }
    private void finish() {
        char[] a=newPassword.getPassword(), b=confirmPassword.getPassword();
        if(!Arrays.equals(a, b)) {
            JOptionPane.showMessageDialog(this, "The passwords do not match.", "Password not updated", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            account.changePassword(new String(a));
            new MainFrame(store, account).setVisible(true);
            dispose();
        }
        catch(IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Password not updated", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void show(String page) {
        ((CardLayout)pages.getLayout()).show(pages, page);
    }
}
// The main portal window. Role-based navigation keeps student and nurse actions distinct.
class MainFrame extends JFrame {
    private static final String[] COURSE_OPTIONS= {
        "BS Information Technology", "BS Nursing", "BS Criminology", "BS Hospitality Management", "BS Tourism Management", "Bachelor of Elementary Education", "Bachelor of Secondary Education", "Other"
    }
    ;
    private static final String[] YEAR_OPTIONS= {
        "1st Year", "2nd Year", "3rd Year", "4th Year", "5th Year"
    }
    ;
    private final ClinicStore store;
    private final Account account;
    private final boolean nurse;
    private final JPanel content=new JPanel(new BorderLayout());
    private JPanel sidebar;
    private JButton menuButton;
    private final Map<String, JButton> navigationButtons=new LinkedHashMap<>();
    private String page="Dashboard";
    private JTable table;
    private JLabel breadcrumb;
    MainFrame(ClinicStore store, Account account) {
        this.store=store;
        this.account=account;
        this.nurse=account.getRole()==Role.NURSE;
        setTitle("CCC Pulse | "+(nurse?"Nurse":"Student")+" portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 820);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(720, 560));
        buildShell();
        showPage("Dashboard");
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) {
                updateResponsiveNavigation();
            }
        }
        );
        updateResponsiveNavigation();
    }
    private void buildShell() {
        // Compose the persistent sidebar and header around a page area that changes navigation screens.
        JPanel shell=new JPanel(new BorderLayout());
        shell.setBackground(Theme.BG);
        setContentPane(shell);
        sidebar=new JPanel();
        sidebar.setBackground(Theme.NAVY);
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(22, 15, 18, 15));
        JPanel logo=new JPanel(new FlowLayout(FlowLayout.LEFT, 9, 0));
        logo.setOpaque(false);
        logo.add(new JLabel(Theme.logoIcon(34)));
        logo.add(Theme.label("CCC Pulse", 20, true, Theme.WHITE));
        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(28));
        sidebar.add(Theme.label(nurse?"NURSE WORKSPACE":"STUDENT PORTAL", 10, true, new Color(160, 181, 198)));
        sidebar.add(Box.createVerticalStrut(12));
        String[] nav=nurse?new String[] {
            "Dashboard", "History", "Students", "Medical Records", "Non-students", "Settings"
        }
        :new String[] {
            "Dashboard", "History", "Medical Records", "Physical Assessment", "School Clinic Records", "Settings"
        }
        ;
        for(String item:nav) {
            JButton b=new JButton(" "+item);
            b.setUI(new javax.swing.plaf.basic.BasicButtonUI());
            b.setHorizontalAlignment(SwingConstants.LEFT);
            b.setFont(Theme.font(13, true));
            b.setForeground(Theme.WHITE);
            b.setBackground(Theme.NAVY);
            b.setOpaque(true);
            b.setContentAreaFilled(true);
            b.setFocusPainted(false);
            b.setBorderPainted(false);
            b.setBorder(new EmptyBorder(12, 10, 12, 8));
            b.setMaximumSize(new Dimension(220, 44));
            b.setAlignmentX(LEFT_ALIGNMENT);
            b.addActionListener(e->showPage(item));
            navigationButtons.put(item, b);
            sidebar.add(b);
            sidebar.add(Box.createVerticalStrut(4));
        }
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(Theme.label(account.getUsername(), 13, true, Theme.WHITE));
        sidebar.add(Box.createVerticalStrut(14));
        JButton logout=Theme.button("Sign out", false);
        logout.setAlignmentX(LEFT_ALIGNMENT);
        logout.addActionListener(e-> {
            new LoginFrame(store).setVisible(true);
            dispose();
        }
        );
        sidebar.add(logout);
        shell.add(sidebar, BorderLayout.WEST);
        JPanel right=new JPanel(new BorderLayout());
        right.setOpaque(false);
        JPanel top=new JPanel(new BorderLayout());
        top.setBackground(Theme.WHITE);
        top.setBorder(new EmptyBorder(14, 24, 14, 24));
        breadcrumb=Theme.label("Home / "+page, 13, false, Theme.MUTED);
        JPanel topLeft=new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topLeft.setOpaque(false);
        menuButton=Theme.button("☰", false);
        menuButton.setFont(Theme.font(20, true));
        menuButton.setToolTipText("Navigation menu");
        JPopupMenu navigationMenu=new JPopupMenu();
        for(String item:nav) {
            JMenuItem menuItem=new JMenuItem(item);
            menuItem.addActionListener(e->showPage(item));
            navigationMenu.add(menuItem);
        }
        menuButton.addActionListener(e->navigationMenu.show(menuButton, 0, menuButton.getHeight()));
        topLeft.add(menuButton);
        topLeft.add(breadcrumb);
        top.add(topLeft, BorderLayout.WEST);
        JButton user=Theme.button(nurse?"School Nurse":"Student", false);
        JPopupMenu accountMenu=new JPopupMenu();
        JMenuItem profileItem=new JMenuItem("My profile");
        profileItem.addActionListener(e->showPage("Settings"));
        JMenuItem passwordItem=new JMenuItem("Change password");
        passwordItem.addActionListener(e->changePassword());
        JMenuItem signOutItem=new JMenuItem("Sign out");
        signOutItem.addActionListener(e-> {
            new LoginFrame(store).setVisible(true);
            dispose();
        }
        );
        accountMenu.add(profileItem);
        accountMenu.add(passwordItem);
        accountMenu.addSeparator();
        accountMenu.add(signOutItem);
        user.addActionListener(e->accountMenu.show(user, 0, user.getHeight()));
        top.add(user, BorderLayout.EAST);
        right.add(top, BorderLayout.NORTH);
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(22, 24, 24, 24));
        right.add(content, BorderLayout.CENTER);
        shell.add(right, BorderLayout.CENTER);
    }
    private void updateResponsiveNavigation() {
        boolean compact=getWidth()<900;
        sidebar.setVisible(!compact);
        menuButton.setVisible(compact);
        revalidate();
        repaint();
    }
    private void showPage(String p) {
        // Rebuild the center content for the selected page and current user's role.
        page=p;
        for(Map.Entry<String, JButton> entry:navigationButtons.entrySet())entry.getValue().setBackground(entry.getKey().equals(page)?new Color(39,
        68, 93):Theme.NAVY);
        if(breadcrumb!=null)breadcrumb.setText("Home / "+p);
        content.removeAll();
        JPanel pagePanel=new JPanel(new BorderLayout(0, 18));
        pagePanel.setOpaque(false);
        JPanel heading=new JPanel(new BorderLayout());
        heading.setOpaque(false);
        heading.add(Theme.label(p, 27, true, Theme.INK), BorderLayout.NORTH);
        heading.add(Theme.label(nurse?"Manage clinic care and student clearances":"Your health, assessment and clinic visits",
        13, false, Theme.MUTED), BorderLayout.SOUTH);
        pagePanel.add(heading, BorderLayout.NORTH);
        JPanel body=new JPanel(new BorderLayout(0, 16));
        body.setOpaque(false);
        if(p.equals("Dashboard"))dashboard(body);
        else if(p.equals("Settings"))settings(body);
        else if(p.equals("Physical Assessment")&&!nurse)assessment(body);
        else if(p.equals("Medical Records")) {
            if(nurse)nurseMedicalRecords(body);
            else medical(body);
        }
        else if(p.equals("School Clinic Records"))visits(body, account.getStudentId(), false);
        else if(p.equals("History"))history(body);
        else if(p.equals("Students"))students(body);
        else if(p.equals("Non-students"))nonStudents(body);
        else body.add(new JLabel("Select a section from the navigation."));
        pagePanel.add(body, BorderLayout.CENTER);
        content.add(pagePanel, BorderLayout.CENTER);
        content.revalidate();
        content.repaint();
    }
    private String sid() {
        return account.getStudentId();
    }
    private void dashboard(JPanel body) {
        // Students see personal status; nurses see clinic-wide counts and quick actions.
        if(!nurse) {
            Person person=store.students.get(sid());
            HealthRecord r=store.latest(sid());
            JPanel welcome=Theme.card();
            welcome.setLayout(new BorderLayout());
            welcome.add(Theme.label("Good day, "+person.getFirstName(), 22, true, Theme.INK), BorderLayout.NORTH);
            welcome.add(Theme.label("Keep your clinic information up to date before your next activity.", 13, false,
            Theme.MUTED), BorderLayout.CENTER);
            body.add(welcome, BorderLayout.NORTH);
            JPanel cards=new JPanel(new GridLayout(1, 3, 14, 0));
            cards.setOpaque(false);
            cards.add(metric("Clearance status", r==null?"Assessment needed":r.isExpired()?"Expired":r.getFitness(),
            r==null||r.isExpired()?"Please visit the nurse":"Valid until "+r.getValidUntil(), r==null||r.isExpired()?new Color(186,
            104, 42):Theme.TEAL));
            cards.add(metric("Clinic visits", ""+countVisits(sid()), "Recorded visits", Theme.NAVY));
            cards.add(metric("Medical certificate", r!=null&&r.hasCertificate()?"On file":"Required", "Enrollment document",
            r!=null&&r.hasCertificate()?Theme.TEAL:new Color(186, 104, 42)));
            body.add(cards, BorderLayout.CENTER);
            if(r==null||r.isExpired()) {
                JPanel alert=Theme.card();
                alert.setBackground(new Color(255, 247, 231));
                alert.add(Theme.label("Assessment reminder: your six-month clearance is due. Schedule a visit with the school nurse.",
                14, true, new Color(144, 91, 29)));
                body.add(alert, BorderLayout.SOUTH);
            }
        }
        else {
            JPanel cards=new JPanel(new GridLayout(1, 3, 14, 0));
            cards.setOpaque(false);
            cards.add(metric("Students", ""+store.students.size(), "Active student profiles", Theme.TEAL));
            cards.add(metric("Assessments due", ""+dueCount(), "Expired or not assessed", new Color(186, 104, 42)));
            cards.add(metric("Clinic visits", ""+store.visits.size(), "All visit records", Theme.NAVY));
            body.add(cards, BorderLayout.NORTH);
            JPanel actions=Theme.card();
            actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));
            actions.add(Theme.label("Quick actions", 18, true, Theme.INK));
            actions.add(Box.createVerticalStrut(14));
            JButton a=Theme.button("Record a student assessment", true);
            a.setAlignmentX(LEFT_ALIGNMENT);
            a.addActionListener(e->showPage("Students"));
            actions.add(a);
            actions.add(Box.createVerticalStrut(10));
            JButton b=Theme.button("Review clinic visit history", false);
            b.setAlignmentX(LEFT_ALIGNMENT);
            b.addActionListener(e->showPage("History"));
            actions.add(b);
            body.add(actions, BorderLayout.CENTER);
        }
    }
    private JPanel metric(String title, String value, String sub, Color accent) {
        JPanel p=Theme.card();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(Theme.label(title, 12, true, Theme.MUTED));
        p.add(Box.createVerticalStrut(13));
        p.add(Theme.label(value, 21, true, accent));
        p.add(Box.createVerticalStrut(7));
        p.add(Theme.label(sub, 11, false, Theme.MUTED));
        return p;
    }
    private int countVisits(String id) {
        int n=0;
        for(ClinicVisit v:store.visits)if(v.getPersonId().equals(id))n++;
        return n;
    }
    private int dueCount() {
        int n=0;
        for(String id:store.students.keySet()) {
            HealthRecord r=store.latest(id);
            if(r==null||r.isExpired())n++;
        }
        return n;
    }
    private void changePassword() {
        JPasswordField first=new JPasswordField(), confirm=new JPasswordField();
        JPanel fields=new JPanel(new GridLayout(0, 2, 8, 8));
        fields.add(new JLabel("New password"));
        fields.add(first);
        fields.add(new JLabel("Confirm password"));
        fields.add(confirm);
        fields.add(new JLabel(""));
        fields.add(LoginFrame.showPasswordToggle(first, confirm));
        if(JOptionPane.showConfirmDialog(this, fields, "Change password", JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION) {
            if(!Arrays.equals(first.getPassword(), confirm.getPassword())) {
                error("The passwords do not match.");
                return;
            }
            try {
                account.changePassword(new String(first.getPassword()));
                JOptionPane.showMessageDialog(this, "Password updated.");
            }
            catch(IllegalArgumentException ex) {
                error(ex.getMessage());
            }
        }
    }
    private void history(JPanel body) {
        // Student history is limited to their own records; nurse history covers all clinic visits.
        if(!nurse) {
            medical(body);
            JPanel v=Theme.card();
            v.setLayout(new BorderLayout());
            v.add(Theme.label("Clinic visit timeline", 17, true, Theme.INK), BorderLayout.NORTH);
            v.add(tableForVisits(sid()), BorderLayout.CENTER);
            body.add(v, BorderLayout.SOUTH);
        }
        else {
            JPanel c=Theme.card();
            c.setLayout(new BorderLayout(0, 12));
            c.add(Theme.label("Recent clinic activity", 17, true, Theme.INK), BorderLayout.NORTH);
            c.add(tableForAllVisits(), BorderLayout.CENTER);
            body.add(c, BorderLayout.CENTER);
        }
    }
    private void medical(JPanel body) {
        // Build a read-only assessment table; the nurse uses the assessment form to create records.
        String id=nurse&&table!=null&&table.getSelectedRow()>=0?String.valueOf(table.getValueAt(table.getSelectedRow(),
        0)):sid();
        List<HealthRecord> rs=store.records(id);
        JPanel panel=Theme.card();
        panel.setLayout(new BorderLayout(0, 12));
        panel.add(Theme.label("Physical assessment history", 17, true, Theme.INK), BorderLayout.NORTH);
        String[] cols= {
            "Check-up", "Valid until", "Height", "Weight", "Blood pressure", "Oxygen", "Blood type", "Fitness", "Certificate"
        }
        ;
        DefaultTableModel m=new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        }
        ;
        for(int i=rs.size()-1; i>=0; i--) {
            HealthRecord r=rs.get(i);
            m.addRow(new Object[] {
                r.getCheckedOn(), r.getValidUntil(), r.getHeight()+" cm", r.getWeight()+" kg", r.getBloodPressure(),
                r.getOxygen(), r.getBloodType(), r.getFitness(), r.hasCertificate()?"On file":"Missing"
            }
            );
        }
        JTable t=new JTable(m);
        styleTable(t);
        panel.add(new JScrollPane(t), BorderLayout.CENTER);
        JPanel actions=new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        JButton print=Theme.button("Print / Save report", true);
        print.addActionListener(e->printReport(id));
        actions.add(print);
        panel.add(actions, BorderLayout.SOUTH);
        body.add(panel, BorderLayout.CENTER);
        if(!rs.isEmpty()) {
            HealthRecord r=rs.get(rs.size()-1);
            JPanel info=Theme.card();
            info.setLayout(new GridLayout(2, 2, 10, 10));
            info.add(infoLine("Medical history", r.getHistory()));
            info.add(infoLine("Allergies", r.getAllergies()));
            info.add(infoLine("Current concerns", r.getConcerns()));
            info.add(infoLine("Nurse remarks", r.getRemarks()));
            body.add(info, BorderLayout.SOUTH);
        }
    }
    private void nurseMedicalRecords(JPanel body) {
        JPanel p=Theme.card();
        p.setLayout(new BorderLayout(0, 12));
        p.add(Theme.label("All student assessment records", 17, true, Theme.INK), BorderLayout.NORTH);
        DefaultTableModel m=new DefaultTableModel(new String[] {
            "Student ID", "Student", "Check-up", "Valid until", "Blood type", "Fitness", "Certificate copy", "Nurse remarks"
        }
        , 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        }
        ;
        for(Person person:store.students.values()) {
            for(HealthRecord r:store.records(person.getId()))m.addRow(new Object[] {
                person.getId(), person.getName(), r.getCheckedOn(), r.getValidUntil(), r.getBloodType(), r.getFitness(),
                r.hasCertificate()?"Received":"Missing", r.getRemarks()
            }
            );
        }
        JTable t=new JTable(m);
        styleTable(t);
        p.add(new JScrollPane(t), BorderLayout.CENTER);
        JButton print=Theme.button("Print / Save selected report", true);
        print.addActionListener(e-> {
            if(t.getSelectedRow()<0) {
                error("Select a student record first.");
                return;
            }
            printReport(String.valueOf(t.getValueAt(t.getSelectedRow(), 0)));
        }
        );
        JPanel actions=new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        actions.add(print);
        p.add(actions, BorderLayout.SOUTH);
        body.add(p, BorderLayout.CENTER);
    }
    private JPanel infoLine(String k, String v) {
        JPanel p=new JPanel(new BorderLayout(0, 5));
        p.setOpaque(false);
        p.add(Theme.label(k, 11, true, Theme.MUTED), BorderLayout.NORTH);
        p.add(Theme.label(v.isEmpty()?"None recorded":v, 13, false, Theme.INK), BorderLayout.CENTER);
        return p;
    }
    private void assessment(JPanel body) {
        HealthRecord r=store.latest(sid());
        JPanel p=Theme.card();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        if(r==null||r.isExpired()) {
            p.add(Theme.label("Assessment required", 20, true, new Color(186, 104, 42)));
            p.add(Box.createVerticalStrut(12));
            p.add(Theme.label("Visit the school nurse to complete your physical assessment and receive activity clearance.",
            14, false, Theme.INK));
        }
        else {
            p.add(Theme.label("Current clearance: "+r.getFitness(), 20, true, Theme.TEAL));
            p.add(Box.createVerticalStrut(10));
            p.add(Theme.label("Assessed "+r.getCheckedOn()+" · Valid through "+r.getValidUntil(), 14, false, Theme.MUTED));
            p.add(Box.createVerticalStrut(8));
            p.add(Theme.label("Nurse remarks: "+r.getRemarks(), 14, false, Theme.INK));
        }
        p.add(Box.createVerticalStrut(18));
        JLabel privacy=Theme.label("Health status changes must be reviewed by the school nurse. Contact the clinic to update medical details.",
        13, false, Theme.MUTED);
        p.add(privacy);
        body.add(p, BorderLayout.NORTH);
        medical(body);
    }
    private void settings(JPanel body) {
        // Students can edit their profile; both roles can manage their account password.
        Person person=nurse?null:store.students.get(sid());
        JPanel cards=new JPanel(new GridLayout(1, 2, 16, 0));
        cards.setOpaque(false);
        JPanel profile=Theme.card();
        profile.setLayout(new BoxLayout(profile, BoxLayout.Y_AXIS));
        profile.add(Theme.label("Profile settings", 17, true, Theme.INK));
        profile.add(Box.createVerticalStrut(5));
        profile.add(Theme.label("Keep your account information up to date.", 12, false, Theme.MUTED));
        profile.add(Box.createVerticalStrut(18));
        if(person!=null) {
            JTextField last = field(person.getLastName());
            JTextField first = field(person.getFirstName());
            JTextField middle = field(person.getMiddleName());
            JTextField suffix = field(person.getSuffix());
            JTextField contact = field(person.getContact());
            JTextField email = field(person.getEmail());
            JComboBox<String> course=optionBox(COURSE_OPTIONS, person.getCourse()), year=optionBox(YEAR_OPTIONS, person.getYear());
            profile.add(formRow("Last name *", last));
            profile.add(Box.createVerticalStrut(7));
            profile.add(formRow("First name *", first));
            profile.add(Box.createVerticalStrut(7));
            profile.add(formRow("Middle name (optional)", middle));
            profile.add(Box.createVerticalStrut(7));
            profile.add(formRow("Suffix (optional)", suffix));
            profile.add(Box.createVerticalStrut(7));
            profile.add(formRow("Course / program", course));
            profile.add(Box.createVerticalStrut(7));
            profile.add(formRow("Year level", year));
            profile.add(Box.createVerticalStrut(7));
            profile.add(formRow("Contact number", contact));
            profile.add(Box.createVerticalStrut(7));
            profile.add(formRow("Email", email));
            profile.add(Box.createVerticalStrut(12));
            JButton save=Theme.button("Save profile", true);
            save.setAlignmentX(LEFT_ALIGNMENT);
            save.addActionListener(e-> {
                if(last.getText().trim().isEmpty()||first.getText().trim().isEmpty()||contact.getText().trim().isEmpty()) {
                    error("Last name, first name, and contact number are required.");
                    return;
                }
                person.updateProfile(last.getText().trim(), first.getText().trim(), middle.getText().trim(), suffix.getText().trim(),
                String.valueOf(course.getSelectedItem()), String.valueOf(year.getSelectedItem()), contact.getText().trim(),
                email.getText().trim());
                JOptionPane.showMessageDialog(this, "Profile updated.");
            }
            );
            profile.add(save);
            if(!nurse) {
                profile.add(Box.createVerticalStrut(18));
                JButton request=Theme.button("Request health record review", false);
                request.setAlignmentX(LEFT_ALIGNMENT);
                request.addActionListener(e->JOptionPane.showMessageDialog(this, "Please visit or contact the school clinic. A nurse must verify health status changes."));
                profile.add(request);
            }
        }
        else {
            profile.add(Theme.label("Nurse account", 12, true, Theme.MUTED));
            profile.add(Box.createVerticalStrut(5));
            profile.add(Theme.label(account.getUsername(), 14, true, Theme.INK));
        }
        JPanel security=Theme.card();
        security.setLayout(new BoxLayout(security, BoxLayout.Y_AXIS));
        security.add(Theme.label("Change password", 17, true, Theme.INK));
        security.add(Box.createVerticalStrut(5));
        security.add(Theme.label("Choose a new password for your account.", 12, false, Theme.MUTED));
        security.add(Box.createVerticalStrut(18));
        security.add(Theme.label("New password", 12, true, Theme.MUTED));
        security.add(Box.createVerticalStrut(6));
        JPasswordField pw=new JPasswordField(), confirmPw=new JPasswordField();
        LoginFrame.styleInput(pw);
        security.add(pw);
        security.add(Box.createVerticalStrut(14));
        security.add(Theme.label("Confirm new password", 12, true, Theme.MUTED));
        security.add(Box.createVerticalStrut(6));
        LoginFrame.styleInput(confirmPw);
        security.add(confirmPw);
        security.add(Box.createVerticalStrut(5));
        security.add(LoginFrame.showPasswordToggle(pw, confirmPw));
        security.add(Box.createVerticalStrut(8));
        security.add(Theme.label("Use at least 6 characters.", 11, false, Theme.MUTED));
        security.add(Box.createVerticalStrut(12));
        JButton change=Theme.button("Update password", true);
        change.setAlignmentX(LEFT_ALIGNMENT);
        change.addActionListener(e-> {
            if(!Arrays.equals(pw.getPassword(), confirmPw.getPassword())) {
                error("The passwords do not match.");
                return;
            }
            try {
                account.changePassword(new String(pw.getPassword()));
                pw.setText("");
                confirmPw.setText("");
                JOptionPane.showMessageDialog(this, "Password updated.");
            }
            catch(IllegalArgumentException ex) {
                error(ex.getMessage());
            }
        }
        );
        security.add(change);
        cards.add(profile);
        cards.add(security);
        body.add(cards, BorderLayout.CENTER);
    }
    private JTextField field(String x) {
        JTextField f=new JTextField(x);
        LoginFrame.styleInput(f);
        return f;
    }
    private JPanel formRow(String label, JComponent f) {
        JPanel r=new JPanel(new BorderLayout(10, 0));
        r.setOpaque(false);
        r.setMaximumSize(new Dimension(650, 54));
        r.add(Theme.label(label, 12, true, Theme.MUTED), BorderLayout.WEST);
        r.add(f, BorderLayout.CENTER);
        return r;
    }
    private JComboBox<String> optionBox(String[] options, String selected) {
        JComboBox<String> box=new JComboBox<>(options);
        if(selected!=null&&!selected.isEmpty()) {
            boolean found=false;
            for(String option:options)if(option.equals(selected))found=true;
            if(!found)box.addItem(selected);
            box.setSelectedItem(selected);
        }
        box.setFont(Theme.font(13, false));
        box.setForeground(Theme.INK);
        box.setBackground(Theme.WHITE);
        return box;
    }
    private void students(JPanel body) {
        JPanel p=Theme.card();
        p.setLayout(new BorderLayout(0, 12));
        JPanel head=new JPanel(new BorderLayout());
        head.setOpaque(false);
        head.add(Theme.label("Student directory", 17, true, Theme.INK), BorderLayout.WEST);
        JButton add=Theme.button("+ Add student", true);
        add.addActionListener(e->addStudent());
        head.add(add, BorderLayout.EAST);
        p.add(head, BorderLayout.NORTH);
        String[] cols= {
            "Student ID", "Name", "Course", "Year", "Last check-up", "Valid until", "Status"
        }
        ;
        DefaultTableModel m=new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        }
        ;
        for(Person s:store.students.values()) {
            HealthRecord r=store.latest(s.getId());
            m.addRow(new Object[] {
                s.getId(), s.getName(), s.getCourse(), s.getYear(), r==null?"—":r.getCheckedOn(), r==null?"—":r.getValidUntil(),
                r==null?"Due":r.isExpired()?"Expired":r.getFitness()
            }
            );
        }
        table=new JTable(m);
        styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel actions=new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        JButton assess=Theme.button("Record assessment", true);
        assess.addActionListener(e-> {
            if(table.getSelectedRow()<0) {
                error("Select a student first.");
                return;
            }
            recordAssessment(String.valueOf(table.getValueAt(table.getSelectedRow(), 0)));
        }
        );
        actions.add(assess);
        JButton visits=Theme.button("Student visits", false);
        visits.addActionListener(e-> {
            if(table.getSelectedRow()<0) {
                error("Select a student first.");
                return;
            }
            visits(body, String.valueOf(table.getValueAt(table.getSelectedRow(), 0)), true);
        }
        );
        actions.add(visits);
        p.add(actions, BorderLayout.SOUTH);
        body.add(p, BorderLayout.CENTER);
    }
    private void addStudent() {
        showAddStudentForm();
    }
    private void showAddStudentForm() {
        // This form creates a Person and a linked default-password student Account in the in-memory store.
        page="Add student profile";
        for(Map.Entry<String, JButton> entry:navigationButtons.entrySet())entry.getValue().setBackground(entry.getKey().equals("Students")?new Color(39,
        68, 93):Theme.NAVY);
        if(breadcrumb!=null)breadcrumb.setText("Home / Students / Add student profile");
        content.removeAll();
        JPanel pagePanel=new JPanel(new BorderLayout(0, 16));
        pagePanel.setOpaque(false);
        JPanel heading=new JPanel(new BorderLayout());
        heading.setOpaque(false);
        JPanel titles=new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.add(Theme.label("Add student profile", 27, true, Theme.INK));
        titles.add(Box.createVerticalStrut(5));
        titles.add(Theme.label("Enter the student's identity, academic program, and contact information.", 13, false,
        Theme.MUTED));
        heading.add(titles, BorderLayout.WEST);
        JButton back=Theme.button("Back to students", false);
        back.addActionListener(e->showPage("Students"));
        heading.add(back, BorderLayout.EAST);
        pagePanel.add(heading, BorderLayout.NORTH);
        JTextField id=field(""), last=field(""), first=field(""), middle=field(""), suffix=field(""), contact=field(""), email=field("");
        id.setToolTipText("Example student ID: 25-00003");
        JComboBox<String> course=optionBox(COURSE_OPTIONS, "BS Information Technology"), year=optionBox(YEAR_OPTIONS,
        "1st Year");
        JPanel form=new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        JPanel identity=Theme.card();
        identity.setLayout(new BoxLayout(identity, BoxLayout.Y_AXIS));
        identity.add(Theme.label("1. STUDENT IDENTITY", 13, true, Theme.TEAL));
        identity.add(Box.createVerticalStrut(10));
        identity.add(stackedField("Student ID * (e.g. 25-00003)", id));
        identity.add(Box.createVerticalStrut(10));
        JPanel nameGrid=new JPanel(new GridLayout(2, 2, 14, 10));
        nameGrid.setOpaque(false);
        nameGrid.add(stackedField("Last name *", last));
        nameGrid.add(stackedField("First name *", first));
        nameGrid.add(stackedField("Middle name (optional)", middle));
        nameGrid.add(stackedField("Suffix (optional)", suffix));
        identity.add(nameGrid);
        form.add(identity);
        form.add(Box.createVerticalStrut(12));
        JPanel academic=Theme.card();
        academic.setLayout(new BoxLayout(academic, BoxLayout.Y_AXIS));
        academic.add(Theme.label("2. ACADEMIC INFORMATION", 13, true, Theme.TEAL));
        academic.add(Box.createVerticalStrut(10));
        JPanel academicGrid=new JPanel(new GridLayout(1, 2, 14, 0));
        academicGrid.setOpaque(false);
        academicGrid.add(stackedField("Course / program *", course));
        academicGrid.add(stackedField("Year level *", year));
        academic.add(academicGrid);
        form.add(academic);
        form.add(Box.createVerticalStrut(12));
        JPanel contactCard=Theme.card();
        contactCard.setLayout(new BoxLayout(contactCard, BoxLayout.Y_AXIS));
        contactCard.add(Theme.label("3. CONTACT INFORMATION", 13, true, Theme.TEAL));
        contactCard.add(Box.createVerticalStrut(10));
        JPanel contactGrid=new JPanel(new GridLayout(1, 2, 14, 0));
        contactGrid.setOpaque(false);
        contactGrid.add(stackedField("Contact number *", contact));
        contactGrid.add(stackedField("Email (optional)", email));
        contactCard.add(contactGrid);
        form.add(contactCard);
        JScrollPane scroll=new JScrollPane(form);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        pagePanel.add(scroll, BorderLayout.CENTER);
        JPanel actions=new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        JButton cancel=Theme.button("Cancel", false);
        cancel.addActionListener(e->showPage("Students"));
        JButton save=Theme.button("Create student profile", true);
        save.addActionListener(e-> {
            String key=id.getText().trim();
            if(key.isEmpty()||last.getText().trim().isEmpty()||first.getText().trim().isEmpty()||contact.getText().trim().isEmpty()) {
                error("Student ID, last name, first name, and contact number are required.");
                return;
            }
            if(store.students.containsKey(key)) {
                error("That student ID already exists.");
                return;
            }
            store.students.put(key, new Person(key, last.getText().trim(), first.getText().trim(), middle.getText().trim(),
            suffix.getText().trim(), String.valueOf(course.getSelectedItem()), String.valueOf(year.getSelectedItem()),
            contact.getText().trim(), email.getText().trim()));
            store.assessments.put(key, new ArrayList<>());
            store.accounts.put(key, new Account(key, "ccc12345", Role.STUDENT, true, key));
            JOptionPane.showMessageDialog(this, "Student account created. Username: "+key+" | Default password: ccc12345 | The student must change it on first sign-in.");
            showPage("Students");
        }
        );
        actions.add(cancel);
        actions.add(save);
        pagePanel.add(actions, BorderLayout.SOUTH);
        content.add(pagePanel, BorderLayout.CENTER);
        content.revalidate();
        content.repaint();
    }
    private void recordAssessment(String id) {
        showAssessmentForm(id);
    }
    private void showAssessmentForm(String id) {
        // The nurse selects a student first, so the saved HealthRecord is attached to that student's ID.
        Person student=store.students.get(id);
        if(student==null) {
            error("Student profile could not be found.");
            return;
        }
        page="Physical Assessment";
        for(Map.Entry<String, JButton> entry:navigationButtons.entrySet())entry.getValue().setBackground(entry.getKey().equals("Students")?new Color(39,
        68, 93):Theme.NAVY);
        if(breadcrumb!=null)breadcrumb.setText("Home / Students / Physical Assessment");
        content.removeAll();
        JPanel pagePanel=new JPanel(new BorderLayout(0, 16));
        pagePanel.setOpaque(false);
        JPanel heading=new JPanel(new BorderLayout());
        heading.setOpaque(false);
        JPanel title=new JPanel();
        title.setOpaque(false);
        title.setLayout(new BoxLayout(title, BoxLayout.Y_AXIS));
        title.add(Theme.label("Physical assessment", 27, true, Theme.INK));
        title.add(Box.createVerticalStrut(5));
        title.add(Theme.label("Complete the student health form and record a nurse-reviewed activity clearance.", 13,
        false, Theme.MUTED));
        heading.add(title, BorderLayout.WEST);
        JButton back=Theme.button("Back to students", false);
        back.addActionListener(e->showPage("Students"));
        heading.add(back, BorderLayout.EAST);
        pagePanel.add(heading, BorderLayout.NORTH);
        JPanel form=new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        JPanel identity=Theme.card();
        identity.setLayout(new BorderLayout());
        identity.add(Theme.label("STUDENT", 10, true, Theme.MUTED), BorderLayout.NORTH);
        identity.add(Theme.label(student.getName()+" · "+student.getId()+" · "+student.getCourse()+" / "+student.getYear(),
        15, true, Theme.INK), BorderLayout.CENTER);
        form.add(identity);
        form.add(Box.createVerticalStrut(12));
        JTextField height=field(""), weight=field(""), bp=field(""), oxygen=field("");
        JComboBox<String> blood=optionBox(new String[] {
            "Select blood type", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-", "Unknown"
        }
        , "Select blood type");
        JPanel vitals=Theme.card();
        vitals.setLayout(new BoxLayout(vitals, BoxLayout.Y_AXIS));
        vitals.add(Theme.label("1. VITAL SIGNS", 13, true, Theme.TEAL));
        vitals.add(Box.createVerticalStrut(12));
        JPanel grid=new JPanel(new GridLayout(0, 2, 14, 10));
        grid.setOpaque(false);
        grid.add(stackedField("Height (cm) *", height));
        grid.add(stackedField("Weight (kg) *", weight));
        grid.add(stackedField("Blood pressure *", bp));
        grid.add(stackedField("Oxygen level *", oxygen));
        grid.add(stackedField("Blood type *", blood));
        vitals.add(grid);
        form.add(vitals);
        form.add(Box.createVerticalStrut(12));
        JRadioButton historyYes=new JRadioButton("Yes"), historyNo=new JRadioButton("No");
        JTextField historyDetail=field("");
        JPanel history=questionCard("2. PREVIOUS MEDICAL / HOSPITAL RECORDS", historyYes, historyNo, historyDetail, "Describe the medical or hospital record");
        form.add(history);
        form.add(Box.createVerticalStrut(12));
        JRadioButton allergyYes=new JRadioButton("Yes"), allergyNo=new JRadioButton("No");
        JTextField allergyDetail=field("");
        JPanel allergyCard=questionCard("3. ALLERGIES", allergyYes, allergyNo, allergyDetail, "Describe the allergy");
        form.add(allergyCard);
        form.add(Box.createVerticalStrut(12));
        JTextArea concerns=new JTextArea(3, 30);
        styleArea(concerns);
        JPanel concernCard=Theme.card();
        concernCard.setLayout(new BoxLayout(concernCard, BoxLayout.Y_AXIS));
        concernCard.add(Theme.label("4. CURRENT HEALTH CONCERNS", 13, true, Theme.TEAL));
        concernCard.add(Box.createVerticalStrut(6));
        concernCard.add(Theme.label("Examples: asthma, epilepsy, recent injury. Enter ‘None’ if there are no concerns.",
        11, false, Theme.MUTED));
        concernCard.add(Box.createVerticalStrut(9));
        concernCard.add(new JScrollPane(concerns));
        form.add(concernCard);
        form.add(Box.createVerticalStrut(12));
        JCheckBox cert=new JCheckBox("Medical certificate copy received for enrollment");
        cert.setOpaque(false);
        cert.setForeground(Theme.INK);
        JPanel documents=Theme.card();
        documents.setLayout(new BoxLayout(documents, BoxLayout.Y_AXIS));
        documents.add(Theme.label("5. REQUIRED DOCUMENT", 13, true, Theme.TEAL));
        documents.add(Box.createVerticalStrut(8));
        documents.add(cert);
        form.add(documents);
        form.add(Box.createVerticalStrut(12));
        JRadioButton fitYes=new JRadioButton("Fit"), fitRestrictions=new JRadioButton("Fit with restrictions"), fitNo=new JRadioButton("Not fit"), fitPending=new JRadioButton("Pending further evaluation");
        ButtonGroup fitGroup=new ButtonGroup();
        for(JRadioButton option:new JRadioButton[] {
            fitYes, fitRestrictions, fitNo, fitPending
        }
        ) {
            fitGroup.add(option);
            option.setOpaque(false);
            option.setForeground(Theme.INK);
        }
        JPanel fitCard=Theme.card();
        fitCard.setLayout(new BoxLayout(fitCard, BoxLayout.Y_AXIS));
        fitCard.add(Theme.label("6. NURSE FITNESS DECISION *", 13, true, Theme.TEAL));
        fitCard.add(Box.createVerticalStrut(8));
        fitCard.add(fitYes);
        fitCard.add(fitRestrictions);
        fitCard.add(fitNo);
        fitCard.add(fitPending);
        form.add(fitCard);
        form.add(Box.createVerticalStrut(12));
        JTextArea remarks=new JTextArea(3, 30);
        styleArea(remarks);
        JPanel remarkCard=Theme.card();
        remarkCard.setLayout(new BoxLayout(remarkCard, BoxLayout.Y_AXIS));
        remarkCard.add(Theme.label("7. NURSE REMARKS", 13, true, Theme.TEAL));
        remarkCard.add(Box.createVerticalStrut(8));
        remarkCard.add(new JScrollPane(remarks));
        form.add(remarkCard);
        form.add(Box.createVerticalStrut(14));
        JScrollPane scroll=new JScrollPane(form);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        pagePanel.add(scroll, BorderLayout.CENTER);
        JPanel actions=new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        JButton cancel=Theme.button("Cancel", false);
        cancel.addActionListener(e->showPage("Students"));
        JButton save=Theme.button("Save physical assessment", true);
        save.addActionListener(e-> {
            try {
                // Validate measurements and checklist details before adding a HealthRecord.
                if(height.getText().trim().isEmpty()||weight.getText().trim().isEmpty())throw new IllegalArgumentException("Height and weight are required.");
                double h=Double.parseDouble(height.getText().trim()), w=Double.parseDouble(weight.getText().trim());
                if(h<80||h>250||w<20||w>350)throw new IllegalArgumentException("Height must be 80-250 cm and weight 20-350 kg.");
                if(bp.getText().trim().isEmpty()||oxygen.getText().trim().isEmpty())throw new IllegalArgumentException("Blood pressure and oxygen level are required.");
                if(blood.getSelectedIndex()==0)throw new IllegalArgumentException("Select the student's blood type.");
                if(historyYes.isSelected()&&historyDetail.getText().trim().isEmpty())throw new IllegalArgumentException("Describe the previous medical record, or select No.");
                if(allergyYes.isSelected()&&allergyDetail.getText().trim().isEmpty())throw new IllegalArgumentException("Describe the allergy, or select No.");
                if(!fitYes.isSelected()&&!fitRestrictions.isSelected()&&!fitNo.isSelected()&&!fitPending.isSelected())throw new IllegalArgumentException("Select the nurse's fitness decision.");
                String medical=historyYes.isSelected()?"Yes - "+historyDetail.getText().trim():"No";
                String allergyRecord=allergyYes.isSelected()?"Yes - "+allergyDetail.getText().trim():"No";
                String decision=fitYes.isSelected()?"Fit":fitRestrictions.isSelected()?"Fit with restrictions":fitNo.isSelected()?"Not fit":"Pending further evaluation";
                store.records(id).add(new HealthRecord(h, w, bp.getText().trim(), oxygen.getText().trim(), String.valueOf(blood.getSelectedItem()),
                medical, allergyRecord, concerns.getText().trim().isEmpty()?"None":concerns.getText().trim(), decision,
                remarks.getText().trim(), cert.isSelected()));
                JOptionPane.showMessageDialog(this, "Assessment saved. Clearance is valid for six months.");
                showPage("Students");
            }
            catch(NumberFormatException ex) {
                error("Enter height and weight as numbers.");
            }
            catch(IllegalArgumentException ex) {
                error(ex.getMessage());
            }
        }
        );
        actions.add(cancel);
        actions.add(save);
        pagePanel.add(actions, BorderLayout.SOUTH);
        content.add(pagePanel, BorderLayout.CENTER);
        content.revalidate();
        content.repaint();
    }
    private JPanel stackedField(String label, JComponent field) {
        JPanel p=new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(Theme.label(label, 11, true, Theme.MUTED));
        p.add(Box.createVerticalStrut(5));
        p.add(field);
        return p;
    }
    private JPanel questionCard(String title, JRadioButton yes, JRadioButton no, JTextField details, String description) {
        JPanel card=Theme.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.add(Theme.label(title, 13, true, Theme.TEAL));
        card.add(Box.createVerticalStrut(8));
        ButtonGroup group=new ButtonGroup();
        group.add(yes);
        group.add(no);
        no.setSelected(true);
        for(JRadioButton choice:new JRadioButton[] {
            yes, no
        }
        ) {
            choice.setOpaque(false);
            choice.setForeground(Theme.INK);
        }
        JPanel choices=new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        choices.setOpaque(false);
        choices.add(yes);
        choices.add(no);
        card.add(choices);
        card.add(Box.createVerticalStrut(5));
        card.add(Theme.label(description+" (required when Yes)", 11, false, Theme.MUTED));
        card.add(Box.createVerticalStrut(5));
        card.add(details);
        details.setEnabled(false);
        yes.addActionListener(e->details.setEnabled(true));
        no.addActionListener(e-> {
            details.setText("");
            details.setEnabled(false);
        }
        );
        return card;
    }
    private void styleArea(JTextArea area) {
        area.setFont(Theme.font(13, false));
        area.setForeground(Theme.INK);
        area.setBackground(Theme.WHITE);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Theme.LINE), new EmptyBorder(8,
        10, 8, 10)));
    }
    private void nonStudents(JPanel body) {
        // Faculty visits are tracked separately and do not require a sign-in account.
        JPanel p=Theme.card();
        p.setLayout(new BorderLayout(0, 12));
        JPanel h=new JPanel(new BorderLayout());
        h.setOpaque(false);
        h.add(Theme.label("Faculty and non-student clinic records", 17, true, Theme.INK), BorderLayout.WEST);
        JButton add=Theme.button("+ Register faculty visit", true);
        add.addActionListener(e->addFacultyVisit());
        h.add(add, BorderLayout.EAST);
        p.add(h, BorderLayout.NORTH);
        DefaultTableModel m=new DefaultTableModel(new String[] {
            "Record ID", "Name", "Visit count", "Latest visit", "Latest reason"
        }
        , 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        }
        ;
        for(Person person:store.faculty.values()) {
            ClinicVisit last=null;
            int n=0;
            for(ClinicVisit v:store.visits)if(v.getPersonId().equals(person.getId())) {
                last=v;
                n++;
            }
            m.addRow(new Object[] {
                person.getId(), person.getName(), n, last==null?"—":last.getDate(), last==null?"—":last.getReason()
            }
            );
        }
        JTable t=new JTable(m);
        styleTable(t);
        p.add(new JScrollPane(t), BorderLayout.CENTER);
        body.add(p, BorderLayout.CENTER);
    }
    private void addFacultyVisit() {
        JTextField id=new JTextField("F-"+String.format("%03d", store.faculty.size()+1)), name=new JTextField(), reason=new JTextField(), remarks=new JTextField(), treatment=new JTextField("No treatment");
        JPanel f=new JPanel(new GridLayout(0, 2, 8, 8));
        String[] ls= {
            "Record ID", "Name", "Reason for visit", "Nurse remarks", "Treatment provided"
        }
        ;
        JTextField[] fs= {
            id, name, reason, remarks, treatment
        }
        ;
        for(int i=0; i<ls.length; i++) {
            f.add(new JLabel(ls[i]));
            f.add(fs[i]);
        }
        if(JOptionPane.showConfirmDialog(this, f, "Faculty clinic visit", JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION) {
            if(name.getText().trim().isEmpty()||reason.getText().trim().isEmpty()) {
                error("Name and visit reason are required.");
                return;
            }
            String key=id.getText().trim();
            store.faculty.put(key, new Person(key, name.getText().trim(), "Faculty", "Staff", "", ""));
            store.visits.add(new ClinicVisit(key, reason.getText().trim(), remarks.getText().trim(), treatment.getText().trim()));
            showPage("Non-students");
        }
    }
    private void visits(JPanel body, String id, boolean replace) {
        JPanel p=Theme.card();
        p.setLayout(new BorderLayout(0, 10));
        p.add(Theme.label("Clinic visits · "+(store.students.containsKey(id)?store.students.get(id).getName():id), 17,
        true, Theme.INK), BorderLayout.NORTH);
        p.add(tableForVisits(id), BorderLayout.CENTER);
        JPanel actions=new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        JButton add=Theme.button("+ Log visit", true);
        add.addActionListener(e->logVisit(id));
        actions.add(add);
        p.add(actions, BorderLayout.SOUTH);
        if(replace) {
            body.removeAll();
            body.add(p, BorderLayout.CENTER);
            body.revalidate();
            body.repaint();
        }
        else body.add(p, BorderLayout.CENTER);
    }
    private JScrollPane tableForVisits(String id) {
        DefaultTableModel m=new DefaultTableModel(new String[] {
            "Date", "Reason", "Nurse remarks", "Treatment provided"
        }
        , 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        }
        ;
        for(int i=store.visits.size()-1; i>=0; i--) {
            ClinicVisit v=store.visits.get(i);
            if(v.getPersonId().equals(id))m.addRow(new Object[] {
                v.getDate(), v.getReason(), v.getNurseRemarks(), v.getTreatment()
            }
            );
        }
        JTable t=new JTable(m);
        styleTable(t);
        return new JScrollPane(t);
    }
    private JScrollPane tableForAllVisits() {
        DefaultTableModel m=new DefaultTableModel(new String[] {
            "Date", "Person / ID", "Reason", "Nurse remarks", "Treatment"
        }
        , 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        }
        ;
        for(int i=store.visits.size()-1; i>=0; i--) {
            ClinicVisit v=store.visits.get(i);
            Person person=store.students.containsKey(v.getPersonId())?store.students.get(v.getPersonId()):store.faculty.get(v.getPersonId());
            m.addRow(new Object[] {
                v.getDate(), person==null?v.getPersonId():person.getName(), v.getReason(), v.getNurseRemarks(), v.getTreatment()
            }
            );
        }
        JTable t=new JTable(m);
        styleTable(t);
        t.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if(e.getClickCount()==2&&t.getSelectedRow()>=0)editVisit(t.getSelectedRow());
            }
        }
        );
        return new JScrollPane(t);
    }
    private void logVisit(String id) {
        // The person ID links this visit to the correct student or faculty record.
        JTextField reason=new JTextField(), remarks=new JTextField(), treatment=new JTextField("No treatment");
        JPanel f=new JPanel(new GridLayout(0, 2, 8, 8));
        f.add(new JLabel("Reason"));
        f.add(reason);
        f.add(new JLabel("Nurse remarks"));
        f.add(remarks);
        f.add(new JLabel("Treatment"));
        f.add(treatment);
        if(JOptionPane.showConfirmDialog(this, f, "Log clinic visit", JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION) {
            if(reason.getText().trim().isEmpty()) {
                error("Visit reason is required.");
                return;
            }
            store.visits.add(new ClinicVisit(id, reason.getText().trim(), remarks.getText().trim(), treatment.getText().trim()));
            showPage(nurse?"History":"School Clinic Records");
        }
    }
    private void editVisit(int row) {
        // Update only nurse-maintained fields while preserving the original visit date and reason.
        int idx=store.visits.size()-1-row;
        if(idx<0||idx>=store.visits.size())return;
        ClinicVisit v=store.visits.get(idx);
        JTextField remarks=new JTextField(v.getNurseRemarks()), treatment=new JTextField(v.getTreatment());
        JPanel f=new JPanel(new GridLayout(0, 2, 8, 8));
        f.add(new JLabel("Nurse remarks"));
        f.add(remarks);
        f.add(new JLabel("Treatment"));
        f.add(treatment);
        if(JOptionPane.showConfirmDialog(this, f, "Update visit record", JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION) {
            v.update(remarks.getText().trim(), treatment.getText().trim());
            showPage("History");
        }
    }
    private void printReport(String id) {
        new ReportPage(store, id).setVisible(true);
    }
    private void error(String message) {
        JOptionPane.showMessageDialog(this, message, "Please review", JOptionPane.WARNING_MESSAGE);
    }
    private void styleTable(JTable t) {
        t.setFont(Theme.font(12, false));
        t.setRowHeight(32);
        t.setForeground(Theme.INK);
        t.setGridColor(Theme.LINE);
        t.setShowVerticalLines(false);
        t.getTableHeader().setFont(Theme.font(11, true));
        t.getTableHeader().setBackground(new Color(242, 246, 248));
        t.getTableHeader().setForeground(Theme.MUTED);
        t.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.LINE));
    }
}
/** Separate, print-ready A4-like clinic report page. */
// Separate report window keeps the print layout independent from the interactive portal pages.
class ReportPage extends JFrame {
    private final ClinicStore store;
    private final String id;
    private final JPanel sheet=new JPanel();
    private final String reportText;
    ReportPage(ClinicStore store, String id) {
        this.store=store;
        this.id=id;
        Person person=store.students.get(id);
        reportText=buildReportText(person);
        setTitle("CCC Pulse | Printable clinic report");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(980, 820);
        setMinimumSize(new Dimension(820, 680));
        setLocationRelativeTo(null);
        build(person);
    }
    private void build(Person person) {
        JPanel root=new JPanel(new BorderLayout(0, 12));
        root.setBackground(Theme.BG);
        root.setBorder(new EmptyBorder(16, 18, 16, 18));
        setContentPane(root);
        JPanel toolbar=new JPanel(new BorderLayout());
        toolbar.setOpaque(false);
        toolbar.add(Theme.label("PRINTABLE CLINIC REPORT", 12, true, Theme.MUTED), BorderLayout.WEST);
        JPanel buttons=new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        JButton save=Theme.button("Save as text", false), print=Theme.button("Print report", true), close=Theme.button("Close",
        false);
        save.addActionListener(e->saveReport());
        print.addActionListener(e->printPage());
        close.addActionListener(e->dispose());
        buttons.add(save);
        buttons.add(print);
        buttons.add(close);
        toolbar.add(buttons, BorderLayout.EAST);
        root.add(toolbar, BorderLayout.NORTH);
        sheet.setLayout(new BoxLayout(sheet, BoxLayout.Y_AXIS));
        sheet.setBackground(Color.WHITE);
        sheet.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Theme.LINE), new EmptyBorder(34,
        42, 36, 42)));
        sheet.setPreferredSize(new Dimension(790, 1120));
        sheet.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel masthead=new JPanel(new BorderLayout(18, 0));
        masthead.setOpaque(false);
        JLabel seal=new JLabel(Theme.logoIcon(94));
        masthead.add(seal, BorderLayout.WEST);
        JPanel titles=new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.add(Theme.label("CITY COLLEGE OF CALAPAN", 18, true, Theme.NAVY));
        titles.add(Box.createVerticalStrut(4));
        titles.add(Theme.label("SCHOOL CLINIC · CCC PULSE", 12, true, Theme.TEAL));
        titles.add(Box.createVerticalStrut(8));
        titles.add(Theme.label("PHYSICAL ASSESSMENT & CLEARANCE", 13, true, Theme.NAVY));
        masthead.add(titles, BorderLayout.CENTER);
        sheet.add(masthead);
        sheet.add(Box.createVerticalStrut(18));
        JSeparator line=new JSeparator();
        line.setForeground(Theme.TEAL);
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        sheet.add(line);
        sheet.add(Box.createVerticalStrut(16));
        JPanel identity=new JPanel(new GridLayout(2, 2, 12, 10));
        identity.setBackground(new Color(246, 249, 250));
        identity.setBorder(new EmptyBorder(14, 16, 14, 16));
        identity.add(reportField("STUDENT NAME", person==null?id:person.getName()));
        identity.add(reportField("STUDENT ID", id));
        identity.add(reportField("COURSE / YEAR", person==null?"—":person.getCourse()+" · "+person.getYear()));
        identity.add(reportField("REPORT DATE", LocalDate.now().toString()));
        sheet.add(identity);
        sheet.add(Box.createVerticalStrut(16));
        List<HealthRecord> records=store.records(id);
        if(records.isEmpty()) {
            sheet.add(sectionTitle("ASSESSMENT STATUS"));
            sheet.add(reportValue("No physical assessment has been recorded."));
        }
        else {
            for(int i=records.size()-1; i>=0; i--) {
                HealthRecord r=records.get(i);
                sheet.add(sectionTitle(i==records.size()-1?"LATEST PHYSICAL ASSESSMENT":"PREVIOUS ASSESSMENT"));
                JPanel metrics=new JPanel(new GridLayout(2, 4, 8, 8));
                metrics.setOpaque(false);
                metrics.add(metricBox("CHECKED", r.getCheckedOn().toString()));
                metrics.add(metricBox("VALID UNTIL", r.getValidUntil().toString()));
                metrics.add(metricBox("HEIGHT", r.getHeight()+" cm"));
                metrics.add(metricBox("WEIGHT", r.getWeight()+" kg"));
                metrics.add(metricBox("BLOOD PRESSURE", r.getBloodPressure()));
                metrics.add(metricBox("OXYGEN LEVEL", r.getOxygen()));
                metrics.add(metricBox("BLOOD TYPE", r.getBloodType()));
                metrics.add(metricBox("CERTIFICATE", r.hasCertificate()?"COPY ON FILE":"NOT RECEIVED"));
                sheet.add(metrics);
                sheet.add(Box.createVerticalStrut(10));
                JPanel health=new JPanel(new GridLayout(0, 2, 12, 8));
                health.setOpaque(false);
                health.add(reportField("MEDICAL HISTORY", r.getHistory()));
                health.add(reportField("ALLERGIES", r.getAllergies()));
                health.add(reportField("CURRENT HEALTH CONCERNS", r.getConcerns()));
                health.add(reportField("FITNESS DECISION", r.getFitness()));
                sheet.add(health);
                sheet.add(Box.createVerticalStrut(8));
                sheet.add(reportField("SCHOOL NURSE REMARKS", r.getRemarks()));
                sheet.add(Box.createVerticalStrut(14));
            }
        }
        sheet.add(sectionTitle("CLINIC VISIT HISTORY"));
        DefaultTableModel model=new DefaultTableModel(new String[] {
            "DATE", "REASON", "NURSE REMARKS", "TREATMENT"
        }
        , 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        }
        ;
        for(int i=store.visits.size()-1; i>=0; i--) {
            ClinicVisit v=store.visits.get(i);
            if(v.getPersonId().equals(id))model.addRow(new Object[] {
                v.getDate(), v.getReason(), v.getNurseRemarks(), v.getTreatment()
            }
            );
        }
        JTable visits=new JTable(model);
        visits.setFont(Theme.font(10, false));
        visits.setRowHeight(27);
        visits.setGridColor(Theme.LINE);
        visits.setShowVerticalLines(false);
        visits.getTableHeader().setFont(Theme.font(9, true));
        visits.getTableHeader().setBackground(new Color(242, 246, 248));
        JScrollPane visitPane=new JScrollPane(visits);
        visitPane.setBorder(BorderFactory.createLineBorder(Theme.LINE));
        visitPane.setPreferredSize(new Dimension(700, Math.max(70, visits.getRowCount()*28+28)));
        visitPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, Math.max(90, visits.getRowCount()*28+55)));
        sheet.add(visitPane);
        sheet.add(Box.createVerticalStrut(18));
        JSeparator footerLine=new JSeparator();
        sheet.add(footerLine);
        sheet.add(Box.createVerticalStrut(9));
        JLabel foot=Theme.label("This report is generated from CCC Pulse in-memory sample records. Verify with the school clinic before use.",
        10, false, new Color(112, 128, 143));
        foot.setAlignmentX(Component.LEFT_ALIGNMENT);
        sheet.add(foot);
        JPanel canvas=new JPanel(new GridBagLayout());
        canvas.setBackground(new Color(230, 235, 238));
        canvas.add(sheet);
        JScrollPane scroll=new JScrollPane(canvas);
        scroll.setBorder(null);
        root.add(scroll, BorderLayout.CENTER);
    }
    private JPanel sectionTitle(String text) {
        JPanel p=new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(3, 0, 8, 0));
        JLabel label=Theme.label(text, 12, true, Theme.NAVY);
        p.add(label, BorderLayout.WEST);
        JSeparator sep=new JSeparator();
        sep.setForeground(Theme.LINE);
        p.add(sep, BorderLayout.SOUTH);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        return p;
    }
    private JPanel reportField(String title, String value) {
        JPanel p=new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel h=Theme.label(title, 9, true, new Color(112, 128, 143));
        JLabel v=Theme.label(value==null||value.trim().isEmpty()?"None recorded":value, 12, true, new Color(34, 49, 66));
        h.setAlignmentX(LEFT_ALIGNMENT);
        v.setAlignmentX(LEFT_ALIGNMENT);
        p.add(h);
        p.add(Box.createVerticalStrut(4));
        p.add(v);
        return p;
    }
    private JPanel metricBox(String title, String value) {
        JPanel p=new JPanel(new BorderLayout(0, 4));
        p.setBackground(new Color(246, 249, 250));
        p.setBorder(new EmptyBorder(8, 9, 8, 9));
        p.add(Theme.label(title, 8, true, new Color(112, 128, 143)), BorderLayout.NORTH);
        p.add(Theme.label(value, 11, true, Theme.NAVY), BorderLayout.CENTER);
        return p;
    }
    private JLabel reportValue(String text) {
        JLabel l=Theme.label(text, 12, false, Theme.MUTED);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }
    private String buildReportText(Person person) {
        // Assemble a plain-text export from the selected person's assessment and visit history.
        StringBuilder out=new StringBuilder("CITY COLLEGE OF CALAPAN - SCHOOL CLINIC\nPHYSICAL ASSESSMENT & CLEARANCE\n\n");
        out.append("Student: ").append(person==null?id:person.getName())
            .append(" (").append(id).append(")\nGenerated: ")
            .append(LocalDate.now()).append("\n\nASSESSMENTS\n");
        for(HealthRecord r:store.records(id)) {
            out.append("Checked: ").append(r.getCheckedOn())
                .append(" | Valid until: ").append(r.getValidUntil())
                .append(" | Decision: ").append(r.getFitness())
                .append("\nHeight: ").append(r.getHeight()).append(" cm")
                .append(" | Weight: ").append(r.getWeight()).append(" kg")
                .append(" | BP: ").append(r.getBloodPressure())
                .append(" | O2: ").append(r.getOxygen())
                .append(" | Blood: ").append(r.getBloodType())
                .append("\nHistory: ").append(r.getHistory())
                .append(" | Allergies: ").append(r.getAllergies())
                .append(" | Concerns: ").append(r.getConcerns())
                .append("\nRemarks: ").append(r.getRemarks())
                .append(" | Certificate copy: ")
                .append(r.hasCertificate()?"Received":"Missing")
                .append("\n\n");
        }
        out.append("CLINIC VISITS\n");
        for(ClinicVisit v:store.visits) {
            if(v.getPersonId().equals(id)) {
                out.append(v.getDate()).append(" | ").append(v.getReason())
                    .append(" | ").append(v.getNurseRemarks())
                    .append(" | Treatment: ").append(v.getTreatment()).append("\n");
            }
        }
        return out.toString();
    }
    private void saveReport() {
        JFileChooser chooser=new JFileChooser();
        chooser.setSelectedFile(new File(id+"-clinic-report.txt"));
        if(chooser.showSaveDialog(this)==JFileChooser.APPROVE_OPTION) {
            try(FileWriter writer=new FileWriter(chooser.getSelectedFile())) {
                writer.write(reportText);
                JOptionPane.showMessageDialog(this, "Report saved.");
            }
            catch(IOException ex) {
                JOptionPane.showMessageDialog(this, "Could not save report: "+ex.getMessage());
            }
        }
    }
    private void printPage() {
        // Scale the designed report sheet to the printer's available page area.
        PrinterJob job=PrinterJob.getPrinterJob();
        job.setJobName("CCC Pulse Clinic Report");
        job.setPrintable((graphics, format, pageIndex)-> {
            if(pageIndex>0)return Printable.NO_SUCH_PAGE;
            Graphics2D g=(Graphics2D)graphics;
            double sx=format.getImageableWidth()/sheet.getWidth(), sy=format.getImageableHeight()/sheet.getHeight(),
            scale=Math.min(sx, sy);
            g.translate(format.getImageableX(), format.getImageableY());
            g.scale(scale, scale);
            sheet.printAll(g);
            return Printable.PAGE_EXISTS;
        }
        );
        if(job.printDialog()) {
            try {
                job.print();
            }
            catch(PrinterException ex) {
                JOptionPane.showMessageDialog(this, "Could not print: "+ex.getMessage());
            }
        }
    }
}
