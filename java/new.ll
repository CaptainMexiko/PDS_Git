; Target
target triple = "x86_64-unknown-linux-gnu"
; External declaration of the printf function
declare i32 @printf(i8* noalias nocapture, ...)
declare i32 @scanf(i8* noalias nocapture, ...)

; Actual code begins

@"formatRead1" = global [7 x i8] c"%d%d%d\00"
@"formatPrint2" = global [35 x i8] c"x vaut : %d b vaut %d et c vaut %d\00"


define i32 @main(){
; Début block 
%x = alloca i32
%b = alloca i32
%c = alloca i32
call i32 (i8*,...) @scanf(i8* getelementptr inbounds ([7 x i8],[7 x i8]* @formatRead1, i32 0, i32 0), i32* %x, i32* %b, i32* %c)
%tmp1 = load i32, i32* %x
%tmp2 = load i32, i32* %b
%tmp3 = load i32, i32* %c
call i32 (i8*,...) @printf(i8* getelementptr inbounds ([35 x i8],[35 x i8]* @formatPrint2, i32 0, i32 0), i32 %tmp1, i32 %tmp2, i32 %tmp3)
; Fin block 
ret i32 0
}

