import java.util.Stack;

public class Hul_to_C {
	String result = "#include <stdio.h>\nint main() {\n    int _hul;\n\n";
	String tap = "    ";

	public Hul_to_C() {
		Reader r = new Reader();
		String[] token = r.input.split("[\n\t \r\n]+");
		int index = 0;
		for (int i = 0; i < token.length; i++) {
			switch (token[i]) {
			case "Hul?":
				result += tap.repeat(index + 1) + "printf(\"input: \");\n";
				result += tap.repeat(index + 1) + "scanf(\"%d\", &_hul);\n";
				break;
			case "Hul!":
				result += tap.repeat(index + 1) + "printf(\"%d\", _hul);\n";
				break;
			case "Hul>":
				result += tap.repeat(index + 1) + "_hul++;\n";
				break;
			case "Hul<":
				result += tap.repeat(index + 1) + "_hul--;\n";
				break;
			case "Hul{":
				Stack<String> stack = new Stack<String>();
				stack.add("{");
				String num = "";
				for (int j = i + 1; j < token.length; j++) {
					if (token[j].equals("Hul{"))
						stack.add("{");
					if (token[j].equals("}"))
						stack.pop();
					if (stack.isEmpty()) {
						num = token[j + 1];
						break;
					}
				}
				// 짝이 되는 닫는 괄호 뒤에 나온 숫자까지 읽고 다시 돌아오기
				String stri = Integer.toString(index);
				result += tap.repeat(index + 1) + "int max" + stri + " = " + num + ";\n";
				result += tap.repeat(index + 1) + "for (int i" + stri + " = 0; i" + stri + " < max" + stri + "; i"
						+ stri + "++){\n";
				index++;
				break;
			case "}":
				result += tap.repeat(index) + "}\n";
				index--;
				break;
			default: // iter number
				break;
			}
		}
		result += "    return 0;\n}";
	}
}
