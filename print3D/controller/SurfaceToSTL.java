package print3D.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import print3D.model.CommunicateToModel;
import print3D.view.CommunicateToView;
import triangulation.CommunicateToTriangulation;
import triangulation.Triangle2D;
import triangulation.Triangle3D;
import triangulation.Vector2D;
import triangulation.Vector3D;

/*
 * Le Moyne Capstone Fall 2017
 * SurfaceToSTL - the main controller object.
 * Contains most of the business logic for the application.
 * 
 * Alejandro Sanchez Gonzalez and Jessica Rankins
 * 
 * Last edited by Jessica on 12/6
 * 
 */
public class SurfaceToSTL extends CommunicateToTriangulation {

	//1 for showing the bound, else for not showing
	private final int DEBUG_BOUND_TESSELLATION = 0;
	
	private CommunicateToModel data;
	private UserInputController input;

	// Pre: need to create a main controller object and the main view and
	// model objects
	// Post: view and model created
	public SurfaceToSTL(CommunicateToModel model, CommunicateToView view) {
		data = model;
		input = new UserInputController(data, view);
	}

	// Pre: need to start the surface to stl file process
	// Post: surface to stl file process completed
	public void go() {
		if (input != null) {
			boolean tryagain = true;
			do {
				try {
					//functional requirement 1
					input.go(); 
					addPointsToBound();
					// RULE 2 in triangulation package
					//functional requirement 2
					triangulate();
					makeTriangle3Ds();
					//functional requirement 3
					if (!data.writeSTLFile()) {
						input.inputInvalid("Error writing STL file!");
						tryagain = true;
					} else {
						tryagain = false;
						input.done();
					}
				} catch (Exception e) {
					e.printStackTrace();
					input.inputInvalid("An error has occurred!");
					tryagain = false;
				}
			} while (tryagain);
		}

	}

	// Pre: need to make 2D triangulation of the bound RULE 2
	// Post: a set of triangles that compound the bound is created and plotted
	private void triangulate() {
		try {
			List<Triangle2D> triangles = triangulate2DSurface(data.getTotalBoundPoints(), DEBUG_BOUND_TESSELLATION);
			Triangle2D current = null;
			Iterator<Triangle2D> iter = triangles.iterator();
			while (iter.hasNext()) {
				current = iter.next();
				if (current == null || equals(getTriangle2DA(current), getTriangle2DB(current)) || equals(
						getTriangle2DB(current), getTriangle2DC(current)) || equals(getTriangle2DA(current),
								getTriangle2DC(current)))
					iter.remove();
			}

			data.setBoundTriangles(triangles);
		} catch (Exception e) {
			input.inputInvalid("An error has occurred!");
		}
	}
    
	// Pre: need to make 2D triangulation of a solid side's portion given a list of Vector2Ds
	// Post: a set of triangles that compound this portion is created 
	private List<Triangle2D> triangulatePortionOfASide(List<Vector2D> sidePoints)
	{
		List<Triangle2D> solidSide2DTriangles = null;
        try {
        	solidSide2DTriangles = triangulate2DSurface(sidePoints,0);
        	//0 for no view of the tessellation
        } 
	    catch (Exception e) {
			input.inputInvalid("An error has occurred!");
		}  
        return solidSide2DTriangles;  
	}

	// Pre: need to calculate extra points to bound
	// Post: calculated extra points quantity
	private int getExtraPointsQty() {
		
		
		if (data.getBoundChoice() == 'r' || data.getBoundChoice() == 't')
		{   
			return  4 * (int) data.getresolution();
		}		
		else {// make a factor depending on circle radius	
		     ArrayList<Vector2D> bound = data.getBound();		
			double v1 = getVector2DX(bound.get(0));// left vertex
			double v2 = getVector2DX(bound.get(2));// right vertex
			double radius = (double) Math.abs(v2 - v1) / (double) 2;
			double extra = (radius < 100)?(double) (4 * data.getresolution()):(double) (4 * data.getresolution()) * (double) (radius / (double) 100);
			return (int) extra;
		}
	}

	// Pre: need to add points inside and along sides of the bound
	// Post: created a set of points for the bound to be ready for triangulation
	private void addPointsToBound() {
		int qtyForPerimeter = getExtraPointsQty();
		int qtyForInsideShape = getExtraPointsQty();
		ArrayList<Vector2D> bound = data.getBound();
		ArrayList<Vector2D> boundPtsCollection = new ArrayList<Vector2D>();
		ArrayList<Vector2D> ptsOnBoundPerimeter = new ArrayList<Vector2D>();
		char boundChoice = data.getBoundChoice();
		double x1, x2, y1, y2;
		if (boundChoice == 't' || boundChoice == 'r') {
		    ArrayList<Vector2D> aux1;
		    boundPtsCollection.addAll(bound);
			if (boundChoice == 't') // divide qty by number of sides for having
									// uniform distribution of points for each
									// side
			{
			qtyForPerimeter /= 2;
			qtyForInsideShape/=2;
				for (int i = 0; i < bound.size(); i++) {
				x1 = getVector2DX(bound.get(i));
				y1 = getVector2DY(bound.get(i));
				if (i == bound.size() - 1) {
					x2 = getVector2DX(bound.get(0));
					y2 = getVector2DY(bound.get(0));
				} else {
					x2 = getVector2DX(bound.get(i + 1));
					y2 = getVector2DY(bound.get(i + 1));
				}
				ptsOnBoundPerimeter.add(createVector2D(x1, y1));
				aux1 = addPointsAlongSegment(x1, x2, y1, y2, qtyForPerimeter, boundPtsCollection);
				boundPtsCollection.addAll(aux1);
				ptsOnBoundPerimeter.addAll(aux1);
				ptsOnBoundPerimeter.add(createVector2D(x2, y2));
			   }

			   boundPtsCollection.addAll(addPointsInsideTriangle(qtyForInsideShape, boundPtsCollection));
			
		    }
			else
			{
			ArrayList<Vector2D> aux2;
				  qtyForPerimeter /= 2;
				  qtyForInsideShape/=2;
			for (int i = 0; i <=1; i++) {
			x1 = getVector2DX(bound.get(0+i));
			y1 = getVector2DY(bound.get(0+i));
			x2 = getVector2DX(bound.get(1+i));				
			y2 = getVector2DY(bound.get(1+i));
			ptsOnBoundPerimeter.add(createVector2D(x1, y1));
			aux1 = addPointsAlongSegment(x1, x2, y1, y2, qtyForPerimeter, boundPtsCollection);
			boundPtsCollection.addAll(aux1);
			ptsOnBoundPerimeter.addAll(aux1);
			ptsOnBoundPerimeter.add(createVector2D(x2, y2));

			x1 = getVector2DX(bound.get(2+i));
			y1 = getVector2DY(bound.get(2+i));
			x2 = getVector2DX(bound.get(3*(1-i)));				
			y2 = getVector2DY(bound.get(3*(1-i)));
			ptsOnBoundPerimeter.add(createVector2D(x1, y1));
			aux2 = addPointsAlongSegment(x1, x2, y1, y2, qtyForPerimeter, boundPtsCollection);
			boundPtsCollection.addAll(aux2);
			ptsOnBoundPerimeter.addAll(aux2);
			ptsOnBoundPerimeter.add(createVector2D(x2, y2));			
			
			boundPtsCollection.addAll(addPointsInsideRectangle(aux1,aux2,qtyForInsideShape, boundPtsCollection));
			}
			}
		} else // circle
		{
		   addPointsToCircularBound(qtyForPerimeter,qtyForInsideShape,boundPtsCollection,ptsOnBoundPerimeter); 
		}

		data.setTotalBoundPoints(boundPtsCollection);
		data.setOnBoundPerimeterPoints(ptsOnBoundPerimeter);
	}
	
	// Pre: need to add points inside and around a circular bound
	// Post: created a set of points for the bound to be ready for triangulation
	private void addPointsToCircularBound(int qtyForPerimeter,int qtyForInsideShape,ArrayList<Vector2D> boundPtsCollection,ArrayList<Vector2D> ptsOnBoundPerimeter) {		
		ArrayList<Vector2D> bound = data.getBound();				
		double x1, x2, xcenter, ycenter, nx, ny, radius,xlr, ylr;
	        //store pts going to the back side
			//have ptsOnBoundPerimeter ordered from left to right
		    ArrayList<Vector2D> ptsOnAHalfPerimeter = new ArrayList<Vector2D>();
			boolean topVertAdded=false;
	        // vertices: [0]-left,[1]-top,[2]-right,[3]-bottom
			xcenter = getVector2DX(bound.get(1));// the x value of top vertice
													// is the sameas x of the center
			ycenter = getVector2DY(bound.get(0));// the y value of left vertice
													// is the same
			// as y of the center
			x1 = getVector2DX(bound.get(0));// left vertex
			x2 = getVector2DX(bound.get(2));// right vertex
				
			radius = (double) Math.abs(x2 - x1) / (double) 2;
			boundPtsCollection.addAll(bound);
			//ptsOnBoundPerimeter.addAll(bound);
			
			//qtyForInsideShape *= 2;
			if(data.getresolution()>1)
			qtyForPerimeter = qtyForPerimeter / 2;
			for (int j = 1; j <= qtyForPerimeter; j++)// divide diameter into 'qty/2' equal parts, so qty/2 points will
														// be added to each semi-circle, 2*(qty-1) in total,for more accurate curve
			{
				// using analytic math: coordinates for point dividing segment
				// given by 2 points, into equal parts
				// x= Rx2+x1(1-R) where R is a reason (1 to qty-1)/qty
				// y= ycenter +- Sqrt(radius^2-(x-xcenter)^2) to satisfy a
				// circle equation
				nx = (double) ((double) j / (double) (qtyForPerimeter + 1)) * x2 + x1 * (double) (1 - (double) ((double) j / (double) (qtyForPerimeter + 1)));
				// add points near left vertex for better curve
				if (j == 1) {
				    ptsOnBoundPerimeter.add(bound.get(0));
					ptsOnAHalfPerimeter.add(bound.get(0));
					for (int k = 1; k <= (int) (data.getresolution() / 3); k++) {
						xlr = (double) ((double) k / (double) ((int) (data.getresolution() / 3) + 1)) * nx + x1
								* (double) (1 - (double) ((double) k / (double) ((int) (data.getresolution() / 3)+ 1)));
						ylr = ycenter + Math.sqrt(Math.pow(radius, 2) - Math.pow(xlr - xcenter, 2));
						boundPtsCollection.add(createVector2D(xlr, ylr));
						ptsOnAHalfPerimeter.add(0,createVector2D(xlr, ylr));
						
						ylr = ycenter - Math.sqrt(Math.pow(radius, 2) - Math.pow(xlr - xcenter, 2));
						boundPtsCollection.add(createVector2D(xlr, ylr));
						ptsOnBoundPerimeter.add(createVector2D(xlr, ylr));
						if(data.equationContainsFunction("sqrt"))
						boundPtsCollection.addAll(addPointsInsideCircleBySegment(boundPtsCollection.get(boundPtsCollection.size() - 2), 
			                       boundPtsCollection.get(boundPtsCollection.size() - 1), qtyForInsideShape/ 4));
						}
				}else 
				if(j==2)
				{if(data.getresolution()>3)
					{xlr=(nx+ptsOnBoundPerimeter.get(ptsOnBoundPerimeter.size()-1).x)/(double)2;
				ylr = ycenter + Math.sqrt(Math.pow(radius, 2)- Math.pow(xlr - xcenter, 2));
				boundPtsCollection.add(createVector2D(xlr, ylr));
				ptsOnAHalfPerimeter.add(0,createVector2D(xlr, ylr));
				
				ylr = ycenter - Math.sqrt(Math.pow(radius, 2) - Math.pow(xlr - xcenter, 2));
				boundPtsCollection.add(createVector2D(xlr, ylr));
				ptsOnBoundPerimeter.add(createVector2D(xlr, ylr));
				if(data.equationContainsFunction("sqrt"))
				boundPtsCollection.addAll(addPointsInsideCircleBySegment(boundPtsCollection.get(boundPtsCollection.size() - 2), 
			                       boundPtsCollection.get(boundPtsCollection.size() - 1), qtyForInsideShape/ 4));
				}
				}
				if (nx > getVector2DX(bound.get(1)) && !topVertAdded) {
					ptsOnAHalfPerimeter.add(0,bound.get(1));
					ptsOnBoundPerimeter.add(bound.get(3));
					topVertAdded = true;
				}

				ny = ycenter + Math.sqrt(Math.pow(radius, 2) - Math.pow(nx - xcenter, 2));
				boundPtsCollection.add(createVector2D(nx, ny));
				ptsOnAHalfPerimeter.add(0,createVector2D(nx, ny));

				ny = ycenter - Math.sqrt(Math.pow(radius, 2) - Math.pow(nx - xcenter, 2));
				boundPtsCollection.add(createVector2D(nx, ny));
				ptsOnBoundPerimeter.add(createVector2D(nx, ny));
				// add qtyForInsideShape/qtyForPerimeter points inside for each segment			
				boundPtsCollection.addAll(addPointsInsideCircleBySegment(boundPtsCollection.get(boundPtsCollection.size() - 2), 
				boundPtsCollection.get(boundPtsCollection.size() - 1), qtyForInsideShape/ 4));
                // add points near right vertex for better curve
				
				if (j == qtyForPerimeter) {
					if(data.getresolution()>3){
					xlr=(nx+ptsOnBoundPerimeter.get(ptsOnBoundPerimeter.size()-2).x)/(double)2;
					ylr = ycenter + Math.sqrt(Math.pow(radius, 2)- Math.pow(xlr - xcenter, 2));
					boundPtsCollection.add(createVector2D(xlr, ylr));
					ptsOnAHalfPerimeter.add(1,createVector2D(xlr, ylr));
					
					ylr = ycenter - Math.sqrt(Math.pow(radius, 2) - Math.pow(xlr - xcenter, 2));
					boundPtsCollection.add(createVector2D(xlr, ylr));
					ptsOnBoundPerimeter.add(ptsOnBoundPerimeter.size()-1,createVector2D(xlr, ylr));
					if(data.equationContainsFunction("sqrt"))
					boundPtsCollection.addAll(addPointsInsideCircleBySegment(boundPtsCollection.get(boundPtsCollection.size() - 2), 
				                       boundPtsCollection.get(boundPtsCollection.size() - 1), qtyForInsideShape/ 4));
					}
					for (int k = 1; k <= (int) (data.getresolution() / 3); k++) {
						xlr = (double) ((double) k / (double) ((int) (data.getresolution() / 3) + 1)) * x2 + nx
								* (double) (1 - (double) ((double) k / (double) ((int) (data.getresolution() / 3)+ 1)));
						ylr = ycenter + Math.sqrt(Math.pow(radius, 2) - Math.pow(xlr - xcenter, 2));
						boundPtsCollection.add(createVector2D(xlr, ylr));
						ptsOnAHalfPerimeter.add(0,createVector2D(xlr, ylr));

						ylr = ycenter - Math.sqrt(Math.pow(radius, 2) - Math.pow(xlr - xcenter, 2));
						boundPtsCollection.add(createVector2D(xlr, ylr));
						ptsOnBoundPerimeter.add(createVector2D(xlr, ylr));
                        if(data.equationContainsFunction("sqrt"))
						boundPtsCollection.addAll(addPointsInsideCircleBySegment(boundPtsCollection.get(boundPtsCollection.size() - 2), 
			                       boundPtsCollection.get(boundPtsCollection.size() - 1), qtyForInsideShape/ 4));
					}
					ptsOnBoundPerimeter.add(bound.get(2));
				}				
			}
			ptsOnBoundPerimeter.addAll(ptsOnAHalfPerimeter);		
	}
	// Pre: need to add points to a segment inside the circle
	// Post: return a set of points added to a segment inside the circle
	private ArrayList<Vector2D> addPointsInsideCircleBySegment(Vector2D top, Vector2D bottom, int qty) {
		ArrayList<Vector2D> ptsInside = new ArrayList<Vector2D>();
		double y1, y2, nx, ny, distBetPt;

		nx = getVector2DX(top);// the x of all points
		y1 = getVector2DY(top);
		y2 = getVector2DY(bottom);
		ny = y2;
		distBetPt = (double) Math.abs(y1 - y2) / (double) (qty + 1);
		// calculate distance to be added to each point
		// divided by qty+1 to ensure there are exactly qty of points
		for (int j = 0; j < qty; j++) {
			if (ny >= y1)
				break;
			else {
				ny += distBetPt;
				ptsInside.add(createVector2D(nx, ny));
			}
		}
		return ptsInside;
	}

	// Pre: need to add points along the side of a polygon
	// Post: return a set of points added to a side of a polygon
	private ArrayList<Vector2D> addPointsAlongSegment(double x1, double x2, double y1, double y2, double qty,
			ArrayList<Vector2D> compareCurrentPts) {
		ArrayList<Vector2D> ptsAlong = new ArrayList<Vector2D>();
		double nx, ny;

		for (int j = 1; j <= qty; j++)// divide segment into 'qty' equal parts,
										// so just qty-1 points will be added
		{
			// using analytic math: coordinates for point dividing segment given
			// by 2 points, into equal parts
			// (x= Rx2+x1(1-R);y= Ry2+y1(1-R)) where R is a reason (1 to
			// qty-1)/qty
			nx = ((double) j / (double) (qty + 1)) * x2 + x1 * (1 - ((double) j
					/ (double) (qty + 1)));
			ny = ((double) j / (double) (qty + 1)) * y2 + y1 * (1 - ((double) j
					/ (double) (qty + 1)));
			if (compareCurrentPts.indexOf(createVector2D(nx, ny)) < 0)
				ptsAlong.add(createVector2D(nx, ny));
		}
		return ptsAlong;
	}

	// Pre: need to add points inside a rectangle, between two top and bottom segments
	// Post: return a set of points added inside a polygon
	private ArrayList<Vector2D> addPointsInsideRectangle(ArrayList<Vector2D> aux1,ArrayList<Vector2D> aux2,int qty, ArrayList<Vector2D> compareCurrentPts) {
		ArrayList<Vector2D> ptsInside = new ArrayList<Vector2D>();
		
		char boundChoice = data.getBoundChoice();
		double x1, x2, y1, y2;
		if (boundChoice == 'r') {	
			for (int i = 0; i < aux1.size(); i++) {
			x1 = getVector2DX(aux1.get(i));
			y1 = getVector2DY(aux1.get(i));
			x2 = getVector2DX(aux2.get(i));
			y2 = getVector2DY(aux2.get(i));
			ptsInside.addAll(addPointsAlongSegment(x1, x2, y1, y2, qty, compareCurrentPts));
			}			
		} 
		return ptsInside;
	}
	// Pre: need to add points inside a polygon
	// Post: return a set of points added inside a polygon
	private ArrayList<Vector2D> addPointsInsideTriangle(int qty, ArrayList<Vector2D> compareCurrentPts) {
		ArrayList<Vector2D> ptsInside = new ArrayList<Vector2D>();
		ArrayList<Vector2D> bound = data.getBound();
		char boundChoice = data.getBoundChoice();
		double x1, x2, y1, y2;
		if (boundChoice == 't') {
			qty /= 3;// divide qty by 3 medians in triangle
			// first median
			x1 = getVector2DX(bound.get(0));
			y1 = getVector2DY(bound.get(0));
			x2 = (getVector2DX(bound.get(1)) + getVector2DX(bound.get(2))) / (double) 2;
			y2 = (getVector2DY(bound.get(1)) + getVector2DY(bound.get(2))) / (double) 2;
			ptsInside.addAll(addPointsAlongSegment(x1, x2, y1, y2, qty, compareCurrentPts));

			// sec median
			x1 = getVector2DX(bound.get(1));
			y1 = getVector2DY(bound.get(1));
			x2 = (getVector2DX(bound.get(0)) + getVector2DX(bound.get(2))) / (double) 2;
			y2 = (getVector2DY(bound.get(0)) + getVector2DY(bound.get(2))) / (double) 2;
			ptsInside.addAll(addPointsAlongSegment(x1, x2, y1, y2, qty, compareCurrentPts));

			// third median
			x1 = getVector2DX(bound.get(2));
			y1 = getVector2DY(bound.get(2));
			x2 = (getVector2DX(bound.get(0)) + getVector2DX(bound.get(1))) / (double) 2;
			y2 = (getVector2DY(bound.get(0)) + getVector2DY(bound.get(1))) / (double) 2;
			ptsInside.addAll(addPointsAlongSegment(x1, x2, y1, y2, qty, compareCurrentPts));
		}
		return ptsInside;
	}
    // Pre: need to find the first index of a Vector2D within a list
	// Post: return that index
	private int indexOfVector(List<Vector2D> list, Vector2D v) {
		for (int i = 0; i < list.size(); i++) {
			if (equals(list.get(i), v))
				return i;
		}
		return -1;
	}
    // Pre: need to find the first index of a Vector3D within a list
	// Post: return that index
	private int indexOfVector(List<Vector3D> list, Vector3D v) {
		for (int i = 0; i < list.size(); i++) {
			if (equals(list.get(i), v))
				return i;
		}
		return -1;
	}
    // Pre: need to find the last index of a Vector2D within a list
	// Post: return that index
	private int lastIndexOfVector(List<Vector2D> list, Vector2D v) {
		int index = -1;
		for (int i = 0; i < list.size(); i++) {
			if (equals(list.get(i), v))
				index = i;
		}
		return index;
	}
    // Pre: need to find the first index of a Triangle3D within a list
	// Post: return that index
	private int indexOfTriangle(List<Triangle3D> list, Triangle3D t) {
		for (int i = 0; i < list.size(); i++) {
			if (equals(list.get(i), t))
				return i;
		}
		return -1;
	}

	// Pre: need to triangulate sides of cylinder
	// Post:set of triangles forming sides of cylinder added to 3D triangles set
	private void triangulateCylinderSides(List<Triangle3D> solidSide3DTriangles, double qty) {
		// all 2D points on perimeter of the bound
		List<Vector2D> ptsOnBoundPerimeter = data.getOnBoundPerimeterPoints();

		// all 3D points on the sides of the solid
		List<Vector3D> allSolidSidePoints = new ArrayList<Vector3D>();
		// all 2D points on the front side after projecting onto XZ plane
		List<Vector2D> sidePts_1Side = new ArrayList<Vector2D>();
		List<Vector2D> aux1= new ArrayList<Vector2D>();
		List<Triangle2D> _1Side2DTriangles=new ArrayList<Triangle2D>();
		ArrayList<Vector2D> bound = data.getBound();
		// indexes of each third of front side
		int rightVertexIndex = indexOfVector(ptsOnBoundPerimeter, bound.get(2));		

		// store pts where there is an increasing or decreasing of function
		Vector2D _1vectorWithZEquals1 = null;
		
		double z,zn = 1,zant=1;		
		// for each point on the perimeter of the bound, make qty more points at
		// the same x,y value but at different z values
		// all 3D points from this point added to solidSidePoints, and XZ
		// projection of point added to sidePts_FrontBackSide
		for (int i = 0; i < ptsOnBoundPerimeter.size(); i++) {
			zn = input.evaluateEquationAt(ptsOnBoundPerimeter.get(i));
			if (zn > 1) {
				if(zn>zant || i == rightVertexIndex+1)
				{					
				   if (sidePts_1Side.size() >= 3) {
				         //triangulate right third of front side of cylinder side, and after that, get symmetric triangles on back part   
		    	         _1Side2DTriangles.addAll(triangulatePortionOfASide(sidePts_1Side));
		    	         sidePts_1Side=new ArrayList<Vector2D>();
		    	         sidePts_1Side.addAll(aux1);
			            }
				    else
				    {
				    	//take the last point with z=1 if exists (where side begins to grow)
						// to use it for triangulation	     		  
					   if(_1vectorWithZEquals1!=null){
						 if (indexOfVector(sidePts_1Side,_1vectorWithZEquals1) < 0)
						     sidePts_1Side.add(_1vectorWithZEquals1);
						}
				    }
				    _1vectorWithZEquals1=null;   					
				}  

				aux1 = new ArrayList<Vector2D>();

				for (int j = 0; j <= qty + 1; j++) 
					// divide segment into 'qty+1' equal parts, so just qty points will be added
				{ // using analytic math: coordinates for point dividing
					// segment given by 2 points, into equal parts
					// (x= Rx2+x1(1-R);y= Ry2+y1(1-R)) where R is a reason (1 to
					// qty-1)/qty
					z = ((double) j / (double) (qty + 1)) * zn + (1 - ((double) j
							/ (double) (qty + 1)));
					if (indexOfVector(allSolidSidePoints, createVector3D(getVector2DX(ptsOnBoundPerimeter.get(i)),
							getVector2DY(ptsOnBoundPerimeter.get(i)), z)) < 0)
						allSolidSidePoints.add(createVector3D(getVector2DX(ptsOnBoundPerimeter.get(i)), getVector2DY(
								ptsOnBoundPerimeter.get(i)), z));
                   
					// get xy side projected onto xy axis for 2D Triangulation
					if (indexOfVector(aux1,createVector2D(getVector2DX(ptsOnBoundPerimeter.get(i)),	z)) < 0)
						  aux1.add(createVector2D(getVector2DX(ptsOnBoundPerimeter.get(i)), z));
					if (j == qty + 1)
						  sidePts_1Side.addAll(aux1);					  
					
				}
				 if(zn<zant)
				    if (sidePts_1Side.size() >= 3) {
					         //triangulate right third of front side of cylinder side, and after that, get symmetric triangles on back part   
			    	   _1Side2DTriangles.addAll(triangulatePortionOfASide(sidePts_1Side));
			    	    sidePts_1Side=new ArrayList<Vector2D>();
			    	    sidePts_1Side.addAll(aux1);
				        }
							
				zant=zn;				
			} 
			else 
			{// if z is aprox. 1 top and bottom bound on that point
					// coincide
				zant=1;
				if (indexOfVector(allSolidSidePoints, createVector3D(getVector2DX(ptsOnBoundPerimeter.get(i)),
						getVector2DY(ptsOnBoundPerimeter.get(i)), 1)) < 0)
					allSolidSidePoints.add(createVector3D(getVector2DX(ptsOnBoundPerimeter.get(i)), getVector2DY(
							ptsOnBoundPerimeter.get(i)), 1));
           
				 _1vectorWithZEquals1=createVector2D(getVector2DX(ptsOnBoundPerimeter.get(i)), 1);  
				 aux1=new ArrayList<Vector2D>();
				 aux1.add(_1vectorWithZEquals1);
				 if(sidePts_1Side.size()>0)
				 { 
				   if (getVector2DY(sidePts_1Side.get(sidePts_1Side.size() - 1)) > 1)//z>1 in the point before this, then function has decreased to bottom at this point
				  {				   
				     if (indexOfVector(sidePts_1Side,_1vectorWithZEquals1) < 0)
				        sidePts_1Side.add(_1vectorWithZEquals1);
					 _1vectorWithZEquals1=null;					 
				  } 
				 }	
			}
		}

		if (sidePts_1Side.size() == 0 && _1Side2DTriangles.size()==0)// top and bottom bound coincide completely (all z = 1)
			return;
		
		if (sidePts_1Side.size() >= 3) {
			//triangulate right third of front side of cylinder side, and after that, get symmetric triangles on back part   
	    	_1Side2DTriangles.addAll(triangulatePortionOfASide(sidePts_1Side));	    	
		}	   
		if(_1Side2DTriangles.size()>0)
		    makeTrianglesForCylinderSides(solidSide3DTriangles,_1Side2DTriangles,allSolidSidePoints);
	}
    // Pre: take the Triangle2D list from triangulated 2D side, and a list of side's 3D points to make 3D triangles
	// Post: all 3D Triangles are added to the list solidSide3DTriangles
	private void makeTrianglesForCylinderSides( List<Triangle3D> solidSide3DTriangles,List<Triangle2D> solidSide2DTriangles,List<Vector3D> solidSidePoints)
	{
	        double x1, x2, x3, ya1, yb1, yc1, ya2, yb2, yc2;
	       /*
			 * Each x,z point given to the triangulation algorithm will have 2
			 * possible y values since the bound is a circle, and each triangle
			 * found during triangulation will have 3 points (a,b,c). 
			 * So: 
			 * ya1 is the y value for this current triangle from triangulation's
			 * a=(x,z) point that is smaller (from the center of the circle to
			 * the yaxis)
			 * ya2 is the y value for this current triangle from
			 * triangulation's a=(x,z) point that is larger (from the center of
			 * the circle to the point farther away from the yaxis) 
			 * yb1 is the y value for this current triangle from triangulation's b=(x,z)
			 * point that is smaller (from the center of the circle to the
			 * yaxis) 
			 * etc.
			 */
	        // the list of 2D triangles given from triangulation				
			//should be left, top, right, bottom vertices on the perimeter of the circle as seen from above			
			ArrayList<Vector2D> bound = data.getBound();
	        Vector2D centerOfCircle = createVector2D(getVector2DX(bound.get(1)), getVector2DY(bound.get(0)));     
	        Triangle2D currentTriangle;
	        Triangle3D symmetricTriangle=null;
			 // For each triangle from triangulation
				// for each of the 3 points in that 2D triangle
				// find the 2 associated 3D points with the same x,z values
				// get the 2 different y values from the 3D point
				// create 2 3D vectors using the x,z values from the triangulation
				// triangle and the associated y values
            for (int i = 0; i < solidSide2DTriangles.size(); i++)
            {
                ya1 = yb1 = yc1 = ya2 = yb2 = yc2 = -1;
                x1=x2=x3=-1;
                currentTriangle = solidSide2DTriangles.get(i);
               
                x1 = getVector2DX(getTriangle2DA(currentTriangle));
				x2 = getVector2DX(getTriangle2DB(currentTriangle));
				x3 = getVector2DX(getTriangle2DC(currentTriangle));
               
                //getting the y values
               //solidSidePoints contains all points in solid where x are ordered from left to right around the cylinder
                for (int j = 0; j < solidSidePoints.size(); j++)
                {    
                    if ((getVector3DX(solidSidePoints.get(j)) == getVector2DX(getTriangle2DA(currentTriangle)))
								&& (getVector3DZ(solidSidePoints.get(j)) == getVector2DY(getTriangle2DA(currentTriangle))))
                    {   
                        if (getVector3DY(solidSidePoints.get(j)) < getVector2DY(centerOfCircle))//from ycenter to front
                        {    
						     ya1 = getVector3DY(solidSidePoints.get(j)); 
                        }
						else if (getVector3DY(solidSidePoints.get(j)) > getVector2DY(centerOfCircle))//from ycenter to front
                        {    
						     ya2 = getVector3DY(solidSidePoints.get(j)); 
                        }
						else
						{
							ya1 = ya2 = getVector3DY(solidSidePoints.get(j)); 
						}
                    }
					 if ((getVector3DX(solidSidePoints.get(j)) == getVector2DX(getTriangle2DB(currentTriangle)))
								&& (getVector3DZ(solidSidePoints.get(j)) == getVector2DY(getTriangle2DB(currentTriangle))))
                    { 
						 if (getVector3DY(solidSidePoints.get(j)) < getVector2DY(centerOfCircle))//from ycenter to front
		                {    
						   yb1 = getVector3DY(solidSidePoints.get(j)); 
                        }
						else if (getVector3DY(solidSidePoints.get(j)) > getVector2DY(centerOfCircle))//from ycenter to front
		                {    
						  yb2 = getVector3DY(solidSidePoints.get(j));                             
                        }
						else
						{
							 yb1 = yb2 = getVector3DY(solidSidePoints.get(j)); 
						}
                    }
                   if ((getVector3DX(solidSidePoints.get(j)) == getVector2DX(getTriangle2DC(currentTriangle)))
								&& (getVector3DZ(solidSidePoints.get(j)) == getVector2DY(getTriangle2DC(currentTriangle))))
                    {  					   
                	   if (getVector3DY(solidSidePoints.get(j)) < getVector2DY(centerOfCircle))//from ycenter to front
                        {    
						   yc1 = getVector3DY(solidSidePoints.get(j)); 
                        } 
						else if (getVector3DY(solidSidePoints.get(j)) > getVector2DY(centerOfCircle))//from ycenter to front
                        {   
   						    yc2 = getVector3DY(solidSidePoints.get(j)); 
                        }
						else
						{
							yc1 = yc2 = getVector3DY(solidSidePoints.get(j)); 
						}
                    }
                       
                    if (ya1 > -1 && yb1 > -1 && yc1 > -1 && ya2 > -1 && yb2 > -1 && yc2 > -1)
                        break;
                }
                //get previously projected xz axis onto xy axis back, by assigning y value to z
                //and add original y value
                if (ya1 > -1 && yb1 > -1 && yc1 > -1){
                  symmetricTriangle = createTriangle3D(createVector3D(x1, ya1, getVector2DY(getTriangle2DA(currentTriangle))), 
				                                     createVector3D(x2, yb1, getVector2DY(getTriangle2DB(currentTriangle))),
						                             createVector3D(x3, yc1, getVector2DY(getTriangle2DC(currentTriangle))), 
													 centerOfCircle);
                  if (indexOfTriangle(solidSide3DTriangles,symmetricTriangle) < 0 && getTriangle3DA(symmetricTriangle) != null
               		   && getTriangle3DB(symmetricTriangle) != null && getTriangle3DC(symmetricTriangle) != null)
                   solidSide3DTriangles.add(symmetricTriangle);
				 }
				 
				 if (ya2 > -1 && yb2 > -1 && yc2 > -1){
                   symmetricTriangle = createTriangle3D(createVector3D(x1, ya2, getVector2DY(getTriangle2DA(currentTriangle))), 
				                                     createVector3D(x2, yb2, getVector2DY(getTriangle2DB(currentTriangle))),
						                             createVector3D(x3, yc2, getVector2DY(getTriangle2DC(currentTriangle))), 
													 centerOfCircle);;                
                   if (indexOfTriangle(solidSide3DTriangles,symmetricTriangle) < 0 && getTriangle3DA(symmetricTriangle) != null
                		   && getTriangle3DB(symmetricTriangle) != null && getTriangle3DC(symmetricTriangle) != null)
                    solidSide3DTriangles.add(symmetricTriangle);
                 }
            } 			  
	}

    // Pre: need to find the y value at the back side of a circular bounded solid, which corresponds to a x value
	// Post:return that y value
	private double getBackY(List<Vector3D> solidSidePoints, double x) {
		ArrayList<Vector2D> bound = data.getBound();
		Vector2D centerOfCircle = createVector2D(getVector2DX(bound.get(1)), getVector2DY(bound.get(0)));

		for (int j = solidSidePoints.size() - 1; j >= 0; j--) {
			if (getVector3DX(solidSidePoints.get(j)) == x) {
				if (getVector3DY(solidSidePoints.get(j)) >= getVector2DY(centerOfCircle))
					// from ycenter to back
					return getVector3DY(solidSidePoints.get(j));
			}
		}
		return -1;
	}
	
    // Pre: need to make 3D triangulation of 4-face solid sides
	// Post: set of 3D triangles forming that side is added to solidSide3DTriangles
	private void triangulateCubeSides(List<Triangle3D> solidSide3DTriangles, double qty) {
		List<Vector2D> ptsOnBoundPerimeter = data.getOnBoundPerimeterPoints();
		
		// ArrayList<Vector3D> solidSidePoints = new ArrayList<Vector3D>();
		List<Vector2D> sidePts_FrontSide = new ArrayList<Vector2D>();
		List<Vector2D> sidePts_LeftSide = new ArrayList<Vector2D>();
		List<Vector2D> sidePts_BackSide = new ArrayList<Vector2D>();
		List<Vector2D> sidePts_RightSide = new ArrayList<Vector2D>();
		List<Triangle2D> frontSide_2DTriangles = new ArrayList<Triangle2D>();
        List<Triangle2D> backSide_2DTriangles = new ArrayList<Triangle2D>();
        List<Triangle2D> leftSide_2DTriangles = new ArrayList<Triangle2D>();
		List<Triangle2D> rightSide_2DTriangles = new ArrayList<Triangle2D>();
		ArrayList<Vector2D> bound = data.getBound(); //should be the 4 vertices of the rectangle
		char trianVarSelected1, trianVarSelected2, trianVarSelected3, trianVarSelected4;
		
		int _1Index1S, _2Index1S, _1Index2S, _2Index2S, _1Index3S, _2Index3S, _1Index4S, _2Index4S;
		// first and second indexes where each bound side is
		// located within ptsOnBoundPerimeter
		_1Index1S = indexOfVector(ptsOnBoundPerimeter, bound.get(0)); 
		_2Index1S = indexOfVector(ptsOnBoundPerimeter, bound.get(1));
		_1Index2S = indexOfVector(ptsOnBoundPerimeter, bound.get(2));
		_2Index2S = indexOfVector(ptsOnBoundPerimeter, bound.get(3));
		_1Index3S = lastIndexOfVector(ptsOnBoundPerimeter, bound.get(1));
		_2Index3S = lastIndexOfVector(ptsOnBoundPerimeter, bound.get(2));
		_1Index4S = lastIndexOfVector(ptsOnBoundPerimeter, bound.get(3));
		_2Index4S = lastIndexOfVector(ptsOnBoundPerimeter, bound.get(0));		

		trianVarSelected1 = getVariableForProjection(_1Index1S,_2Index1S);
		getFlatSideProjected(frontSide_2DTriangles, sidePts_FrontSide, qty,_1Index1S,_2Index1S,trianVarSelected1); 
		trianVarSelected2 =  getVariableForProjection(_1Index2S,_2Index2S);
		getFlatSideProjected(backSide_2DTriangles, sidePts_BackSide, qty,_1Index2S,_2Index2S,trianVarSelected2); 
		trianVarSelected3 = getVariableForProjection(_1Index3S,_2Index3S);
		getFlatSideProjected(leftSide_2DTriangles, sidePts_LeftSide, qty,_1Index3S,_2Index3S,trianVarSelected3);
		trianVarSelected4=  getVariableForProjection(_1Index4S,_2Index4S);
		getFlatSideProjected(rightSide_2DTriangles, sidePts_RightSide, qty,_1Index4S,_2Index4S,trianVarSelected4);
				
		//TRIANGULATE  
				//front side
				triangulateEachFlatSide(solidSide3DTriangles,frontSide_2DTriangles, sidePts_FrontSide, _1Index1S,_2Index1S, trianVarSelected1);
				//back side
				triangulateEachFlatSide(solidSide3DTriangles, backSide_2DTriangles, sidePts_BackSide, _1Index2S,_2Index2S, trianVarSelected2);
		        //left side
				triangulateEachFlatSide(solidSide3DTriangles, leftSide_2DTriangles, sidePts_LeftSide, _1Index3S,_2Index3S, trianVarSelected3);
				//right side
				triangulateEachFlatSide(solidSide3DTriangles, rightSide_2DTriangles, sidePts_RightSide, _1Index4S,_2Index4S, trianVarSelected4);			
	}

	// Pre: need to make 3D triangulation of 3-face solid sides
	// Post: set of 3D triangles forming that side is added to solidSide3DTriangles
	private void triangulateTriFaceCubeSolidSides(List<Triangle3D> solidSide3DTriangles, double qty) {
		// all 2D points on perimeter of the bound
		List<Vector2D> ptsOnBoundPerimeter = data.getOnBoundPerimeterPoints();
		
		// all 3D points on the sides of the solid
		// List<Vector3D> solidSidePoints = new ArrayList<Vector3D>();
		// all 2D points on the sides after projecting onto XZ plane
		List<Vector2D> sidePts_FrontBackSide = new ArrayList<Vector2D>();
		List<Vector2D> sidePts_LeftRightSide = new ArrayList<Vector2D>();
		List<Vector2D> sidePts_TriangDiagSide = new ArrayList<Vector2D>();
		List<Triangle2D> frontBackSide_2DTriangles = new ArrayList<Triangle2D>();
        List<Triangle2D> leftRightSide_2DTriangles = new ArrayList<Triangle2D>();
        List<Triangle2D> triangDiagSide_2DTriangles = new ArrayList<Triangle2D>();
		ArrayList<Vector2D> bound = data.getBound(); //should be the 3 vertices of the triangle
		
		char trianVarSelected1, trianVarSelected2, trianVarSelected3;		
		int _1Index1S, _2Index1S, _1Index2S, _2Index2S, _1Index3S, _2Index3S;
		// first and second indexes where each triangle side is located within
		// ptsOnBoundPerimeter
		_1Index1S = indexOfVector(ptsOnBoundPerimeter, bound.get(0)); 
		_2Index1S = indexOfVector(ptsOnBoundPerimeter, bound.get(1));
		_1Index2S = lastIndexOfVector(ptsOnBoundPerimeter, bound.get(1));
		_2Index2S = indexOfVector(ptsOnBoundPerimeter, bound.get(2));
		_1Index3S = lastIndexOfVector(ptsOnBoundPerimeter, bound.get(2));
		_2Index3S = lastIndexOfVector(ptsOnBoundPerimeter, bound.get(0));
				
		trianVarSelected1 = getVariableForProjection(_1Index1S,_2Index1S);
		getFlatSideProjected(frontBackSide_2DTriangles, sidePts_FrontBackSide, qty,_1Index1S,_2Index1S,trianVarSelected1); 
		trianVarSelected2 =  getVariableForProjection(_1Index2S,_2Index2S);
		getFlatSideProjected(leftRightSide_2DTriangles, sidePts_LeftRightSide, qty,_1Index2S,_2Index2S,trianVarSelected2); 
		trianVarSelected3 = getVariableForProjection(_1Index3S,_2Index3S);
		getFlatSideProjected(triangDiagSide_2DTriangles, sidePts_TriangDiagSide, qty,_1Index3S,_2Index3S,trianVarSelected3);
       
		//TRIANGULATE    
        //front/back side
	    triangulateEachFlatSide(solidSide3DTriangles, frontBackSide_2DTriangles, sidePts_FrontBackSide, _1Index1S,_2Index1S, trianVarSelected1);          
		//left/right side
		triangulateEachFlatSide(solidSide3DTriangles, leftRightSide_2DTriangles, sidePts_LeftRightSide, _1Index2S,_2Index2S, trianVarSelected2);
		 //diagonal side
		triangulateEachFlatSide(solidSide3DTriangles, triangDiagSide_2DTriangles,sidePts_TriangDiagSide, _1Index3S,_2Index3S, trianVarSelected3);        
	}
    // Pre: need to 3D triangulate a single side of polygonal bounded solid
	// Post:set of 3D triangles forming that side is added to solidSide3DTriangles
	private void triangulateEachFlatSide(List<Triangle3D> solidSide3DTriangles, List<Triangle2D> solidSide2DTriangles, List<Vector2D> sidePoints, int ind1,int ind2, char trianVarSelected) 
	{
		double ya, yb, yc;
		ya = yb = yc = -1;
		List<Vector2D> ptsOnBoundPerimeter = data.getOnBoundPerimeterPoints();		
		ArrayList<Vector2D> bound = data.getBound(); // should be the 4 vertices of the rectangle
		Vector2D centerpoint = getCenterOfMass(bound.toArray());
		Triangle2D currentTriangle;
		Triangle3D toAddTriangle = null;

		if(sidePoints.size()>=3)
		   solidSide2DTriangles.addAll(triangulatePortionOfASide(sidePoints));

		for (int i = 0; i < solidSide2DTriangles.size(); i++) {
				ya = yb = yc = -1;
				// adding 3D triangles to front and back solid side
				currentTriangle = solidSide2DTriangles.get(i);
				for (int j = ind1; j <= ind2; j++) {
					// depending on the variable values taken for triangulation
					// given by trianVarSelected
					// get previously projected xz axis onto xy axis back, by
					// assigning y value to z
					// or get previously projected yz axis onto xy axis back, by
					// assigning y value to z and x value to y
					// and add original x or y value
					if (trianVarSelected == 'x') {
						if (getVector2DX(ptsOnBoundPerimeter.get(j)) == getVector2DX(getTriangle2DA(currentTriangle)))
							ya = getVector2DY(ptsOnBoundPerimeter.get(j));

						if (getVector2DX(ptsOnBoundPerimeter.get(j)) == getVector2DX(getTriangle2DB(currentTriangle)))
							yb = getVector2DY(ptsOnBoundPerimeter.get(j));

						if (getVector2DX(ptsOnBoundPerimeter.get(j)) == getVector2DX(getTriangle2DC(currentTriangle)))
							yc = getVector2DY(ptsOnBoundPerimeter.get(j));

						if (ya > -1 && yb > -1 && yc > -1) {
							toAddTriangle = createTriangle3D(
							                createVector3D(getVector2DX(getTriangle2DA(currentTriangle)), ya, getVector2DY(getTriangle2DA(currentTriangle))),
											createVector3D(getVector2DX(getTriangle2DB(currentTriangle)), yb, getVector2DY(getTriangle2DB(currentTriangle))), 
											createVector3D(getVector2DX(getTriangle2DC(currentTriangle)), yc, getVector2DY(getTriangle2DC(currentTriangle))), 
											centerpoint);
							break;
						}
					} else if (trianVarSelected == 'y') {
						if (getVector2DY(ptsOnBoundPerimeter.get(j)) == getVector2DX(getTriangle2DA(currentTriangle)))
							 ya = getVector2DX(ptsOnBoundPerimeter.get(j));

						if (getVector2DY(ptsOnBoundPerimeter.get(j)) == getVector2DX(getTriangle2DB(currentTriangle)))
							yb = getVector2DX(ptsOnBoundPerimeter.get(j));

						if (getVector2DY(ptsOnBoundPerimeter.get(j)) == getVector2DX(getTriangle2DC(currentTriangle)))
							yc = getVector2DX(ptsOnBoundPerimeter.get(j));

						if (ya > -1 && yb > -1 && yc > -1) {
							toAddTriangle = createTriangle3D(
							                createVector3D(ya, getVector2DX(getTriangle2DA(currentTriangle)), getVector2DY(getTriangle2DA(currentTriangle))), 
											createVector3D(yb, getVector2DX(getTriangle2DB(currentTriangle)), getVector2DY(getTriangle2DB(currentTriangle))), 
											createVector3D(yc, getVector2DX(getTriangle2DC(currentTriangle)), getVector2DY(getTriangle2DC(currentTriangle))), 
											centerpoint);
							break;
						}
					}
				}
				if (ya > -1 && yb > -1 && yc > -1 && toAddTriangle != null)
					if (indexOfTriangle(solidSide3DTriangles, toAddTriangle) < 0 && getTriangle3DA(toAddTriangle) != null
					&& getTriangle3DB(toAddTriangle) != null && getTriangle3DC(toAddTriangle) != null)
						solidSide3DTriangles.add(toAddTriangle);
			}
	}
	// Pre: need to determine which axis has more points of the bound on it
	// Post: return that axis
	private char getVariableForProjection(int ind1, int ind2) {
		List<Vector2D> ptsOnBoundPerimeter = data.getOnBoundPerimeterPoints();
		// check distance between vertices
		// if the bound side has more distance with respect to x axis than to y
		// axis, take x variable
		// otherwise take y
		if (Math.abs(getVector2DX(ptsOnBoundPerimeter.get(ind1)) - getVector2DX(ptsOnBoundPerimeter.get(ind2))) >= Math
				.abs(getVector2DY(ptsOnBoundPerimeter.get(ind1)) - getVector2DY(ptsOnBoundPerimeter.get(ind2))))
			return 'x';
		else
			return 'y';
	}
	// Pre: need to get a 2D side of solid by projecting the 3D side onto a specific axis
	// Post: set of Vector2Ds triangles added to sidePts
	private void getFlatSideProjected(List<Triangle2D> side2DTriangles, List<Vector2D> sidePts, double qty, int ind1, int ind2, char varSelected) {
		List<Vector2D> ptsOnBoundPerimeter = data.getOnBoundPerimeterPoints();
		List<Vector2D> sidePtsToAdd = new ArrayList<Vector2D>();
		List<Vector2D> aux1 = new ArrayList<Vector2D>();		
        Vector2D vectorWithZEquals1=null;		
        double z, zn = 1, zant = 1;
		
		for (int i = ind1; i <= ind2; i++) {
			zn = input.evaluateEquationAt(ptsOnBoundPerimeter.get(i));
			if (zn > 1) {
				
				
                if (zn > zant)
                 {                       
                        if (sidePtsToAdd.size() >= 3)
                        {
                            //triangulate right third of front side of cylinder side, and after that, get symmetric triangles on back part   
                            side2DTriangles.addAll(triangulatePortionOfASide(sidePtsToAdd));
                            sidePtsToAdd = new ArrayList<Vector2D>();
                            sidePtsToAdd.addAll(aux1);                            
                        }  
                        else
                        {
            				// take the last point with z=1 if exists (where side begins to grow)
            				// to use it for triangulation
            				if (vectorWithZEquals1 != null) {
            					if (indexOfVector(sidePtsToAdd, vectorWithZEquals1) < 0)
            						sidePtsToAdd.add(vectorWithZEquals1);            					
            				}
                        }
                        vectorWithZEquals1 = null;                     
                 }
               
                aux1 = new ArrayList<Vector2D>(); 
			 	for (int j = 0; j <= qty + 1; j++)
					// divide segment into 'qty' equal parts, so just
					// qty-1 points will be added
				{
					// using analytic math: coordinates for point dividing
					// segment given by 2 points, into equal parts
					// (x= Rx2+x1(1-R);y= Ry2+y1(1-R)) where R is a reason (1 to
					// qty-1)/qty
					z = ((double) j / (double) (qty + 1)) * zn + (1 - ((double) j / (double) (qty + 1)));

					// get xz axis projected onto xy axis for potential
					// triangulation of side
					if (varSelected == 'x') {
						if (indexOfVector(aux1, createVector2D(getVector2DX(ptsOnBoundPerimeter.get(i)),z)) < 0)
							aux1.add(createVector2D(getVector2DX(ptsOnBoundPerimeter.get(i)), z));
						if (j == qty + 1)
                            sidePtsToAdd.addAll(aux1);
					} else // get yz axis projected onto xy axis for potential
					{		// triangulation of side
					   if (indexOfVector(aux1, createVector2D(getVector2DY(ptsOnBoundPerimeter.get(i)), z)) < 0)
						 aux1.add(createVector2D(getVector2DY(ptsOnBoundPerimeter.get(i)), z));
					   if (j == qty + 1)
                       sidePtsToAdd.addAll(aux1);
                    }
				}

				if (zn <zant)
                if (sidePtsToAdd.size() >= 3)
                 {
                   //triangulate right third of front side of cylinder side, and after that, get symmetric triangles on back part   
                   side2DTriangles.addAll(triangulatePortionOfASide(sidePtsToAdd));
                   sidePtsToAdd = new ArrayList<Vector2D>();
                   sidePtsToAdd.addAll(aux1);
                 }
             zant = zn;
			} 
			else// if z is aprox. 1 top and bottom bound on that point coincide
			{    zant = 1; 
				if (varSelected == 'x')
					vectorWithZEquals1 = createVector2D(getVector2DX(ptsOnBoundPerimeter.get(i)), 1);
				else
					vectorWithZEquals1 = createVector2D(getVector2DY(ptsOnBoundPerimeter.get(i)), 1);

               aux1 = new ArrayList<Vector2D>();
               aux1.add(vectorWithZEquals1);
			   if (sidePtsToAdd.size() > 0)
               {
			    if (getVector2DY(sidePtsToAdd.get(sidePtsToAdd.size() - 1)) > 1)//z>1 in the point before this, then function has decreased to bottom at this point
                {
                 if (indexOfVector(sidePtsToAdd, vectorWithZEquals1) < 0)
                     sidePtsToAdd.add(vectorWithZEquals1);
                 vectorWithZEquals1 = null;
                }
              }
		   }
		}

		if (sidePtsToAdd.size() == 0)// top and bottom bound coincide completely (all z = 1)
			return;

		sidePts.addAll(sidePtsToAdd);
	}

	// Pre: need to triangulate sides of solid that has completely parallel
	// bottom and top bounds
	// Post:return set of 3D triangles forming sides of solid
	private void triangulateSolidSides(double qty) {
		List<Triangle3D> solidSide3DTriangles = new ArrayList<Triangle3D>();
		if (data.getBoundChoice() == 'c')
			triangulateCylinderSides(solidSide3DTriangles, qty);
		else if (data.getBoundChoice() == 'r')
			triangulateCubeSides(solidSide3DTriangles, qty);
		else if (data.getBoundChoice() == 't')
			triangulateTriFaceCubeSolidSides(solidSide3DTriangles, qty);
		data.setSideTriangles(solidSide3DTriangles);
	}

	// Pre: need to find the normal vectors for each triangle from the bound
	// tessellation approximating the
	// 3D surface (plug into surface to get z value), and each triangle on the
	// bound (forms the bottom),
	// and each triangle on the sides if not bounded
	// RULE 3 and 4
	// Post: each normal vector is found and stored
	private void makeTriangle3Ds() {
		List<Triangle2D> boundTriangles = data.getBoundTriangles();
		List<Triangle3D> threeDtriangles = new ArrayList<Triangle3D>();

		Triangle2D currentTriangle;
		Triangle3D triangleToAdd;
		Iterator<Triangle2D> iter = boundTriangles.iterator();
		while (iter.hasNext()) {
			currentTriangle = iter.next();
			if (currentTriangle != null && getTriangle2DA(currentTriangle) != null && getTriangle2DB(
					currentTriangle) != null) {
				// RULE 3 and 4
				// make bound triangle
				triangleToAdd = createTriangle3D(createVector3D(getVector2DX(getTriangle2DA(currentTriangle)),
						getVector2DY(getTriangle2DA(currentTriangle)), 1), createVector3D(getVector2DX(getTriangle2DB(
								currentTriangle)), getVector2DY(getTriangle2DB(currentTriangle)), 1), createVector3D(
										getVector2DX(getTriangle2DC(currentTriangle)), getVector2DY(getTriangle2DC(
												currentTriangle)), 1), 0);

				if (triangleToAdd != null && getTriangle3DA(triangleToAdd) != null && indexOfTriangle(threeDtriangles,
						triangleToAdd) < 0)
					// make sure no triangles repeated
					threeDtriangles.add(triangleToAdd); // for STL file vertices
														// later

				// make surface triangle
				double a_zval = input.evaluateEquationAt(getTriangle2DA(currentTriangle));
				double b_zval = input.evaluateEquationAt(getTriangle2DB(currentTriangle));
				double c_zval = input.evaluateEquationAt(getTriangle2DC(currentTriangle));

				triangleToAdd = createTriangle3D(createVector3D(getVector2DX(getTriangle2DA(currentTriangle)),
						getVector2DY(getTriangle2DA(currentTriangle)), (a_zval < 1) ? 1 : a_zval), createVector3D(
								getVector2DX(getTriangle2DB(currentTriangle)), getVector2DY(getTriangle2DB(
										currentTriangle)), (b_zval < 1) ? 1 : b_zval), createVector3D(getVector2DX(
												getTriangle2DC(currentTriangle)), getVector2DY(getTriangle2DC(
														currentTriangle)), (c_zval < 1) ? 1 : c_zval), 1);
				if (triangleToAdd != null && getTriangle3DA(triangleToAdd) != null && indexOfTriangle(threeDtriangles,
						triangleToAdd) < 0)
					// make sure no triangles repeated
					threeDtriangles.add(triangleToAdd); // for STL file vertices
														// later
			}
			// PSEUDO-CODE FOR TRIANGULATING AND FINDING NORMALS FOR SIDES
			// if currentTriangle has two vertices on the perimeter of the
			// bound:
			// get the boundTriangle and surfaceTriangle corresponding to this
			// currentTriangle
			// take the side that is on the perimeter of the boundTriangle and
			// the surfaceTriangle
			// if both endpoints of that side on the surfaceTriangle have
			// zvalues>1:
			// form a trapezoid with the following sides:
			// the side of the boundTriangle on the perimeter
			// the side of the surfaceTriangle on the perimeter
			// the two sides formed by connecting the same x,y pair on the
			// perimeter of the
			// boundTriangle as the surfaceTriangle (different z values)
			// add extra points on the trapezoid
			// rotate the trapezoid so that the y (or x or z) values are all
			// zero
			// else if one endpoint z>1 and other has z=1: (should be only other
			// case)
			// form a triangle with the following sides:
			// the side of the boundTriangle on the perimeter
			// the side of the surfaceTriangle on the perimeter
			// the one side formed by connecting the same x,y pair on the
			// perimeter of the
			// boundTriangle as the surfaceTriangle with different z values
			// add extra points on the triangle
			// rotate the triangle so that the z value not equal to 1 becomes 1
			// and then subtract
			// 1 from all points to get just x,y points

			// use the 2D tessellation algorithm to tessellate the shape
			// translate back to the original 3D surface for each point in each
			// triangle
			// make sure the points are in CCW order as seen from the outside of
			// the object
			// form the normal vector pointing outwards from the object
			// put triangle and normal vector into Lists

		}
		triangulateSolidSides(getExtraPointsQty());
		data.setThreeDTriangles(threeDtriangles);
	}

	// Pre: need the center of mass of x,y values
	// Post: center of mass returned, or null if parameter is not as expected
	private Vector2D getCenterOfMass(Object vertices[]) {
		if (vertices != null && vertices.length > 0) {
			double cx = 0, cy = 0;
			for (Object vertex : vertices) {
				cx += getVector2DX((Vector2D) vertex);
				cy += getVector2DY((Vector2D) vertex);
			}
			cx /= vertices.length;
			cy /= vertices.length;
			return createVector2D(cx, cy);
		}
		return null;
	}
}