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

        //Ajoute le header de l'IR dans l'header d'un autre
        public IR addHead(IR other){
          header.addAll(other.header);
          return this;
        }

        //Ajoute le code de l'IR dans l'header de l'IR
        public IR addCode(IR other){
          header.addAll(other.code);
          return this;
        }

        public IR addListHeader(List<Instruction> lI){
          header.addAll(lI);
          return this;
        }

        public IR addListCode(List<Instruction> lI){
          code.addAll(lI);
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
                    "declare i32 @scanf(i8* noalias nocapture, ...)\n" +
                    "\n; Actual code begins\n\n");

            for (Instruction inst : header)
                r.append(inst);

            r.append("\n");

            // We create the function main
            // TODO : remove this when you extend the language

            for (Instruction inst : code)
                r.append(inst);

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

    static public class VoidType extends Type {
        public String toString() {
            return "void";
        }
    }

    // TODO : other types


    // LLVM IR Instructions
    static public abstract class Instruction {
        public abstract String toString();
    }

    static public class Comment extends Instruction {
        String lvalue;

        public Comment(String lvalue) {
            this.lvalue = lvalue;
        }

        public String toString() {
            return "; " + lvalue + "\n";
        }
    }


/************************************************ Function **********************************************/

    static public class Function extends Instruction {
      Type type;
      String ident;
      String param;

      public Function(Type type, String ident, String param) {
          this.type = type;
          this.ident = ident;
          this.param = param;
      }

      public String toString() {
          return "\ndefine " + type + " @" + ident + "(" + param + "){" + "\n";
      }
    }

    static public class EndFunction extends Instruction {

      public EndFunction(){};

      public String toString() {
          return  "}\n";
      }
    }

    static public class Call extends Instruction {
      Type type;
      String name;

      public Call(Type type, String name){
        this.type =type;
        this.name =name;
      };

      public String toString() {
          return  "call " + type + " @" + name + "()\n";
      }
    }

    static public class AffectCall extends Instruction {
      String result;
      Type type;
      String name;
      String resultfinale;
      Type typeInter;
      Type resFinal;

      public AffectCall(String result, Type type, String name, String rF, Type typeInter, Type resFinal){
        this.result =result;
        this.type =type;
        this.name =name;
        this.resultfinale = rF;
        this.typeInter = typeInter;
        this.resFinal =resFinal;

      };

      public String toString() {
          return  result + " = call " + type + " @" + name + "()\n"
          + "store " + typeInter + " " + result + ", " + resFinal + "* %" + resultfinale + "\n";
      }
    }

    static public class DecStringPrint extends Instruction {
      String name;
      Utils.LLVMStringConstant result;

      public DecStringPrint(String name, Utils.LLVMStringConstant result) {
          this.name = name;
          this.result = result;
      }

      public String toString() {
          return "@\"" + name + "\" = global [" + result.length + " x i8] c\"" + result.str + "\"\n";
      }
    }

    static public class DecStringRead extends Instruction {
      String head;
      Utils.LLVMStringConstant result;

      public DecStringRead(String head, Utils.LLVMStringConstant result) {
          this.head = head;
          this.result = result;
      }

      public String toString() {
          return "@\"" + head + "\" = global [" + result.length + " x i8] c\"" + result.str + "\"\n";
      }
    }


    static public class Print extends Instruction {
      String result;

      public Print(String result) {
          this.result = result;
      }

      public String toString() {
          return "call i32 (i8*,...) @printf" + result + "\n";
      }
    }

    static public class Read extends Instruction {
      String result;

      public Read(String result) {
          this.result = result;
      }

      public String toString() {
          return "call i32 (i8*,...) @scanf" + result + "\n";
      }
    }

/************************************************ Label ************************************************/
    static public class Label extends Instruction {
        String lvalue;

        public Label(String lvalue) {
            this.lvalue = lvalue;
        }

        public String toString() {
            return "\n" + lvalue + ":" + "\n";
        }
    }



    static public class AppelLabel extends Instruction {
        String lvalue;

        public AppelLabel(String lvalue) {
            this.lvalue = lvalue;
        }

        public String toString() {
            return "br label %" + lvalue + "\n";
        }
    }


/************************************************ Bool ************************************************/
    static public class Bool extends Instruction {
        Type type;
        String icmp;
        String result;

        public Bool(Type type, String icmp, String result) {
            this.type = type;
            this.icmp = icmp;
            this.result = result;
        }

        public String toString() {
            return icmp + " = icmp ne " + type + " " + result + ", 0 \n";
        }
    }



/************************************************ IfThenElse ************************************************/
    static public class IfInstElse extends Instruction {
        String then;
        String elseCond;
        String fi;
        String icmp;

        public IfInstElse(String then, String elseCond,String fi, String icmp) {
            this.then = then;
            this.elseCond = elseCond;
            this.fi = fi;
            this.icmp = icmp;
        }

        public String toString() {
          String rep;
          if(elseCond != null){
            rep =  "br i1 " + icmp + ", label %" + then + ", label %" + elseCond + "\n";
          }
          else {
            rep = "br i1 " + icmp + ", label %" + then + ", label %" + fi + "\n";
        }
        return rep;
    }
  }




/************************************************ While ************************************************/
  static public class WhileInst extends Instruction {
      String doLab;
      String done;
      String icmp;

      public WhileInst(String doLAb, String done, String icmp) {
          this.doLab = doLAb;
          this.done = done;
          this.icmp = icmp;
      }

      public String toString() {
        String rep;
          rep = "br i1 " + icmp + ", label %" + doLab + ", label %" + done + "\n";
      return rep;
  }
}



/************************************************ Add/Moins/Mul/Div ************************************************/
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
          if(type != null){
            return "ret " + type + " " + value + "\n";
          }
          else{
            return "ret " + value + "\n";
          }
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

    static public class ExprIdent extends Instruction {
        Type type;
        String exprIdent;
        String result;

        public ExprIdent(Type type, String exprIdent, String result) {
            this.type = type;
            this.exprIdent = exprIdent;
            this.result = result;
        }

        public String toString() {
            return result + " = load " + type + ", " + type + "* " + "%" + exprIdent + "\n";
        }
    }

    static public class AffectExpression extends Instruction {
      Type type;
      ASD.Affectable.RetAffectable affectable;
      ASD.Expression.RetExpression expression;

      public AffectExpression(Type type, ASD.Affectable.RetAffectable affectable, ASD.Expression.RetExpression expression){
        this.type = type;
        this.affectable = affectable;
        this.expression = expression;
      }

      public String toString() {
          String p100 = " ";
          if (Character.isLetter(expression.result.charAt(0))){
            p100 = " %";
          }
          return "store " + type + p100 + expression.result + ", " + type + "* " + " %" + affectable.result + "\n";
      }
    }

    static public class Var extends Instruction {
      Type type;
      String ident;

      public Var(Type type, String ident){
        this.type = type;
        this.ident = ident;
      }

      public String toString() {
          return  "%" + ident + " = alloca " + type + "\n";
      }
    }

    // TODO : other instructions
}
