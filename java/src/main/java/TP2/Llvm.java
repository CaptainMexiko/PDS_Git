package TP2;

import java.util.List;
import java.util.ArrayList;

// This file contains a simple LLVM IR representation
// and methods to generate its string representation

public class Llvm {
    static public class IR {
        List<Instruction> header; // IR instructions to be placed before the code (global definitions)
        List<Instruction> code;   // main code

        public IR(List<Instruction> header, List<Instruction> code) {
            this.header = header;
            this.code = code;
        }

        // append an other IR
        public IR append(IR other) {
            header.addAll(other.header);
            code.addAll(other.code);
            return this;
        }

        // append a code instruction
        public IR appendCode(Instruction inst) {
            code.add(inst);
            return this;
        }

        // append a code header
        public IR appendHeader(Instruction inst) {
            header.add(inst);
            return this;
        }

        // Final string generation
        public String toString() {
            // This header describe to LLVM the target
            // and declare the external function printf
            StringBuilder r = new StringBuilder("; Target\n" +
                    "target triple = \"x86_64-unknown-linux-gnu\"\n" +
                    "; External declaration of the printf function\n" +
                    "declare i32 @printf(i8* noalias nocapture, ...)\n" +
                    "\n; Actual code begins\n\n");

            for (Instruction inst : header)
                r.append(inst);

            r.append("\n\n");

            // We create the function main
            // TODO : remove this when you extend the language
            r.append("define i32 @main() {\n");


            for (Instruction inst : code)
                r.append(inst);

            // TODO : remove this when you extend the language
            r.append("}\n");

            return r.toString();
        }
    }

    // Returns a new empty list of instruction, handy
    static public List<Instruction> empty() {
        return new ArrayList<Instruction>();
    }


    // LLVM Types
    static public abstract class Type {
        public abstract String toString();
    }

    static public class IntType extends Type {
        public String toString() {
            return "i32";
        }
    }

    // TODO : other types


    // LLVM IR Instructions
    static public abstract class Instruction {
        public abstract String toString();
    }

    static public class Add extends Instruction {
        Type type;
        String left;
        String right;
        String lvalue;

        public Add(Type type, String left, String right, String lvalue) {
            this.type = type;
            this.left = left;
            this.right = right;
            this.lvalue = lvalue;
        }

        public String toString() {
            return lvalue + " = add " + type + " " + left + ", " + right + "\n";
        }
    }

    static public class Return extends Instruction {
        Type type;
        String value;

        public Return(Type type, String value) {
            this.type = type;
            this.value = value;
        }

        public String toString() {
            return "ret " + type + " " + value + "\n";
        }
    }

    static public class Moins extends Instruction {
        Type type;
        String left;
        String right;
        String lvalue;

        public Moins(Type type, String left, String right, String lvalue) {
            this.type = type;
            this.left = left;
            this.right = right;
            this.lvalue = lvalue;
        }

        public String toString() {
            return lvalue + " = sub " + type + " " + left + ", " + right + "\n";
        }
    }

    static public class Mult extends Instruction {
        Type type;
        String left;
        String right;
        String lvalue;

        public Mult(Type type, String left, String right, String lvalue) {
            this.type = type;
            this.left = left;
            this.right = right;
            this.lvalue = lvalue;
        }

        public String toString() {
            return lvalue + " = mul " + type + " " + left + ", " + right + "\n";
        }
    }

    static public class Div extends Instruction {
        Type type;
        String left;
        String right;
        String lvalue;

        public Div(Type type, String left, String right, String lvalue) {
            this.type = type;
            this.left = left;
            this.right = right;
            this.lvalue = lvalue;
        }

        public String toString() {
            return lvalue + " = udiv " + type + " " + left + ", " + right + "\n";
        }
    }

    static public class Affect extends Instruction {
      Type type;
      ASD.AffectableConst.RetAffectable affectable;
      ASD.Expression.RetExpression expression;

      public Affect(Type type, ASD.AffectableConst.RetAffectable affectable, ASD.Expression.RetExpression expression){
        this.type = type;
        this.affectable = affectable;
        this.expression = expression;
      }

      public String toString() {
          return "%" + affectable.result + " = alloca " + type + "\n"
                  + "store " + type + " " + expression.result + ", " + type + "* " + " %" + affectable.result + "\n";
      }
    }

    static public class Const extends Instruction {
      Type type;
      String ident;

      public Const(Type type, String ident){
        this.type = type;
        this.ident = ident;
      }

      public String toString() {
          return  "";
      }
    }

    // TODO : other instructions
}
