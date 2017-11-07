; Target
target triple = "x86_64-unknown-linux-gnu"
; External declaration of the printf function
declare i32 @printf(i8* noalias nocapture, ...)

; Actual code begins



define i32 @main() {
; Début block 
%a = alloca i32
store i32 3, i32*  %a
%tmp1 = load i32, i32* %a
%tmp2 = add i32 %tmp1, 4
; Fin block 
ret i32 %tmp2
}

