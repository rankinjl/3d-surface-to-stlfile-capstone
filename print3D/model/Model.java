package print3D.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import triangulation.Triangle2D;
import triangulation.Triangle3D;
import triangulation.Vector2D;

/*
 * Le Moyne Capstone Fall 2017
 * Model - the main Model object.
 * Contains the non-persistent data structures and holds
 * the data for the application to be written into STL file.
 * 
 * Alejandro Sanchez Gonzalez and Jessica Rankins
 * 
 * Last edited by Jessica on 11/20
 */


public class Model implements CommunicateToModel{
	
	private ArrayList<String> equation;
	private char boundChoice; //'t','c', or 'r' only
	
	//only one of the following will be used at a time
	private ArrayList<Vector2D> bound;
	
	//set of points inside and along perimeter of the bound
	private ArrayList<Vector2D> ptsOnBoundPerimeter;
	private ArrayList<Vector2D> totalBoundPoints;
	//set of triangles made by triangulation on bound
	private List<Triangle2D> boundTriangles;
	private List<Triangle3D> threeDObjectTriangles;
	private List<Triangle3D> sideTriangles;
	
	private int resolution;
	private String solidName;
	
	//Pre: need to create a model object
	//Post: model object created
	public Model()
	{
		resolution = 0;
	}
	
	//Pre: eq has been validated and we need to 
		//set the equation for the 3D surface
	//Post: if eq is valid, equation set to eq and true returned; else false returned
	public boolean setEquation(ArrayList<String> eq)
	{
		if(eq!=null)
		{
			equation = eq;
			return true;
		}
		return false;
	}
	
	//Pre: need to get the equation
	//Post: equation returned (or null if it is not set)
	public ArrayList<String> getEquation()
	{
		return equation;
	}
	
	//Pre: need to set the solid name
	//Post: solid name set and true returned if valid, false returned otherwise
	public boolean setSolidName(String name)
	{
		if(name!=null && !name.trim().isEmpty() && name.getBytes().length<=80)
		{
			if(!Pattern.matches("[ \\w\\-]*",name)) //upper,lowercase letters,digits,-,_, space
				return false;
			solidName = name;
			return true;
		}
		return false;
	}
	
	//Pre: need to get solid name
	//Post: name of solid returned
	public String getSolidName()
	{
		return solidName;
	}
	
	//Pre: need to set the set of points forming the perimeter of bound
	//Post: ptsOnBoundPerimeter set
	public void setOnBoundPerimeterPoints(ArrayList<Vector2D> ptsPerimeter)
	{
		ptsOnBoundPerimeter=ptsPerimeter;
	}
		
	//Pre: need to set the set of triangles forming the sides of solid
	//Post: sideTriangles set		
	public void setSideTriangles(List<Triangle3D> solidSide3DTriangles)
	{
		sideTriangles= solidSide3DTriangles;
	}
		
	//Pre: need to set the set of triangles forming the sides of solid
	//Post: sideTriangles set		
	public List<Triangle3D> getSideTriangles()
	{
		return sideTriangles;
	}
		
	//Pre: need to get the set of points forming the perimeter of bound
	//Post: ptsOnBoundPerimeter returned (or null if it is not set)		
	public ArrayList<Vector2D> getOnBoundPerimeterPoints()
	{
		return ptsOnBoundPerimeter;
	}
		
	//Pre: need to set the set of points forming the bound
	//Post: totalBoundPoints set
	public void setTotalBoundPoints(ArrayList<Vector2D> boundPointsCollection)
	{
		totalBoundPoints=boundPointsCollection;
	}
			
	//Pre: need to get the set of points forming the bound
	//Post: totalBoundPoints returned (or null if it is not set)
	public ArrayList<Vector2D> getTotalBoundPoints()
	{
		return totalBoundPoints;
	}
		
	//Pre: need to set resolution
	//Post: if coarseValue valid, resolution set to coarseValue and true returned
		//else, false returned
	public boolean setresolution(int coarseValue)
	{
		if(coarseValue>=1 && coarseValue<=10)
		{
			resolution = coarseValue;
			return true;
		}
		return false;
	}
	
	//Pre: need to get the resolution
	//Post: resolution returned (0 if not set)
	public double getresolution()
	{
		return resolution;
	}
	//Pre: need to get the boundChoice
	//Post: boundChoice returned (null if not set)
	public char getBoundChoice()
	{
		return boundChoice;
	}
	//Pre: sets the bound choice for this equation
		//'t' for triangle, 'r' for rectangle, or 'c' for circle
	//Post: return true if boundChoice is acceptable, false otherwise
	public boolean setBoundChoice(char choice)
	{
		if(choice=='t'||choice=='r'||choice=='c')
		{
			boundChoice = choice;
			bound = null;
			return true;
		}
		else
			return false;
	}
	
	//Pre: need to get the bound
	//circle: should be left, top, right, bottom vertices on the perimeter of the circle as seen from above
	//rectangle: should be the 4 vertices of the rectangle
	//triangle: should be the 3 vertices of the triangle
	//Post: bound returned or null if illegal bound choice
	public ArrayList<Vector2D> getBound()
	{
		if(boundChoice=='r' || boundChoice=='t' || boundChoice=='c')
			return bound;
		return null;
	}
	
	//Pre: given the vertices of the bound, need to set the correct bound 
	//circle: should be left, top, right, bottom vertices on the perimeter of the circle as seen from above
	//rectangle: should be the 4 vertices of the rectangle
	//triangle: should be the 3 vertices of the triangle
	//Post: the correct bound is set and true returned, or false returned
	public boolean setBound(Vector2D vertices[]) 
	{
		if(vertices==null || !(boundChoice=='r' || boundChoice=='t' || boundChoice=='c'))
			return false;
		
		bound = new ArrayList<Vector2D>();
		for(int i=0;i<vertices.length;i++)
		{
			bound.add(vertices[i]);
		}
		return true;
	}
	
	//Pre: need to set the triangles for the bound
	//Post: set the triangles for the bound and true returned; otherwise false returned
	public boolean setBoundTriangles(List<Triangle2D> triangles)
	{
		if(triangles!=null && !triangles.isEmpty())
		{
			boundTriangles = triangles;
			return true;
		}
		return false;
	}
	
	//Pre: need to get the bound triangles
	//Post: bound triangles returned
	public List<Triangle2D> getBoundTriangles()
	{
		return boundTriangles;
	}
	
	//Pre: need to set the triangles approximating the 3D surface
	//Post: surface triangles set
	public void setThreeDTriangles(List<Triangle3D> triangles)
	{
		threeDObjectTriangles = triangles;
	}
	
	//Pre: need to get the surface triangles
	//Post: surface triangles returned
	public List<Triangle3D> getThreeDTriangles()
	{
		return threeDObjectTriangles;
	}
    //Pre: need to know if equation contains a given function 
	//Post: return true if that function is found, otherwise false
	public boolean equationContainsFunction(String func)
	{
	    return equation.indexOf(func)>=0;	
	}
	//Pre: need to write the STL file to this directory in ASCII
	//Post: ASCII STL file written in this directory and true returned, else false returned
	public boolean writeSTLFile()
	{
		STLFileWriter writer = new STLFileWriter();
		return writer.writeFile(solidName,threeDObjectTriangles,sideTriangles);
	}
}