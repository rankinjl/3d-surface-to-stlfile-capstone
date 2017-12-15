package print3D.view;

import java.util.Scanner;

/*
 * Le Moyne Capstone Fall 2017
 * TextUserInterface - the main User Interface component
 * 
 * Alejandro Sanchez Gonzalez and Jessica Rankins
 * 
 * Last edited by Jessica on 10/25
 */

public class TextUserInterface implements CommunicateToView{

	//buffered reader or scanner...
	Scanner user_input; //get the user input from System.in
	
	//Pre: Need a way to communicate with the user
	//Post: TUI created using Scanner
	public TextUserInterface()
	{
		user_input = new Scanner(System.in);
		
		//display License agreement:
		System.out.println("\tThe MIT License (MIT)\n\n"
				+ "\tCopyright (c) 2015 Johannes Diemke\n\n"
				+ "\tPermission is hereby granted, free of charge, to any person obtaining a copy\n"
				+ "\tof this software and associated documentation files (the \"Software\"), to deal\n"
				+ "\tin the Software without restriction, including without limitation the rights\n"
				+ "\tto use, copy, modify, merge, publish, distribute, sublicense, and/or sell\n"
				+ "\tcopies of the Software, and to permit persons to whom the Software is\n"
				+ "\tfurnished to do so, subject to the following conditions:\n\n"
				+ "\tThe above copyright notice and this permission notice shall be included in all\n"
				+ "\tcopies or substantial portions of the Software.\n\n"
				+ "\tTHE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR\n"
				+ "\tIMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,\n"
				+ "\tFITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE\n"
				+ "\tAUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER\n"
				+ "\tLIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,\n"
				+ "\tOUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE\n"
				+ "\tSOFTWARE.\n\n");
		
		
		System.out.println("Welcome to the Surface to 3D printer Program!");
		System.out.println("A few notes to help you get started:\n"
				+ "You must enter your 3D surface equation in the form z = f(x,y).\n"
				+ "You must enter a lower bound in the shape of a triangle, rectangle, or circle.\n"
				+ "This bound for the surface will lie on the XY plane.\n"
				+ "The surface will only be used where positive (above XY plane) and in the bounds specified.\n"
				+ "The surface must be above the XY plane at one or more of the "
						+ "vertices of the bound and the center point.\n");
	}
	
	//Pre: Need to get the name of the solid
	//Post: name of solid returned as string
	public String getSolidName()
	{
		String input;
		System.out.println("What would you like to name this solid?");
		input = user_input.nextLine();
		while(input.isEmpty())
		{
			input = user_input.nextLine();
		}
		return input;
	}
	
	//Pre: Need a 3D surface equation
	//Post: equation returned as a string
	public String getUserEquation()
	{
		String input;
		System.out.println("Please enter a 3D equation in the form of 'z = f(x,y)' (for example 'z = (x^2+y^2)^0.5').\n"
				+ "Note: surface must lie above the XY plane.");
		System.out.print("z = ");
		input = user_input.nextLine();
		while(input.isEmpty())
		{
			input = user_input.nextLine();
		}
		return input;
	}
	
	//Pre: 3D surface input from user is invalid
	//Post: message displayed to user about validity
	public void inputInvalid(String message)
	{
		System.out.println("\nThe information you entered was invalid! Try again.");
		System.out.println("HINT: "+message+"\n");
	}
	
	//Pre: Need a bound option for this function (rectangle, triangle, or circle)
	//Post: bound choice returned as a char 
	public char getUserBoundOption()
	{
		char answer;
		System.out.println("Would you like the bound to be a rectangle ('r'), triangle ('t'), "
				+ "or circle ('c')?\n"
				+ "Note: this bound will be on the XY-plane. ");
		try{
			answer = user_input.nextLine().charAt(0);
		}catch(StringIndexOutOfBoundsException ex)
		{
			answer = 'n';
		}
		while(answer!='c' && answer!='t' && answer!='r')
		{
			System.out.println("Please enter one of the options.");
			System.out.println("Would you like the bound to be a rectangle ('r'), triangle ('t'), "
					+ "or circle ('c')?\n"
					+ "Note: this bound will be on the XY-plane. ");
			try{
				answer = user_input.nextLine().charAt(0);
			}catch(StringIndexOutOfBoundsException ex)
			{
				answer = 'n';
			}
		}
		
		return answer;
		
	}
	
	//Pre: Need a bound for this function
	//Post: part of bound returned as a string 
	public String getUserBoundPart(String message)
	{
		System.out.println(message);
		String bound = user_input.nextLine();
		while(bound.isEmpty())
		{
			bound = user_input.nextLine();
		}
		return bound;
	}

	//Pre: Need a resolution for the solid from the user
	//Post: resolution returned as a String
	public String getUserresolution() 
	{
		String input;
		System.out.print("Please enter the resolution for the triangulation as an integer between 1 and 10: ");
		input = user_input.nextLine();
		while(input.isEmpty())
		{
			input = user_input.nextLine();
		}
		return input;
	}
	
	//Pre: Need to notify the user that program is done
	//Post: user notified that program is done
	public void done()
	{
		System.out.println("The program has finished!");
	}
	
}
