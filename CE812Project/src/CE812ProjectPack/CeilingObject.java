package CE812ProjectPack;

import java.awt.Color;
import java.awt.geom.Path2D.Float;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

public class CeilingObject extends BasicRectangle {

	public CeilingObject(float sx, float sy, float vx, float vy, float radius, Color col, float mass,
			float rollingFriction, Float polygonPath, float width, float height,BodyType type) {
		super(sx, sy, vx, vy, radius, col, mass, rollingFriction, polygonPath, width, height, type);
	}
	


}
