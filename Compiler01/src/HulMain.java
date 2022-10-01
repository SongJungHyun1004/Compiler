public class HulMain {
	public static void main(String[] args) {
		Hul_to_C htc = new Hul_to_C();
		new Writer(htc.result);
	}
}
//윈도우 10에서 우분투를 켜고 wsl을 이용하여 gcc를 통해 컴파일했습니다.