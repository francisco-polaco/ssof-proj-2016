### Working in the command line

0. Install gradle v3.1

1. Generate grammars, compile and run:
  gradle run -Dexec.args="slice_file_path_goes_here"
  
2. Step-by-step
  * Generate the grammars: gradle generateGrammarSource
  * Compile sources: gradle compileJava
  * Run: gradle run -Dexec.args="slice_file_path_goes_here"
  
### Working with Eclipse

0. Install Gradle plugin: follow tutorial at http://www.vogella.com/tutorials/EclipseGradle/article.html
  * step 2.1 - Installation via the Marketplace
  * step 4 - Import an existing Gradle project - IMPORTANT! -> choose Specific Gradle version and enter 3.1
  * steps 5.2 and 5.1 - by this order!!!
  
1. Project receives one command line argument. To pass it to the main function via propmt:
  * Go to menu item Run->Run configurations, tab Arguments, input box Program arguments:, and type ${string_prompt}

### Packing everything into a JAR file

0. Clean first:
  gradle clean
  
1. Run task:
  gradle fatJar
  
* a JAR file named Analyzer.jar is created under build/libs

### Running the project from the JAR file

0. First copy the patterns folder (from the project root) to the directory where you have the JAR file

1. Run with: 
  java -jar Analyzer.jar **slice_file_path**
  
 
