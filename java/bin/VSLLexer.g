lexer grammar VSLLexer;

options {
  language = Java;
}

@header {
  package TP2;
}

WS : (' '|'\n'|'\t') -> skip
   ;

COMMENT : '//' (~'\n')* -> skip
        ;

fragment LETTER : 'a'..'z' ;
fragment DIGIT  : '0'..'9' ;
fragment ASCII  : ~('\n'|'"');

// keywords
LP    : '(' ; // Left parenthesis
RP    : ')' ;
PLUS  : '+' ;
MOINS : '-' ;
MULT  : '*' ;
DIV   : '/' ;
AFFECT : ':=' ;
DEBLOCK : '{' ;
FIBLOCK : '}' ;
DECINT : 'INT' ;
VIRGULE : ',' ;
RET : 'RETURN' ;
IF : 'IF' ;
THEN : 'THEN' ;
ELSE : 'ELSE' ;
FI : 'FI' ;
WHILE : 'WHILE' ;
DO : 'DO' ;
DONE : 'DONE' ;
FUNC : 'FUNC' ;
PROTO : 'PROTO' ;
VOID : 'VOID' ;
READ : 'READ' ;
PRINT : 'PRINT' ;

// other tokens (no conflict with keywords in VSL)
IDENT   : LETTER (LETTER|DIGIT)*;
TEXT    : '"' (ASCII)* '"' { setText(getText().substring(1, getText().length() - 1)); };
INTEGER : (DIGIT)+ ;
