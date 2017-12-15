package print3D.view;

public interface CommunicateToView {

/*
 * Le Moyne Capstone Fall 2017 
 * CommunicateToView - interface for communicating with view (text user interface)
 * 
 * Alejandro Sanchez Gonzalez and Jessica Rankins
 * 
 * Last edited by Jessica on 11/25
 */

	// Pre: Need to get the name of the solid
	// Post: name of solid returned as string
	public String getSolidName();

	// Pre: Need a 3D surface equation
	// Post: equation returned as a string
	public String getUserEquation();

	// Pre: 3D surface input from user is invalid
	// Post: message displayed to user about validity
	public void inputInvalid(String message);

	// Pre: Need a bound option for this function (rectangle, triangle, or
	// circle)
	// Post: bound choice returned as a char
	public char getUserBoundOption();

	// Pre: Need a bound for this function
	// Post: part of bound returned as a string
	public String getUserBoundPart(String message);

	// Pre: Need a resolution for the solid from the user
	// Post: resolution returned as a String
	public String getUserresolution();
	
	//Pre: Need to notify the user that program is done
	//Post: user notified that program is done
	public void done();
}
