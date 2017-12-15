package print3D.model;

import java.util.ArrayList;
import java.util.List;

import triangulation.Triangle2D;
import triangulation.Triangle3D;
import triangulation.Vector2D;

public interface CommunicateToModel {
	
/*
 * Le Moyne Capstone Fall 2017
 * CommunicateToModel - interface for communication with the Model component
 * 
 * Alejandro Sanchez Gonzalez and Jessica Rankins
 * 
 * Last edited by Jessica on 11/25
 */
		
	//Pre: eq has been validated and we need to 
		//set the equation for the 3D surface
	//Post: if eq is valid, equation set to eq and true returned; else false returned
	public boolean setEquation(ArrayList<String> eq);
	
	//Pre: need to get the equation
	//Post: equation returned (or null if it is not set)
	public ArrayList<String> getEquation();
	
	//Pre: need to set the solid name
	//Post: solid name set and true returned if valid, false returned otherwise
	public boolean setSolidName(String name);
	
	//Pre: need to get solid name
	//Post: name of solid returned
	public String getSolidName();
	
	//Pre: need to set the set of points forming the perimeter of bound
	//Post: ptsOnBoundPerimeter set
	public void setOnBoundPerimeterPoints(ArrayList<Vector2D> ptsPerimeter);
		
	//Pre: need to set the set of triangles forming the sides of solid
	//Post: sideTriangles set		
	public void setSideTriangles(List<Triangle3D> solidSide3DTriangles);
		
	//Pre: need to set the set of triangles forming the sides of solid
	//Post: sideTriangles set		
	public List<Triangle3D> getSideTriangles();
		
	//Pre: need to get the set of points forming the perimeter of bound
	//Post: ptsOnBoundPerimeter returned (or null if it is not set)		
	public ArrayList<Vector2D> getOnBoundPerimeterPoints();
		
	//Pre: need to set the set of points forming the bound
	//Post: totalBoundPoints set
	public void setTotalBoundPoints(ArrayList<Vector2D> boundPointsCollection);
			
	//Pre: need to get the set of points forming the bound
	//Post: totalBoundPoints returned (or null if it is not set)
	public ArrayList<Vector2D> getTotalBoundPoints();
		
	//Pre: need to set resolution
	//Post: if coarseValue valid, resolution set to coarseValue and true returned
		//else, false returned
	public boolean setresolution(int coarseValue);
	
	//Pre: need to get the resolution
	//Post: resolution returned (0 if not set)
	public double getresolution();

	//Pre: need to get the boundChoice
	//Post: boundChoice returned (null if not set)
	public char getBoundChoice();

	//Pre: sets the bound choice for this equation
		//'t' for triangle, 'r' for rectangle, or 'c' for circle
	//Post: return true if boundChoice is acceptable, false otherwise
	public boolean setBoundChoice(char choice);
	
	//Pre: need to get the bound
	//circle: should be left, top, right, bottom vertices on the perimeter of the circle as seen from above
	//rectangle: should be the 4 vertices of the rectangle
	//triangle: should be the 3 vertices of the triangle
	//Post: bound returned or null if illegal bound choice
	public ArrayList<Vector2D> getBound();
	
	//Pre: given the vertices of the bound, need to set the correct bound 
	//circle: should be left, top, right, bottom vertices on the perimeter of the circle as seen from above
	//rectangle: should be the 4 vertices of the rectangle
	//triangle: should be the 3 vertices of the triangle
	//Post: the correct bound is set and true returned, or false returned
	public boolean setBound(Vector2D vertices[]);
	
	//Pre: need to set the triangles for the bound
	//Post: set the triangles for the bound and true returned; otherwise false returned
	public boolean setBoundTriangles(List<Triangle2D> triangles);
	
	//Pre: need to get the bound triangles
	//Post: bound triangles returned
	public List<Triangle2D> getBoundTriangles();
	
	//Pre: need to set the triangles approximating the 3D surface
	//Post: surface triangles set
	public void setThreeDTriangles(List<Triangle3D> triangles);
	
	//Pre: need to get the surface triangles
	//Post: surface triangles returned
	public List<Triangle3D> getThreeDTriangles();
	
    //Pre: need to know if equation contains a given function 
	//Post: return true if that function is found, otherwise false
	public boolean equationContainsFunction(String func);
	
	//Pre: need to write the STL file to this directory in ASCII
	//Post: ASCII STL file written in this directory and true returned, else false returned
	public boolean writeSTLFile();
}
