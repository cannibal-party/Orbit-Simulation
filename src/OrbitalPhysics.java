import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Timer;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import java.awt.Graphics2D;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

public class OrbitalPhysics {
	
	static ArrayList<OrbitalBody> listOfBodies = new ArrayList();
	final static int gravConst = 100;
	final static int perturbationCalculationMethod = 0; // 0 = Cowell's Method
	
	final static float deltaTime = (float) 0.01;
	final static int numOfIterations = 1000000;

	public static void main(String [] args)
	{	
		//frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
 		JFrame frame = new JFrame("Oribital Simulation");
		frame.setVisible(true);
		frame.setSize(1000, 1000);
 		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setResizable(false);
 		Test p = new Test();
		OrbitalBody planet = new OrbitalBody();	
		listOfBodies.add(planet);
		planet.setName("Planet #1");
		planet.setMass(1);
		planet.setPosition(100, 100, 100);
		planet.setVelocity(200, 0, 0);

		
		OrbitalBody sun = new OrbitalBody();
		listOfBodies.add(sun);
		sun.setName("Sun");
		sun.setMass(10000);
		sun.setPosition(0, 0, 0);
		sun.setVelocity(0, 0, 0);
		

		p.passList(listOfBodies);
		
		Thread iterate = new Thread(new Runnable() {
			@Override
			public void run() {
				for (int x = 0; x < 10000; x ++){
					float deltaTime = (float) 0.1;
					iterateSimulation(deltaTime);
					frame.repaint();
					//p.paintImmediately(500,500,900,900);
					System.out.println(x);
					System.out.println("PLANET~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
					System.out.println("X Position: " + planet.xPosition);
					System.out.println("Y Position: " + planet.yPosition);
					System.out.println("X Position Old: " + planet.getXOldPosition());
					System.out.println("Y Position Old: " + planet.getYOldPosition());
					System.out.println("X Velocity: " + planet.xVelocity);
					System.out.println("Y Velocity: " + planet.yVelocity);
					System.out.println("X Acceleration: " + planet.xAcceleration);
					System.out.println("Y Acceleration: " + planet.yAcceleration);
				}
			}
		});
		iterate.start();
	}

	private static void iterateSimulation(float deltaTime) {

		double timeCounter = 0;
		for (int x = 0; x < numOfIterations; x++){
			
			// DEBUG
			if (x % numOfIterations/100 == 0){
				
				//System.out.println(planet.posVect.getX());
				
				System.out.println(planet.name);
				System.out.println("t: " + timeCounter);
				System.out.println("p: " + planet.posVect.getX());
				System.out.println("v: " + planet.velVect.getX());
				System.out.println("a: " + planet.accVect.getX());
				System.out.println("");
				
			}	
			
			timeCounter += deltaTime;
			iterateSimulation(deltaTime);				

		}
	}

	
	static void iterateSimulation(float deltaTime) {
		
		// 1. Calculate net force and acceleration from acting on each body.

		for (int i=0; i < listOfBodies.size(); i++){
			
			OrbitalBody currentBody = listOfBodies.get(i);
			Vector3 sumOfAcc = new Vector3();
			
			for (int j = 0; j < listOfBodies.size() ; j++){
				
				if (j != i){
					OrbitalBody pullingBody = listOfBodies.get(j);
									
					if (perturbationCalculationMethod == 0){ // Cowell's Formulation
						Vector3 calculatedAcc = cowellsFormulation(currentBody, pullingBody);
						sumOfAcc.add(calculatedAcc);				
					}
					/*
					else if {
						// TODO: Encke's Method, Variation of Parameters, etc.
					}
					*/
				}
			}
			
			// 2. Iterate and integrate for velocity and then position.
			currentBody.setAcceleration(sumOfAcc.getX(), sumOfAcc.getY(), sumOfAcc.getZ());			
			currentBody.iterateVelThenPos(deltaTime);
		}	
	}	
	
	static Vector3 cowellsFormulation(OrbitalBody currentBody, OrbitalBody pullingBody) {
		

		Vector3 currentPos = currentBody.posVect;
		Vector3 pullingPos = pullingBody.posVect;
		
		Vector3 diffOfPosVect = new Vector3();
		diffOfPosVect.add(pullingPos);	
		currentPos.scale(-1);
		diffOfPosVect.add(currentPos);
		
		Vector3 calculatedAcc = new Vector3();	
		calculatedAcc.add(diffOfPosVect);

		calculatedAcc.scale(-1*gravConst * pullingBody.mass / Math.pow(diffOfPosVect.length(), 3));	
		/*
		if (currentBody.name == "Planet #1"){
			System.out.println(calculatedAcc);
		}
		*/
		return calculatedAcc;
	
	} 
	/*
    public static boolean checkCollision(OrbitalBody body1, OrbitalBody body2) {
		Vector3d diffOfPosVect = new Vector3d();
		diffOfPosVect = body1.posVect;
        diffOfPosVect.sub(body2.posVect);

        if (diffOfPosVect.length() <= body1.radius + body2.radius) {
            return true;
        } else {
            return false;
        }
    }
    */
}

