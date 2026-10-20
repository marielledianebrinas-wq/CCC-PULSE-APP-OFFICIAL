# CCC Pulse App - Duo Thesis Defense Script

**Project:** CCC Pulse App - Automated School Clinic Physical Assessment System  
**Member A:** [Full name] - Lead: Swing interface, screen flow, and input validation  
**Member B:** [Full name] - Lead: domain/OOP design, records, and business rules  
**Course/section:** [Course and section]  
**Adviser:** [Name, if applicable]

Use this as a rehearsal script, not as a claim that only one member understands a section. The tutorial's duo-work guidance expects both members to understand the whole system and be able to answer questions about any feature.

## 1. Opening and project introduction

**Member A:**

“Good [morning/afternoon], respected panel members. We are [Member A] and [Member B]. Today we are presenting CCC Pulse App, an Automated School Clinic Physical Assessment System built with Java Swing.”

**Member B:**

“Our project focuses on school activities that require medical attention or clearance, such as sports events, travel, and competitions. Students need a physical assessment reviewed by the school nurse, and the clinic needs an organized way to record assessments, clinic visits, and nurse remarks.”

## 2. Problem, solution, and objectives

**Member A:**

“The problem we are addressing is the need to keep physical assessment information and clinic visit records organized and easy to review. The nurse needs to record a student's profile, measurements, medical history, allergies, current health concerns, medical certificate copy status, fitness decision, and remarks.”

**Member B:**

“CCC Pulse provides separate student and nurse workspaces. It tracks physical assessments for six months, shows when an assessment is due, keeps assessment and visit history, records whether treatment was provided, and provides a separate printable report page.”

**Member A:**

“The main objectives are to standardize profile and assessment entry, help the nurse review student records, let students view their own status and history, and make clinic visit information easier to follow.”

## 3. Scope and limitations

**Member B:**

“This version is a Java Swing classroom prototype. It stores accounts and clinic records in memory, so the sample data resets when the application closes. It does not use a database, send actual email or text notifications, store certificate scans, or provide production-grade authentication. The six-month reminder appears in the student dashboard when the record is due.”

“These limitations keep the prototype focused on demonstrating the user flow, OOP design, and clinic record rules. Persistent storage and actual notification services are possible future improvements.”

## 4. Duo work distribution and collaboration

**Member A:**

“My lead responsibilities were the Swing screens and navigation, input forms, required-field and range validation, and GUI review. I also worked on [insert the classes/features you personally implemented]. I reviewed how the interface calls the model so I can explain the records and business rules as well.”

**Member B:**

“My lead responsibilities were the domain and OOP design, the record relationships, and the assessment and visit rules. I also worked on [insert the classes/features you personally implemented]. I reviewed the screens and event handling so I can explain how the user actions reach the model.”

**Member A or B:**

“We divided the work by lead area, but we reviewed and integrated the features together. Both of us can explain the full system, including the Swing interface, validation, data flow, and business rules.”

## 5. OOP and class design

**Member B:**

“Our main domain classes represent the information the clinic manages. `Person` stores student or faculty profile information. `Account` stores the username, role, student link, and first-login password-change state. `HealthRecord` stores an assessment and calculates its six-month validity. `ClinicVisit` stores the reason, date, nurse remarks, and treatment. `ClinicStore` connects these objects and holds the sample records in memory.”

“The fields are encapsulated, and the classes expose constructors and methods for the actions the application needs. For example, `ClinicVisit` can update remarks and treatment, and `HealthRecord` determines whether it is expired.”

**Member A:**

“The UI classes are `LoginFrame`, `FirstLoginFrame`, and `MainFrame`. They display sign-in, first-login setup, and role-specific pages. `ReportPage` displays a separate printable report. When a user saves a form, an event handler validates the input and then updates the corresponding object in `ClinicStore`.”

## 6. Live demonstration

### Student flow

**Member A (narration):**

“We will begin with the student workflow. In this demo, the sample student ID is `25-00001`, and the initial password is `pulse123`. At first sign-in, the student reviews the profile, can edit it, and must set a new password before entering the portal.”

**Action:** Sign in as `25-00001` with `pulse123`. Review the profile, optionally edit and save it, then set and confirm a new password.

**Member A:**

“The dashboard greets the student using the first name in the profile. It also shows the assessment status, clinic visit count, and medical certificate-copy status.”

**Action:** Show the dashboard, then open **Physical Assessment**, **Medical Records**, **History**, **School Clinic Records**, and **Settings**.

**Member A:**

“Students can review their own medical and visit history. They cannot make a nurse's fitness decision. Health-status changes are handled through the school nurse.”

### Nurse assessment flow

**Member B (narration):**

“Next, we will show the nurse workflow. The nurse selects a student, completes the physical assessment, reviews the checklist responses, records a fitness decision and remarks, and saves the record.”

**Action:** Sign out. Sign in as `nurse` with `nurse123`. Open **Students**, select a student, and open the physical assessment form.

**Member B:**

“The medical-history and allergy questions use Yes/No choices, with a description provided when needed. The nurse records the measurements, certificate-copy status, fitness decision, and remarks. The application records the check-up date and calculates the valid-through date six months later.”

**Action:** If the demo environment is fresh and time allows, enter a sample assessment and save it. Otherwise, show the existing sample assessment and explain the form fields without altering the demo data.

### Visit and report flow

**Member A:**

“The nurse can review clinic visits, add nurse remarks, and record whether treatment was given. Faculty or other non-student visits can be kept in the clinic without creating a personal sign-in account.”

**Action:** Show **History**, open a visit to update remarks/treatment if appropriate, then show **Non-students**.

**Member B:**

“The report opens in its own designed page and includes the selected person's clinic information. It can be sent to the configured printer or saved as a text file.”

**Action:** Open the printable report page. Demonstrate print or text export only if the demo computer is ready for it.

## 7. Validation demonstration

**Member A:**

“We will show one validation example. Required fields prevent incomplete records from being saved, and invalid measurement values are rejected with a message. This gives the user a clear chance to correct the form.”

**Action:** With a safe demo form, leave a required field blank or enter a non-numeric height/weight and attempt to save. Show the validation feedback, then cancel or correct the form.

## 8. Closing

**Member B:**

“CCC Pulse demonstrates how a school clinic can organize student assessments and visit history in a single desktop workflow. Its main strengths are the nurse-reviewed assessment process, six-month validity tracking, student history view, and separate faculty visit records.”

**Member A:**

“The current version is a sample that uses memory-only storage. Our next steps could include persistent storage, real notification delivery, and certificate file handling, subject to the project requirements. Thank you. We are ready for your questions.”

## 9. Likely panel questions and answer prompts

Fill in and rehearse the answers using your actual implementation and course rubric.

### Why did you choose Java Swing?

“We used Java Swing because it is part of the Java desktop GUI toolkit covered by our project requirements. It supports forms, tables, navigation, and event-driven actions for demonstrating the clinic workflow.”

### How does the system determine when an assessment expires?

“When a `HealthRecord` is created, it records the check-up date and sets the valid-through date six months later. The application checks that date to show whether the assessment is current or due.”

### How are student and nurse permissions different?

“The signed-in account has a `Role`. The main window uses that role to choose the navigation and available actions. Students can review their own records, while the nurse workspace includes student management and assessment decisions.”

### What happens when the program closes?

“The current prototype's records are held in memory, so changes are lost when the program closes. Persistent database or file storage is outside this version's scope.”

### How is a faculty member's visit recorded without an account?

“The nurse can create a faculty/non-student profile entry and link clinic visit records to that person's ID. That record does not require a sign-in account.”

### What validation does the application perform?

“It checks required profile and visit fields, password confirmation and minimum length, and assessment measurement input and ranges. We will show one invalid-input example during the demo.”

### What is the most important limitation?

“The most important limitation is memory-only storage. It is suitable for a classroom sample, but real clinic use would need secure persistent storage, access controls, and an approved privacy and records process.”

### What was the most challenging part of the duo work?

“[Describe a real integration or design challenge, what you tried, and what you learned. Do not use a generic answer; agree on the example together.]”

## Final rehearsal checklist

- [ ] Replace every bracketed placeholder with accurate names, course information, responsibilities, and team-approved future improvements.
- [ ] Agree on who controls the computer and who narrates each step.
- [ ] Confirm the demo starts with the sample student's first-login state; restarting the program restores in-memory demo data.
- [ ] Practice student and nurse sign-in, profile review, assessment view, history, faculty visit, and report page.
- [ ] Rehearse a validation error and recovery without losing the demo flow.
- [ ] Make sure both members can explain every listed class and every demonstrated screen.
- [ ] Verify the current build on the presentation computer and confirm whether printing is available.
- [ ] Follow the instructor's official rubric and presentation time limit if they differ from this draft.

