import os

def combine_kt_files(file_names):
    # Script combine.py should be located at: RoutineTurboA\app\extras

    # This is where the combine.py is. It gives absolute path.
    script_path_or_current_working_dir_abs_path = os.getcwd()

    # Get relative (which will print only a dot, because it's cwd)
    script_or_cwd_relative = os.path.relpath(script_path_or_current_working_dir_abs_path)

    # Move up a directory from a working directory to go to base
    # This is the main starting point of this Android Project. It should be: 'RoutineTurboA'
    base_directory = os.path.join(script_or_cwd_relative, '..')

    kotlin_files_path = os.path.join(base_directory, 'src', 'main', 'java', 'com', 'app', 'routineturboa')
    output_file_path = os.path.join(script_or_cwd_relative, 'combined.txt')

    build_gradle_file = os.path.join(kotlin_files_path, 'build.gradle.kts')
    manifest_file = os.path.join(kotlin_files_path, 'AndroidManifest.xml')

    # Check if main source directory exists
    if not os.path.exists(kotlin_files_path):
        print(f"Source for kotlin files not found: {kotlin_files_path}")
        return

    def write_file_contents_without_imports(infile, outfile):
        included_line_count = 0
        skipped_line_count = 0

        for line in infile:
            if not line.strip().startswith('import'):
                outfile.write(line)
                included_line_count += 1
            else:
                skipped_line_count += 1

    with open(output_file_path, 'w') as outfile:
        if os.path.exists(manifest_file):
            print(f"Including {manifest_file}")
            with open(manifest_file, 'r') as infile:
                outfile.write(f"\n// Content from {manifest_file}\n")
                write_file_contents_without_imports(infile, outfile)
                outfile.write("\n")

        if os.path.exists(build_gradle_file):
            print(f"Including {build_gradle_file}")
            with open(build_gradle_file, 'r') as infile:
                outfile.write(f"\n// Content from {build_gradle_file}\n")
                write_file_contents_without_imports(infile, outfile)
                outfile.write("\n")

        for root, _, files in os.walk(kotlin_files_path):
            for file in files:
                if file.endswith('.kt') and file in file_names:
                    file_path = os.path.join(root, file)
                    print(f"Including {file}\n")
                    with open(file_path, 'r') as infile:
                        outfile.write(f"\n// Content from {file}\n")
                        write_file_contents_without_imports(infile, outfile)
                        outfile.write("\n")

if __name__ == '__main__':

    file_names = [
        'MainActivity.kt',
        # Add other filenames as needed
    ]

    file_names_all = [
        'AndroidManifest.xml',

        'MainActivity.kt',
        'RoutineTurboApp.kt',
        'RoutineDatabase.kt',
        'RoutineRepository.kt',
        'TaskDao.kt',
        'TaskEntity.kt',
        'downloadFromOneDrive.kt',
        'MsalAuthManager.kt',
        'OneDriveManager.kt',
        'ReminderManager.kt',
        'ReminderReceiver.kt',
        'ScheduleReminders.kt',
        'CustomTextField.kt',
        'DottedLine.kt',
        'SignInItem.kt',
        'TaskCardPlaceholder.kt',
        'MainScreen.kt',
        'TasksLazyColumn.kt',
        'BottomNavBar.kt',
        'MainDrawer.kt',
        'MainTopBar.kt',
        'QuickEdit.kt',
        'TaskCard.kt',
        'TaskDropdown.kt',
        'AddTaskDialog.kt',
        'FullEditDialog.kt',
        'TaskDetailsDialog.kt',
        'Converters.kt',
        'demoTasks.kt',
        'NotificationPermissionHandler.kt',
        'SineEasing.kt',
        'TimeUtils.kt',
        'TasksViewModel.kt',
        'TaskViewModelFactory.kt'
    ]

    # Call the function with the directory and filenames
    combine_kt_files(file_names_all)



"""

"""