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
		while (true) {
			if (alive && health > 0) {
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
	}

	public void fight(Immortal i2) {
		if (alive && health > 0) {
			int myIndex = immortalsPopulation.indexOf(this);
			int hisIndex = immortalsPopulation.indexOf(i2);
			Immortal b1;
			Immortal b2;
			if (myIndex < hisIndex) {// Si el índice de este inmortal es menor al del índice del inmortal con el que
										// va a pelear, entonces este inmortal bloquea al otro.
				b1 = this;
				b2 = i2;
			} else {
				b1 = i2;
				b2 = this;
			}
			synchronized (b1) {
				if (i2.getHealth() > 0) {
					synchronized (b2) {

						i2.changeHealth(i2.getHealth() - defaultDamageValue);
						this.health += defaultDamageValue;
						updateCallback.processReport("Fight: " + this + " vs " + i2 + "\n");
					}
				} else {

					int nextFighterIndex = immortalsPopulation.indexOf(i2);
					while (immortalsPopulation.indexOf(i2) == nextFighterIndex || myIndex == nextFighterIndex) {
						nextFighterIndex = r.nextInt(immortalsPopulation.size());
					}

					i2 = immortalsPopulation.get(nextFighterIndex);
					b2 = i2;
					synchronized (b2) {
						i2.changeHealth(i2.getHealth() - defaultDamageValue);
						this.health += defaultDamageValue;
						updateCallback.processReport("Fight: " + this + " vs " + i2 + "\n");
						updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
					}
				}
			}

		}
	}

	public synchronized void changeHealth(int v) {
		health = v;

		if (health <= 0) {
			alive = false;
			System.out.println(this + " ded");
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
		System.out.println("awake" + this);
		synchronized (this) {
			notify();
		}
	}

}
