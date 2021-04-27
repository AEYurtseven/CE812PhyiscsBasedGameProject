package CE812ProjectPack;

import java.awt.Color;
import java.awt.Graphics2D;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import CE812ProjectPack.BasicKeyListener;
import CE812ProjectPack.BasicPhysicsEngineUsingBox2D;



public class BasicParticle  {
	/* Author: Michael Fairbank
	 * Creation Date: 2016-02-05 (JBox2d version)
	 * Significant changes applied:
	 */
	public int SCREEN_RADIUS;

	float rollingFriction;

	float mass;
	public  Color col;
	Body body;
	CircleShape circleShape;
	private Vec2 totalForceThisTimeStep;

	public BasicParticle(float sx, float sy, float vx, float vy, float radius, Color col, float mass, float rollingFriction, BodyType type) {
		World w=BasicPhysicsEngineUsingBox2D.world; // a Box2D object
		BodyDef bodyDef = new BodyDef();  // a Box2D object
		bodyDef.type = type; // this says the physics engine is to move it automatically
		bodyDef.position.set(sx, sy);
		bodyDef.linearVelocity.set(vx, vy);
		this.body = w.createBody(bodyDef);
		circleShape = new CircleShape();// This class is from Box2D
		circleShape.m_radius = radius;
		FixtureDef fixtureDef = new FixtureDef();// This class is from Box2D
		fixtureDef.shape = circleShape;
		fixtureDef.density = (float) (mass/(Math.PI*radius*radius));
		fixtureDef.friction = 750f;// this is surface friction;
		fixtureDef.restitution = 0f;
		body.createFixture(fixtureDef);
		this.rollingFriction=rollingFriction;
		this.mass=mass;
		this.SCREEN_RADIUS=(int)Math.max(BasicPhysicsEngineUsingBox2D.convertWorldLengthToScreenLength(radius),1);
		this.col=col;
	}
	
	public void draw(Graphics2D g) {
		int x = BasicPhysicsEngineUsingBox2D.convertWorldXtoScreenX(body.getPosition().x);
		int y = BasicPhysicsEngineUsingBox2D.convertWorldYtoScreenY(body.getPosition().y);
		g.setColor(col);
		g.fillOval(x - SCREEN_RADIUS, y - SCREEN_RADIUS, 2 * SCREEN_RADIUS, 2 * SCREEN_RADIUS);
		
		g.setColor(Color.black);
		g.drawLine(x, y,(int)( x + (Math.cos(-body.getAngle()) * SCREEN_RADIUS)), (int)(y + (Math.sin(-body.getAngle()) * SCREEN_RADIUS)));
	}


	public void notificationOfNewTimestep() {
		if (rollingFriction>0) {
			Vec2 rollingFrictionForce=new Vec2(body.getLinearVelocity());
			rollingFrictionForce=rollingFrictionForce.mul(-rollingFriction*mass);
			body.applyForceToCenter(rollingFrictionForce);
		}
	}
	
	public void update() {
		if(BasicKeyListener.isRotateRightKeyPressed()) {
				body.applyTorque(25f);
		}
		if(BasicKeyListener.isRotateLeftKeyPressed()) {
				body.applyTorque(-25f);

		}
	}

	public void applyForceToParticle(Vec2 force) {
		// TODO Auto-generated method stub
		totalForceThisTimeStep=totalForceThisTimeStep.add(force);
	}
	
	public void resetTotalForce() {
		totalForceThisTimeStep=new Vec2(0,0);
	}
	
}
