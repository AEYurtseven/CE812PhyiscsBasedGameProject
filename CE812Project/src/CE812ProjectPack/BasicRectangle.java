package CE812ProjectPack;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Path2D.Float;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;






public class BasicRectangle extends BasicPolygon{

	public static final double MAGNITUDE_OF_ENGINE_THRUST_FORCE = 1000000;
	private Vec2 totalForceThisTimeStep;
	
	public BasicRectangle(float sx, float sy, float vx, float vy, float radius, Color col, float mass,
			float rollingFriction, Float polygonPath, float width, float height, BodyType type) {
		super(sx, sy, vx, vy, radius, col, mass, rollingFriction, mkRegularRectangle(width, height), 4, type);
		// TODO Auto-generated constructor stub
		this.totalForceThisTimeStep=new Vec2();
	}

	public static Path2D.Float mkRegularRectangle(float width, float height) {
		/*Path2D.Float p = new Path2D.Float();
		p.moveTo(radius, 0);
		for (int i = 0; i < n; i++) {
			float x = (float) (Math.cos((Math.PI * 2 * i) / n) * radius);
			float y = (float) (Math.sin((Math.PI * 2 * i) / n) * radius);
			p.lineTo(x, y);
		}
		p.closePath();
		return p;*/
		
		
		Path2D.Float p = new Path2D.Float();
		p.moveTo(width/2,height/2);
		p.lineTo(-width/2, height/2);
		p.lineTo(-width/2, -height/2);
		p.lineTo(width/2, -height/2);
		p.lineTo(width/2,height/2);
		p.closePath();
		return p;
	}


	
}
