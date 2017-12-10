package TP2;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ASD {

    /************************************************ Program ************************************************/
    static public class Program {
        List<Function> lfunct;
        SymbolTable symbolTable = new SymbolTable();
        public Program(List<Function> lf) {
            this.lfunct = lf;
        }

        // Pretty-printer
        public String pp() {
            String ret = "";
            for (Function f : lfunct) {
              ret = ret + f.pp();
            }
            return ret;
        }

        // IR generation
        public Llvm.IR toIR() throws TypeException, SymbolException {
            Llvm.IR irprog = new Llvm.IR(Llvm.empty(), Llvm.empty());
            List<Function.RetFunction> lRF = new ArrayList<>();

            for (Function f : lfunct) {
              Function.RetFunction retF = f.toIR(symbolTable);
              lRF.add(retF);
            }
            for (Function.RetFunction retF : lRF) {
              if(retF.lI != null){
              irprog.addListHeader(retF.lI);
              }
            }
            for (Function.RetFunction retF : lRF) {
              irprog.append(retF.ir);
            }
            return irprog;
        }
    }


    static public abstract class Function{
        Type type;
        String ident;
        BlockImplement bloc;
      List<Affectable> lParam;

        public abstract String pp();

        public abstract RetFunction toIR(SymbolTable symbolTable) throws TypeException, SymbolException;

        // Object returned by toIR on expressions, with IR + synthesized attributes
        static public class RetFunction{
            // The LLVM IR:
            public Llvm.IR ir;
            public List<Llvm.Instruction> lI;

            public RetFunction(Llvm.IR ir, List<Llvm.Instruction> lI) {
                this.ir = ir;
                this.lI = lI;
            }
        }
    }

    static public class FunctionImplement extends Function {
        Type type;
        String ident;
        BlockImplement bloc;
        List<Affectable> lParam;

        public FunctionImplement(Type type, String ident, BlockImplement bloc, List<Affectable> lParam) {
            this.type = type;
            this.ident = ident;
            this.bloc = bloc;
            this.lParam =lParam;
        }

        // Pretty-printer
        public String pp() {
          return "Function " + type.pp() + ident + bloc.pp();
        }

        // IR generation
        public RetFunction toIR(SymbolTable st) throws TypeException, SymbolException {
          SymbolTable.FunctionSymbol symboleTableFunc = new SymbolTable.FunctionSymbol(type, ident, null, false);

          if(st.lookup(symboleTableFunc.ident) != null){
            Block.RetBlock retbloc = bloc.toIR(st);
            Llvm.IR irFunction = new Llvm.IR(Llvm.empty(), Llvm.empty());
            String param = "";
            if(!lParam.isEmpty()){
            List<Affectable.RetAffectable> lRetParam = new ArrayList<>();
            for (Affectable exp : lParam) {
              lRetParam.add(exp.toIR());
            }
            for (int i = 0; i < lRetParam.size() - 2 ; i++ ) {
              String p100 = " ";
              if (Character.isLetter(lRetParam.get(i).result.charAt(0))){
                p100 = " %";
              }
              param = param + lRetParam.get(i).type.toLlvmType() + p100 + lRetParam.get(i).result + ", ";
            }
            String p100 = " ";
            if (Character.isLetter(lRetParam.get(lRetParam.size() - 1).result.charAt(0))){
              p100 = " %";
            param = param + lRetParam.get(lRetParam.size() - 1).type.toLlvmType() + p100 + lRetParam.get(lRetParam.size() - 1).result;}
          }
            Llvm.Instruction instFunc = new Llvm.Function(type.toLlvmType(), ident, param);
            Llvm.Instruction instend = new Llvm.EndFunction();

            List<Llvm.Instruction> lI = new ArrayList<>();
            lI.addAll(retbloc.ir.header);
            irFunction.appendHeader(instFunc);
            irFunction.addCode(retbloc.ir);

            VoidType voidType = new VoidType();
            if(!voidType.equals(type)){
            Llvm.Instruction irReturn = new Llvm.Return(type.toLlvmType(), "0");
            irFunction.appendHeader(irReturn);
          }
          else {
            Llvm.Instruction irReturn = new Llvm.Return(null, "void");
            irFunction.appendHeader(irReturn);
          }
            irFunction.appendHeader(instend);
           return new RetFunction(irFunction, lI);
          }
          else {
          Block.RetBlock retbloc = bloc.toIR(st);
          String param = "";
          if(!lParam.isEmpty()){
          List<Affectable.RetAffectable> lRetParam = new ArrayList<>();
          for (Affectable exp : lParam) {
            lRetParam.add(exp.toIR());
          }
          for (int i = 0; i < lRetParam.size() - 2 ; i++ ) {
            String p100 = " ";
            if (Character.isLetter(lRetParam.get(i).result.charAt(0))){
              p100 = " %";
            }
            param = param + lRetParam.get(i).type.toLlvmType() + p100 + lRetParam.get(i).result + ", ";
          }
          String p100 = " ";
          if (Character.isLetter(lRetParam.get(lRetParam.size() - 1).result.charAt(0))){
            p100 = " %";
          }
          param = param + lRetParam.get(lRetParam.size() - 1).type.toLlvmType() + p100 + lRetParam.get(lRetParam.size() - 1).result;
        }
          Llvm.IR irFunction = new Llvm.IR(Llvm.empty(), Llvm.empty());
          Llvm.Instruction instFunc = new Llvm.Function(type.toLlvmType(), ident, param);
          Llvm.Instruction instend = new Llvm.EndFunction();

          irFunction.appendCode(instFunc);
          irFunction.append(retbloc.ir);

          VoidType voidType = new VoidType();
          if(!voidType.equals(type)){
          Type type = new IntType();
          Llvm.Instruction irReturn = new Llvm.Return(type.toLlvmType(), "0");
          irFunction.appendCode(irReturn);
        }
        else {
          Llvm.Instruction irReturn = new Llvm.Return(null, "void");
          irFunction.appendCode(irReturn);
        }
          irFunction.appendCode(instend);
         return new RetFunction(irFunction, null);
        }
      }
    }

    static public class FunctionProto extends Function {
        Type type;
        String ident;
        List<Affectable> lParam;

        public FunctionProto(Type type, String ident, List<Affectable> lParam) {
            this.type = type;
            this.ident = ident;
            this.lParam =lParam;
        }

        // Pretty-printer
        public String pp() {
          return "Function " + type.pp() + ident + bloc.pp();
        }

        // IR generation
        public RetFunction toIR(SymbolTable symbolTable) throws TypeException {
          Llvm.IR irFunction = new Llvm.IR(Llvm.empty(), Llvm.empty());
          SymbolTable.FunctionSymbol symboleTableProto = new SymbolTable.FunctionSymbol(type, ident, null, false);
          symbolTable.add(symboleTableProto);
         return new RetFunction(irFunction, null);
        }
    }

    /************************************************ Block ************************************************/


    static public abstract class Block{
        Declaration dImpl;
        List<Statement> corpBlock = new ArrayList<>();

        public abstract String pp();

        public abstract RetBlock toIR(SymbolTable st) throws TypeException, SymbolException;

        // Object returned by toIR on expressions, with IR + synthesized attributes
        static public class RetBlock{
            // The LLVM IR:
            public Llvm.IR ir;
            Type type;
            String value;

            public RetBlock(Llvm.IR ir, Type type, String value) {
                this.ir = ir;
                this.type = type;
                this.value = value;
            }
        }
    }

    static public class BlockImplement extends Block {
        DeclarationImplement dImpl;
        List<StatementImplement> lStatement = new ArrayList<>();

        public BlockImplement(DeclarationImplement dImpl, List<StatementImplement> lStatement) {
            this.dImpl = dImpl;
            this.lStatement = lStatement;
        }

        // Pretty-printer
        public String pp() {
          String str = "";
          for (Statement e : lStatement) {
            str = str + e.toString();
          }
          return str;
        }

        // IR generation
        public RetBlock toIR(SymbolTable st) throws TypeException, SymbolException {
         Llvm.IR irBlock = new  Llvm.IR(Llvm.empty(), Llvm.empty());

         Llvm.Instruction commmentBlockD = new Llvm.Comment("Début block ");
         irBlock.appendCode(commmentBlockD);

         if (dImpl != null){
           Declaration.RetDeclaration retDec = dImpl.toIR();
           irBlock.append(retDec.ir);
         }

         String lastExprRes = "0";
         Type lastTypeRes = new IntType();

         for (StatementImplement si : lStatement) {

          Statement.RetStatement reStat = si.toIR(st);
          irBlock.append(reStat.ir);
          if(si.e != null){
          lastExprRes = reStat.result;
          lastTypeRes = reStat.type;
          }

         }

         Llvm.Instruction commmentBlockf = new Llvm.Comment("Fin block ");
         irBlock.appendCode(commmentBlockf);

         return new RetBlock(irBlock, lastTypeRes, lastExprRes);
        }
    }


    /************************************************ Declaration ************************************************/
    static public abstract class Declaration{

      public abstract String pp();

      public abstract RetDeclaration toIR() throws TypeException;

      // Object returned by toIR on expressions, with IR + synthesized attributes
      static public class RetDeclaration {
          // The LLVM IR:
          public Llvm.IR ir;

          public RetDeclaration(Llvm.IR ir) {
              this.ir = ir;
          }
        }
      }

      static public class DeclarationImplement extends Declaration{
        List <AffectableVar> la = new ArrayList<>();

        public DeclarationImplement (List<AffectableVar> la) {
            this.la = la;
        }

        // Pretty-printer
        public String pp() {
          String str = "";
            for (AffectableVar a : la) {
              str = str + a.pp();
            }
            return str;
        }

        // IR generation
        public RetDeclaration toIR() throws TypeException {
          Llvm.IR irDecl = new Llvm.IR(Llvm.empty(), Llvm.empty());

          for (AffectableVar a : la) {
            AffectableVar.RetAffectable retAv = a.toIR();
            irDecl.append(retAv.ir);
          }

          return new RetDeclaration(irDecl);
        }
      }




    /************************************************ Statement ************************************************/
    static public abstract class Statement{

      public abstract String pp();

      public abstract RetStatement toIR(SymbolTable st) throws TypeException, SymbolException;

      // Object returned by toIR on expressions, with IR + synthesized attributes
      static public class RetStatement {
          // The LLVM IR:
          public Llvm.IR ir;
          // And additional stuff:
          public Type type; // The type of the expression
          public String result; // The name containing the expression's result
          // (either an identifier, or an immediate value)

          public RetStatement(Llvm.IR ir, Type type, String result) {
              this.ir = ir;
              this.type = type;
              this.result = result;
          }
        }
      }

    static public class StatementImplement extends Statement{
      private Expression e = null;
      private Instruction i = null;

      public StatementImplement (Expression expression) {
          this.e = expression;
      }

      public StatementImplement (Instruction instruction){
        this.i = instruction;
      }

      // Pretty-printer
      public String pp() {
        String str = "";
          if (e != null){
            str =  e.pp();
          }
          else if (i != null){
            str = i.pp();
          }
          return str;
      }

      // IR generation
      public RetStatement toIR(SymbolTable st) throws TypeException, SymbolException {
        RetStatement rep = null;
        if(e != null){
          Expression.RetExpression retExpr = e.toIR();
          rep =  new RetStatement(retExpr.ir, retExpr.type, retExpr.result);
        }
        if(i != null){
          Instruction.RetInstruction retInstr = i.toIR(st);
          rep =  new RetStatement(retInstr.ir, null, null);
        }
        return rep;
      }
    }


    /************************************************ Expression ************************************************/
    static public abstract class Expression{
        public abstract String pp();

        public abstract RetExpression toIR() throws TypeException;

        // Object returned by toIR on expressions, with IR + synthesized attributes
        static public class RetExpression {
            // The LLVM IR:
            public Llvm.IR ir;
            // And additional stuff:
            public Type type; // The type of the expression
            public String result; // The name containing the expression's result
            // (either an identifier, or an immediate value)

            public RetExpression(Llvm.IR ir, Type type, String result) {
                this.ir = ir;
                this.type = type;
                this.result = result;
            }
        }
    }



    /************************************************ Instruction ************************************************/
    static public abstract class Instruction{
        public abstract String pp();

        public abstract RetInstruction toIR(SymbolTable st) throws TypeException, SymbolException;

        static public class RetInstruction {
            // The LLVM IR:
            public Llvm.IR ir;

            public RetInstruction(Llvm.IR ir) {
                this.ir = ir;
            }
        }
    }


    /************************************************ Affectable ************************************************/
    static public abstract class Affectable{
        public abstract String pp();

        public abstract RetAffectable toIR() throws TypeException;

        static public class RetAffectable {
            // The LLVM IR:
            public Llvm.IR ir;
            // And additional stuff:
            public Type type;
            public String result; // The name containing the expression's result
            // (either an identifier, or an immediate value)

            public RetAffectable(Llvm.IR ir,Type type, String result) {
                this.ir = ir;
                this.type = type;
                this.result = result;
            }
        }
    }

    /************************************************ Affichable ************************************************/

    static public abstract class Affichable{
        public abstract String pp();

        public abstract RetAffichable toIR() throws TypeException;

        static public class RetAffichable {
            // The LLVM IR:
            public Llvm.IR ir;
            public String name;
            public Expression exprIdent;

            public RetAffichable(Llvm.IR ir, String name, Expression exprIdent) {
                this.ir = ir;
                this.name = name;
                this.exprIdent = exprIdent;
            }
        }
    }


/************************************************ CallFunction ************************************************/


    static public class CallFunc extends Instruction {
        String name;

        public CallFunc(String name) {
            this.name = name;
        }

        // Pretty-printer
        public String pp() {
            return "call @" + name + "()";
        }

        // IR generation
        public RetInstruction toIR(SymbolTable st) throws TypeException, SymbolException {
            Llvm.IR irCallF = new Llvm.IR(Llvm.empty(), Llvm.empty());

            SymbolTable.FunctionSymbol funcSymbol = (SymbolTable.FunctionSymbol) st.lookup(name);
            if(funcSymbol == null){
              throw new SymbolException("Erreur, la fonction " + name +" n'existe pas");
            }
            else{
              Type type =funcSymbol.returnType;
              Llvm.Instruction instCall = new Llvm.Call(type.toLlvmType(), name);
              irCallF.appendCode(instCall);
            }

            return new RetInstruction(irCallF);
          }
    }


    /************************************************ AddExpression ************************************************/
    // Concrete class for Expression: add case
    static public class AddExpression extends Expression {
        Expression left;
        Expression right;

        public AddExpression(Expression left, Expression right) {
            this.left = left;
            this.right = right;
        }

        // Pretty-printer
        public String pp() {
            return "(" + left.pp() + " + " + right.pp() + ")";
        }

        // IR generation
        public RetExpression toIR() throws TypeException {
            RetExpression leftRet = left.toIR();
            RetExpression rightRet = right.toIR();

            // We check if the types mismatches
            if (!leftRet.type.equals(rightRet.type)) {
                throw new TypeException("type mismatch: have " + leftRet.type + " and " + rightRet.type);
            }

            // We base our build on the left generated IR:
            // append right code
            leftRet.ir.append(rightRet.ir);

            // allocate a new identifier for the result
            String result = Utils.newtmp();

            // new add instruction result = left + right
            Llvm.Instruction add = new Llvm.Add(leftRet.type.toLlvmType(), leftRet.result, rightRet.result, result);

            // append this instruction
            leftRet.ir.appendCode(add);

            // return the generated IR, plus the type of this expression
            // and where to find its result
            return new RetExpression(leftRet.ir, leftRet.type, result);
        }
    }




    /************************************************ MoinsExpression ************************************************/
    static public class MoinsExpression extends Expression {
        Expression left;
        Expression right;

        public MoinsExpression(Expression left, Expression right) {
            this.left = left;
            this.right = right;
        }

        // Pretty-printer
        public String pp() {
            return "(" + left.pp() + " - " + right.pp() + ")";
        }

        // IR generation
        public RetExpression toIR() throws TypeException {
            RetExpression leftRet = left.toIR();
            RetExpression rightRet = right.toIR();

            // We check if the types mismatches
            if (!leftRet.type.equals(rightRet.type)) {
                throw new TypeException("type mismatch: have " + leftRet.type + " and " + rightRet.type);
            }

            // We base our build on the left generated IR:
            // append right code
            leftRet.ir.append(rightRet.ir);

            // allocate a new identifier for the result
            String result = Utils.newtmp();

            // new add instruction result = left + right
            Llvm.Instruction moins = new Llvm.Moins(leftRet.type.toLlvmType(), leftRet.result, rightRet.result, result);

            // append this instruction
            leftRet.ir.appendCode(moins);

            // return the generated IR, plus the type of this expression
            // and where to find its result
            return new RetExpression(leftRet.ir, leftRet.type, result);
        }
    }



    /************************************************ MultExpression ************************************************/
    static public class MultExpression extends Expression {
        Expression left;
        Expression right;

        public MultExpression(Expression left, Expression right) {
            this.left = left;
            this.right = right;
        }

        // Pretty-printer
        public String pp() {
            return "(" + left.pp() + " * " + right.pp() + ")";
        }

        // IR generation
        public RetExpression toIR() throws TypeException {
            RetExpression leftRet = left.toIR();
            RetExpression rightRet = right.toIR();

            // We check if the types mismatches
            if (!leftRet.type.equals(rightRet.type)) {
                throw new TypeException("type mismatch: have " + leftRet.type + " and " + rightRet.type);
            }

            // We base our build on the left generated IR:
            // append right code
            leftRet.ir.append(rightRet.ir);

            // allocate a new identifier for the result
            String result = Utils.newtmp();

            // new add instruction result = left + right
            Llvm.Instruction mult = new Llvm.Mult(leftRet.type.toLlvmType(), leftRet.result, rightRet.result, result);

            // append this instruction
            leftRet.ir.appendCode(mult);

            // return the generated IR, plus the type of this expression
            // and where to find its result
            return new RetExpression(leftRet.ir, leftRet.type, result);
        }
    }



    /************************************************ DivExpression ************************************************/
    static public class DivExpression extends Expression {
        Expression left;
        Expression right;

        public DivExpression(Expression left, Expression right) {
            this.left = left;
            this.right = right;
        }

        // Pretty-printer
        public String pp() {
            return "(" + left.pp() + " / " + right.pp() + ")";
        }

        // IR generation
        public RetExpression toIR() throws TypeException {
            RetExpression leftRet = left.toIR();
            RetExpression rightRet = right.toIR();

            // We check if the types mismatches
            if (!leftRet.type.equals(rightRet.type)) {
                throw new TypeException("type mismatch: have " + leftRet.type + " and " + rightRet.type);
            }

            // We base our build on the left generated IR:
            // append right code
            leftRet.ir.append(rightRet.ir);

            // allocate a new identifier for the result
            String result = Utils.newtmp();

            // new add instruction result = left + right
            Llvm.Instruction div = new Llvm.Div(leftRet.type.toLlvmType(), leftRet.result, rightRet.result, result);

            // append this instruction
            leftRet.ir.appendCode(div);

            // return the generated IR, plus the type of this expression
            // and where to find its result
            return new RetExpression(leftRet.ir, leftRet.type, result);
        }
    }




    /************************************************ Ident ************************************************/
    static public class ExprIdent extends Expression {
        Type type;
        String ident;

        public ExprIdent(Type type, String ident) {
            this.type = type;
            this.ident = ident;
        }

        // Pretty-printer
        public String pp() {
            return "(" + ident + ")";
        }

        // IR generation
        public RetExpression toIR() throws TypeException {

            Llvm.IR irRE = new Llvm.IR(Llvm.empty(), Llvm.empty());

            String result = Utils.newtmp();

            // new add instruction result = left + right
            Llvm.Instruction exprIdent = new Llvm.ExprIdent(type.toLlvmType(), ident, result);

            // append this instruction
            irRE.appendCode(exprIdent);

            // return the generated IR, plus the type of this expression
            // and where to find its result
            return new RetExpression(irRE, type, result);
        }
    }


    /************************************************ ExpressionIf ************************************************/
    static public class ExpressionIf extends Expression {
        Expression expr;

        public ExpressionIf(Expression expr) {
            this.expr = expr;
        }

        // Pretty-printer
        public String pp() {
            return "(" + expr.pp() + ")";
        }

        // IR generation
        public RetExpression toIR() throws TypeException {

            Expression.RetExpression exprRet = expr.toIR();

            String icmp = Utils.newicmp();

            // new add instruction result = left + right
            Llvm.Instruction exprIfCond = new Llvm.Bool(exprRet.type.toLlvmType(), icmp , exprRet.result);

            // append this instruction
            exprRet.ir.appendCode(exprIfCond);

            // return the generated IR, plus the type of this expression
            // and where to find its result
            return new RetExpression(exprRet.ir, exprRet.type, icmp);
        }
    }




    /************************************************ Integer ************************************************/
    // Concrete class for Expression: constant (integer) case
    static public class IntegerExpression extends Expression {
        int value;

        public IntegerExpression(int value) {
            this.value = value;
        }

        public String pp() {
            return "" + value;
        }

        public RetExpression toIR() {
            // Here we simply return an empty IR
            // the `result' of this expression is the integer itself (as string)
            return new RetExpression(new Llvm.IR(Llvm.empty(), Llvm.empty()), new IntType(), "" + value);
        }
    }





    /************************************************ AffectInstruction ************************************************/
    static public class AffectInstruction extends Instruction {
        Affectable left;
        Expression right;

        public AffectInstruction(Affectable left, Expression right) {
            this.left = left;
            this.right = right;
        }

        // Pretty-printer
        public String pp() {
            return "(" + left.pp() + " := " + right.pp() + ")";
        }

        // IR generation
        public RetInstruction toIR(SymbolTable st) throws TypeException {
            Affectable.RetAffectable leftRet = left.toIR();
            Expression.RetExpression rightRet = right.toIR();

            // We check if the types mismatches
            if (!leftRet.type.equals(rightRet.type)) {
                throw new TypeException("type mismatch: have " + leftRet.type + " and " + rightRet.type);
            }

            // new affect instruction result = affectable := expression
            Llvm.Instruction affect = new Llvm.AffectExpression(leftRet.type.toLlvmType(),leftRet, rightRet);

            // append this instruction
            rightRet.ir.appendCode(affect);

            // return the generated IR, plus the type of this expression
            // and where to find its result
            return new RetInstruction(rightRet.ir);
        }
    }





    /************************************************ Return ************************************************/
    static public class ReturnInstruction extends Instruction {
        Expression expr;

        public ReturnInstruction(Expression expr) {
            this.expr = expr;
        }

        // Pretty-printer
        public String pp() {
            return "return " + expr.pp();
        }

        // IR generation
        public RetInstruction toIR(SymbolTable st) throws TypeException {
            Expression.RetExpression exprRet = expr.toIR();

            // new affect instruction result = affectable := expression
            Llvm.Instruction returninstr = new Llvm.Return(exprRet.type.toLlvmType(),exprRet.result);

            // append this instruction
            exprRet.ir.appendCode(returninstr);

            // return the generated IR, plus the type of this expression
            // and where to find its result
            return new RetInstruction(exprRet.ir);
        }
    }


    /************************************************ Print ************************************************/

    static public class Print extends Instruction {
      List<Affichable> listAffich = new ArrayList<>();

        public Print(List<Affichable> lA) {
            this.listAffich = lA;
        }

        // Pretty-printer
        public String pp() {
            String rep = "";
            for (Affichable aff : listAffich) {
              rep = rep + aff.pp();
            }
            return rep;
        }

        // IR generation
        public RetInstruction toIR(SymbolTable st) throws TypeException {
            String result = "(i8* getelementptr inbounds ";
            String format = "";
            List<Affichable.RetAffichable> lRet = new ArrayList<>();

            Llvm.IR irPrint = new Llvm.IR(Llvm.empty(), Llvm.empty());

            for (Affichable a : listAffich) {
              Affichable.RetAffichable retAffich = a.toIR();
              lRet.add(retAffich);
              if(retAffich.name != null){
                irPrint.append(retAffich.ir);
                format = format + retAffich.name;
              }
              else {
                format = format + "%d";
              }
            }

            String name = Utils.newglob("formatPrint");

            Utils.LLVMStringConstant llvmFormat = Utils.stringTransform(format);

            Llvm.Instruction irInstPrint = new Llvm.DecStringPrint(name, llvmFormat);
            result = result + "([" + llvmFormat.length  + " x i8]" + ",[" + llvmFormat.length + " x i8]* @" + name + ", i32 0, i32 0)";

            for(int i = 0; i < lRet.size(); i++){
              if(lRet.get(i).exprIdent != null){
                Expression.RetExpression rslt =lRet.get(i).exprIdent.toIR();
                irPrint.append(rslt.ir);
                result = result + ", " + rslt.type.toLlvmType() + " " + rslt.result;
              }
            }

            result = result + ")";

            Llvm.Instruction instPrint = new Llvm.Print(result);

            irPrint.appendHeader(irInstPrint);
            irPrint.appendCode(instPrint);

            return new RetInstruction(irPrint);
        }
    }

    /************************************************ Read************************************************/

    static public class Read extends Instruction{
    	List<Affectable> lA;

    	public Read(List<Affectable> lA){
    		this.lA = lA;
    	}

    	public String pp() {
            String rep = "";
            for (Affectable a : lA) {
                rep = rep + a.pp();
            }
            return rep;
          }

    	  public RetInstruction toIR(SymbolTable st) throws TypeException{
    		Llvm.IR irRead = new Llvm.IR(Llvm.empty(), Llvm.empty());
        String result = "(i8* getelementptr inbounds ";
        String format = "";
        List<Affectable.RetAffectable> lRet = new ArrayList<>();

        for (Affectable affectable : lA) {
          Affectable.RetAffectable retA = affectable.toIR();
          lRet.add(retA);
          format = format + "%d";
        }

        Utils.LLVMStringConstant llvmFormat = Utils.stringTransform(format);


        String head = Utils.newglob("formatRead");
        Llvm.Instruction irInstRead = new Llvm.DecStringRead(head, llvmFormat);

        result = result + "([" + llvmFormat.length  + " x i8]" + ",[" + llvmFormat.length + " x i8]* @" + head + ", i32 0, i32 0)";

        for(int i = 0; i < lRet.size(); i++){
            result = result + ", " + lRet.get(i).type.toLlvmType() + "* %" + lRet.get(i).result;
          }

        result = result + ")";

        Llvm.Instruction instRead = new Llvm.Read(result);
        irRead.appendHeader(irInstRead);
        irRead.appendCode(instRead);
    		return new RetInstruction(irRead);
    	}
    }


    /************************************************ IfInstElse ************************************************/
    static public class IfInstructionElse extends Instruction {
        Expression expr;
        Block bloc;
        Block bloc2;

        public IfInstructionElse(Expression expr, Block block, Block bloc2) {
            this.expr = expr;
            this.bloc = block;
            this.bloc2 = bloc2;
        }

        // Pretty-printer
        public String pp() {
            return "if " + expr.pp() + " then " + bloc.pp() + " else " + bloc2.pp();
        }

        // IR generation
        public RetInstruction toIR(SymbolTable st) throws TypeException, SymbolException {
            String labelElse = null;
            Llvm.Instruction labelelse = null;
            Block.RetBlock expr2Bloc = null;

            Llvm.IR ifIR = new Llvm.IR(Llvm.empty(), Llvm.empty());

            Expression.RetExpression exprRet = expr.toIR();
            Block.RetBlock exprBloc = bloc.toIR(st);

            if(this.bloc2 != null){
            expr2Bloc = bloc2.toIR(st);
          }

            String labelThen = Utils.newlab("then");
            String labelFi = Utils.newlab("fi");

            Llvm.Instruction labelthen = new Llvm.Label(labelThen);
            Llvm.Instruction lablefi = new Llvm.Label(labelFi);

            if(this.bloc2 != null){
              labelElse = Utils.newlab("else");
              labelelse = new Llvm.Label(labelElse);
            }

            // new affect instruction result = affectable := expression
            Llvm.Instruction ifinstr = new Llvm.IfInstElse(labelThen, labelElse, labelFi, exprRet.result);
            Llvm.Instruction appelfi = new Llvm.AppelLabel(labelFi);

            Llvm.Instruction commentdeb = new Llvm.Comment("Début du if");
            Llvm.Instruction commentend = new Llvm.Comment("Fin du if");
            // append this
            ifIR.appendCode(commentdeb);
            ifIR.append(exprRet.ir);
            ifIR.appendCode(ifinstr);
            ifIR.appendCode(labelthen);
            ifIR.append(exprBloc.ir);
            ifIR.appendCode(appelfi);
            if(bloc2 != null){
            ifIR.appendCode(labelelse);
            ifIR.append(expr2Bloc.ir);
            ifIR.appendCode(appelfi);
          }
            ifIR.appendCode(lablefi);
            ifIR.appendCode(commentend);

            // return the generated IR, plus the type of this expression
            // and where to find its result
            return new RetInstruction(ifIR);
        }
    }




    /************************************************ InstructionWhile ************************************************/
    static public class InstructionWhile extends Instruction {
        Expression expr;
        Block bloc;

        public InstructionWhile(Expression expr, Block block) {
            this.expr = expr;
            this.bloc = block;
        }

        // Pretty-printer
        public String pp() {
            return "while " + expr.pp() + " then " + bloc.pp() + " else ";
        }

        // IR generation
        public RetInstruction toIR(SymbolTable st) throws TypeException, SymbolException {
            Llvm.IR instWhile = new Llvm.IR(Llvm.empty(), Llvm.empty());

            Expression.RetExpression exprRet = expr.toIR();
            Block.RetBlock exprBloc = bloc.toIR(st);

            String labelWhile = Utils.newlab("while");
            String labelDo = Utils.newlab("do");
            String labelDone = Utils.newlab("done");
            String icmp = exprRet.result;

            Llvm.Instruction labelwhile = new Llvm.Label(labelWhile);
            Llvm.Instruction labeldo = new Llvm.Label(labelDo);
            Llvm.Instruction labeldone = new Llvm.Label(labelDone);
            Llvm.Instruction appelwhile = new Llvm.AppelLabel(labelWhile);

            Llvm.Instruction whileinst = new Llvm.WhileInst(labelDo, labelDone, icmp);

            instWhile.appendCode(appelwhile);
            instWhile.appendCode(labelwhile);
            instWhile.append(exprRet.ir);
            instWhile.appendCode(whileinst);
            instWhile.appendCode(labeldo);
            instWhile.append(exprBloc.ir);
            instWhile.appendCode(appelwhile);
            instWhile.appendCode(labeldone);


            // return the generated IR, plus the type of this expression
            // and where to find its result
            return new RetInstruction(instWhile);
        }
    }

    /************************************************ Affichable ************************************************/
    static public class AffichableImpl extends Affichable {
        String ident = null;
        Expression exprIdent = null;

        public AffichableImpl(String ident) {
            this.ident = ident;
        }

        public AffichableImpl(Expression exprIdent){
          this.exprIdent = exprIdent;
        }

        public String pp() {
          String rep = "";
          if(ident != null){
            rep = ident;
          }
          else {
            rep = exprIdent.pp();
          }
            return rep;
        }


        public RetAffichable toIR() {
          Llvm.IR irAffichable = new Llvm.IR(Llvm.empty(), Llvm.empty());

          if(exprIdent == null){
            return new RetAffichable(irAffichable, ident, null);
          }
          return new RetAffichable(irAffichable, null, exprIdent);
        }
    }



    /************************************************ AffectableVar ************************************************/
    static public class AffectableVar extends Affectable {
        Type type;
        String ident;

        public AffectableVar(Type type, String ident) {
            this.type = type;
            this.ident = ident;
        }

        public String pp() {
            return "" + ident;
        }


        public RetAffectable toIR() {

          Llvm.IR irConst = new Llvm.IR(Llvm.empty(), Llvm.empty());

          Llvm.Instruction constante = new Llvm.Var(type.toLlvmType(), ident);

          irConst.appendCode(constante);

            return new RetAffectable(irConst, type, ident);
        }
    }



    /************************************************ Type/Integer ************************************************/
    // Warning: this is the type from VSL+, not the LLVM types!
    static public abstract class Type {
        public abstract String pp();

        public abstract Llvm.Type toLlvmType();
    }

    static class IntType extends Type {
        public String pp() {
            return "INT";
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof IntType;
        }

        public Llvm.Type toLlvmType() {
            return new Llvm.IntType();
        }
    }

    static class VoidType extends Type {
        public String pp() {
            return "VOID";
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof VoidType;
        }

        public Llvm.Type toLlvmType() {
            return new Llvm.VoidType();
        }
    }
  }
