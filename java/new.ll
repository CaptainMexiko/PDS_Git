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
%b = alloca i32
store i32 %tmp1, i32*  %b
%tmp2 = load i32, i32* %b
%tmp3 = load i32, i32* %a
%tmp4 = add i32 %tmp2, %tmp3
%c = alloca i32
store i32 %tmp4, i32*  %c
%tmp5 = load i32, i32* %c
%tmp6 = add i32 %tmp5, 4
; Fin block 
ret i32 %tmp6
}

