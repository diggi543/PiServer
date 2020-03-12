import java.io.DataInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class client {
	public String readString(InputStream pInputStream) {
		
	}
	public static void main(String[] args) {
		try {
			Socket socket = new Socket(InetAddress.getByName("localhost"), 3333);
			InputStream inputStream = socket.getInputStream();
			OutputStream outputStream = socket.getOutputStream();
			Thread readThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						System.out.println("Start reading");
						StringBuilder response = new StringBuilder();
						response.append((char) inputStream.read());
						byte[] buffer = new byte[inputStream.available()];
						inputStream.read(buffer);
						response.append(new String(buffer));
						System.out.println("Response: " + response);
					} catch (Exception e) {
						System.out.println(e);
					}
				}
			});
			readThread.start();
			// Thread.sleep(3000);
			System.out.println("Send data");

			outputStream.write("{\"login\": \"123\", \"password\": \"123\"}".getBytes());
			outputStream.flush();
			readThread.join();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}