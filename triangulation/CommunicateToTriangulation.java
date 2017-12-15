package triangulation;

import java.util.List;

public abstract class CommunicateToTriangulation {

	/*
	 * Le Moyne Capstone Fall 2017
	 * CommunicateToTriangulation - a triangulation abstract class 
	 * Contains methods to interface with the triangulation package.
	 * Extended by all classes that need to communicate with the triangulation package.
	 * 
	 * Alejandro Sanchez Gonzalez and Jessica Rankins
	 * 
	 * Last edited by Jessica on 12/6
	 * 
	 */
	
	//Pre: need to triangulate the 2D surface given by the list of points
		//will show the DelaunayTriangulationView representation of this triangulation
		//if showDelaunayTriangulationView==1, else not show
	//Post: list of triangles tessellating this 2D surface are returned in a list
		//or error thrown. 
	public List<Triangle2D> triangulate2DSurface(List<Vector2D> points, int showDelaunayTriangulationView) throws Exception
	{
		if(points==null)
			return null;
		DelaunayTriangulator delaunayTriangulator;
        try {
        	delaunayTriangulator = new DelaunayTriangulator(points);
            delaunayTriangulator.triangulate();
            List<Triangle2D> triangles = delaunayTriangulator.getTriangles();
            if(showDelaunayTriangulationView==1)
            {
            	DelaunayTriangulationView plotter = new DelaunayTriangulationView();
				plotter.setDelaunayTriangulator(delaunayTriangulator);
				plotter.plot();
			}
            return triangles;	
        } 
	    catch (Exception e) {
			throw e;
		}  
	}

	//Pre: need to create a Vector2D object as <x,y>
	//Post: Vector2D object created and returned
	public Vector2D createVector2D(double x, double y)
	{
		return new Vector2D(x,y);
	}
	
	//Pre: need to get the x value from this Vector2D object
	//Post: x value of vector returned as double
	public double getVector2DX(Vector2D vector)
	{
		if(vector==null)
			return 0;
		return vector.x;
	}
	
	//Pre: need to get the y value from this Vector2D object
	//Post: y value of vector returned as double
	public double getVector2DY(Vector2D vector)
	{
		if(vector==null)
			return 0;
		return vector.y;
	}

	//Pre: need to set the x value for this Vector2D object to xvalue
	//Post: x value of vector set
	public void setVector2DX(Vector2D vector, double xvalue)
	{
		if(vector!=null)
			vector.x = xvalue;
	}
	
	//Pre: need to set the y value for this Vector2D object to yvalue
	//Post: y value of vector set
	public void setVector2DY(Vector2D vector, double yvalue)
	{
		if(vector!=null)
			vector.y = yvalue;
	}
	
	//Pre: need to know if a is equal to b
	//Post: true returned if equal, else false returned
	public boolean equals(Vector2D a, Vector2D b)
	{
		if(a==null || b==null)
			return false;
		return a.equals(b);
	}
	
	//Pre: need to create a Vector3D object as <x,y,z>
	//Post: Vector3D object created and returned
	public Vector3D createVector3D(double x, double y, double z)
	{
		return new Vector3D(x,y,z);
	}
	
	//Pre: need to get the x value from this Vector3D object
	//Post: x value of vector returned as double
	public double getVector3DX(Vector3D vector)
	{
		if(vector==null)
			return 0;
		return vector.x;
	}
	
	//Pre: need to get the y value from this Vector3D object
	//Post: y value of vector returned as double
	public double getVector3DY(Vector3D vector)
	{
		if(vector==null)
			return 0;
		return vector.y;
	}
	
	//Pre: need to get the z value from this Vector3D object
	//Post: z value of vector returned as double
	public double getVector3DZ(Vector3D vector)
	{
		if(vector==null)
			return 0;
		return vector.z;
	}
	
	//Pre: need to find the cross product of 2 Vector3D objects
	//Post: cross product a x b found and returned as Vector3D
	public Vector3D crossVector3D(Vector3D a, Vector3D b)
	{
		if(a==null || b==null)
			return null;
		return a.cross(b);
	}
	
	//Pre: need to know if a and b are equal
	//Post: return true if a equals b, otherwise false returned
	public boolean equals(Vector3D a, Vector3D b)
	{
		if(a==null || b==null)
			return false;
		return a.equals(b);
	}
	
	//Pre: need to get the first vertex (a) from this Triangle2D object
	//Post: first vertex of triangle returned as Vector2D
	public Vector2D getTriangle2DA(Triangle2D triangle)
	{
		if(triangle==null)
			return null;
		return triangle.a;
	}
	
	//Pre: need to get the second vertex (b) from this Triangle2D object
	//Post: second vertex of triangle returned as Vector2D
	public Vector2D getTriangle2DB(Triangle2D triangle)
	{
		if(triangle==null)
			return null;
		return triangle.b;
	}
	
	//Pre: need to get the third vertex (c) from this Triangle2D object
	//Post: third vertex of triangle returned as Vector2D
	public Vector2D getTriangle2DC(Triangle2D triangle)
	{
		if(triangle==null)
			return null;
		return triangle.c;
	}
	
	//Pre: need to create a Triangle3D object with vertices a,b,c either on the bound
		//(boundOrSurface=0) or on the surface (boundOrSurface=1)
	//Post: Triangle3D object created and returned
	public Triangle3D createTriangle3D(Vector3D a, Vector3D b, Vector3D c, int boundOrSurface)
	{
		return new Triangle3D(a,b,c,boundOrSurface);
	}
	
	//Pre: need to create a Triangle3D object with vertices a,b,c on the side of the 
		//3D solid where the center of the bound is centerOfBound
	//Post: Triangle3D object created and returned
	public Triangle3D createTriangle3D(Vector3D a, Vector3D b, Vector3D c, Vector2D centerOfBound)
	{
		return new Triangle3D(a,b,c,centerOfBound);
	}
	
	//Pre: need to get the first vertex (a) from this Triangle3D object
	//Post: first vertex of triangle returned as Vector3D
	public Vector3D getTriangle3DA(Triangle3D triangle)
	{
		if(triangle==null)
			return null;
		return triangle.a;
	}
	
	//Pre: need to get the second vertex (b) from this Triangle3D object
	//Post: second vertex of triangle returned as Vector3D
	public Vector3D getTriangle3DB(Triangle3D triangle)
	{
		if(triangle==null)
			return null;
		return triangle.b;
	}
	
	//Pre: need to get the third vertex (c) from this Triangle3D object
	//Post: third vertex of triangle returned as Vector3D
	public Vector3D getTriangle3DC(Triangle3D triangle)
	{
		if(triangle==null)
			return null;
		return triangle.c;
	}
	
	//Pre: need to get the normal vector from this Triangle3D object
	//Post: normal vector of triangle returned as Vector3D
	public Vector3D getTriangle3DNormal(Triangle3D triangle)
	{
		if(triangle==null)
			return null;
		return triangle.normal;
	}
	
	//Pre: need to know if a equals b
	//Post: true returned if a equals b, else false returned
	public boolean equals(Triangle3D a, Triangle3D b)
	{
		if(a==null || b==null)
			return false;
		return a.equals(b);
	}
}
