; Target
target triple = "x86_64-unknown-linux-gnu"
; External declaration of the printf function
declare i32 @printf(i8* noalias nocapture, ...)

; Actual code begins

@"\n" = global [2 x i8] c"\0A\00"


define i32@main(){
; Début block 
%x = alloca i32
%tmp1 = add i32 2, 3
store i32 %tmp1, i32*  %x
call i32 (i8*,...)* @printf
%tmp2 = load i32, i32* %x
ret i32 %tmp2
; Fin block 
}
