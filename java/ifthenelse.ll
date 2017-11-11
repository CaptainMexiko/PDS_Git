; Target
target triple = "x86_64-unknown-linux-gnu"
; External declaration of the printf function
declare i32 @printf(i8* noalias nocapture, ...)

; Actual code begins



define i32 @main() {
; Début block 
%x = alloca i32
%b = alloca i32
store i32 5, i32*  %x
; Début du if
%tmp1 = load i32, i32* %x
%icmp1= icmp ne i32 %tmp1, 0 
br i1 %icmp1, label %then1, label %fi2
then1:
; Début block 
%tmp2 = add i32 5, 2
store i32 %tmp2, i32*  %b
; Fin block 
br label %fi2
fi2:
; Fin du if
%tmp3 = load i32, i32* %b
ret i32 %tmp3
; Fin block 
}

