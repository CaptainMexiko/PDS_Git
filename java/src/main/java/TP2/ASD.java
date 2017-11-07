package TP2;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ASD {
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

            Llvm.Instruction ret = new Llvm.Return(retBlock.type.toLlvmType(), retBlock.value);

            retBlock.ir.appendCode(ret);

            return retBlock.ir;
        }
    }

    static public abstract class Block{
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
        List<StatementImplement> lStatement = new ArrayList<>();

        public BlockImplement(List<StatementImplement> lStatement) {
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

            // We base our build on the left generated IR:
            // append right code
            leftRet.ir.append(rightRet.ir);

            // new affect instruction result = affectable := expression
            Llvm.Instruction affect = new Llvm.AffectExpression(leftRet.type.toLlvmType(),leftRet, rightRet);

            // append this instruction
            leftRet.ir.appendCode(affect);

            // return the generated IR, plus the type of this expression
            // and where to find its result
            return new RetInstruction(leftRet.ir);
        }
    }

    static public class AffectableConst extends Affectable {
        Type type;
        String ident;

        public AffectableConst(Type type, String ident) {
            this.type = type;
            this.ident = ident;
        }

        public String pp() {
            return "" + ident;
        }


        public RetAffectable toIR() {

          Llvm.IR irConst = new Llvm.IR(Llvm.empty(), Llvm.empty());

          Llvm.Instruction constante = new Llvm.Const(type.toLlvmType(), ident);

          irConst.appendCode(constante);

            return new RetAffectable(irConst, type, "" + ident);
        }
    }

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
  }
