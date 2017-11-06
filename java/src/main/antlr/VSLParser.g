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
    : e=expressionbasseprio i=instruction { $out = new ASD.Program($e.out, $i.out); } // TODO : change when you extend the language
    ;

instruction returns [ASD.Instruction out]
    : a=affectable  AFFECT  e=expressionbasseprio  { $out = new ASD.AffectInstruction($a.out, $e.out); }
    ;

expressionbasseprio returns [ASD.Expression out]
  : l=expressionhauteprio ( PLUS r=expressionbasseprio  { $out = new ASD.AddExpression($l.out, $r.out); }
                            |MOINS r=expressionbasseprio  { $out = new ASD.MoinsExpression($l.out, $r.out); }
                            )+
  | l=expressionhauteprio { $out = $l.out; }
  // TODO : that's all?
  ;

expressionhauteprio returns [ASD.Expression out]
  : l=factor (MULT r=expressionhauteprio  { $out = new ASD.MultExpression($l.out, $r.out); }
              |DIV r=expressionhauteprio  { $out = new ASD.DivExpression($l.out, $r.out); }
              )+
  | f=factor { $out = $f.out; }
  ;
factor returns [ASD.Expression out]
    : p=primary { $out = $p.out; }
    | LP e=expressionbasseprio RP { $out = $e.out; }
    // TODO : that's all?
    ;

affectable returns [ASD.Affectable out]
  : IDENT { $out = new ASD.VariableAffectable($IDENT.type, $IDENT.text); }
  ;

primary returns [ASD.Expression out]
    : INTEGER { $out = new ASD.IntegerExpression($INTEGER.int); }
    // TODO : that's all?
    ;
