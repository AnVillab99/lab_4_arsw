/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class HostBlackListsValidator {

	private static final int BLACK_LIST_ALARM_COUNT = 5;
	private LinkedList<Integer> blackListOcurrences;
	private ListThread[] threads;

	/**
	 * Check the given host's IP address in all the available black lists, and
	 * report it as NOT Trustworthy when such IP was reported in at least
	 * BLACK_LIST_ALARM_COUNT lists, or as Trustworthy in any other case. The search
	 * is not exhaustive: When the number of occurrences is equal to
	 * BLACK_LIST_ALARM_COUNT, the search is finished, the host reported as NOT
	 * Trustworthy, and the list of the five blacklists returned.
	 * 
	 * @param ipaddress
	 *            suspicious host's IP address.
	 * @return Blacklists numbers where the given host's IP address was found.
	 */
	public List<Integer> checkHost(String ipaddress, int n) {

		blackListOcurrences = new LinkedList<>();

		int ocurrencesCount = 0;

		HostBlacklistsDataSourceFacade skds = HostBlacklistsDataSourceFacade.getInstance();

		int checkedListsCount = 0;
		threads = new ListThread[n];
		int A = n;
		int lo = (int) skds.getRegisteredServersCount();
		n = lo / n;

		// System.out.println(threads.length);

		int i = 0;
		for (int N = 0; N < A; N++) {

			ListThread t = null;
			// System.out.println("N: "+N+" i:"+i+" lo:"+lo);
			if (i + n < lo) {
				t = new ListThread(ipaddress, i, i + n, this,i);
				// System.out.println(" hilo i:"+i+" j:"+(i+n));
			} else {
				t = new ListThread(ipaddress, i, lo, this,i);
				// System.out.println(" hilo final i:"+i+" j:"+(lo));
			}

			threads[N] = t;
			t.start();

			i += n;

		}

		synchronized (this) {

			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("halt de si mismo");
		ocurrencesCount = halt();

		System.out.println("Numero de ocurrencias: " + ocurrencesCount);

		if (ocurrencesCount >= BLACK_LIST_ALARM_COUNT) {
			skds.reportAsNotTrustworthy(ipaddress);
		} else {
			skds.reportAsTrustworthy(ipaddress);
		}
		LOG.log(Level.INFO, "Checked Black Lists:{0} of {1}",
				new Object[] { ocurrencesCount, skds.getRegisteredServersCount() });

		return blackListOcurrences;
	}

	
	
	
	public int getNumber() {
		return BLACK_LIST_ALARM_COUNT;
	}

	public int halt() {
		
		int ocurrencesCount = 0;
		for (ListThread l : threads) {

			LinkedList<Integer> listaThread = l.getList();

			ocurrencesCount += listaThread.size();
			blackListOcurrences.addAll(listaThread);
			synchronized (this) {
				this.notify();
			}
			l.stopp();
		}
		return ocurrencesCount;

	}

	private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName());

}
