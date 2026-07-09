# LBYCPOB-FinalProject-AnimoQuestList

PROJECT TITLE:

AnimoQuestList: A Web App Designed to Aid Students in Choosing the Courses Taken Per Term Based on Their Inputs

TEAM MEMBERS:

BALINAGAY, Harold Jay E.  - haroldbalinagay

GERILLA, Edwin Jr. C.    - edwingerilla-cmd

SZE, Timothy Emmanuel U. - TimothySze1254

PROBLEM STATEMENT & GOALS:

Problem Statement: When a student fails one or more subjects, they are required to manually review their course checklist to determine which courses need to be retaken and to update their academic plan accordingly. This manual process is time-consuming, prone to error, and may lead to missed prerequisites or scheduling conflicts, increasing the student’s academic burden and risk of delayed graduation.

Goals: 

To ease the burden on the students and automate their preferred course checklist according to their inputs.

TARGET USER:

This program aims to support both DLSU students and faculty by providing a simple tool to make their lives easier when it comes to managing the courses they plan to take in the upcoming terms. 

BRIEF DESCRIPTION:


AnimoQuestList is an intelligent application designed to eliminate human error from academic planning. By capturing a user’s completed, failed, and in-progress subjects, the engine builds an internal dependency graph of the curriculum. It then automatically computes tailored, term-by-term schedules based on constraints such as maximum allowed units, prerequisite flags, and user choice. The dashboard for students shows their current progress on their program along with their projected term for graduation assuming they don't fail anymore classes. For the administrator side they can all view or modify all the student checklist. Administrators can also get view some statistics such as subjects that are commonly failed and factors that may affect the rate students pass such as a heavy workload for the given term.

CORE OOP CONCEPTS:
- Encapsulation: Apply encapsulation on the user class so user data such as log in credentials or their current progress on the checklist can be accessed only by the intended users.
- Inheritance: Creating interfaces or abstract classes, such as user, so classes like administrator and student can inherit characteristics to be shared between the two.
- Polymorphism: Polymorphism will be used for modifying shared methods between through inheritance such as getDashBoard which will be different depending on the tpye of user.
- Abstraction: As mention earlier in inheritance, a user abstract class  will be inherited by the administrator and student class to get methods to be used in both that have different characteristics depending on the class.

INITIAL CLASS IDEAS:

Abstract Class:
- Users:
  
Classes:
- Courses: Class that creates course objects with the characteristics needed such as PASSED/FAILED, HARD PREREQUISITE/SOFT PREREQUISITE etc.
- Students: Manage the data of the students logging in or out, containing all relavent data to their needs on the program.
- Checklist: Class that creates checklist objects that contain the neccessary courses for a student to graduate, can be modified by admin to reflect updates.

USER STORIES =:
- As a Student, I want to <action> so that <goal>.
- As a <user type>, I want to <action> so that <goal>.

CORE FEATURES:
- Allow administrators to edits current course offerings.
- Create optimized workloads to help students catch up to the checklist.
- Shift functionality allowing one to carry over PASSED courses on to their next checklist.
- SHow some Statistics for the administrator side to view.
