package triangulation;

/*
 * Le Moyne Capstone Fall 2017
 * Triangle3D - object to store 3D triangles approximating the 3D surface
 * 
 * Considered aggregation with Triangle2D or a common base class or interface.
 * HOWEVER, this would probably lead to more work than necessary since 
 * 		different types of methods are needed for each, and Triangle3D uses 
 * 		Vector3Ds while Triangle2Ds use Vector2Ds.
 * 
 * Alejandro Sanchez Gonzalez and Jessica Rankins
 * 
 * Last edited by Jessica on 11/15
 * 
 */

public class Triangle3D implements Comparable<Triangle3D>{

	public Vector3D a;
	public Vector3D b;
	public Vector3D c;
	
	public Vector3D normal;
	
	//Pre: need to create a Triangle3D for bound or surface
	//Post: created a Triangle3D with these points a,b,c and created normal vector based on bound or surface
	//boundOrSurface == 0 if triangle is supposed to be on the bound, 1 if on surface
	public Triangle3D(Vector3D a, Vector3D b, Vector3D c, int boundOrSurface) {
		if(a!=null&&b!=null&&c!=null&& !a.equals(b) && !b.equals(c) && !a.equals(c)
				&& (boundOrSurface==0||boundOrSurface==1))
		{
			//must make sure vertices of triangle in CCW order so starting at a, b is CCW from a, and c is CCW from b
			Vector3D ab = new Vector3D(b.x-a.x,b.y-a.y,1);
			Vector3D ac = new Vector3D(c.x-a.x,c.y-a.y,1);
			normal = ab.cross(ac);
			
			switch(boundOrSurface)
			{
			case 0: //triangle on the bound
				if(normal.z>0) //vertices in wrong order (want point down), switch order from abc to acb
				{
					this.a = a;
					this.b = c;
					this.c = b;
					normal.negate(); //RULE 4 (point other direction aka downward)
					normal.makeUnitVector(); //RULE 4
				}
				else if(normal.z<0) //vertices in correct order, keep in abc order
				{
					this.a = a;
					this.b = b;
					this.c = c;			
					normal.makeUnitVector(); //RULE 4
				}
				break;
			case 1: //triangle on the surface
				if(normal.z<0) //vertices in wrong order (want point up), switch order from abc to acb
				{
					this.a = a;
					this.b = c;
					this.c = b;
					normal.negate(); //RULE 4 (point other direction aka upward)
					normal.makeUnitVector(); //RULE 4
				}
				else if(normal.z>0) //vertices in correct order, keep in abc order
				{
					this.a = a;
					this.b = b;
					this.c = c;			
					normal.makeUnitVector(); //RULE 4
				}
				break;				
			}

		}
	}
	
	//Pre: need to create a Triangle3D for a side triangle
	//Post: triangle3D created with vertices a,b,c and normal pointing away from center
	public Triangle3D(Vector3D a, Vector3D b, Vector3D c, Vector2D centerOfBound)
	{
		if(a!=null&&b!=null&&c!=null&& !a.equals(b) && !b.equals(c) && !a.equals(c)
				&& centerOfBound!=null)
		{
			//must make sure vertices of triangle in CCW order so starting at a, b is CCW from a, and c is CCW from b
			//as seen from the outside of the 3D object
			Vector3D ab = new Vector3D(b.x-a.x,b.y-a.y,b.z-a.z);
			Vector3D ac = new Vector3D(c.x-a.x,c.y-a.y,c.z-a.z);
			normal = ab.cross(ac);
			normal.makeUnitVector(); //RULE 4
			
			Vector2D normal2d = new Vector2D(normal.x,normal.y);
			Vector2D centerToTriangle = new Vector2D(a.x-centerOfBound.x,a.y-centerOfBound.y);
		
			//angle between normal vector and vector from center of bound to the given triangle
			double theta = Math.toDegrees(Math.acos(normal2d.dot(centerToTriangle)/(normal2d.length()*centerToTriangle.length())));
			
			if(theta<90)
			{
				this.a = a;
				this.b = b;
				this.c = c;
			}
			else //normal pointing wrong way (should be away from center), also vertices in wrong order
			{
				this.a = a;
				this.b = c;
				this.c = b;
				normal.negate(); //RULE 4 (point other direction aka upward)
			}
		}
	}
	
	
	//Pre: need a string representation of this triangle3D object
	//Post: string representation of this object returned
	@Override
    public String toString() {
        return "Triangle3D[" + a + ", " + b + ", " + c + "] with normal ("+ normal+")";
    }

	//Pre: compare this object to other
	//Post: return 0 if equal z value, -1 if this.a.z<other.a.z, 1 if this.a.z>other.a.z
	@Override
	public int compareTo(Triangle3D other) throws NullPointerException
	{
		if(other==null || a==null ||other.a==null)
			throw new NullPointerException();
		
		if(this.a.z>other.a.z)
			return 1;
		else if(this.a.z<other.a.z)
			return -1;
		else
			return 0;
	}
}
