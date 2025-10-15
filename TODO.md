### TODO Tasks

NEW_FEATURE: Add Habit Repository, Controller, DTO, Service, Model
- Add a form for users to create new habits, this form should have a days of the week (eg. "M,W,F or F, S, Su"), start date, end date, streak_status (int, the last n tasks in the past that where completed)
- Add a Task Repository, Model, Controller, Service. Each task has a status completed or not completed (could be boolean)
### SQL Realtionships
- Each task must be associated with a habit and each habit can be associated with zero or more tasks
- Each task must be associated with a user and each user can be associated with zero or more tasks
- Each habit must be associated with one user and each user can be associated with zero or more habits

### Frontned Tasks
 - Create a task card component that is very simple, only contains the name of the task. If the name is too big for the current screen it abbreaviates the task name like so: task name -> ta...
 - In the calendar view on the left of the calendar add a column with all the habits that the user has. Each habit with a different color. This column disappears for smaller devices
 - In the calendar view, fetch the tasks for the current week and place the task card in the respective 
 - Make a habit view where a user can edit the days of the week they want to do the tasks associated with the habit and also delete the habit.
 - Makle a task view where a user can click a cool button to mention that they have completed the task, they can also edit the task name

IMRPOVEMENT - add tests for Notes

NEW_FEATURE: Add user created reminders for each task completion
these will be email sent and can also be seen in the application

KEY_FEATURE: add AI plans recommendations - create habits or routines that will help the user
acomplish their goals

KEY_FEATURE: Based on Plans, create smaller goals or tasks to achieve a goal

NEW_FEATURE: add google authentication
NEW_FEATURE: add JWT authentication
