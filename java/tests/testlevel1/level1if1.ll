; Target
target triple = "x86_64-unknown-linux-gnu"
; External declaration of the printf function
declare i32 @printf(i8* noalias nocapture, ...)

; Actual code begins



define i32 @main(){
; Début block 
; Début du if
%icmp1 = icmp ne i32 1, 0 
br i1 %icmp1, label %then1, label %fi2

then1:
; Début block 
ret i32 1
; Fin block 
br label %fi2

fi2:
; Fin du if
; Fin block 
ret i32 0
}
