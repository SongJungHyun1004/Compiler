import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Reader {
	String input = "";
	public Reader() {
		try {
			BufferedReader reader = new BufferedReader(
					new FileReader("./test.hul"));			
			String str;
			while ((str = reader.readLine()) != null) {
				input += str + " ";
			}
			reader.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
}
