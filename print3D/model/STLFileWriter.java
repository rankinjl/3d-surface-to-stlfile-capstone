package print3D.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import triangulation.CommunicateToTriangulation;
import triangulation.Triangle3D;
import triangulation.Vector3D;

/*
 * Le Moyne Capstone Fall 2017
 * STLFileWriter:
 * Writes the given data to the STL file,
 * the interface to the 3D printer.
 * 
 * CommunicateToTriangulation used in writeVector() to access Vector3D x,y,z
 * 		and in writeFile to access Triangle3D a,b,c,normal
 * 
 * Alejandro Sanchez Gonzalez and Jessica Rankins
 * 
 * Last edited by Jessica on 11/7
 */

public class STLFileWriter extends CommunicateToTriangulation{

	private String extension = ".stl";
	
	//RULE 5 - list triangles in STL file in ascending z order
	//Pre: need to write the STL file
	//Post: STL file written for solid "name"
	public boolean writeFile(String name, List<Triangle3D> threeDObjectTriangles, List<Triangle3D> sideTriangles)
	{
		if(threeDObjectTriangles==null || sideTriangles==null || name==null || threeDObjectTriangles.size()<1)
			return false;
		List<Triangle3D> allTriangles = new ArrayList<Triangle3D>();
		allTriangles.addAll(threeDObjectTriangles);
		allTriangles.addAll(sideTriangles);
		Object trianglesAndNormals[] = allTriangles.toArray();
		Arrays.sort(trianglesAndNormals); 
		
		try
		{
			String tab = "  ";
	        BufferedWriter output = constructFile(name);
	        if(output==null)
	        	return false;
			Triangle3D currentTriangle = null;

	        output.write("solid "+name+"\n");
	        for(int i = 0; i<trianglesAndNormals.length; i++)
	        {
	        	currentTriangle = (Triangle3D) trianglesAndNormals[i];
	        	
	        	output.write(tab+"facet normal ");
	        	writeVector(output,"",getTriangle3DNormal(currentTriangle));
	        	output.write(tab+tab+"outer loop\n");
	        	writeVector(output,tab+tab+tab,getTriangle3DA(currentTriangle));
	        	writeVector(output,tab+tab+tab,getTriangle3DB(currentTriangle));
	        	writeVector(output,tab+tab+tab,getTriangle3DC(currentTriangle));
	        	output.write(tab+tab+"endloop");
	        	output.write(tab+"endfacet\n");
	        }
	        output.write("endsolid "+name);
	        output.flush();
	        output.close();
	        return true;
		}catch(IOException e)
		{
			return false;
		}
	}
	
	//Pre: need to create a file with this name
	//Post: file with this name created (with extra information if name is already taken)
			//and bufferedWriter for writing to this file is returned
	private BufferedWriter constructFile(String name) throws IOException
	{
		File file = new File(name+extension);
        
        //if file already exists:
        int count = 1;
        while(file.exists())
        {
        	file = new File(name+"("+count+")"+extension);
        	count++;
        }
        return new BufferedWriter(new FileWriter(file));

	}
	
	//Pre: using output, write currentVector's x,y,z coordinates preceded by whitespace
	//Post: vector information put in file
	private void writeVector(BufferedWriter output, String whitespace, Vector3D currentVector) throws IOException
	{
		output.write(whitespace+"vertex "
    			+(float)getVector3DX(currentVector)+" "
    			+(float)getVector3DY(currentVector)+" "
    			+(float)getVector3DZ(currentVector)+"\n");
	}
}
