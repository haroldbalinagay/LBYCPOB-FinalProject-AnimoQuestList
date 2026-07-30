# PLANNING NOTES

## FEATURES LIST

- Spring Boot & Supabase (web app & database)
- HTML (browser layout of each page)
- CSS (browser styling)

_--- OR ---_

- JavaFX (desktop app)
- FXGL (for complex visualization, if needed)
- Spring Boot & Supabase (database)
- FXML (easy to use with Scene Builder)
- CSS (styling)

### PRIORITY !!!

| Done? | Member Assigned | Feature | Subfeatures |
| --- | --- | --- | --- |
|  |  | **Different user types:** Admin, Student
|  |  | **Login system UI (Admin / Student)** -> login info saved in database
|  |  | **Course Editor**
|  |  | | Screen with controls for creating, editing, deleting _(only for admin; could have search function AND can select the course directly from the checklists)_
|  |  | | Handles setting H/S/C requisites -> app updates course relationship table
|  |  | | Handles assigning courses to Term checklists _(if not directly editing Term)_ -> app updates Term checklist table
|  |  | | Handles setting # of units (or just pass/fail), course code, name, etc. -> app updates course info table
|  |  | **Term Checklist Visualization**
|  |  | | Admin can edit Term to directly add _(can show list of courses NOT found in any Term to choose from)_ / remove _(x button)_ / reorder courses _(arrow buttons?, drag & drop?)_
|  |  | | Admin can add / remove / reorder Terms _(similar buttons as above)_
|  |  | | Shows all courses (in the form of boxes) of the Term
|  |  | | idea: Shows all requisites from previous Term on the LEFT _(distinct arrows)_, all dependencies for the next Term on the RIGHT
|  |  | | Nav buttons to move between the Terms
|  |  | **Course Visualization (in checklists)**
|  |  | | Shows course code, name, units, requisites, status (all in a box container; see inspiration below)
|  |  | | Changes border & fill colors for statuses: passed, failed, in-progress
|  |  | | Has button to plan to enroll in it _(except if already passed)_ -> app checks if user has complete PRErequisites -> if missing reqs, gives warning message / "!" indicator somewhere
|  |  | | Has button to open Requisite Visualization popup
|  |  | **Requisite Visualization**
|  |  | | Small popup window that shows the target course & all of its requisites (with labeled / distinct arrows)
|  |  | | 
|  |  | **Enrollment Planning List (USER)**
|  |  | | Shows a list of all "planned to enroll in" courses
|  |  | | Shows "!" warning indicator if missing reqs
|  |  | | Has buttons to remove course, carry over to Course Status Editor _(meaning actually enrolled in Term)_.
|  |  | **Course Status Editor (USER)**
|  |  | | User can update statuses of actually enrolled courses: in-progress, passed, failed
|  |  | | 
|  |  | | 
|  |  | | 

### ADDITIONAL (if have time)
- [ ] User type: Faculty _(but what's the benefit?)_
- [ ] Requisite Visualization: Include courses that depend on a course as their requisite

---
## DATABASE STRUCTURE
- Table 1

| col | col | col |
| --- | --- | --- |
| text | text | text |


- Table 2

| col | col | col |
| --- | --- | --- |
| text | text | text |

---
## PROGRAM FLOW
- test

---
