; Target
target triple = "x86_64-unknown-linux-gnu"
; External declaration of the printf function
declare i32 @printf(i8* noalias nocapture, ...)

; Actual code begins



define i32 @main() {
%tmp1 = add i32 3, 7
%tmp2 = sub i32 10, %tmp1
%tmp3 = mul i32 2, %tmp2
ret i32 %tmp3
}

