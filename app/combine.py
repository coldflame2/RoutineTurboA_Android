import os

def combine_kt_files(source_directory, main_source_directory, output_file, file_names):
    build_gradle_file = os.path.join(source_directory, 'build.gradle.kts')
    manifest_file = os.path.join(main_source_directory, 'AndroidManifest.xml')

    def write_file_contents_without_imports(infile, outfile):
        for line in infile:
            if not line.strip().startswith('import'):
                outfile.write(line)

    with open(output_file, 'w') as outfile:
        # Include AndroidManifest.xml if it exists
        if 'AndroidManifest.xml' in file_names and os.path.exists(manifest_file):
            with open(manifest_file, 'r') as infile:
                outfile.write(f"\n// Content from {manifest_file}\n")
                write_file_contents_without_imports(infile, outfile)
                outfile.write("\n")

        # Include build.gradle.kts if it exists
        if 'build.gradle.kts' in file_names and os.path.exists(build_gradle_file):
            with open(build_gradle_file, 'r') as infile:
                outfile.write(f"\n// Content from {build_gradle_file}\n")
                write_file_contents_without_imports(infile, outfile)
                outfile.write("\n")

        # Include specified .kt files from main source directory
        for root, _, files in os.walk(main_source_directory):
            for file in files:
                if file.endswith('.kt') and file in file_names:
                    file_path = os.path.join(root, file)
                    with open(file_path, 'r') as infile:
                        outfile.write(f"\n// Content from {file_path}\n")
                        write_file_contents_without_imports(infile, outfile)
                        outfile.write("\n")

source_directory = r'C:\Users\vivid\Documents\C-Creative Projects\RoutineTurboA_android\app'
main_source_directory = os.path.join(source_directory, 'src', 'main')
output_file = 'combined.txt'

# Specify the files you want to include
file_names = [
    'MainActivity.kt',
    'AndroidManifest.xml',
    'MainScreen.kt',
    'DatabaseHelper.kt'
    'RoutineRepository.kt'
    'TaskViewModel.kt'
]

combine_kt_files(source_directory, main_source_directory, output_file, file_names)
print(f"All specified files have been combined into {output_file}")
