# CCC Pulse App - Defense Script for Member B

**Presentation order:** backend, Java Swing implementation, methods, and OOP decisions after Member A's objectives and GUI walkthrough.  
**Member A:** [Full name]  
**Member B:** [Full name]  
**Course/section:** [Course and section]

Replace bracketed text with accurate details. Member B explains how the GUI works internally and why the selected Java and OOP techniques fit the project. Both members should understand the complete application.

## 1. Handoff and implementation overview

“Thank you, [Member A]. Member A has shown the objectives and the screens. I will now explain what happens behind those screens: how the data is represented, how Java Swing responds to user actions, and why we used these classes and methods.”

“The program begins in `CccPulseApp.main`. It uses `SwingUtilities.invokeLater` to create the interface on Swing's event-dispatch thread. It then creates the sample `ClinicStore` and opens `LoginFrame`. Running the GUI on Swing's event thread is the standard Swing approach for keeping interface updates coordinated.”

## 2. Classes and backend data

“The backend is organized around the information the clinic needs to manage.”

- “`Account` stores the username, password, role, linked student ID, and whether the user must change the initial password.”
- “`Person` stores a student's or faculty member's profile. It has constructors for both a simple full-name profile and a profile with separate last, first, middle, and suffix fields.”
- “`HealthRecord` stores the measurements and health details from one physical assessment, along with the nurse's decision and remarks.”
- “`ClinicVisit` stores the person ID, visit date, reason, nurse remarks, and treatment. Its `update` method lets the nurse revise the remarks and treatment.”
- “`ClinicStore` holds the in-memory maps and lists for accounts, people, assessments, and visits. The IDs connect an account or person to their related records.”
- “`Role` is an enum with `STUDENT` and `NURSE`. An enum makes the allowed account roles explicit instead of relying on arbitrary text.”

“These classes keep related information together. For example, assessment data is in `HealthRecord` rather than mixed into the login screen, and visit updates are handled by `ClinicVisit`.”

## 3. OOP concepts used and why

### Encapsulation

“We use encapsulation by keeping model fields private. Other parts of the program use getters and specific methods such as `changePassword`, `updateProfile`, or `ClinicVisit.update` instead of directly changing those fields. This keeps the model's changes controlled.”

### Inheritance

“Our Swing windows use inheritance. `LoginFrame`, `FirstLoginFrame`, `MainFrame`, and `ReportPage` extend `JFrame`. We chose this because each one is a top-level application window and needs the window behavior Swing already provides, such as a title, size, close operation, and visibility.”

“The domain classes do not inherit from one another. Students and faculty share the `Person` class because they use the same profile shape in this prototype; their different workflows are handled by role and by which portal is open.”

### Composition and relationships

“`ClinicStore` contains collections of accounts, people, assessment records, and visits. This is a has-a relationship: the store has the records used by the application. A `HealthRecord` is associated with a student through the student ID key, and a `ClinicVisit` stores the person's ID so it can be shown in that person's history.”

### Method overloading and overriding

“`Person` has overloaded constructors: one accepts a full name, and another accepts separate name fields. This lets the demo data and structured profile forms create a person using the format that fits their input.”

“We also override `isCellEditable` in table models and return false. That keeps the displayed record tables read-only, so users do not accidentally edit a table cell without going through the intended form.”

“`MainFrame` also overrides `componentResized` to update navigation visibility when the window changes size.”

## 4. Java Swing components and why we used them

“The GUI is built from standard Swing components.”

- “`JFrame` provides each top-level window.”
- “`JPanel` groups related controls into sections and cards.”
- “`JButton` triggers actions such as sign-in, save, navigation, and report printing.”
- “`JTextField` and `JPasswordField` collect text and passwords. `JPasswordField` masks input by default.”
- “`JComboBox` provides course, year, and other fixed-choice selections.”
- “`JCheckBox` and `JRadioButton` collect checklist answers. `ButtonGroup` ensures only one fitness decision is selected.”
- “`JTable` displays lists of students, assessments, and visits. `DefaultTableModel` provides the rows and columns.”
- “`JScrollPane` lets longer forms and tables fit in the window without making the whole page too large.”
- “`JOptionPane` provides sign-in feedback, validation warnings, and confirmation dialogs.”
- “`JFileChooser` lets the user choose where to save a report, and Java's print APIs send the designed report page to the configured printer.”

“We use layout managers to make the pages easier to arrange and resize. `BorderLayout` organizes the main window into areas such as header, navigation, and content. `GridLayout` aligns cards and form fields in a grid. `BoxLayout` stacks controls vertically. `FlowLayout` arranges action buttons in a row. `GridBagLayout` positions the sign-in content. `CardLayout` switches between the profile review, edit profile, and password setup screens during first login.”

## 5. Event handling and important methods

“Swing is event-driven: a button click or selection change runs an event handler. We attach these handlers with `addActionListener` and lambdas, keeping the action close to the control that triggers it.”

- “`authenticate` checks the entered credentials. If a student must change the initial password, it opens `FirstLoginFrame`; otherwise it opens `MainFrame`.”
- “`showPage` updates the breadcrumb and replaces the main content with the selected role-specific page.”
- “`dashboard` builds different dashboard content for a student and a nurse.”
- “`saveProfile` checks required profile fields and updates the `Person` object.”
- “`showAssessmentForm` builds the nurse's form. Its save action checks required values, numeric ranges, checklist descriptions, and the selected fitness decision before adding a `HealthRecord`.”
- “`HealthRecord` sets its valid-through date with `checkedOn.plusMonths(6)`, and `isExpired` checks whether that date has passed.”
- “`ClinicStore.records` uses `computeIfAbsent` to return an existing assessment list or create one if there is not one yet.”
- “`logVisit` creates a clinic visit after checking the reason. `editVisit` updates nurse remarks and treatment.”
- “`buildReportText` assembles the text export, while `printPage` sends the report component to Java's print service.”

“We put validation before saving so invalid or incomplete data is stopped before it is added to the model. The interface displays a clear error message, and the user can correct the input.”

## 6. Why some features are implemented this way

“We use an in-memory `ClinicStore` because the requested version is a sample and does not require a database. This makes the project easier to run and demonstrate, but the trade-off is that saved data disappears when the app closes.”

“The six-month validity rule is part of `HealthRecord`, because it describes an assessment's own lifecycle. The role-based navigation is in `MainFrame`, because the main window needs to show the right pages and actions for the signed-in user. Student and nurse access are separated so students review records while the nurse manages assessment decisions.”

“The separate `ReportPage` gives the report a printable layout without mixing report design into the dashboard. The report can be printed or saved as text using Java's file and print APIs.”

## 7. Closing and handoff for questions

“To summarize, the application combines Swing windows and event listeners with model classes for accounts, people, assessments, and visits. We used inheritance for the application windows, encapsulation for controlled data access, relationships through `ClinicStore` and IDs, and overridden table-model behavior to keep record tables read-only.”

“This is a prototype, so data is stored only in memory. A future version could add persistent storage and real notification delivery if required. Thank you. We are ready for your questions.”

## Likely code questions

### Is this inheritance?

“Yes. The application window classes extend Swing's `JFrame` class. They inherit standard window behavior and add CCC Pulse's own screens and actions.”

### Is the application using polymorphism?

“The clearest examples are method overrides: the table models override `isCellEditable`, and `MainFrame` overrides `componentResized`. We use the overridden behavior to make tables read-only and adjust the UI after resizing.”

### Why are students and faculty both represented by `Person`?

“They share common profile information, and in this prototype the faculty workflow only needs enough profile information to attach visit records. Reusing `Person` avoids duplicating those fields. Their account requirements differ: faculty visit records do not need a sign-in account.”

### Why use `ButtonGroup` for the fitness decision?

“The nurse must choose one fitness decision. `ButtonGroup` makes the radio buttons mutually exclusive, which matches that rule.”

### Why use a `JTable` for history?

“A table makes repeated records easier to compare by date and field. We keep it read-only and use forms or dialogs for intentional updates.”

### What is the limitation of `ClinicStore`?

“It stores data only for the current program run. Closing the application loses changes. A production version would need persistent storage and stronger security and privacy controls.”

## Rehearsal checklist for Member B

- [ ] Fill in member names and course details.
- [ ] Replace the contribution sentence with the work actually completed by Member B.
- [ ] Rehearse the model classes and explain how the IDs connect records.
- [ ] Explain inheritance, encapsulation, composition/relationships, constructors, and the actual method overrides accurately.
- [ ] Be ready to point to one validation path and the six-month validity method in the source.
- [ ] Explain why the project uses Swing layouts, controls, event listeners, and in-memory storage.
- [ ] Coordinate with Member A so the code explanation follows the GUI walkthrough.
- [ ] Be ready to explain Member A's GUI work as well as your own backend area.

