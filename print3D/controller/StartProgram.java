package print3D.controller;

import print3D.model.Model;
import print3D.view.TextUserInterface;

/*
 * Le Moyne Capstone Fall 2017
 * Surface for 3D Printer - translating a 3D surface
 * 		to a 3D printer-friendly STL file
 * Alejandro Sanchez Gonzalez and Jessica Rankins
 * 
 * Last edited by Jessica on 9/7
 */

public class StartProgram {

	public static void main(String[] args) {
		SurfaceToSTL controller = new SurfaceToSTL(new Model(),new TextUserInterface());
		controller.go();
	}

}
