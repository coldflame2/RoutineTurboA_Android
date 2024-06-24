import os
import sys

"""
HOW TO USE:
Set the working directory to 'src' in run configuration.
"""


def list_files_in_tree_structure(target_dir, output_file):
    prefix = '|-- '  # Right before file and folder name
    indent_prefix = '|   '  # before the prefix, depending on level of file/folder

    # Create a temporary list to store the output
    output = []

    for root, dirs, files in os.walk(target_dir):  # os.walk generates file names in dir tree
        # depth of current dir in the loop, relative to target_dir (example: 1 or 2)
        level = root.replace(target_dir, '').count(os.sep)

        # creates indented_prefix_string prefix (along with file prefix) string based on depth of current dir in the loop
        indented_prefix_string = indent_prefix * level + prefix

        # for space before folder name
        print(indent_prefix + indent_prefix)
        output.append(indent_prefix + indent_prefix + '\n')

        # Print folder names
        print('{}{}/'.format(indented_prefix_string, os.path.basename(root)))
        output.append('{}{}/\n'.format(indented_prefix_string, os.path.basename(root)))

        # for representing files
        sub_indent = indent_prefix * (level + 1) + prefix

        # ignore pycache
        if '__pycache__' in dirs:
            dirs.remove('__pycache__')
        if 'flask_session' in dirs:
            dirs.remove('flask_session')

        # Check if current directory is inside icons folder
        if 'icons' in root:
            for d in dirs:
                sub_indent = indented_prefix_string + indent_prefix
                print('{}{}/'.format(sub_indent, d))
                output.append('{}{}/\n'.format(sub_indent, d))
                print('{}## icons'.format(sub_indent + indent_prefix))
                output.append('{}## icons\n'.format(sub_indent + indent_prefix))
            # Skip processing files in the icons directory
            dirs[:] = []

        for f in files:
            # Print file names
            print('{}{}'.format(sub_indent, f))
            output.append('{}{}\n'.format(sub_indent, f))

    # Write the output to the file
    with open(output_file, 'w') as file:
        file.writelines(output)

if __name__ == "__main__":
    start_directory = os.path.abspath('./app/routineturboa')  # get_abs_path works for files inside src

    # Construct the output path relative to the project root
    base_dir = os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..'))
    output_path = os.path.join(base_dir, 'files_tree_structure.txt')

    list_files_in_tree_structure(start_directory, output_path)

"""
Detailed Explanation

The function list_files_in_tree_structure takes a single argument target_dir, which is the directory whose structure we want to list.

Setting up prefixes: prefix and folder_prefix are defined to format the output in a tree structure.
prefix = '|-- ' is used to denote files and folders.
folder_prefix = '| ' is used for indentation to visually represent the hierarchy.

Walking the directory tree with os.walk; os.walk(target_dir) generates the file names in a directory tree by walking either top-down or bottom-up. 
It yields a tuple (root, dirs, files) for each directory in the tree rooted at target_dir.

Handling __pycache__: If the directory __pycache__ is found in dirs, it is removed from the list to avoid displaying it.

Determining the indentation level:
level = root.replace(target_dir, '').count(os.sep) calculates the depth of the current directory relative to target_dir by counting the number of path separators (os.sep).

This depth (level) is used to determine how much indentation to apply.

Printing the directory:

indent = folder_prefix * level + prefix creates the indentation string for the current directory.
print('{}{}/'.format(indent, os.path.basename(root))) prints the directory name with the correct indentation.
Printing the files:

sub_indent = folder_prefix * (level + 1) + prefix creates the indentation string for files inside the current directory.
The loop for f in files iterates through the files and prints each one with the appropriate indentation.
Why It Works the Way It Works
Directory Structure Setup
The function expects a certain structure based on the relative path provided by get_abs_path.

Explanation of get_abs_path
Bundled mode check:

if getattr(sys, 'frozen', False) checks if the script is running in a bundled mode (e.g., with PyInstaller). If so, sys._MEIPASS is used as the base path.
Normal Python environment:

If not in bundled mode, base_path = os.path.abspath('../src') sets the base path to the absolute path of the ../src directory relative to the script's location.
Constructing the absolute path:

return os.path.join(base_path, relative_path) constructs and returns the absolute path by combining the base_path and the provided relative_path.
Why You Need to Set the Working Directory to 'src' in PyCharm
When the working directory is set to 'src', os.path.abspath('../src') resolves to the correct base path relative to 'src'.
If the working directory is not set to 'src', the relative path ../src may not correctly resolve to the expected directory structure, causing the function to list files from an incorrect or unintended location.

"""
