# Task-Tracker-CLI
The Task Tracker CLI project is a command-line interface (CLI) application designed to help users track and manage their tasks. This tool allows users to add, update, delete tasks, and mark them as in-progress or completed. All tasks are stored in a JSON file, ensuring easy management and access to data.

Key Features  
* Add tasks: Users can add new tasks using the command `add "task_description"`  
* Update and delete tasks: Commands `update [id] "new description"` and `delete [id]` allow users to update and delete tasks, respectively  
* Mark tasks: Commands `mark-in-progress [id]`, `mark-todo [id]` and `mark-done [id]` allow users to mark tasks as in-progress, to do or completed  
* List tasks: The command `list` displays all tasks, while `list done`, `list todo`, and `list in-progress` filter tasks by status

Task Properties  
* id: A unique identifier for the task
* description: A short description of the task
* status: The status of the task (todo, in-progress, done)
* createdAt: The date and time when the task was created
* updatedAt: The date and time when the task was last updated

https://roadmap.sh/projects/task-tracker
