package CE812ProjectPack;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Polygon;
import java.awt.Toolkit;
import java.awt.geom.Path2D.Float;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.common.Color3f;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.joints.DistanceJointDef;
import org.jbox2d.dynamics.joints.MouseJoint;
import org.jbox2d.dynamics.joints.PrismaticJointDef;
import org.jbox2d.dynamics.joints.PulleyJointDef;
import org.jbox2d.dynamics.joints.RevoluteJointDef;
import org.jbox2d.dynamics.joints.WheelJointDef;

import javax.swing.JOptionPane;

/* Author of the framework Dr.Michael Fairbank
/* Author of the assginment and the game: Ahmet Erdal Yurtseven*/


public class BasicPhysicsEngineUsingBox2D {

	// frame dimensions
	public static int SCREEN_HEIGHT = 1000;
	public static  int SCREEN_WIDTH = 2000;
	public static final Dimension FRAME_SIZE = new Dimension(
			SCREEN_WIDTH, SCREEN_HEIGHT);
	public static float WORLD_WIDTH=45;//metres
	public static  float WORLD_HEIGHT=SCREEN_HEIGHT*(WORLD_WIDTH/SCREEN_WIDTH);// meters - keeps world dimensions in same aspect ratio as screen dimensions, so that circles get transformed into circles as opposed to ovals
	public float oldHeight= WORLD_HEIGHT;
	public static final float GRAVITY=9.8f;
	public static final boolean ALLOW_MOUSE_POINTER_TO_DRAG_BODIES_ON_SCREEN=true;// There's a load of code in basic mouse listener to process this, if you set it to true

	static boolean alreadyDone = false;
	static int count = 0;
	
	public static World world; // Box2D container for all bodies and barriers 

	// sleep time between two drawn frames in milliseconds 
	public static final int DELAY = 20;
	public static final int NUM_EULER_UPDATES_PER_SCREEN_REFRESH=100;
	// estimate for time between two frames in seconds 
	public static final float DELTA_T = DELAY / 1000.0f;
	
	
	public static int convertWorldXtoScreenX(float worldX) {
		return (int) (worldX/WORLD_WIDTH*SCREEN_WIDTH);
	}
	public static int convertWorldYtoScreenY(float worldY) {
		// minus sign in here is because screen coordinates are upside down.
		return (int) (SCREEN_HEIGHT-(worldY/WORLD_HEIGHT*SCREEN_HEIGHT));
	}
	public static float convertWorldLengthToScreenLength(float worldLength) {
		return (worldLength/WORLD_WIDTH*SCREEN_WIDTH);
	}
	public static float convertScreenXtoWorldX(int screenX) {
		return screenX*WORLD_WIDTH/SCREEN_WIDTH;
	}
	public static float convertScreenYtoWorldY(int screenY) {
		return (SCREEN_HEIGHT-screenY)*WORLD_HEIGHT/SCREEN_HEIGHT;
	}
	
	
	
	public static List<BasicParticle> particles;
	public List<BasicPolygon> polygons;
	public List<AnchoredBarrier> barriers;
	
	public static MouseJoint mouseJointDef;
	
	public  BasicRectangle BasicTrunk; 
	public  Wheel wheelBack;
	public  Wheel wheelFront;
	//This ball will hurt the vehicle only once
	public EnemyBall enemyBall;
	CeilingObject ceilingObject;
	CeilingObject ceilingObjectAttach;
	BasicParticle elevatorPulley;
	BasicParticle elevatorPulley2;
	BasicRectangle counter_weight;
	public List<ElasticConnector> connectors;
	BasicRectangle elevator;
	
	BasicPolygon traps_floor1_1;
	BasicPolygon traps_floor1_2;
	BasicPolygon traps_floor1_3;
	BasicRectangle jumpObject;
	BasicRectangle jumpPlatform;
	
	BasicRectangle trapFloor_a;
	BasicRectangle trapFloor_b;
	BasicRectangle trapFloor_c;
	BasicRectangle trapFoundation1;
	BasicRectangle trapFoundation2;
	
	BasicParticle hangObj;
	
	BasicRectangle magnet;
	
	BasicRectangle enemyBallGenerator;
	
	BasicParticle balls1;
	BasicParticle balls2;
	BasicParticle balls3;
	BasicParticle balls4;
	
	AnchoredBarrier_StraightLine lastLine;
	
	public static BasicParticle bombWheel;
	public static LinkedList <BasicParticle> bombWheelList;

	int i = 0;
	
	RevoluteJointDef jointDef = new RevoluteJointDef();
	
	BasicRectangle trigger;
	
	BasicRectangle gameOverTrigger;
	BasicRectangle gameOverTrigger2;
	
	BasicPolygon polygonHealth;
	BasicPolygon polygonHealth2;
	BasicPolygon polygonHealth3;
	
	DebugDraw draw;
	
	static int levelSelect = 0;
	
	float elevatorTimer = 0;
	float elevatorTimer2;
	float jumpTimer = 0;
	float trapTimer = 0;
	float magnetTimer = 0;
	float walkingRectTimer=0;
	float hydrolicTimer = 0;
	

	int n = 1;
	
	static int health = 9;
	boolean IsUserHurt= false;
	boolean IsUserHurt2= false;
	boolean AlreadyShotFinished = false;
	boolean AlreadyShot = false;
	
	boolean isGameEnded = false;
	
	public static enum LayoutMode {GameLevelOne};
	
	Vec2 mganet_to_car;
	
	BasicRectangle hydrolicLift_Big;
	BasicRectangle hydrolicLift_Small;
	
	public BasicPhysicsEngineUsingBox2D() {
		world = new World(new Vec2(0, -GRAVITY));// create Box2D container for everything
		world.setContinuousPhysics(true);

		particles = new ArrayList<BasicParticle>();
		polygons = new ArrayList<BasicPolygon>();
		barriers = new ArrayList<AnchoredBarrier>();
		connectors=new ArrayList<ElasticConnector>();
	
		// pinball:
		float rollingFriction=0.55f;
		float r=.3f;
		float s=1.2f;
	
		
		LayoutMode layout = LayoutMode.GameLevelOne;
	
		//Apply torque to enemyBall
		//enemyBall.body.applyTorque(-25f);

		
		//Car Properties
		BasicTrunk = new BasicRectangle(4f,23f, 0, 0, s, Color.BLUE, 1.2f, s, null, s, 0.2f,BodyType.DYNAMIC);
		wheelBack = new Wheel(2f,21f,0,0f, r/1.6f,Color.RED, 0.45f, 0.6f,BodyType.DYNAMIC);
		wheelFront = new Wheel(3f,21f,0,0f, r/1.6f,Color.RED, 0.45f, 0.6f,BodyType.DYNAMIC);
		
		polygons.add(BasicTrunk);
		particles.add(wheelBack);
		particles.add(wheelFront);
		
		//Car parts
		BasicPolygon p1 = polygons.get(0);		
		BasicParticle p3 = particles.get(0);
		BasicParticle p4 = particles.get(1);
		
		//Generate The Car -- Initialize this after drawing the entire level
		generateCar(p1,p3,p4);
		
		
		ceilingObject = new CeilingObject(36.76f,21.2f, 0, 0, s, Color.YELLOW, 0.2f, s, null, s, 1.6f,BodyType.STATIC);
		ceilingObjectAttach = new CeilingObject(36.76f,20.32f, 0, 0, s, Color.YELLOW, 0.2f, s, null, 0.8f, 0.8f,BodyType.STATIC);
		
		elevatorPulley = new BasicParticle(36.76f,19.62f,0,0f, r,Color.GREEN, 0.76f, 0.6f,BodyType.KINEMATIC);
		elevator = new BasicRectangle(36.76f,11f, 0, 0, s, Color.BLUE, 11f, s, null, 4f, 0.2f,BodyType.DYNAMIC);
		elevatorPulley2 = new BasicParticle(40.45f,19.62f,0,0f, r,Color.GREEN, 0.76f, 0.6f,BodyType.KINEMATIC);
		counter_weight = new BasicRectangle(40.45f,18f, 0, 0, s, Color.RED, 18f, s, null, 0.8f, 0.8f,BodyType.DYNAMIC);
		elevator.body.setFixedRotation(true); 
		counter_weight.body.setFixedRotation(true);
		//Elevator Object: Note this is huge subject to change
		
		polygons.add(ceilingObject);
		polygons.add(ceilingObjectAttach);
		particles.add(elevatorPulley);
		polygons.add(elevator);
		particles.add(elevatorPulley2);
		polygons.add(counter_weight);
	
		RevoluteJointDef revJointBetweenPulleyNCeiling = new RevoluteJointDef();
		revJointBetweenPulleyNCeiling.bodyA = ceilingObjectAttach.body;
		revJointBetweenPulleyNCeiling.bodyB = elevatorPulley.body;
		revJointBetweenPulleyNCeiling.localAnchorA = new Vec2(0,1.2f);
		revJointBetweenPulleyNCeiling.localAnchorA = new Vec2(0,-0.72f);
		world.createJoint(revJointBetweenPulleyNCeiling);
		
		rollingFriction=.999f;
		double springConstant=100, springDampingConstant=10;
		Double hookesLawTruncation=0.1;
		boolean canGoSlack=false;
		
		connectors.add(new ElasticConnector(elevatorPulley, elevator, 16f, springConstant,springDampingConstant, canGoSlack, Color.WHITE, hookesLawTruncation));
		connectors.add(new ElasticConnector(elevatorPulley2, counter_weight, 0.2f, springConstant,springDampingConstant, canGoSlack, Color.WHITE, hookesLawTruncation));
		
		//Jumping Object done without elastic connector

		jumpObject = new BasicRectangle(32.83f,10f, 0, 0, s, Color.YELLOW, 20f, s, null, s+1f, 1.2f,BodyType.DYNAMIC);
		jumpPlatform = new BasicRectangle(32.83f,9.75f, 0, 0, s, Color.BLUE, 350.2f, s, null, s+1f, 1.2f,BodyType.DYNAMIC);
		polygons.add(jumpObject);
		polygons.add(jumpPlatform);
		
		DistanceJointDef dDef = new DistanceJointDef();
		dDef.bodyA = jumpObject.body;
		dDef.bodyB = jumpPlatform.body;
		dDef.length = 6.2f;
		dDef.collideConnected= false;
		dDef.frequencyHz = 3f;
		dDef.dampingRatio = .5f;
		world.createJoint(dDef);
		
	

		trapFloor_a = new BasicRectangle(8.12f,11.26f, 0, 0, s, Color.BLUE, 75f, s, null, s, 0.5f,BodyType.DYNAMIC);
		trapFloor_b = new BasicRectangle(11.10f,11.26f, 0, 0, s, Color.BLUE, 75f, s, null, s, 0.5f,BodyType.DYNAMIC);
		trapFloor_c = new BasicRectangle(9.6f,11.26f, 0, 0, s, Color.BLUE, 75f, s, null, s+0.47f, 0.5f,BodyType.DYNAMIC);
		trapFoundation1 = new BasicRectangle(8.65f,9.75f, 0, 0, s, Color.RED, 200f, s, null, .95f, s+1f,BodyType.DYNAMIC);
		trapFoundation2 = new BasicRectangle(10.65f,9.75f, 0, 0, s, Color.RED, 200f, s, null, .95f, s+1f,BodyType.DYNAMIC);

		
		//TrapFloor essential codes
		polygons.add(trapFloor_a);
		polygons.add(trapFloor_b);
		polygons.add(trapFloor_c);
		polygons.add(trapFoundation1);
		polygons.add(trapFoundation2);
		
		
		//Magnet Object
		hangObj = new BasicParticle(2.35f,19.62f,0,0f, r,Color.BLACK, 300f, 0.6f,BodyType.STATIC); 
		magnet = new BasicRectangle(2.35f,15f, 0, 0, s, Color.DARK_GRAY, 1.75f, s, null, 1.5f, 1f,BodyType.DYNAMIC);
		magnet.body.setFixedRotation(true);
		particles.add(hangObj);
		polygons.add(magnet);
		
		/*rollingFriction=.999f;
		double springConstant=1, springDampingConstant=1;
		Double hookesLawTruncation=0.01;
		boolean canGoSlack=false;*/
		
		connectors.add(new ElasticConnector(hangObj, magnet, 5.0, springConstant,springDampingConstant, canGoSlack, Color.WHITE, hookesLawTruncation));
		
		
		//Enemy ball launcher cannon script
		enemyBallGenerator = new BasicRectangle(0.82f,6.04f, 0, 0, s, Color.CYAN, 1000f, s, null, 1.f, 1f,BodyType.DYNAMIC); 
		polygons.add(enemyBallGenerator);
		
		
		//Will be used for elevator and trap doors
		
		//Hydrolic System
		hydrolicLift_Big = new BasicRectangle(29.53f,1.9f, 0, 0, s, Color.BLUE, 20f, s , null, s+0.775f, s,BodyType.DYNAMIC);
		hydrolicLift_Big.body.setFixedRotation(true);
		hydrolicLift_Small = new BasicRectangle(34.626f,4.65f, 0, 0, s, Color.BLUE, 25f, s, null, s-0.055f, s,BodyType.DYNAMIC);
		hydrolicLift_Small.body.setFixedRotation(true);
		polygons.add(hydrolicLift_Big);
		polygons.add(hydrolicLift_Small);
		
		barriers = new ArrayList<AnchoredBarrier>();
		
		
		//Will be tried again
				/*WheelJointDef wheelJoint = new WheelJointDef();
				wheelJoint.bodyA = BasicTrunk.body;
				wheelJoint.bodyB = wheelBack.body;
				
				wheelJoint.frequencyHz = 12;
				wheelJoint.dampingRatio = 0.7f;
				wheelJoint.initialize(wheelJoint.bodyA, wheelJoint.bodyB, new Vec2(1.4f,2.5f), 
						new Vec2(wheelJoint.bodyB.getPosition().x,wheelJoint.bodyB.getPosition().y + 2f));*/ 

		
		balls1 = new BasicParticle(10.35f,3.62f,0,0f, r,Color.GREEN, 0.25f, 0.6f,BodyType.DYNAMIC);
		balls2 = new BasicParticle(11.35f,3.62f,0,0f, r,Color.GREEN, 0.25f, 0.6f,BodyType.DYNAMIC);
		balls3 = new BasicParticle(13.35f,3.62f,0,0f, r,Color.GREEN, 0.25f, 0.6f,BodyType.DYNAMIC);
		balls4 = new BasicParticle(15.35f,3.62f,0,0f, r,Color.GREEN, 0.25f, 0.6f,BodyType.DYNAMIC);
		particles.add(balls1);
		particles.add(balls2);
		particles.add(balls3);
		particles.add(balls4);
		
		gameOverTrigger = new BasicRectangle(38.39f,8.75f, 0, 0, s, Color.BLUE, 250f, s, null, s+7.78f, 0.4f,BodyType.STATIC);
		polygons.add(gameOverTrigger);
		
		gameOverTrigger2 = new BasicRectangle(10.39f,8.75f, 0, 0, s, Color.GREEN, 250f, s, null, s+7.78f, 0.4f,BodyType.STATIC);
		polygons.add(gameOverTrigger2);
		
		trigger = new BasicRectangle(13.35f,2.75f, 0, 0, s, Color.RED, 250f, s, null, s+2.735f, 0.1f,BodyType.STATIC);
		polygons.add(trigger);
		
		polygonHealth = new BasicPolygon(42f,7f,0,0, r * 1.2f,Color.GREEN, 1, rollingFriction,6,BodyType.STATIC);
		polygons.add(polygonHealth);
		polygonHealth2 = new BasicPolygon(42f,5f,0,0, r * 1.2f,Color.GREEN, 1, rollingFriction,6,BodyType.STATIC);
		polygons.add(polygonHealth2);
		polygonHealth3 = new BasicPolygon(42f,3f,0,0, r * 1.2f,Color.GREEN, 1, rollingFriction,6,BodyType.STATIC);
		
		polygons.add(polygonHealth3);
		
		switch (layout) {
			case GameLevelOne:{
				// rectangle walls:
				// anticlockwise listing
				// These would be better created as a JBox2D "chain" type object for efficiency and potentially better collision detection at joints. 
				barriers.add(new AnchoredBarrier_StraightLine(0, 22f, 50f, 22f, Color.RED));
				
				
				barriers.add(new AnchoredBarrier_StraightLine(0, 20f, 2f, 20f, Color.RED));
				barriers.add(new AnchoredBarrier_StraightLine(2.001f, 20f, 5f, 20f, Color.RED));
				barriers.add(new AnchoredBarrier_StraightLine(5.001f, 20f, 7f, 19f, Color.RED));
				barriers.add(new AnchoredBarrier_StraightLine(7.001f, 19.001f, 12f, 18f, Color.RED));
				barriers.add(new AnchoredBarrier_StraightLine(12.001f, 18f, 13f, 16.75f, Color.RED));
				barriers.add(new AnchoredBarrier_StraightLine(13.001f, 16.75f, 14f, 15.95f, Color.RED));
				barriers.add(new AnchoredBarrier_StraightLine(14f, 15.95f, 14.35f, 15.85f, Color.RED));
				barriers.add(new AnchoredBarrier_StraightLine(14.351f, 15.85f, 24.5f, 15.85f, Color.RED));
				barriers.add(new AnchoredBarrier_StraightLine(24.5f, 15.85f, 24.85f, 15.95f, Color.RED));
				barriers.add(new AnchoredBarrier_StraightLine(24.851f, 15.951f, 25.65f, 16.45f, Color.RED));
				barriers.add(new AnchoredBarrier_StraightLine(25.651f, 16.451f, 27.65f, 18.15f, Color.RED));
				barriers.add(new AnchoredBarrier_StraightLine(27.651f, 18.15f, 33.65f, 18.15f, Color.RED));
				
				barriers.add(new AnchoredBarrier_StraightLine(4.75f, 8.5f, 49.65f, 8.5f, Color.RED));
				barriers.add(new AnchoredBarrier_StraightLine(31.65f, 8.51f, 31.65f, 16.51f, Color.RED));
				barriers.add(new AnchoredBarrier_StraightLine(33.899f, 8.51f, 33.899f, 16.65f, Color.GREEN));
				barriers.add(new AnchoredBarrier_StraightLine(31.65f, 16.51f, 29.15f, 16.51f, Color.RED));
				barriers.add(new AnchoredBarrier_StraightLine(29.15f, 16.51f, 25.15f, 13.51f, Color.RED));
				barriers.add(new AnchoredBarrier_StraightLine(25.15f, 13.51f, 23f, 12.51f, Color.RED));
				barriers.add(new AnchoredBarrier_StraightLine(23f, 12.51f, 18.75f, 11.51f, Color.RED));
				barriers.add(new AnchoredBarrier_StraightLine( 16.75f, 11.51f,11.75f, 11.51f, Color.RED));
				
				barriers.add(new AnchoredBarrier_StraightLine( 18.75f, 11.51f,11.75f, 11.51f, Color.RED));
				barriers.add(new AnchoredBarrier_StraightLine( 4.75f, 11.51f,7.45f, 11.51f, Color.RED));
				
				barriers.add(new AnchoredBarrier_StraightLine( 0f, 5.51f,4.45f, 5.51f, Color.RED));
				barriers.add(new AnchoredBarrier_StraightLine( 4.45f, 5.51f, 5.45f, 4.76f, Color.RED));
				barriers.add(new AnchoredBarrier_StraightLine( 5.45f, 4.76f, 6.45f,3.76f, Color.RED));
				barriers.add(new AnchoredBarrier_StraightLine( 6.45f,3.76f, 9.45f,2.76f, Color.RED));
				barriers.add(new AnchoredBarrier_StraightLine( 6.45f,3.76f, 9.45f,2.76f, Color.RED));
				
				barriers.add(new AnchoredBarrier_StraightLine( 9.45f,2.76f, 17.45f,2.76f, Color.RED));
				
				barriers.add(new AnchoredBarrier_StraightLine( 17.45f,2.76f,21.45f, 4.26f, Color.RED));
				barriers.add(new AnchoredBarrier_StraightLine( 21.45f, 4.26f,22.45f, 5.26f, Color.RED));
				barriers.add(new AnchoredBarrier_StraightLine( 22.45f, 5.26f,28.45f, 5.26f, Color.RED));

				barriers.add(new AnchoredBarrier_StraightLine( 0f, 1.26f,45.45f, 1.26f, Color.RED));
				
				barriers.add(new AnchoredBarrier_StraightLine( 0f, 1.26f,45.45f, 1.26f, Color.RED));
				barriers.add(new AnchoredBarrier_StraightLine( 0f, 1.26f,45.45f, 1.26f, Color.RED));
				
				//elevatorPulley = new BasicParticle(36.76f,19.62f,0,0f, r,Color.GREEN, 0.76f, 0.6f,BodyType.KINEMATIC);
				//barriers.add(new AnchoredBarrier_StraightLine( 36.76f, 19.62f,40.45f, 19.62f, Color.GREEN));
				//2.35f,15f
				barriers.add(new AnchoredBarrier_StraightLine( 4.76f, 11.5f,4.76f, 14.62f, Color.GREEN));
				barriers.add(new AnchoredBarrier_StraightLine( 4.76f, 14.62f,2.76f, 14.62f, Color.GREEN));
				
				barriers.add(new AnchoredBarrier_StraightLine(30.56f,2.32f,30.56f,5.25f, Color.GREEN));
				barriers.add(new AnchoredBarrier_StraightLine(28.5f, 1.32f,28.5f, 5.25f, Color.GREEN));
				barriers.add(new AnchoredBarrier_StraightLine(30.56f, 2.62f,34f,2.62f, Color.GREEN));
				barriers.add(new AnchoredBarrier_StraightLine(34f, 2.62f,34f,5.25f, Color.GREEN));
				barriers.add(new AnchoredBarrier_StraightLine(35.25f, 1.32f,35.25f,5.25f, Color.GREEN));
				barriers.add(new AnchoredBarrier_StraightLine(30.55f, 5.25f,35.25f,5.25f, Color.BLUE));


				
				barriers.add(new AnchoredBarrier_StraightLine(335, WORLD_HEIGHT/4, 631, WORLD_HEIGHT/4, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT, 0, WORLD_HEIGHT, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(0, WORLD_HEIGHT, 0, 0, Color.WHITE));
			
				break;
			}
		}
	}
	
	
	
	public static void generateBall() {
		boolean alreadyExecuted = false;	
		if(!alreadyExecuted) {
			world.destroyBody(particles.get(0).body);
			particles.clear();
			particles.add(new BasicParticle(0,2f,0,0.0f, 0.2f * 2,Color.BLUE, 90, 0.2f,BodyType.DYNAMIC));
		}
		alreadyExecuted = true;
	}
	
	public void generateCar(BasicPolygon p1,BasicParticle p3, BasicParticle p4) {
		RevoluteJointDef jointDef2 = new RevoluteJointDef();
		jointDef2.bodyA = BasicTrunk.body;
		jointDef2.bodyB = wheelBack.body;
		jointDef2.collideConnected = true;
		
		jointDef2.localAnchorA = new Vec2(-0.5f,-0.3f);
		jointDef2.localAnchorB = new Vec2(0f,0f);
		world.createJoint(jointDef2);
		
		RevoluteJointDef jointDef3 = new RevoluteJointDef();
		jointDef2.bodyA = BasicTrunk.body;
		jointDef2.bodyB = wheelFront.body;
		jointDef2.collideConnected = true;
		
		jointDef2.localAnchorA = new Vec2(0.5f,-0.3f);
		jointDef2.localAnchorB = new Vec2(0f,0f);
		world.createJoint(jointDef2);
	
	}
	
	private void createCushion(List<AnchoredBarrier> barriers, float centrex, float centrey, double orientation, float cushionLength, float cushionDepth) {
		// on entry, we require centrex,centrey to be the centre of the rectangle that contains the cushion.
		Color col=Color.WHITE;
		Vec2 p1=new Vec2(cushionDepth/2, -cushionLength/2-cushionDepth/2);
		Vec2 p2=new Vec2(-cushionDepth/2, -cushionLength/2);
		Vec2 p3=new Vec2(-cushionDepth/2, +cushionLength/2);
		Vec2 p4=new Vec2(cushionDepth/2, cushionLength/2+cushionDepth/2);
		p1=rotateVec(p1,orientation);
		p2=rotateVec(p2,orientation);
		p3=rotateVec(p3,orientation);
		p4=rotateVec(p4,orientation);
		// we are being careful here to list edges in an anticlockwise manner, so that normals point inwards!
		barriers.add(new AnchoredBarrier_StraightLine((float)(centrex+p1.x), (float)(centrey+p1.y), (float)(centrex+p2.x), (float)(centrey+p2.y), col));
		barriers.add(new AnchoredBarrier_StraightLine((float)(centrex+p2.x), (float)(centrey+p2.y), (float)(centrex+p3.x), (float)(centrey+p3.y), col));
		barriers.add(new AnchoredBarrier_StraightLine((float)(centrex+p3.x), (float)(centrey+p3.y), (float)(centrex+p4.x), (float)(centrey+p4.y), col));
		// oops this will have concave corners so will need to fix that some time! 
	}
	private static Vec2 rotateVec(Vec2 v, double angle) {
		// I couldn't find a rotate function in Vec2 so had to write own temporary one here, just for the sake of 
		// cushion rotation for snooker table...
		float cos = (float) Math.cos(angle);
		float sin = (float) Math.sin(angle);
		float nx = v.x * cos - v.y * sin;
		float ny = v.x * sin + v.y * cos;
		return new Vec2(nx,ny);
	}
	public static void main(String[] args) throws Exception {
		final BasicPhysicsEngineUsingBox2D game = new BasicPhysicsEngineUsingBox2D();
		final BasicView view = new BasicView(game);
		JEasyFrame frame = new JEasyFrame(view, "Basic Physics Engine");
		frame.addKeyListener(new BasicKeyListener());
		view.addMouseMotionListener(new BasicMouseListener());
		game.startThread(view);
	}
	private void startThread(final BasicView view) throws InterruptedException {
		final BasicPhysicsEngineUsingBox2D game=this;
		
		while (true) {
			game.update();
			view.repaint();
			Toolkit.getDefaultToolkit().sync();
			try {
				Thread.sleep(DELAY);
			} catch (InterruptedException e) {
			}
		}
	}
	

	public void update() {

		int VELOCITY_ITERATIONS=NUM_EULER_UPDATES_PER_SCREEN_REFRESH;
		int POSITION_ITERATIONS=NUM_EULER_UPDATES_PER_SCREEN_REFRESH;
		
		
		for (BasicParticle p : particles) {
			p.resetTotalForce();// reset to zero at start of time step, so accumulation of forces can begin.
		}
		
		for (BasicPolygon p : polygons) {
			p.resetTotalForce();// reset to zero at start of time step, so accumulation of forces can begin.
		}
		
		for (BasicParticle p:particles) {
			// give the objects an opportunity to add any bespoke forces, e.g. rolling friction
			p.notificationOfNewTimestep();		
		}
		for (BasicPolygon p:polygons) {
			// give the objects an opportunity to add any bespoke forces, e.g. rolling friction
			p.notificationOfNewTimestep();
		}
		
		//Elevator physics but this is also huge subject to chang
		
		connectors.get(0).applyTensionForceToBothParticles();
		connectors.get(1).applyTensionForceToBothParticles();
		//System.out.println("Tension in ropes: "+connectors.get(0).getTension() + connectors.get(1).getTension() + " Tension in weight: " + elevator.body.getMass()*GRAVITY + counter_weight.body.getMass()*GRAVITY);
		
		elevatorTimer += 1;
		/*elevator.body.applyForceToCenter(new Vec2(0,-elevator.body.m_mass * GRAVITY));
		counter_weight.body.applyForceToCenter(new Vec2(0,counter_weight.body.m_mass * GRAVITY));*/
		if(elevatorTimer <= 250) {
			elevator.body.applyForceToCenter(new Vec2(0,-elevator.body.m_mass * GRAVITY * 4));
			counter_weight.body.applyForceToCenter(new Vec2(0,counter_weight.body.m_mass * GRAVITY * 1.2f));
		}else if(elevatorTimer > 251 && elevatorTimer<=750) {
			elevator.body.applyForceToCenter(new Vec2(0,elevator.body.m_mass * GRAVITY * 1.5f));
			counter_weight.body.applyForceToCenter(new Vec2(0,-counter_weight.body.m_mass * GRAVITY * 2.5f));
		}else if(elevatorTimer>=750) {
			elevatorTimer = 0;
		}	
		
		if(BasicKeyListener.isRotateRightKeyPressed()) {
			wheelBack.body.applyTorque(-7.5f);
		}
		
		if(BasicKeyListener.isThrustKeyPressed()) {
			BasicTrunk.body.applyTorque(7.5f);
		}
		if(BasicKeyListener.isDownKeyPressed()) {
			BasicTrunk.body.applyTorque(-7.5f);
		}
		if(BasicKeyListener.isRotateLeftKeyPressed()) {
			wheelBack.body.applyTorque(7.5f);
		}
		

		//Script for magnet
		connectors.get(2).applyTensionForceToBothParticles();
		if(BasicTrunk.body.getPosition().x>=5.201 && BasicTrunk.body.getPosition().x<=6.355 && BasicTrunk.body.getPosition().y >= 11.32f && BasicTrunk.body.getPosition().y <= 16.75f) {
			//System.out.println("HELLI");
			mganet_to_car = new Vec2(magnet.body.getPosition().x - BasicTrunk.body.getPosition().x,magnet.body.getPosition().y - BasicTrunk.body.getPosition().y);
			mganet_to_car = new Vec2(mganet_to_car.x/mganet_to_car.length(),mganet_to_car.y/mganet_to_car.length());
			magnetTimer += 1;
			if(magnetTimer<=120) {
				magnet.body.setLinearVelocity(new Vec2(3,-4));
			}
			if(magnetTimer>=121 && magnetTimer<=200) {
				magnet.body.setLinearVelocity(new Vec2(0,4));
				BasicTrunk.body.applyForceToCenter(new Vec2(0,mganet_to_car.y * 240f));
			}
			if(magnetTimer>=201 && magnetTimer<250) {
				magnet.body.setLinearVelocity(new Vec2(-6,0));
				BasicTrunk.body.setLinearVelocity(new Vec2(-12,0));
				BasicTrunk.body.applyForceToCenter(new Vec2(0,mganet_to_car.y * 60f));
			}
		}
		
	
		hydrolicTimer +=1;
		//System.out.println(hydrolicLift_Big.body.m_mass);
		//System.out.println(hydrolicLift_Small.body.m_mass);
		if(hydrolicLift_Big.body.getPosition().x - BasicTrunk.body.getPosition().x <=0.25f &&
		   hydrolicLift_Big.body.getPosition().x - BasicTrunk.body.getPosition().x >= -2.05f &&
		   BasicTrunk.body.getPosition().y - hydrolicLift_Big.body.getPosition().y >= 0.25f &&
		   BasicTrunk.body.getPosition().y - hydrolicLift_Big.body.getPosition().y <= 1.25f) {
			if(hydrolicTimer>=0 &&hydrolicTimer<=65) {
				hydrolicLift_Big.body.applyForceToCenter(new Vec2(0,(90 -27f*GRAVITY) ));
				///1.2f+0.72f * 1.2f is the area of the larger platform
				///1.2f-0.055f * 1.2f is the area of the smaller
				hydrolicLift_Small.body.applyForceToCenter(new Vec2(0,(90 + (27f*GRAVITY ) *(1.2f+0.72f * 1.2f)/(1.2f-0.055f * 1.2f))));
			
			}else if(hydrolicTimer>65f && hydrolicTimer<100f) {
				hydrolicLift_Big.body.applyForceToCenter(new Vec2(0,90 + (27f*GRAVITY  *(1.2f-0.055f * 1.2f) )/(1.2f+0.72f * 1.2f)));
				hydrolicLift_Small.body.applyForceToCenter(new Vec2(0,90+ -27f*GRAVITY));
			}
			
			else if(hydrolicTimer>100f) {
				hydrolicTimer = 0;
			}
		}else {
			if(hydrolicTimer>=0 &&hydrolicTimer<=65) {
				hydrolicLift_Big.body.applyForceToCenter(new Vec2(0,90 -25f*GRAVITY));
				//hydrolicLift_Small.body.applyForceToCenter(new Vec2(0,90 + (25f*GRAVITY) *(1.2f+0.72f * 1.2f)/(1.2f-0.055f * 1.2f) ));
				hydrolicLift_Small.body.applyForceToCenter(new Vec2(0,90 + (25f*GRAVITY *(1.2f+0.72f * 1.2f)/(1.2f-0.055f * 1.2f)))) ;
			}else if(hydrolicTimer>65f && hydrolicTimer<100f) {
				hydrolicLift_Big.body.applyForceToCenter(new Vec2(0,90 + (25f*GRAVITY *  (1.2f-0.055f * 1.2f) )/(1.2f+0.72f * 1.2f) ));
				hydrolicLift_Small.body.applyForceToCenter(new Vec2(0,90 -25f*GRAVITY));
			}
			
			else if(hydrolicTimer>100f) {
				hydrolicTimer = 0;
			}
		}
		
		
		
		//Make jump object function
		if(BasicKeyListener.isJumpKeyPressed()) {
			jumpTimer += DELTA_T;
			if(jumpTimer < 2.0f) {
			jumpObject.body.applyForceToCenter(new Vec2(0,-500*GRAVITY));
			}else {
			jumpObject.body.applyForceToCenter(new Vec2(0,-700f*GRAVITY));
			}
		}else {
			jumpTimer = 0;
		}
		
		//Enemy ball launcher cannon script
		
	
		
		if(BasicTrunk.body.getPosition().x > trigger.body.getPosition().x-1.2f 
				&& BasicTrunk.body.getPosition().y > trigger.body.getPosition().y
				&& BasicTrunk.body.getPosition().y < trigger.body.getPosition().y+1.2f && AlreadyShotFinished == false) {
			walkingRectTimer +=1;
			
			if(walkingRectTimer < 200f) {
				enemyBallGenerator.body.setAngularVelocity(-2f);
			}
			if(walkingRectTimer >=201f && alreadyDone == false) {
				//0.82f,6.04f
				lastLine = new AnchoredBarrier_StraightLine(0f, 6.64f, 7f, 6.64f, Color.RED);
				barriers.add(lastLine);
				//Ball player has to run away from
				enemyBall = new EnemyBall(2.65f,5.85f,0,0, 0.45f,Color.RED, 0.76f, 0.6f,BodyType.DYNAMIC);
				particles.add(enemyBall);
				System.out.println(enemyBall.body.getPosition());
				alreadyDone = true;
			}
			if(walkingRectTimer >201f && walkingRectTimer < 251f) {
				enemyBallGenerator.body.setLinearVelocity(new Vec2(-2,0));
			}
			if(walkingRectTimer >252f && walkingRectTimer < 255f) {
				enemyBallGenerator.body.setLinearVelocity(new Vec2(35.5f,0));
				AlreadyShot = true;
			}
			if(walkingRectTimer >273f && walkingRectTimer < 283f) {
				enemyBallGenerator.body.setLinearVelocity(new Vec2(-35.5f,0));
			}
			if(walkingRectTimer >294f) {
				AlreadyShotFinished = true;
			}
		
		}
		
	
		if( BasicTrunk.body.getPosition().x - trapFloor_b.body.getPosition().x <= 0f
				&& BasicTrunk.body.getPosition().x - trapFloor_b.body.getPosition().x <= -1.25
				&& BasicTrunk.body.getPosition().y - trapFloor_b.body.getPosition().y >= 0f
				&&BasicTrunk.body.getPosition().y - trapFloor_b.body.getPosition().y <= 2.25f) {
			trapTimer += 1;
			if(trapTimer >=35) {
				world.destroyBody(trapFoundation1.body);
				world.destroyBody(trapFoundation2.body);
				polygons.remove(trapFoundation1);
				polygons.remove(trapFoundation2);
				trapTimer = 0;
			}
		}
		
		if(IsUserHurt2 == false && AlreadyShot == true
				&& enemyBall.body.getPosition().x -wheelFront.body.getPosition().x >=0 
				&& enemyBall.body.getPosition().x -wheelFront.body.getPosition().x <=0.75f
				&& enemyBall.body.getPosition().y -wheelFront.body.getPosition().y >= -0.5f
				&& enemyBall.body.getPosition().y -wheelFront.body.getPosition().y <=0.5f
				) {
			health = health -1;
			
			if(health == 8) {
				polygonHealth.setColor(Color.RED);
			}
			else if (health==7) {
				polygonHealth2.setColor(Color.RED);
			}
			else if (health==6) {
				polygonHealth3.setColor(Color.RED);
				polygons.remove(BasicTrunk);
				world.destroyBody(BasicTrunk.body);
				JOptionPane.showMessageDialog(null, "You lost !!");
				JOptionPane.showMessageDialog(null, "Thank you for playing !!");
			}
			IsUserHurt2 = true;
		}else if(BasicTrunk.body.getPosition().y - gameOverTrigger.body.getPosition().y >1.5f) {
			IsUserHurt2 = false;
		}
		
		if(IsUserHurt == false && BasicTrunk.body.getPosition().y - gameOverTrigger.body.getPosition().y >0f&&
				BasicTrunk.body.getPosition().y - gameOverTrigger.body.getPosition().y <1.25f &&
				BasicTrunk.body.getPosition().x - gameOverTrigger.body.getPosition().x >=-33.65f&&
				BasicTrunk.body.getPosition().x - gameOverTrigger.body.getPosition().x <=5.35f) {
	
			health = health -1;
		
			if(health == 8) {
				polygonHealth.setColor(Color.RED);
				System.out.println("alreadyshot: " + AlreadyShot);
			}
			else if (health==7) {
				polygonHealth2.setColor(Color.RED);
			}
			else if (health==6) {
				polygonHealth3.setColor(Color.RED);
				polygons.remove(BasicTrunk);
				world.destroyBody(BasicTrunk.body);
				JOptionPane.showMessageDialog(null, "You lost !!");
				JOptionPane.showMessageDialog(null, "Thank you for playing !!");
			}
			IsUserHurt = true;
	

		}else if(BasicTrunk.body.getPosition().y - gameOverTrigger.body.getPosition().y >2.25f) {
			IsUserHurt = false;
		}
		
		if(BasicTrunk.body.getPosition().x >= 39f &&
				BasicTrunk.body.getPosition().x < 42f	&&
				BasicTrunk.body.getPosition().y >= 0f &&
				BasicTrunk.body.getPosition().y < 4f && isGameEnded == false) {
			polygonHealth.setColor(Color.BLUE);
			polygonHealth2.setColor(Color.BLUE);
			polygonHealth3.setColor(Color.BLUE);
			JOptionPane.showMessageDialog(null, "WINNER !!!");
			isGameEnded = true;
			JOptionPane.showMessageDialog(null, "Thank you for playing !!");
		}
	
			
		world.step(DELTA_T, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
	}
	
}
