; Target
target triple = "x86_64-unknown-linux-gnu"
; External declaration of the printf function
declare i32 @printf(i8* noalias nocapture, ...)

; Actual code begins



define i32 @main(){
; Début block 
br label %while1

while1:
%icmp1 = icmp ne i32 1, 0 
br i1 %icmp1, label %do2, label %done3

do2:
; Début block 
ret i32 0
; Fin block 
br label %while1

done3:
; Fin block 
ret i32 0
}
