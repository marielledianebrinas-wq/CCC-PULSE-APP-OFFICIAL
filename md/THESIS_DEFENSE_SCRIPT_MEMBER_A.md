# CCC Pulse App - Defense Script for Member A

**Presentation order:** objectives first, then the complete GUI walkthrough.  
**Member A:** [Full name]  
**Member B:** [Full name]  
**Course/section:** [Course and section]

Replace bracketed items with accurate details. Member A introduces the project and demonstrates the user interface. Member B takes over after the GUI walkthrough to explain implementation, Java Swing, the backend, and OOP. Both members should be ready to answer questions about the full system.

## 1. Opening and project objectives

“Good [morning/afternoon], respected panel members. We are [Member A] and [Member B]. Our project is CCC Pulse App, an Automated School Clinic Physical Assessment System developed using Java Swing.”

“Students may need a physical assessment approved by the school nurse before joining activities such as sports events, competitions, or school travel. The clinic also needs a clear record of the student's health assessment and clinic visits.”

“Our objectives are:

1. To provide a consistent profile and assessment workflow for students.
2. To record physical measurements, medical history, allergies, health concerns, medical certificate copy status, the nurse's decision, and remarks.
3. To keep assessment history and show when an assessment reaches its six-month validity period.
4. To let students review their assessment and clinic visit history.
5. To let the nurse manage student assessments and clinic visits, including faculty visits that do not require a personal account.
6. To provide a separate page for printing or saving clinic reports.”

“The system is a classroom prototype. It uses sample data held in memory, so changes are cleared when the program closes. It does not send actual remote notifications or store certificate scans.”

## 2. Explain the general GUI layout

“Before showing each page, I want to explain the common layout. The system uses a light interface with navy navigation, a main content area, and a header. The breadcrumb at the top indicates the current page. The left navigation gives access to the major sections. On smaller windows, a menu button opens the navigation. Account options are available from the user menu.”

## 3. Student sign-in and first-login flow

“We will start as a student. The sample student ID is `25-00001` and the initial password is `pulse123`.”

**Action:** Sign in as `25-00001` / `pulse123`.

“At first sign-in, the student first sees a profile review page. This gives the student a chance to check the details before continuing. The profile can be edited. Last name and first name are required; middle name and suffix are optional.”

**Action:** Show the profile review. Select **Edit profile**, point out the fields, and return to the review page. Save an edit only if you intend to change the sample data for this presentation.

“The student then sets and confirms a new password. The show-password control can reveal the entered password. A minimum length and confirmation check help prevent an incomplete password setup.”

**Action:** Set and confirm a demo password, then continue.

## 4. Student portal GUI walkthrough

“The student portal has six sections in its navigation.”

### Dashboard

“The dashboard greets the student using the first name from the profile. It displays clearance status, clinic visit count, and whether the enrollment medical certificate copy is on file. When the assessment is missing or expired, the dashboard shows a reminder to visit the nurse.”

**Action:** Point to the first-name greeting, the three status cards, and the reminder if displayed.

### Physical Assessment

“This page lets the student review the latest clearance, its assessment date and valid-through date, and the nurse's remarks. If there is no current assessment, it asks the student to visit the nurse. The student does not choose the fitness decision.”

### Medical Records

“This page displays the student's physical assessment history in a table. The columns include check-up date, valid-through date, height, weight, blood pressure, oxygen level, blood type, fitness, and certificate-copy status. Details such as history, allergies, current concerns, and nurse remarks are shown with the history.”

### History

“History brings the student's assessment information and clinic visit timeline together, so the student can review prior care.”

### School Clinic Records

“This page shows clinic visits associated with the student. The student can log a visit reason for the nurse to review. Nurse remarks and treatment are recorded by the clinic.”

### Settings

“Settings is where the student can update their profile and change their password. Health-status changes should be discussed with the school nurse.”

## 5. Nurse portal GUI walkthrough

“Next, I will show the nurse interface. I will sign out and log in as the nurse. The nurse account for this demo is `nurse` / `nurse123`.”

**Action:** Sign out and sign in as the nurse.

“The nurse navigation has Dashboard, History, Students, Medical Records, Non-students, and Settings. The dashboard summarizes the number of student profiles, assessments that are due, and clinic visits.”

### Students and add profile

“Students displays a directory with student ID, name, course, year, check-up date, valid-through date, and status. The nurse can add a student with the structured profile page. It asks for student ID, name, course and year, and contact information.”

**Action:** Show the student list. If time allows, open the add-student page and explain the name fields; do not save a duplicate profile.

### Nurse assessment form

“From the student list, the nurse selects a student and chooses **Record assessment**. The form is arranged in sections for the student, measurements and health details, medical history and allergies, current concerns, certificate copy, fitness decision, and nurse remarks.”

“Medical history and allergy questions use Yes/No choices, with a description when the answer is Yes. The nurse records the measurements and selects the fitness decision.”

**Action:** Select a student and open the assessment form. Point to the checklist fields and fitness options. Save a new assessment only if appropriate for the demo.

### Medical Records and History

“The nurse's Medical Records page shows student assessment records. History shows recent clinic activity. The nurse can open a visit and update remarks or treatment.”

### Non-students

“Non-students is for faculty or other people who need clinic attention without having a student account. The nurse can record their visit separately.”

### Report page

“When the nurse wants a formatted record, the report opens on a separate page. It can be printed or saved as a text file.”

**Action:** Open a report page. Print only if the presentation computer has a printer configured; otherwise, show the page and describe the save option.

## 6. Validation example

“The forms check required information before saving and give the user feedback if something needs correction. For example, the assessment requires numeric height and weight within the accepted ranges, and a clinic visit requires a reason.”

**Action:** Leave a required field blank or enter non-numeric height/weight and attempt to save. Show the message, then correct the input or cancel the form.

## 7. Handoff to Member B

“That completes the objectives and GUI walkthrough. I will hand over to [Member B], who will explain how the screens connect to the backend, which Java Swing components we used, and how the classes and methods implement the system.”

## Rehearsal checklist for Member A

- [ ] Fill in member names and course details.
- [ ] Rehearse the objectives without reading too quickly.
- [ ] Practice the student sign-in, first-login setup, and each student navigation page.
- [ ] Practice the nurse navigation, assessment form, visit history, non-student page, and report page.
- [ ] Coordinate screen hand-offs with Member B.
- [ ] Confirm the demo resets to the sample student state before the defense.
- [ ] Avoid saving unwanted changes or duplicate profiles during the demo.
- [ ] Leave code and backend explanations for Member B, but prepare to answer questions about the overall system.

