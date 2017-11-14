package TP2;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ASD {

    /************************************************ Program ************************************************/
    static public class Program {
        BlockImplement bprincipale;
        SymbolTable symbolTable = new SymbolTable();
        public Program(BlockImplement bprincipale) {
            this.bprincipale = bprincipale;
        }

        // Pretty-printer
        public String pp() {
            return bprincipale.pp();
        }

        // IR generation
        public Llvm.IR toIR() throws TypeException {
            // TODO : change when you extend the language

            // computes the IR of the expression
            Block.RetBlock retBlock = bprincipale.toIR();
            return retBlock.ir;
        }
    }


    static public abstract class Function{
        String ident;
        BlockImplement bloc;

        public abstract String pp();

        public abstract RetFunction toIR() throws TypeException;

        // Object returned by toIR on expressions, with IR + synthesized attributes
        static public class RetFunction{
            // The LLVM IR:
            public Llvm.IR ir;

            public RetFunction(Llvm.IR ir) {
                this.ir = ir;
            }
        }
    }

    static public class FunctionImplement extends Function {
        String ident;
        BlockImplement bloc;

        public FunctionImplement(String ident, BlockImplement bloc) {
            this.ident = ident;
            this.bloc = bloc;
        }

        // Pretty-printer
        public String pp() {
          return "Function " + ident + bloc.pp();
        }

        // IR generation
        public RetFunction toIR() throws TypeException {
          Llvm.IR irFunction = new Llvm.IR(Llvm.empty(), Llvm.empty());


         return new RetFunction(irFunction);
        }
    }

    /************************************************ Block ************************************************/


    static public abstract class Block{
        Declaration dImpl;
        List<Statement> corpBlock = new ArrayList<>();

        public abstract String pp();

        public abstract RetBlock toIR() throws TypeException;

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
        public RetBlock toIR() throws TypeException {
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

          Statement.RetStatement reStat = si.toIR();
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

      public abstract RetStatement toIR() throws TypeException;

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
      public RetStatement toIR() throws TypeException {
        RetStatement rep = null;
        if(e != null){
          Expression.RetExpression retExpr = e.toIR();
          rep =  new RetStatement(retExpr.ir, retExpr.type, retExpr.result);
        }
        if(i != null){
          Instruction.RetInstruction retInstr = i.toIR();
          rep =  new RetStatement(retInstr.ir, null, null);
        }
        return rep;
      }
    }

    // All toIR methods returns the IR, plus extra information (synthesized attributes)
    // They can take extra arguments (inherited attributes)


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

        public abstract RetInstruction toIR() throws TypeException;

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
        public RetInstruction toIR() throws TypeException {
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
        public RetInstruction toIR() throws TypeException {
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
        public RetInstruction toIR() throws TypeException {
            String labelElse = null;
            Llvm.Instruction labelelse = null;
            Block.RetBlock expr2Bloc = null;

            Llvm.IR ifIR = new Llvm.IR(Llvm.empty(), Llvm.empty());

            Expression.RetExpression exprRet = expr.toIR();
            Block.RetBlock exprBloc = bloc.toIR();

            if(this.bloc2 != null){
            expr2Bloc = bloc2.toIR();
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
        public RetInstruction toIR() throws TypeException {
            Llvm.IR instWhile = new Llvm.IR(Llvm.empty(), Llvm.empty());

            Expression.RetExpression exprRet = expr.toIR();
            Block.RetBlock exprBloc = bloc.toIR();

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

            return new RetAffectable(irConst, type, "" + ident);
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
