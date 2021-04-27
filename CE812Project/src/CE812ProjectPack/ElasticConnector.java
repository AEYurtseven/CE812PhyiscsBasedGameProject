package CE812ProjectPack;

import java.awt.Color;
import java.awt.Graphics2D;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.joints.DistanceJointDef;





public class ElasticConnector {
	/* Author: Michael Fairbank
	 * Creation Date: 2016-01-28
	 * Significant changes applied:
	 */
	private final BasicParticle particle1;
	private final double naturalLength;
	private final double springConstant;
	private final double motionDampingConstant;
	private final boolean canGoSlack;
	private final Color col;
	private final Double hookesLawTruncation;
	private BasicRectangle particle2;
	public DistanceJointDef jd;
	public ElasticConnector(BasicParticle p1, BasicRectangle p2, double naturalLength, double springConstant, double motionDampingConstant, 
			boolean canGoSlack, Color col, Double hookesLawTruncation) {
		this.particle1 = p1;
		this.particle2 = p2;
		this.naturalLength = naturalLength;
		this.springConstant = springConstant;
		this.motionDampingConstant=motionDampingConstant;
		this.canGoSlack = canGoSlack;
		this.hookesLawTruncation=hookesLawTruncation;
		this.col=col;
		// This is a hack to use the closest box2D joint type, which is a distance joint.
		// However this is inextensible. So if we want elasticity, then we have to apply Hookes law 
		// (and damping) ourselves through the application of manually calculated forces to each connected particle
		// - lecture notes show how to do this.  
		// Due to this current limitation, many of the constructor's arguments are not used, and this connector
		// would have best been called "InelasticConnector", but it was left with the old title to retain 
		// backwards compatibility and hopefully make Box2d easier to understand.
		
		/*jd = new DistanceJointDef(); // This is an inextensible string
		jd.initialize(p1.body, p2.body, p1.body.getPosition(), p2.body.getPosition());
		BasicPhysicsEngineUsingBox2D.world.createJoint(jd);// add the distanceJoint to the world*/
	}
	
	
	
	public double rateOfChangeOfExtension() {
		//Vect2D v12=Vect2D.minus(particle2.getPos(), particle1.getPos()); // goes from p1 to p2
		Vec2 v12 = new Vec2(particle2.body.getPosition().x - particle1.body.getPosition().x,particle2.body.getPosition().y - particle1.body.getPosition().y);
		v12= new Vec2(v12.x/v12.length(),v12.y/v12.length()); // make it a unit vector.
		//Vec2 relativeVeloicty=Vec2.minus(particle2.getVel(), particle1.getVel()); // goes from p1 to p2
		//Vec2 relativeVeloicty= new Vec2(particle2.body.getLinearVelocity().x - particle1.body.getLinearVelocity().x,particle2.body.getLinearVelocity().y - particle1.body.getLinearVelocity().y);
		Vec2 relativeVeloicty= new Vec2(particle2.body.getPosition().x - particle1.body.getPosition().x,particle2.body.getPosition().y - particle1.body.getPosition().y);
		//return relativeVeloicty.scalarProduct(v12);// if this is positive then it means the 
		return v12.x * relativeVeloicty.x + v12.y * relativeVeloicty.y;
		// connector is getting longer
	}
	
	public double calculateTension() {
		// implementation of truncated hooke's law
		//double dist=Vec2.(particle1.body.getPosition(), particle2.body.getPosition()).mag();
		double dist = new Vec2(particle1.body.getPosition().x - particle2.body.getPosition().x,particle1.body.getPosition().y - particle2.body.getPosition().y).length();
		if (dist<naturalLength && canGoSlack) return 0;
		
		double extensionRatio = (dist-naturalLength)/naturalLength;
		Double truncationLimit=this.hookesLawTruncation;// this stops Hooke's law giving too high a force which might cause instability in the numerical integrator
		if (truncationLimit!=null && extensionRatio>truncationLimit) 
			extensionRatio=truncationLimit;
		if (truncationLimit!=null && extensionRatio<-truncationLimit) 
			extensionRatio=-truncationLimit;
		double tensionDueToHookesLaw = extensionRatio*springConstant;
		double tensionDueToMotionDamping = motionDampingConstant*rateOfChangeOfExtension();
		return tensionDueToHookesLaw+tensionDueToMotionDamping;
	}
		
	public void applyTensionForceToBothParticles() {
		float tension=(float)calculateTension();
		Vec2 p12=new Vec2(particle2.body.getPosition().x - particle1.body.getPosition().x,particle2.body.getPosition().y - particle1.body.getPosition().y); // goes from p1 to p2
		p12=new Vec2(p12.x / p12.length(),p12.y / p12.length()); // make it a unit vector.
		//Vec2 forceOnP1=p12.mult(tension);
		Vec2 forceOnP1=new Vec2(p12.x*tension, p12.y*tension);
		particle1.body.applyForceToCenter(forceOnP1);

		//Vec2 forceOnP2=p12.mult(-tension);// tension on second particle acts in opposite direction (an example of Newton's 3rd Law)
		Vec2 forceOnP2=new Vec2(p12.x * -tension, p12.y * -tension);
		//particle2.applyForceToParticle(forceOnP2);
		particle2.body.applyForceToCenter(new Vec2(forceOnP2.x,forceOnP2.y));
		//particle2.body.applyForce(new Vec2(forceOnP2.x/3,forceOnP2.y/3), new Vec2(particle2.body.getPosition().x + 1.8f,particle2.body.getPosition().y));
		//particle2.body.applyForce(new Vec2(forceOnP2.x/3,forceOnP2.y/3), new Vec2(particle2.body.getPosition().x - 1.8f,particle2.body.getPosition().y));
	}
	
	public float getTension() {
		float tension=(float)calculateTension();
		return tension;
	}
	
	public BasicParticle getParticle() {
		return particle1;
	}
	public BasicPolygon getPolygon() {
		return particle2;
	}


	public void draw(Graphics2D g) {
		int x1 = BasicPhysicsEngineUsingBox2D.convertWorldXtoScreenX(particle1.body.getPosition().x);
		int y1 = BasicPhysicsEngineUsingBox2D.convertWorldYtoScreenY(particle1.body.getPosition().y);
		int x2 = BasicPhysicsEngineUsingBox2D.convertWorldXtoScreenX(particle2.body.getPosition().x);
		int y2 = BasicPhysicsEngineUsingBox2D.convertWorldYtoScreenY(particle2.body.getPosition().y);
		g.setColor(col);
		g.drawLine(x1, y1, x2, y2);
		/*int x1_1 = BasicPhysicsEngineUsingBox2D.convertWorldXtoScreenX(particle1.body.getPosition().x);
		int y1_1 = BasicPhysicsEngineUsingBox2D.convertWorldYtoScreenY(particle1.body.getPosition().y);
		int x2_1 = BasicPhysicsEngineUsingBox2D.convertWorldXtoScreenX(particle2.body.getPosition().x+1.8f);
		int y2_1 = BasicPhysicsEngineUsingBox2D.convertWorldYtoScreenY(particle2.body.getPosition().y);
		g.setColor(col);
		g.drawLine(x1_1, y1_1, x2_1,y2_1);
		
		int x1_2 = BasicPhysicsEngineUsingBox2D.convertWorldXtoScreenX(particle1.body.getPosition().x);
		int y1_2 = BasicPhysicsEngineUsingBox2D.convertWorldYtoScreenY(particle1.body.getPosition().y);
		int x2_2 = BasicPhysicsEngineUsingBox2D.convertWorldXtoScreenX(particle2.body.getPosition().x-1.8f);
		int y2_2 = BasicPhysicsEngineUsingBox2D.convertWorldYtoScreenY(particle2.body.getPosition().y);
		g.setColor(col);
		g.drawLine(x1_2, y1_2, x2_2,y2_2);*/
	}

}
