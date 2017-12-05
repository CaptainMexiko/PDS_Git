; Target
target triple = "x86_64-unknown-linux-gnu"
; External declaration of the printf function
declare i32 @printf(i8* noalias nocapture, ...)

; Actual code begins

@"formatPrint1" = global [29 x i8] c"x vaut : %d mais b vaut : %d\00"


define i32 @main(){
; Début block 
%x = alloca i32
%b = alloca i32
%tmp1 = add i32 2, 3
store i32 %tmp1, i32*  %x
store i32 0, i32*  %b
%tmp2 = load i32, i32* %x
%tmp3 = load i32, i32* %b
call i32 (i8*,...) @printf(i8* getelementptr inbounds ([29 x i8],[29 x i8]* @formatPrint1, i32 0, i32 0), i32 %tmp2, i32 %tmp3)
%tmp4 = load i32, i32* %x
ret i32 %tmp4
; Fin block 
}
