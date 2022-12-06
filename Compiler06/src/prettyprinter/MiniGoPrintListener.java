package prettyprinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.antlr.v4.runtime.tree.ParseTreeProperty;
import generated.*;

public class MiniGoPrintListener extends MiniGoBaseListener {
	int depth = 0; // 들여쓰기의 현재 깊이를 나타낸다.
	String tab = "...."; // tab을 .네 개로 표현한다.
	ParseTreeProperty<String> newTexts = new ParseTreeProperty<>(); // 파스트리를 생성한다.
	/*
	 * Nonterminal의 경우 newTexts.get(ctx.Nonterminal())을 이용해 가져오고 
	 * Terminal의 경우 ctx.Terminal().getText()를 이용해 가져온다.
	 */

	@Override
	public void exitProgram(MiniGoParser.ProgramContext ctx) {// program 파싱이 끝났을 때
		String program = "";
		for (int i = 0; i < ctx.getChildCount(); i++) {
			newTexts.put(ctx, ctx.decl(i).getText());// newTexts에 decl을 넣음
			program += newTexts.get(ctx.getChild(i));// ctx의 child에 들어갔다가 나오면서 출력
		}
		System.out.println(program);// 지우기
		File file = new File(String.format("[HW3]201802112.go"));
		try {// 결과를 파일에 저장
			FileWriter fw = new FileWriter(file);
			fw.write(program);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void exitDecl(MiniGoParser.DeclContext ctx) {// decl 파싱이 끝났을 때
		String decl = "";
		if (ctx.var_decl() != null) {
			decl = newTexts.get(ctx.var_decl());// var_decl일 경우 가져와서 decl에 저장
		} else if (ctx.fun_decl() != null) {
			decl = newTexts.get(ctx.fun_decl());// fun_decl일 경우 가져와서 decl에 저장
		}
		newTexts.put(ctx, decl);// decl을 트리에 저장
	}

	@Override
	public void exitVar_decl(MiniGoParser.Var_declContext ctx) {// var_decl 파싱이 끝났을 때
		String vDecl = newTexts.get(ctx.dec_spec()) + " " + ctx.IDENT().getText();// 공통 부분인 dec_spec IDENT를 처리해준다.
		String space = "";
		if (!newTexts.get(ctx.type_spec()).equals(""))
			space = " ";// type_spec이 있을 경우에만 space에 공백을 하나 저장한다.
		switch (ctx.getChildCount()) {// 자식의 개수로 경우를 나눈다.
		case 3:
			vDecl += space + newTexts.get(ctx.type_spec());// type_spec을 처리한다.
			break;
		case 5:// type_spec = LITERAL을 처리한다.
			vDecl += space + newTexts.get(ctx.type_spec()) + " = " + ctx.LITERAL().getText();
			break;
		case 6:// [LITERAL] type_spec을 처리한다.
			vDecl += "[" + ctx.LITERAL().getText() + "]" + space + newTexts.get(ctx.type_spec());
			break;
		}
		vDecl += "\n";// 변수 선언 후 개행해준다.
		newTexts.put(ctx, vDecl);// vDecl을 트리에 저장한다.
	}

	@Override
	public void exitDec_spec(MiniGoParser.Dec_specContext ctx) {// dec_spec 파싱이 끝났을 때
		String dSpec = ctx.VAR().getText();// VAR을 읽어와 dSpec에 저장한다.
		newTexts.put(ctx, dSpec);// dSpec을 트리에 저장한다.
	}

	@Override
	public void exitType_spec(MiniGoParser.Type_specContext ctx) {// type_spec 파싱이 끝났을 때
		String tSpec = "";
		switch (ctx.getChildCount()) {// 자식의 개수로 경우를 나눈다.
		case 0:// 없는 경우 아무것도 하지 않는다.
			break;
		case 1:
			tSpec = ctx.INT().getText();// 한 개인 경우 INT를 읽어와 tSpec에 저장한다.
			break;
		case 4:
			tSpec = "[" + ctx.LITERAL().getText() + "]" + ctx.INT().getText();// [LITERAL] INT를 처리한다.
			break;
		}
		newTexts.put(ctx, tSpec);// tSpec을 트리에 저장한다.
	}

	@Override
	public void exitFun_decl(MiniGoParser.Fun_declContext ctx) {// fun_decl 파싱이 끝났을 때
		String fDecl = ctx.FUNC().getText() + " " + ctx.IDENT().getText() + "(" + newTexts.get(ctx.params()) + ") "
				+ newTexts.get(ctx.type_spec()) + newTexts.get(ctx.compound_stmt()) + "\n";
		// FUNC IDENT (params) type_spec compound_stmt를 차례로 처리 후 개행을 처리한다.
		newTexts.put(ctx, fDecl);// fDecl을 트리에 저장한다.
	}

	@Override
	public void exitParams(MiniGoParser.ParamsContext ctx) {// params 파싱이 끝났을 때
		String params = "";
		if (ctx.param(0) != null) {// param이 하나라도 존재할 경우
			params += newTexts.get(ctx.param(0));// params에 처음 param을 붙여넣는다.
			for (int i = 1; i < ctx.param().size(); i++)// 두 개 이상일 경우
				params += ", " + newTexts.get(ctx.param(i));// ", "과 함께 반복하여 params에 붙여넣는다.
		}
		newTexts.put(ctx, params);// params를 트리에 저장한다.
	}

	@Override
	public void exitParam(MiniGoParser.ParamContext ctx) {// param 파싱이 끝났을 때
		String param = ctx.IDENT().getText();// 공통 부분인 IDENT를 param에 저장한다.
		if (ctx.type_spec() != null)
			param += " " + newTexts.get(ctx.type_spec());// type_spec이 있으면 공백을 붙여 처리한다.
		newTexts.put(ctx, param);// param을 트리에 저장한다.
	}

	@Override
	public void exitStmt(MiniGoParser.StmtContext ctx) {// stmt 파싱이 끝났을 때
		String stmt = "";
		if (ctx.expr_stmt() != null)
			stmt = newTexts.get(ctx.expr_stmt());// expr_stmt일 경우 expr_stmt를 가져와 stmt에 저장한다.
		else if (ctx.compound_stmt() != null)
			stmt = newTexts.get(ctx.compound_stmt());// compound_stmt일 경우 compound_stmt를 가져와 stmt에 저장한다.
		else if (ctx.if_stmt() != null)
			stmt = newTexts.get(ctx.if_stmt());// if_stmt일 경우 if_stmt를 가져와 stmt에 저장한다.
		else if (ctx.for_stmt() != null)
			stmt = newTexts.get(ctx.for_stmt());// for_stmt일 경우 for_stmt를 가져와 stmt에 저장한다.
		else if (ctx.return_stmt() != null)
			stmt = newTexts.get(ctx.return_stmt());// return_stmt일 경우 return_stmt를 가져와 stmt에 저장한다.
		newTexts.put(ctx, stmt);// stmt를 트리에 저장한다.
	}

	@Override
	public void exitExpr_stmt(MiniGoParser.Expr_stmtContext ctx) {// expr_stmt 파싱이 끝났을 때
		String eStmt = newTexts.get(ctx.expr()) + "\n";// expr을 가져오고 개행을 추가해 eStmt에 저장한다.
		newTexts.put(ctx, eStmt);// eStmt를 트리에 저장한다.
	}

	@Override
	public void exitFor_stmt(MiniGoParser.For_stmtContext ctx) {// for_stmt 파싱이 끝났을 때
		String fStmt = ctx.FOR().getText() + " " + newTexts.get(ctx.expr()) + newTexts.get(ctx.stmt());
		// FOR expr stmt를 처리한다.
		newTexts.put(ctx, fStmt);// fStmt를 트리에 저장한다.
	}

	@Override
	public void enterCompound_stmt(MiniGoParser.Compound_stmtContext ctx) {// compound_stmt 파싱에 들어갈 때
		depth++;// 현재 깊이를 1 증가한다.
	}

	@Override
	public void exitCompound_stmt(MiniGoParser.Compound_stmtContext ctx) {// compount_stmt 파싱이 끝났을 때
		String cStmt = "\n" + tab.repeat(depth - 1) + "{\n";// 개행을 먼저하고 현재 깊이-1 만큼 반복해서 tab을 붙이고 여는 괄호 후에 다시 개행한다.
		for (int i = 0; i < ctx.local_decl().size(); i++)
			cStmt += tab.repeat(depth) + newTexts.get(ctx.local_decl(i));// 현재 깊이만큼 반복해서 tab을 붙이고 local_decl을 있는 개수만큼
																			// cStmt에 저장한다.
		for (int i = 0; i < ctx.stmt().size(); i++)
			cStmt += tab.repeat(depth) + newTexts.get(ctx.stmt(i));// 현재 깊이만큼 반복해서 tab을 붙이고 stmt를 있는 개수만큼 cStmt에 저장한다.
		depth--;// 깊이를 1 감소한다.
		cStmt += tab.repeat(depth) + "}\n";// 현재 깊이만큼 반복해서 tab을 붙이고 닫는 괄호 후에 개행한다.
		newTexts.put(ctx, cStmt);// cStmt를 트리에 저장한다.
	}

	@Override
	public void exitLocal_decl(MiniGoParser.Local_declContext ctx) {// local_decl 파싱이 끝났을 때
		String lDecl = newTexts.get(ctx.var_decl());// var_decl을 처리한다.
		newTexts.put(ctx, lDecl);// lDecl을 트리에 저장한다.
	}

	@Override
	public void exitIf_stmt(MiniGoParser.If_stmtContext ctx) {// if_stmt 파싱이 끝났을 때
		String iStmt = ctx.IF().getText() + " " + newTexts.get(ctx.expr()) + newTexts.get(ctx.stmt(0));
		// 공통 부분인 IF expr stmt를 처리한다.
		if (ctx.ELSE() != null && ctx.stmt(1) != null)// ELSE stmt가 있다면 현재 깊이만큼 반복해서 tab을 붙이고 ELSE와 stmt를 읽어와 iStmt에
														// 붙인다.
			iStmt += tab.repeat(depth) + ctx.ELSE().getText() + newTexts.get(ctx.stmt(1));
		newTexts.put(ctx, iStmt);// iStmt를 트리에 저장한다.
	}

	@Override
	public void exitReturn_stmt(MiniGoParser.Return_stmtContext ctx) {// return_stmt 파싱이 끝났을 때
		String rStmt = ctx.RETURN().getText();// 공통 부분인 RETURN을 처리한다.
		switch (ctx.getChildCount()) {// 자식의 개수로 경우를 나눈다.
		case 1:// RETURN 하나만 있는 경우 넘어간다.
			break;
		case 2:// RETURN expr을 처리한다.
			rStmt += " " + newTexts.get(ctx.expr(0));
			break;
		case 4:// RETURN expr, expr을 처리한다.
			rStmt += " " + newTexts.get(ctx.expr(0)) + ", " + newTexts.get(ctx.expr(1));
			break;
		}
		rStmt += "\n";// return문 후 개행을 해준다.
		newTexts.put(ctx, rStmt);// rStmt를 트리에 저장한다.
	}

	@Override
	public void exitExpr(MiniGoParser.ExprContext ctx) {// expr 파싱이 끝났을 때
		String expr = "";
		switch (ctx.getChildCount()) {// 자식의 개수로 경우를 나눈다.
		case 1:// (LITERAL|IDENT) 처리
			if (ctx.LITERAL() != null)
				expr = ctx.LITERAL().getText();// LITERAL 처리
			else if (ctx.IDENT() != null)
				expr = ctx.IDENT().getText();// IDENT처리
			break;
		case 3:// 자식이 3개인 경우
			if (ctx.getChild(1).equals(ctx.expr(0)))// 두 번째 자식이 expr인 경우
				expr = "(" + newTexts.get(ctx.expr(0)) + ")";// (expr)을 처리한다.
			else if (ctx.getChild(0).equals(ctx.expr(0)))//첫 번째 자식이 expr인 경우
				expr = newTexts.get(ctx.expr(0)) + " " + ctx.op.getText() + " " + newTexts.get(ctx.expr(1));// expr op expr을 처리한다.
			else if (ctx.IDENT() != null)// IDENT가 있는 경우
				expr = ctx.IDENT().getText() + " = " + newTexts.get(ctx.expr(0));// IDENT = expr을 처리한다.
			break;
		case 4:
			expr = ctx.IDENT().getText();// 공통부분인 IDENT를 처리한다.
			if (ctx.expr(0) != null)
				expr += "[" + newTexts.get(ctx.expr(0)) + "]";// [expr]를 처리한다.
			else if (ctx.args() != null)
				expr += "(" + newTexts.get(ctx.args()) + ")";// (args)를 처리한다.
		case 2:// op expr처리
			expr = ctx.op.getText() + newTexts.get(ctx.expr(0));
			break;
		case 6:// IDENT[expr] = expr처리
			expr = ctx.IDENT().getText() + "[" + newTexts.get(ctx.expr(0)) + "]" + " = " + newTexts.get(ctx.expr(1));
			break;
		}
		newTexts.put(ctx, expr);// expr을 트리에 저장한다.
	}

	@Override
	public void exitArgs(MiniGoParser.ArgsContext ctx) {// args 파싱이 끝났을 때
		String args = "";
		if (ctx.expr(0) != null) {// expr이 하나라도 있는 경우
			args += newTexts.get(ctx.expr(0));// 첫 번째 expr을 읽어와 args에 저장한다.
			for (int i = 1; i < ctx.expr().size(); i++)// 두 개 이상일 경우
				args += ", " + newTexts.get(ctx.expr(i));// ", "과 함께 반복하여 args에 붙여넣는다
		}
		newTexts.put(ctx, args);// args를 트리에 저장한다.
	}
}
