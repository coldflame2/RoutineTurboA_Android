import os

def print_tree_structure(target_dir, output_path, prefix='', indent_prefix='|   '):
    # Ensure the directory exists
    if not os.path.exists(target_dir):
        print(f"Directory {target_dir} does not exist.")
        return

    prefix = '|-- '  # Right before file and folder name
    indent_prefix = '|   '  # before the prefix, depending on level of file/folder

    # Create a temporary list to store the output
    output = []

    for root, dirs, files in os.walk(target_dir):  # os.walk generates file names in dir tree
        # depth of current dir in the loop, relative to target_dir (example: 1 or 2)
        level = root.replace(target_dir, '').count(os.sep)

        # creates indented_prefix_string prefix (along with file prefix) string based on depth of current dir in the loop
        indented_prefix_string = indent_prefix * level + prefix

        # Print folder names
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
                output.append('{}{}/\n'.format(sub_indent, d))
                output.append('{}## icons\n'.format(sub_indent + indent_prefix))
            # Skip processing files in the icons directory
            dirs[:] = []

        for f in files:
            # Print file names
            output.append('{}{}\n'.format(sub_indent, f))
        
        print(output)

    # Write the output to the file
    with open(output_path, 'w') as file:
        file.writelines(output)

if __name__ == "__main__":
    import sys
    script_dir = os.path.dirname(os.path.abspath(__file__))

    # Directory you want to print the tree structure for, relative to the script location
    relative_path = os.path.join('app', 'src', 'main', 'java', 'com', 'app', 'routineturboa')
    abs_path = os.path.abspath('')
        
    output_path = os.path.join(abs_path, 'list_files_output.txt')


    # Print the tree structure
    print_tree_structure(abs_path, output_path)

    
