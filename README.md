# DOEOptimization
DOE Optimization for Simulation Model Alignment

Instructions for setting up, and running the Model Alignment web application:

Requirements:

-A web application server with JSP enabled (we used an Apache Tomcat server v 7.0.47)

-Matlab (v R2014b)

-Additional libraries (provide statistical analysis functions and additional functionality):

 -Elki v 0.7.1 - Environment for Developing KDD-Applications Supported by Index-Structures
 
 -JDistLib v 0.4.5 - Java Statistical Distribution Library
 
 -Matlab control v 4.1.0 - Allows Java-based software to invoke and execute Matlab commands
 
 -Commons Math v 3.6.1
 
 -JSwarm-PSO v 2.08 (http://jswarm-pso.sourceforge.net/) - Java-based particle swarm optimizer.

(1) Start the server.

(2) Install the WAR file on the server.

(3) Upload the JAR files for the required libraries.

(4) Access the application's main page: [server location]/DOEOptimization/index.jsp
