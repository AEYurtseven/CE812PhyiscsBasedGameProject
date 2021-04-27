package CE812ProjectPack;

import java.awt.Color;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

public class Wheel extends BasicParticle {

	public Wheel(float sx, float sy, float vx, float vy, float radius, Color col, float mass, float rollingFriction,BodyType type) {
		super(sx, sy, vx, vy, radius, col, mass, rollingFriction,type);
	}

}
