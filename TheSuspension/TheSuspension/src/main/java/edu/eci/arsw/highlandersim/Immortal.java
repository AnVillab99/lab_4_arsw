package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;

public class Immortal extends Thread {

	private ImmortalUpdateReportCallback updateCallback = null;

	private int health;

	private int defaultDamageValue;

	private final List<Immortal> immortalsPopulation;

	private final String name;

	private final Random r = new Random(System.currentTimeMillis());

	private boolean alive = true;
	private boolean stop = false;

	public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue,
			ImmortalUpdateReportCallback ucb) {
		super(name);
		this.updateCallback = ucb;
		this.name = name;
		this.immortalsPopulation = immortalsPopulation;
		this.health = health;
		this.defaultDamageValue = defaultDamageValue;
	}

	public void run() {
		while (health > 0) {

			if (stop) {
				try {
					synchronized (this) {
						wait();
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Immortal im;

			int myIndex = immortalsPopulation.indexOf(this);

			int nextFighterIndex = r.nextInt(immortalsPopulation.size());

			// avoid self-fight
			if (nextFighterIndex == myIndex) {
				nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
			}

			im = immortalsPopulation.get(nextFighterIndex);

			this.fight(im);

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	public void fight(Immortal i2) {
		if (health > 0 && i2.getHealth() > 0) {
			int indexY = immortalsPopulation.indexOf(this);
			int indexO = immortalsPopulation.indexOf(i2);
			Immortal b1;
			Immortal b2;
			if (indexY < indexO) {
				b1 = this;
				b2 = i2;
			} else {
				b1 = i2;
				b2 = this;
			}

			synchronized (b1) {
				synchronized (b2) {
					if (i2.getHealth() > 0) {
						i2.changeHealth(i2.getHealth() - defaultDamageValue);
						changeHealth(health + defaultDamageValue);


						updateCallback.processReport("Fight: " + this + " vs " + i2 + "\n");
					}
				}
			}
		}

	}

	public void changeHealth(int v) {
		health = v;

		if (health <= 0) {
			alive = false;

		}
	}

	public int getHealth() {
		return health;
	}

	@Override
	public String toString() {

		return name + "[" + health + "]";
	}

	public void halt() {
		stop = true;
	}

	public void awake() {
		stop = false;

		synchronized (this) {
			notify();
		}
	}

}
