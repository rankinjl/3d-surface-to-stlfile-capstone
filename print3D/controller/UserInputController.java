package print3D.controller;

import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.Pattern;

import print3D.model.CommunicateToModel;
import print3D.view.CommunicateToView;
import triangulation.CommunicateToTriangulation;
import triangulation.Vector2D;
import triangulation.Vector3D;

/*
 * Le Moyne Capstone Fall 2017
 * UserInputController - a controller class.
 * Contains logic to get user input from UI, validate, and store in Model.
 * 
 * Fulfills Functional Requirement 1: user entering and system storing valid information
 * 		for the 3D model, notifying user of invalid data
 * 
 * Uses CommunicateToTriangulation for Vector2D and Vector3D methods
 * 
 * Alejandro Sanchez Gonzalez and Jessica Rankins
 * 
 * Last edited by Jessica on 11/25
 * 
 */

public class UserInputController extends CommunicateToTriangulation {

	private CommunicateToView tui;
	private CommunicateToModel data;

	public UserInputController(CommunicateToModel model, CommunicateToView view) {
		tui = view;
		data = model;
	}

	// Pre: need to start the surface to stl file process
	// Post: surface to stl file process completed
	public void go() {
		if (tui != null && data != null) {
			boolean tryagain = true;
			do {
				try {
					//functional requirement 1.1
					getSolidName();
					//functional requirement 1.2
					getUserEquation();
					//functional requirement 1.3
					getresolution();
					//functional requirement 1.4
					getUserBound();
					tryagain = false;
				} catch (NotAboveXYPlaneException ex) {
					tryagain = true;
				} /*
					 * catch(Exception e) {
					 * tui.inputInvalid("An error has occurred in UserInput!");
					 * tryagain=false; }
					 */
			} while (tryagain);
		}
	}

	// Pre: need to display error message with messageForTui to user interface
	// Post: error message displayed
	public void inputInvalid(String messageForTui) {
		tui.inputInvalid(messageForTui);
	}

	// Pre: Need to notify the user that program is done
	// Post: user notified that program is done
	public void done() {
		tui.done();
	}

	// Pre: need to get the equation from the user
	// Post: valid equation from user set
	private void getUserEquation() {
		String exp = tui.getUserEquation();
		ArrayList<String> equation = null;
		do {
			while ((equation = equationValidation(exp)) == null) {
				tui.inputInvalid("The equation is invalid!");
				exp = tui.getUserEquation();
			}
		} while (!data.setEquation(equation));

	}

	// Pre: need to get the resolution setting from user
	// Post: valid resolution from user set
	private void getresolution() {
		String input = tui.getUserresolution();
		boolean tryagain = true;
		int resolution = 0;
		do {
			try {
				resolution = Integer.parseInt(input); // may throw a
														// NumberFormatException
				if (resolution < 1 || resolution > 10)
					throw new NumberFormatException();
				else
					tryagain = false;
			} catch (NumberFormatException nfe) {
				tui.inputInvalid("The resolution setting \'" + input + "\' is invalid!");
				input = tui.getUserresolution();
				tryagain = true;
			}
			if (!tryagain) {
				if (!data.setresolution(resolution))
					tryagain = true;
			}
		} while (tryagain);

	}

	// Pre: need to get the name of solid from user
	// Post: valid name of solid from user set
	private void getSolidName() {
		String input = tui.getSolidName();
		boolean tryagain = true;
		do {
			if (input == null || input.trim().isEmpty() || input.trim().getBytes().length > 80 || !Pattern.matches(
					"[ \\w\\-]*", input) // upper,lowercase letters,digits,-,_,
											// space
					|| !data.setSolidName(input.trim())) {
				tryagain = true;
				tui.inputInvalid("The name of the solid is invalid!");
				input = tui.getSolidName();
			} else
				tryagain = false;
		} while (tryagain);

	}

	// Pre: need to get and set bound from user
	// Post: valid bound from user set
	// may through NotAboveXYPlaneException when 3D surface is not above bound
	// in certain spots
	private void getUserBound() throws NotAboveXYPlaneException {
		char boundChoice;
		boundChoice = tui.getUserBoundOption();
		while (boundChoice != 'c' && boundChoice != 't' && boundChoice != 'r') {
			tui.inputInvalid("The bound choice " + boundChoice + " is invalid!");
			boundChoice = tui.getUserBoundOption();
		}
		data.setBoundChoice(boundChoice);

		switch (boundChoice) {
		case 'c':
			try {
				getCircleInformation();
				break;
			} catch (NotAboveXYPlaneException ex) {
				throw ex;
			}
		case 't':
			try {
				getTriangleInformation();
				break;
			} catch (NotAboveXYPlaneException ex) {
				throw ex;
			}
		case 'r':
			try {
				getRectangleInformation();
				break;
			} catch (NotAboveXYPlaneException ex) {
				throw ex;
			}
		}
	}

	// Pre: bound choice is triangle, need to get triangle vertices
	// Post: valid triangle vertices from the user set
	// may through NotAboveXYPlaneException when 3D surface is not above bound
	// in certain spots
	private void getTriangleInformation() throws NotAboveXYPlaneException {
		String bound;
		boolean repeat = true;
		Vector2D triangleVertices[];
		do {
			triangleVertices = new Vector2D[3]; // 3 vertices
			String triVertexNumbers[] = { "first", "second", "third" };
			for (int i = 0; i < 3; i++) {
				bound = tui.getUserBoundPart("Enter the " + triVertexNumbers[i] + " vertex for the triangle in "
						+ "the form x,y: ");
				while ((triangleVertices[i] = vertexValidation(bound)) == null) {
					tui.inputInvalid("The triangle vertex '" + bound + "' is invalid!");
					bound = tui.getUserBoundPart("Enter the " + triVertexNumbers[i] + " vertex for the triangle in "
							+ "the form x,y: ");
				}
			}
			if (!formsTriangle(triangleVertices)) {
				repeat = true;
				tui.inputInvalid("The triangle vertices do not form a triangle!");
			} else {
				triangleVertices = putInCCWOrder(triangleVertices);

				// calculate new all-positive x,y vertices and adjust equation
				// RULE 1
				triangleVertices = satisfyOctantRule(triangleVertices);
				if (triangleVertices == null)
					repeat = true;
				else
					repeat = false;

				if (!repeat && !aboveXYPlane(triangleVertices)) 
					// not above XY plane
				{
					tui.inputInvalid("The surface does not seem to be above the XY plane...");
					throw new NotAboveXYPlaneException();
				}
			}
		} while (repeat);

		if (triangleVertices != null)
			data.setBound(triangleVertices);
	}

	// Pre: need to determine if the vertices given form a triangle
	// uses determinants to find area of triangle using points - if
	// area==0, invalid collinear points
	// Post: return true if form a triangle, false otherwise
	private boolean formsTriangle(Vector2D vertices[]) {
		if (vertices != null && vertices.length == 3) {
			if (0 != .5 * ((getVector2DX(vertices[0]) - getVector2DX(vertices[1])) * (getVector2DY(vertices[1])
					- getVector2DY(vertices[2])) - (getVector2DX(vertices[1]) - getVector2DX(vertices[2]))
							* (getVector2DY(vertices[0]) - getVector2DY(vertices[1]))))
				return true;
		}
		return false;

	}

	// Pre: bound choice is rectangle, need to get rectangle vertices
	// Post: valid rectangle vertices from the user set
	// may through NotAboveXYPlaneException when 3D surface is not above bound
	// in certain spots
	private void getRectangleInformation() throws NotAboveXYPlaneException {
		boolean repeat = true;
		String bound;
		Vector2D rectangleVertices[];
		do {
			rectangleVertices = new Vector2D[4]; // 4 vertices
			String rectVertexNumbers[] = { "first", "second", "third", "fourth" };
			for (int i = 0; i < 4; i++) {
				bound = tui.getUserBoundPart("Enter the " + rectVertexNumbers[i] + " vertex for the rectangle in "
						+ "the form x,y: ");
				while ((rectangleVertices[i] = vertexValidation(bound)) == null) {
					tui.inputInvalid("The rectangle vertex '" + bound + "' is invalid!");
					bound = tui.getUserBoundPart("Enter the " + rectVertexNumbers[i] + " vertex for the rectangle in "
							+ "the form x,y: ");
				}
			}
			if (!formsRectangle(rectangleVertices)) {
				repeat = true;
				tui.inputInvalid("The rectangle vertices do not form a rectangle!");
			} else {
				rectangleVertices = putInCCWOrder(rectangleVertices);

				// calculate new all-positive x,y vertices and adjust equation
				// RULE 1
				rectangleVertices = satisfyOctantRule(rectangleVertices);
				if (rectangleVertices == null)
					repeat = true;
				else
					repeat = false;

				if (!repeat && !aboveXYPlane(rectangleVertices)) 
					// rectangleVertices not above xy plane
				{
					tui.inputInvalid("The surface does not seem to be above the XY plane...");
					throw new NotAboveXYPlaneException();
				}
			}
		} while (repeat);

		if (rectangleVertices != null)
			data.setBound(rectangleVertices);
	}

	// Pre: need to put the vertices in arbitrary counter clockwise order
	// Post: vertices returned in a counter clockwise order
	private Vector2D[] putInCCWOrder(Vector2D[] vertices) {
		if (vertices != null && vertices.length == 3) {
			return ccwOrderHelper(vertices, 1, 2);
		} else if (vertices != null && vertices.length == 4) {
			// check 1x2 then 2x3 then 1x2 again
			return ccwOrderHelper(ccwOrderHelper(ccwOrderHelper(vertices, 1, 2), 2, 3), 1, 2);
		}
		return null;
	}

	// Pre: need to put two vertices in relative counterclockwise order to each
	// other
	// Post: vertices returned
	private Vector2D[] ccwOrderHelper(Vector2D[] vertices, int index1, int index2) {
		if (vertices == null || index1 < 0 || index2 < 0 || index1 >= vertices.length || index2 >= vertices.length
				|| index1 == index2)
			return null;
		Vector3D ba = createVector3D(getVector2DX(vertices[index1]) - getVector2DX(vertices[0]), getVector2DY(
				vertices[index1]) - getVector2DY(vertices[0]), 0);
		Vector3D ca = createVector3D(getVector2DX(vertices[index2]) - getVector2DX(vertices[0]), getVector2DY(
				vertices[index2]) - getVector2DY(vertices[0]), 0);
		Vector3D crossproduct = crossVector3D(ba, ca);

		// positive if A,B,C clockwise, negative if A,B,C counterclockwise
		if (getVector3DZ(crossproduct) > 0) // CW A,B,C so swap
		{
			Vector2D temp = vertices[index1];
			vertices[index1] = vertices[index2];
			vertices[index2] = temp;
		} // otherwise leave them how they are
		return vertices;
	}

	// Pre: need to determine if the vertices given form a rectangle
	// since the diagonals of a rectangle are congruent and bisect each other,
	// find the center of mass cx,cy (which is the center of the rectangle)
	// and make sure the (square of the) distance
	// between the center of mass and each vertex is the same
	// Post: return true if form a rectangle, false otherwise
	private boolean formsRectangle(Vector2D vertices[]) {
		if (vertices != null && vertices.length == 4) {
			Vector2D center = getCenterOfMass(vertices); // x,y
			if (center == null)
				return false;

			double centerX = getVector2DX(center);
			double centerY = getVector2DY(center);

			// make sure the distance (squared) from cx,cy to each
			// vertex is the same
			double distance = Math.pow(centerX - getVector2DX(vertices[0]), 2) + Math.pow(centerY - getVector2DY(
					vertices[0]), 2);
			if (distance <= 0) // a vertex (or multiple) are at the center - not
								// a rectangle
				return false;
			for (Vector2D vertex : vertices) {
				if (centerX == getVector2DX(vertex) && centerY == getVector2DY(vertex)) 
					// vertex in middle..should not happen
					return false;
				if (distance != (Math.pow(centerX - getVector2DX(vertex), 2) + Math.pow(centerY - getVector2DY(vertex),
						2)))
					return false;
			}
			return true;
		}
		return false;

	}

	// Pre: bound choice is circle, need to get center point and radius
	// Post: valid center point and radius from user set
	// may through NotAboveXYPlaneException when 3D surface is not above bound
	// in certain spots
	private void getCircleInformation() throws NotAboveXYPlaneException {
		boolean repeat = true;
		String bound;
		Vector2D center = null;
		double circleRadius;
		Vector2D vertices[] = null;

		do {
			bound = tui.getUserBoundPart("Enter the radius for the circle: ");
			while ((circleRadius = radiusValidation(bound)) <= 0) {
				tui.inputInvalid("The circle radius '" + bound + "' is invalid!");
				bound = tui.getUserBoundPart("Enter the radius for the circle: ");
			}

			bound = tui.getUserBoundPart("Enter the center point for the circle in " + "the form x,y: ");
			while ((center = vertexValidation(bound)) == null) {
				tui.inputInvalid("The circle point '" + bound + "' is invalid!");
				bound = tui.getUserBoundPart("Enter the center point for the circle in " + "the form x,y: ");
			}
			// calculate new all-positive x,y center and adjust equation RULE 1
			vertices = new Vector2D[3]; // 3 vertices for center and minx and
										// miny vertices
			vertices[0] = center;
			vertices[1] = createVector2D(getVector2DX(center) - circleRadius, getVector2DY(center));
			vertices[2] = createVector2D(getVector2DX(center), getVector2DY(center) - circleRadius);

			vertices = satisfyOctantRule(vertices);
			if (vertices == null)
				repeat = true;
			else {
				repeat = false;
				setVector2DX(center, getVector2DX(vertices[0]));
				setVector2DY(center, getVector2DY(vertices[0]));
			}
		} while (repeat);

		double centerX = getVector2DX(center);
		double centerY = getVector2DY(center);
		Vector2D circleVertices[] = new Vector2D[4]; // left,top,right,bottom
														// vertices around
														// circle
		circleVertices[0] = createVector2D(centerX - circleRadius, centerY); // left
		circleVertices[1] = createVector2D(centerX, centerY + circleRadius); // top
		circleVertices[2] = createVector2D(centerX + circleRadius, centerY); // right
		circleVertices[3] = createVector2D(centerX, centerY - circleRadius); // bottom
		if (!aboveXYPlane(circleVertices)) {
			tui.inputInvalid("The surface does not seem to be above the XY plane...");
			throw new NotAboveXYPlaneException();
		}
		if (circleVertices != null)
			data.setBound(circleVertices);
	}

	// Pre: must check and see if surface is above the XY plane
	// Post: true returned if surface at center point and vertices is
	// above XY plane, otherwise false returned
	private boolean aboveXYPlane(Vector2D vertices[]) {
		if (vertices != null) {
			for (Vector2D vertex : vertices) {
				try {
					if (evaluateEquationAt(vertex) >= 0) // if any point returns
															// nonnegative
						return true;
				} catch (IllegalArgumentException e) {
					return false;
				}
			}
			// if no vertex is positive, check center of mass of vertices
			Vector2D center = getCenterOfMass(vertices);
			if (center == null)
				return false;
			try {
				if (evaluateEquationAt(center) >= 0)
					return true;
			} catch (IllegalArgumentException e) {
				return false;
			}
		}
		return false;
	}

	// Pre: need to evaluate the equation at this x,y point. Equation has to be
	// valid
	// Post: double evaluated returned, or exception thrown
	// may through IllegalArgumentException when point is not valid
	public double evaluateEquationAt(Vector2D point) throws IllegalArgumentException {
		if (point != null) {
			ArrayList<String> equation = data.getEquation();
			if (equation != null) {
				// evaluate equation at x=point[0],y=point[1]
				// must map each String in the equation to mathematical function
				// or operator or variable
				// then evaluate (using java Math library where necessary - for
				// example Math.sqrt()
				double res = 0;
				double eval = 0;
				ArrayList<String> postfixExp = toPostfixNotation(equation);
				Stack<Object> stack = new Stack<Object>();
				Object exch;
				for (int i = 0; i < postfixExp.size(); i++) {
					String op = postfixExp.get(i);
					if ((op.charAt(0) >= '0' && op.charAt(0) <= '9') || op.equals("(")) {
						stack.push(op);
					} else if (op.equals("x")) {
						stack.push(getVector2DX(point));
					} else if (op.equals("y")) {
						stack.push(getVector2DY(point));
					} else if (op.equals("pi")) {
						stack.push(3.14);
					} else if (op.equals(")")) {
						exch = stack.pop();// take the only one result within ()
						stack.pop();// remove '('
						stack.push(exch);
					} else if (op.equals("-") || op.equals("+") || op.equals("*") || op.equals("/") || op.equals("^")) {
						res = 0;
						for (int j = 0; j < 2; j++) {
							if (stack.size() != 0) {

								if (j == 1)// pop the 2 operands and push the
											// result of operation
								{
									res = Double.parseDouble(stack.pop().toString());
									res = executeOperation(res, eval, op);
									stack.push(res);
								} else {
									eval = Double.parseDouble(stack.pop().toString());
									if (stack.size() == 0) 
										// the case when equation begins
										// with a '-' or '+' sign
									{
										res = executeOperation(res, eval, op);
										stack.push(res);
										break;
									} else if (stack.peek().equals("(")) {
										res = executeOperation(res, eval, op);
										stack.push(res);
										break;
									}
								}

							}

						}
					} else// the case the operator is a function: sin, cos, tan,
							// ln, sqrt, abs, etcetera
					{
						if (stack.size() != 0) {
							res = 0;
							eval = Double.parseDouble(stack.pop().toString());
							res = executeFunction(eval, op);
							// pop the operand and push the result of operation
							stack.push(res);
						}
					}
				}
				return Double.parseDouble(stack.pop().toString());
			}
		}
		throw new IllegalArgumentException(); // only if nothing returned
	}

	// Pre: must execute the given operation on op1 and op2
	// Post: operation executed and result returned, or error thrown if illegal
	// operation
	// may through IllegalArgumentException when Oper is not valid
	private double executeOperation(double op1, double op2, String Oper) throws IllegalArgumentException {
		double result = 0;
		switch (Oper) {
		case "+":
			result = op1 + op2;
			break;
		case "-":
			result = op1 - op2;
			break;
		case "*":
			result = op1 * op2;
			break;
		case "/":
			if (op2 != 0)
				result = op1 / op2;
			else
				throw new IllegalArgumentException();
			break;
		case "^":
			result = Math.pow(op1, op2);
			break;
		default:
			throw new IllegalArgumentException();
		}
		return result;

	}

	// Pre: must execute function on the given operand
	// Post: function executed on function and result returned
	// may through IllegalArgumentException when Func is not valid
	private double executeFunction(double op, String Func) throws IllegalArgumentException {
		double result = 0;
		switch (Func) {
		case "sqrt":
			if (op < 0)
				result = 0;
			else
				result = Math.sqrt(op);
			break;
		case "abs":
			result = Math.abs(op);
			break;
		case "ln":
			result = Math.log(op);
			break;
		case "sin":
			result = Math.sin(op);
			break;
		case "cos":
			result = Math.cos(op);
			break;
		case "tan":
			result = Math.tan(op);
			break;
		case "exp":
			result = Math.exp(op);
			break;
		default:
			throw new IllegalArgumentException();
		}
		return result;
	}

	// Pre: need to satisfy octant rule (RULE 1) as specified by
	// http://www.fabbers.com/tech/STL_Format
	// (all vertex coordinates must be positive-definite (nonnegative and
	// nonzero))
	// Post: vertices returned after
	private Vector2D[] satisfyOctantRule(Vector2D vertices[]) {
		if (vertices != null) {
			double ymin;
			double xmin;
			try {
				xmin = getMin(vertices, 0);
				ymin = getMin(vertices, 1);
			} catch (IllegalArgumentException e) {
				return null;
			}
			ArrayList<String> equation = data.getEquation();

			if (ymin < 1) {
				// make each "y" in equation become (y-(abs(ymin)+1))
				for (int i = 0; i < equation.size(); i++) {
					if (equation.get(i).equals("y")) {
						equation.add(i, "(");
						// y at i+1
						equation.add(i + 2, "-");
						String value = "" + (Math.abs(ymin) + 1);
						equation.add(i + 3, value);
						equation.add(i + 4, ")");
						i += 4;
					}
				}
			}
			if (xmin < 1) {
				// make each "x" in equation become (x-(abs(xmin)+1))
				for (int i = 0; i < equation.size(); i++) {
					if (equation.get(i).equals("x")) {
						equation.add(i, "(");
						// x at i+1
						equation.add(i + 2, "-");
						String value = "" + (Math.abs(xmin) + 1);
						equation.add(i + 3, value);
						equation.add(i + 4, ")");
						i += 4;
					}
				}
			}
			data.setEquation(equation);

			for (int i = 0; i < vertices.length; i++) {
				if (xmin < 1) // change the x values in triangleVertices
					setVector2DX(vertices[i], getVector2DX(vertices[i]) + (Math.abs(xmin) + 1));
				if (ymin < 1) // change the y values in triangleVertices
					setVector2DY(vertices[i], getVector2DY(vertices[i]) + (Math.abs(ymin) + 1));
			}
			return vertices;
		}
		return null;
	}

	// Pre: given an array of vertices and an index (x=0,y=1)
	// need the minimum
	// Post: minimum returned or error thrown
	// may through IllegalArgumentException when vertices or index are not valid
	private double getMin(Vector2D vertices[], int index) throws IllegalArgumentException {
		if (vertices != null && (index == 0 || index == 1)) {
			double min;
			switch (index) {
			case 0:
				min = getVector2DX(vertices[0]);
				for (int i = 1; i < vertices.length; i++) {
					if (getVector2DX(vertices[i]) < min)
						min = getVector2DX(vertices[i]);
				}
				return min;
			case 1:
				min = getVector2DY(vertices[0]);
				for (int i = 1; i < vertices.length; i++) {
					if (getVector2DY(vertices[i]) < min)
						min = getVector2DY(vertices[i]);
				}
				return min;
			}
		}
		throw new IllegalArgumentException();
	}

	// Pre: need the center of mass of x,y values
	// Post: center of mass returned, or null if parameter is not as expected
	private Vector2D getCenterOfMass(Vector2D vertices[]) {
		if (vertices != null && vertices.length > 0) {
			double cx = 0, cy = 0;
			for (Vector2D vertex : vertices) {
				cx += getVector2DX(vertex);
				cy += getVector2DY(vertex);
			}
			cx /= vertices.length;
			cy /= vertices.length;
			return createVector2D(cx, cy);
		}
		return null;
	}

	// Pre: need to validate equation
	// Post: returns equation as a list if valid, null otherwise
	private ArrayList<String> equationValidation(String eq) {
		ArrayList<String> exp = null;
		if (eq != "") {
			exp = eqStringToList(eq);
			if (exp == null)
				return null;

			// move entire surface up one in the z direction to counteract
			// moving the bounds up 1
			// when we do the normal vectors (entire 3D object must be positive
			// - not negative or 0)
			exp.add("+");
			exp.add("1");

			ArrayList<String> pfExp = toPostfixNotation(exp);
			if (pfExp == null)
				return null;
			return expressionIsCorrect(pfExp) ? exp : null;
		}
		return exp;
	}

	// Pre: equation has been read from user as a plain text string and passed
	// as parameter
	// Post: returns a list with each operand, operator and function from user's
	// equation,
	//       returns null if operator, operand and functions have invalid format
	// or characters
	// for which can't been successfully constructed and delimited
	private ArrayList<String> eqStringToList(String userText) {
		userText = userText.replaceAll("\\s","");
		userText = userText.toLowerCase();
		char[] txt = userText.toCharArray();
		ArrayList<String> exp = new ArrayList<String>();
		String aux = "";
		// *check for letters other than sin cos tan sqrt abs exp ln x y
		// *check for symbols other than - + ( ) . ^ * /
		for (int i = 0; i < txt.length; i++) {
			if ((txt[i] >= '0' && txt[i] <= '9') || txt[i] == '.') {
				if (txt[i] == '.' && (aux.indexOf(".") >= 0)) 
					// number already has a '.', another isn't accepted
					return null;
				aux += txt[i];
			} else {

				if (txt[i] == '+' || txt[i] == '-' || txt[i] == '*' || txt[i] == '/' || txt[i] == '^') {
					if (aux != "") {
						if (aux.indexOf('.') == 0 || aux.indexOf('.') == (aux.length() - 1))
							return null;
						exp.add(aux);
						aux = "";
					}
					exp.add(Character.toString(txt[i]));

				} else {
					if (aux != "")// if aux isn't empty a number was read which
									// is not accepted before a function or
									// variable without an operator between them
					{
						if (txt[i] != ')')
							return null;
						exp.add(aux);
						aux = "";
					}
					if (exp.size() > 0) {
						if (exp.get(exp.size() - 1).equals("x") || exp.get(exp.size() - 1).equals("y"))
							// before a function or variable
							//can't go a variable without operator between them
						{
							if (txt[i] != ')')
								return null;
						} else {
							if (txt[i] == ')') {
								if ((exp.get(exp.size() - 1).charAt(0) < '0' || exp.get(exp.size() - 1).charAt(0) > '9')
										&& !exp.get(exp.size() - 1).equals("pi") && !exp.get(exp.size() - 1).equals(
												")"))
									// before closing ) only numbers or pi or
										// variable or another ) are accepted
								{
									return null;
								}
							}
						}
					}
					if (txt[i] == 'x' || txt[i] == 'y' || txt[i] == '(' || txt[i] == ')') {
						exp.add(Character.toString(txt[i]));
						continue;
					} else if (txt[i] == 's') {
						if (userText.indexOf("sqrt", i) == i) {

							exp.add("sqrt");
							i += 3;
						} else if (userText.indexOf("sin", i) == i) {
							exp.add("sin");
							i += 2;
						} else {
							return null;
						}

					} else if (txt[i] == 'c') {
						if (userText.indexOf("cos", i) == i) {
							exp.add("cos");
							i += 2;
						} else {

							return null;
						}

					} else if (txt[i] == 'p') {

						if (userText.indexOf("pi", i) == i) {

							exp.add("pi");
							i += 1;
							continue;
						} else {
							return null;
						}

					} else if (txt[i] == 't') {

						if (userText.indexOf("tan", i) == i) {

							exp.add("tan");
							i += 2;
						} else {
							return null;
						}

					} else if (txt[i] == 'l') {

						if (userText.indexOf("ln", i) == i) {

							exp.add("ln");
							i += 1;
						} else {
							return null;
						}

					} else if (txt[i] == 'a') {
						if (userText.indexOf("abs", i) == i) {

							exp.add("abs");
							i += 2;
						} else {
							return null;
						}

					} else if (txt[i] == 'e') {
						if (userText.indexOf("exp", i) == i) {

							exp.add("exp");
							i += 2;
						} else {

							return null;
						}

					} else
						return null;
				}
				if (i == txt.length - 1)
					return null;
			}
		}
		if (aux != "") {
			if (aux.indexOf('.') == 0 || aux.indexOf('.') == (aux.length() - 1))
				return null;
			exp.add(aux);
			aux = "";
		}
		return exp;
	}

	// Pre: the equation has been converted into a list and passed as parameter
	// Post: returns a list with the equation converted into a postfix notation
	private ArrayList<String> toPostfixNotation(ArrayList<String> exp) {
		ArrayList<String> result = new ArrayList<String>();
		Stack<String> stack = new Stack<String>();

		for (int i = 0; i < exp.size(); i++) {
			if (exp.get(i).equals("(")) {
				result.add("(");
				stack.push(exp.get(i));
			} else if (exp.get(i).equals(")")) {
				if (!stack.isEmpty()) {
					while (!stack.peek().equals("(")) {
						result.add(stack.pop());
						if (stack.isEmpty())
							return null; // if stack gets empty then there isn't
											// an open ( then no balance
					}
					if (!stack.isEmpty()) // pop the (
						stack.pop();
					result.add(")");
				} else // if stack is empty then there isn't an open ( then no
						// balance
					return null;
			} else if (exp.get(i).equals("+") || exp.get(i).equals("-") || exp.get(i).equals("*") || exp.get(i).equals(
					"/") || exp.get(i).equals("^") || exp.get(i).equals("sqrt") || exp.get(i).equals("sin") || exp.get(
							i).equals("cos") || exp.get(i).equals("tan") || exp.get(i).equals("cot") || exp.get(i)
									.equals("arc") || exp.get(i).equals("abs") || exp.get(i).equals("ln") || exp.get(i)
											.equals("exp")) {
				if (!stack.isEmpty()) {
					while (!stack.peek().equals("(") && !isLowerPrecedence(stack.peek(), exp.get(i))) {
						result.add(stack.pop());
						if (stack.size() == 0)
							break;
					}
				}
				stack.push(exp.get(i));

			} else
				result.add(exp.get(i));
		}
		while (!stack.isEmpty()) {
			if (stack.peek().equals("("))
				return null;
			result.add(stack.pop());
		}
		return result;
	}

	// Pre: operator in auxiliary stack and operator being currently read from
	// equation are passed as parameter
	// Post: returns true if operator in stack has a lower precedence than the
	// one being read from equation, else returns false
	private boolean isLowerPrecedence(String opInStack, String opInExp) {
		boolean isLower = false;
		switch (opInExp) {
		case "*":
			if (opInStack.equals("-") || opInStack.equals("+"))
				isLower = true;
			break;
		case "/":
			if (opInStack.equals("-") || opInStack.equals("+"))
				isLower = true;
			break;
		case "-":
			isLower = false;

			break;
		case "+":
			isLower = false;

			break;
		default:
			if (opInStack.equals("-") || opInStack.equals("+") || opInStack.equals("*") || opInStack.equals("/"))
				isLower = true;
			break;
		}
		return isLower;
	}

	// Pre: equation has been converted into postfix notation and passed as a
	// parameter
	// Post: a reverse polish notation algorithm is applied and returns true if
	// postfix equation (and thus original expression)
	// is correctly formatted otherwise returns false 
	//Reverse Polish Notation
	private boolean expressionIsCorrect(ArrayList<String> postfixExp)
	{
		boolean isCorrect = false;
		String exch = "";
		boolean initSignRead = false;
		Stack<String> stack = new Stack<String>();
		for (int i = 0; i < postfixExp.size(); i++) {
			String op = postfixExp.get(i);
			if (op.equals("pi") || op.equals("x") || op.equals("y") || op.equals("(") || (op.charAt(0) >= '0' && op
					.charAt(0) <= '9')) {
				stack.push(op);
			} else if (op.equals(")")) {
				exch = stack.pop();// take the only one result within ()
				stack.pop();// remove '('
				stack.push(exch);
			} else if (op.equals("-") || op.equals("+") || op.equals("*") || op.equals("/") || op.equals("^")) {
				for (int j = 0; j < 2; j++) {
					if (stack.size() != 0) {
						if ((stack.peek().charAt(0) >= '0' && stack.peek().charAt(0) <= '9') || stack.peek().equals(
								"pi") || stack.peek().equals("x") || stack.peek().equals("y")) {
							stack.pop();
							if (j == 0 && (op.equals("-") || op.equals("+"))) {
								if (stack.size() > 0) {
									if (stack.peek().equals("("))
										// the case when there is a ( followed by a '-' or '+' sign
									{
										stack.push("1"); 
										// push the result of operation(for
										// ease '1' is added as result)
										break;
									}
								} else if (!initSignRead) 
									// the case when equation begins
									// with a '-' or '+' sign
								{
									initSignRead = true;
									stack.push("1"); 
									// push the result of operation(for ease
									// '1' is added as result)
									break;
								} else
									return false;
							} else if (j == 1)
								// pop the 2 operands and push the
								// result of operation(for ease
								// '1' is added as result)
								stack.push("1");
						} else {
							return false;
						}
					} else {
						return false;
					}
				}
			} else// the case the operator is a function: sin, cos, tan, ln,
					// sqrt, abs, etcetera
			{
				if (stack.size() != 0) {
					if ((stack.peek().charAt(0) >= '0' && stack.peek().charAt(0) <= '9') || stack.peek().equals("pi")
							|| stack.peek().equals("x") || stack.peek().equals("y")) {
						stack.pop();// pop the operand and push the result of
									// operation(for ease '1' is added as
									// result)
						stack.push("1");
					} else {
						return false;
					}
				} else {
					return false;
				}
			}
		}
		if (stack.size() == 1)
			isCorrect = true;
		return isCorrect;
	}

	// Pre: need to validate the vertex bound given
	// Post: return double array of vertex coordinates
	// if bound seems valid, null otherwise
	// bound is the user input in the form x,y
	private Vector2D vertexValidation(String bound) {
		Vector2D vertex = null;
		if (bound.trim() != "") {
			// check syntax x,y
			int index = bound.indexOf(",");
			if (bound.indexOf(",", index + 1) != -1) // if contains more than
														// one ','
				return null;
			String boundComponents[] = bound.split(",");
			if (boundComponents.length != 2)
				return null;
			try {
				boundComponents[0] = boundComponents[0].trim();
				boundComponents[1] = boundComponents[1].trim();
				if (boundComponents[0].length() < 1 || boundComponents[1].length() < 1)
					return null;
				vertex = createVector2D(Double.parseDouble(boundComponents[0]), Double.parseDouble(boundComponents[1]));
				return vertex;
			} catch (NumberFormatException e) {
				return null;
			}
		} else
			return null;
	}

	// Pre: need to validate the radius bound given
	// Post: return bound as double if it seems valid, 0 otherwise
	private double radiusValidation(String bound) {
		double radius = 0;
		if (bound != "") {
			try {
				radius = Double.parseDouble(bound);
				if (radius <= 0)
					return 0;
			} catch (NumberFormatException e) {
				return 0;
			}
		}
		return radius;
	}
}
