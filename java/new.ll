; Target
target triple = "x86_64-unknown-linux-gnu"
; External declaration of the printf function
declare i32 @printf(i8* noalias nocapture, ...)

; Actual code begins



; Début block 
%tmp1 = add i32 1, 2
%a = alloca i32
store i32 3, i32*  %a
%tmp2 = udiv i32 3, 4
%tmp3 = add i32 2, %tmp2
%v = alloca i32
store i32 12, i32*  %v
%test = alloca i32
store i32 1, i32*  %test
; Fin block 

