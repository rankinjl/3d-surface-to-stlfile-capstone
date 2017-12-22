# 3d-surface-to-stlfile-capstone

This repository contains my Computer Science Senior Capstone Project that I completed with my partner Alejandro Sanchez-Gonzalez in the Fall Semester of 2017. It is meant to take in a 3D surface from the user and output a 3D-printer-ready STL file. The user enters information such as 3D equation in the form z=f(x,y), a resolution of the object, a name for the object, and a bound on the XY plane in the form of a circle, triangle, or rectangle. The STL file is outputted to the Java project directory.

The Project Plan that we followed and the Requirements Documentation for this project are included in this repository. A Design Document is also included that outlines the processing the code follows, class diagrams, behavior diagrams, logical data models, and physical data models. A table of Test Cases is in the Test Cases Document and outlines some of what the program can and cannot do as of the end of the project in December 2017.

The Java code is located in the print3D and triangluation folders. The print3D folder consists of three different packages for the Model, View, and Controller in the MVC architecture. The View is a simple Text User Interface asking the user for information in sequence and returning the information to the Controller. The Model holds the data for the Controller to use, and writes the STL file at the end of the processing. The Controller runs the entire program starting by asking the user for the desired information from the View, storing this information in the Model, using this information and the triangulation package to triangulate the entire 3D solid, and then having the Model create the STL file.

The triangulation folder contains a third party library used in triangulating the sides and the bound of the solid. All of this code is from this third party EXCEPT the Triangle3D, Vector3D, package-info, and CommunicateToTriangulation files (and where specified). This third party code and documentation can be found at the following github account and uses the MIT License outlined below.

https://github.com/jdiemke/delaunay-triangulator

		The MIT License (MIT)
		
		Copyright (c) 2015 Johannes Diemke
		
		Permission is hereby granted, free of charge, to any person obtaining a copy
		of this software and associated documentation files (the "Software"), to deal
		in the Software without restriction, including without limitation the rights
		to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
		copies of the Software, and to permit persons to whom the Software is
		furnished to do so, subject to the following conditions:
		
		The above copyright notice and this permission notice shall be included in all
		copies or substantial portions of the Software.
		
		THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
		IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
		FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
		AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
		LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
		OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
		SOFTWARE.
    
I was responsible for writing the code for the view package, most of the model package including the STL file writer, the Triangle3D and Vector3D classes, the CommunicateToView and CommunicateToModel interfaces, the CommunicateToController API for the third party code access, the validation of most of the user input excluding the equation validation, making sure all surfaces are in the positive octant, finding the 3D triangles for the STL file on the bound and on the surface (but not the sides), and finding all normal vectors for the 3D triangles.

Note: the view of the bound triangulation is not included in the final system, but is included in the testing to point out possible errors. This debugging tool can be turned on by changing DEBUG_BOUND_TESSELLATION in SurfaceToSTL to 1 and including joglibrary ( com.jogamp.opengl found at http://jogamp.org/deployment/jogamp-next/javadoc/jogl/javadoc/) to the src folder with the rest of the code.
Note: to make sure the tessellation is more accurate, use larger numbers and stretched surfaces.
Used http://www.wolframalpha.com/widgets/view.jsp?id=f708f36bc40c46f8db505d43ca92053b to test what 3D surfaces should look like and https://www.viewstl.com/ to see what the STL file actually produces.
