; Target
target triple = "x86_64-unknown-linux-gnu"
; External declaration of the printf function
declare i32 @printf(i8* noalias nocapture, ...)

; Actual code begins



define i32 @main() {
%tmp1 = add i32 5, 10
%tmp2 = mul i32 11, 3
%tmp3 = add i32 %tmp1, %tmp2
ret i32 %tmp3
}

