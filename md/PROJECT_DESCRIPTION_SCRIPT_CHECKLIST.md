# CCC Pulse App - Project Description, Demo Script, and Completion Checklist

Prepared against pages 18-26 of the Java Swing Final Project Tutorial. This draft describes the current sample application and flags items that still need team or course information.

## Pages 18-26: what applies to this project

| Tutorial page | Topic | CCC Pulse application |
|---|---|---|
| 18 | Workflow from idea to defense | The idea, requirements, initial class design, Swing screens, implementation, and draft documentation are present. Test evidence and a rehearsed defense still need completion. |
| 19 | Duo work distribution | Use the suggested UI/validation and OOP/business-rule leads as a starting point; enter the real members and contributions below. Both members must be ready to explain the whole application. |
| 20 | Minimum functional requirements | The project has multiple meaningful features and classes, encapsulated fields, constructors/methods, record relationships, Swing event handlers, validation, and documented example test cases. Test execution/results remain unrecorded. |
| 21 | Documentation checklist | The title, problem, scope, and feature description exist. The checklist below identifies missing team details, screenshots, code/design explanations, test results, and future improvements. |
| 22 | Final presentation/defense | A student-to-nurse demo and validation/OOP explanation script are included below. Personal roles and actual demo evidence need team input. |
| 23 | Suggested evaluation criteria | The tutorial proposes functionality, OOP, GUI/usability, testing, documentation, and defense as review areas. Use the instructor's official weights if they differ; do not treat the tutorial's suggested percentages as the official course rubric. |
| 24 | Duo project planning activity | The problem, users, features, classes, and screen flow are summarized in the description and pitch. Confirm that the instructor/team approved this concept. |
| 25 | Quick project pitch | A filled-in pitch is provided below. |
| 26 | Project proposal checklist | The checklist at the end marks what is known and leaves member/approval confirmation open. |

### Suggested self-review areas (not an official grade)

- Functionality: confirm every demonstrated workflow saves and displays the intended in-memory records.
- OOP: explain encapsulation, constructors, methods, and how IDs connect accounts, people, assessments, and visits.
- GUI/usability: review navigation, form labels, validation feedback, and the printable report page.
- Testing: record actual outcomes for valid and invalid inputs; retain requested evidence.
- Documentation: complete screenshots, code/design notes, limitations, and future improvements.
- Presentation/defense: rehearse the workflow and make sure both members can explain the code.

## Project description

### Project title

**CCC Pulse App - Automated School Clinic Physical Assessment System**

### Problem statement

Students who join sports, travel, competitions, and other school activities may need a current physical assessment reviewed by the school nurse. Without a central clinic record, staff can have difficulty checking a student's medical history, allergies, certificate-copy status, fitness decision, assessment validity, and prior clinic visits. CCC Pulse provides a desktop prototype for recording these details and reviewing them by student.

### Objectives

- Record student profile and academic information in a consistent form.
- Let the nurse record physical measurements, medical history, allergies, current concerns, certificate-copy receipt, fitness decision, and remarks.
- Track assessment history and its six-month validity period.
- Let students review their assessment and clinic-visit history.
- Record clinic visits, nurse remarks, and whether treatment was provided.
- Keep faculty/non-student clinic visits in a separate record without requiring a faculty account.
- Provide a designed report page that can be printed or saved as text.

### System overview and scope

CCC Pulse is a Java Swing desktop sample with student and nurse views. A student can sign in, complete first-login setup, review or edit their profile, view the dashboard and records, and log a clinic visit. A nurse can manage student profiles, record assessments, review records and visits, update visit remarks and treatment, and record faculty visits. The application calculates a six-month assessment validity period and displays a due status on the student dashboard.

The application stores data in memory only. Records are lost when it closes. It does not use a database or provide actual remote access, email/SMS notifications, appointment booking, certificate-file upload, or production-grade authentication. The six-month reminder is displayed in the app when the student views the dashboard; it is not pushed to the student.

### Main users

- **Student:** reviews their profile, assessment status, medical record history, and clinic visits; can record a visit request for nurse follow-up.
- **School nurse:** manages student data and assessments, reviews clinic history, records nurse remarks and treatment, and records faculty/non-student visits.

### Core features demonstrated

1. Student and nurse sign-in with role-specific navigation and first-login password setup for the demo student.
2. Student profile review and editing, with required last name, first name, and contact number; middle name and suffix are optional.
3. Nurse physical assessment entry, including measurements, conditional medical-history/allergy details, certificate-copy status, fitness decision, remarks, and six-month validity.
4. Assessment and clinic-visit history, including nurse remarks and treatment.
5. Separate printable report page and text export, plus faculty/non-student visit tracking.

### Important classes and relationships

- `Account` stores a username, password, role, student link, and first-login password-change flag.
- `Person` stores student or faculty profile details.
- `HealthRecord` stores a dated assessment and computes its six-month expiry date.
- `ClinicVisit` stores the visit reason, nurse remarks, treatment, person ID, and date.
- `ClinicStore` holds the in-memory accounts, profiles, assessments, and visits and provides demo data.
- `Role` distinguishes student and nurse accounts.
- `LoginFrame`, `FirstLoginFrame`, and `MainFrame` provide sign-in, first-login setup, and role-based application screens.
- `ReportPage` presents a printable view and supports text export.
- `Theme` centralizes colors, fonts, and reusable visual components.

`ClinicStore` relates accounts and people to assessment and visit records by their IDs. `MainFrame` uses those records to render role-specific pages. `ReportPage` uses the selected person's profile and clinic records to compose a report.

### Suggested 60-second project pitch

“Our project is CCC Pulse, a Java Swing school clinic physical assessment system. Students need nurse-reviewed physical assessments before some school activities, and clinics also need a clear history of health checks and visits. CCC Pulse gives students a way to review their assessment status and visit history, while the school nurse can manage student profiles, record assessments, make the fitness decision, and note treatments or remarks. The system tracks the six-month assessment period and provides a printable report. We used classes such as `Person`, `Account`, `HealthRecord`, `ClinicVisit`, and `ClinicStore` to model the people and records. This version is an in-memory classroom prototype, so it demonstrates the workflow without persistent storage.”

## Presentation and live demo script

Replace the member placeholders and rehearse the hand-offs before presenting. The script follows the tutorial's suggested order: problem and solution, complete workflow, OOP, validation, and team roles.

### 1. Introduce the problem and solution

**Presenter:** “Students joining activities may need a current physical assessment. CCC Pulse helps the school nurse record assessments and clinic visits, while students can review their own clearance and history. Our sample uses in-memory data to demonstrate the workflow.”

### 2. Demonstrate the student workflow

1. At sign-in, use the demo student account: **student ID `25-00001`, password `pulse123`**.
2. On first sign-in, review the profile. Select **Edit profile**, explain the required and optional name fields, save, and return to the profile review.
3. Continue to password setup. Show the password visibility control, enter a new password with at least six characters, confirm it, and continue.
4. On the dashboard, point out the first-name greeting, assessment status, clinic visit count, and medical-certificate-copy status.
5. Open **Physical Assessment**, **Medical Records**, **History**, and **School Clinic Records**. Explain what the student can see and that health-status changes must be reviewed by the nurse.

### 3. Demonstrate the nurse workflow

1. Sign out and sign in as **nurse / `nurse123`**.
2. Open **Students** and show the directory and student profile flow.
3. Select a student and open the physical assessment form. Explain the checklist questions, conditional description fields, certificate-copy status, and nurse-only fitness decision and remarks.
4. Save a sample assessment and point out that the check-up date and six-month validity are recorded.
5. Open **Medical Records** and **History** to show stored assessment and visit records. Open a visit to add or update nurse remarks and treatment.
6. Open **Non-students** and show how a faculty clinic visit can be recorded without a faculty login account.
7. Open **Print / Save report** to show the separate report page, then demonstrate printing or text export if an available printer/file location is configured.

### 4. Show validation and explain OOP

**Validation scenario:** In a form that requires a reason or required profile fields, leave the required field blank and attempt to save. Explain the validation message and that incomplete information is not accepted. If time allows, also demonstrate a mismatched password confirmation or invalid height/weight entry.

**OOP explanation:** “`Person` holds profile information; `Account` holds sign-in and role information; `HealthRecord` stores an assessment and its expiry; `ClinicVisit` stores visit details and can update remarks and treatment; `ClinicStore` connects these records in memory. The Swing frames use these classes to display and update the records.”

### 5. Explain team roles and limitations

**Presenter A:** “[Name] led [screens/UI or other agreed work] and contributed to [classes/features].”

**Presenter B:** “[Name] led [domain/OOP or other agreed work] and contributed to [classes/features].”

“Both members reviewed the overall workflow and should be ready to explain both the UI and the underlying records. This prototype resets to sample data after the app closes; persistent storage and actual remote notifications are future improvements.”

## Documentation and project completion checklist

Status is based on the source code and README currently in the workspace, not on a live test run or official instructor approval.

| Tutorial item | Current status | Remaining work |
|---|---|---|
| Project title | Present | Confirm the final title with the team/instructor. |
| Duo members identified | Missing from project files | Add both members' full names and course/section if required. |
| Problem statement | Present in README and this draft | Confirm it matches the team's approved problem. |
| Objectives | Drafted here | Team should approve or revise them. |
| System overview and scope | Present in README and this draft | Confirm intended scope and limitations. |
| Use-case/feature description | Present in README and this draft | Add a use-case diagram if required by the course. |
| Class diagram/UML | Partial Mermaid diagram in README | Verify relationships and include all classes used in the final build; export as a clear diagram if required. |
| GUI screenshots with explanations | Missing | Capture current login, student dashboard, assessment, nurse screens, and report page; add captions. |
| Important code snippets/design decisions | Partial | Select and explain the final code for expiry, validation, role navigation, and record relationships. |
| Test cases | Examples are listed in README | Turn them into a test table with actual inputs and observed outcomes. |
| Test results/evidence | Not recorded here | Run the agreed cases, record pass/fail and screenshots or other required evidence. |
| Limitations | Present in README and this draft | Confirm with the team; add any instructor-required risks or constraints. |
| Future improvements | Not yet listed explicitly | Decide which realistic next steps to include, such as persistent database storage or reminders. |
| Presentation/demo | Script drafted here | Replace placeholders, rehearse using the actual build, and confirm printer availability. |
| Duo work distribution | Missing names and actual contributions | Agree on truthful role descriptions; both members should understand the whole system. |
| Approved concept/proposal | Not evidenced in workspace | Attach or record instructor approval if the class requires it. |
| Both members can explain the idea | Cannot be confirmed from files | Rehearse the pitch and defense together. |

## Project proposal checklist (tutorial page 26)

- [ ] Duo members identified: **names needed**.
- [x] Project title selected: CCC Pulse App - Automated School Clinic Physical Assessment System.
- [x] Real-life problem identified: nurse-reviewed assessment and clinic record tracking.
- [x] Three to five core features listed above.
- [x] Initial classes identified above.
- [x] Rough screen flow planned: sign-in → first-login setup → student/nurse dashboard → records and forms → report.
- [ ] Both members have reviewed the idea and can explain it: **team confirmation needed**.
- [ ] Instructor/course approval recorded, if required: **approval status needed**.

## Information needed to complete the documentation package

Please provide or confirm:

1. Both team members' names and course/section (and which person led which work).
2. Whether your instructor requires specific items beyond pages 18–26, such as a use-case diagram, exported UML image, number of test cases, or a particular report format.
3. Whether the concept is already approved, and any official rubric or instructor feedback that should be followed.
4. Your actual test outcomes and screenshots, or confirmation that you want the current draft to remain a fill-in checklist until you run the demo.
5. Which future improvements you want to claim in the presentation (for example, database persistence, actual student notifications, or certificate uploads).