package aero.geosystems.ntrip.utils;

import aero.geosystems.decoder.nmea.NmeaGga;
import aero.geosystems.gnss.Datetime;
import aero.geosystems.ntrip.NtripClientRequest;
import aero.geosystems.ntrip.NtripServerRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;

/**
 * Created by aimozg on 08.03.2016.
 * Confidential.
 */
public class NtripApp {
	private String[] args;
	private String host;
	private int port;
	private String mount;
	private String authstr;
	private Socket socket;
	private NmeaGga nmea;

	NtripApp(String[] args) {
		this.args = args;
	}

	void help() {
		System.out.println("java -jar NtripApp.jar (client|server) HOST PORT MOUNT USER[:password] [LAT,LNG]");
	}
	void openClient() throws IOException {
		NtripClientRequest request = new NtripClientRequest()
				.authentication(new NtripClientRequest.BasicAuthorization(authstr))
				.mountpoint(mount);
		socket = new Socket(host,port);
		String s = request.request();
		System.out.println(s);
		socket.getOutputStream().write(s.getBytes("UTF-8"));
	}
	void openServer() throws IOException {
		NtripServerRequest request = new NtripServerRequest()
				.password(authstr)
				.mountpoint(mount);
		socket = new Socket(host,port);
		String s = request.request();
		System.out.println(s);
		socket.getOutputStream().write(s.getBytes("UTF-8"));
	}
	private void run() throws IOException {
		if (args.length==0) {
			help();
		} else switch (args[0]){
			case "client":
				host = args[1];
				port = Integer.parseInt(args[2]);
				mount = args[3];
				authstr = args[4];
				if (args.length>=6) {
					String[] ll = args[5].split(",");
					nmea = new NmeaGga();
					nmea.utcOfPosition.set(new Datetime());
					nmea.lat.set(Double.parseDouble(ll[0]));
					nmea.lon.set(Double.parseDouble(ll[1]));
					nmea.antennaAltitude.set(0.0);
					nmea.quality.set(1);
					nmea.numSats.set(14);
					nmea.ageOfDgpsData.set(1.0);
					nmea.hdop.set(0.8);
				}
				openClient();
				workSocket();
				break;
			case "server":
				host = args[1];
				port = Integer.parseInt(args[2]);
				mount = args[3];
				authstr = args[4];
				openServer();
				workSocket();
				break;
			default:
				help();
		}
	}

	private void workSocket() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					readLoop(NtripApp.this.socket.getInputStream(), System.out);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
		if (nmea != null) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						PrintStream printStream = new PrintStream(socket.getOutputStream());
						while (socket.isConnected()) {
							Thread.sleep(1000);
							nmea.utcOfPosition.set(new Datetime());
							String s = nmea.toNmeaString();
							System.out.println(s);
							printStream.println(s);
						}
					} catch (IOException | InterruptedException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
		try {
			readLoop(System.in, socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void readLoop(InputStream in, OutputStream out) throws IOException {
		byte[] buf = new byte[1024];
		while (socket.isConnected() && !socket.isInputShutdown()) {
			int n = in.read(buf);
			if (n>0) out.write(buf,0,n);
			else if (n<0) break;
		}
	}

	public static void main(String[] args) throws IOException {
		new NtripApp(args).run();
	}
}
