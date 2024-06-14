import os

def combine_kt_files(source_directory, main_source_directory, output_file):
    build_gradle_file = os.path.join(source_directory, 'build.gradle.kts')
    manifest_file = os.path.join(main_source_directory, 'AndroidManifest.xml')
    
    with open(output_file, 'w') as outfile:
        # Include AndroidManifest.xml if it exists
        if os.path.exists(manifest_file):
            with open(manifest_file, 'r') as infile:
                outfile.write(f"\n// Content from {manifest_file}\n")
                outfile.write(infile.read())
                outfile.write("\n")
        
        # Include build.gradle.kts if it exists
        if os.path.exists(build_gradle_file):
            with open(build_gradle_file, 'r') as infile:
                outfile.write(f"\n// Content from {build_gradle_file}\n")
                outfile.write(infile.read())
                outfile.write("\n")
                
        # Include all .kt files from main source directory
        for root, _, files in os.walk(main_source_directory):
            for file in files:
                if file.endswith('.kt'):
                    file_path = os.path.join(root, file)
                    with open(file_path, 'r') as infile:
                        outfile.write(f"\n// Content from {file_path}\n")
                        outfile.write(infile.read())
                        outfile.write("\n")


source_directory = r'C:\Users\Vivid\AndroidStudioProjects\RoutineTurbo_android\app'
main_source_directory = os.path.join(source_directory, 'src', 'main')
output_file = 'combined.kt'

combine_kt_files(source_directory, main_source_directory, output_file)
print(f"All relevant files have been combined into {output_file}")
