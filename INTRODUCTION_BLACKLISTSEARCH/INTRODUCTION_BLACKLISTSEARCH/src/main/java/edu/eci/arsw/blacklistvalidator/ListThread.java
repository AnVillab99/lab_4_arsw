package edu.eci.arsw.blacklistvalidator;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;

/**
 *
 * @author hcadavid
 */
public class ListThread extends Thread {
	private int I;
	private int s;
	private String ip;
	private LinkedList<Integer> blackListOcurrences;
	private HostBlackListsValidator hb;
	int name;

	public ListThread(String ipaddress, int inf, int sup, HostBlackListsValidator hbv, int n) {
		I = inf;
		s = sup;
		ip = ipaddress;
		hb = hbv;
		name = n;
	}

	public void run() {

		blackListOcurrences = new LinkedList<>();

		HostBlacklistsDataSourceFacade skds = HostBlacklistsDataSourceFacade.getInstance();

		for (int i = I; i <= s; i++) {
			if (skds.isInBlackListServer(i, ip)) {

				blackListOcurrences.add(i);

			}
			if (blackListOcurrences.size() == hb.getNumber()) {
				System.out.println("halt hecho por " + name);
				hb.halt();

				System.out.println("ded hecho por " + name);
				System.out.println("finalizado");
				break;

			}
		}
		stopp();

	}

	public LinkedList<Integer> getList() {
		return blackListOcurrences;
	}

	public int ret() {
		return s;
	}

	public void stopp() {
		Thread.currentThread().interrupt();
		return;
	}

}
