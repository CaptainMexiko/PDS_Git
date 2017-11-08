; Target
target triple = "x86_64-unknown-linux-gnu"
; External declaration of the printf function
declare i32 @printf(i8* noalias nocapture, ...)

; Actual code begins



define i32 @main() {
; Début block 
%tmp1 = load i32, i32* %x
%tmp2 = load i32, i32* %a
%tmp3 = load i32, i32* %b
%tmp4 = add i32 3, 4
%x = alloca i32
store i32 %tmp4, i32*  %x
%a = alloca i32
store i32 5, i32*  %a
%b = alloca i32
store i32 5, i32*  %b
; Fin block 
ret i32 %tmp3
}

