package com.example.asus.cardsagainsthumanity.router;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

// import com.ecse414.android.echo.MessageActivity;
// import com.ecse414.android.echo.WiFiDirectActivity;
import com.example.asus.cardsagainsthumanity.ManagerInterface;
import com.example.asus.cardsagainsthumanity.RoomActivity;
// import com.example.asus.cardsagainsthumanity.RoomPeersList;
import com.example.asus.cardsagainsthumanity.config.Configuration;
import com.example.asus.cardsagainsthumanity.game.PlayerPick;
import com.example.asus.cardsagainsthumanity.router.tcp.TcpReciever;
// import com.ecse414.android.echo.ui.DeviceDetailFragment;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * The main receiver class
 * @author Matthew Vertescher
 * @author Peter Henderson
 */
public class Receiver implements Runnable {

	/**
	 * Flag if the receiver has been running to prevent overzealous thread spawning
	 */
	public static boolean running = false;
	
	/**
	 * A ref to the activity
	 */
	static Activity activity;

	/**
	 * Constructor with activity
	 * @param a
	 */
	public Receiver(Activity a) {
		Receiver.activity = a;
		running = true;
	}

	/** 
	 * Main thread runner
	 */
	public void run() {
		/*
		 * A queue for received packets
		 */
		ConcurrentLinkedQueue<Packet> packetQueue = new ConcurrentLinkedQueue<Packet>();

		Log.wtf("RECEIVER", "OPENED");
		/*
		 * Receiver thread 
		 */
		new Thread(new TcpReciever(Configuration.RECEIVE_PORT, packetQueue)).start();

		Packet p;

		/*
		 * Keep going through packets
		 */
		while (true)
		{
			/*
			 * If the queue is empty, sleep to give up CPU cycles
			 */
			while (packetQueue.isEmpty())
			{
				try
				{
					Thread.sleep(125);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			/*
			 * Pop a packet off the queue
			 */
			p = packetQueue.remove();

			Log.wtf("PACKET", "RECEIVED");
			/*
			 * If it's a hello, this is special and need to go through the connection mechanism for any node receiving this
			 */
			if (p.getType().equals(Packet.TYPE.HELLO))
			{
				// Put it in your routing table
				for (AllEncompasingP2PClient c : MeshNetworkManager.routingTable.values())
				{
					if (c.getMac().equals(MeshNetworkManager.getSelf().getMac()) || c.getMac().equals(p.getSenderMac()))
					{
						continue;
					}
					Packet update = new Packet(Packet.TYPE.UPDATE, Packet.getMacAsBytes(p.getSenderMac()), c.getMac(),
							MeshNetworkManager.getSelf().getMac());
					Sender.queuePacket(update);
				}

				MeshNetworkManager.routingTable.put(p.getSenderMac(),
						new AllEncompasingP2PClient(p.getSenderMac(), p.getSenderIP(), p.getSenderMac(),
								MeshNetworkManager.getSelf().getMac()));

				// Send routing table back as HELLO_ACK
				byte[] rtable = MeshNetworkManager.serializeRoutingTable();

				Packet ack = new Packet(Packet.TYPE.HELLO_ACK, rtable, p.getSenderMac(), MeshNetworkManager.getSelf()
						.getMac());
				Sender.queuePacket(ack);
                MeshNetworkManager.getSelf().setIsCzar(true);
				somebodyJoined(p.getSenderMac());
				updatePeerList();
			}
            else if (p.getType().equals(Packet.TYPE.BYE))
            {
				MeshNetworkManager.routingTable.remove(p.getSenderMac());
				Receiver.somebodyLeft(p.getSenderMac());
				Receiver.updatePeerList();
			}
            else
            {
				// If you're the intended target for a non hello message
				if (p.getMac().equals(MeshNetworkManager.getSelf().getMac()))
                {
					//if we get a hello ack populate the table
					if (p.getType().equals(Packet.TYPE.HELLO_ACK))
                    {
						MeshNetworkManager.deserializeRoutingTableAndAdd(p.getData());
						MeshNetworkManager.getSelf().setGroupOwnerMac(p.getSenderMac());
                        MeshNetworkManager.getSelf().setIsCzar(false);
						somebodyJoined(p.getSenderMac());
						updatePeerList();
					}
                    else if (p.getType().equals(Packet.TYPE.UPDATE))
                    {
						//if it's an update, add to the table
						String emb_mac = Packet.getMacBytesAsString(p.getData(), 0);
						MeshNetworkManager.routingTable.put(emb_mac,
								new AllEncompasingP2PClient(emb_mac, p.getSenderIP(), p.getMac(), MeshNetworkManager
										.getSelf().getMac()));

						final String message = emb_mac + " joined the conversation";
						final String name = p.getSenderMac();
						/*activity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								if (activity.isVisible) {
									Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
								} else {
									MessageActivity.addMessage(name, message);
								}
							}
						});*/
						updatePeerList();
					}
                    else if (p.getType().equals(Packet.TYPE.MESSAGE))
                    {
						//If it's a message display the message and update the table if they're not there 
						// for whatever reason
						final String message = p.getSenderMac() + " says:\n" + new String(p.getData());
						final String msg = new String(p.getData());
						final String name = p.getSenderMac();

						if (!MeshNetworkManager.routingTable.contains(p.getSenderMac()))
                        {
							/*
							 * Update your routing table if for some reason this
							 * guy isn't in it
							 */
							MeshNetworkManager.routingTable.put(p.getSenderMac(),
									new AllEncompasingP2PClient(p.getSenderMac(), p.getSenderIP(), p.getSenderMac(),
											MeshNetworkManager.getSelf().getGroupOwnerMac()));
						}
						/*activity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								if (activity.isVisible) {
									Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
								} else {
									MessageActivity.addMessage(name, msg);
								}
							}
						});*/
						updatePeerList();
					}
                    else if (p.getType().equals(Packet.TYPE.CZAR) && ("RoomActivity".equals(((ManagerInterface) activity).getActivityName()) ||
                            ("FinalRound".equals(((ManagerInterface) activity).getActivityName()))))
                    {
                        String data = new String(p.getData());
                        List<String> args = Arrays.asList(data.split(","));
                        if (MeshNetworkManager.getSelf().getMac().equals(data))  // Check if user was delegated to be CZAR
                        {
                            MeshNetworkManager.getSelf().setIsCzar(true);
                        }
                        else
                        {
                            MeshNetworkManager.getSelf().setIsCzar(false);
                            Intent intent = new Intent(activity, PlayerPick.class);
                            intent.putExtra("Question", args.get(1));
                            activity.startActivity(intent);
                        }
                    }
                    else if (p.getType().equals(Packet.TYPE.WHITECARD))
                    {

                    }
                    else if (p.getType().equals(Packet.TYPE.BLACKCARD))
                    {

                    }
                    else if (p.getType().equals(Packet.TYPE.FINISH))
                    {

                    }
                    else if (p.getType().equals(Packet.TYPE.WINNER))
                    {

                    }
				}
                else
                {
					// otherwise forward it if you're not the recipient
					int ttl = p.getTtl();
					// Have a ttl so that they don't bounce around forever
					ttl--;
					if (ttl > 0)
                    {
						Sender.queuePacket(p);
						p.setTtl(ttl);
					}
				}
			}

		}
	}

	/**
	 * GUI thread to send somebody joined notification
	 * @param smac
	 */
	public static void somebodyJoined(String smac) {

		final String message;
		final String msg;
		message = msg = smac + " has joined.";
		final String name = smac;
		/*activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (activity.isVisible) {
					Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
				} else {
					MessageActivity.addMessage(name, msg);
				}
			}
		});*/
	}

	/**
	 * Somebody left notification on the UI thread
	 * @param smac
	 */
	public static void somebodyLeft(String smac) {

		final String message;
		final String msg;
		message = msg = smac + " has left.";
		final String name = smac;
		/*activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (activity.isVisible) {
					Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
				} else {
					MessageActivity.addMessage(name, msg);
				}
			}
		});*/
	}

	/**
	 * Update the list of peers on the front page
	 */
	public static void updatePeerList() {
		if (activity == null)
			return;

		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				((RoomActivity) activity).updatePeersList();
			}
		});
	}

	public static void setActivity(Activity activity) {
		Receiver.activity = activity;
	}
}