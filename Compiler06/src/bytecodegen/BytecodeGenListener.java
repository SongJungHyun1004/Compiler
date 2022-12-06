package bytecodegen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;

import generated.MiniGoBaseListener;
import generated.MiniGoParser;
import generated.MiniGoParser.*;


import static bytecodegen.BytecodeGenListenerHelper.*;
import static bytecodegen.SymbolTable.*;

public class BytecodeGenListener extends MiniGoBaseListener implements ParseTreeListener {
    ParseTreeProperty<String> newTexts = new ParseTreeProperty<String>();
    SymbolTable symbolTable = new SymbolTable();

    int tab = 0;
    int label = 0;

    // program	: decl+
    @Override
    public void enterFun_decl(MiniGoParser.Fun_declContext ctx) {
        symbolTable.initFunDecl();

        String fname = getFunName(ctx);
        ParamsContext params;

        if (fname.equals("main")) {
            symbolTable.putLocalVar("args", Type.INTARRAY);
        } else {
            symbolTable.putFunSpecStr(ctx);
            params = (MiniGoParser.ParamsContext) ctx.getChild(3);
            symbolTable.putParams(params);
        }
    }
    

    // var_decl	:  dec_spec IDENT  type_spec
    //		| dec_spec IDENT type_spec '=' LITERAL
    //		| dec_spec IDENT '[' LITERAL ']' type_spec
    @Override
    public void enterVar_decl(MiniGoParser.Var_declContext ctx) {
        String varName = ctx.IDENT().getText();

        if (isArrayDecl(ctx)) {
            symbolTable.putLocalVar(varName, Type.INTARRAY);
        }
        else if (isDeclWithInit(ctx)) {
            // Fill here
        	symbolTable.putLocalVarWithInitVal(varName, Type.INT, Integer.parseInt(ctx.LITERAL().getText()));
        }
        else  { // simple decl
            symbolTable.putLocalVar(varName, Type.INT);
        }
    }

    @Override
	public void exitLocal_decl(MiniGoParser.Local_declContext ctx) {
    	String lDecl = newTexts.get(ctx.var_decl());
		newTexts.put(ctx, lDecl);
    }


    @Override
    public void exitProgram(MiniGoParser.ProgramContext ctx) {
        String classProlog = getFunProlog();

        String fun_decl = "", var_decl = "";

        for(int i = 0; i < ctx.getChildCount(); i++) {
            if(isFunDecl(ctx, i))
                fun_decl += newTexts.get(ctx.decl(i));
            else
                var_decl += newTexts.get(ctx.decl(i));
        }

        newTexts.put(ctx, classProlog + var_decl + fun_decl);
        String program = newTexts.get(ctx);
        System.out.println(program);
        File file = new File(String.format("test.j"));
		try {// 결과를 파일에 저장
			FileWriter fw = new FileWriter(file);
			fw.write(program);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }


    // decl	: var_decl | fun_decl
    @Override
    public void exitDecl(MiniGoParser.DeclContext ctx) {
        String decl = "";
        if(ctx.getChildCount() == 1)
        {
            if(ctx.var_decl() != null)				//var_decl
                decl += newTexts.get(ctx.var_decl());
            else							//fun_decl
                decl += newTexts.get(ctx.fun_decl());
        }
        newTexts.put(ctx, decl);
    }

    // stmt	: expr_stmt | compound_stmt | if_stmt | for_stmt | return_stmt
    @Override
    public void exitStmt(MiniGoParser.StmtContext ctx) {
        String stmt = "";
        if(ctx.getChildCount() > 0)
        {
            if(ctx.expr_stmt() != null)				// expr_stmt
                stmt += newTexts.get(ctx.expr_stmt());
            else if(ctx.compound_stmt() != null)	// compound_stmt
                stmt += newTexts.get(ctx.compound_stmt());
            // <(0) Fill here>
            else if(ctx.if_stmt() != null)
                stmt += newTexts.get(ctx.if_stmt());
            else if(ctx.for_stmt() != null)
                stmt += newTexts.get(ctx.for_stmt());
            else if(ctx.return_stmt() != null)
                stmt += newTexts.get(ctx.return_stmt());
        }
        newTexts.put(ctx, stmt);
    }

    // expr_stmt	: expr ';'
    @Override
    public void exitExpr_stmt(MiniGoParser.Expr_stmtContext ctx) {
        String stmt = "";

        stmt += newTexts.get(ctx.expr());	// expr

        newTexts.put(ctx, stmt);
    }

    @Override
	public void exitFor_stmt(MiniGoParser.For_stmtContext ctx) {
    	String stmt = "";
    	String condExpr= newTexts.get(ctx.expr());
    	String loop = symbolTable.newLabel();
    	String lend = symbolTable.newLabel();
    	stmt += loop + ":\n"
    			+ condExpr
                + "ifeq " + lend + "\n"
                + newTexts.get(ctx.stmt())
                + "goto " + loop + "\n"
                + lend + ":"  + "\n";
    	newTexts.put(ctx, stmt);
    }


    @Override
    public void exitFun_decl(MiniGoParser.Fun_declContext ctx) {
        // <(2) Fill here!>
    	String fun_decl = "";
    	fun_decl += funcHeader(ctx, getFunName(ctx));
    	fun_decl += newTexts.get(ctx.compound_stmt());
    	String ret = fun_decl.substring(fun_decl.length()-7, fun_decl.length()-1);
    	if(!ret.equals("return"))
    		fun_decl += "return\n";
    	fun_decl += ".end method\n";
    	newTexts.put(ctx, fun_decl);
    }


    private String funcHeader(MiniGoParser.Fun_declContext ctx, String fname) {
        return ".method public static " + symbolTable.getFunSpecStr(fname) + "\n"
                + "\t" + ".limit stack " 	+ getStackSize(ctx) + "\n"
                + "\t" + ".limit locals " 	+ getLocalVarSize(ctx) + "\n";

    }

    @Override
    public void exitVar_decl(MiniGoParser.Var_declContext ctx) {
    	String varName = ctx.IDENT().getText();
        String varDecl = "";
        if (isDeclWithInit(ctx)) {
//            varDecl += "putfield " + varName + "\n";
        	String value = ctx.LITERAL().getText();
        	varDecl += "ldc " + value + "\n"
        			+ "istore" + symbolTable.getVarId(varName) + " \n";
            // v. initialization => Later! skip now..: 
        }
        newTexts.put(ctx, varDecl);
    }

    // compound_stmt	: '{' local_decl* stmt* '}'
    @Override
    public void exitCompound_stmt(MiniGoParser.Compound_stmtContext ctx) {
        // <(3) Fill here>
    	String stmt = "";
    	for(int i = 0; i < ctx.local_decl().size(); i++)
    		stmt += newTexts.get(ctx.local_decl(i));
    	for(int i = 0; i < ctx.stmt().size(); i++)
    		stmt += newTexts.get(ctx.stmt(i));
    	newTexts.put(ctx, stmt);
    }

    // if_stmt		:  IF  expr  stmt
    //		| IF  expr  stmt ELSE stmt   ;
    @Override
    public void exitIf_stmt(MiniGoParser.If_stmtContext ctx) {
        String stmt = "";
        String condExpr= newTexts.get(ctx.expr());
        String thenStmt = newTexts.get(ctx.stmt(0));

        String lend = symbolTable.newLabel();
        String lelse = symbolTable.newLabel();


        if(noElse(ctx)) {
            stmt += condExpr
                    + "ifeq " + lend + "\n"
                    + thenStmt
                    + lend + ":" + "\n";
        }
        else {
            String elseStmt = newTexts.get(ctx.stmt(1));
            stmt += condExpr
                    + "ifeq " + lelse + "\n"
                    + thenStmt
                    + "goto " + lend + "\n"
                    + lelse + ":" + "\n"
                    + elseStmt
                    + lend + ":" + "\n";
        }

        newTexts.put(ctx, stmt);
    }


    // return_stmt	: RETURN
    //		| RETURN expr
    //		| RETURN expr ',' expr	 ;
    @Override
    public void exitReturn_stmt(MiniGoParser.Return_stmtContext ctx) {
        // <(4) Fill here>
    	String stmt = "";
    	if(ctx.getChildCount() == 1)
    		stmt += "return\n";
    	else if(ctx.getChildCount() == 2)
    		stmt += newTexts.get(ctx.expr(0)) 
    				+ "ireturn\n";
    	newTexts.put(ctx, stmt);
    }


    // warning! Too many holes. You should check the rules rather than use them as is.
    @Override
    public void exitExpr(MiniGoParser.ExprContext ctx) {
        String expr = "";

        if(ctx.getChildCount() <= 0) {
            newTexts.put(ctx, "");
            return;
        }

        if(ctx.getChildCount() == 1) { // IDENT | LITERAL
            if(ctx.IDENT() != null) {
                String idName = ctx.IDENT().getText();
                if(symbolTable.getVarType(idName) == Type.INT) {
                    expr += "iload" + symbolTable.getVarId(idName) + "\n";
                }
                //else	// Type int array => Later! skip now..
                //	expr += "           lda " + symbolTable.get(ctx.IDENT().getText()).value + " \n";
            } else if (ctx.LITERAL() != null) {
                String literalStr = ctx.LITERAL().getText();
                expr += "ldc " + literalStr + "\n";
            }
        } else if(ctx.getChildCount() == 2) { // UnaryOperation
        	expr = handleUnaryExpr(ctx, expr);
        } else if(ctx.getChildCount() == 3) {
            if(ctx.getChild(0).getText().equals("(")) { 		// '(' expr ')'
                expr = newTexts.get(ctx.expr(0));
            } else if(ctx.getChild(1).getText().equals("=")) { 	// IDENT '=' expr
                expr = newTexts.get(ctx.expr(0))
                        + "istore" + symbolTable.getVarId(ctx.IDENT().getText()) + "\n";
            } else { 											// binary operation
                expr = handleBinExpr(ctx, expr);

            }
        }
        // IDENT '(' args ')' |  IDENT '[' expr ']'
        else if(ctx.getChildCount() == 4) {
            if(ctx.args() != null){		// function calls
                expr = handleFunCall(ctx, expr);
            } else { // expr
                // Arrays: TODO  
            }
        }
        // IDENT '[' expr ']' '=' expr
        else { // Arrays: TODO			*/
        }
        newTexts.put(ctx, expr);
    }


    private String handleUnaryExpr(MiniGoParser.ExprContext ctx, String expr) {
        String l1 = symbolTable.newLabel();
        String l2 = symbolTable.newLabel();
        String lend = symbolTable.newLabel();

        expr += newTexts.get(ctx.expr(0));
        String storeId = expr.substring(expr.length()-3, expr.length()-1);
        
        switch(ctx.getChild(0).getText()) {
            case "-":
                expr += "ineg\n"; break;
            case "--":
                expr += "ldc 1" + "\n"
                        + "isub" + "\n"
                        + "istore" + storeId + "\n";
                break;
            case "++":
                expr += "ldc 1" + "\n"
                        + "iadd" + "\n"
                        + "istore" + storeId + "\n";
                break;
            case "!":
                expr += "ifeq " + l2 + "\n"
                        + l1 + ":\n" + "ldc 0" + "\n"
                        + "goto " + lend + "\n"
                        + l2 + ":\n" + "ldc 1" + "\n"
                        + lend + ": " + "\n";
                break;
        }
        return expr;
    }


    private String handleBinExpr(MiniGoParser.ExprContext ctx, String expr) {
        String l2 = symbolTable.newLabel();
        String lend = symbolTable.newLabel();

        expr += newTexts.get(ctx.expr(0));
        expr += newTexts.get(ctx.expr(1));

        switch (ctx.getChild(1).getText()) {
            case "*":
                expr += "imul \n"; break;
            case "/":
                expr += "idiv \n"; break;
            case "%":
                expr += "irem \n"; break;
            case "+":		// expr(0) expr(1) iadd
                expr += "iadd \n"; break;
            case "-":
                expr += "isub \n"; break;

            case "==":
                expr += "isub " + "\n"
                        + "ifeq "+ l2 + "\n"
                        + "ldc 0" + "\n"
                        + "goto " + lend + "\n"
                        + l2 + ":" + "\n" 
                        + "ldc 1" + "\n"
                        + lend + ":" + "\n";
                break;
            case "!=":
                expr += "isub " + "\n"
                        + "ifne "+ l2 + "\n"
                        + "ldc 0" + "\n"
                        + "goto " + lend + "\n"
                        + l2 + ":" + "\n" 
                        + "ldc 1" + "\n"
                        + lend + ":" + "\n";
                break;
            case "<=":
                // <(5) Fill here>
            	expr += "isub " + "\n"
                        + "ifle "+ l2 + "\n"
                        + "ldc 0" + "\n"
                        + "goto " + lend + "\n"
                        + l2 + ":" + "\n" 
                        + "ldc 1" + "\n"
                        + lend + ":" + "\n";
                break;
            case "<":
                // <(6) Fill here>
            	expr += "isub " + "\n"
                        + "iflt "+ l2 + "\n"
                        + "ldc 0" + "\n"
                        + "goto " + lend + "\n"
                        + l2 + ":" + "\n" 
                        + "ldc 1" + "\n"
                        + lend + ":" + "\n";
                break;

            case ">=":
                // <(7) Fill here>
            	expr += "isub " + "\n"
                        + "ifge "+ l2 + "\n"
                        + "ldc 0" + "\n"
                        + "goto " + lend + "\n"
                        + l2 + ":" + "\n" 
                        + "ldc 1" + "\n"
                        + lend + ":" + "\n";
                break;

            case ">":
                // <(8) Fill here>
            	expr += "isub " + "\n"
                        + "ifgt "+ l2 + "\n"
                        + "ldc 0" + "\n"
                        + "goto " + lend + "\n"
                        + l2 + ":" + "\n" 
                        + "ldc 1" + "\n"
                        + lend + ":" + "\n";
                break;

            case "and":
                expr +=  "ifne "+ lend + "\n"
                        + "pop" + "\n" + "ldc 0" + "\n"
                        + lend + ":" + "\n"; break;
            case "or":
                // <(9) Fill here>
            	expr +=  "ifeq "+ lend + "\n"
                        + "pop" + "\n" + "ldc 1" + "\n"
                        + lend + ":" + "\n";
                break;

        }
        return expr;
    }
    private String handleFunCall(MiniGoParser.ExprContext ctx, String expr) {
        String fname = getFunName(ctx);

        if (fname.equals("_print")) {		// System.out.println	
            expr = "getstatic java/lang/System/out Ljava/io/PrintStream; " + "\n"
                    + newTexts.get(ctx.args())
                    + "invokevirtual " + symbolTable.getFunSpecStr("_print") + "\n";
        } else {
            expr = newTexts.get(ctx.args())
                    + "invokestatic " + getCurrentClassName()+ "/" + symbolTable.getFunSpecStr(fname) + "\n";
        }
        return expr;

    }

    // args	: expr (',' expr)* | ;
    @Override
    public void exitArgs(MiniGoParser.ArgsContext ctx) {

        String argsStr = "";

        for (int i=0; i < ctx.expr().size() ; i++) {
            argsStr += newTexts.get(ctx.expr(i)) ;
        }
        newTexts.put(ctx, argsStr);
    }

}
