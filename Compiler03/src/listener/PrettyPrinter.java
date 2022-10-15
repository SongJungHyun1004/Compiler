package listener;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import generated.*;

public class PrettyPrinter {
	public static void main(String[] args) throws Exception {
		ANTLRFileStream codeStream = new ANTLRFileStream("test2.go");// test.go로 변경 후 제출
		MiniGoLexer lexer = new MiniGoLexer(codeStream);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		MiniGoParser parser = new MiniGoParser(tokens);
		ParseTree tree = parser.program();
		ParseTreeWalker walker = new ParseTreeWalker();
		walker.walk(new MiniGoPrintListener(), tree);
	}
}