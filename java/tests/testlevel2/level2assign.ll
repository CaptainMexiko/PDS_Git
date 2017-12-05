; Target
target triple = "x86_64-unknown-linux-gnu"
; External declaration of the printf function
declare i32 @printf(i8* noalias nocapture, ...)

; Actual code begins

@"formatPrint1" = global [10 x i8] c"y vaut %d\00"


define void @main(){
; Début block 
%x = alloca i32
%y = alloca i32
%tmp1 = load i32, i32* %x
store i32 %tmp1, i32*  %y
%tmp2 = load i32, i32* %y
call i32 (i8*,...) @printf(i8* getelementptr inbounds ([10 x i8],[10 x i8]* @formatPrint1, i32 0, i32 0), i32 %tmp2)
; Fin block 
ret void
}
