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
    : DEBLOCK b=bloc FIBLOCK EOF { $out = new ASD.Program($b.out); }
    |b=bloc EOF { $out = new ASD.Program($b.out); } // TODO : change when you extend the language
    ;

bloc returns [ASD.BlockImplement out]
  : {ASD.DeclarationImplement decl = null; List<ASD.StatementImplement> ls = new ArrayList(); }
   (d=declaration {decl = $d.out;})?
   (s=statement {ls.add($s.out);} )+ { $out = new ASD.BlockImplement(decl, ls); }
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
    | IIF ei=expression TH b=bloc IFI { $out = new ASD.IfInstruction($ei.out, $b.out); }
    //| IIF es=expression TH bs=bloc EL be=bloc IFI { $out = new ASD.IfInstructionElse($es.out, $bs.out, $be.out); }
    //| IWHILE ew=expression IDO bw=bloc FW      // ?? s=statement { $out = new ASD.StatementImplement($s.out)}
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

affectable returns [ASD.AffectableVar out]
  : IDENT { $out = new ASD.AffectableVar(new ASD.IntType(), $IDENT.text); }
  ;

primary returns [ASD.Expression out]
    : INTEGER { $out = new ASD.IntegerExpression($INTEGER.int); }
    | IDENT { $out = new ASD.ExprIdent(new ASD.IntType(), $IDENT.text); }
    // TODO : that's all?
    ;
