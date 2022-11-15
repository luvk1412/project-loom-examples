# project-loom-examples

### IDEA Run Instructions

- Go to File > Project Structure > Project Settings > Project and choose appropriate JDK 19 and choose language level as 19 preview.
- Go to Preferences | Build, Execution, Deployment | Compiler | Java Compiler and add "--enable-preview --source 19" in Additional command line parameters.
- Go to Run/Debug Configuration > Modify Options and enable "Add VM options" and enter "--enable-preview" in VM options.
- Add to compiler parameters per module as well.
- Now running the main function should run things.