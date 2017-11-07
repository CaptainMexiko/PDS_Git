package TP2;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ASD {
    static public class Program {
        SymbolTable symbolTable = new SymbolTable();
        Expression e; // What a program contains. TODO : change when you extend the language
        Instruction i;
        public Program(Expression e, Instruction i) {
            this.e = e;
            this.i = i;
        }

        // Pretty-printer
        public String pp() {
            return e.pp();
        }

        // IR generation
        public Llvm.IR toIR() throws TypeException {
            // TODO : change when you extend the language

            // computes the IR of the expression
            Expression.RetExpression retExpr = e.toIR();
            Instruction.RetInstruction retInstr = i.toIR();

            retInstr.ir.append(retExpr.ir);

            // add a return instruction
            Llvm.Instruction ret = new Llvm.Return(retExpr.type.toLlvmType(), retExpr.result);
            retInstr.ir.appendCode(ret);

            return retInstr.ir;
        }
    }

    // All toIR methods returns the IR, plus extra information (synthesized attributes)
    // They can take extra arguments (inherited attributes)

    static public abstract class Expression {
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

    static public abstract class Instruction {
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

    static public abstract class Affectable {
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
            Llvm.Instruction affect = new Llvm.Affect(leftRet.type.toLlvmType(),leftRet, rightRet);

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
