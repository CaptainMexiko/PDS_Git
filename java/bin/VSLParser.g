parser grammar VSLParser;

options {
  language = Java;
  tokenVocab = VSLLexer;
}

@header {
  package TP2;

  import java.util.stream.Collectors;
  import java.util.Arrays;

}


// TODO : other rules



program returns [ASD.Program out]
    : {List<ASD.Function> lf = new ArrayList(); } (f=function {lf.add($f.out);} )+ { $out = new ASD.Program(lf); }
    ;

function returns [ASD.Function out]
    : { ASD.Type typeP = null; } PROTO (VOID { typeP = new ASD.VoidType(); } | DECINT { typeP = new ASD.IntType(); } ) IDENT DEBLOCK b=bloc FIBLOCK { $out = new ASD.FunctionProto(typeP, $IDENT.text, $b.out); }
    | { ASD.Type typeF = null; } FUNC (VOID { typeF = new ASD.VoidType(); } | DECINT { typeF = new ASD.IntType(); } ) IDENT DEBLOCK b=bloc FIBLOCK { $out = new ASD.FunctionImplement(typeF, $IDENT.text, $b.out); } // TODO : change when you extend the language
    ;

bloc returns [ASD.BlockImplement out]
  : {ASD.DeclarationImplement decl = null; List<ASD.StatementImplement> ls = new ArrayList(); }
   (d=declaration {decl = $d.out;})?
   (s=statement {ls.add($s.out);} )+ { $out = new ASD.BlockImplement(decl, ls); }
   | {ASD.DeclarationImplement decl = null; List<ASD.StatementImplement> ls = new ArrayList(); }
    DEBLOCK (d=declaration {decl = $d.out;})?
    (s=statement {ls.add($s.out);} )+ FIBLOCK { $out = new ASD.BlockImplement(decl, ls); }
  ;

declaration returns [ASD.DeclarationImplement out]
  : DECINT {List<ASD.AffectableVar> la = new ArrayList(); }
   (a=affectable {la.add($a.out);}
  (VIRGULE a=affectable {la.add($a.out);})* )+ { $out = new ASD.DeclarationImplement(la); }
  ;

statement returns [ASD.StatementImplement out]
  : i=instruction { $out = new ASD.StatementImplement($i.out); }
    | e=expression { $out = new ASD.StatementImplement($e.out); }
    | s=statementreturn { $out = $s.out; }
  ;

statementreturn returns [ASD.StatementImplement out]
  : i=instructionreturn { $out = new ASD.StatementImplement($i.out); }
  ;

instructionreturn returns [ASD.Instruction out]
  : RET e=expression { $out = new ASD.ReturnInstruction($e.out); }
  ;

instruction returns [ASD.Instruction out]
    : a=affectable  AFFECT  e=expression  { $out = new ASD.AffectInstruction($a.out, $e.out); }
    | { ASD.Block bx = null; } IF ei=expressionif THEN b=bloc (ELSE be=bloc { bx = $be.out; } )? FI { $out = new ASD.IfInstructionElse($ei.out, $b.out, bx); }
    | WHILE ew=expressionif DO bw=bloc DONE { $out = new ASD.InstructionWhile($ew.out, $bw.out); }
    //| READ ec=entreeclavier { $out = new ASD.Read($ec.out); }
    | { List<ASD.Affichable> la = new ArrayList(); } PRINT af=affichable { la.add($af.out); } (VIRGULE as=affichable { la.add($as.out); })* { $out = new ASD.Print(la); }
    ;

expressionif returns [ASD.Expression out]
  : e=expression { $out = new ASD.ExpressionIf($e.out);}
  ;

expression returns [ASD.Expression out]
  : e=expressionbasseprio { $out = $e.out; }
  ;

expressionbasseprio returns [ASD.Expression out]
  : l=expressionbasseprio ( PLUS r=expressionhauteprio  { $out = new ASD.AddExpression($l.out, $r.out); }
                            |MOINS r=expressionhauteprio  { $out = new ASD.MoinsExpression($l.out, $r.out); }
                            )
  | lh=expressionhauteprio { $out = $lh.out; }
  // TODO : that's all?
  ;

expressionhauteprio returns [ASD.Expression out]
  : l=expressionhauteprio (MULT r=factor  { $out = new ASD.MultExpression($l.out, $r.out); }
                          |DIV r=factor  { $out = new ASD.DivExpression($l.out, $r.out); }
                          )
  | f=factor { $out = $f.out; }
  ;
factor returns [ASD.Expression out]
    : p=primary { $out = $p.out; }
    | LP e=expressionbasseprio RP { $out = $e.out; }
    // TODO : that's all?
    ;

affichable returns [ASD.Affichable out]
	: TEXT { $out = new ASD.AffichableImpl($TEXT.text);}
	| a=primary { $out = new ASD.AffichableImpl($a.out);}
	;

//entreeclavier returns [ASD.EntreeClavier out]
	//: ec=affectable { $out = neww ASD.EntreeClavierImpl($ec.out);}
	//;

affectable returns [ASD.AffectableVar out]
  : IDENT { $out = new ASD.AffectableVar(new ASD.IntType(), $IDENT.text); }
  ;

primary returns [ASD.Expression out]
    : INTEGER { $out = new ASD.IntegerExpression($INTEGER.int); }
    | IDENT { $out = new ASD.ExprIdent(new ASD.IntType(), $IDENT.text); }
    // TODO : that's all?
    ;
