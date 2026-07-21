# LBYCPOB-FinalProject-AnimoQuestList

## PROJECT TITLE:

AnimoQuestList: A Web App Designed to Aid Students in Choosing the Courses Taken Per Term Based on Their Inputs

---

## TEAM MEMBERS:

BALINAGAY, Harold Jay E.  - haroldbalinagay

GERILLA, Edwin Jr. C.    - edwingerilla-cmd

SZE, Timothy Emmanuel U. - TimothySze1254

---

## PROBLEM STATEMENT & GOALS:

Problem Statement: When a student fails one or more subjects, they are required to manually review their course checklist to determine which courses need to be retaken and to update their academic plan accordingly. This manual process is time-consuming, prone to error, and may lead to missed prerequisites or scheduling conflicts, increasing the student’s academic burden and risk of delayed graduation.

Goals: To ease the burden on the students and automate their preferred course checklist according to their inputs.

---

## TARGET USER:

This program aims to support both DLSU students and faculty by providing a simple tool to make their lives easier when it comes to managing the courses they plan to take in the upcoming terms. 

---

## BRIEF DESCRIPTION:

AnimoQuestList is an intelligent application designed to eliminate human error from academic planning. By capturing a user’s completed, failed, and in-progress subjects, the engine builds an internal dependency graph of the curriculum. It then automatically computes tailored, term-by-term schedules based on constraints such as maximum allowed units, prerequisite flags, and user choice. The dashboard for students shows their current progress on their program along with their projected term for graduation, assuming they don't fail any more classes. For the administrator side, they can view or modify the student checklist. Administrators can also view some statistics such as subjects that are commonly failed and factors that may affect the rate at which students pass, such as a heavy workload for the given term.

---

## CORE OOP CONCEPTS:
- Encapsulation: Apply encapsulation on the user class so user data such as login credentials or their current progress on the checklist can be accessed only by the intended users.
- Inheritance: Create interfaces or abstract classes, such as User, so classes like Administrator and Student can inherit characteristics to be shared between the two.
- Polymorphism: Polymorphism will be used for modifying shared methods through inheritance, such as getDashboard, which will be different depending on the type of user.
- Abstraction: As mentioned earlier in inheritance, a user abstract class  will be inherited by the administrator and student classes to get methods to be used in both that have different characteristics depending on the class.

---

## INITIAL CLASS IDEAS:

Abstract Class:
- `User` -  
  
Classes:
- `Course` - Class that creates course objects with the characteristics needed, such as PASSED/FAILED, HARD PREREQUISITE/SOFT PREREQUISITE, etc.
- `Student` - Manage the data of the students logging in or out, containing all relevant data to their needs on the program.
- `Checklist` - Class that creates checklist objects that contain the necessary courses for a student to graduate, can be modified by admin to reflect updates.
- `Admin` - Can manage the checklist by modifying the courses.

---

## USER STORIES:
> As a student, I want to have a tool that can let me visualize my current curriculum progression, so that I can better plan for my future subjects.

> As a student, I want to look at a visual graph of my course subjects instead of reading through a checklist, as it is much more convenient and intuitive.

> As a student, I want to have a visual graph that shows how different subjects are connected to one other (ie. hard / soft prerequisites, corequisites), so that I will not take subjects which I do not yet have the complete requisites.

> As a student, I want to have a tool that allows me to select which subjects to take next Term based on my current course progression, which would be quite convenient and a time saver.

> As a student, when I have a failed subject/s, I want to have a tool that can automatically update my course checklist and suggest the courses to take in succeeding Terms, so that I will not fall behind in my course.

> As a student, I want all of the aforementioned features to be accessible in a web application, so that I can access these features on any device.

> As an admin, I want to be able to easily create and update course checklists, so that students will be immediately informed of any changes.

> As an admin, I want to see some statistics on how many students are taking or have taken each course, and how many passed or failed each course for a given batch of students (eg. ID 125 only), so that we may adjust our checklists accordingly.

---

## CORE FEATURES:
- Allow administrators to edit current course offerings.
- Create optimized workloads to help students catch up to the checklist.
- Shift functionality allowing one to carry over PASSED courses onto their next checklist.
- Show some Statistics for the administrator side to view.
