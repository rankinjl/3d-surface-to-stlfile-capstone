package triangulation;

/*
 * Le Moyne Capstone Fall 2017
 * Vector3D - object to store a 3D point/vector (normal vector)
 * 
 * Considered aggregation with Vector2D or a common base class or interface
 * HOWEVER, this would probably lead to more work than necessary since 
 * 		different types of methods are needed for each
 * 
 * Alejandro Sanchez Gonzalez and Jessica Rankins
 * 
 * Last edited by Jessica on 12/6
 * 
 */

public class Vector3D {
	
	public double x;
	public double y;
	public double z;

	public Vector3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	//Pre: need the vector resulting from the cross product of this and other
	//Post: crossproduct calculated and returned
    public Vector3D cross(Vector3D other) {
    	if(other!=null)
    		return new Vector3D(y*other.z-z*other.y, 
        		z*other.x-x*other.z, x*other.y-y*other.x);
    	else return null;
    }

    //Pre: need to know if this equals other
    //Post: true returned if same x,y,z; else false returned
    public boolean equals(Vector3D other)
    {
    	if(other==null)
    		return false;
    	return (x==other.x && y==other.y && z==other.z);
    }
    
    //Pre: need to negate this vector componenetwise
    //Post: vector negated
    public void negate()
    {
    	x = -x;
    	y = -y;
    	z = -z;
    }
	
    //Pre: need to make this vector a unit vector
  	//Post: vector changed to a unit vector
  	public void makeUnitVector()
  	{
  		double length = Math.sqrt(x*x + y*y + z*z);
  		if(length!=1 && length!=0)
  		{
  			x/=length;
  			y/=length;
  			z/=length;
  		}
  	}
    
    //Pre: need a string representation of this vector3D
    //Post: string representation of this object returned
	@Override
    public String toString() {
        return "Vector3D[" + x + ", " + y + ", " + z +"]";
    }
}
