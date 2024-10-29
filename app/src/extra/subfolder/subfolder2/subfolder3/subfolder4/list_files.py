def print_tree_structure(target_dir, output_path):
    # Ensure the directory exists
    if not os.path.exists(target_dir):
        print(f"Directory {target_dir} does not exist.")
        return

    # Ensure the output directory exists (we check the directory, not the file itself)
    output_dir_name = os.path.dirname(output_path)
    if not os.path.exists(output_dir_name):
        print(f"Directory for output path {output_dir_name} does not exist.")
        return

    prefix = '|-- '  # Right before file and folder name
    indent_prefix = '|   '  # before the prefix, depending on level of file/folder

    # Create a temporary list to store the output
    output = []

    for root, dirs, files in os.walk(target_dir):  # os.walk generates efile names in dir tree
        # depth of current dir in the loop, relative to target_src_dir (example: 1 or 2)
        level = root.replace(target_dir, '').count(os.sep)

        # creates indented_prefix_string prefix (along with file prefix) string
        # based on depth of current dir in the loop
        indented_prefix_string = indent_prefix * level + prefix

        # Print folder names
        output.append('{}{}/\n'.format(indented_prefix_string, os.path.basename(root)))

        # for representing files
        sub_indent = indent_prefix * (level + 1) + prefix

        # Directories to Ignore
        exclude_dirs = [
                '__pycache__', 
                'flask_session', 
                '.git', 
                '.gradle', 
                '.idea'
            ]
        for exclude in exclude_dirs:
            if exclude in dirs:
                dirs.remove(exclude)

        # Check if current directory is inside icons folder
        if 'icons' in root:
            for d in dirs:

                # Just add the 'icons' directory (without the actual icons)
                sub_indent = indented_prefix_string + indent_prefix
                output.append('{}{}/\n'.format(sub_indent, d))
                output.append('{}## icons\n'.format(sub_indent + indent_prefix))

            # Skip processing files in the icons directory
            dirs[:] = []

        # Special handling for 'app/build' directory
        if os.path.basename(root) == 'build' and 'app' in root:
            for d in dirs:
                output.append('{}{}/\n'.format(sub_indent, d))
            # Skip further traversal into 'build' subdirectories and files
            dirs[:] = []
            continue
            
        # Print file names for other directories
        for f in files:
            # Print file names
            output.append('{}{}\n'.format(sub_indent, f))

    # Write the output to the file
    with open(output_path, 'w') as file:
        file.writelines(output)

    print(f"Output saved in: {output_path} \n")


if __name__ == "__main__":
    import os

    # Get the directory where the script is located (it should be in 'extra')
    script_dir = os.path.dirname(os.path.abspath(__file__))
    print(f"script_dir: {script_dir} \n")

    # If 'script_dir' is in extra, navigate up the required levels from 'Extra' all the way to 'src' folder
    src_dir = os.path.join(script_dir, "..", "..", "..", "..", "..", "..", "src")

    # Join the src_dir path to navigate to 'routineturboa'
    target_src_dir = os.path.join(src_dir, "main", "java", "com", "app", "routineturboa")
    print(f"target_src_dir: {target_src_dir} \n")

    # to be used when I need files from some other directory
    alt_target_src_dir = "C:\\Users\\vivid\\Documents\\C-Creative Projects\\RoutineTurboA"

    # Output_dir can be anything, ideally same as 'script_dir' if it is 'extra'
    output_file_path = os.path.join(script_dir, 'files.txt')

    # Print the tree structure
    print_tree_structure(target_src_dir, output_file_path)
