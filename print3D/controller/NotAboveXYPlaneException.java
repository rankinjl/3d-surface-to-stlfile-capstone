package print3D.controller;

/*
 * Le Moyne Capstone Fall 2017
 * NotAboveXYPlaneException - an exception to use in SurfaceToSTL
 * 
 * Alejandro Sanchez Gonzalez and Jessica Rankins
 * 
 * Last edited by Jessica on 10/2
 * 
 */
//to be thrown when the surface given is not above the XY plane 
	//and the processing has to be started over
public class NotAboveXYPlaneException extends Exception {

	public NotAboveXYPlaneException()
	{
		super();
	}
	
	public NotAboveXYPlaneException(String msg)
	{
		super(msg);
	}
}
