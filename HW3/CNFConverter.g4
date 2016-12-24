grammar CNFConverter;		

prog:	stat+ ;

stat:	expr NEWLINE
	;

expr:	LEFTBRAC expr (IMPLY|AND|OR) expr RIGHTBRAC
    |	LEFTBRAC expr RIGHTBRAC 
    |	(NOT) expr
    |	PREDICATE LEFTBRAC PREDICATE RIGHTBRAC
    ;
    
OR		: '|' ;
AND		: '&' ;
IMPLY	: '=>' ;
NOT		: '~' ;	
LEFTBRAC : '(' ;
RIGHTBRAC: ')' ;

PREDICATE	: [A-Za-z,]+ ;


NEWLINE	: '\r'? '\n' ;
SPACE	: [\ \t]+ -> skip ;