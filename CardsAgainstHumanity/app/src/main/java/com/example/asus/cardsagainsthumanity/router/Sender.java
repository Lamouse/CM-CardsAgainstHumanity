package com.example.asus.cardsagainsthumanity.router;

import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.example.asus.cardsagainsthumanity.config.Configuration;
import com.example.asus.cardsagainsthumanity.router.tcp.TcpSender;

/**
 * Responsible for sending all packets that appear in the queue
 * 
 * @author Matthew Vertescher
 */
public class Sender implements Runnable {

	/**
	 * Queue for packets to send
	 */
	private static ConcurrentLinkedQueue<Packet> ccl;

	/**
	 * Constructor
	 */
	public Sender() {
		if (ccl == null)
			ccl = new ConcurrentLinkedQueue<Packet>();
	}

	/**
	 * Enqueue a packet to send
	 * @param p
	 * @return
	 */
	public static boolean queuePacket(Packet p) {
		if (ccl == null)
			ccl = new ConcurrentLinkedQueue<Packet>();
		return ccl.add(p);
	}

	@Override
	public void run() {
		TcpSender packetSender = new TcpSender();

		while (true) {
			//Sleep to give up CPU cycles
			while (ccl.isEmpty()) {
				try {
					Thread.sleep(125);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			Log.wtf("PacketToSend", "Now");
			Packet p = ccl.remove();
			String ip = MeshNetworkManager.getIPForClient(p.getMac());
			packetSender.sendPacket(ip, Configuration.RECEIVE_PORT, p);

		}
	}

}
