package CE812ProjectPack;

import java.awt.Color;

import org.jbox2d.dynamics.BodyType;

public class EnemyBall extends BasicParticle{

	public EnemyBall(float sx, float sy, float vx, float vy, float radius, Color col, float mass,
			float rollingFriction, BodyType type) {
		super(sx, sy, vx, vy, radius, col, mass, rollingFriction, type);
		// TODO Auto-generated constructor stub
	}

}
