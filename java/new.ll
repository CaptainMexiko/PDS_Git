; Target
target triple = "x86_64-unknown-linux-gnu"
; External declaration of the printf function
declare i32 @printf(i8* noalias nocapture, ...)

; Actual code begins

@"x vaut : " = global [10 x i8] c"x vaut : \00"


define i32@main(){
; Début block 
%x = alloca i32
%b = alloca i32
%tmp1 = add i32 2, 3
store i32 %tmp1, i32*  %x
store i32 0, i32*  %b
call i32 (i8*,...) @printf(i8* getelementptr inbounds (([12 x i8],[12 x i8])* @"x vaut : %d\00", i32 0, i32 0))
%tmp2 = load i32, i32* %x
ret i32 %tmp2
; Fin block 
}
