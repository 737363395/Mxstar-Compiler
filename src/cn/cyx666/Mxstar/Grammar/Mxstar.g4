grammar Mxstar;

//program
program
    :   declaration* EOF
    ;

declaration
    :   functionDeclaration
    |   classDeclaration
    |   variableDeclaration
    ;

//declaration
functionDeclaration
    :   typeOrVoid? Identifier '(' parameterDeclarationList? ')' blockStatement
    ;

classDeclaration
    :   Class Identifier '{' memberDeclaration* '}'
    ;

variableDeclaration
    :   type variableList ';'
    ;

variableList
    :   variable (',' variable)*
    ;

variable
    :   Identifier ('=' expression)?
    ;

memberDeclaration
    :   functionDeclaration
    |   variableDeclaration
    ;

parameterDeclarationList
    :   parameterDeclaration (',' parameterDeclaration)*
    ;

parameterDeclaration
    :   type Identifier
    ;

typeOrVoid
    :   type
    |   Void
    ;

type
    :   type '[' ']'        # array
    |   nonArrayType        # nonArray
    ;

nonArrayType
    :   Int
    |   Bool
    |   String
    |   Identifier
    ;

//statement
statement
    :   blockStatement          # block
    |   conditionStatement      # condition
    |   loopStatement           # loop
    |   jumpStatement           # jump
    |   otherStatement          # other
    |   ';'                     # blank
    ;

blockStatement
    :   '{' localStatement* '}'
    ;

otherStatement
    :   expression ';'
    ;

localStatement
    :   statement               # nonVariableStatement
    |   variableDeclaration     # variableStatement
    ;

conditionStatement
    :   If '(' expression ')' thenStatement=statement (Else elseStatement=statement)?
    ;

loopStatement
    :   While '(' expression ')' statement              # while
    |   For '(' initialization=expression? ';'
                condition=expression? ';'
                step=expression? ')' statement          # for
    ;

jumpStatement
    :   Continue ';'                # continue
    |   Break ';'                   # break
    |   Return expression? ';'      # return
    ;

//expression
expression
    :   expression operator=('++' | '--')                             # suffixExpression
    |   expression '.' Identifier                               # memberAccessExpression
    |   array=expression '[' subscript=expression ']'                   # subscriptExpression
    |   expression '(' parameterList? ')'                       # funcCallExpression
    |   <assoc=right> operator=('++'|'--') expression                 # prefixExpression
    |   <assoc=right> operator=('+' | '-') expression                 # prefixExpression
    |   <assoc=right> operator=('!' | '~') expression                 # prefixExpression
    |   <assoc=right> New creator                               # newExpression
    |   leftChild=expression operator=('*' | '/' | '%') rightChild=expression      # binaryExpression
    |   leftChild=expression operator=('+' | '-') rightChild=expression            # binaryExpression
    |   leftChild=expression operator=('<<'|'>>') rightChild=expression            # binaryExpression
    |   leftChild=expression operator=('<' | '>') rightChild=expression            # binaryExpression
    |   leftChild=expression operator=('<='|'>=') rightChild=expression            # binaryExpression
    |   leftChild=expression operator=('=='|'!=') rightChild=expression            # binaryExpression
    |   leftChild=expression operator='&' rightChild=expression                    # binaryExpression
    |   leftChild=expression operator='^' rightChild=expression                    # binaryExpression
    |   leftChild=expression operator='|' rightChild=expression                    # binaryExpression
    |   <assoc=right> leftChild=expression operator='&&' rightChild=expression     # binaryExpression
    |   <assoc=right> leftChild=expression operator='||' rightChild=expression     # binaryExpression
    |   <assoc=right> leftChild=expression operator='=' rightChild=expression      # assignExpression
    |   primary                            # primaryExpression
    ;

primary
    :   Identifier                                      # identifier
    |   This                                            # this
    |   basicConstant                                   # constant
    |   '(' expression ')'                              # body
    ;

basicConstant
    :   IntegerConstant             # int
    |   StringConst                 # string
    |   NullLiteral                 # null
    |   BoolConstant                # bool
    ;

nonArrayTypeCreator
    :   Int
    |   Bool
    |   String
    |   Identifier ('(' ')')?
    ;

creator
    :   nonArrayType ('[' expression ']')+ ('[' ']')+ ('[' expression ']')+     # errorCreator
    |   nonArrayType ('[' expression ']')+ ('[' ']')*                           # arrayCreator
    |   nonArrayTypeCreator                                                         # nonArrayCreator
    ;

parameterList
    :   expression (',' expression)*
    ;

//reserved word
Bool                : 'bool';
Int                 : 'int';
String              : 'string';
fragment Null       : 'null';
Void                : 'void';
fragment True       : 'true';
fragment False      : 'false';
If                  : 'if';
Else                : 'else';
For                 : 'for';
While               : 'while';
Break               : 'break';
Continue            : 'continue';
Return              : 'return';
New                 : 'new';
Class               : 'class';
This                : 'this';

//constant
IntegerConstant
    :   [1-9] [0-9]*
    |   '0'
    ;

StringConst
    :   '"' StringCharacter* '"'
    ;

fragment StringCharacter
    :   ~["\\\r\n]
    |   '\\' ["n\\]
    ;

NullLiteral
    :   Null
    ;

BoolConstant
    :   True
    |   False
    ;

//identifier
Identifier
    :   IdentifierNonDigitUnderline (IdentifierNonDigit | Digit)*
    ;

fragment IdentifierNonDigitUnderline
    :   [a-zA-Z]
    ;

fragment IdentifierNonDigit
    :   [a-zA-Z_]
    ;

fragment Digit
    :   [0-9]
    ;

//skip
WhiteSpace
    :   [ \t]+ -> skip
    ;

NewLine
    :   '\r'? '\n' -> skip
    ;

LineComment
    :   '//' ~[\r\n]* -> skip
    ;

BlockComment
    :   '/*' .*? '*/' -> skip
    ;