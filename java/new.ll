; Target
target triple = "x86_64-unknown-linux-gnu"
; External declaration of the printf function
declare i32 @printf(i8* noalias nocapture, ...)

; Actual code begins



define i32 @main() {
; Début block 
%x = alloca i32
%b = alloca i32
%tmp1 = add i32 2, 4
store i32 %tmp1, i32*  %x
%tmp2 = load i32, i32* %x
%tmp3 = load i32, i32* %x
%tmp4 = mul i32 %tmp2, %tmp3
%tmp5 = load i32, i32* %x
%tmp6 = udiv i32 %tmp4, %tmp5
store i32 %tmp6, i32*  %x
%tmp7 = load i32, i32* %x
; Fin block 
ret i32 %tmp7
}

